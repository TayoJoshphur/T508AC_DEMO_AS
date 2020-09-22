package com.POS.apis.MagnetCardController;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android_jb.com.POSD.controllers.MagnetCardController;
import android_jb.com.POSD.interfaces.MagnetCardReadCallBack;
import jepower.com.t508ac_demo.R;

@SuppressLint("HandlerLeak")
public class MagnetCardControllerActivity extends Activity implements
		OnClickListener, MagnetCardReadCallBack, OnItemSelectedListener {
	private Button btn_connect;
	private Button btn_disconnect;
	private Button btn_clear;
	private RadioButton rd_encryptionmode;
	private RadioButton rd_disencryptionmode;
	private TextView et_read;
	private TextView tv_version;
	private Spinner sp_msr;
	private ScrollView sl_show;
	private ArrayAdapter<String> adapter = null;
	private MagnetCardController magneticCardController = null;
	private int flag = 0;
	private int msr_en = 0;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//et_read.setText("");
			if (0 == msg.what) {
				et_read.setText(et_read.getText().toString()+"\n\n"+msg.obj.toString());
				Log.i("et_read", msg.obj.toString());
				mHandler.post(new Runnable() {
				    @Override
				    public void run() {
				    	sl_show.fullScroll(ScrollView.FOCUS_DOWN);
				    }
				});
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.magneticcard_controller_layout);
		initview();
	}

	private void initview() {
		magneticCardController = MagnetCardController.getInstance();
		tv_version = (TextView) findViewById(R.id.tv_version);
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		rd_encryptionmode = (RadioButton) findViewById(R.id.rd_encryptionmode);
		rd_disencryptionmode = (RadioButton) findViewById(R.id.rd_disencryptionmode);
		btn_connect.setOnClickListener(this);
		btn_disconnect.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		rd_encryptionmode.setOnClickListener(this);
		rd_disencryptionmode.setOnClickListener(this);
		sl_show = (ScrollView) findViewById(R.id.sl_show);
		et_read = (TextView) findViewById(R.id.et_read);
		et_read.setText("");
		et_read.setFocusable(true);
		et_read.setFocusableInTouchMode(true);
		sp_msr = (Spinner) findViewById(R.id.sp_msr);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.mode_ne));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_msr.setAdapter(adapter);
		sp_msr.setOnItemSelectedListener(this);
		setViewsetEnabledFalse();
	}

	private void setViewsetEnabledFalse() {
		btn_connect.setEnabled(true);
		btn_disconnect.setEnabled(false);
		btn_clear.setEnabled(false);
		rd_encryptionmode.setEnabled(true);
		rd_disencryptionmode.setEnabled(true);
	}

	private void setViewsetEnabledTrue() {
		btn_connect.setEnabled(false);
		btn_disconnect.setEnabled(true);
		btn_clear.setEnabled(true);
		rd_encryptionmode.setEnabled(true);
		rd_disencryptionmode.setEnabled(true);
	}

	public void onClick(View arg0) {
		et_read.setText("");
		switch (arg0.getId()) {
		case R.id.btn_connect:
			et_read.setText("");
			connect();
			break;
		case R.id.btn_disconnect:
			disconnect();
			break;
		case R.id.rd_encryptionmode:
			flag = 1;
			Toast.makeText(this, "MagneticCardController_encryptionmode",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.rd_disencryptionmode:
			flag = 0;
			Toast.makeText(this, "MagneticCardController_disencryptionmode",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_clear:
			et_read.setText("");
			Toast.makeText(this, "MagneticCardController_clear",
					Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	private void connect() {
		int flag = magneticCardController.MagnetCardController_Open(this, msr_en);
		System.out.println("flag == " + flag);
		if (flag == 0) {
			setViewsetEnabledTrue();
			Toast.makeText(this, "MagneticCardController_Open_Success",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "MagneticCardController_Open_Failure",
					Toast.LENGTH_SHORT).show();
		}
		tv_version.setText(getResources().getString(R.string.msr_version) + magneticCardController.getVersion());
	}

	private void disconnect() {
		int flag = magneticCardController.MagnetCardController_Close(msr_en);
		if (flag == 0) {
			setViewsetEnabledFalse();
			Toast.makeText(this, "MagnetCardController_Close_Success",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "MagnetCardController_Close_Failure",
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void onDestroy() {
		magneticCardController.MagnetCardController_Close(flag);
		super.onDestroy();
	}

	public void MagnetCardController_Read(byte[] data) {
		if (null == data)
			data = "".getBytes();
		Message msg = new Message();
		msg.what = 0;
		msg.obj = new String(data);
		mHandler.sendMessage(msg);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0){
			msr_en = 0;
		}else {
			msr_en = 1;
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {

	}
}