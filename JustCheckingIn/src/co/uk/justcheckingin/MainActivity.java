package co.uk.justcheckingin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*File dir = getFilesDir();
        File file = new File(dir, "Events.data");
		boolean deleted = file.delete();
		file = new File(dir, "ContactLists.data");
		deleted = file.delete();*/
		
		// Retreive existing ContactLists
		loadContactLists();
        
        // Retreive existing Events
        loadEvents();
        
        // Retreive Emergency Contact List
        loadEmergencyContactList();
        
		// Contacts        
		Button contactsButton = (Button) findViewById(R.id.button4);
		contactsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ContactListsActivity.class);
				startActivity(intent);
			}
		});
		
		// Events
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
		
		// Settings Button
		Button settingsButton = (Button) findViewById(R.id.settingsButton);
		settingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
				startActivity(intent);
			}
		});
		
		// Emergency Numbers Button
		Button numbersButton = (Button) findViewById(R.id.numbersButton);
		numbersButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), EmergencyNumbersActivity.class);
				startActivity(intent);
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
			InputStream in_events = openFileInput("Events.data");
			InputStreamReader inputreader = new InputStreamReader(in_events);
			BufferedReader br = new BufferedReader(inputreader);
	
			EventsActivity.eventsList.clear();
			
			String input = "";
/*			int c;
			while( (c=in_events.read()) != -1){
				input += (char) c;
			}*/
			String line;
			while((line = br.readLine()) != null){
				input += line;
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
				inputreader.close();
				br.close();
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
			InputStream in_contactlists = openFileInput("ContactLists.data");
			InputStreamReader inputreader = new InputStreamReader(in_contactlists);
			BufferedReader br = new BufferedReader(inputreader);
	
			ContactListsActivity.contactsList.clear();
			
			String input = "";
			/*int c;
			while( (c=in_contactlists.read()) != -1){
				input += (char) c;
			}*/
			String line;
			while((line = br.readLine()) != null){
				input += line;
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
				inputreader.close();
				br.close();
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
	
	public void loadEmergencyContactList(){
		try {
			InputStream in_emergencycontactlist = openFileInput("EmergencyContactList.data");
			InputStreamReader inputreader = new InputStreamReader(in_emergencycontactlist);
			BufferedReader br = new BufferedReader(inputreader);
			
			String input = "";
/*			int c;
			while( (c=in_emergencycontactlist.read()) != -1){
				input += (char) c;
			}*/
			String line;
			while((line = br.readLine()) != null){
				input += line;
			}
			if(input.equalsIgnoreCase("")) return;
			
			for (String cList : input.split("<ContactList>")) {
				if(!cList.equalsIgnoreCase("")){
					Log.d("DEBUG_EMERGENCY", cList);
					ContactList e = new ContactList();
					EmergencyContactListActivity.emergencyContactList = new ContactList();
					EmergencyContactListActivity.emergencyContactList = e.fromString(cList);
				}
			}
	        
			try {
				in_emergencycontactlist.close();
				inputreader.close();
				br.close();
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
