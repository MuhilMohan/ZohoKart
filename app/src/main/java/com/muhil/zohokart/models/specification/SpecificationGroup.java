package com.muhil.zohokart.models.specification;

import android.content.ContentResolver;
import android.net.Uri;

import com.muhil.zohokart.utils.ZohokartContentProvider;

import java.util.List;

public class SpecificationGroup {

    public static final String TABLE_NAME = "specifications";
    public static final String PRODUCT_ID = "product_id";
    public static final String GROUP_NAME = "group_name";
    public static final String SPECIFICATIONS = "specifications";

    public static final String[] PROJECTION = {PRODUCT_ID, GROUP_NAME, SPECIFICATIONS};

    public static final Uri CONTENT_URI = Uri.withAppendedPath(ZohokartContentProvider.CONTENT_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.muhil.zohokart.models.SpecificationGroup";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.muhil.zohokart.models.SpecificationGroup";

    public int productId;
    public String name;
    public List<Specification> specifications;

    public SpecificationGroup() {

    }

    public SpecificationGroup(String name, List<Specification> specifications) {
        this.name = name;
        this.specifications = specifications;
    }

    public SpecificationGroup(int productId, String name, List<Specification> specifications) {
        this.name = name;
        this.productId = productId;
        this.specifications = specifications;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Specification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<Specification> specifications) {
        this.specifications = specifications;
    }
}
