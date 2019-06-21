package com.example.newsapp.utils;

import android.util.Log;

import com.example.newsapp.R;
import com.example.newsapp.NewsApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DataUtils {
    public static String dataFormat(String data) {
        try {
            Date date = getDate(data);
            if (getDataTillOneDay(date).isEmpty()) {
                SimpleDateFormat dt1 = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                return dt1.format(date);
            } else {
                return getDataTillOneDay(date);
            }

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }

        return "";
    }

    private static Date getDate(String date) throws ParseException {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dt.parse(date);
    }

    public static String incrementSeconds(String lastDate) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        Date date;
        try {
            date = dt.parse(lastDate);
            int seconds = date.getSeconds();
            date.setSeconds(++seconds);
            SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            return dt1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return "";
    }

    private static String getDataTillOneDay(Date date) {
        Date currentData = new Date();
        Calendar currentCalendar = new GregorianCalendar();
        currentCalendar.setTime(currentData);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH) + 1;
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int currentHours = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCalendar.get(Calendar.MINUTE);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        int Year = calendar.get(Calendar.YEAR);
        int Month = calendar.get(Calendar.MONTH) + 1;
        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        int Hours = calendar.get(Calendar.HOUR_OF_DAY);
        int Minute = calendar.get(Calendar.MINUTE);

        if (currentYear == Year
                && currentMonth == Month
                && currentDay == Day) {
            int agoHours = currentHours - Hours;
            int agoMinute = currentMinute - Minute;
            if (agoHours > 0) {
                return agoHours + " ".concat(NewsApp.instance.getString(R.string.hours_ago));
            } else if (agoMinute > 0) {

                return agoMinute + " ".concat(NewsApp.instance.getString(R.string.minute_ago));
            } else {
                return NewsApp.instance.getString(R.string.just_now);
            }
        }
        return "";
    }
}
