package com.muhil.zohokart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.models.SubCategory;

import java.util.List;

/**
 * Created by muhil-ga42 on 29/09/15.
 */
public class SubCategoryAdapter extends ArrayAdapter {

    List<SubCategory> subCategories;
    Context context;
    int layoutResource;
    TextView subCategoryName;

    public SubCategoryAdapter(Context context, int layoutResource, List<SubCategory> subCategories) {
        super(context, layoutResource, subCategories);
        this.context = context;
        this.layoutResource = layoutResource;
        this.subCategories = subCategories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(layoutResource, parent, false);
        subCategoryName = (TextView) convertView.findViewById(R.id.subCategoryItem);

        subCategoryName.setText(subCategories.get(position).getName());

        return convertView;
    }
}
