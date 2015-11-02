package com.muhil.zohokart.utils;

/**
 * Created by muhil-ga42 on 30/10/15.
 */
public class CardValidator
{
    public static boolean ifAnyDigitExist(String nameOnCard)
    {
        boolean result = false;
        char[] nameOnCardArray = nameOnCard.toCharArray();
        for (char aNameOnCardArray : nameOnCardArray)
        {
            if ((aNameOnCardArray <= 'A' || aNameOnCardArray >= 'Z'))
            {
                if (String.valueOf(aNameOnCardArray).equals(" "))
                {
                    result = false;
                } else
                {
                    result = true;
                    break;
                }
            } else
            {
                result = false;
            }
        }
        return result;
    }

    public static boolean ifAnyLetterExist(String cardNumber)
    {
        boolean result = false;
        char[] cardNumberArray = cardNumber.toCharArray();
        for (char cardNumbrArrayChar : cardNumberArray)
        {
            if (cardNumbrArrayChar <='0' || cardNumbrArrayChar >= '9')
            {
                result = true;
                break;
            }
            else
            {
                result = false;
            }
        }
        return result;
    }

}
