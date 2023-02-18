package com.habitdev.sprout.utill;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;

public class AlarmScheduler {
    private Context context;
    private AlarmManager alarmMgrMorning, alarmMgrEvening;
    private PendingIntent alarmIntentMorning, alarmIntentEvening;
    private static final String PREFS_NAME = "ALARM_SHARED_PREF";

    public AlarmScheduler(Context context) {
        this.context = context;
    }

    public AlarmScheduler() {
        //no args
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void scheduleMorningAlarm(int hour, int minute, String message) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedHour = prefs.getInt("morning_hour", -1);
        int savedMinute = prefs.getInt("morning_minute", -1);
        if (savedHour == hour && savedMinute == minute) {
            Log.d("tag", "Schedule Morning: Alarm already set for this time");
            return;
        }
        Calendar calendar = setCaledendar(hour, minute);
        scheduleAlarm(calendar, "morning", message);
        prefs.edit().putInt("morning_hour", hour).putInt("morning_minute", minute).apply();
        Log.d("tag", "Schedule Morning: Alarm Set");
    }

    public void scheduleEveningAlarm(int hour, int minute, String message) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedHour = prefs.getInt("evening_hour", -1);
        int savedMinute = prefs.getInt("evening_minute", -1);

        if (savedHour == hour && savedMinute == minute) {
            Log.d("tag", "Schedule Evening: Alarm already set for this time");
            return;
        }
        Calendar calendar = setCaledendar(hour, minute);
        scheduleAlarm(calendar, "evening", message);
        prefs.edit().putInt("evening_hour", hour).putInt("evening_minute", minute).apply();
        Log.d("tag", "Schedule Evening: Alarm Set");
    }

    @NonNull
    private Calendar setCaledendar(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public void updateMorningAlarm(int hour, int minute, String message) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int morningHour = prefs.getInt("morning_hour", -1);
        int morningMinute = prefs.getInt("morning_minute", -1);

        if (morningHour == hour && morningMinute == minute) {
            Log.d("tag", "updateMorningAlarm: already set for " + hour + ":" + minute);
            return;
        }

        cancelMorningAlarm();
        scheduleMorningAlarm(hour, minute, message);
        Log.d("tag", "updateMorningAlarm: alarm updated to " + hour + ":" + minute);
    }

    public void updateEveningAlarm(int hour, int minute, String message) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int eveningHour = prefs.getInt("evening_hour", -1);
        int eveningMinute = prefs.getInt("evening_minute", -1);

        if (eveningHour == hour && eveningMinute == minute) {
            Log.d("tag", "updateEveningAlarm: already set for " + hour + ":" + minute);
            return;
        }

        cancelEveningAlarm();
        scheduleEveningAlarm(hour, minute, message);
        Log.d("tag", "updateEveningAlarm: alarm updated to " + hour + ":" + minute);
    }

    public void cancelMorningAlarm() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (alarmMgrMorning != null) {
            alarmMgrMorning.cancel(alarmIntentMorning);
            alarmMgrMorning = null;

            prefs.edit().remove("morning_hour").remove("morning_minute").apply();
            Log.d("tag", "cancelledMorningAlarm: success");
            return;
        }

        if (alarmMgrMorning == null) {
            //Get existing morning alarm manager
            int morningHour = prefs.getInt("morning_hour", -1);
            int morningMinute = prefs.getInt("morning_minute", -1);

            if (morningHour != -1 && morningMinute != -1) {
                Intent intent = new Intent(context, AlarmReciever.class);
                intent.putExtra("message", "message");
                PendingIntent morningPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.cancel(morningPendingIntent);

                prefs.edit().remove("morning_hour").remove("morning_minute").apply();
                Log.d("tag", "cancelMorningAlarm: success");
            } else {
                Log.d("tag", "cancelMorningAlarm: no alarm scheduled");
            }
        }
    }

    public void cancelEveningAlarm() {

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (alarmMgrEvening != null) {
            alarmMgrEvening.cancel(alarmIntentEvening);
            alarmMgrEvening = null;

            prefs.edit().remove("evening_hour").remove("evening_minute").apply();
            Log.d("tag", "cancelledEveningAlarm: success");
            return;
        }

        if (alarmMgrMorning == null) {
            //Get existing evening alarm manager
            int eveningHour = prefs.getInt("evening_hour", -1);
            int eveningMinute = prefs.getInt("evening_minute", -1);

            if (eveningHour != -1 && eveningMinute != -1) {
                Intent intent = new Intent(context, AlarmReciever.class);
                intent.putExtra("type", "evening");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.cancel(pendingIntent);

                prefs.edit().remove("evening_hour").remove("evening_minute").apply();
                Log.d("tag", "cancelEveningAlarm: success");
            } else {
                Log.d("tag", "cancelEveningAlarm: no alarm scheduled");
            }
        }
    }


    private void scheduleAlarm(Calendar calendar, String type, String message) {

        // create a SimpleDateFormat object with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // use the format() method to convert the Calendar object to a string
        String formattedDate = dateFormat.format(calendar.getTime());
        Log.d("tag", "scheduleAlarm: " + formattedDate);

        Intent intent = new Intent(context, AlarmReciever.class);
        intent.setAction(type);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        if (type.equals("morning")) {
            alarmMgrMorning = alarmMgr;
            alarmIntentMorning = pendingIntent;
        } else if (type.equals("evening")) {
            alarmMgrEvening = alarmMgr;
            alarmIntentEvening = pendingIntent;
        }
    }
}
