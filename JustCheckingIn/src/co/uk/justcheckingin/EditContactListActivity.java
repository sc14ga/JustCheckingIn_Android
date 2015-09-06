
package co.uk.justcheckingin;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows editing an existing ContactList.
 * 
 * @author Georgios Aikaterinakis
 */
public class EditContactListActivity extends Activity {
    private Button save, cancel;
    private EditText title;
    private ListView myContacts;
    private ImageButton backButton;

    ContactsAdapter adapter = new ContactsAdapter();

    // selected ContactList and its position in contactsList from previous Activity
    private ContactList selected;
    private int listPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_contact_list);

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

        title = (EditText) findViewById(R.id.editText1);

        // Retrieve data from caller Activity
        Intent intent = getIntent();
        listPosition = Integer.parseInt(intent.getStringExtra("listPosition"));
        selected = ContactListsActivity.contactsList.get(listPosition);
        title.setText(selected.getName());

        cancel = (Button) findViewById(R.id.button2);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter.mInflater = (LayoutInflater) EditContactListActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ContentResolver cr = getContentResolver();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);
        while (phones.moveToNext())
        {
            String name = phones.getString(phones
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            adapter.namesList.add(name);
            adapter.numbersList.add(phoneNumber);

            Boolean flag = false;
            for (Contact c : selected.getList()) {
                if (c.getName().equalsIgnoreCase(name)
                        && c.getNumber().equalsIgnoreCase(phoneNumber)) {
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                adapter.boxes.add(false);
            }
            else {
                adapter.boxes.add(true);
            }

        }
        phones.close();

        myContacts = (ListView) findViewById(R.id.listView1);
        myContacts.setAdapter(adapter);

        save = (Button) findViewById(R.id.button1);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Contact> list = new ArrayList<Contact>();
                for (int i = 0; i < adapter.boxes.size(); i++) {
                    if (adapter.boxes.get(i).booleanValue() == true) {
                        Contact newContact = new Contact(adapter.namesList.get(i),
                                adapter.numbersList.get(i));
                        list.add(newContact);
                    }
                }

                Boolean flag = false;
                // Contact List name empty
                if (title.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter a name for the list",
                            Toast.LENGTH_LONG).show();
                    flag = true;
                } // No contacts selected
                else if (list.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "You have not selected any contacts",
                            Toast.LENGTH_LONG).show();
                    flag = true;
                }
                else { // Contact List name exists
                    String name = title.getText().toString();
                    for (ContactList cList : ContactListsActivity.contactsList) {
                        if (!(cList.equals(selected)) && (name.equalsIgnoreCase(cList.getName()))) {
                            Toast.makeText(getApplicationContext(),
                                    "An existing Contact List has the same name", Toast.LENGTH_LONG)
                                    .show();
                            flag = true;
                            break;
                        }
                    }
                }
                // No issues detected - ContactList creation
                if (flag == false) {
                    ContactList newList = new ContactList(title.getText().toString(), list);
                    ContactListsActivity.contactsList.set(listPosition, newList);
                    finish();
                }
            }
        });
    }
}
