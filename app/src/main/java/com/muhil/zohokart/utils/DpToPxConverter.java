package com.muhil.zohokart.utils;

import android.content.res.Resources;

/**
 * Created by muhil-ga42 on 27/10/15.
 */
public class DpToPxConverter
{

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
