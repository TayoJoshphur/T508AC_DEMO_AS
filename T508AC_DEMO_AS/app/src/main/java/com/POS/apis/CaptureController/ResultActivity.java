package com.POS.apis.CaptureController;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import jepower.com.t508ac_demo.R;

public class ResultActivity extends Activity {
	ImageView image;
	TextView text;
	Button btn;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.result);
		image = (ImageView) findViewById(R.id.image);
		text = (TextView) findViewById(R.id.text);
		btn = (Button) findViewById(R.id.btn);
		
		String s =getIntent().getStringExtra("text");
		text.setText(s);
				
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ResultActivity.this,CaptureActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
