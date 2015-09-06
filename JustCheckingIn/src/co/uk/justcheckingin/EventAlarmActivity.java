
package co.uk.justcheckingin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Runs whenever an Event is activated. It creates and shows the pop-up window, while it programs a
 * thread to execute the emergency functionality.
 * 
 * @author Georgios Aikaterinakis
 */
public class EventAlarmActivity extends Activity {
    private AudioManager audioManager;
    private MediaPlayer mMediaPlayer;
    private int savedVolume;

    AlertDialog alertDialog;

    GPSTracker gps;
    Handler handler;
    EmergencyFunction run;

    public void stopMediaPlayer() {
        mMediaPlayer.stop();
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, savedVolume, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (EventsActivity.eventsList.isEmpty()) {
            MainActivity.loadEvents(getApplicationContext());
        }

        Intent intent = getIntent();
        final Event e = new Event().fromString(intent.getExtras().getString("event"));
        final int pos = intent.getExtras().getInt("timer");
        final int reqCode = intent.getExtras().getInt("code");

        // Check if specific event has been deleted.
        boolean flag = false;
        for (Event events : EventsActivity.eventsList) {
            if (events.id == e.id) {
                flag = true;
                break;
            }
        }
        if (flag == false) {
            finish();
            return;
        }

        // else continue.
        EventAlarmActivity activity = this;
        gps = new GPSTracker(EventAlarmActivity.this);
        handler = new Handler();
        run = new EmergencyFunction(activity, e, pos, reqCode, gps);

        playSound(this, getAlarmUri());

        alertDialog = new AlertDialog.Builder(EventAlarmActivity.this).create();
        alertDialog.setTitle("JustCheckingIn alarm");
        alertDialog
                .setMessage("Has something happened?\nDisable this event or your contact list:\""
                        + e.getList().get(pos).list.getName()
                        + "\" will receive the emergency message with your location.");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Disable",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMediaPlayer.stop();
                        // removes the thread from the queue
                        handler.removeCallbacks(run);

                        // if recurring event then program it for next week
                        if (e.getRepeat()) {
                            // calculate the time of the 1st timer of the event for next week
                            // (because it might be disabled on the 2nd timer)
                            long timeAlarm = calculateTime(e, 0, reqCode - e.id + 1);

                            Intent intentAlarm = new Intent(getApplicationContext(),
                                    EventAlarmActivity.class);
                            intentAlarm.putExtra("event", e.toXML());
                            intentAlarm.putExtra("code", reqCode);
                            intentAlarm.putExtra("timer", 0);

                            SimpleDateFormat formatter = new SimpleDateFormat(
                                    "dd/MM/yyyy HH:mm:ss", Locale.UK);
                            Toast.makeText(getApplicationContext(),
                                    "reqCode:" + reqCode + "->" + formatter.format(timeAlarm),
                                    Toast.LENGTH_SHORT).show();

                            PendingIntent pendingIntent = PendingIntent.getActivity(
                                    getApplicationContext(),
                                    reqCode, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

                            AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                            am.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendingIntent);
                        }
                        else {
                            // if not recurring event then set it to OFF on disable
                            EventsActivity.activeEvent--;
                            // find the actual Event in the list and update its value

                            for (Event events : EventsActivity.eventsList) {
                                if (events.id == e.id) {
                                    events.setStatus(false);
                                    break;
                                }
                            }
                        }
                        finish();
                    }
                });
        alertDialog.show();

        // Sets the thread to run in one minute.
        handler.postDelayed(run, 60000);
    }

    /**
     * Plays the retrieved ringtone.
     * 
     * @see #getAlarmUri
     */
    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            savedVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

            audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Acquires alarm ringtone. Secondary options the call ringtone or the notification sound.
     */
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        return alert;
    }

    /**
     * Functionality of the Event. It includes the messaging of the selected ContactList as well as
     * programming the next in sequence alarm of this Event or the current alarm for the next week
     * if it is a recurring Event.
     */
    class EmergencyFunction implements Runnable {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        EventAlarmActivity activity;
        Event event;
        int position;
        int code;
        GPSTracker gps;

        public EmergencyFunction(EventAlarmActivity a, Event e, int pos, int reqCode, GPSTracker gps) {
            this.activity = a;
            this.event = e;
            this.position = pos;
            this.code = reqCode;
            this.gps = gps;
        }

        @Override
        public void run() {
            try {
                ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
                ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

                PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(SENT), 0);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(DELIVERED), 0);

                // Send a message to each contact in the specified contact list
                for (int j = 0; j < event.getList().get(position).list.getList().size(); j++) {
                    sentIntents.add(sentPI);
                    deliveryIntents.add(deliveredPI);
                }

                double latitude = 0, longitude = 0;
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                } else {
                    // can't get location
                    // both GPS and Network are disabled
                }
                SmsManager smsManager = SmsManager.getDefault();
                String strSMSBody = String.format(Locale.ENGLISH,
                        "EMERGENCY! This is my location: http://maps.google.com/?q=%f,%f\n"
                                + MainActivity.emergencyMessage, latitude, longitude);
                List<String> messages = smsManager.divideMessage(strSMSBody);
                for (int i = 0; i < event.getList().get(position).list.getList().size(); i++) {
                    for (String str : messages) {
                        smsManager.sendTextMessage(event.getList().get(position).list.getList()
                                .get(i).getNumber(),
                                null, str, sentIntents.get(i), deliveryIntents.get(i));
                    }
                }

                // Create Notification
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        getApplicationContext());
                mBuilder.setSmallIcon(R.drawable.ic_launcher);
                mBuilder.setContentTitle("Just Checking In");
                mBuilder.setContentText("\"" + event.getName()
                        + "\" timer went off: Contacts in \""
                        + event.getList().get(position).list.getName() + "\"  were notified.");
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager
                        .notify(code, mBuilder.build());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // if the event has more timers
            if (position < event.getList().size() - 1) {
                // Since it was not disabled on time, schedule the next timer of the event
                long timeAlarm = calculateTime(event, position + 1, code - event.id + 1);

                // Create a new PendingIntent and add it to the AlarmManager
                Intent intentAlarm = new Intent(getApplicationContext(), EventAlarmActivity.class);
                intentAlarm.putExtra("event", event.toXML());
                intentAlarm.putExtra("code", code);
                intentAlarm.putExtra("timer", position + 1);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        code, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendingIntent);

            }
            // it was the last timer of this event
            else if (event.getRepeat()) {
                // if recurring event then program it for next week

                long timeAlarm = calculateTime(event, 0, code - event.id + 1);

                Intent intentAlarm = new Intent(getApplicationContext(), EventAlarmActivity.class);
                intentAlarm.putExtra("event", event.toXML());
                intentAlarm.putExtra("code", code);
                intentAlarm.putExtra("timer", 0);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        code, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendingIntent);
            }
            else {
                // if not recurring event then set it to OFF
                EventsActivity.activeEvent--;
                // find the actual Event in the list and update its value
                if (EventsActivity.eventsList.isEmpty()) {
                    MainActivity.loadEvents(getApplicationContext());
                }
                for (Event events : EventsActivity.eventsList) {
                    if (events.id == event.id) {
                        events.setStatus(false);
                        break;
                    }
                }
            }

            // Stop playing the alarm, close the pop-up window and terminate this activity.
            activity.stopMediaPlayer();
            activity.alertDialog.dismiss();
            activity.finish();
        }
    }

    /**
     * Calculates the time for an alarm given the required arguments.
     * 
     * @param event The selected Event.
     * @param timer The position of the selected timer for this Event.
     * @param day The day of the week for which the alarm is being calculated.
     */
    long calculateTime(Event event, int timer, int day) {
        Timer t = event.getList().get(timer);

        // Get current date and time
        Calendar timeAlarm = Calendar.getInstance();

        int hour = Integer.parseInt(t.hour);
        int minute = Integer.parseInt(t.minute);
        int weekOfYear = timeAlarm.get(Calendar.WEEK_OF_YEAR);
        int year = timeAlarm.get(Calendar.YEAR);

        // Today or next week in case the specified time has passed
        if (timeAlarm.get(Calendar.DAY_OF_WEEK) == day) {
            if (hour * 60 + minute <= timeAlarm.get(Calendar.HOUR_OF_DAY) * 60
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
        timeAlarm.get(Calendar.DAY_OF_WEEK); // known Android issue: required to make the following
                                             // set work
        timeAlarm.set(Calendar.DAY_OF_WEEK, day);

        // Set time
        timeAlarm.set(Calendar.HOUR_OF_DAY, hour);
        timeAlarm.set(Calendar.MINUTE, minute);

        return timeAlarm.getTimeInMillis();
    }
}
