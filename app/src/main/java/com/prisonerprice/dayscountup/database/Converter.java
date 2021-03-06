package com.prisonerprice.dayscountup.database;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Converter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimeStamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String toString(ArrayList<Date> dates) {
        if(dates == null || dates.size() == 0) return null;
        else {
            DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < dates.size() - 1; i++) {
                sb.append(format.format(dates.get(i)) + "!");
            }
            sb.append(format.format(dates.get(dates.size() - 1)));
            return sb.toString();
        }
    }

    @TypeConverter
    public static ArrayList<Date> toArray(String dateString) {
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        ArrayList<Date> result = new ArrayList<>();
        if (dateString == null || dateString.length() == 0) return result;

        String[] dateStrings = dateString.split("!");
        try {
            for(String ds : dateStrings) {
                result.add(format.parse(ds));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
