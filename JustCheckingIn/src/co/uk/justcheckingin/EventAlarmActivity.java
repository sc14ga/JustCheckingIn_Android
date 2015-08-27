
package co.uk.justcheckingin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
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
import android.telephony.SmsManager;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

        Intent intent = getIntent();
        final Event e = new Event().fromString(intent.getExtras().getString("event"));
        final int pos = intent.getExtras().getInt("timer");
        final int reqCode = intent.getExtras().getInt("code");
        
        EventAlarmActivity activity = this;
        gps = new GPSTracker(EventAlarmActivity.this);
        handler = new Handler();
        run = new EmergencyFunction(activity, e, pos, reqCode, gps);
        
        playSound(this, getAlarmUri());

        alertDialog = new AlertDialog.Builder(EventAlarmActivity.this).create();
        alertDialog.setTitle("JustCheckingIn alarm");
        alertDialog.setMessage("Has something happened?\nDisable this event or your contact list:\""+e.getList().get(pos).list.getName()+"\" will receive the emergency message with your location.");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Disable",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMediaPlayer.stop();
                        handler.removeCallbacks(run);
                        
                        if(e.getRepeat()){
                         // if recurring event then program it for next week
                            
                            long timeAlarm = calculateTime(e, 0, reqCode-e.id+1);
                            
                            Intent intentAlarm = new Intent(getApplicationContext(), EventAlarmActivity.class);
                            intentAlarm.putExtra("event", e.toXML());
                            intentAlarm.putExtra("code", reqCode);
                            intentAlarm.putExtra("timer", 0);
                            
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK);
                            Toast.makeText(getApplicationContext(), "reqCode:"+reqCode+"->"+formatter.format(timeAlarm), Toast.LENGTH_SHORT).show();
                             
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                    reqCode, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

                            AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                            am.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendingIntent);
                        }
                        else{
                            EventsActivity.activeEvent--;
                        }
                        
                        finish();
                    }
                });
        alertDialog.show();
        
        /////////////////////////////////// 60000
        handler.postDelayed(run, 10000);
    }

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

    // Get an alarm sound. If none set try ringtone, otherwise notification.
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
    
    class EmergencyFunction implements Runnable{
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

                PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0);

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
                                "EMERGENCY! This is my location: http://maps.google.com/?q=%f,%f\nI notified my selected contacts using the JustCheckingIn app!",
                                latitude, longitude);
                List<String> messages = smsManager.divideMessage(strSMSBody);
                for (int i = 0; i < event.getList().get(position).list.getList().size(); i++) {
                    for (String str : messages) {
//                        smsManager.sendTextMessage(contacts.getList().get(i).getNumber(),
//                                null, str, sentIntents.get(i), deliveryIntents.get(i));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            Toast.makeText(getApplicationContext(), "Your contact list: \""+event.getList().get(position).list.getName()+"\" has been notified!",Toast.LENGTH_LONG).show();
            
            if(position < event.getList().size()-1){
                long timeAlarm = calculateTime(event, position+1, code-event.id+1);
                
                // Create a new PendingIntent and add it to the AlarmManager
                Intent intentAlarm = new Intent(getApplicationContext(), EventAlarmActivity.class);
                intentAlarm.putExtra("event", event.toXML());
                intentAlarm.putExtra("code", code);
                intentAlarm.putExtra("timer", position+1);
                
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK);
                Toast.makeText(getApplicationContext(), "reqCode:"+code+"->"+formatter.format(timeAlarm), Toast.LENGTH_SHORT).show();
                 
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        code, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendingIntent);
                
            }
            else if(event.getRepeat()){
                // if recurring event then program it for next week
                
                long timeAlarm = calculateTime(event, 0, code-event.id+1);
                
                Intent intentAlarm = new Intent(getApplicationContext(), EventAlarmActivity.class);
                intentAlarm.putExtra("event", event.toXML());
                intentAlarm.putExtra("code", code);
                intentAlarm.putExtra("timer", 0);
                
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK);
                Toast.makeText(getApplicationContext(), "reqCode:"+code+"->"+formatter.format(timeAlarm), Toast.LENGTH_SHORT).show();
                 
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        code, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendingIntent);
            }
            else{
                EventsActivity.activeEvent--;
            }
            
            activity.stopMediaPlayer();
            activity.finish();
        }
    }
    
    long calculateTime(Event e, int pos, int day){
       Timer t = e.getList().get(pos);
       
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
       timeAlarm.get(Calendar.DAY_OF_WEEK);    // known Android issue: required to make the following set work
       timeAlarm.set(Calendar.DAY_OF_WEEK, day);

       // Set time
       timeAlarm.set(Calendar.HOUR_OF_DAY, hour);
       timeAlarm.set(Calendar.MINUTE, minute);

       return timeAlarm.getTimeInMillis();
   }
}
