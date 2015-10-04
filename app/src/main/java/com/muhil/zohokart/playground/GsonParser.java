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
       /* Map<String, List<Map<String, List<Map<String, String>>>>> specifications = gson.fromJson(json, new TypeToken<Map<String, List<Map<String, List<Map<String, String>>>>>>() {
        }.getType());

        List<Map<String, List<Map<String, String>>>> productSpecification = specifications.get("1001");
        
        for(Map<String, List<Map<String, String>>> productSpecificationGroups: productSpecification) {
            for(Map.Entry<String, List<Map<String, String>>> productSpecGroup: productSpecificationGroups.entrySet()) {
                System.out.println(productSpecGroup.getKey());
                for(Map<String, String> spec: productSpecGroup.getValue()) {
                    for (Map.Entry<String, String> keyValue: spec.entrySet()) {
                        System.out.println(keyValue.getKey() + " -> " + keyValue.getValue());
                    }
                }
            }
        }*/

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
