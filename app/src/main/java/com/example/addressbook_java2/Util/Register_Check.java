package com.example.addressbook_java2.Util;

public class Register_Check {

    public static boolean check_number(String num)
    {
        char[] s = num.toCharArray();
        int flag = 0;
        for(int i = 0; i < num.length(); i++)
        {
            if((s[i] >= '0' && s[i] <= '9') || (s[i] >= 'a' && s[i] <= 'z') || (s[i] >= 'A' && s[i] <= 'Z'))
            {
                flag = 0;
            }
            else
            {
                flag = 1;
                break;
            }
        }
        if(flag == 0)
            return true;
        else
            return false;
    }

    public static boolean check_passward(String password1, String password2)
    {
        if (password1.equals(password2))
        {
            return true;
        }
        else {
            return false;
        }
    }

}
