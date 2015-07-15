package co.uk.justcheckingin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class MainActivity extends Activity {
	FileInputStream in_events = null;
	FileInputStream in_contactlists = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Retreive existing ContactLists
		loadContactLists();
		/*ArrayList<Contact> contactlist = new ArrayList<Contact>();
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
        ContactListsActivity.contactsList.add(new ContactList("Friends3", contactlist3));*/
        
        // Retreive existing Events
        loadEvents();
        
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
		Button eventsButton = (Button) findViewById(R.id.button2);
		eventsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
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
	
	public void loadEvents(){
		try {
			in_events = openFileInput("Events.data");
	
			EventsActivity.eventsList.clear();
			
			String input = "";
			int c;
			while( (c=in_events.read()) != -1){
				input += (char) c;
			}
			if(input.equalsIgnoreCase("")) return;
			
			for (String event : input.split("<Event>")) {
				if(!event.equalsIgnoreCase("")){
					Log.d("DEBUG", event);
					Event e = new Event();
					EventsActivity.eventsList.add(e.fromString(event));
				}
			}
	        
			try {
				in_events.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadContactLists(){
		try {
			in_contactlists = openFileInput("ContactLists.data");
	
			ContactListsActivity.contactsList.clear();
			
			String input = "";
			int c;
			while( (c=in_contactlists.read()) != -1){
				input += (char) c;
			}
			if(input.equalsIgnoreCase("")) return;
			
			for (String cList : input.split("<ContactList>")) {
				if(!cList.equalsIgnoreCase("")){
					Log.d("DEBUG", cList);
					ContactList list = new ContactList();
					ContactListsActivity.contactsList.add(list.fromString(cList));
				}
			}
	        
			try {
				in_contactlists.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
