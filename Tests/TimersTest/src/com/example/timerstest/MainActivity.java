package com.example.timerstest;

import java.util.Calendar;

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
	
	int time1, time2, time3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button start = (Button) findViewById(R.id.button1);
		picker1 = (TimePicker) findViewById(R.id.timePicker1);
		picker1.setIs24HourView(true);
		picker2 = (TimePicker) findViewById(R.id.timePicker2);
		picker2.setIs24HourView(true);
		picker3 = (TimePicker) findViewById(R.id.timePicker3);
		picker3.setIs24HourView(true);
		picker1.setCurrentHour(0);
		picker1.setCurrentMinute(30);
		picker2.setCurrentHour(0);
		picker2.setCurrentMinute(45);
		picker3.setCurrentHour(1);
		picker3.setCurrentMinute(0);
		
		start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				
				int hour1 = picker1.getCurrentHour();
				int minute1 = picker1.getCurrentMinute();
				int hour2 = picker2.getCurrentHour();
				int minute2 = picker2.getCurrentMinute();
				int hour3 = picker3.getCurrentHour();
				int minute3 = picker3.getCurrentMinute();
				
				time1 = hour1*60+minute1;
				time2 = hour2*60+minute2;
				time3 = hour3*60+minute3;
				Toast.makeText(getApplicationContext(), "1:("+time1+" mins), 2:("+time2+" mins), 3:("+time3+"mins)", Toast.LENGTH_LONG).show();
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
