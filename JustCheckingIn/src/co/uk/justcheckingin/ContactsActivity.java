package co.uk.justcheckingin;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		
		ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        
        /*if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                  String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                  String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                  if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                     Cursor pCur = cr.query(
                               ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                               null,
                               ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                               new String[]{id}, null);
                     while (pCur.moveToNext()) {
                         String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                         Toast.makeText(NativeContentProvider.this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                     }
                    pCur.close();
                }
            }
        }*/
        
        String[] from = new String[]{
        		ContactsContract.Contacts.DISPLAY_NAME
        		//ContactsContract.CommonDataKinds.Phone.NUMBER
		};
		
		//the XML defined views which the data will be bound to
		int[] to = new int[] {
				R.id.name
				//R.id.number
		};
        
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.listview_contacts, cur, from, to, 0);
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(cursorAdapter);
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
