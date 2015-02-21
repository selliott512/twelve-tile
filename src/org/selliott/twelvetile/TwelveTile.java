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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class TwelveTile extends Activity implements
        OnSharedPreferenceChangeListener {
    static TwelveTile last;
    private static final String RESULT_FLIP = "LKJIHGFEDCBA";
    private static final String RESULT_MERGE = "ALBKCJDIEHFG";
    private static final String RESULT_ROLL = "BCDEFGHIJKLA";
    private static final String RESULT_SOLVED = "ABCDEFGHIJKL";
    private static final String RESULT_SWAP = "BACDEFGHIJKL";
    private static final String TAG = "M12";
    private static final String UNKNOWN = "unknown";

    static void reset() {
        if (last == null) {
            Log.e(TAG, "M12.reset() called with last == null");
            return;
        }

        last.board.reset();
        last.setMoves();
        last.update(null);
    }

    private Board board;
    private DBase dBase;
    private Button move1Button;
    private Button move2Button;
    private SharedPreferences prefs;
    private ProgressBar prog;
    private Button randomButton;
    private Button resetButton;
    private Button rewindButton;
    private boolean solvedOld = true;
    private Tile[] tiles;
    private Button undoButton;

    private void msgBox(int msgNum) {
        msgBox(getString(msgNum));
    }

    private void msgBox(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Log.d(TAG, "onCreate() called for M12");
        setContentView(R.layout.twelvetile);

        // This is done since there does not seem to be a good way to otherwise
        // find a handle to this activity.
        last = this;

        // Grab a reference to the preferences created by the preferences
        // activity.
        PreferenceManager.setDefaultValues(TwelveTile.this, R.xml.preferences,
                false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Query what the orientation currently really is.
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // The following message is only displayed once.
            msgBox(R.string.landscape);

            // The value set by the following is initially
            // ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED. Once set it
            // retains it's value. The View will be rendered in the specified
            // orientation from this point forward.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        // Grab references to various GUI items.

        prog = (ProgressBar) findViewById(R.id.prog); // hang on
        prog.setMax(Board.MAX_MOVES);

        // Store handles to the tiles in an array in order to make references
        // to the tiles more efficient.
        tiles = new Tile[] { (Tile) findViewById(R.id.tile0),
                (Tile) findViewById(R.id.tile1),
                (Tile) findViewById(R.id.tile2),
                (Tile) findViewById(R.id.tile3),
                (Tile) findViewById(R.id.tile4),
                (Tile) findViewById(R.id.tile5),
                (Tile) findViewById(R.id.tile6),
                (Tile) findViewById(R.id.tile7),
                (Tile) findViewById(R.id.tile8),
                (Tile) findViewById(R.id.tile9),
                (Tile) findViewById(R.id.tile10),
                (Tile) findViewById(R.id.tile11) };

        dBase = new DBase(this);
        board = new Board(dBase.getBytes());

        setMoves();

        move1Button = (Button) findViewById(R.id.move1Button);
        move1Button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                board.move1();
                update((Button) v);
            }
        });

        move2Button = (Button) findViewById(R.id.move2Button);
        move2Button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                board.move2();
                update((Button) v);
            }
        });

        undoButton = (Button) findViewById(R.id.undoButton);
        undoButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                board.undo();
                update((Button) v);
            }
        });

        randomButton = (Button) findViewById(R.id.randomButton);
        randomButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                board.random();
                update((Button) v);
            }
        });

        resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                board.reset();
                update((Button) v);
            }
        });

        rewindButton = (Button) findViewById(R.id.rewindButton);
        rewindButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                board.rewind();
                update((Button) v);
            }
        });

        update(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()) {
        case R.id.menu_preferences:
            intent = new Intent(TwelveTile.this, Preferences.class);
            break;
        case R.id.menu_help:
            intent = new Intent(TwelveTile.this, Help.class);
            break;
        }

        if (intent != null) {
            // Launch the activity asynchronously.
            TwelveTile.this.startActivity(intent);
        }

        update(null);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        // Log.d(TAG, "Preference change detected.  key=" + key);

        // Dependency checks between properties.
        String type = prefs.getString("puzzle_type_key", UNKNOWN);
        if (prefs.getBoolean("cheat", false) && !type.equals("m12")) {
            msgBox(R.string.cheating);
            Editor edit = prefs.edit();
            edit.putBoolean("cheat", false);
            edit.commit();
        }

        // Validation checks
        if (key.equals("puzzle_type_key")) {
            setMoves();
        } else if (key.contains("_result_key")) {
            String resultOld = prefs.getString(key, RESULT_SOLVED);
            String result = resultClean(resultOld);
            if (!resultOld.equals(result)) {
                // Fix the result so that it is valid and tell the user if
                // there is a discrepancy.
                Editor edit = prefs.edit();
                edit.putString(key, result);
                edit.commit();
                msgBox(getString(R.string.fixed_result) + result
                        + getString(R.string.fixed_instead) + resultOld);
            }

            // Convert the result to numeric format and pass it to the
            // board.
            int[] resultInts = resultToInts(result);

            if (key.equals("move1_result_key")) {
                board.setMove1Result(resultInts);
            } else if (key.equals("move2_result_key")) {
                board.setMove2Result(resultInts);
            }
        }

        update(null);
    }

    private String resultClean(String result) {
        // Make it upper case
        result = result.toUpperCase();

        // Remove all characters that are not ok.
        char[] resultChars = result.toCharArray();
        int wIdx = 0;
        for (int rIdx = 0; rIdx < resultChars.length; rIdx++) {
            if ((resultChars[rIdx] >= 'A') && (resultChars[rIdx] <= 'L')) {
                resultChars[wIdx++] = resultChars[rIdx];
            }
        }

        // Truncate to no more than 12 characters.
        if (wIdx > RESULT_SOLVED.length()) {
            wIdx = RESULT_SOLVED.length();
        }
        result = String.copyValueOf(resultChars, 0, wIdx);

        // Append the end of SOLVED if it is too short.
        if (result.length() < RESULT_SOLVED.length()) {
            result += RESULT_SOLVED.substring(result.length());
        }

        return result;
    }

    private int[] resultToInts(String result) {
        int[] resultInts = new int[result.length()];
        for (int i = 0; i < resultInts.length; i++) {
            resultInts[i] = result.charAt(i) - 'A';
        }
        return resultInts;
    }

    private void setMoves() {
        String result1;
        String result2;
        String type = prefs.getString("puzzle_type_key", UNKNOWN);
        if (type.equals("m12")) {
            result1 = RESULT_FLIP;
            result2 = RESULT_MERGE;
        } else if (type.equals("bubble")) {
            result1 = RESULT_SWAP;
            result2 = RESULT_ROLL;
        } else if (type.equals("custom")) {
            result1 = prefs.getString("move1_result_key", RESULT_SOLVED);
            result2 = prefs.getString("move2_result_key", RESULT_SOLVED);
        } else {
            // This should not happen
            Log.e(TAG, "Unknown move type " + type
                    + " setting move 1 result to " + type);
            result1 = result2 = RESULT_SOLVED;
        }
        // Log.d(TAG, "Setting move 1 result to " + result1);
        board.setMove1Result(resultToInts(result1));

        // Log.d(TAG, "Setting move 2 result to " + result2);
        board.setMove2Result(resultToInts(result2));
    }

    private void update(Button button) {
        for (int i = 0; i < Board.LENGTH; i++) {
            tiles[i].setValue(board.get(i));
        }

        String type = prefs.getString("puzzle_type_key", UNKNOWN);
        // Log.d(TAG, "In update() type=" + type);
        if (type.equals("m12")) {
            move1Button.setText("Flip");
            move2Button.setText("Merge");
            board.setUseDb(true);
        } else if (type.equals("bubble")) {
            move1Button.setText("Swap");
            move2Button.setText("Roll");
            board.setUseDb(false);
        } else if (type.equals("custom")) {
            move1Button.setText(prefs.getString("move1_name_key", UNKNOWN));
            move2Button.setText(prefs.getString("move2_name_key", UNKNOWN));
            board.setUseDb(false);
        }

        prog.setProgress(prefs.getBoolean("cheat", false) ? Board.MAX_MOVES
                - board.getMoves() : 0);

        int nextMove;
        if (prefs.getBoolean("cheat", false)) {
            nextMove = board.nextMove();
            // Log.d(TAG, "Cheat mode is on.  nextMove=" + nextMove);
        } else {
            nextMove = Board.MOVE_NONE;
        }
        switch (nextMove) {
        case Board.MOVE_NONE:
            move1Button.setTextColor(Color.BLACK);
            move2Button.setTextColor(Color.BLACK);
            break;
        case Board.MOVE_FLIP:
            move1Button.setTextColor(Color.GREEN);
            move2Button.setTextColor(Color.BLACK);
            break;
        case Board.MOVE_MERGE:
            move1Button.setTextColor(Color.BLACK);
            move2Button.setTextColor(Color.GREEN);
            break;
        }

        // Enable / disable views

        undoButton.setEnabled(board.canUndo());
        rewindButton.setEnabled(board.rewindable());

        // Handle solved/unsolved transitions.

        // Congratulate the user for solving the puzzle, but only if the final
        // operation was a move.
        boolean solved = board.getSolved();
        resetButton.setEnabled(!solved);
        if (solved && (!solvedOld)
                && ((button == move1Button) || (button == move2Button))) {
            if (board.getRandomized()) {
                // It really was a random position.
                msgBox(R.string.congrats);
            }
            else {
                // Maybe they just pressed "flip" twice.
                msgBox(R.string.restored);
            }
            board.setRandomized(false);
        }

        solvedOld = solved;
    }
}