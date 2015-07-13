package co.uk.justcheckingin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class EventsActivity extends Activity{
	ListView list;
	Button create, cancel, start;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		Button create = (Button) findViewById(R.id.create_button);
		Button cancel = (Button) findViewById(R.id.cancel_button);
		Button start = (Button) findViewById(R.id.start_button);
		
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		ListView list = (ListView) findViewById(R.id.listView1);
	}
}
