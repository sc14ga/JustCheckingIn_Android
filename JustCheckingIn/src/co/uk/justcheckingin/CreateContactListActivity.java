package co.uk.justcheckingin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CreateContactListActivity extends Activity {
	Button create, cancel;
	EditText title;
	ListView myContacts;
	
	ContactsAdapter adapter = new ContactsAdapter();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_contact_list);
		
		cancel = (Button) findViewById(R.id.button2);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		adapter.mInflater = (LayoutInflater) CreateContactListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		ContentResolver cr = getContentResolver();
		Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
          String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
          String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); 
          adapter.namesList.add(name);
          adapter.numbersList.add(phoneNumber);
          adapter.boxes.add(false);
        }
        phones.close();
        
        myContacts = (ListView) findViewById(R.id.listView1);
        myContacts.setAdapter(adapter);
        
		create = (Button) findViewById(R.id.button1);
		create.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<Contact> list = new ArrayList<Contact>();
				for(int i = 0; i < adapter.boxes.size(); i++){
					if(adapter.boxes.get(i).booleanValue() == true){
						Contact newContact = new Contact(adapter.namesList.get(i), adapter.numbersList.get(i));
						list.add(newContact);
					}
				}
				
				if(!(title.getText().toString().isEmpty()) && !(list.isEmpty())){
					ContactList newList = new ContactList(title.getText().toString(), list);
					ContactListsActivity.contactsList.add(newList);
					finish();
				}
			}
		});
		
		title = (EditText) findViewById(R.id.editText1);
	}
}
