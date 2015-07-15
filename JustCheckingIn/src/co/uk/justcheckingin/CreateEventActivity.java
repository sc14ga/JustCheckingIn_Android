package co.uk.justcheckingin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateEventActivity extends Activity {
	Button create, cancel;
	EditText title;
	
	TimePicker picker1, picker2, picker3;
	Spinner spinner1, spinner2, spinner3;
	
	ContactsAdapter adapter = new ContactsAdapter();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);
		
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		spinner3 = (Spinner) findViewById(R.id.spinner3);
		ArrayAdapter<ContactList> adapter = new ArrayAdapter<ContactList>(this, 
			    android.R.layout.simple_spinner_item, ContactListsActivity.contactsList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter);
		spinner2.setAdapter(adapter);
		spinner3.setAdapter(adapter);
		
		// Initial setup of TimePickers
		Calendar cal = Calendar.getInstance();
		
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
		// ---
		
		
		cancel = (Button) findViewById(R.id.cancel_button);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
		create = (Button) findViewById(R.id.create_button);
		create.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<Timer> list = new ArrayList<Timer>();
				
				Timer t1 = new Timer((ContactList) spinner1.getSelectedItem(), picker1.getCurrentHour(), picker1.getCurrentMinute());
				Timer t2 = new Timer((ContactList) spinner2.getSelectedItem(), picker2.getCurrentHour(), picker2.getCurrentMinute());
				Timer t3 = new Timer((ContactList) spinner3.getSelectedItem(), picker3.getCurrentHour(), picker3.getCurrentMinute());
				list.add(t1);
				list.add(t2);
				list.add(t3);
				
				
				Boolean flag = false;
					// Event name empty
				if(title.getText().toString().isEmpty()){
					Toast.makeText(getApplicationContext(), "Enter a name for the event", Toast.LENGTH_LONG).show();
					flag = true;
				}	
				else{	// Event name exists
					String name = title.getText().toString();
					for(Event e : EventsActivity.eventsList){
						if(name.equalsIgnoreCase(e.getName())){
							Toast.makeText(getApplicationContext(), "An existing Event has the same name", Toast.LENGTH_LONG).show();
							flag = true;
							break;
						}
					}
				}
				// CHECK TIMES
				
					// No issues detected - Event creation
				if(flag == false){
					Event newEvent = new Event(title.getText().toString(), list);
					EventsActivity.eventsList.add(newEvent);
					
					finish();
				}
			}
		});
		
		title = (EditText) findViewById(R.id.editText);
	}
}
