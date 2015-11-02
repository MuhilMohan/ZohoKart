package com.muhil.zohokart.utils;

import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

/**
 * Created by muhil-ga42 on 29/10/15.
 */
public class FileCache
{
    private File imageFileCache;

    public FileCache(Context context)
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            Log.d("STORAGE_OPENED", "true");
            imageFileCache = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ZohoKart_Images");
        }
        else
        {
            imageFileCache = context.getCacheDir();
        }
        if (!(imageFileCache.mkdirs()))
        {
            imageFileCache.mkdirs();
        }
    }

    public File getFile(String url)
    {

        String fileName = String.valueOf(url.hashCode());
        File file = new File(imageFileCache, fileName);
        if (imageFileCache.exists())
        {
            Log.d("FILE_CACHE", "cache EXISTS");
        }
        else
        {
            Log.d("FILE_CACHE", "cache DOESNT EXISTS");
        }
        if (file.exists())
        {
            Log.d("FILE_EXIST", "EXISTS");
        }
        else
        {
            Log.d("FILE_EXIST", "DOESNT EXISTS");
        }
        return file;
    }

    public void clear()
    {
        File[] files = imageFileCache.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                file.delete();
            }
        }
    }



}
