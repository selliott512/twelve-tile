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
import android.os.Bundle;
import android.webkit.WebView;

public class Help extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        WebView helpWebView = (WebView) findViewById(R.id.helpWebView);
        helpWebView.loadUrl("file:///android_asset/help.html");
    }
}
