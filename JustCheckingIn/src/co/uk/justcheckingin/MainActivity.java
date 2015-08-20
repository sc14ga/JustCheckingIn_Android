
package co.uk.justcheckingin;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    int NOTIFICATION_EMERGENCY_BUTTON = 1;

    Handler handler;
    Runnable run;
    GPSTracker gps;

    private Vibrator vib;
    private TextView activeEvents;

    static String emergencyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Mint.initAndStartSession(MainActivity.this, "77d0c26e");

        /*
         * File dir = getFilesDir(); File file = new File(dir, "Events.data"); boolean deleted =
         * file.delete(); file = new File(dir, "ContactLists.data"); deleted = file.delete(); file =
         * new File(dir, "EmergencyContactList.data"); deleted = file.delete();
         */

        // Retreive existing ContactLists
        if (ContactListsActivity.contactsList.isEmpty()){
            loadContactLists();
        }

        // Retreive Emergency Contact List
        if (EmergencyContactListActivity.emergencyContactList == null){
            loadEmergencyContactList();
        }
        else if (EmergencyContactListActivity.emergencyContactList.getList().isEmpty()){
            loadEmergencyContactList();
        } 

        // Retreive Emergency Message
        SharedPreferences saved = getPreferences(Context.MODE_PRIVATE);
        emergencyMessage = saved.getString("emergencyMessage", "I notified my emergency contacts using the JustCheckingIn app!");
        
        // Emergency Button functionality
        run = new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
                    ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

                    PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                            new Intent(SENT), 0);
                    PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(),
                            0, new Intent(DELIVERED), 0);

                    // for each emergency contact list
                    for (int j = 0; j < EmergencyContactListActivity.emergencyContactList.getList()
                            .size(); j++) {
                        sentIntents.add(sentPI);
                        deliveryIntents.add(deliveredPI);
                    }

                    if (EmergencyContactListActivity.emergencyContactList.getList()
                            .size() != 0) {
                        gps = new GPSTracker(MainActivity.this);
                        double latitude = 0, longitude = 0;

                        if (gps.canGetLocation()) {
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                        }

                        SmsManager smsManager = SmsManager.getDefault();
                        String strSMSBody = String
                                .format(Locale.ENGLISH,
                                        "EMERGENCY! This is my location: http://maps.google.com/?q=%f,%f\n"
                                                + emergencyMessage, latitude, longitude);
                        List<String> messages = smsManager.divideMessage(strSMSBody);
                        for (int i = 0; i < EmergencyContactListActivity.emergencyContactList
                                .getList().size(); i++) {
                            for (String str : messages) {
                                // smsManager.sendTextMessage(
                                // EmergencyContactListActivity.emergencyContactList.getList()
                                // .get(i).getNumber(),
                                // null, str, sentIntents.get(i), deliveryIntents.get(i));
                            }
                        }
                        // Toast.makeText(getApplicationContext(),
                        // "Your emergency contact list has been notified!", Toast.LENGTH_LONG)
                        // .show();
                        // Create Notification
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                                getApplicationContext());
                        mBuilder.setSmallIcon(R.drawable.ic_launcher);
                        mBuilder.setContentTitle("Just Checking In");
                        mBuilder.setContentText("Emergency button was used: Emergency contacts were notified.");
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager
                                .notify(NOTIFICATION_EMERGENCY_BUTTON, mBuilder.build());
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "Try again: you have to select emergency contacts!",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        // ///////////////////////////////////////////////////////////

        // Contacts
        // ImageButton contactsButton = (ImageButton) findViewById(R.id.button4);
        // contactsButton.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // Intent intent = new Intent(getApplicationContext(), ContactListsActivity.class);
        // startActivity(intent);
        // }
        // });

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

        handler = new Handler();

        emergencyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vib.vibrate(5000);
                        handler.postDelayed(run, 5000);
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        vib.cancel();
                        handler.removeCallbacks(run);
                        break;

                    case MotionEvent.ACTION_UP:
                        vib.cancel();
                        handler.removeCallbacks(run);
                        break;

                }
                return true;
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

        ImageButton setfakecallButton = (ImageButton) findViewById(R.id.button3);
        setfakecallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetFakeCallActivity.class);
                startActivity(intent);
            }
        });

        activeEvents = (TextView) findViewById(R.id.activeEvents);
        activeEvents.setText(String.valueOf(EventsActivity.activeEvent));
        if (EventsActivity.activeEvent > 0) {
            activeEvents.setVisibility(View.VISIBLE);
        }

        // Button disable = (Button) findViewById(R.id.disableButton);
        // disable.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // StartEventActivity.pendingIntent.cancel();
        // EventAlarmActivity.pendingService.cancel();
        //
        // EventsActivity.activeEvent = 0;
        //
        // RelativeLayout banner = (RelativeLayout) findViewById(R.id.relativeLayout1);
        // LayoutParams params = (LayoutParams) banner.getLayoutParams();
        // params.height = 0;
        // banner.setLayoutParams(params);
        // }
        // });
    }

    @Override
    protected void onResume() {
        super.onResume();

        activeEvents = (TextView) findViewById(R.id.activeEvents);
        activeEvents.setText(String.valueOf(EventsActivity.activeEvent));
        if (EventsActivity.activeEvent > 0) {
            activeEvents.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        
        SharedPreferences saved = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saved.edit();
        editor.putString("emergencyMessage", emergencyMessage);
        editor.commit();
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

    public void loadContactLists() {
        try {
            ContactListsActivity.contactsList.clear();

            File file = new File("ContactLists.data");
            if (!file.exists()) {
                return;
            }

            InputStream in_contactlists = openFileInput("ContactLists.data");
            InputStreamReader inputreader = new InputStreamReader(in_contactlists);
            BufferedReader br = new BufferedReader(inputreader);

            String input = "";
            String line;
            while ((line = br.readLine()) != null) {
                input += line;
            }
            if (input.equalsIgnoreCase(""))
                return;

            for (String cList : input.split("<ContactList>")) {
                if (!cList.equalsIgnoreCase("")) {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadEmergencyContactList() {
        try {
            File file = new File("EmergencyContactList.data");
            if (!file.exists()) {
                EmergencyContactListActivity.emergencyContactList = new ContactList();
                return;
            }

            InputStream in_emergencycontactlist = openFileInput("EmergencyContactList.data");
            InputStreamReader inputreader = new InputStreamReader(in_emergencycontactlist);
            BufferedReader br = new BufferedReader(inputreader);

            String input = "";
            String line;
            while ((line = br.readLine()) != null) {
                input += line;
            }
            if (input.equalsIgnoreCase(""))
                return;

            for (String cList : input.split("<ContactList>")) {
                if (!cList.equalsIgnoreCase("")) {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
