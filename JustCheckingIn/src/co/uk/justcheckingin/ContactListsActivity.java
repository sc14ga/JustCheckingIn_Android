
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Shows the list of ContactLists. A Contact List is used in conjunction with an Event.
 * <p>
 * Leads to CreateContactListActivity and EditContactListActivity.
 * 
 * @author Georgios Aikaterinakis
 *
 */
public class ContactListsActivity extends Activity {
    static List<ContactList> contactsList = new ArrayList<ContactList>();
    
    private ContactListsAdapter adapter;
    private ListView list;
    private Button create;
    private ImageButton backButton;

    private FileOutputStream out = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact_lists);

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
        
        create = (Button) findViewById(R.id.create_button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateContactListActivity.class);
                startActivity(intent);
            }
        });

        list = (ListView) findViewById(R.id.listView1);
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

    /**
     * The list is updated when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();

        adapter = new ContactListsAdapter(this, R.layout.listview_contacts_row, contactsList);
        list.setAdapter(adapter);
    }

    /**
     * When the activity is paused, the ContactLists are saved into the "ContactLists.data" file.
     */
    @Override
    protected void onPause() {
        saveContactLists();

        super.onPause();
    }

    /**
     * Defines how the ContactLists are presented in the ListView.
     */
    public class ContactListsAdapter extends ArrayAdapter<ContactList> {
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

            if (row == null)
            {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ContactListHolder();
                holder.title = (TextView) row.findViewById(R.id.listName);

                row.setTag(holder);
            }
            else
            {
                holder = (ContactListHolder) row.getTag();
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

    /**
     * Function that populates the menu options for each item in the list.
     * It opens onItemClick.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listView1) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(contactsList.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    /**
     * Functionality for each menu option
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu);
        String menuItemName = menuItems[menuItemIndex];

        if (menuItemName.equalsIgnoreCase("Edit")) {
            Intent intent = new Intent(getApplicationContext(), EditContactListActivity.class);
            intent.putExtra("listPosition", String.valueOf(info.position));
            startActivity(intent);
        }
        else if (menuItemName.equalsIgnoreCase("Delete")) {
            contactsList.remove(info.position);
            list.setAdapter(adapter);
            return true;
        }

        return true;
    }

    /**
     * Function that saves the ContactLists in the file: "ContactLists.data".
     */
    public void saveContactLists() {
        try {
            File dir = getFilesDir();
            File file = new File(dir, "ContactLists.data");
            boolean deleted = file.delete();
            // Toast.makeText(getApplicationContext(), String.valueOf(deleted),
            // Toast.LENGTH_LONG).show();

            out = openFileOutput("ContactLists.data", Context.MODE_PRIVATE);

            for (ContactList cList : ContactListsActivity.contactsList) {
                String buffer = "<ContactList>" + cList.toXML();

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
