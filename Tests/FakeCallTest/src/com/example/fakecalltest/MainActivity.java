package com.example.fakecalltest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_card);
		
		TextView upperTitle = (TextView) findViewById(R.id.upperTitle);
		TextView lowerTitle = (TextView) findViewById(R.id.lowerTitle);
	
		Intent inCallIntent = new Intent();
		inCallIntent.setClassName("com.android.phone", "com.android.phone.InCallScreen");
		startActivity(inCallIntent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
        // then it has handled the app icon touch event
	    return super.onOptionsItemSelected(item);
	}
}
