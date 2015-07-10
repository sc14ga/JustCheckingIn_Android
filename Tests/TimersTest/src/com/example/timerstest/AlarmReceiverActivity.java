package com.example.timerstest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class AlarmReceiverActivity extends Activity {
    private MediaPlayer mMediaPlayer; 
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.alarm);
 
        
        
        Button stopAlarm = (Button) findViewById(R.id.stopAlarm);
        stopAlarm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMediaPlayer.stop();
                finish();
			}
        });
 
        playSound(this, getAlarmUri());
        
        Intent intent = getIntent();
        String cal2 = intent.getStringExtra("cal2");
        String cal3 = intent.getStringExtra("cal3");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH:mm:ss", Locale.US);
        try {
			cal.setTime(sdf.parse(cal2));
			Toast.makeText(this, cal2, Toast.LENGTH_LONG).show();
			
			intent = new Intent(AlarmReceiverActivity.this, AlarmReceiverActivity.class);
	        intent.putExtra("alarm_message", "Alarm message");
//	        "yyyy MMM dd HH:mm:ss"
	        
	        intent.putExtra("cal2", cal3);
	        intent.putExtra("cal3", "Danger");
	        Toast.makeText(AlarmReceiverActivity.this, "OK", Toast.LENGTH_LONG).show();
	        PendingIntent pendingIntent = PendingIntent.getActivity(AlarmReceiverActivity.this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	        AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
	        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
		} catch (ParseException e) {
			Toast.makeText(this, "Parse Exception", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
    }
 
    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }
 
    //Get an alarm sound. Try for an alarm. If none set, try notification, 
    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }
}