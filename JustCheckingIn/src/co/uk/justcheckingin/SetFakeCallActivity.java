
package co.uk.justcheckingin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * The Screen where the user may set a fake call.
 * 
 * @author Georgios Aikaterinakis
 */
public class SetFakeCallActivity extends Activity {
    private TimePicker time;
    private EditText caller;
    private Button set;
    private ImageButton backButton;

    /**
     * A unique identifier to program the fake call.
     */
    private int FAKE_CALL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_fake_call);

        // Back Button
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Settings Button
        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

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
                if (caller.getText().toString().isEmpty()) {
                    intent.putExtra("name", "Unknown");
                }
                else {
                    intent.putExtra("name", caller.getText().toString());
                }

                Calendar alarmTime = Calendar.getInstance();
                alarmTime.set(Calendar.HOUR_OF_DAY, time.getCurrentHour());
                alarmTime.set(Calendar.MINUTE, time.getCurrentMinute());

                if (alarmTime.getTimeInMillis() >= Calendar.getInstance().getTimeInMillis()) {
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            getApplicationContext(),
                            FAKE_CALL, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Select a future time.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
