
package co.uk.justcheckingin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends Activity {
    static List<Event> eventsList = new ArrayList<Event>();
    ListView list;
    private Button start;
    private ImageButton backButton, create;

    EventsAdapter adapter;

    FileOutputStream out = null;

    static int activeEvent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_events);

        // Retreive existing Events
        if (EventsActivity.eventsList.isEmpty())
            loadEvents();

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

        create = (ImageButton) findViewById(R.id.create_button);
        start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getCount() != 0) {
                    Toast.makeText(getApplicationContext(),
                            ((Event) list.getItemAtPosition(adapter.selectedEvent)).getName(),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), StartEventActivity.class);
                    intent.putExtra("event",
                            ((Event) list.getItemAtPosition(adapter.selectedEvent)).toXML());
                    startActivity(intent);

                    activeEvent = 1;
                    finish();
                }
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
                holder.details = (RelativeLayout) row.findViewById(R.id.eventDetailsRelativeLayout);
                holder.timers = (LinearLayout) row.findViewById(R.id.timersLinearLayout);
                holder.position = position;

                // Updating the Events structure when the OFF/ON switch is changed
                holder.active.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Switch s = (Switch) buttonView;
                        int pos = (int) s.getTag();
                        eventsList.get(pos).setStatus(isChecked);
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

            // Populating LinearLayout with Timers/ContactLists rows for each Event
            holder.timers.removeAllViews();
            View child = ((Activity) context).getLayoutInflater().inflate(
                    R.layout.listview_event_timer_row, parent, false);
            TextView time = (TextView) child.findViewById(R.id.time);
            TextView contactList = (TextView) child.findViewById(R.id.contactList);
            for (int i = 0; i < event.getList().size(); i++) {
                time.setText(event.getList().get(i).hour + ":" + event.getList().get(i).minute);
                contactList.setText(event.getList().get(i).list.getName());
                holder.timers.addView(child);
            }

            row.setOnCreateContextMenuListener(null);
            return row;
        }
    }

    static class EventHolder
    {
        int position;
        TextView title;
        ImageButton expandButton;
        Switch active;
        RelativeLayout details;
        LinearLayout timers;
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

        if (menuItemName.equalsIgnoreCase("Edit")) {
            Intent intent = new Intent(getApplicationContext(), EditEventActivity.class);
            intent.putExtra("listPosition", String.valueOf(info.position));
            startActivity(intent);
        }
        else if (menuItemName.equalsIgnoreCase("Delete")) {
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
}
