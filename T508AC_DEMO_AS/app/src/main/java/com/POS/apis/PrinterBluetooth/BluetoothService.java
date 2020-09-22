package com.POS.apis.PrinterBluetooth;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import jepower.com.t508ac_demo.R;

public class BluetoothService {
	private Context context = null;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private ArrayList<BluetoothDevice> unbondDevices = null;
	private ArrayList<BluetoothDevice> bondDevices = null;
	private Button switchBT = null;
	private Button searchDevices = null;
	private ListView unbondDevicesListView = null;
	private ListView bondDevicesListView = null;

	private void addBondDevicesToListView() {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		int count = this.bondDevices.size();
		for (int i = 0; i < count; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("deviceName", this.bondDevices.get(i).getName());
			data.add(map);
		}
		String[] from = { "deviceName" };
		int[] to = { R.id.device_name };
		SimpleAdapter simpleAdapter = new SimpleAdapter(this.context, data,
				R.layout.bonddevice_item, from, to);
		this.bondDevicesListView.setAdapter(simpleAdapter);

		this.bondDevicesListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						BluetoothDevice device = bondDevices.get(arg2);
						Intent intent = new Intent(context,PrintDataActivity.class);
//						intent.setClassName(context,
//								"com.POS.apis.PrinterBluetooth.PrintDataActivity");
						intent.putExtra("deviceAddress", device.getAddress());
						context.startActivity(intent);
					}
				});

	}

	private void addUnbondDevicesToListView() {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		int count = this.unbondDevices.size();
		for (int i = 0; i < count; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("deviceName", this.unbondDevices.get(i).getName());
			data.add(map);
		}
		String[] from = { "deviceName" };
		int[] to = { R.id.undevice_name };
		SimpleAdapter simpleAdapter = new SimpleAdapter(this.context, data,
				R.layout.unbonddevice_item, from, to);

		this.unbondDevicesListView.setAdapter(simpleAdapter);

		this.unbondDevicesListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						try {
							Method createBondMethod = BluetoothDevice.class
									.getMethod("createBond");
							createBondMethod.invoke(unbondDevices.get(arg2));
							bondDevices.add(unbondDevices.get(arg2));
							unbondDevices.remove(arg2);
							addBondDevicesToListView();
							addUnbondDevicesToListView();
						} catch (Exception e) {
						}

					}
				});
	}

	public BluetoothService(Context context, ListView unbondDevicesListView,
			ListView bondDevicesListView, Button switchBT, Button searchDevices) {
		this.context = context;
		this.unbondDevicesListView = unbondDevicesListView;
		this.bondDevicesListView = bondDevicesListView;
		// this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.unbondDevices = new ArrayList<BluetoothDevice>();
		this.bondDevices = new ArrayList<BluetoothDevice>();
		this.switchBT = switchBT;
		this.searchDevices = searchDevices;
		this.initIntentFilter();

	}

	public void registerReceivers() {
		context.unregisterReceiver(receiver);
	}

	private void initIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		context.registerReceiver(receiver, intentFilter);

	}

	public void openBluetooth(Activity activity) {
		Intent enableBtIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
		activity.startActivityForResult(enableBtIntent, 1);
	}

	public void closeBluetooth() {
		this.bluetoothAdapter.disable();
	}

	public boolean isOpen() {
		if (null == bluetoothAdapter) {
			return false;
		} else {
			return this.bluetoothAdapter.isEnabled();
		}

	}

	public void searchDevices() {
		this.bondDevices.clear();
		this.unbondDevices.clear();

		this.bluetoothAdapter.startDiscovery();
	}

	public void addUnbondDevices(BluetoothDevice device) {
		if (!this.unbondDevices.contains(device)) {
			this.unbondDevices.add(device);
		}
	}

	public void addBandDevices(BluetoothDevice device) {
		if (!this.bondDevices.contains(device)) {
			this.bondDevices.add(device);
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		ProgressDialog progressDialog = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
					addBandDevices(device);
				} else {
					addUnbondDevices(device);
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				progressDialog = ProgressDialog.show(context,
						context.getString(R.string.Please_wait),
						context.getString(R.string.Search), true);

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				progressDialog.dismiss();

				addUnbondDevicesToListView();
				addBondDevicesToListView();
				// bluetoothAdapter.cancelDiscovery();
			}
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
					switchBT.setText(R.string.closeBluetooth);
					searchDevices.setEnabled(true);
					bondDevicesListView.setEnabled(true);
					unbondDevicesListView.setEnabled(true);
				} else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
					switchBT.setText(R.string.openBluetooth);
					searchDevices.setEnabled(false);
					bondDevicesListView.setEnabled(false);
					unbondDevicesListView.setEnabled(false);
				}
			}

		}
	};

}