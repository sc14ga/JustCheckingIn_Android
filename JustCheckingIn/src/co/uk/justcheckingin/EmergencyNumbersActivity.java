
package co.uk.justcheckingin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Useful Numbers screen.
 * 
 * @author Georgios Aikaterinakis
 */
public class EmergencyNumbersActivity extends Activity {
    private EditText uni1, uni2, uni3, police, fire, hospital;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_numbers);

        // Back Button
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Settings Button
        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uni1 = (EditText) findViewById(R.id.editText1);
        uni2 = (EditText) findViewById(R.id.editText2);
        uni3 = (EditText) findViewById(R.id.editText3);
        police = (EditText) findViewById(R.id.editText4);
        fire = (EditText) findViewById(R.id.editText5);
        hospital = (EditText) findViewById(R.id.editText6);

        uni1.setText("+44 (0) 113 343 5494");
        uni2.setText("+44 (0) 113 343 5495");
        uni3.setText("+44 (0) 113 343 2222");
        police.setText("999");
        fire.setText("999");
        hospital.setText("0113 243 2799");

        uni1.setEnabled(false);
        uni2.setEnabled(false);
        uni3.setEnabled(false);
        police.setEnabled(false);
        fire.setEnabled(false);
        hospital.setEnabled(false);
    }
}
