package com.muhil.zohokart.activities;

import android.Manifest;
import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.GalleryListAdapter;
import com.muhil.zohokart.adapters.GalleryPagerAdapter;
import com.muhil.zohokart.fragments.GalleryItemFragment;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ProductGalleryActivity extends AppCompatActivity implements GalleryItemFragment.GalleryCommunicator
{

    private static final int REQUEST_STORAGE_PERMISSION_FOR_DOWNLOAD = 100;
    private static final int REQUEST_STORAGE_PERMISSION_FOR_DELETE = 101;
    ImageView productThumbnail;
    TextView productName;
    Product product = null;
    int productId;
    boolean hidden = false;
    ZohokartDAO zohokartDAO;
    List<String> imageLinks;
    ViewPager galleryPager;
    RecyclerView galleryList;
    GalleryListAdapter galleryListAdapter;
    GalleryPagerAdapter galleryPagerAdapter;
    SharedPreferences galleryTutsPref;
    SharedPreferences.Editor galleryTutsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_gallery);

        findViewById(R.id.root_view).setPadding(0, getStatusBarHeight(), 0, 0);

        productThumbnail = (ImageView) findViewById(R.id.product_thumbnail);
        productName = (TextView) findViewById(R.id.product_name);

        findViewById(R.id.download_image).setTranslationY(
                findViewById(R.id.gallery_image_list).getHeight() +
                        findViewById(R.id.download_image).getHeight() + 32);

        findViewById(R.id.delete_image).setTranslationY(
                findViewById(R.id.gallery_image_list).getHeight() +
                        findViewById(R.id.delete_image).getHeight() + 32);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        zohokartDAO = new ZohokartDAO(this);
        galleryTutsPref = getSharedPreferences(ZohoKartSharePreferences.GALLERY_TUTORIAL, MODE_PRIVATE);
        boolean tuts = galleryTutsPref.getBoolean(ZohoKartSharePreferences.GALLERY_TUTORIAL_SEEN, false);
        if (!tuts)
        {
            findViewById(R.id.gallery_tutorial).setVisibility(View.VISIBLE);
        }

        productId = getIntent().getIntExtra("product_id", 0);

        if (productId != 0)
        {
            Log.d("PRODUCT_ID", "" + productId);
            new SetupGalleryTask().execute(productId);
        }

        findViewById(R.id.gallery_tutorial_close_fab).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        findViewById(R.id.gallery_tutorial).animate().setDuration(300).alpha(0).setListener(
                                new Animator.AnimatorListener()
                                {
                                    @Override
                                    public void onAnimationStart(Animator animation)
                                    {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        findViewById(R.id.gallery_tutorial).setVisibility(View.GONE);
                                        galleryTutsEditor = galleryTutsPref.edit();
                                        galleryTutsEditor.putBoolean(ZohoKartSharePreferences.GALLERY_TUTORIAL_SEEN, true);
                                        galleryTutsEditor.apply();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation)
                                    {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation)
                                    {

                                    }
                                }
                        );
                    }
                }
        );

        findViewById(R.id.download_image).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                        {
                            Log.d("SD_CARD", "available");
                            if (hasStoragePermissions(REQUEST_STORAGE_PERMISSION_FOR_DOWNLOAD))
                            {
                                int currentImage = galleryPager.getCurrentItem();
                                new DownloadImageTask().execute(imageLinks.get(currentImage));
                            }
                        }
                        else
                        {
                            AlertDialog.Builder SDCardChecker = new AlertDialog.Builder(ProductGalleryActivity.this);
                            SDCardChecker.setCancelable(true);
                            SDCardChecker.setMessage("No SD card available to save images");
                            SDCardChecker.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            });
                            SDCardChecker.show();
                        }
                    }
                }
        );

        findViewById(R.id.delete_image).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                        {
                            Log.d("SD_CARD", "available");
                            if (hasStoragePermissions(REQUEST_STORAGE_PERMISSION_FOR_DELETE))
                            {
                                int currentImage = galleryPager.getCurrentItem();
                                if (fileDeleted(Uri.parse(imageLinks.get(currentImage)).getLastPathSegment()))
                                {
                                    showDownload();
                                }
                            }
                        }
                        else
                        {
                            AlertDialog.Builder SDCardChecker = new AlertDialog.Builder(ProductGalleryActivity.this);
                            SDCardChecker.setCancelable(true);
                            SDCardChecker.setMessage("No SD card available to save images");
                            SDCardChecker.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            });
                            SDCardChecker.show();
                        }
                    }
                }
        );

    }



    private boolean hasStoragePermissions(final int requestCode)
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            Log.d("PERMISSION", "not granted");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                AlertDialog.Builder storagePermissionRequester = new AlertDialog.Builder(this);
                storagePermissionRequester.setMessage("Storage permissions needed to download images");
                storagePermissionRequester.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ActivityCompat.requestPermissions(ProductGalleryActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                requestCode);
                    }
                });
                storagePermissionRequester.show();
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        requestCode);
            }
            return false;
        }
        else
        {
            Log.d("PERMISSION", "already granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_STORAGE_PERMISSION_FOR_DOWNLOAD:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("PERMISSION", "permission granted");
                    int currentImage = galleryPager.getCurrentItem();
                    new DownloadImageTask().execute(imageLinks.get(currentImage));
                }
                else
                {
                    Log.d("PERMISSION", "permission denied");
                }
                break;
            case REQUEST_STORAGE_PERMISSION_FOR_DELETE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("PERMISSION", "permission granted");
                    int currentImage = galleryPager.getCurrentItem();
                    if (fileDeleted(Uri.parse(imageLinks.get(currentImage)).getLastPathSegment()))
                    {
                        showDownload();
                    }
                }
                else
                {
                    Log.d("PERMISSION", "permission denied");
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        findViewById(R.id.download_image).animate().setDuration(400).translationY(
                findViewById(R.id.gallery_image_list).getHeight() +
                findViewById(R.id.download_image).getHeight() + 32);
        findViewById(R.id.delete_image).animate().setDuration(400).translationY(
                findViewById(R.id.gallery_image_list).getHeight() +
                        findViewById(R.id.delete_image).getHeight() + 32);

        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        GalleryListAdapter.galleryListItems.clear();
        super.onBackPressed();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void toggleGalleryList()
    {
        if (hidden)
        {
            findViewById(R.id.gallery_actions).animate().setDuration(200).translationY(0);
            hidden = false;
        }
        else
        {
            findViewById(R.id.gallery_actions).animate().setDuration(200).translationY(findViewById(R.id.gallery_image_list).getHeight());
            hidden = true;
        }
    }

    @Override
    public void setSelectedItemInPager(int position)
    {
        galleryPager.setCurrentItem(position);
    }

    class SetupGalleryTask extends AsyncTask<Integer, Void, List<String>>
    {

        @Override
        protected void onPreExecute()
        {
            findViewById(R.id.gallery_loading).setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<String> doInBackground(Integer... productId)
        {
            imageLinks = zohokartDAO.getImageLinksForProductId(productId[0]);
            product = zohokartDAO.getProductForProductId(productId[0]);
            return imageLinks;
        }

        @Override
        protected void onPostExecute(final List<String> imageLinks)
        {

            if (product != null)
            {
                productName.setText(product.getTitle());
            }

            if (imageLinks != null)
            {
                Log.d("IMAGE_LINKS", imageLinks.toString());
                galleryPager = (ViewPager) findViewById(R.id.gallery_pager);
                galleryPagerAdapter = new GalleryPagerAdapter(ProductGalleryActivity.this.getSupportFragmentManager(), ProductGalleryActivity.this, imageLinks);
                galleryPager.setAdapter(galleryPagerAdapter);
                findViewById(R.id.download_image).animate().setDuration(400).translationY(0);
                findViewById(R.id.delete_image).animate().setDuration(400).translationY(0);
                checkFileForAction(Uri.parse(imageLinks.get(0)).getLastPathSegment());
                galleryPager.addOnPageChangeListener(
                        new ViewPager.OnPageChangeListener()
                        {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                            {

                            }

                            @Override
                            public void onPageSelected(int position)
                            {
                                GalleryListAdapter.setSelectedItem(position);
                                checkFileForAction(Uri.parse(imageLinks.get(position)).getLastPathSegment());
                            }

                            @Override
                            public void onPageScrollStateChanged(int state)
                            {

                            }
                        }
                );
                galleryList = (RecyclerView) findViewById(R.id.gallery_list);
                galleryList.setLayoutManager(new LinearLayoutManager(ProductGalleryActivity.this, LinearLayoutManager.HORIZONTAL, false));
                galleryListAdapter = new GalleryListAdapter(ProductGalleryActivity.this, imageLinks);
                galleryList.setAdapter(galleryListAdapter);
                findViewById(R.id.gallery_loading).setVisibility(View.GONE);
            } else
            {
                findViewById(R.id.gallery_empty).setVisibility(View.VISIBLE);
            }

            super.onPostExecute(imageLinks);
        }
    }

    class DownloadImageTask extends AsyncTask<String, Integer, Void>
    {

        String fileName;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... url)
        {
            try
            {
                URL imageUrl = new URL(url[0]);
                fileName = Uri.parse(url[0]).getLastPathSegment();
                File destinationFolder = new File(Environment.getExternalStorageDirectory(), "ZohoKart pictures");
                if (!destinationFolder.exists())
                {
                    destinationFolder.mkdirs();
                }
                File destination = new File(destinationFolder.getPath(), fileName);
                if (!destination.exists())
                {
                    destination.createNewFile();
                }
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream sourceFile = connection.getInputStream();
                int fileLength = connection.getContentLength();
                OutputStream destinationFile = new FileOutputStream(destination);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = sourceFile.read(data)) != -1) {
                    total += count;
                    // Publish the progress
                    publishProgress((int) (total * 100 / fileLength));
                    destinationFile.write(data, 0, count);
                }
                destinationFile.flush();
                destinationFile.close();
                sourceFile.close();
            }
            catch (Exception e)
            {
                // Error Log
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (!ifFileExists(fileName))
            {
                showDownload();
            }
            else
            {
                showDelete();
            }
        }

    }

    private void checkFileForAction(String filename)
    {
        if (ifFileExists(filename))
        {
            showDelete();
        }
        else
        {
            showDownload();
        }
    }

    private boolean ifFileExists(String fileName)
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/ZohoKart pictures", fileName);
        return file.exists();
    }

    private boolean fileDeleted(String fileName)
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/ZohoKart pictures", fileName);
        return file.delete();
    }

    public void showDownload()
    {
        findViewById(R.id.delete_image).animate().setDuration(500).alpha(0).rotation(360);
        findViewById(R.id.delete_image).setVisibility(View.GONE);
        findViewById(R.id.delete_image).setRotation(0);
        findViewById(R.id.download_image).setAlpha(0);
        findViewById(R.id.download_image).setRotation(0);
        findViewById(R.id.download_image).setVisibility(View.VISIBLE);
        findViewById(R.id.download_image).animate().setDuration(500).alpha(1).rotation(360);
    }

    public void showDelete()
    {
        findViewById(R.id.download_image).animate().setDuration(500).alpha(0).rotation(360);
        findViewById(R.id.download_image).setVisibility(View.GONE);
        findViewById(R.id.download_image).setRotation(0);
        findViewById(R.id.delete_image).setAlpha(0);
        findViewById(R.id.delete_image).setRotation(0);
        findViewById(R.id.delete_image).setVisibility(View.VISIBLE);
        findViewById(R.id.delete_image).animate().setDuration(500).alpha(1).rotation(360);
    }

    @Override
    protected void onResume()
    {
        Log.d("RESUME", "on resume");
        super.onResume();
    }
}
