
package co.uk.justcheckingin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class EventEmergencyService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventAlarmActivity.getInstance().stopMediaPlayer();
        EventAlarmActivity.getInstance().finish();

        Toast.makeText(getApplicationContext(), "Your ContactList has been notified!",
                Toast.LENGTH_LONG).show();
        int rv = super.onStartCommand(intent, flags, startId);
        stopSelf();
        return rv;
    }
}
