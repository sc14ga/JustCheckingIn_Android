
package co.uk.justcheckingin;

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
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmergencyContactListActivity extends Activity {
    Button save, cancel;
    EditText title;
    ListView myContacts;

    ContactsAdapter adapter = new ContactsAdapter();

    FileOutputStream out;

    static ContactList emergencyContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_list);

        title = (EditText) findViewById(R.id.editText1);
        title.setText("Emergency Contacts");
        title.setEnabled(false);

        cancel = (Button) findViewById(R.id.button2);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter.mInflater = (LayoutInflater) EmergencyContactListActivity.this
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
            if (emergencyContactList != null) {
                for (Contact c : emergencyContactList.getList()) {
                    if (c.getName().equalsIgnoreCase(name)
                            && c.getNumber().equalsIgnoreCase(phoneNumber)) {
                        flag = true;
                        break;
                    }
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

                if (list.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "You have not selected any contacts",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    emergencyContactList = new ContactList(title.getText().toString(), list);
                    saveEmergencyContactList();

                    finish();
                }
            }
        });
    }

    public void saveEmergencyContactList() {
        try {
            File dir = getFilesDir();
            File file = new File(dir, "EmergencyContactList.data");
            boolean deleted = file.delete();
            // Toast.makeText(getApplicationContext(), String.valueOf(deleted),
            // Toast.LENGTH_LONG).show();

            out = openFileOutput("EmergencyContactList.data", Context.MODE_PRIVATE);

            String buffer = "<ContactList>" + emergencyContactList.toXML();

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
