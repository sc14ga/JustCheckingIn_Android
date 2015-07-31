package co.uk.justcheckingin;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class IncomingCallActivity extends Activity {
    private ImageButton accept;
    private ImageButton decline;
    
    private String caller;
	private TextView mName;
    private TextView mPhoneNumber;
    
    private Vibrator v;
	long pattern[]={0,800,200,1200,300,2000,400};
	
	private MediaPlayer mMediaPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.call_card);
		
		Intent intent = getIntent();
		caller = intent.getExtras().getString("name");

		accept = (ImageButton) findViewById(R.id.acceptButton);
		decline = (ImageButton) findViewById(R.id.declineButton);
		
        mName = (TextView) findViewById(R.id.name);
        mPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        
        mName.setText(caller);
        mName.setTextColor(Color.WHITE);
        //mPhoneNumber.setText("07518924080");
        
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);		
		playSound(this, ringtone);
		
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(pattern,1);
        
        decline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				v.cancel();
				stopMediaPlayer();
				
				finish();
			}
		});
        
        accept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				v.cancel();
				stopMediaPlayer();
				
				Intent intent = new Intent(getApplicationContext(), InCallActivity.class);
				intent.putExtra("name", caller);
				startActivity(intent);
				finish();
			}
		});
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
