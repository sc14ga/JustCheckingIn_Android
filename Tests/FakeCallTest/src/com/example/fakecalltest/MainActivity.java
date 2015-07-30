package com.example.fakecalltest;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button accept;
	private Button decline;
    
	private TextView mName;
    private TextView mPhoneNumber;
    private TextView mLabel;
    private TextView mCallTypeLabel;
	
	private TextView mElapsedTime;
	
	private Vibrator v;
	long[] pattern={0,1000,400};
	
	private MediaPlayer mMediaPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.call_card);

        mName = (TextView) findViewById(R.id.name);
        mPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        mLabel = (TextView) findViewById(R.id.label);
        mCallTypeLabel = (TextView) findViewById(R.id.callTypeLabel);
        
        mElapsedTime = (TextView) findViewById(R.id.elapsedTime);
        
        //showImage(mPhoto, R.drawable.picture_unknown);
        
        mName.setText("George");
        mName.setTextColor(Color.WHITE);
        mPhoneNumber.setText("07518924080");
        mElapsedTime.setText("00:02");
        mLabel.setText("Label");
        mCallTypeLabel.setText("Call");
        
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);		
		playSound(this, ringtone);
		
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        boolean hasVibrator = v.hasVibrator();
        Toast.makeText(getApplicationContext(), String.valueOf(hasVibrator), Toast.LENGTH_LONG).show();
        v.vibrate(pattern,0);
        
        accept = (Button) findViewById(R.id.acceptButton);
        accept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				v.cancel();
				stopMediaPlayer();
				//Intent intent
			}
		});
        decline = (Button) findViewById(R.id.declineButton);
        decline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				v.cancel();
				stopMediaPlayer();
				finish();
			}
		});
        

        
        // pass the number of millseconds fro which you want to vibrate the phone here we
        // have passed 2000 so phone will vibrate for 2 seconds.

         //v.vibrate(2000);

	     // If you want to vibrate  in a pattern
	     
	     // 2nd argument is for repetition pass -1 if you do not want to repeat the Vibrate
	     
        
	}
	
	private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public void stopMediaPlayer(){
    	mMediaPlayer.stop();
    }
}
