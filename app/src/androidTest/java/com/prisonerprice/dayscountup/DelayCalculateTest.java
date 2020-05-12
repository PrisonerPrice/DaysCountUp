package com.prisonerprice.dayscountup;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.prisonerprice.dayscountup.notification.NotificationDelayCalculator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class DelayCalculateTest {

    private static final String TAG = DelayCalculateTest.class.getSimpleName();
    private DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
    private Date date;
    private Date today;

    @Before
    public void init() throws ParseException {
        date = new Date(System.currentTimeMillis());
        String dateString = format.format(date);
        today = format.parse(dateString);
    }

    @Test
    public void calculateDaysTest() {
        Long delayInSeconds = NotificationDelayCalculator.calculateDays(today, 1);

        Log.d(TAG, delayInSeconds + "");

        Assert.assertTrue(delayInSeconds < 3600L * 24);
    }

    @Test
    public void calculateAnniversaryTest() {
        List<Long> delayLongs = NotificationDelayCalculator.calculateAnniversary(today);

        Assert.assertTrue(delayLongs.size() == 10);

        for(int i = 0; i < 10; i++) {
            Log.d(TAG, delayLongs.get(i) + "");
            Assert.assertTrue(delayLongs.get(i) > 364 * 3600L * 24 * (i + 1));
            Assert.assertTrue(delayLongs.get(i) < 366 * 3600L * 24 * (i + 1));
        }

    }
}
