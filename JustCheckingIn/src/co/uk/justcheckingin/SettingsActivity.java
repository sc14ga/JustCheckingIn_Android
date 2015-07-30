package co.uk.justcheckingin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingsActivity extends Activity {
	Button emergencyContactsButton;
	Switch gpsSwitch;
	
	boolean state;
	
	GPSTracker gps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		emergencyContactsButton = (Button) findViewById(R.id.buttonEmergencyContacts);
		emergencyContactsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this, EmergencyContactListActivity.class);
				startActivity(intent);
			}
		});
		
		gpsSwitch = (Switch) findViewById(R.id.switchGPS);
		gpsSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(state != isChecked){
					state = isChecked;
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                startActivity(intent);
				}
			}
		});
		
		gps = new GPSTracker(SettingsActivity.this);
		//gpsSwitch.setEnabled();
		state = gps.getGPSStatus();
		gpsSwitch.setChecked(gps.getGPSStatus());
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		state = gps.getGPSStatus();
		gpsSwitch.setChecked(gps.getGPSStatus());
	}
}
