
package co.uk.justcheckingin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

/**
 * The settings screen.
 * 
 * @author Georgios Aikaterinakis
 */
public class SettingsActivity extends Activity {
    private Button emergencyContactsButton;
    private Button emergencyMessageButton;
    private Button numbersButton;
    private ImageButton backButton;
    private Switch gpsSwitch;

    private boolean state;

    private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        emergencyMessageButton = (Button) findViewById(R.id.buttonEmergencyMessage);
        emergencyMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
                alertDialog.setTitle("Edit Emergency Message");

                final EditText newMessage = new EditText(SettingsActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                newMessage.setLayoutParams(params);
                newMessage.setText(MainActivity.emergencyMessage);
                alertDialog.setView(newMessage);

                alertDialog.setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.emergencyMessage = newMessage.getText().toString();
                            }
                        });

                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });

        emergencyContactsButton = (Button) findViewById(R.id.buttonEmergencyContacts);
        emergencyContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,
                        EmergencyContactListActivity.class);
                startActivity(intent);
            }
        });

        gpsSwitch = (Switch) findViewById(R.id.switchGPS);
        gpsSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (state != isChecked) {
                    state = isChecked;
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }
        });

        gps = new GPSTracker(SettingsActivity.this);
        // Set the switch to show the GPS status
        state = gps.getGPSStatus();
        gpsSwitch.setChecked(gps.getGPSStatus());

        // Emergency Numbers Button
        numbersButton = (Button) findViewById(R.id.numbersButton);
        numbersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmergencyNumbersActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set the switch to show the GPS status
        state = gps.getGPSStatus();
        gpsSwitch.setChecked(gps.getGPSStatus());
    }
}
