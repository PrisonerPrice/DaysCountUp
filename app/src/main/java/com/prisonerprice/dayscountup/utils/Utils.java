package com.prisonerprice.dayscountup.utils;

public class Utils {

    public static boolean isLeapYear(int year) {
        if (year % 4 != 0) return false;
        else if (year % 100 == 0) {
            if(year % 400 == 0)
                return true;
            else
                return false;
        }
        return true;
    }
}