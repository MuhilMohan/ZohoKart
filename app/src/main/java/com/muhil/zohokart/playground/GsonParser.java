package com.muhil.zohokart.playground;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muhil.zohokart.models.Mobile;

import java.util.List;

public class GsonParser {
    public static void main(String[] args) {

        String json = "[\n" +
                "  {\n" +
                "    \"id\": 1001,\n" +
                "    \"category_id\": 101,\n" +
                "    \"name\": \"Apple iPhone 6\",\n" +
                "    \"brand\": \"Apple\",\n" +
                "    \"color\": \"Gold\",\n" +
                "    \"internal_memory\": 16,\n" +
                "    \"price\": 41937.0,\n" +
                "    \"thumbnail\": \"http://rukmini4.flixcart.com/image/220/220/mobile/h/h/g/apple-iphone-6-original-imaeyny5hqffnnbn.jpeg\",\n" +
                "    \"stars\": 4.5,\n" +
                "    \"ratings\": 768\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 1002,\n" +
                "    \"category_id\": 101,\n" +
                "    \"name\": \"Apple iPhone 6\",\n" +
                "    \"brand\": \"Apple\",\n" +
                "    \"color\": \"Space Grey\",\n" +
                "    \"internal_memory\": 16,\n" +
                "    \"price\": 43258.0,\n" +
                "    \"thumbnail\": \"http://rukmini4.flixcart.com/image/220/220/mobile/f/2/j/apple-iphone-6-original-imaeymdqs5gm5xkz.jpeg\",\n" +
                "    \"stars\": 4.0,\n" +
                "    \"ratings\": 748\n" +
                "  }" +
                "]";
        Gson gson = new Gson();
        List<Mobile> mobiles = gson.fromJson(json, new TypeToken<List<Mobile>>() {
        }.getType());

        for (Mobile mobile : mobiles) {
            /*Product product = new Product(mobile.getId(), mobile.getCategoryId(),
                    mobile.getTitle(), mobile.getDescription(), mobile.getPrice(),
                    mobile.getStars(), mobile.getRatings());*/
            System.out.println(mobile.getProduct());
        }

    }
}
