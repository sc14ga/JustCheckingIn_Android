package com.example.messagetest;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button send = (Button) findViewById(R.id.button1);
		  
		send.setOnClickListener(new View.OnClickListener() {    
		   @Override
		   public void onClick(View v) { 
			   try {
				   SmsManager smsManager = SmsManager.getDefault();
				   //smsManager.sendTextMessage("+447518924080", null, "test", null, null);
				   Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
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
}
