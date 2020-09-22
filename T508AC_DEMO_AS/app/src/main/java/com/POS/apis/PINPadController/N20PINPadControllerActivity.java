package com.POS.apis.PINPadController;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android_jb.com.POSD.controllers.N20PINPadController;
import jepower.com.t508ac_demo.R;

public class N20PINPadControllerActivity extends Activity implements
		OnClickListener {
	private Button btn_connect;
	private Button btn_disconnect;
	private Button btn_send;
	private Button btn_recv;
	private Button btn_clear;
	private Button btn_random;
	private TextView tv_version;
	private TextView tv_read;
	private EditText et_send;
	private N20PINPadController PinPadController;
	private int iret = -1;
	private int pinb = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.n20pinpad_controller_layout);
		initview();
		PinPadController = N20PINPadController.getInstance();
	}

	private void setViewEnabledFalse() {
		btn_connect.setEnabled(true);
		btn_disconnect.setEnabled(false);
		btn_send.setEnabled(false);
		btn_recv.setEnabled(false);
		btn_clear.setEnabled(false);
		btn_random.setEnabled(false);
		et_send.setEnabled(false);
		et_send.setClickable(false);
	}

	private void setViewEnabledTrue() {
		btn_connect.setEnabled(false);
		btn_disconnect.setEnabled(true);
		btn_send.setEnabled(true);
		btn_recv.setEnabled(true);
		btn_clear.setEnabled(true);
		btn_random.setEnabled(true);
		et_send.setEnabled(true);
		et_send.setClickable(true);
	}

	private void initview() {
		// TODO Auto-generated method stub
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
		btn_send = (Button) findViewById(R.id.pinpad_controller_btn_send);
		btn_recv = (Button) findViewById(R.id.pinpad_controller_btn_recv);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_random = (Button) findViewById(R.id.btn_random);
		btn_connect.setOnClickListener(this);
		btn_recv.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_disconnect.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		btn_random.setOnClickListener(this);
		tv_read = (TextView) findViewById(R.id.pinpad_controller_tv_recv);
		et_send = (EditText) findViewById(R.id.pinpad_controller_et_send);
		tv_version = (TextView) findViewById(R.id.pinpad_controller_tv_version);
		tv_version.setText("");
		setViewEnabledFalse();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_connect:
			connect();
			break;
		case R.id.btn_disconnect:
			disconnect();
			break;
		case R.id.btn_clear:
			clear();
			tv_read.setText("");
			break;
		case R.id.pinpad_controller_btn_send:
			String senddata = et_send.getText().toString();
			send(senddata);
			break;
		case R.id.pinpad_controller_btn_recv:
			recv();
			tv_read.setText("");
			break;
		case R.id.btn_random:
			random();
			break;
		default:
			break;
		}
	}

	private void random() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unused")
	private void version() {
		// TODO Auto-generated method stub

	}

	private void clear() {
		// TODO Auto-generated method stub
		if (null != PinPadController) {
			new Thread() {
				public void run() {
					PinPadController.PINPad_clear();
				};
			}.start();
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				tv_read.setText(msg.obj.toString());
				Toast.makeText(N20PINPadControllerActivity.this, "recv",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	private void recv() {
		// TODO Auto-generated method stub
		if (null != PinPadController) {
			new Thread() {
				public void run() {
					String readdata = PinPadController.PINPad_getFromPinPad("");
					if (null != readdata) {
						Message msg = new Message();
						msg.obj = readdata;
						msg.what = 0;
						mHandler.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.obj = "No password keyboard or data acquisition failure.";
						msg.what = 0;
						mHandler.sendMessage(msg);
					}
				};
			}.start();
		}
	}

	private void send(final String data) {
		// TODO Auto-generated method stub
		if (null != PinPadController) {
			new Thread() {
				public void run() {
					PinPadController.PINPad_sendToPinPad(data);
				};
			}.start();
			Toast.makeText(this, "send", Toast.LENGTH_SHORT).show();
		}
	}

	private void disconnect() {
		if (null != PinPadController) {
			iret = PinPadController.PINPad_Close();
			pinb = 3;
			setViewEnabledFalse();
			Toast.makeText(this, "disconnect", Toast.LENGTH_SHORT).show();
		}
	}

	private void connect() {
		iret = PinPadController.PINPad_Open();
		while (iret != 0) {
			iret = PinPadController.PINPad_Open();
		}
		if (0 == iret) {
			setViewEnabledTrue();
			Toast.makeText(this, "PINPad_Open_Success", Toast.LENGTH_SHORT)
					.show();
			pinb = 2;
		} else {
			Toast.makeText(this, "PINPad_Open_Failure", Toast.LENGTH_SHORT)
					.show();
		}
		/*
		 * // 更新主密钥，具体请按业务需求来实现 byte masterindex = 0x01; byte desflag = '0';
		 * byte[] keydata = { 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31,
		 * 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31 }; int datalen = 16;
		 * int iret1 = PinPadController.PINPad_updateMKey(masterindex, desflag,
		 * keydata, datalen); System.out.println("iret1 == " + iret1);
		 * 
		 * // 更新工作密钥，具体请按业务需求来实现 byte pinindex = 0x02; byte macindex = 0x04;
		 * byte desindex = 0x06; byte[] workkeydata = { (byte) 0xC6, (byte)
		 * 0x97, (byte) 0xFF, (byte) 0xE6, (byte) 0xEC, (byte) 0x86, (byte)
		 * 0x9A, 0x10, (byte) 0xC1, (byte) 0xD3, 0x46, 0x19, (byte) 0xA7, 0x43,
		 * (byte) 0xF4, 0x6E, (byte) 0x9C, (byte) 0xA2, (byte) 0xC6, (byte)
		 * 0x89, (byte) 0xF0, 0x44, (byte) 0x86, 0x35, (byte) 0xAC, 0x44, 0x32,
		 * 0x69, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xA2,
		 * 0x46, (byte) 0xBB, (byte) 0xB8, (byte) 0xC6, (byte) 0x97, (byte)
		 * 0xFF, (byte) 0xE6, (byte) 0xEC, (byte) 0x86, (byte) 0x9A, 0x10,
		 * (byte) 0xC1, (byte) 0xD3, 0x46, 0x19, (byte) 0xA7, 0x43, (byte) 0xF4,
		 * 0x6E, (byte) 0x9C, (byte) 0xA2, (byte) 0xC6, (byte) 0x89 }; desflag =
		 * '0'; datalen = 60; int iret2 =
		 * PinPadController.PINPad_updateWKey(masterindex, pinindex, macindex,
		 * desindex, desflag, keydata, datalen); System.out.println("iret2 == "
		 * + iret2); if (0 == iret1 && iret2 == 0) { setViewEnabledTrue();
		 * Toast.makeText(this, "PINPad_init_Success", Toast.LENGTH_SHORT)
		 * .show(); } else { Toast.makeText(this, "PINPad_init_Failure",
		 * Toast.LENGTH_SHORT) .show(); }
		 */
		byte masterindex = 0x01;
		byte desflag = '0';
		byte[] keydata = { 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31,
				0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31 };
		int datalen = 16;

		int iret1 = PinPadController.PINPad_updateMKey(masterindex, desflag,
				keydata, datalen);

		byte pinindex = 0x02;
		byte macindex = 0x04;
		byte desindex = 0x06;

		byte[] workkeydata = { (byte) 0xC6, (byte) 0x97, (byte) 0xFF,
				(byte) 0xE6, (byte) 0xEC, (byte) 0x86, (byte) 0x9A, 0x10,
				(byte) 0xC1, (byte) 0xD3, 0x46, 0x19, (byte) 0xA7, 0x43,
				(byte) 0xF4, 0x6E, (byte) 0x9C, (byte) 0xA2, (byte) 0xC6,
				(byte) 0x89, (byte) 0xF0, 0x44, (byte) 0x86, 0x35, (byte) 0xAC,
				0x44, 0x32, 0x69, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, (byte) 0xA2, 0x46, (byte) 0xBB, (byte) 0xB8, (byte) 0xC6,
				(byte) 0x97, (byte) 0xFF, (byte) 0xE6, (byte) 0xEC,
				(byte) 0x86, (byte) 0x9A, 0x10, (byte) 0xC1, (byte) 0xD3, 0x46,
				0x19, (byte) 0xA7, 0x43, (byte) 0xF4, 0x6E, (byte) 0x9C,
				(byte) 0xA2, (byte) 0xC6, (byte) 0x89 };

		desflag = '0';
		datalen = 60;

		int iret2 = PinPadController.PINPad_updateWKey(masterindex, pinindex,
				macindex, desindex, desflag, workkeydata, datalen);
		if (0 == iret1 && iret2 == 0) {
			setViewEnabledTrue();
			Toast.makeText(this, "PINPad_init_Success", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, "PINPad_init_Failure", Toast.LENGTH_SHORT)
					.show();
		}
	}

	protected void onDestroy() {
		if (pinb == 2) {
			System.out.println("PINPad_Close");
			PinPadController.PINPad_Close();
		}
		super.onDestroy();
	}
}
