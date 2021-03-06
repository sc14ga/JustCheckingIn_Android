
package co.uk.justcheckingin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Screen of an ongoing fake call.
 * 
 * @author Georgios Aikaterinakis
 */
public class InCallActivity extends Activity {
    private Button terminate;

    private String caller;
    private TextView mName;
    private TextView mTimer;

    Thread t;
    int timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.incall_card);

        Intent intent = getIntent();
        caller = intent.getExtras().getString("name");

        mName = (TextView) findViewById(R.id.name);
        mTimer = (TextView) findViewById(R.id.timer);

        mName.setText(caller);
        mName.setTextColor(Color.WHITE);

        final String format = "%1$02d";
        timer = 0;

        // Thread to calculate the elapsed time and update the TextView on the screen
        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timer++;
                                mTimer.setText(String.format(format, timer / 60) + ":"
                                        + String.format(format, timer - 60 * (timer / 60)));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    // Interrupt sent by application to stop the thread.
                }
            }
        };

        t.start();

        terminate = (Button) findViewById(R.id.terminateButton);
        terminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.interrupt();
                finish();
            }
        });
    }
}
