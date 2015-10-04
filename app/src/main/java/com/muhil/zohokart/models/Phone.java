package com.muhil.zohokart.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhil-ga42 on 25/09/15.
 */
public class Phone {

    public static List<Phone> phoneList;

    static {

        phoneList = new ArrayList<>();

        phoneList.add(new Phone(1, "Apple iPhone 6", "A8001", 57000.00, "http://drop.ndtv.com/TECH/product_database/images/530201374810PM_635_iPhone_4s.png",4.5));
        phoneList.add(new Phone(2, "Apple iPhone 5S", "A7S01", 47599.00, "http://www9.pcmag.com/media/images/301505-apple-iphone-5-at-t.jpg",2.5));
        phoneList.add(new Phone(3, "Apple iPhone 4S", "A6001", 35599.00, "http://drop.ndtv.com/TECH/product_database/images/530201374810PM_635_iPhone_4s.png", 3.0));
        phoneList.add(new Phone(4, "Samsung S6", "S6721", 42000.00, "http://images.samsung.com/is/image/samsung/in_SM-G920IZDAINS_030_Front_gold_thumb?$L2-Gallery$", 3.0));
        phoneList.add(new Phone(5, "Samsung J5", "S54J1", 13999.00, "http://images.samsung.com/is/image/samsung/in_SM-J500FZWDINS_000000001_Front_white_10049712003929?$TM-Gallery$", 3.0));
        phoneList.add(new Phone(6, "Samsung J7", "S54J2", 17999.00, "http://cdn.ndtv.com/tech/images/samsung_galaxy_j7.jpg", 3.0));
        phoneList.add(new Phone(7, "Nokia Lumia 630", "N4621", 10999.00, "http://www.gizmozone.in/wp-content/uploads/2014/05/Nokia-Lumia-630.jpg", 3.0));
        phoneList.add(new Phone(8, "Nokia Lumia 535", "N7822", 7999.00, "http://drop.ndtv.com/TECH/product_database/images/11112014102438AM_635_microsoft_lumia_535.jpeg", 3.0));
        phoneList.add(new Phone(9, "Nokia Lumia 1370", "N9933", 17899.00, "http://i.webapps.microsoft.com/r/image/view/-/4030740/extraHighRes/2/-/Lumia-1320-Hero-1-jpg.jpg", 3.0));
        phoneList.add(new Phone(10, "Micromax Canvas A1", "A8001", 57000.00, "http://drop.ndtv.com/TECH/product_database/images/530201374810PM_635_iPhone_4s.png", 3.0));
        phoneList.add(new Phone(11, "Asus Zenfone 2 Laser", "A8001", 57000.00, "http://drop.ndtv.com/TECH/product_database/images/530201374810PM_635_iPhone_4s.png", 3.0));
        phoneList.add(new Phone(12, "Asus Zenfone 2", "A8001", 57000.00, "http://drop.ndtv.com/TECH/product_database/images/530201374810PM_635_iPhone_4s.png", 3.0));
        phoneList.add(new Phone(13, "Moto G 3rd Gen", "A8001", 57000.00, "http://drop.ndtv.com/TECH/product_database/images/530201374810PM_635_iPhone_4s.png", 3.0));

    }

    private int _id;
    private String name;
    private String model;
    private double price;
    private String displayImageUrl;
    private double rating;

    public Phone() {
    }

    public Phone(int _id, String name, String model, double price, String displayImageUrl, double rating) {
        this._id = _id;
        this.displayImageUrl = displayImageUrl;
        this.model = model;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    public int get_id() {
        return _id;
    }

    public String getDisplayImageUrl() {
        return displayImageUrl;
    }

    public String getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getRating() { return rating; }
}
