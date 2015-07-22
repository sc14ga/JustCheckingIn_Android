package com.example.timerstest;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class AlarmMessageActivity extends Service {

	@Override
	public IBinder onBind(Intent intent) {		
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Intent kill = new Intent("Close JustCheckingIn Dialog");
	    intent.putExtra("action", "close");
	    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(kill);
		
		Toast.makeText(getApplicationContext(), "Your ContactList has been notified!", Toast.LENGTH_LONG).show();
		int rv = super.onStartCommand(intent, flags, startId);
		stopSelf();
		return rv;
	}
}
