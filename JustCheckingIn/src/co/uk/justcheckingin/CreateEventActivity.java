
package co.uk.justcheckingin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * The screen where the user may create an Event.
 * 
 * @author Georgios Aikaterinakis
 */
public class CreateEventActivity extends Activity {
    private Button create, addTimer;
    private EditText title;
    private TimePicker picker;
    private Spinner spinner;
    private ImageButton backButton;

    private ListView timersListView;
    private List<Timer> timersList = new ArrayList<Timer>();
    private TimerAdapter timersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_event_onetimer);

        /*
         * if(ContactListsActivity.contactsList.isEmpty()){ Intent intent = new
         * Intent(getApplicationContext(), CreateContactListActivity.class); startActivity(intent);
         * }
         */

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

        spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<ContactList> adapter = new ArrayAdapter<ContactList>(this,
                R.layout.spinner_contact_list_item, ContactListsActivity.contactsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Initial setup of TimePickers
        Calendar cal = Calendar.getInstance();

        picker = (TimePicker) findViewById(R.id.timePicker1);
        picker.setIs24HourView(true);
        if (cal.get(Calendar.MINUTE) + 30 >= 60) {
            picker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY) + 1);
        }
        else {
            picker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
        }
        picker.setCurrentMinute(cal.get(Calendar.MINUTE) + 30);
        // End of setup

        // Adding Timers for this Event
        addTimer = (Button) findViewById(R.id.addTimerButton);
        addTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer t = new Timer((ContactList) spinner.getSelectedItem(), String.format("%02d",
                        picker
                                .getCurrentHour()),
                        String.format("%02d", picker.getCurrentMinute()));
                timersList.add(t);
                timersAdapter.notifyDataSetChanged();
            }
        });

        create = (Button) findViewById(R.id.create_button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean flag = false;

                if (timersList.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Add at least one alarm for this event",
                            Toast.LENGTH_LONG).show();
                    flag = true;
                }
                // Event name empty
                else if (title.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter a name for the event",
                            Toast.LENGTH_LONG).show();
                    flag = true;
                }
                else { // Event name exists
                    String name = title.getText().toString();
                    for (Event e : EventsActivity.eventsList) {
                        if (name.equalsIgnoreCase(e.getName())) {
                            Toast.makeText(getApplicationContext(),
                                    "An existing Event has the same name", Toast.LENGTH_LONG)
                                    .show();
                            flag = true;
                            break;
                        }
                    }
                }

                // No issues detected - Event creation
                if (flag == false) {
                    Event newEvent = new Event(title.getText().toString(), timersList);
                    EventsActivity.eventsList.add(newEvent);

                    finish();
                }
            }
        });

        title = (EditText) findViewById(R.id.editText);

        timersListView = (ListView) findViewById(R.id.timersListView);
        timersAdapter = new TimerAdapter(this, R.layout.listview_event_timer_row, timersList);
        timersListView.setAdapter(timersAdapter);
    }

    /**
     * Specifies how the added Timers and ContactLists are presented during the creation of an
     * Event.
     */
    public class TimerAdapter extends ArrayAdapter<Timer> {
        Context context;
        int layoutResourceId;
        List<Timer> data = null;

        View row;

        public TimerAdapter(Context context, int layoutResourceId, List<Timer> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            row = convertView;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }

            TextView timer = (TextView) row.findViewById(R.id.time);
            TextView contactList = (TextView) row.findViewById(R.id.contactList);

            timer.setText(data.get(position).hour + ":" + data.get(position).minute);
            contactList.setText(data.get(position).list.getName());

            return row;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Timer getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
