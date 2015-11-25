package com.muhil.zohokart.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    Context context;
    final int placeholderImage = R.drawable.placeholder;
    LruBitmapCache memoryCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;

    public ImageLoader(Context context)
    {
        this.context = context;
        memoryCache = new LruBitmapCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    /*public void displayImage(String url, ImageView imageView)
    {
        //imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.getBitmapFromMemCache(url);
        if (bitmap != null)
        {
            Log.d("LOADER", "from cache");
            ImageViewAnimatedChange(context, imageView, bitmap);
        }
        else
        {
            Log.d("LOADER", "downloading");
            BitmapWorkerTask task = new BitmapWorkerTask(url, imageView);
            task.execute(url);
            imageView.setImageResource(placeholderImage);
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>
    {
        String url;
        ImageView imageView;
        public BitmapWorkerTask(String url, ImageView imageView)
        {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... url)
        {
            try
            {
                Bitmap bitmapFromUrl;
                URL imageUrl = new URL(url[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) imageUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(30000);
                urlConnection.setReadTimeout(30000);
                urlConnection.setInstanceFollowRedirects(true);
                InputStream inputStream = urlConnection.getInputStream();
                bitmapFromUrl = BitmapFactory.decodeStream(inputStream);
                return bitmapFromUrl;
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            memoryCache.addBitmapToMemoryCache(url, bitmap);
            Log.d("LOADER", "" + memoryCache.size());
            BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(bitmap, imageView);
            Activity activity = (Activity) imageView.getContext();
            activity.runOnUiThread(bitmapDisplayer);
        }
    }

    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        ImageView imageView;

        public BitmapDisplayer(Bitmap bitmap, ImageView imageView)
        {
            this.bitmap = bitmap;
            this.imageView = imageView;
        }

        @Override
        public void run()
        {
            if (bitmap != null)
            {
                ImageViewAnimatedChange(context, imageView, bitmap);
            }
            else
            {
                Log.d("IMAGE", "placeholder replaced");
                imageView.setImageResource(placeholderImage);
            }
        }
    }

    public static void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }*/

}
