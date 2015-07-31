package co.uk.justcheckingin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EventsActivity extends Activity{
	static List<Event> eventsList = new ArrayList<Event>();
	ListView list;
	Button create, cancel, start;
	
	EventsAdapter adapter;
	
	FileOutputStream out = null;
	
	static int activeEvent = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		// Retreive existing Events
		if(EventsActivity.eventsList.isEmpty())
			loadEvents();
				
		create = (Button) findViewById(R.id.create_button);
		cancel = (Button) findViewById(R.id.cancel_button);
		start = (Button) findViewById(R.id.start_button);
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(adapter.getCount() != 0){
					Toast.makeText(getApplicationContext(),((Event) list.getItemAtPosition(adapter.selectedEvent)).getName() , Toast.LENGTH_LONG).show();
					Intent intent = new Intent(getApplicationContext(), StartEventActivity.class);
					intent.putExtra("event", ((Event) list.getItemAtPosition(adapter.selectedEvent)).toXML());
					startActivity(intent);
					
					activeEvent = 1;
					finish();
				}
			}
		});
		
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
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
	
	public class EventsAdapter extends ArrayAdapter<Event>{
	    Context context; 
	    int layoutResourceId;    
	    List<Event> data = null;
	    int selectedEvent = 0;
	    
	    public EventsAdapter(Context context, int layoutResourceId, List<Event> data) {
	        super(context, layoutResourceId, data);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	        this.data = data;
	        this.selectedEvent = 0;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        EventHolder holder = null;
	        
	        
	        if(row == null)
	        {
	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            row = inflater.inflate(layoutResourceId, parent, false);
	            
	            holder = new EventHolder();
	            holder.title = (TextView)row.findViewById(R.id.listName);
	            
	            row.setTag(holder);
	            
	            // RadioButton r = (RadioButton)row.findViewById(R.id.radioButton1);
	        }
	        else
	        {
	            holder = (EventHolder)row.getTag();
	        }
	        
	        Event list = data.get(position);
	        holder.title.setText(list.getName());
	        
	        RadioButton r = (RadioButton)row.findViewById(R.id.radioButton1);
	        r.setChecked(position == selectedEvent);
            r.setTag(position);
            r.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                	selectedEvent = (Integer)view.getTag();
                    notifyDataSetChanged();
                }
            });
            
            row.setOnCreateContextMenuListener(null);
	        return row;
	    }
	    
	    class EventHolder
	    {
	        TextView title;
	    }
	}
	
	// Menu for ListView
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  if ((v.getId()==R.id.listView1) || (v.getId()==R.id.listName)){
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    menu.setHeaderTitle(eventsList.get(info.position).getName());
	    String[] menuItems = getResources().getStringArray(R.array.menu);
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	  }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
	  String[] menuItems = getResources().getStringArray(R.array.menu);
	  String menuItemName = menuItems[menuItemIndex];
	  String listItemName = eventsList.get(info.position).getName();

	  if(menuItemName.equalsIgnoreCase("Edit")){
		  Intent intent = new Intent(getApplicationContext(), EditEventActivity.class);
		  intent.putExtra("listPosition", String.valueOf(info.position));
		  startActivity(intent);
	  }
	  else if(menuItemName.equalsIgnoreCase("Delete")){
		  eventsList.remove(info.position);
		  list.setAdapter(adapter);
		  return true;
	  }
	  
	  return true;
	}
		
	public void saveEvents(){
		try {
			File dir = getFilesDir();
	        File file = new File(dir, "Events.data");
			boolean deleted = file.delete();
			//Toast.makeText(getApplicationContext(), String.valueOf(deleted), Toast.LENGTH_LONG).show();
			
			out = openFileOutput("Events.data", Context.MODE_PRIVATE);
			
			for(Event event : EventsActivity.eventsList){
				String buffer = "<Event>"+event.toXML();	//SPINNER ERROR?
				
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
	
	public void loadEvents(){
		try {
			InputStream in_events = openFileInput("Events.data");
			InputStreamReader inputreader = new InputStreamReader(in_events);
			BufferedReader br = new BufferedReader(inputreader);
	
			EventsActivity.eventsList.clear();
			
			String input = "";
			String line;
			while((line = br.readLine()) != null){
				input += line;
			}
			if(input.equalsIgnoreCase("")) return;
			
			for (String event : input.split("<Event>")) {
				if(!event.equalsIgnoreCase("")){
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
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
