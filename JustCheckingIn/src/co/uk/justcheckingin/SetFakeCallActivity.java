
package co.uk.justcheckingin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class SetFakeCallActivity extends Activity {
    TimePicker time;
    EditText caller;
    Button set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_fake_call);

        time = (TimePicker) findViewById(R.id.timePicker1);
        time.setIs24HourView(true);

        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.MINUTE) + 30 >= 60)
            time.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY) + 1);
        else time.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
        time.setCurrentMinute(cal.get(Calendar.MINUTE) + 30);

        caller = (EditText) findViewById(R.id.editText1);
        set = (Button) findViewById(R.id.setButton);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IncomingCallActivity.class);
                if (caller.getText().toString().isEmpty())
                    intent.putExtra("name", "Unknown");
                else
                intent.putExtra("name", caller.getText().toString());

                Calendar alarmTime = Calendar.getInstance();
                alarmTime.set(Calendar.HOUR_OF_DAY, time.getCurrentHour());
                alarmTime.set(Calendar.MINUTE, time.getCurrentMinute());

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        20000, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
                finish();
            }
        });
    }
}
