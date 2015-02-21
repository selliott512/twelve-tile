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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Reset extends Activity {
    private static final int DIALOG_RESET_CONFIRM = 1; // Not really used.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDialog(DIALOG_RESET_CONFIRM);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return new AlertDialog.Builder(Reset.this).setTitle(
                R.string.reset_question).setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Ok clicked
                        SharedPreferences prefs = PreferenceManager
                                .getDefaultSharedPreferences(Reset.this);
                        Editor edit = prefs.edit();
                        edit.clear();
                        edit.commit();
                        PreferenceManager.setDefaultValues(Reset.this,
                                R.xml.preferences, true);

                        // The preference screen is outdated now.
                        Preferences.exit();

                        // Reset the board of the puzzle.
                        TwelveTile.reset();

                        finish();
                    }
                }).setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Cancel clicked
                        finish();
                    }
                }).create();
    }
}
