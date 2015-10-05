package com.muhil.zohokart.utils;

import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.models.specification.SpecificationGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHolder {
    public static Map<String, List<SpecificationGroup>> specifications = new HashMap<>();
    public static List<PromotionBanner> promotionBanners = new ArrayList<>();
}
