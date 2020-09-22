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

import android_jb.com.POSD.controllers.SP10PINPadController;
import jepower.com.t508ac_demo.R;

public class SP10PINPadControllerActivity extends Activity implements
		OnClickListener {
	private Button btn_connect;
	private Button btn_disconnect;
	private Button btn_send;
	private Button btn_recv;
	private Button btn_clear;
	private Button btn_init;
	private Button btn_update;
	private TextView tv_version;
	private TextView tv_read;
	private EditText et_send;
	private SP10PINPadController PinPadController;
	private int iret = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sp10pinpad_controller_layout);
		initview();
		PinPadController = SP10PINPadController.getInstance();
	}

	private void updateM() {
		byte[] pas = { 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38,
				0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38 };
		byte[] npas = { 0x7C, (byte) 0x8A, (byte) 0xBF, 0x51, 0x37,
				(byte) 0xAD, (byte) 0xDC, 0x52, (byte) 0x8A, (byte) 0xBF,
				(byte) 0xBF, (byte) 0xDF, (byte) 0xD0, (byte) 0xCD,
				(byte) 0xFD, 0x67 };
		int a = PinPadController.PINPad_updateMKey(3, pas, npas);
		System.out.println("aacc = " + a);
		if (a == 0) {
			Toast.makeText(SP10PINPadControllerActivity.this, "更新主密钥成功！", 0)
					.show();
		} else {
			Toast.makeText(SP10PINPadControllerActivity.this, "更新主密钥失败！", 0)
					.show();
		}
	}

	private void updteW() {
		byte[] wm = { 0x73, (byte) 0xE2, (byte) 0xDB, (byte) 0xCF, 0x11,
				(byte) 0x94, (byte) 0xCB, 0x65 };
		int a = PinPadController.PINPad_updateWKey(3, 1, wm);
		System.out.println(a & 0xff);
		if (a == 0) {
			Toast.makeText(SP10PINPadControllerActivity.this, "更新工作密钥成功！", 0)
					.show();
		} else {
			Toast.makeText(SP10PINPadControllerActivity.this, "更新工作密钥失败！", 0)
					.show();
		}
	}

	private void setViewEnabledFalse() {
		btn_connect.setEnabled(true);
		btn_disconnect.setEnabled(false);
		btn_send.setEnabled(false);
		btn_recv.setEnabled(false);
		btn_clear.setEnabled(false);
		btn_init.setEnabled(false);
		et_send.setEnabled(false);
		et_send.setClickable(false);
	}

	private void setViewEnabledTrue() {
		btn_connect.setEnabled(false);
		btn_disconnect.setEnabled(true);
		btn_send.setEnabled(true);
		btn_recv.setEnabled(true);
		btn_clear.setEnabled(true);
		btn_init.setEnabled(true);
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
		btn_init = (Button) findViewById(R.id.btn_init);
		btn_update = (Button) findViewById(R.id.btn_update);
		btn_update.setOnClickListener(this);
		btn_connect.setOnClickListener(this);
		btn_recv.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_disconnect.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		btn_init.setOnClickListener(this);
		tv_read = (TextView) findViewById(R.id.pinpad_controller_tv_recv);
		et_send = (EditText) findViewById(R.id.pinpad_controller_et_send);
		tv_version = (TextView) findViewById(R.id.pinpad_controller_tv_version);
		tv_version.setText("");
		setViewEnabledFalse();
	}

	private int res = -1;

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
			getData();
			recv();
			tv_read.setText("");
			break;
		case R.id.btn_init:
			new Thread() {
				public void run() {
					res = PinPadController.PINPad_Init();
					if (res == 0) {
						Message msg = new Message();
						msg.obj = getResources().getString(
								R.string.pinpad_controller_inits);
						msg.what = 0;
						mHandler.sendMessage(msg);
					}
				};
			}.start();
			break;
		case R.id.btn_update:
			updateM();
			updteW();
			break;
		default:
			break;
		}
	}

	private void clear() {
		if (null != PinPadController) {
			new Thread() {
				public void run() {
					PinPadController.PINPad_clear();
				};
			}.start();
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				tv_read.setText(msg.obj.toString());
				Toast.makeText(SP10PINPadControllerActivity.this, "recv",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	private void recv() {
		if (null != PinPadController) {
			new Thread() {
				public void run() {
					int readdata = PinPadController.PINPad_InputPINPinPad();
				};
			}.start();
		}
	}

	private void send(final String data) {
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
			PinPadController.PINPad_Close();
			setViewEnabledFalse();
			Toast.makeText(this, "disconnect", Toast.LENGTH_SHORT).show();
		}
	}

	private void connect() {
		iret = PinPadController.PINPad_Open();
		if (0 == iret) {
			setViewEnabledTrue();
			Toast.makeText(this, "PINPad_Open_Success", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, "PINPad_Open_Failure", Toast.LENGTH_SHORT)
					.show();
		}
		if (0 == iret) {
			setViewEnabledTrue();
			Toast.makeText(this, "PINPad_init_Success", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, "PINPad_init_Failure", Toast.LENGTH_SHORT)
					.show();
		}

	}

	private void getData() {
		try {
			new Thread() {
				public void run() {
					boolean whs = true;
					while (whs) {
						try {
							Thread.sleep(300);
						} catch (Exception e) {
							e.printStackTrace();
						}
						String readdata = PinPadController
								.PINPad_GetDataPinPad();
						if (readdata == null) {
							continue;
						}
						Message msg = new Message();
						msg.obj = readdata;
						System.out.println("readdata == " + readdata);
						msg.what = 0;
						mHandler.sendMessage(msg);
						whs = false;
					}
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (null != PinPadController && iret != 1) {
			PinPadController.PINPad_Close();
		}
	}
}
