package com.POS.apis.CaptureController;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class ResultPhotoActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String recode = getIntent().getStringExtra("result");
		TextView tv_show = new TextView(this);
		tv_show.setText(recode);
		tv_show.setGravity(Gravity.CENTER);
		setContentView(tv_show);
	}
}
