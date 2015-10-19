package com.muhil.zohokart.models;

import java.util.List;

/**
 * Created by muhil-ga42 on 19/10/15.
 */
public class FilterPair
{

    private String selectionString;
    private List<String> selectionArgs;

    public FilterPair()
    {
    }

    public FilterPair(String selectionString, List<String> selectionArgs)
    {
        this.selectionArgs = selectionArgs;
        this.selectionString = selectionString;
    }

    public List<String> getSelectionArgs()
    {
        return selectionArgs;
    }

    public String getSelectionString()
    {
        return selectionString;
    }
}
