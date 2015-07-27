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

public class InCallActivity extends Activity {
	private ImageButton terminate;
    
    private String caller;
	private TextView mName;
    private TextView mPhoneNumber;
    private TextView mLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.incall_card);
		
		Intent intent = getIntent();
		caller = intent.getExtras().getString("name");
		
		mName = (TextView) findViewById(R.id.name);
		mPhoneNumber = (TextView) findViewById(R.id.number);
		mLabel = (TextView) findViewById(R.id.label);
		
		mName.setText(caller);
		mName.setTextColor(Color.WHITE);
		
		terminate = (ImageButton) findViewById(R.id.terminateButton);
		terminate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
