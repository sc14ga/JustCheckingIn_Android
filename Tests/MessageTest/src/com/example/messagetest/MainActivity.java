package com.example.messagetest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private BroadcastReceiver sendBroadcastReceiver;
	private BroadcastReceiver deliveryBroadcastReceiver;
	String SENT = "SMS_SENT";
	String DELIVERED = "SMS_DELIVERED";
	
	GPSTracker gps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        
		// Register broadcast receivers for SMS sent and delivered intents
		sendBroadcastReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context arg0, Intent arg1) {
	            switch (getResultCode()) {
	            case Activity.RESULT_OK:
	                Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	                Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_NO_SERVICE:
	                Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_NULL_PDU:
	                Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_RADIO_OFF:
	                Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	    };

	    deliveryBroadcastReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context arg0, Intent arg1) {
	            switch (getResultCode()) {

	            case Activity.RESULT_OK:
	                Toast.makeText(getBaseContext(), "SMS delivered",
	                        Toast.LENGTH_SHORT).show();
	                break;
	            case Activity.RESULT_CANCELED:
	                Toast.makeText(getBaseContext(), "SMS not delivered",
	                        Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	    };
	    
	    registerReceiver(sendBroadcastReceiver , new IntentFilter(SENT));
	    registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));
	    
		Button send = (Button) findViewById(R.id.button1);
		send.setOnClickListener(new View.OnClickListener() {    
		   @Override
		   public void onClick(View v) { 
			   try {
				   //SmsManager smsManager = SmsManager.getDefault();
				   //smsManager.sendTextMessage("5554", null, "test", null, null);
				   //Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
				   
				   ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
				   ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
				
				   PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
				   PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0);
				
				   for (int j = 0; j < 7; j++) {
					   sentIntents.add(sentPI);
					   deliveryIntents.add(deliveredPI);
				   }
				   
				   gps = new GPSTracker(MainActivity.this);
				   double latitude=0, longitude=0;
	               // check if GPS enabled     
	               if(gps.canGetLocation()){
	                     
	                    latitude = gps.getLatitude();
	                    longitude = gps.getLongitude();
	                     
	                    // \n is for new line
	                    //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
	                    
	                    //String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
	                    //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
	                    //startActivity(intent);
	                    
	                    // http://maps.google.com/?q=<lat>,<lng>
	                    // String link = String.format(Locale.ENGLISH, "http://maps.google.com/?q=<%f>,<%f>", latitude, longitude);
	               }else{
	                    // can't get location
	                    // GPS or Network is not enabled
	                    // Ask user to enable GPS/network in settings
	                    //gps.showSettingsAlert();
	               }
				   SmsManager smsManager = SmsManager.getDefault();
				   String strSMSBody = String.format(Locale.ENGLISH, "EMERGENCY! This is my location: http://maps.google.com/?q=%f,%f\nI notified my emergency contacts using the JustCheckingIn app!", latitude, longitude);
			       List<String> messages = smsManager.divideMessage(strSMSBody);
			       for (int i=0; i<7; i++) {
			    	   for(String str : messages){
			    		   smsManager.sendTextMessage(String.valueOf(5554+i), null, str, sentIntents.get(i), deliveryIntents.get(i));
			    	   }
			       }
			   } catch (Exception ex) {
				   Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
				   ex.printStackTrace();
			   } 
		   }
		});  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStop()
	{
	    unregisterReceiver(sendBroadcastReceiver);
	    unregisterReceiver(deliveryBroadcastReceiver);
	    super.onStop();
	}
}

