
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

        // Retrieve existing Events
        if (EventsActivity.eventsList.isEmpty())
            loadEvents();
        
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
            {
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
                holder.active.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Switch s = (Switch) buttonView;
                        int pos = (int) s.getTag();
                        eventsList.get(pos).setStatus(isChecked);
                        if(holder.active.isChecked() == true){
                            holder.titleLayout.setBackgroundResource(R.drawable.event_row_enabled_background);
                            //holder.sun.toggle();
                            //holder.sun.toggle();
                        }
                        else{
                            holder.titleLayout.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                });

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
                
                holder.sun.setOnCheckedChangeListener(new DayOnCheckedChangeListener(eventsList.get(holder.position), "SUN", holder.repeat, holder.mon, holder.tue, holder.wed, holder.thu, holder.fri, holder.sat)); 
                holder.mon.setOnCheckedChangeListener(new DayOnCheckedChangeListener(eventsList.get(holder.position), "MON", holder.repeat, holder.sun, holder.tue, holder.wed, holder.thu, holder.fri, holder.sat));
                holder.tue.setOnCheckedChangeListener(new DayOnCheckedChangeListener(eventsList.get(holder.position), "TUE", holder.repeat, holder.sun, holder.mon, holder.wed, holder.thu, holder.fri, holder.sat));
                holder.wed.setOnCheckedChangeListener(new DayOnCheckedChangeListener(eventsList.get(holder.position), "WED", holder.repeat, holder.sun, holder.mon, holder.tue, holder.thu, holder.fri, holder.sat));
                holder.thu.setOnCheckedChangeListener(new DayOnCheckedChangeListener(eventsList.get(holder.position), "THU", holder.repeat, holder.sun, holder.mon, holder.tue, holder.wed, holder.fri, holder.sat));
                holder.fri.setOnCheckedChangeListener(new DayOnCheckedChangeListener(eventsList.get(holder.position), "FRI", holder.repeat, holder.sun, holder.mon, holder.tue, holder.wed, holder.thu, holder.sat));
                holder.sat.setOnCheckedChangeListener(new DayOnCheckedChangeListener(eventsList.get(holder.position), "SAT", holder.repeat, holder.sun, holder.mon, holder.tue, holder.wed, holder.thu, holder.fri));
                holder.repeat.setOnCheckedChangeListener(new RepeatOnCheckedChangeListener(eventsList.get(holder.position), holder.sun, holder.mon, holder.tue, holder.wed, holder.thu, holder.fri, holder.sat));
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
            holder.active.setChecked(event.getStatus());
            
            holder.repeat.setChecked(event.getRepeat());
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
        
        //  Listener for days' ToggleButtons
        private class DayOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
            Event event;
            CheckBox repeat;
            ToggleButton day1;
            ToggleButton day2;
            ToggleButton day3;
            ToggleButton day4; 
            ToggleButton day5; 
            ToggleButton day6;
            String id;  // Identifier of day to use the appropriate getter/setter
            
            public DayOnCheckedChangeListener(Event e, String id, CheckBox repeat, ToggleButton day1, ToggleButton day2, ToggleButton day3, ToggleButton day4, ToggleButton day5, ToggleButton day6) {
                this.event = e;
                this.id = id;
                this.repeat = repeat;
                this.day1 = day1;
                this.day2 = day2;
                this.day3 = day3;
                this.day4 = day4;
                this.day5 = day5;
                this.day6 = day6;
            }
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(id.equalsIgnoreCase("SUN")){
                        event.setSun(true);
                        enableEvent(event, Calendar.SUNDAY);
                    }
                    else if(id.equalsIgnoreCase("MON")){
                        event.setMon(true);
                        enableEvent(event, Calendar.MONDAY);
                    }
                    else if(id.equalsIgnoreCase("TUE")){
                        event.setTue(true);
                        enableEvent(event, Calendar.TUESDAY);
                    }
                    else if(id.equalsIgnoreCase("WED")){
                        event.setWed(true);
                        enableEvent(event, Calendar.WEDNESDAY);
                    }
                    else if(id.equalsIgnoreCase("THU")){
                        event.setThu(true);
                        enableEvent(event, Calendar.THURSDAY);
                    }
                    else if(id.equalsIgnoreCase("FRI")){
                        event.setFri(true);
                        enableEvent(event, Calendar.FRIDAY);
                    }
                    else if(id.equalsIgnoreCase("SAT")){
                        event.setSat(true);
                        enableEvent(event, Calendar.SATURDAY);
                    }
                } else {
                    if(id.equalsIgnoreCase("SUN")){
                        event.setSun(false);
                        
                        Intent intent = new Intent(getApplicationContext(), EventAlarmActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                            event.id-1+Calendar.SUNDAY, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                        am.cancel(pendingIntent);
                        pendingIntent.cancel();
                        
                        activeEvent--;
                    }
                    else if(id.equalsIgnoreCase("MON")){
                        event.setMon(false);
                        
                        Intent intent = new Intent(getApplicationContext(), EventAlarmActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                event.id-1+Calendar.MONDAY, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                        am.cancel(pendingIntent);
                        pendingIntent.cancel();
                        
                        activeEvent--;
                    }
                    else if(id.equalsIgnoreCase("TUE")){
                        event.setTue(false);
                        
                        Intent intent = new Intent(getApplicationContext(), EventAlarmActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                event.id-1+Calendar.TUESDAY, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        
                        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                        am.cancel(pendingIntent);
                        pendingIntent.cancel();
                        
                        activeEvent--;
                    }
                    else if(id.equalsIgnoreCase("WED")){
                        event.setWed(false);
                        Log.e("PI", "IN");
                        Intent intent = new Intent(getApplicationContext(), EventAlarmActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                event.id-1+Calendar.WEDNESDAY, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        
                        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                        am.cancel(pendingIntent);
                        pendingIntent.cancel();
                        
                        activeEvent--;
                    }
                    else if(id.equalsIgnoreCase("THU")){
                        event.setThu(false);
                        Log.e("PI", "IN");
                        Intent intent = new Intent(getApplicationContext(), EventAlarmActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                event.id-1+Calendar.THURSDAY, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        
                        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                        am.cancel(pendingIntent);
                        pendingIntent.cancel();
                        
                        activeEvent--;
                    }
                    else if(id.equalsIgnoreCase("FRI")){
                        event.setFri(false);
                        
                        Intent intent = new Intent(getApplicationContext(), EventAlarmActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                event.id-1+Calendar.FRIDAY, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        
                        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                        am.cancel(pendingIntent);
                        pendingIntent.cancel();
                        
                        activeEvent--;
                    }
                    else if(id.equalsIgnoreCase("SAT")){
                        event.setSat(false);
                        
                        Intent intent = new Intent(getApplicationContext(), EventAlarmActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                event.id-1+Calendar.FRIDAY, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        
                        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                        am.cancel(pendingIntent);
                        pendingIntent.cancel();
                        
                        activeEvent--;
                    }
                    // if all days' ToggleButtons are OFF then they are hidden and repeat gets unchecked
                    if(!day1.isChecked() && !day2.isChecked() && !day3.isChecked() && !day4.isChecked() && !day5.isChecked() && !day6.isChecked()){
                        repeat.setChecked(false);
                    }
                }
            }
        }
        
        // Listener for repeat CheckBox
        private class RepeatOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
            Event event;
            ToggleButton sun;
            ToggleButton mon;
            ToggleButton tue;
            ToggleButton wed; 
            ToggleButton thu; 
            ToggleButton fri;
            ToggleButton sat;
            
            public RepeatOnCheckedChangeListener(Event e, ToggleButton day1, ToggleButton day2, ToggleButton day3, ToggleButton day4, ToggleButton day5, ToggleButton day6, ToggleButton day7) {
                this.event = e;
                this.sun = day1;
                this.mon = day2;
                this.tue = day3;
                this.wed = day4;
                this.thu = day5;
                this.fri = day6;
                this.sat = day7;
            }
            
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    event.setRepeat(true);
                    sun.setChecked(true);
                    mon.setChecked(true);
                    tue.setChecked(true);
                    wed.setChecked(true);
                    thu.setChecked(true);
                    fri.setChecked(true);
                    sat.setChecked(true);
                    
                    // When repeat is changed to checked, the Days' ToggleButtons appear
                    sun.setVisibility(View.VISIBLE);
                    mon.setVisibility(View.VISIBLE);
                    tue.setVisibility(View.VISIBLE);
                    wed.setVisibility(View.VISIBLE);
                    thu.setVisibility(View.VISIBLE);
                    fri.setVisibility(View.VISIBLE);
                    sat.setVisibility(View.VISIBLE);
                } else {
                    event.setRepeat(false);
                    sun.setChecked(false);
                    mon.setChecked(false);
                    tue.setChecked(false);
                    wed.setChecked(false);
                    thu.setChecked(false);
                    fri.setChecked(false);
                    sat.setChecked(false);
                    
                    // Days' ToggleButtons are only visible when repeat is checked
                    sun.setVisibility(View.GONE);
                    mon.setVisibility(View.GONE);
                    tue.setVisibility(View.GONE);
                    wed.setVisibility(View.GONE);
                    thu.setVisibility(View.GONE);
                    fri.setVisibility(View.GONE);
                    sat.setVisibility(View.GONE);
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
            String[] menuItems = getResources().getStringArray(R.array.menu);
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
        String[] menuItems = getResources().getStringArray(R.array.menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = eventsList.get(info.position).getName();

        /*
        if (menuItemName.equalsIgnoreCase("Edit")) {
            Intent intent = new Intent(getApplicationContext(), EditEventActivity.class);
            intent.putExtra("listPosition", String.valueOf(info.position));
            startActivity(intent);
        }
        else*/ if (menuItemName.equalsIgnoreCase("Delete")) {
            eventsList.remove(info.position);
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

    public void loadEvents() {
        try {
            EventsActivity.eventsList.clear();

            /*
             * File file = new File("Events.data"); if(!file.exists()){
             * Toast.makeText(getApplicationContext(), "NO events", Toast.LENGTH_LONG).show();
             * return; }
             */

            InputStream in_events = openFileInput("Events.data");
            InputStreamReader inputreader = new InputStreamReader(in_events);
            BufferedReader br = new BufferedReader(inputreader);

            String input = "";
            String line;
            while ((line = br.readLine()) != null) {
                input += line;
            }
            if (input.equalsIgnoreCase("")) return;

            for (String event : input.split("<Event>")) {
                if (!event.equalsIgnoreCase("")) {
                    Event e = new Event();
                    EventsActivity.eventsList.add(e.fromString(event));
                }
            }

            try {
                in_events.close();
                inputreader.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        
        activeEvent++;
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
            if (hour * 60 + minute < timeAlarm.get(Calendar.HOUR_OF_DAY) * 60
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
