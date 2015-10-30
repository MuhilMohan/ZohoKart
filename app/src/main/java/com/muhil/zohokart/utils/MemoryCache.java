package com.muhil.zohokart.utils;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by muhil-ga42 on 29/10/15.
 */
public class MemoryCache
{
    private static final String TAG = "memory_cache";
    private Map<String, Bitmap> memoryCache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
    private long size = 0;
    private long limit = 1000000;

    public MemoryCache()
    {
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    public void setLimit(long newLimit)
    {
        this.limit = newLimit;
    }

    public Bitmap get(String url)
    {
        try
        {
            if (!(memoryCache.containsKey(url)))
            {
                return null;
            }
            else
            {
                return memoryCache.get(url);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void put(String url, Bitmap bitmap)
    {
        try
        {
            if (memoryCache.containsKey(url))
            {
                size -= getSizeInBytes(memoryCache.get(url));
            }
            memoryCache.put(url, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        }
        catch (Throwable e)
        {
            e.printStackTrace();

        }
    }

    private void checkSize()
    {
        if (size > limit)
        {
            for (Map.Entry<String, Bitmap> integerBitmapEntry : memoryCache.entrySet())
            {
                size -= getSizeInBytes(integerBitmapEntry.getValue());
                memoryCache.remove(integerBitmapEntry.getKey());
                if (size <= limit)
                {
                    break;
                }
            }
        }
    }

    public void clear()
    {
        try
        {
            memoryCache.clear();
            size = 0;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    long getSizeInBytes(Bitmap bitmap)
    {
        if (bitmap != null)
        {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
        else
        {
            return 0;
        }
    }

}
