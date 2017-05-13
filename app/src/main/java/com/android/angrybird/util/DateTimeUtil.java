package com.android.angrybird.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hp pc on 06-05-2017.
 */

public final class DateTimeUtil {

    private DateTimeUtil()
    {

    }

    public static String getFormattedDate(int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(calendar.getTime());
    }

    public static String getCurrentDateTime()
    {
        return DateFormat.getDateTimeInstance().format(new Date());
    }
}
