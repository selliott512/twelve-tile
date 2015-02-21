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

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Preferences extends PreferenceActivity {
    static Preferences last; // Last instance of this activity created.
    private static final String TAG = "M12";

    static void exit() {
        if (last == null) {
            Log.e(TAG, "Unable to shutdown the last instance of the "
                    + "Preferences activity due to last being null.");
        } else {
            last.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This is done since there does not seem to be a good way to otherwise
        // find a handle to this activity.
        last = this;

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
