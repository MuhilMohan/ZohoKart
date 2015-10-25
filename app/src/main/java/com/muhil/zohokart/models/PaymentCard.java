package com.muhil.zohokart.models;

import android.content.ContentResolver;
import android.net.Uri;

import com.muhil.zohokart.utils.ZohokartContentProvider;

/**
 * Created by muhil-ga42 on 24/10/15.
 */
public class PaymentCard
{

    public static final String TABLE_NAME = "payment_cards";
    public static final String EMAIL = "email";
    public static final String CARD_NUMBER = "card_number";
    public static final String CARD_TYPE = "card_type";
    public static final String NAME_ON_CARD = "name_on_card";
    public static final String EXPIRY = "expiry";

    public static final String[] PROJECTION = {EMAIL, CARD_NUMBER, CARD_TYPE, NAME_ON_CARD, EXPIRY};

    public static final Uri CONTENT_URI = Uri.withAppendedPath(ZohokartContentProvider.CONTENT_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.muhil.zohokart.models.PaymentCard";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.muhil.zohokart.models.PaymentCard";

    private String email;
    private String cardNumber;
    private String cardType;
    private String nameOnCard;
    private String expiryDate;

    public PaymentCard()
    {
    }

    public PaymentCard(String email, String cardNumber, String cardType, String nameOnCard, String expiryDate)
    {
        this.email = email;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.nameOnCard = nameOnCard;
        this.expiryDate = expiryDate;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    public String getCardType()
    {
        return cardType;
    }

    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

    public String getNameOnCard()
    {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard)
    {
        this.nameOnCard = nameOnCard;
    }

    public String getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate)
    {
        this.expiryDate = expiryDate;
    }
}
