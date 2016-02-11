package com.skeds.android.phone.business.util;

import android.text.TextUtils;

import com.skeds.android.phone.business.Utilities.Logger;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    private static final String DATE_FORMAT_WITHOUT_ZEROS = "M/d/yyyy";

    public static String removeLeadingZeroFromDate(final String dateStr, final String pattern) {
        String formattedDate = null;
        final Date date;
        try {
            if (!TextUtils.isEmpty(dateStr)) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                date = dateFormat.parse(dateStr);
                dateFormat.applyPattern(DATE_FORMAT_WITHOUT_ZEROS);
                formattedDate = dateFormat.format(date);
            }
        } catch (ParseException ignored) {
            Logger.err(ignored.getMessage());
        }

        return formattedDate;
    }

    public static String formatDayMonth(final String dateStr, final String pattern) {
        String formattedDate = null;
        final Date date;
        try {
            if (!TextUtils.isEmpty(dateStr)) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                final SimpleDateFormat dayMonthFormat = new SimpleDateFormat("EEE MMM");
                date = dateFormat.parse(dateStr);
                formattedDate = dayMonthFormat.format(date);
            }
        } catch (ParseException ignored) {
            Logger.err(ignored.getMessage());
        }

        return formattedDate;
    }

    public static String formatDay(final String dateStr, final String pattern) {
        String formattedDate = null;
        final Date date;
        try {
            if (!TextUtils.isEmpty(dateStr)) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                final SimpleDateFormat dayMonthFormat = new SimpleDateFormat("d");
                date = dateFormat.parse(dateStr);
                formattedDate = dayMonthFormat.format(date);
            }
        } catch (ParseException ignored) {
            Logger.err(ignored.getMessage());
        }

        return formattedDate;
    }

    public static Date getDate(final String dateStr, final String pattern, TimeZone timeZone) throws ParseException {

            if (!TextUtils.isEmpty(dateStr)) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                dateFormat.setTimeZone(timeZone);

                Date date = dateFormat.parse(dateStr);
                return date;

            }
        return null;
    }

    public static  String convertFromPatternToPattern(String input,String fromPattern, String toPattern, TimeZone timeZone){
        return  convertFromPatternToPattern(input,fromPattern, toPattern, timeZone, timeZone);

    }

    public static  String convertFromPatternToPattern(String input,String fromPattern, String toPattern, TimeZone fromTimeZone, TimeZone toTimeZone)
    {
        Date date = null;
        try {
            date = getDate(input, fromPattern, fromTimeZone);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        SimpleDateFormat sdf= new SimpleDateFormat(toPattern);
        sdf.setTimeZone(toTimeZone);

        return sdf.format(date);
    }

    public static String formatAmToAM(final String dateStr, final String pattern) {
        String formattedDate = null;
        final Date date;
        try {
            if (!TextUtils.isEmpty(dateStr)) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                date = dateFormat.parse(dateStr);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm aaa");
                formattedDate = simpleDateFormat.format(date).replace("am", "AM").replace("pm", "PM");
            }
        } catch (ParseException ignored) {
            Logger.err(ignored.getMessage());
        }

        return formattedDate;
    }
}
