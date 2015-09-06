
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The main screen of Just Checking In.
 * 
 * @author Georgios Aikaterinakis
 */
public class MainActivity extends Activity {
    final String SENT = "SMS_SENT";
    final String DELIVERED = "SMS_DELIVERED";

    /**
     * Identifier for the notification created when using the Emergency button
     */
    final int NOTIFICATION_EMERGENCY_BUTTON = 1;

    Handler handler;
    Runnable run;
    GPSTracker gps;

    private ImageButton eventsButton;
    private ImageButton emergencyButton;
    private ImageButton settingsButton;
    private ImageButton setfakecallButton;

    private Vibrator vib;
    private TextView activeEvents;

    static String emergencyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Splunk MINT setup
        // Mint.initAndStartSession(MainActivity.this, "77d0c26e");

        // Delete internal files
        /*
         * File dir = getFilesDir(); File file = new File(dir, "Events.data"); boolean deleted =
         * file.delete();
         */
        // file = new File(dir, "ContactLists.data"); deleted = file.delete();
        // file = new File(dir, "EmergencyContactList.data"); deleted = file.delete();

        // Retrieve existing ContactLists
        loadContactLists();

        // Retrieve Emergency Contact List
        loadEmergencyContactList();

        // Retrieve existing Events
        if (EventsActivity.eventsList.isEmpty()) {
            EventsActivity.activeEvent = 0;
            loadEvents(getApplicationContext());
        }

        eventsButton = (ImageButton) findViewById(R.id.button2);
        emergencyButton = (ImageButton) findViewById(R.id.button1);
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        setfakecallButton = (ImageButton) findViewById(R.id.button3);
        activeEvents = (TextView) findViewById(R.id.activeEvents);

        // Retrieve Emergency Message
        SharedPreferences saved = getPreferences(Context.MODE_PRIVATE);
        emergencyMessage = saved.getString("emergencyMessage",
                "I notified my emergency contacts using the JustCheckingIn app!");

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
                        double latitude = 0;
                        double longitude = 0;

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
                                smsManager.sendTextMessage(
                                        EmergencyContactListActivity.emergencyContactList.getList()
                                                .get(i).getNumber(),
                                        null, str, sentIntents.get(i), deliveryIntents.get(i));
                            }
                        }
                        ;
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
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Events
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
                startActivity(intent);
            }
        });

        // Emergency Button
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
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        setfakecallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetFakeCallActivity.class);
                startActivity(intent);
            }
        });

        activeEvents.setText(String.valueOf(EventsActivity.activeEvent));
        if (EventsActivity.activeEvent > 0) {
            activeEvents.setVisibility(View.VISIBLE);
        }
        else {
            activeEvents.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        activeEvents = (TextView) findViewById(R.id.activeEvents);
        activeEvents.setText(String.valueOf(EventsActivity.activeEvent));
        if (EventsActivity.activeEvent > 0) {
            activeEvents.setVisibility(View.VISIBLE);
        }
        else {
            activeEvents.setVisibility(View.INVISIBLE);
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

    /**
     * Loads the saved ContactList instances from file: "ContactLists.data" and adds them to the
     * ArrayList contactsList.
     */
    public void loadContactLists() {
        ContactListsActivity.contactsList.clear();

        /*
         * File file = new File("ContactLists.data"); if (!file.exists()) {
         * Toast.makeText(getApplicationContext(), "NOT_contacts", Toast.LENGTH_SHORT).show();
         * return; }
         */

        InputStream in_contactlists = null;
        try {
            in_contactlists = openFileInput("ContactLists.data");
        } catch (FileNotFoundException e) {
            return;
        }
        InputStreamReader inputreader = new InputStreamReader(in_contactlists);
        BufferedReader br = new BufferedReader(inputreader);

        String input = "";
        String line;
        try {
            while ((line = br.readLine()) != null) {
                input += line;
            }
        } catch (IOException e) {
            Log.e("MainActivity/loadContactLists()", "IOException while reading the file.");
            e.printStackTrace();
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
            Log.e("MainActivity/loadContactLists()", "IOException while closing the streams.");
            e.printStackTrace();
        }
    }

    /**
     * Loads the saved emergency ContactList from file: "EmergencyContactList.data" to ContactsList
     * emergencyContactList.
     */
    public void loadEmergencyContactList() {
        /*
         * File file = new File("EmergencyContactList.data"); if (!file.exists()) {
         * EmergencyContactListActivity.emergencyContactList = new ContactList();
         * Toast.makeText(getApplicationContext(), "NOT_emergency", Toast.LENGTH_LONG).show();
         * return; }
         */

        InputStream in_emergencycontactlist;
        try {
            in_emergencycontactlist = openFileInput("EmergencyContactList.data");
        } catch (FileNotFoundException e) {
            return;
        }
        InputStreamReader inputreader = new InputStreamReader(in_emergencycontactlist);
        BufferedReader br = new BufferedReader(inputreader);

        String input = "";
        String line;
        try {
            while ((line = br.readLine()) != null) {
                input += line;
            }
        } catch (IOException e) {
            Log.e("MainActivity/loadEmergencyContactList()", "IOException while reading the file.");
            e.printStackTrace();
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
            Log.e("MainActivity/loadEmergencyContactList()",
                    "IOException while closing the streams.");
            e.printStackTrace();
        }
    }

    /**
     * Loads the saved Event instances from file: "Events.data" and adds them to the ArrayList
     * eventsList.
     * 
     * @param context the context for which the function is called. This allows the use of the
     *            function from other Activities.
     */
    public static void loadEvents(Context context) {
        EventsActivity.eventsList.clear();

        /*
         * File file = new File("Events.data"); if(!file.exists()){
         * Toast.makeText(getApplicationContext(), "NO events", Toast.LENGTH_LONG).show(); return; }
         */

        InputStream in_events;
        try {
            in_events = context.openFileInput("Events.data");
        } catch (FileNotFoundException e) {
            return;
        }
        InputStreamReader inputreader = new InputStreamReader(in_events);
        BufferedReader br = new BufferedReader(inputreader);

        String input = "";
        String line;
        try {
            while ((line = br.readLine()) != null) {
                input += line;
            }
        } catch (IOException e) {
            Log.e("MainActivity/loadEvents()", "IOException while reading the file.");
            e.printStackTrace();
        }
        if (input.equalsIgnoreCase("")) return;

        for (String event : input.split("<Event>")) {
            if (!event.equalsIgnoreCase("")) {
                Event e = new Event().fromString(event);
                EventsActivity.eventsList.add(e);
                if (e.getStatus()) {
                    EventsActivity.activeEvent++;
                }
            }
        }

        try {
            in_events.close();
            inputreader.close();
            br.close();
        } catch (IOException e) {
            Log.e("MainActivity/loadEvents()", "IOException while closing the streams.");
            e.printStackTrace();
        }
    }
}
