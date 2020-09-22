package com.POS.apis.LedControllers;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import android_jb.com.POSD.controllers.LedController;
import jepower.com.t508ac_demo.R;

public class LedControllerActivity extends Activity {
	private boolean ledShow = false;
	private EditText et_show;
	private LedController controller;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.led_contorller_layout);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		et_show = (EditText) findViewById(R.id.et_number);
		controller = LedController.getInstance();
		controller.LedController_Open();
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_show:
			controller.LedController_Close_ShowNums(et_show.getText()
					.toString());
			break;
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		controller.LedController_Close();
	}

}
