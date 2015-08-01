
package co.uk.justcheckingin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

public class EmergencyNumbersActivity extends Activity {
    EditText uni1, uni2, uni3, police, fire, hospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers);

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

        uni1.setTextColor(Color.BLACK);
        uni2.setTextColor(Color.BLACK);
        uni3.setTextColor(Color.BLACK);
        police.setTextColor(Color.BLACK);
        fire.setTextColor(Color.BLACK);
        hospital.setTextColor(Color.BLACK);
    }
}
