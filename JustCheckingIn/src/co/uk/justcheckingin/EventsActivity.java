
package co.uk.justcheckingin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EventsActivity extends Activity {
    static List<Event> eventsList = new ArrayList<Event>();
    private ListView list;
    private ImageButton backButton, create;

    volatile static int counter;
    EventsAdapter adapter;

    FileOutputStream out = null;

    static int activeEvent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_events);

        /*// Retrieve existing Events
        if (EventsActivity.eventsList.isEmpty())
            loadEvents();*/
        
        SharedPreferences saved = getPreferences(Context.MODE_PRIVATE);
        counter = saved.getInt("counter", 10);

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

        // Contacts
        ImageButton contactsButton = (ImageButton) findViewById(R.id.button4);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListsActivity.class);
                startActivity(intent);
            }
        });

        create = (ImageButton) findViewById(R.id.create_button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                startActivity(intent);
            }
        });

        list = (ListView) findViewById(R.id.listView1);

        adapter = new EventsAdapter(this, R.layout.listview_events_row, eventsList);
        list.setAdapter(adapter);
        registerForContextMenu(list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new EventsAdapter(this, R.layout.listview_events_row, eventsList);
        list.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        saveEvents();

        super.onPause();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        
        SharedPreferences saved = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saved.edit();
        editor.putInt("counter", counter);
        editor.commit();
    }

    public class EventsAdapter extends ArrayAdapter<Event> {
        Context context;
        int layoutResourceId;
        List<Event> data = null;
        int selectedEvent = 0;

        View row;

        public EventsAdapter(Context context, int layoutResourceId, List<Event> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
            this.selectedEvent = 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            row = convertView;
            final EventHolder holder;

            if (row == null)
            {   // runs only at the first time to initialize the listview rows
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new EventHolder();
                holder.title = (TextView) row.findViewById(R.id.listName);
                holder.expandButton = (ImageButton) row.findViewById(R.id.expandingButton);
                holder.active = (Switch) row.findViewById(R.id.eventSwitch);
                holder.titleLayout = (RelativeLayout) row.findViewById(R.id.eventTitleHeader);
                holder.details = (RelativeLayout) row.findViewById(R.id.eventDetailsRelativeLayout);
                holder.timers = (LinearLayout) row.findViewById(R.id.timersLinearLayout);
                holder.repeat = (CheckBox) row.findViewById(R.id.repeatBox);
                holder.sun = (ToggleButton) row.findViewById(R.id.sunToggle);
                holder.mon = (ToggleButton) row.findViewById(R.id.monToggle);
                holder.tue = (ToggleButton) row.findViewById(R.id.tueToggle);
                holder.wed = (ToggleButton) row.findViewById(R.id.wedToggle);
                holder.thu = (ToggleButton) row.findViewById(R.id.thuToggle);
                holder.fri = (ToggleButton) row.findViewById(R.id.friToggle);
                holder.sat = (ToggleButton) row.findViewById(R.id.satToggle);
                
                // Initialize CheckBox repeat and days' ToggleButton
                holder.repeat.setChecked(eventsList.get(position).getRepeat());
                //holder.repeat.setChecked(false);
                if(holder.repeat.isChecked()){
                    holder.sun.setVisibility(View.VISIBLE);
                    holder.mon.setVisibility(View.VISIBLE);
                    holder.tue.setVisibility(View.VISIBLE);
                    holder.wed.setVisibility(View.VISIBLE);
                    holder.thu.setVisibility(View.VISIBLE);
                    holder.fri.setVisibility(View.VISIBLE);
                    holder.sat.setVisibility(View.VISIBLE);
                }
                
                holder.sun.setChecked(eventsList.get(position).getSun());
                holder.mon.setChecked(eventsList.get(position).getMon());
                holder.tue.setChecked(eventsList.get(position).getTue());
                holder.wed.setChecked(eventsList.get(position).getWed());
                holder.thu.setChecked(eventsList.get(position).getThu());
                holder.fri.setChecked(eventsList.get(position).getFri());
                holder.sun.setChecked(eventsList.get(position).getSat());
                
                holder.position = position;

                // Updating the Events structure when the OFF/ON switch is changed
                holder.active.setOnCheckedChangeListener(new ActiveOnCheckedChangeListener(holder));
                holder.active.setChecked(data.get(position).getStatus());
                if(data.get(position).getStatus()){
                    holder.titleLayout.setBackgroundResource(R.drawable.event_row_enabled_background);
                }
                
                // Show/Hide Event details when clicking the expand button and change its icon to
                // Expand or Collapse
                holder.expandButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageButton exp = (ImageButton) v;
                        if (holder.details.isShown()) {
                            holder.details.setVisibility(View.GONE);
                            holder.timers.setVisibility(View.GONE);
                            exp.setImageResource(R.drawable.expand_64_blue);
                        }
                        else {
                            holder.details.setVisibility(View.VISIBLE);
                            holder.timers.setVisibility(View.VISIBLE);
                            exp.setImageResource(R.drawable.collapse_64_gray);
                        }
                    }
                });
                
                holder.sun.setOnCheckedChangeListener(new DayOnCheckedChangeListener(Calendar.SUNDAY, holder)); 
                holder.mon.setOnCheckedChangeListener(new DayOnCheckedChangeListener(Calendar.MONDAY, holder));
                holder.tue.setOnCheckedChangeListener(new DayOnCheckedChangeListener(Calendar.TUESDAY, holder));
                holder.wed.setOnCheckedChangeListener(new DayOnCheckedChangeListener(Calendar.WEDNESDAY, holder));
                holder.thu.setOnCheckedChangeListener(new DayOnCheckedChangeListener(Calendar.THURSDAY, holder));
                holder.fri.setOnCheckedChangeListener(new DayOnCheckedChangeListener(Calendar.FRIDAY, holder));
                holder.sat.setOnCheckedChangeListener(new DayOnCheckedChangeListener(Calendar.SATURDAY, holder));
                holder.repeat.setOnCheckedChangeListener(new RepeatOnCheckedChangeListener(holder));
                row.setTag(holder);
            }
            else
            {
                holder = (EventHolder) row.getTag();
            }

            // Updating Event details
            Event event = data.get(position);
            holder.title.setText(event.getName());
            holder.active.setTag(position);
            holder.expandButton.setTag(position);
//            holder.active.setChecked(event.getStatus());
//            if(eventsList.get(position).getStatus()){
//                holder.titleLayout.setBackgroundResource(R.drawable.event_row_enabled_background);
//            }
            
            //holder.repeat.setChecked(event.getRepeat());
//            holder.sun.setChecked(event.getSun());
//            holder.mon.setChecked(event.getMon());
//            holder.tue.setChecked(event.getTue());
//            holder.wed.setChecked(event.getWed());
//            holder.thu.setChecked(event.getThu());
//            holder.fri.setChecked(event.getFri());
//            holder.sat.setChecked(event.getSat());
            
            
            // Populating LinearLayout with Timers/ContactLists rows for each Event
            holder.timers.removeAllViews();
            for (int i = 0; i < event.getList().size(); i++) {
                View child = ((Activity) context).getLayoutInflater().inflate(
                        R.layout.listview_event_timer_row, parent, false);
                TextView time = (TextView) child.findViewById(R.id.time);
                TextView contactList = (TextView) child.findViewById(R.id.contactList);
                time.setText(event.getList().get(i).hour + ":" + event.getList().get(i).minute);
                contactList.setText(event.getList().get(i).list.getName());
                holder.timers.addView(child);
            }

            row.setOnCreateContextMenuListener(null);
            return row;
        }
        
        // Listener for OFF/ON event switch
        private class ActiveOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
            EventHolder holder;
            
            public ActiveOnCheckedChangeListener(EventHolder holder) {
                this.holder = holder;
            }
            
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // if event is already active then do nothing
                    if(eventsList.get(holder.position).getStatus() == false){
                        eventsList.get(holder.position).setStatus(isChecked);
                    
                        // Event title row color changed
                        holder.titleLayout.setBackgroundResource(R.drawable.event_row_enabled_background);
                        if(holder.repeat.isChecked()){
                            // Check each ToggleButton and create the alarm
                            if(holder.sun.isChecked()){
                                enableEvent(eventsList.get(holder.position), Calendar.SUNDAY);
                            }
                            if(holder.mon.isChecked()){
                                enableEvent(eventsList.get(holder.position), Calendar.MONDAY);
                            }
                            if(holder.tue.isChecked()){
                                enableEvent(eventsList.get(holder.position), Calendar.TUESDAY);
                            }
                            if(holder.wed.isChecked()){
                                enableEvent(eventsList.get(holder.position), Calendar.WEDNESDAY);
                            }
                            if(holder.thu.isChecked()){
                                enableEvent(eventsList.get(holder.position), Calendar.THURSDAY);
                            }
                            if(holder.fri.isChecked()){
                                enableEvent(eventsList.get(holder.position), Calendar.FRIDAY);
                            }
                            if(holder.sat.isChecked()){
                                enableEvent(eventsList.get(holder.position), Calendar.SATURDAY);
                            }
                        }
                        else{
                            // Repeat is disabled Create the alarm for the next date
                            Calendar cal = Calendar.getInstance();
                            enableEvent(eventsList.get(holder.position), cal.get(Calendar.DAY_OF_WEEK));
                        }
                        
                        activeEvent++;
                    }
                } else {
                    eventsList.get(holder.position).setStatus(isChecked);
                    
                    // Event title row color
                    holder.titleLayout.setBackgroundColor(Color.TRANSPARENT);
                    
                    // Event has been disabled. Cancel all the alarms of this event. 
                    Intent intent = new Intent(getApplicationContext(), EventAlarmActivity.class);
                    AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                    
                    for(int i=0; i<7; i++){ // for every day's requestCode
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                eventsList.get(holder.position).id+i, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        am.cancel(pendingIntent);
                        pendingIntent.cancel();
                    }
                    
                    activeEvent--;
                }
            }
        }
        
        //  Listener for days' ToggleButtons
        private class DayOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
            Event event;
            int day;  // Identifier of day to use the appropriate getter/setter
            EventHolder holder;
            
            public DayOnCheckedChangeListener(int day, EventHolder holder) {
                this.event = eventsList.get(holder.position);
                this.day = day;
                this.holder = holder;
            }
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If Event switch is ON then create alarm
                    if(holder.active.isChecked()){
                        enableEvent(event, day);
                    }
                    
                    // in either case update the boolean which will be checked when the event is turned to ON
                    if(day == Calendar.SUNDAY){
                        event.setSun(true);
                    }
                    else if(day == Calendar.MONDAY){
                        event.setMon(true);
                    }
                    else if(day == Calendar.TUESDAY){
                        event.setTue(true);
                    }
                    else if(day == Calendar.WEDNESDAY){
                        event.setWed(true);
                    }
                    else if(day == Calendar.THURSDAY){
                        event.setThu(true);
                    }
                    else if(day == Calendar.FRIDAY){
                        event.setFri(true);
                    }
                    else if(day == Calendar.SATURDAY){
                        event.setSat(true);
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), EventAlarmActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        event.id-1+day, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                    am.cancel(pendingIntent);
                    pendingIntent.cancel();
                    
                    Log.e("delete alarm", String.valueOf(event.id-1+day));
                    
                    if(day == Calendar.SUNDAY){
                        event.setSun(false);
                    }
                    else if(day == Calendar.MONDAY){
                        event.setMon(false);
                    }
                    else if(day == Calendar.TUESDAY){
                        event.setTue(false);
                    }
                    else if(day == Calendar.WEDNESDAY){
                        event.setWed(false);
                    }
                    else if(day == Calendar.THURSDAY){
                        event.setThu(false);
                    }
                    else if(day == Calendar.FRIDAY){
                        event.setFri(false);
                    }
                    else if(day == Calendar.SATURDAY){
                        event.setSat(false);
                    }
                    // if all days' ToggleButtons are OFF then they are hidden and repeat gets unchecked
                    if(!holder.sun.isChecked() && !holder.mon.isChecked() && !holder.tue.isChecked() && !holder.wed.isChecked() && !holder.thu.isChecked() && !holder.fri.isChecked() && !holder.sat.isChecked()){
                        holder.repeat.setChecked(false);
                    }
                }
            }
        }
        
        // Listener for repeat CheckBox
        private class RepeatOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
            EventHolder holder;
            
            public RepeatOnCheckedChangeListener(EventHolder holder) {
                this.holder = holder;
            }
            
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    eventsList.get(holder.position).setRepeat(true);
                    holder.sun.setChecked(true);
                    holder.mon.setChecked(true);
                    holder.tue.setChecked(true);
                    holder.wed.setChecked(true);
                    holder.thu.setChecked(true);
                    holder.fri.setChecked(true);
                    holder.sat.setChecked(true);
                    
                    // When repeat is changed to checked, the Days' ToggleButtons appear
                    holder.sun.setVisibility(View.VISIBLE);
                    holder.mon.setVisibility(View.VISIBLE);
                    holder.tue.setVisibility(View.VISIBLE);
                    holder.wed.setVisibility(View.VISIBLE);
                    holder.thu.setVisibility(View.VISIBLE);
                    holder.fri.setVisibility(View.VISIBLE);
                    holder.sat.setVisibility(View.VISIBLE);
                } else {
                    eventsList.get(holder.position).setRepeat(false);
                    holder.sun.setChecked(false);
                    holder.mon.setChecked(false);
                    holder.tue.setChecked(false);
                    holder.wed.setChecked(false);
                    holder.thu.setChecked(false);
                    holder.fri.setChecked(false);
                    holder.sat.setChecked(false);
                    
                    // Days' ToggleButtons are only visible when repeat is checked
                    holder.sun.setVisibility(View.GONE);
                    holder.mon.setVisibility(View.GONE);
                    holder.tue.setVisibility(View.GONE);
                    holder.wed.setVisibility(View.GONE);
                    holder.thu.setVisibility(View.GONE);
                    holder.fri.setVisibility(View.GONE);
                    holder.sat.setVisibility(View.GONE);
                }
            }
        }
    }

    static class EventHolder
    {
        int position;
        TextView title;
        ImageButton expandButton;
        Switch active;
        RelativeLayout titleLayout;
        RelativeLayout details;
        LinearLayout timers;
        CheckBox repeat;
        ToggleButton sun, mon, tue, wed, thu, fri, sat;
    }

    // Menu for ListView
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if ((v.getId() == R.id.listView1) || (v.getId() == R.id.listName)) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(eventsList.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.menu_events);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu_events);
        String menuItemName = menuItems[menuItemIndex];

        // Delete and clean up
        if (menuItemName.equalsIgnoreCase("Delete")) {
            if(eventsList.get(info.position).getStatus()){
                activeEvent--;
            }
            eventsList.remove(info.position);
            adapter = new EventsAdapter(this, R.layout.listview_events_row, eventsList);
            list.setAdapter(adapter);
            return true;
        }

        return true;
    }

    public void saveEvents() {
        try {
            File dir = getFilesDir();
            File file = new File(dir, "Events.data");
            boolean deleted = file.delete();
            // Toast.makeText(getApplicationContext(), String.valueOf(deleted),
            // Toast.LENGTH_LONG).show();

            out = openFileOutput("Events.data", Context.MODE_PRIVATE);

            for (Event event : EventsActivity.eventsList) {
                String buffer = "<Event>" + event.toXML();

                try {
                    out.write(buffer.getBytes(), 0, buffer.getBytes().length);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            try {
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    void enableEvent(Event e, int day){
        long timeAlarm = calculateTime(e, 0, day);
        //for (Timer t : e.getList()) {
        
        // Create a new PendingIntent and add it to the AlarmManager
        Intent intentAlarm = new Intent(getApplicationContext(), EventAlarmActivity.class);
        intentAlarm.putExtra("event", e.toXML());
        
        // e.g. id=10 -> alarm code for Sunday:10 (10-1+1), Monday:11,...
        int requestCode = e.id-1+day;
        intentAlarm.putExtra("code", requestCode);
        intentAlarm.putExtra("timer", 0);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK);
        Toast.makeText(getApplicationContext(), "reqCode:"+requestCode+"->"+formatter.format(timeAlarm), Toast.LENGTH_SHORT).show();
         
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                requestCode, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendingIntent);
    }
    
    long calculateTime(Event e, int pos, int day){
        Timer t = e.getList().get(pos);
        
        // Get current date and time
        Calendar timeAlarm = Calendar.getInstance();

        int hour = Integer.parseInt(t.hour);
        int minute = Integer.parseInt(t.minute);
        int weekOfYear = timeAlarm.get(Calendar.WEEK_OF_YEAR);
        int year = timeAlarm.get(Calendar.YEAR);

        // Today or next week in case the specified time has passed
        if (timeAlarm.get(Calendar.DAY_OF_WEEK) == day) {
            if (hour * 60 + minute <= timeAlarm.get(Calendar.HOUR_OF_DAY) * 60
                    + timeAlarm.get(Calendar.MINUTE)) {
                // Next week
                if (timeAlarm.get(Calendar.WEEK_OF_YEAR) == 52) {
                    // if last week of year go to the first week of next year
                    weekOfYear = 1;
                    year = timeAlarm.get(Calendar.YEAR) + 1;
                }
                else {
                    weekOfYear = timeAlarm.get(Calendar.WEEK_OF_YEAR) + 1;
                }
            }
        }
        else if (timeAlarm.get(Calendar.DAY_OF_WEEK) > day) {
            // Next week
            if (timeAlarm.get(Calendar.WEEK_OF_YEAR) == 52) {
                // if last week of year go to the first week of next year
                weekOfYear = 1;
                year = timeAlarm.get(Calendar.YEAR) + 1;
            }
            else {
                weekOfYear = timeAlarm.get(Calendar.WEEK_OF_YEAR) + 1;
            }
        }

        // Set date
        timeAlarm.set(Calendar.YEAR, year);
        timeAlarm.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        timeAlarm.get(Calendar.DAY_OF_WEEK);    // known Android issue: required to make the following set work
        timeAlarm.set(Calendar.DAY_OF_WEEK, day);

        // Set time
        timeAlarm.set(Calendar.HOUR_OF_DAY, hour);
        timeAlarm.set(Calendar.MINUTE, minute);

        return timeAlarm.getTimeInMillis();
    }
}
