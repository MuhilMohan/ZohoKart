package com.muhil.zohokart.playground;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muhil.zohokart.models.specification.Specification;
import com.muhil.zohokart.models.specification.SpecificationGroup;

import java.util.List;
import java.util.Map;

public class GsonParser {
    public static void main(String[] args) {
        Gson gson = new Gson();
        String json = "{\n" +
                "  \"1001\": [\n" +
                "    {\n" +
                "      \"name\": \"General\",\n" +
                "      \"specifications\": [{\"key\":\"Brand\", \"value\": \"Apple\"},{\"key\": \"Video Calling\", \"value\": \"Yes\"}]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Display\",\n" +
                "      \"specifications\": [{\"key\": \"HD\", \"value\": \"FullHD\"}, {\"key\": \"Resolution\", \"value\": \"1280x900\"}]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Map<String, List<SpecificationGroup>> specifications = gson.fromJson(json, new TypeToken<Map<String, List<SpecificationGroup>>>() {
        }.getType());

        List<SpecificationGroup> specificationGroups = specifications.get("1001");
        for (SpecificationGroup specificationGroup : specificationGroups) {
            System.out.println(specificationGroup.getName());
            for (Specification specification : specificationGroup.getSpecifications()) {
                System.out.println(specification.getKey() + " = " + specification.getValue());
            }
        }
    }

    public static void expand(final View v)
    {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }
            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(200);
        v.startAnimation(a);
    }

    public static void collapse(final View v)
    {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                if(interpolatedTime == 1)
                {
                    v.setVisibility(View.GONE);
                }
                else
                {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(200);
        v.startAnimation(a);
    }
}
