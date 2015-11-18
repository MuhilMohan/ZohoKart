package com.muhil.zohokart.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.GalleryListAdapter;
import com.muhil.zohokart.adapters.GalleryPagerAdapter;
import com.muhil.zohokart.fragments.GalleryItemFragment;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.List;

public class ProductGalleryActivity extends AppCompatActivity implements GalleryItemFragment.GalleryCommunicator
{

    int productId;
    boolean galleryListFlag = false;
    ZohokartDAO zohokartDAO;
    List<String> imageLinks;
    ViewPager galleryPager;
    RecyclerView galleryList;
    GalleryListAdapter galleryListAdapter;
    GalleryPagerAdapter galleryPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_gallery);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        findViewById(R.id.root_view).setPadding(0, getStatusBarHeight(), 0, 0);
        zohokartDAO = new ZohokartDAO(this);

        productId = getIntent().getIntExtra("product_id", 0);

        if (productId != 0)
        {
            Log.d("PRODUCT_ID", "" + productId);
            new SetupGalleryTask().execute(productId);
        }

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
    public void toggleGalleryList()
    {
        if (galleryListFlag)
        {
            findViewById(R.id.gallery_image_list).animate().setDuration(300).y(findViewById(R.id.root_view).getHeight()-findViewById(R.id.gallery_image_list).getHeight());
            galleryListFlag = false;
        }
        else
        {
            findViewById(R.id.gallery_image_list).animate().setDuration(300).y(findViewById(R.id.root_view).getHeight()+findViewById(R.id.gallery_image_list).getHeight());
            galleryListFlag = true;
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
            Log.d("PRODUCT_ID", "" + imageLinks.toString());
            return imageLinks;
        }

        @Override
        protected void onPostExecute(final List<String> imageLinks)
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

            super.onPostExecute(imageLinks);
        }
    }

}
