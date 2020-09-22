package com.POS.apis.RS232Controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android_jb.com.POSD.controllers.RS232Controller;
import android_jb.com.POSD.interfaces.RS232ReadCallback;
import jepower.com.t508ac_demo.R;

public class RS232ControllerActivity extends Activity implements
		OnClickListener, OnItemSelectedListener, RS232ReadCallback {
	private Button btn_connect;
	private Button btn_disconnect;
	private Button btn_send;
	private Button btn_clean;
	private TextView tv_read;
	private EditText et_write;

	private Spinner sp_bits;
	private Spinner sp_event;
	private Spinner sp_stopbits;
	private Spinner sp_file;
	private Spinner sp_baud;

	private ArrayAdapter<String> adapter_file;
	private ArrayAdapter<String> adapter_baud;
	private ArrayAdapter<String> adapter_stopbits;
	private ArrayAdapter<String> adapter_bits;
	private ArrayAdapter<String> adapter_event;

	private RS232Controller rs232Controller = null;
	private String file;
	private int baud;
	private int bits;
	private char event;
	private int stopbits;

	private String tempStr = null;
	private static final int READMESSAGE = 0;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (READMESSAGE == msg.what) {
				tv_read.setText(msg.obj.toString());
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rs232_controller_layout);
		initViews();
		initRS232();
	}

	private void setviewsEnabledFalse() {
		btn_connect.setEnabled(true);
		btn_disconnect.setEnabled(false);
		btn_send.setEnabled(false);
		et_write.setEnabled(false);
		btn_clean.setEnabled(false);
	}

	private void setviewsEnabledTrue() {
		btn_connect.setEnabled(false);
		btn_disconnect.setEnabled(true);
		btn_send.setEnabled(true);
		et_write.setEnabled(true);
		btn_clean.setEnabled(true);
	}

	private void initRS232() {
		rs232Controller = RS232Controller.getInstance();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
		btn_send = (Button) findViewById(R.id.btn_send);
		btn_clean = (Button) findViewById(R.id.btn_clean);
		btn_connect.setOnClickListener(this);
		btn_disconnect.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_clean.setOnClickListener(this);

		sp_file = (Spinner) findViewById(R.id.sp_file);
		sp_baud = (Spinner) findViewById(R.id.sp_baud);
		sp_bits = (Spinner) findViewById(R.id.sp_bits);
		sp_event = (Spinner) findViewById(R.id.sp_event);
		sp_stopbits = (Spinner) findViewById(R.id.sp_stopbits);

		sp_file.setOnItemSelectedListener(this);
		sp_baud.setOnItemSelectedListener(this);
		sp_bits.setOnItemSelectedListener(this);
		sp_event.setOnItemSelectedListener(this);
		sp_stopbits.setOnItemSelectedListener(this);

		adapter_file = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.port));
		adapter_file
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_file.setAdapter(adapter_file);

		adapter_baud = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.butrat));
		adapter_baud
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_baud.setAdapter(adapter_baud);

		adapter_stopbits = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.stopbits));
		adapter_stopbits
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_stopbits.setAdapter(adapter_stopbits);

		adapter_bits = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.bits));
		adapter_bits
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_bits.setAdapter(adapter_bits);

		adapter_event = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.event));
		adapter_event
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_event.setAdapter(adapter_event);

		tv_read = (TextView) findViewById(R.id.tv_read);
		et_write = (EditText) findViewById(R.id.et_write);
		setviewsEnabledFalse();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_connect:
			setviewsEnabledTrue();
			connect();
			tv_read.setText("");
			break;
		case R.id.btn_disconnect:
			setviewsEnabledFalse();
			disconnect();
			tv_read.setText("");
			break;
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_clean:
			tv_read.setText("");
			et_write.setText("");
			Toast.makeText(this, "clean", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	private void initdevice() {
		// TODO Auto-generated method stub
		file = "/dev/"
				+ clearBlank(sp_file.getSelectedItem().toString().trim());
		baud = Integer.parseInt(clearBlank(sp_baud.getSelectedItem().toString()
				.trim()));
		bits = Integer.parseInt(clearBlank(sp_bits.getSelectedItem().toString()
				.trim()));
		// event = Character.valueOf(clearBlank(
		// sp_event.getSelectedItem().toString().trim()).charAt(0));
		if (sp_event.getSelectedItemPosition() == 1) {
			event = 'O';
		} else if (sp_event.getSelectedItemPosition() == 2) {
			event = 'E';
		} else {
			event = 'N';
		}
		stopbits = (int) Float.parseFloat(clearBlank(sp_stopbits
				.getSelectedItem().toString().trim()));
	}

	private void send() {
		// TODO Auto-generated method stub
		String data = et_write.getText().toString();
		rs232Controller.Rs232_Write(data.getBytes());
		Toast.makeText(this, "send", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void RS232_Read(byte[] data) {
		// TODO Auto-generated method stub
		tempStr = new String(data, 0, data.length);
		Message msg = new Message();
		msg.obj = tempStr;
		msg.what = READMESSAGE;
		mHandler.sendMessage(msg);
	}

	private void disconnect() {
		// TODO Auto-generated method stub
		if (null != rs232Controller) {
			rs232Controller.Rs232_Close();
			rs232Controller = null;
			Toast.makeText(this, "disconnect_Success", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void connect() {
		// TODO Auto-generated method stub
		if (null == rs232Controller) {
			rs232Controller = RS232Controller.getInstance();
		}
		start();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if (rs232Controller != null) {
			initdevice();
			rs232Controller.Rs232_Close();
			rs232Controller.Rs232_Open(file, baud, bits, event, stopbits, this);
		}
	}

	private void start() {
		initdevice();
		if (rs232Controller != null) {
			int flag = rs232Controller.Rs232_Open(file, baud, bits, event,
					stopbits, this);
			if (flag == 0) {
				Toast.makeText(this, "connect_Success", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this, "connect_Failure", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private String clearBlank(String str) {
		String dest = "";
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(str);
		dest = m.replaceAll("");
		return dest;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (null != rs232Controller) {
			rs232Controller.Rs232_Close();
			rs232Controller = null;
		}
		super.onDestroy();
	}
}
