package com.muhil.zohokart.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.muhil.zohokart.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by muhil-ga42 on 29/10/15.
 */
public class ImageLoader
{
    final int placeholderImage = R.drawable.blank_screen;
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;

    public ImageLoader(Context context)
    {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    public void displayImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
        {
            imageView.setImageBitmap(bitmap);
        }
        else
        {
            queuePhoto(url, imageView);
            imageView.setImageResource(placeholderImage);
        }
    }

    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotoLoader(photoToLoad));
    }

    private Bitmap getBitmap(String url)
    {
        File file = fileCache.getFile(url);
        Bitmap bitmap = decodeFile(file);
        if (bitmap != null)
        {
            return bitmap;
        }
        else
        {
            try
            {
                Bitmap bitmapFromUrl = null;
                URL imageUrl = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) imageUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(30000);
                urlConnection.setReadTimeout(30000);
                urlConnection.setInstanceFollowRedirects(true);
                InputStream inputStream = urlConnection.getInputStream();
                OutputStream outputStream = new FileOutputStream(file);
                FileTransferUtils.copyStream(inputStream, outputStream);
                outputStream.close();
                bitmapFromUrl = decodeFile(file);
                return bitmapFromUrl;
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }

    private Bitmap decodeFile(File file)
    {
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);

            final int REQUIRED_SIZE = 200;
            int tempWidth = options.outWidth, tempHeight = options.outHeight;
            int scale = 1;

            while (true)
            {
                if ((tempWidth/2 < REQUIRED_SIZE) || (tempHeight/2 < REQUIRED_SIZE))
                {
                    break;
                }
                tempWidth /= 2;
                tempHeight /= 2;
                scale *= 2;
            }
            BitmapFactory.Options finalOptions = new BitmapFactory.Options();
            finalOptions.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, finalOptions);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String url, ImageView imageView)
        {
            this.url = url;
            this.imageView = imageView;
        }
    }

    class PhotoLoader implements Runnable
    {
        PhotoToLoad photoToLoad;

        public PhotoLoader(PhotoToLoad photoToLoad)
        {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run()
        {
            if (imageViewReUsed(photoToLoad))
            {
                return;
            }
            Bitmap bitmap = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bitmap);
            if (imageViewReUsed(photoToLoad))
            {
                return;
            }
            BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(bitmap, photoToLoad);
            Activity activity = (Activity) photoToLoad.imageView.getContext();
            activity.runOnUiThread(bitmapDisplayer);
        }
    }

    boolean imageViewReUsed(PhotoToLoad photoToLoad)
    {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !(tag.equals(photoToLoad.url)))
        {
            return true;
        }
        return false;
    }

    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap bitmap, PhotoToLoad photoToLoad)
        {
            this.bitmap = bitmap;
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run()
        {
            if (imageViewReUsed(photoToLoad))
            {
                return;
            }
            if (bitmap != null)
            {
                photoToLoad.imageView.setImageBitmap(bitmap);
            }
            else
            {
                Log.d("IMAGE", "placeholder replaced");
                photoToLoad.imageView.setImageResource(placeholderImage);
            }
        }
    }

    public void clearCache()
    {
        memoryCache.clear();
        fileCache.clear();
    }

}
