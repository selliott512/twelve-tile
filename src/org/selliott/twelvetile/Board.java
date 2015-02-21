/*****************************************************************************
 * Copyright (c) 2009 Steven Elliott <selliott4@austin.rr.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under a the GNU General Public License version 2 or 
 * later, which also accompanies this distribution, and which is available at:
 *   http://www.fsf.org/licensing/licenses/info/GPLv2.html
 *
 * Contributors:
 *     Steven Elliott - Initial implementation
 *     
 * History:
 *     0.8.0 - 2008-08-01 - Initial version
 *     0.8.1 - 2008-08-09 - Minor cosmetic changes
 *     0.9.0 - 2009-04-01 - Port to Android
 *****************************************************************************/

package org.selliott.twelvetile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Board {
    public static final int INV = -1; // Invalid
    public static final int LENGTH = 12;
    private static final int[] locValues = { 1, 8, 9 * 8, 10 * 9 * 8,
            11 * 10 * 9 * 8 };
    public static final int MAX_MOVES = 29;
    public static final int MOVE_FLIP = 1; // This could be an enumeration.
    public static final int MOVE_MERGE = 2;
    public static final int MOVE_NONE = 0;
    public static final int NUM_LOCS = 5;
    private byte[] db;
    private int idx = INV;
    private int[] locs;
    private int[] move1Result;
    private int[] move2Result;
    private boolean randomized;
    private Random rnd;
    private boolean saveOld = true;
    private boolean useDb = true; // The database applies to the current results.
    private int[] values = new int[LENGTH];
    private int[] valuesCheckpoint;
    ArrayList<int[]> valuesOldList = new ArrayList<int[]>();

    public Board() {
        reset();
    }

    // Copy constructor
    public Board(Board oldBoard) {
        db = oldBoard.db;
        idx = oldBoard.idx;

        locs = new int[oldBoard.locs.length];
        System.arraycopy(oldBoard.locs, 0, locs, 0, locs.length);

        saveOld = oldBoard.saveOld;

        values = new int[oldBoard.values.length];
        System.arraycopy(oldBoard.values, 0, values, 0, values.length);

        // Copy the results
        if (oldBoard.move1Result != null) {
            move1Result = new int[oldBoard.move1Result.length];
            System.arraycopy(oldBoard.move1Result, 0, move1Result, 0,
                    move1Result.length);
        }

        if (oldBoard.move2Result != null) {
            move2Result = new int[oldBoard.move2Result.length];
            System.arraycopy(oldBoard.move2Result, 0, move2Result, 0,
                    move2Result.length);
        }
    }

    public Board(byte[] dbase) {
        this();
        db = dbase;
    }

    public Board(byte[] dbase, int[] result1, int[] result2) {
        this(dbase);
        move1Result = result1;
        move2Result = result2;
    }

    public boolean canUndo() {
        return valuesOldList.size() != 0;
    }

    private void clearCheckpoint() {
        valuesCheckpoint = null;
    }

    public void clearUndo() {
        valuesOldList = new ArrayList<int[]>();
    }

    public int get(int i) {
        return values[i];
    }

    public int getIdx() {
        if (idx != INV) {
            return idx;
        }

        setLocs();

        idx = 0;
        for (int i = locs.length - 1; i >= 0; i--) {
            int loc = locs[i]; // This assumes they are [1, 12].
            int sigLess = 0; // locs that are more significant that are
            // lower.
            for (int j = locs.length - 1; j > i; j--) {
                if (locs[j] < loc) {
                    sigLess++;
                }
            }
            idx += locValues[i] * (loc - sigLess);
        }

        return idx;
    }

    public int getMoves() {
        if (!useDb) {
            return 0;
        }
        
        return db[getIdx()];
    }

    public boolean getRandomized() {
        return randomized;
    }

    public boolean getSolved() {
        // Assume it is solved until a piece is found that is out of order.
        for (int i = 0; i < values.length; i++) {
            if (values[i] != i) {
                return false;
            }
        }

        return true;
    }

    private void move(int[] result) {
        undoAppend();

        if (result == null) {
            return;
        }

        int[] valuesNew = new int[values.length];
        System.arraycopy(values, 0, valuesNew, 0, values.length);

        // The following assumes that the result has already been validated.
        for (int i = 0; i < valuesNew.length; i++) {
            valuesNew[i] = values[result[i]];
        }

        values = valuesNew;

        updateIdx();
    }

    public void move1() {
        move(move1Result);
    }

    public void move2() {
        move(move2Result);
    }

    public int nextMove() {
        int moves = getMoves();
        if (moves == 0) {
            return MOVE_NONE;
        }
        Board bCopy = new Board(this);
        bCopy.saveOld = false;
        bCopy.move1();
        if (bCopy.getMoves() < moves) {
            return MOVE_FLIP;
        } else {
            return MOVE_MERGE;
        }
    }

    public void random() {
        saveOld = false;
        int[] valuesCopy = new int[values.length];
        System.arraycopy(values, 0, valuesCopy, 0, values.length);
        valuesOldList.add(valuesCopy);
        // Reset before starting just in case the current values are not valid.
        reset();

        if (rnd == null) {
            rnd = new Random();
        }

        // For a perfect scrambling a random index could be picked in the range
        // [0, DB_SIZE). But this should be good enough.
        for (int i = 0; i < 100; i++) {
            if (rnd.nextBoolean()) {
                move1();
            } else {
                move2();
            }
        }
        saveOld = true;

        // Copy this random position to the checkpoint so that it it is possible
        // to rewind to it.
        valuesCheckpoint = new int[values.length];
        System.arraycopy(values, 0, valuesCheckpoint, 0, values.length);
        
        randomized = true;

        updateIdx();
    }

    public void reset() {
        for (int i = 0; i < values.length; i++) {
            values[i] = i;
        }

        // Undo gets confusing is it last past a reset/random.
        randomized = false;
        clearUndo();
        clearCheckpoint();

        updateIdx();
    }

    public void rewind() {
        if (!rewindable()) {
            return;
        }

        randomized = true;
        System.arraycopy(valuesCheckpoint, 0, values, 0, values.length);
        clearUndo();
        updateIdx();
    }

    public boolean rewindable() {
        if (valuesCheckpoint == null) {
            return false;
        }

        for (int i = 0; i < valuesCheckpoint.length; i++) {
            if (valuesCheckpoint[i] != values[i]) {
                return true;
            }
        }

        return false;
    }

    public void set(int i, int cur) {
        values[i] = cur;
        idx = INV;
    }

    public void setIdx(int valuesIdx) {
        idx = valuesIdx;

        for (int i = locs.length - 1; i >= 0; i--) {
            int loc = valuesIdx / locValues[i];
            int locOrig = loc;
            valuesIdx -= locValues[i] * loc;
            int sigLess = 0; // locs that are more significant that are
            // lower.
            int sigLessOld;
            while (true) {
                sigLessOld = sigLess;
                sigLess = 0;
                for (int j = locs.length - 1; j > i; j--) {
                    if (locs[j] <= loc) {
                        sigLess++;
                    }
                }
                if (sigLess == sigLessOld) {
                    break;
                }
                loc = locOrig + sigLess;
            }
            locs[i] = loc;
        }

        Arrays.fill(values, INV);
        for (int i = 0; i < locs.length; i++) {
            values[locs[i]] = i;
        }
    }

    private void setLocs() {
        locs = new int[NUM_LOCS];

        for (int i = 0; i < LENGTH; i++) {
            int val = values[i];
            if ((val != INV) && (val < NUM_LOCS)) {
                locs[val] = i;
            }
        }
    }

    public void setMove1Result(int[] result) {
        move1Result = result;
    }

    public void setMove2Result(int[] result) {
        move2Result = result;
    }

    public void setRandomized(boolean rndized) {
        randomized = rndized;
    }

    public void setUseDb(boolean uDb) {
        useDb = uDb;
    }
    
    public void undo() {
        if (canUndo()) {
            values = valuesOldList.remove(valuesOldList.size() - 1);
            idx = INV;
        }
    }

    public void undoAppend() {
        if (saveOld) {
            int[] valuesCopy = new int[values.length];
            System.arraycopy(values, 0, valuesCopy, 0, values.length);
            valuesOldList.add(valuesCopy);
        }
    }

    private void updateIdx() {
        idx = INV;
        if (useDb) {
            getIdx();
        }
    }
}
