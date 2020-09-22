package com.POS.apis.PrinterBluetooth;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import jepower.com.t508ac_demo.R;

public class BluetoothActivity extends Activity {

	private Context context = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.bluetooth_layout);
		this.initListener();
	}

	BluetoothAction bluetoothAction = null;

	private void initListener() {
		ListView unbondDevices = (ListView) this
				.findViewById(R.id.unbondDevices);
		ListView bondDevices = (ListView) this.findViewById(R.id.bondDevices);
		Button switchBT = (Button) this.findViewById(R.id.openBluetooth_tb);
		Button searchDevices = (Button) this.findViewById(R.id.searchDevices);

		bluetoothAction = new BluetoothAction(this.context, unbondDevices,
				bondDevices, switchBT, searchDevices, BluetoothActivity.this);

		Button returnButton = (Button) this
				.findViewById(R.id.return_Bluetooth_btn);
		bluetoothAction.setSearchDevices(searchDevices);
		bluetoothAction.initView();

		switchBT.setOnClickListener(bluetoothAction);
		searchDevices.setOnClickListener(bluetoothAction);
		returnButton.setOnClickListener(bluetoothAction);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		bluetoothAction.Finish();
	}
}
