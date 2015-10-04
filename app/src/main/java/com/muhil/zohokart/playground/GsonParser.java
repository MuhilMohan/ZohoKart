package com.muhil.zohokart.playground;

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
}
