package com.example.serwe.Common;

import com.example.serwe.Model.GoogleUser;
import com.example.serwe.Model.User;

public class Common {
    public static User currentUser;
    public static GoogleUser googleUser;
    public static String convertCodeToStatus(String code)
    {
        if(code.equals("0"))
            return "Placed";
        else
        if(code.equals("1"))
            return "On My Way";

        else
            return "Shipped";
    }
}
