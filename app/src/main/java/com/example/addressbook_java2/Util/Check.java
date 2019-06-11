package com.example.addressbook_java2.Util;

public class Check {
    public static boolean telteCheck(String telte)
    {
        int flag = 1;
        char[] s = telte.toCharArray();
        for(int i = 0; i < telte.length(); i++){
            if(s[i] >= '0' && s[i] <= '9')
                flag = 1;
            else {
                flag = 0;
                break;
            }
        }
        if(flag == 1)
            return true;
        else
            return false;
    }

    public static boolean nameCheck(String name)
    {
        if(name.length() > 10)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
