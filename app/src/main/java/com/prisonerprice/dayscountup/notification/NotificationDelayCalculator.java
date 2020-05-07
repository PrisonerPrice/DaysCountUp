package com.prisonerprice.dayscountup.notification;

import android.content.Intent;

import com.prisonerprice.dayscountup.utils.LeapYearCalculator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.System.currentTimeMillis;

public class NotificationDelayCalculator {

    public final static Long A_DAY_IN_MILI = 24 * 3600 * 1000L;

    public static Long calculateDays(Date date, int day) {
        Long prevTimestamp = date == null ? null : date.getTime();
        Date notificationDate = new Date(prevTimestamp + day * A_DAY_IN_MILI);
        Long notificationTimestamp = notificationDate.getTime();
        return (notificationTimestamp - currentTimeMillis()) / 1000;
    }

    // return future 10 years' notification delays
    public static List<Long> calculateAnniversary(Date date){
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

        List<String> notificationDateStrings = new ArrayList<>();
        String dateString = format.format(date);
        String[] dateStringSplits = dateString.split("-");
        int year = Integer.parseInt(dateStringSplits[2]);
        int month = Integer.parseInt(dateStringSplits[0]);
        int day = Integer.parseInt(dateStringSplits[1]);

        if(month == 2 && day == 29) {
            for(int i = 1; i <= 10; i++) {
                if (LeapYearCalculator.isLeapYear(year + i)) {
                    String s = "02-28-" + (year + i);
                    notificationDateStrings.add(s);
                } else {
                    String s = "02-29-" + (year + i);
                    notificationDateStrings.add(s);
                }
            }
        } else {
            String prefix = "" + month + "-" + day + "-";
            for(int i = 1; i <= 10; i++) {
                if (LeapYearCalculator.isLeapYear(year + i)) {
                    String s = prefix + (year + i);
                    notificationDateStrings.add(s);
                }
            }
        }

        List<Long> notificationDateLongs = new ArrayList<>();
        try {
            for(String ds : notificationDateStrings) {
                Long notificationLong = format.parse(ds).getTime();
                notificationDateLongs.add((notificationLong - System.currentTimeMillis()) / 1000);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return notificationDateLongs;
    }
}
