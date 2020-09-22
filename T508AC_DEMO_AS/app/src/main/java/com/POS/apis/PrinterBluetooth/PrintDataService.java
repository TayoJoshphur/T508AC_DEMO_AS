package com.POS.apis.PrinterBluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import jepower.com.t508ac_demo.R;

public class PrintDataService {
	private Context context = null;
	private String deviceAddress = null;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private BluetoothDevice device = null;
	private static BluetoothSocket bluetoothSocket = null;
	private static OutputStream outputStream = null;
	private static final UUID uuid = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private boolean isConnection = false;
	final byte[][] byteCommands = { { 0x1b, 0x40 }, { 0x1b, 0x4d, 0x00 },
			{ 0x1b, 0x4d, 0x01 }, { 0x1d, 0x21, 0x00 }, { 0x1d, 0x21, 0x11 },
			{ 0x1b, 0x45, 0x00 }, { 0x1b, 0x45, 0x01 }, { 0x1b, 0x7b, 0x00 },
			{ 0x1b, 0x7b, 0x01 }, { 0x1d, 0x42, 0x00 }, { 0x1d, 0x42, 0x01 },
			{ 0x1b, 0x56, 0x00 }, { 0x1b, 0x56, 0x01 }, };

	public PrintDataService(Context context, String deviceAddress) {
		super();
		this.context = context;
		this.deviceAddress = deviceAddress;
		this.device = this.bluetoothAdapter.getRemoteDevice(this.deviceAddress);
	}

	public String getDeviceName() {
		return this.device.getName();
	}

	@SuppressLint("ShowToast")
	public boolean connect() {
		if (!this.isConnection) {
			try {
				bluetoothSocket = this.device
						.createRfcommSocketToServiceRecord(uuid);
				bluetoothSocket.connect();
				outputStream = bluetoothSocket.getOutputStream();
				this.isConnection = true;
				if (this.bluetoothAdapter.isDiscovering()) {
					this.bluetoothAdapter.isDiscovering();
				}
			} catch (Exception e) {
				Toast.makeText(this.context, R.string.cf, 1).show();
				return false;
			}
			Toast.makeText(
					this.context,
					this.device.getName() + " "
							+ context.getString(R.string.cs),
					Toast.LENGTH_SHORT).show();
			return true;
		} else {
			return true;
		}
	}

	public static void disconnect() {
		try {
			if (bluetoothSocket != null) {
				bluetoothSocket.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void selectCommand() {
		new AlertDialog.Builder(context).setTitle(R.string.psi)
				.setItems(R.array.commd, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (isConnection) {
							try {
								outputStream.write(byteCommands[which]);

							} catch (IOException e) {
								Toast.makeText(context, R.string.scf,
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(context, R.string.dincr,
									Toast.LENGTH_SHORT).show();
						}
					}
				}).create().show();
	}

	public void send(String sendData) {
		if (this.isConnection) {
			try {
				byte[] data = sendData.getBytes("gbk");
				outputStream.write(data, 0, data.length);
				outputStream.flush();
			} catch (IOException e) {
				Toast.makeText(this.context, R.string.sf, Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(this.context, R.string.dincr, Toast.LENGTH_SHORT)
					.show();

		}
	}

}