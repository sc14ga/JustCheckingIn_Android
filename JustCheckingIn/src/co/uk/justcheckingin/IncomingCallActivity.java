package co.uk.justcheckingin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class IncomingCallActivity extends Activity {
    private ImageButton accept;
    private Button decline;
    
    private String caller;
	private TextView mName;
    private TextView mPhoneNumber;
    private TextView mLabel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.call_card);
		
		Intent intent = getIntent();
		caller = intent.getExtras().getString("name");

		accept = (ImageButton) findViewById(R.id.acceptButton);
		decline = (Button) findViewById(R.id.declineButton);
		
        mName = (TextView) findViewById(R.id.name);
        mPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        mLabel = (TextView) findViewById(R.id.label);
        
        mName.setText(caller);
        mName.setTextColor(Color.WHITE);
        mPhoneNumber.setText("07518924080");
        //mLabel.setText("Label");
        
        decline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        accept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), InCallActivity.class);
				intent.putExtra("name", caller);
				startActivity(intent);
				finish();
			}
		});
	}
}
