package co.uk.justcheckingin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactListsActivity extends Activity {
	static List<ContactList> contactsList = new ArrayList<ContactList>();
	ContactListsAdapter adapter;
	ListView list;
	Button create;
	
	FileOutputStream out = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_lists);
		
		create = (Button) findViewById(R.id.create_button);
		create.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), CreateContactListActivity.class);
				startActivity(intent);
			}
		});
		
		list = (ListView) findViewById(R.id.listView1);
		/*list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ContactList item = adapter.getItem(position);
			}


			});*/
        
		
        
        adapter = new ContactListsAdapter(this, R.layout.listview_contacts_row, contactsList);
        
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				registerForContextMenu(list);
                openContextMenu(view);
			}
        });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		adapter = new ContactListsAdapter(this, R.layout.listview_contacts_row, contactsList);
        list.setAdapter(adapter);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		saveContactLists();
	}
	
	public class ContactListsAdapter extends ArrayAdapter<ContactList>{
	    Context context; 
	    int layoutResourceId;    
	    List<ContactList> data = null;
	    
	    public ContactListsAdapter(Context context, int layoutResourceId, List<ContactList> data) {
	        super(context, layoutResourceId, data);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	        this.data = data;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        ContactListHolder holder = null;
	        
	        if(row == null)
	        {
	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            row = inflater.inflate(layoutResourceId, parent, false);
	            
	            holder = new ContactListHolder();
	            holder.title = (TextView)row.findViewById(R.id.listName);
	            
	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (ContactListHolder)row.getTag();
	        }
	        
	        ContactList list = data.get(position);
	        holder.title.setText(list.getName());
	        
	        return row;
	    }
	    
	    class ContactListHolder
	    {
	        TextView title;
	    }
	}
	
	
	// Menu for ListView
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  if (v.getId()==R.id.listView1) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    menu.setHeaderTitle(contactsList.get(info.position).getName());
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
	  String listItemName = contactsList.get(info.position).getName();

	  if(menuItemName.equalsIgnoreCase("Edit")){
		  Intent intent = new Intent(getApplicationContext(), EditContactListActivity.class);
		  intent.putExtra("listPosition", String.valueOf(info.position));
		  startActivity(intent);
	  }
	  else if(menuItemName.equalsIgnoreCase("Delete")){
		  contactsList.remove(info.position);
		  list.setAdapter(adapter);
		  return true;
	  }
	  
	  return true;
	}
	
	public void saveContactLists(){
		try {
			File dir = getFilesDir();
	        File file = new File(dir, "ContactLists.data");
			boolean deleted = file.delete();
			//Toast.makeText(getApplicationContext(), String.valueOf(deleted), Toast.LENGTH_LONG).show();
			
			out = openFileOutput("ContactLists.data", Context.MODE_PRIVATE);
		
			for(ContactList cList : ContactListsActivity.contactsList){
				String buffer = "<ContactList>"+cList.toXML();
				
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
}
