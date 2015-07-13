package co.uk.justcheckingin;

import java.util.ArrayList;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Retreive existing ContactLists
		ArrayList<Contact> contactlist = new ArrayList<Contact>();
		ArrayList<Contact> contactlist2 = new ArrayList<Contact>();
		ArrayList<Contact> contactlist3 = new ArrayList<Contact>();
		Contact me = new Contact("me", "07518924080");
		contactlist.add(me);
		Contact you = new Contact("you", "07518924081");
		contactlist2.add(you);
		contactlist2.add(me);
		contactlist3.add(you);
        ContactListsActivity.contactsList.add(new ContactList("Friends", contactlist));
        ContactListsActivity.contactsList.add(new ContactList("Friends2", contactlist2));
        ContactListsActivity.contactsList.add(new ContactList("Friends3", contactlist3));
        
		// Contacts        
		Button contactsButton = (Button) findViewById(R.id.button4);
		contactsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ContactListsActivity.class);
				startActivity(intent);
			}
		});
		
		// Timers
		Button timersButton = (Button) findViewById(R.id.button2);
		timersButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), TimersActivity.class);
				startActivity(intent);
			}
		});
		
		// Emergency Button
		Button emergencyButton = (Button) findViewById(R.id.button1);
		emergencyButton.setOnClickListener(new View.OnClickListener() {    
		   @Override
		   public void onClick(View v) { 
			   try {
				   //SmsManager smsManager = SmsManager.getDefault();
				   //smsManager.sendTextMessage("+447518924080", null, "test", null, null);
				   Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
			   } catch (Exception ex) {
				   Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
				   ex.printStackTrace();
			   } 
		   }
		});  
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
