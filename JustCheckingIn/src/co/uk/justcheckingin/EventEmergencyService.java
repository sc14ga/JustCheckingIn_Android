
package co.uk.justcheckingin;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventEmergencyService extends Service {
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    
    GPSTracker gps;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventAlarmActivity.getInstance().stopMediaPlayer();
        EventAlarmActivity.getInstance().finish();

        ContactList c = new ContactList();
        c = c.fromString(intent.getExtras().getString("contacts"));
        
        try {
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

            PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    new Intent(SENT), 0);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(),
                    0, new Intent(DELIVERED), 0);

            // for each emergency contact list
            for (int j = 0; j < c.getList().size(); j++) {
                sentIntents.add(sentPI);
                deliveryIntents.add(deliveredPI);
            }

            
            gps = new GPSTracker(EventEmergencyService.this);
            double latitude = 0, longitude = 0;
            // check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();

                // \n is for new line
                // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                // +
                // latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                // String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude,
                // longitude);
                // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                // startActivity(intent);

                // http://maps.google.com/?q=<lat>,<lng>
                // String link = String.format(Locale.ENGLISH,
                // "http://maps.google.com/?q=<%f>,<%f>", latitude, longitude);
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                // gps.showSettingsAlert();
            }
            SmsManager smsManager = SmsManager.getDefault();
            String strSMSBody = String
                    .format(Locale.ENGLISH,
                            "EMERGENCY! This is my location: http://maps.google.com/?q=%f,%f\nI notified my selected contacts using the JustCheckingIn app!",
                            latitude, longitude);
            List<String> messages = smsManager.divideMessage(strSMSBody);
            for (int i = 0; i < c.getList().size(); i++) {
                for (String str : messages) {
                    smsManager.sendTextMessage(c.getList().get(i).getNumber(),
                            null, str, sentIntents.get(i), deliveryIntents.get(i));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        Toast.makeText(getApplicationContext(), "Your "+c.getName()+" has been notified!",
                Toast.LENGTH_LONG).show();
        int rv = super.onStartCommand(intent, flags, startId);
        stopSelf();
        return rv;
    }
}
