package com.muhil.zohokart.activities;

import android.animation.Animator;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.GalleryListAdapter;
import com.muhil.zohokart.adapters.GalleryPagerAdapter;
import com.muhil.zohokart.fragments.GalleryItemFragment;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.List;

public class ProductGalleryActivity extends AppCompatActivity implements GalleryItemFragment.GalleryCommunicator
{



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
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        findViewById(R.id.root_view).setPadding(0, getStatusBarHeight(), 0, 0);
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

    }

    @Override
    public void onBackPressed()
    {
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
    public void toggleGalleryList(boolean flag)
    {
        if (flag)
        {
            if (hidden)
            {
                findViewById(R.id.gallery_image_list).animate().setDuration(300).y(findViewById(R.id.root_view).getHeight()-findViewById(R.id.gallery_image_list).getHeight());
                hidden = false;
            }
        }
        else
        {
            if (!hidden)
            {
                findViewById(R.id.gallery_image_list).animate().setDuration(300).y(findViewById(R.id.root_view).getHeight()+findViewById(R.id.gallery_image_list).getHeight());
                hidden = true;
            }
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
            return imageLinks;
        }

        @Override
        protected void onPostExecute(final List<String> imageLinks)
        {

            if (imageLinks != null)
            {
                Log.d("IMAGE_LINKS", imageLinks.toString());
                galleryPager = (ViewPager) findViewById(R.id.gallery_pager);
                galleryPagerAdapter = new GalleryPagerAdapter(ProductGalleryActivity.this.getSupportFragmentManager(), ProductGalleryActivity.this, imageLinks);
                galleryPager.setAdapter(galleryPagerAdapter);
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
            }
            else
            {
                findViewById(R.id.gallery_empty).setVisibility(View.VISIBLE);
            }

            super.onPostExecute(imageLinks);
        }
    }

}
