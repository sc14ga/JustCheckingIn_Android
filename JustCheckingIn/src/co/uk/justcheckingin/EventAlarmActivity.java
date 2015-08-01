
package co.uk.justcheckingin;

import android.app.Activity;
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

import java.io.IOException;

public class EventAlarmActivity extends Activity {
    private AudioManager audioManager;
    private MediaPlayer mMediaPlayer;
    private int savedVolume;

    AlertDialog alertDialog;

    static PendingIntent pendingService;
    static Intent intentService;

    static EventAlarmActivity popupActivity;

    public static EventAlarmActivity getInstance() {
        return popupActivity;
    }

    public void stopMediaPlayer() {
        mMediaPlayer.stop();
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, savedVolume, 0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        popupActivity = this;

        playSound(this, getAlarmUri());

        alertDialog = new AlertDialog.Builder(EventAlarmActivity.this).create();
        alertDialog.setTitle("JustCheckingIn alarm");
        alertDialog
                .setMessage("Has something happened?\nDisable this event or your 'ContactList' will receive a message with your location.");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Disable",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMediaPlayer.stop();
                        pendingService.cancel();
                        finish();
                    }
                });
        alertDialog.show();
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
}
