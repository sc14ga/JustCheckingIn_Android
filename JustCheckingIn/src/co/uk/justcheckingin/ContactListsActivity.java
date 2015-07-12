package co.uk.justcheckingin;

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

public class ContactListsActivity extends Activity {
	static List<ContactList> contactsList = new ArrayList<ContactList>();
	ContactListsAdapter adapter;
	ListView list;
	Button create;
	
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
        
		ArrayList<Contact> contactlist = new ArrayList<Contact>();
		ArrayList<Contact> contactlist2 = new ArrayList<Contact>();
		ArrayList<Contact> contactlist3 = new ArrayList<Contact>();
		Contact me = new Contact("me", "07518924080");
		contactlist.add(me);
		Contact you = new Contact("you", "07518924081");
		contactlist2.add(you);
		contactlist2.add(me);
		contactlist3.add(you);
        contactsList.add(new ContactList("Friends", contactlist));
        contactsList.add(new ContactList("Friends2", contactlist2));
        contactsList.add(new ContactList("Friends3", contactlist3));
        
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
	  
	  TextView text = (TextView)findViewById(R.id.footer);
	  text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
	  return true;
	}
}
