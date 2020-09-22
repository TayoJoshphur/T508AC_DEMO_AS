package com.POS.apis.BuzzerController;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import android_jb.com.POSD.controllers.BuzzerController;
import jepower.com.t508ac_demo.R;

public class BuzzerControllerActivity extends Activity implements
		OnClickListener {
	private Button btn_open = null;
	private Button btn_close = null;
	private BuzzerController buzzerController = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buzzer_controller_layout);
		btn_open = (Button) findViewById(R.id.btn_open);
		btn_close = (Button) findViewById(R.id.btn_close);
		btn_open.setOnClickListener(this);
		btn_close.setOnClickListener(this);
		buzzerController = BuzzerController.getInstance();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_open:
			Open();
			break;
		case R.id.btn_close:
			Close();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		buzzerController.BuzzerController_Close();
	}

	private void Close() {
		// TODO Auto-generated method stub
		int flag = buzzerController.BuzzerController_Close();
		if (flag == 0) {
			Toast.makeText(this, "Buzzer_Close_Success", Toast.LENGTH_SHORT)
					.show();
		} else if (flag == -1) {
			Toast.makeText(this, "Buzzer_Close_Failure", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void Open() {
		// TODO Auto-generated method stub
		int flag = buzzerController.BuzzerController_Open();
		System.out.println("flag === " + flag);
		if (flag == 0) {
			Toast.makeText(this, "Buzzer_Open_Success", Toast.LENGTH_SHORT)
					.show();
		} else if (flag == -1) {
			Toast.makeText(this, "Buzzer_Open_Failure", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
