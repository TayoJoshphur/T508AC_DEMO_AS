package com.POS.apis.PrinterBluetooth;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jepower.com.t508ac_demo.R;

public class PrintDataActivity extends Activity {
	private Context context = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.printdata_layout);
		this.context = this;
		this.initListener();
	}

	private String getDeviceAddress() {
		Intent intent = this.getIntent();
		if (intent != null) {
			return intent.getStringExtra("deviceAddress");
		} else {
			return null;
		}
	}

	private void initListener() {
		TextView deviceName = (TextView) this.findViewById(R.id.device_name);
		TextView connectState = (TextView) this
				.findViewById(R.id.connect_state);

		PrintDataAction printDataAction = new PrintDataAction(this.context,
				this.getDeviceAddress(), deviceName, connectState);

		EditText printData = (EditText) this.findViewById(R.id.print_data);
		Button send = (Button) this.findViewById(R.id.send);
		Button command = (Button) this.findViewById(R.id.command);
		printDataAction.setPrintData(printData);

		send.setOnClickListener(printDataAction);
		command.setOnClickListener(printDataAction);
	}

	@Override
	protected void onDestroy() {
		PrintDataService.disconnect();
		super.onDestroy();
	}

}