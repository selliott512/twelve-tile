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

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.TextView;

public class Tile extends TextView {

    private SharedPreferences prefs;
    private boolean square;
    private int value;

    public Tile(Context context, AttributeSet attrs) {
        super(context, attrs);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();

        // Make the tile be square. The width is used, but perhaps this should
        // be configurable.
        if (!square) {
            setHeight(width);
            square = true;
        }

        // Border used to make the tiles distinct. The variation in the
        // thickness of each side was found by trial and error to center the
        // letters in each tile.
        // TODO: Find a better way of centering the letters that involves
        // querying the font metrics.
        canvas.drawRect(0, 0, width, 0.03f * width, getPaint());
        canvas.drawRect(0.95f * width, 0, width, width, getPaint());
        canvas.drawRect(0, 0.9f * width, width, width, getPaint());
        canvas.drawRect(0, 0, 0.1f * width, width, getPaint());
    }

    private void setLabel() {
        if (prefs.getBoolean("five", false) && (value >= 5)) {
            setText("");
        } else {
            setText(" " + new String(Character.toString((char) ('A' + value))));
        }
    }

    public void setValue(int v) {
        value = v;
        setLabel();

        int red;
        int green;
        int blue;

        if (prefs.getBoolean("mono", false)) {
            red = green = blue = 0;
        } else {
            if (value < Board.LENGTH / 2) {
                green = (200 * value) / ((Board.LENGTH) / 2);
                red = 200 - green;
                blue = 0;
            } else {
                green = (200 * (Board.LENGTH - value - 1))
                        / ((Board.LENGTH) / 2);
                red = 0;
                blue = 200 - green;
            }
        }

        setBackgroundColor(Color.rgb(red, green, blue));
    }
}
