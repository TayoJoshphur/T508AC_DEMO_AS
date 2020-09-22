package com.POS.apis.CashboxController;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import android_jb.com.POSD.controllers.CashboxController;
import jepower.com.t508ac_demo.R;

public class CashboxControllerActivity extends Activity implements
		OnClickListener {
	private Button btn = null;
	private CashboxController cashboxController = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cashbox_controller_layout);
		btn = (Button) findViewById(R.id.cashbox_open);
		btn.setOnClickListener(this);
		cashboxController = CashboxController.getInstance();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.cashbox_open:
			Controller();
			break;
		default:
			break;
		}
	}

	private void Controller() {
		// TODO Auto-generated method stub
		int flag = cashboxController.CashboxController_Controller("1");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int flag1 = cashboxController.CashboxController_Controller("0");
		if (0 == flag) {
			Toast.makeText(this, "CashboxController_Failure",
					Toast.LENGTH_SHORT).show();
		} else if (1 == flag &&flag1==1) {
			Toast.makeText(this, "CashboxController_Success",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onPause(){
	 cashboxController.CashboxController_Controller("0");
	 super.onPause();
	}
}
