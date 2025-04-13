package com.example.eventtrackerfinalproject;

public class DateUtils {

    public static String formatTime(int hour, int minute) {
        String period = (hour < 12) ? "AM" : "PM";
        int formattedHour = (hour == 0 || hour == 12) ? 12 : hour % 12;
        String formattedMinute = (minute < 10) ? "0" + minute : String.valueOf(minute);
        return String.format("%d:%s %s", formattedHour, formattedMinute, period);
    }
}
