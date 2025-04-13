package com.example.eventtrackerfinalproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

public class ReminderManager {
    private Context context;
    private Queue<Reminder> reminderQueue;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public ReminderManager(Context context) {
        this.context = context;
        this.reminderQueue = new PriorityQueue<>((r1, r2) -> {
            try {
                Date date1 = dateFormat.parse(r1.getDate() + " " + r1.getTime());
                Date date2 = dateFormat.parse(r2.getDate() + " " + r2.getTime());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                return 0;
            }
        });
    }

    public void addToQueue(Reminder reminder) {
        reminderQueue.add(reminder);
    }

    public void processNextReminder() throws ParseException {
        if (!reminderQueue.isEmpty()) {
            Reminder next = reminderQueue.poll();
            setAlarm(next);
        }
    }

    public void setAlarm(Reminder reminder) throws ParseException {
        Intent intent = new Intent(context, Alarm.class);
        intent.putExtra(Constants.EXTRA_TIME, reminder.getTime());
        intent.putExtra(Constants.EXTRA_DATE, reminder.getDate());
        intent.putExtra(Constants.EXTRA_EVENT, reminder.getTitle());
        intent.putExtra(Constants.EXTRA_DESCRIPTION, reminder.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        String dateTime = reminder.getDate() + " " + reminder.getTime();
        DateFormat format = new SimpleDateFormat("d-M-yyyy hh:mm");
        Date dateToSet = format.parse(dateTime);

        if (alarmManager != null && dateToSet != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dateToSet.getTime(), pendingIntent);
            Toast.makeText(context, "Alarm set", Toast.LENGTH_SHORT).show();
        }
    }
}
