package co.uk.justcheckingin;

import co.uk.justcheckingin.ContactListsActivity.ContactListsAdapter;

import com.splunk.mint.Mint;

import java.io.BufferedReader;
import java.io.File;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Mint.initAndStartSession(MainActivity.this, "77d0c26e");
		
		/*File dir = getFilesDir();
        File file = new File(dir, "Events.data");
		boolean deleted = file.delete();
		file = new File(dir, "ContactLists.data");
		deleted = file.delete();
		file = new File(dir, "EmergencyContactList.data");
		deleted = file.delete();*/
		
		// Retreive existing ContactLists
		if(ContactListsActivity.contactsList.isEmpty())
			loadContactLists();
        
        // Retreive Emergency Contact List
		if(EmergencyContactListActivity.emergencyContactList == null)
			loadEmergencyContactList();
        
		// Contacts        
		ImageButton contactsButton = (ImageButton) findViewById(R.id.button4);
		contactsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ContactListsActivity.class);
				startActivity(intent);
			}
		});
		
		// Events
		ImageButton eventsButton = (ImageButton) findViewById(R.id.button2);
		eventsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
				startActivity(intent);
			}
		});
		
		// Emergency Button
		ImageButton emergencyButton = (ImageButton) findViewById(R.id.button1);
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
		ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
		settingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
				startActivity(intent);
			}
		});
		
		// Emergency Numbers Button
		ImageButton numbersButton = (ImageButton) findViewById(R.id.numbersButton);
		numbersButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), EmergencyNumbersActivity.class);
				startActivity(intent);
			}
		});
		
		ImageButton setfakecallButton = (ImageButton) findViewById(R.id.button3);
		setfakecallButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SetFakeCallActivity.class);
				startActivity(intent);
			}
		});
		
		// Banner visibility affected by active event
		RelativeLayout banner = (RelativeLayout) findViewById(R.id.relativeLayout1);
		LayoutParams params = (LayoutParams) banner.getLayoutParams();
		if(EventsActivity.activeEvent == 1){
			params.height = 120;
			banner.setLayoutParams(params);
		}
		else{
			params.height = 0;
			banner.setLayoutParams(params);
		}
		
		Button disable = (Button) findViewById(R.id.disableButton);
		disable.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartEventActivity.pendingIntent.cancel();
				EventAlarmActivity.pendingService.cancel();
				
				EventsActivity.activeEvent = 0;
				
				RelativeLayout banner = (RelativeLayout) findViewById(R.id.relativeLayout1);
				LayoutParams params = (LayoutParams) banner.getLayoutParams();
				params.height = 0;
				banner.setLayoutParams(params);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		RelativeLayout banner = (RelativeLayout) findViewById(R.id.relativeLayout1);
		LayoutParams params = (LayoutParams) banner.getLayoutParams();
		
		if(EventsActivity.activeEvent == 1){
			params.height = 120;
			banner.setLayoutParams(params);
		}
		else{
			params.height = 0;
			banner.setLayoutParams(params);
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
	
	
	
	public void loadContactLists(){
		try {
			InputStream in_contactlists = openFileInput("ContactLists.data");
			InputStreamReader inputreader = new InputStreamReader(in_contactlists);
			BufferedReader br = new BufferedReader(inputreader);
	
			ContactListsActivity.contactsList.clear();
			
			String input = "";
			String line;
			while((line = br.readLine()) != null){
				input += line;
			}
			if(input.equalsIgnoreCase("")) return;
			
			for (String cList : input.split("<ContactList>")) {
				if(!cList.equalsIgnoreCase("")){
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
			String line;
			while((line = br.readLine()) != null){
				input += line;
			}
			if(input.equalsIgnoreCase("")) return;
			
			for (String cList : input.split("<ContactList>")) {
				if(!cList.equalsIgnoreCase("")){
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
