package com.muhil.zohokart.playground;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by muhil-ga42 on 23/10/15.
 */
public class EmailValidator
{

    private static Pattern emailNamePtrn = Pattern.compile(
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    public static boolean validateEmail(String email)
    {
        Matcher matcher = emailNamePtrn.matcher(email);
        if (matcher.matches())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static void main(String[] args)
    {
        boolean result;

        result = validateEmail("muhil1234mohan@gmail.com");
        if (result)
        {
            System.out.println("correct email id.");
        }
        else
        {
            System.out.println("incorrect email id.");
        }

    }

}
