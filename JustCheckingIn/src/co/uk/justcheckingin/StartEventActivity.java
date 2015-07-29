package co.uk.justcheckingin;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class StartEventActivity extends Activity {
	static PendingIntent pendingIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		Event e = new Event().fromString(intent.getExtras().getString("event"));
		
		int hour1 = Integer.parseInt(e.getList().get(0).hour);
		int minute1 = Integer.parseInt(e.getList().get(0).minute);
//		int hour2 = picker2.getCurrentHour();
//		int minute2 = picker2.getCurrentMinute();
//		int hour3 = picker3.getCurrentHour();
//		int minute3 = picker3.getCurrentMinute();
		
		
		
		// Start
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.HOUR_OF_DAY, hour1);
		cal1.set(Calendar.MINUTE, minute1);
		Calendar cal1alarm = Calendar.getInstance();
		if(minute1 == 0){
			if(hour1 == 0)
				cal1alarm.set(Calendar.HOUR_OF_DAY, 23);
			else
				cal1alarm.set(Calendar.HOUR_OF_DAY, hour1-1);
			cal1alarm.set(Calendar.MINUTE, 59);
		}
		else{
			cal1alarm.set(Calendar.MINUTE, minute1-1);
			cal1alarm.set(Calendar.HOUR_OF_DAY, hour1);
		}
		
		
//		Calendar cal2 = Calendar.getInstance();
//		cal2.set(Calendar.HOUR_OF_DAY, hour2);
//		cal2.set(Calendar.MINUTE, minute2);
//		
//		Calendar cal3 = Calendar.getInstance();
//		cal3.set(Calendar.HOUR_OF_DAY, hour3);
//		cal3.set(Calendar.MINUTE, minute3);
					
		//Create a new PendingIntent and add it to the AlarmManager
        Intent intent_alarm = new Intent(StartEventActivity.this, EventAlarmActivity.class);
        EventAlarmActivity.intentService = new Intent(StartEventActivity.this, EventEmergencyService.class);
        
//        intent.putExtra("cal2", cal2.get(Calendar.YEAR)+" "+cal2.get(Calendar.MONTH)+" "+cal2.get(Calendar.DAY_OF_MONTH)+" "+cal2.get(Calendar.HOUR_OF_DAY)+":"+cal2.get(Calendar.MINUTE)+":"+cal2.get(Calendar.SECOND));
//        intent.putExtra("cal3", cal3.get(Calendar.YEAR)+" "+cal3.get(Calendar.MONTH)+" "+cal3.get(Calendar.DAY_OF_MONTH)+" "+cal3.get(Calendar.HOUR_OF_DAY)+":"+cal3.get(Calendar.MINUTE)+":"+cal3.get(Calendar.SECOND));
        
        Toast.makeText(StartEventActivity.this, "OK", Toast.LENGTH_SHORT).show();
        pendingIntent = PendingIntent.getActivity(StartEventActivity.this, 10001, intent_alarm, PendingIntent.FLAG_CANCEL_CURRENT);
        EventAlarmActivity.pendingService = PendingIntent.getService(StartEventActivity.this, 10002, EventAlarmActivity.intentService, PendingIntent.FLAG_CANCEL_CURRENT);
        
        AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal1alarm.getTimeInMillis(), pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(), EventAlarmActivity.pendingService);
        
        finish();
	}
}
