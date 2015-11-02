package com.muhil.zohokart.utils;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by muhil-ga42 on 31/10/15.
 */
public class SnackBarProvider
{
    public static Snackbar getSnackbar(String textToDisplay, View rootview)
    {
        Snackbar snackbar = Snackbar.make(rootview, textToDisplay, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        return snackbar;
    }
}
