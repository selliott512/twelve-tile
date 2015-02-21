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

import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class DBase {
    public static final int SIZE = 12 * 11 * 10 * 9 * 8;
    private static final String TAG = "M12";
    private byte[] db;

    // TODO: Consider creating a filename constructor.
    public DBase(Context context) {
        InputStream dbIn = context.getResources().openRawResource(
                R.raw.twelvetile);

        // Read the database from disk.
        // Log.d(TAG, "Loading the database");
        try {
            db = new byte[SIZE];
            int totalRead = 0;
            while (totalRead < SIZE) {
                int bytesRead = dbIn.read(db, totalRead, SIZE - totalRead);
                if (bytesRead <= 0) {
                    break;
                }
                totalRead += bytesRead;
            }
            dbIn.close();
        } catch (Exception e) {
            Log.e(TAG, "Could not read the database: " + e);

            // Instead of having the constructor throw an exception later code
            // will notice this.
            db = null;
        }
    }

    public byte[] getBytes() {
        return db;
    }
}
