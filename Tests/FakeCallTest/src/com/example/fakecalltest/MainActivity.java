package com.example.fakecalltest;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private ImageView mPhoto;
    private View mPhotoDimEffect;
    
	private TextView mName;
    private TextView mPhoneNumber;
    private TextView mLabel;
    private TextView mCallTypeLabel;
	
	private TextView mElapsedTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_card);
		
		//mPhoto = (ImageView) findViewById(R.id.photo);
        mPhotoDimEffect = findViewById(R.id.dim_effect_for_primary_photo);

        mName = (TextView) findViewById(R.id.name);
        mPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        mLabel = (TextView) findViewById(R.id.label);
        mCallTypeLabel = (TextView) findViewById(R.id.callTypeLabel);
        
        mElapsedTime = (TextView) findViewById(R.id.elapsedTime);
        
        //showImage(mPhoto, R.drawable.picture_unknown);
        
        mName.setText("George");
        mPhoneNumber.setText("07518924080");
        mElapsedTime.setText("00:02");
        mLabel.setText("Label");
        mCallTypeLabel.setText("Call");
        
	}
	
}
