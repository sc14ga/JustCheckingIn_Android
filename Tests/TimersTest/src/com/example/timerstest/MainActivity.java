package com.example.timerstest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {

	TimePicker picker1;
	TimePicker picker2;
	TimePicker picker3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button start = (Button) findViewById(R.id.button1);
		picker1 = (TimePicker) findViewById(R.id.timePicker1);
		picker2 = (TimePicker) findViewById(R.id.timePicker2);
		picker3 = (TimePicker) findViewById(R.id.timePicker3);
		start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int hour1 = picker1.getCurrentHour();
				int minute1 = picker1.getCurrentMinute();
				int hour2 = picker2.getCurrentHour();
				int minute2 = picker2.getCurrentMinute();
				int hour3 = picker3.getCurrentHour();
				int minute3 = picker3.getCurrentMinute();
				
				Toast.makeText(getApplicationContext(), "1:("+hour1+","+minute1+"), 2:("+hour2+","+minute2+"), 3:("+hour3+","+minute3+")", Toast.LENGTH_LONG).show();
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
