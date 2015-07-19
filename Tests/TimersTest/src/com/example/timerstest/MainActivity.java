package com.example.timerstest;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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
		
		Calendar cal = Calendar.getInstance();
		
		Button start = (Button) findViewById(R.id.button1);
		picker1 = (TimePicker) findViewById(R.id.timePicker1);
		picker1.setIs24HourView(true);
		picker2 = (TimePicker) findViewById(R.id.timePicker2);
		picker2.setIs24HourView(true);
		picker3 = (TimePicker) findViewById(R.id.timePicker3);
		picker3.setIs24HourView(true);
		
		if(cal.get(Calendar.MINUTE)+30 >= 60)
			picker1.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)+1);
		else
			picker1.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		picker1.setCurrentMinute(cal.get(Calendar.MINUTE)+30);
		if(cal.get(Calendar.MINUTE)+45 >= 60)
			picker2.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)+1);
		else
			picker2.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		
		picker2.setCurrentMinute(cal.get(Calendar.MINUTE)+45);
		picker3.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)+1);
		
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				
				int hour1 = picker1.getCurrentHour();
				int minute1 = picker1.getCurrentMinute();
				int hour2 = picker2.getCurrentHour();
				int minute2 = picker2.getCurrentMinute();
				int hour3 = picker3.getCurrentHour();
				int minute3 = picker3.getCurrentMinute();
				
				
				
				// Start
				Calendar cal1 = Calendar.getInstance();
				cal1.set(Calendar.HOUR_OF_DAY, hour1);
				cal1.set(Calendar.MINUTE, minute1);
				
				Calendar cal2 = Calendar.getInstance();
				cal2.set(Calendar.HOUR_OF_DAY, hour2);
				cal2.set(Calendar.MINUTE, minute2);
				
				Calendar cal3 = Calendar.getInstance();
				cal3.set(Calendar.HOUR_OF_DAY, hour3);
				cal3.set(Calendar.MINUTE, minute3);
							
				//Create a new PendingIntent and add it to the AlarmManager
		        Intent intent = new Intent(MainActivity.this, AlarmReceiverActivity.class);
		        intent.putExtra("alarm_message", "Alarm message");
		        intent.putExtra("cal2", cal2.get(Calendar.YEAR)+" "+cal2.get(Calendar.MONTH)+" "+cal2.get(Calendar.DAY_OF_MONTH)+" "+cal2.get(Calendar.HOUR_OF_DAY)+":"+cal2.get(Calendar.MINUTE)+":"+cal2.get(Calendar.SECOND));
		        intent.putExtra("cal3", cal3.get(Calendar.YEAR)+" "+cal3.get(Calendar.MONTH)+" "+cal3.get(Calendar.DAY_OF_MONTH)+" "+cal3.get(Calendar.HOUR_OF_DAY)+":"+cal3.get(Calendar.MINUTE)+":"+cal3.get(Calendar.SECOND));
		        Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_LONG).show();
		        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		        AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
		        am.set(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(), pendingIntent);
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
