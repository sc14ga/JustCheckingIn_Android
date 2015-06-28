package co.uk.justcheckingin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ContactsActivity extends Activity {
	List<String> namesList = new ArrayList<String>();
    List<String> numbersList = new ArrayList<String>();
    ContactsAdapter adapter ;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		
		ContentResolver cr = getContentResolver();
		Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
          String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
          String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); 
          namesList.add(name);
          numbersList.add(phoneNumber);
        }

        phones.close();
        
        ListView listView = (ListView) findViewById(R.id.listView1);
        adapter = new ContactsAdapter();
        listView.setAdapter(adapter);
        
        /*String[] from = new String[]{
        		ContactsContract.Contacts.DISPLAY_NAME
        		//ContactsContract.CommonDataKinds.Phone.NUMBER
		};
		
		//the XML defined views which the data will be bound to
		int[] to = new int[] {
				R.id.name
				//R.id.number
		};
        
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.listview_contacts, cur, from, to, 0);
        */
	}

	class ContactsAdapter extends BaseAdapter{  
		LayoutInflater mInflater;
		
        ContactsAdapter(){
            mInflater = (LayoutInflater) ContactsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return namesList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub

            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi=convertView;
            if(convertView==null)
            	vi = mInflater.inflate(R.layout.listview_contacts, null); 
             
            TextView name = (TextView) vi.findViewById(R.id.name);
            TextView number = (TextView) vi.findViewById(R.id.number);
            name.setText(namesList.get(position));
            number.setText(numbersList.get(position));

            return vi;
        }
    }   
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
