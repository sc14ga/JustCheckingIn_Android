
package co.uk.justcheckingin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StartEventActivity extends Activity {
    // static PendingIntent pendingIntent;

    int PENDING_INTENT_ID = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve information from intent
        Intent intent = getIntent();
        Event e = new Event().fromString(intent.getExtras().getString("event"));
        int day = intent.getExtras().getInt("day");

        for (Timer t : e.getList()) {
            // Get current date and time
            Calendar timeAlarm = Calendar.getInstance();

            int hour = Integer.parseInt(t.hour);
            int minute = Integer.parseInt(t.minute);
            int weekOfYear = timeAlarm.get(Calendar.WEEK_OF_YEAR);
            int year = timeAlarm.get(Calendar.YEAR);

            // Today or next week in case the specified time has passed
            if (timeAlarm.get(Calendar.DAY_OF_WEEK) == day) {
                if (hour * 60 + minute < timeAlarm.get(Calendar.HOUR_OF_DAY) * 60
                        + timeAlarm.get(Calendar.MINUTE)) {
                    // Next week
                    if (timeAlarm.get(Calendar.WEEK_OF_YEAR) == 52) {
                        // if last week of year go to the first week of next year
                        weekOfYear = 1;
                        year = timeAlarm.get(Calendar.YEAR) + 1;
                    }
                    else {
                        weekOfYear = timeAlarm.get(Calendar.WEEK_OF_YEAR) + 1;
                    }
                }
            }
            else if (timeAlarm.get(Calendar.DAY_OF_WEEK) > day) {
                // Next week
                if (timeAlarm.get(Calendar.WEEK_OF_YEAR) == 52) {
                    // if last week of year go to the first week of next year
                    weekOfYear = 1;
                    year = timeAlarm.get(Calendar.YEAR) + 1;
                }
                else {
                    weekOfYear = timeAlarm.get(Calendar.WEEK_OF_YEAR) + 1;
                }
            }

            // Set date
            timeAlarm.set(Calendar.YEAR, year);
            timeAlarm.set(Calendar.WEEK_OF_YEAR, weekOfYear);
            timeAlarm.get(Calendar.DAY_OF_WEEK);    // known Android issue: required to make the following set work
            timeAlarm.set(Calendar.DAY_OF_WEEK, day);

            // Set time
            timeAlarm.set(Calendar.HOUR_OF_DAY, hour);
            timeAlarm.set(Calendar.MINUTE, minute);

            // Create a new PendingIntent and add it to the AlarmManager
            Intent intentAlarm = new Intent(getApplicationContext(), EventAlarmActivity.class);
            intentAlarm.putExtra("name", t.list.getName());
            intentAlarm.putExtra("contacts", t.list.toXML());
            intentAlarm.putExtra("code", PENDING_INTENT_ID);
            //intentAlarm.putExtra("timeEmergency", timeEmergency.getTimeInMillis());

            // intentService = new Intent(StartEventActivity.this,
            // EventEmergencyService.class);
            // intentService.putExtra("contacts", t.list.toXML());

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK);
            Toast.makeText(getApplicationContext(), formatter.format(timeAlarm.getTimeInMillis()), Toast.LENGTH_LONG).show();
             
            // Toast.makeText(StartEventActivity.this, "OK", Toast.LENGTH_SHORT).show();
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    PENDING_INTENT_ID, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
            // EventAlarmActivity.pendingService = PendingIntent.getService(StartEventActivity.this,
            // 10002, EventAlarmActivity.intentService, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, timeAlarm.getTimeInMillis(), pendingIntent);
            // am.set(AlarmManager.RTC_WAKEUP, timeEmergency.getTimeInMillis(),
            // EventAlarmActivity.pendingService);
        }

        finish();
    }
}
