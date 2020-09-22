package com.POS.apis.LaserlightController;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import android_jb.com.POSD.controllers.LaserlightController;
import jepower.com.t508ac_demo.R;

public class LaserlightControllerActivity extends Activity implements
		OnClickListener {
	private Button btn_jopen = null;
	private Button btn_jclose = null;
	private Button btn_nopen = null;
	private Button btn_nclose = null;
	private LaserlightController laserlightController = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.laserlight_controller_layout);
		btn_jopen = (Button) findViewById(R.id.btn_jopen);
		btn_jclose = (Button) findViewById(R.id.btn_jclose);
		btn_jopen.setOnClickListener(this);
		btn_jclose.setOnClickListener(this);

		btn_nopen = (Button) findViewById(R.id.btn_nopen);
		btn_nclose = (Button) findViewById(R.id.btn_nclose);
		btn_nopen.setOnClickListener(this);
		btn_nclose.setOnClickListener(this);
		laserlightController = LaserlightController.getInstance();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_jopen:
			int flag1 = laserlightController.LaserlightController_J_Open();
			if (flag1 == 1) {
				Toast.makeText(this, "light_Open_Success",
						Toast.LENGTH_SHORT).show();
			} else if (flag1 == 0) {
				Toast.makeText(this, "light_Open_Failure",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_jclose:
			int flag2 = laserlightController.LaserlightController_J_Close();
			if (flag2 == 1) {
				Toast.makeText(this, "light_Close_Success",
						Toast.LENGTH_SHORT).show();
			} else if (flag2 == 0) {
				Toast.makeText(this, "light_Close_Failure",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_nopen:
			int flag3 = laserlightController.LaserlightController_N_Open();
			if (flag3 == 1) {
				Toast.makeText(this, "light_Open_Success",
						Toast.LENGTH_SHORT).show();
			} else if (flag3 == 0) {
				Toast.makeText(this, "light_Open_Failure",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_nclose:
			int flag4 = laserlightController.LaserlightController_N_Close();
			if (flag4 == 1) {
				Toast.makeText(this, "light_Close_Success",
						Toast.LENGTH_SHORT).show();
			} else if (flag4 == 0) {
				Toast.makeText(this, "light_Close_Failure",
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//laserlightController.LaserlightController_Close(23);
		laserlightController.LaserlightController_Close();
	}
}
