package co.uk.justcheckingin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EventsActivity extends Activity{
	static List<Event> eventsList = new ArrayList<Event>();
	ListView list;
	Button create, cancel, start;
	
	EventsAdapter adapter;
	
	FileOutputStream out = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		create = (Button) findViewById(R.id.create_button);
		cancel = (Button) findViewById(R.id.cancel_button);
		start = (Button) findViewById(R.id.start_button);
		
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
		adapter = new EventsAdapter(this, R.layout.listview_contacts_row, eventsList);
        list.setAdapter(adapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		adapter = new EventsAdapter(this, R.layout.listview_contacts_row, eventsList);
        list.setAdapter(adapter);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		saveEvents();
	}
	
	public class EventsAdapter extends ArrayAdapter<Event>{
	    Context context; 
	    int layoutResourceId;    
	    List<Event> data = null;
	    
	    public EventsAdapter(Context context, int layoutResourceId, List<Event> data) {
	        super(context, layoutResourceId, data);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	        this.data = data;
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
	        }
	        else
	        {
	            holder = (EventHolder)row.getTag();
	        }
	        
	        Event list = data.get(position);
	        holder.title.setText(list.getName());
	        
	        return row;
	    }
	    
	    class EventHolder
	    {
	        TextView title;
	    }
	}
	
	public void saveEvents(){
		try {
			File dir = getFilesDir();
	        File file = new File(dir, "Events.data");
			boolean deleted = file.delete();
			Toast.makeText(getApplicationContext(), String.valueOf(deleted), Toast.LENGTH_LONG).show();
			
			out = openFileOutput("Events.data", Context.MODE_PRIVATE);
		
			for(int i=0; i<EventsActivity.eventsList.size(); i++){
			//for(Event event : EventsActivity.eventsList){
				Event event = EventsActivity.eventsList.get(0);
				String buffer = "<Event>"+event.toString();
				
				try {
					out.write(buffer.getBytes(), 0, buffer.getBytes().length);
				} catch (IOException e) {
					e.printStackTrace();
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
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
}
