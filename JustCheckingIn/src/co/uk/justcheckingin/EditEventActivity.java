package co.uk.justcheckingin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditEventActivity extends Activity {
	Button save, cancel;
	EditText title;
	
	TimePicker picker1, picker2, picker3;
	Spinner spinner1, spinner2, spinner3;
	
	ContactsAdapter adapter = new ContactsAdapter();
	
	// selected Event and its position from previous Activity
	Event selected;
	int listPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event_onetimer);
		
		// Retreive data from the caller Activity
		Intent intent = getIntent();
		listPosition = Integer.parseInt(intent.getStringExtra("listPosition"));
		selected = EventsActivity.eventsList.get(listPosition);
		
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		//spinner2 = (Spinner) findViewById(R.id.spinner2);
		//spinner3 = (Spinner) findViewById(R.id.spinner3);
		ArrayAdapter<ContactList> adapter = new ArrayAdapter<ContactList>(this, 
			    android.R.layout.simple_spinner_item, ContactListsActivity.contactsList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter);
		//spinner2.setAdapter(adapter);
		//spinner3.setAdapter(adapter);
		
		// Initial setup of TimePickers
		Calendar cal = Calendar.getInstance();
		
		picker1 = (TimePicker) findViewById(R.id.timePicker1);
		picker1.setIs24HourView(true);
		//picker2 = (TimePicker) findViewById(R.id.timePicker2);
		//picker2.setIs24HourView(true);
		//picker3 = (TimePicker) findViewById(R.id.timePicker3);
		//picker3.setIs24HourView(true);
		
		if(cal.get(Calendar.MINUTE)+30 >= 60)
			picker1.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)+1);
		else
			picker1.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		picker1.setCurrentMinute(cal.get(Calendar.MINUTE)+30);
		/*if(cal.get(Calendar.MINUTE)+45 >= 60)
			picker2.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)+1);
		else
			picker2.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		
		picker2.setCurrentMinute(cal.get(Calendar.MINUTE)+45);
		picker3.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)+1);*/
		// ---
		
		picker1.setCurrentHour(Integer.parseInt(selected.getList().get(0).hour));
		picker1.setCurrentMinute(Integer.parseInt(selected.getList().get(0).minute));
		/*picker2.setCurrentHour(Integer.parseInt(selected.getList().get(1).hour));
		picker2.setCurrentMinute(Integer.parseInt(selected.getList().get(1).minute));
		picker3.setCurrentHour(Integer.parseInt(selected.getList().get(2).hour));
		picker3.setCurrentMinute(Integer.parseInt(selected.getList().get(2).minute));
		*/
		
		cancel = (Button) findViewById(R.id.cancel_button);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
		save = (Button) findViewById(R.id.create_button);
		save.setText("Save Changes");
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<Timer> list = new ArrayList<Timer>();
				
				Timer t1 = new Timer((ContactList) spinner1.getSelectedItem(), picker1.getCurrentHour().toString(), picker1.getCurrentMinute().toString());
//				Timer t2 = new Timer((ContactList) spinner2.getSelectedItem(), picker2.getCurrentHour().toString(), picker2.getCurrentMinute().toString());
//				Timer t3 = new Timer((ContactList) spinner3.getSelectedItem(), picker3.getCurrentHour().toString(), picker3.getCurrentMinute().toString());
				list.add(t1);
//				list.add(t2);
//				list.add(t3);
				
				
				Boolean flag = false;
					// Event name empty
				if(title.getText().toString().isEmpty()){
					Toast.makeText(getApplicationContext(), "Enter a name for the event", Toast.LENGTH_LONG).show();
					flag = true;
				}	
				else{	// Event name exists
					String name = title.getText().toString();
					for(Event e : EventsActivity.eventsList){
						if(!(e.equals(selected)) && (name.equalsIgnoreCase(e.getName()))){
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
					EventsActivity.eventsList.set(listPosition, newEvent);
					
					finish();
				}
			}
		});
		
		title = (EditText) findViewById(R.id.editText);
		title.setText(selected.getName());
	}
}
