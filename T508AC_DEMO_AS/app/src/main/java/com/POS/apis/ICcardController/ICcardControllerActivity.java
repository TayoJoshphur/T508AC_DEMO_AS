package com.POS.apis.ICcardController;

import com.imagpay.MessageHandler;
import com.imagpay.Settings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android_jb.com.POSD.controllers.ICcardController;
import jepower.com.t508ac_demo.R;

public class ICcardControllerActivity extends Activity implements
		OnClickListener {
	private ICcardController iccardController = null;
	private Button btn_connect;
	private Button btn_disconnect;
	private Button btn_ic;
	private Button btn_at24;
	private Button btn_sle;
	private Button btn_version;
	private TextView tv_message;
	private MessageHandler _mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.iccard_controller_layout);
		initview();
		initic();
	}

	private void setbtnEnabledFalse() {
		btn_connect.setEnabled(true);
		btn_disconnect.setEnabled(false);
		btn_ic.setEnabled(false);
		btn_at24.setEnabled(false);
		btn_sle.setEnabled(false);
		btn_version.setEnabled(false);
	}

	private void setbtnEnabledTrue() {
		btn_connect.setEnabled(false);
		btn_disconnect.setEnabled(true);
		btn_ic.setEnabled(true);
		btn_at24.setEnabled(true);
		btn_version.setEnabled(true);
		btn_sle.setEnabled(true);
	}

	private void initview() {
		// TODO Auto-generated method stub
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
		btn_ic = (Button) findViewById(R.id.btn_ic);
		btn_at24 = (Button) findViewById(R.id.btn_at24);
		btn_sle = (Button) findViewById(R.id.btn_sle);
		btn_version = (Button) findViewById(R.id.btn_version);
		btn_version.setOnClickListener(this);
		btn_connect.setOnClickListener(this);
		btn_disconnect.setOnClickListener(this);
		btn_ic.setOnClickListener(this);
		btn_at24.setOnClickListener(this);
		btn_sle.setOnClickListener(this);
		tv_message = (TextView) findViewById(R.id.status);
		setbtnEnabledFalse();
	}

	private void initic() {
		// TODO Auto-generated method stub
		iccardController = ICcardController.getInstance();
		_mHandler = new MessageHandler(tv_message);
	}

	@Override
	public synchronized void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_connect:
			iccardController.ICcardController_Init(this);
			connect();
			ssy = true;
			break;
		case R.id.btn_disconnect:
			disconnect();
			ssy = false;
			break;
		case R.id.btn_ic:
			ic();
			break;
		case R.id.btn_at24:
			new Thread(new Runnable() {
				@Override
				public void run() {
					at24();
				}
			}).start();
			break;
		case R.id.btn_sle:
			new Thread(new Runnable() {
				@Override
				public void run() {
					sle();
				}
			}).start();
			break;
		case R.id.btn_version:
			if (ssy) {
				new Thread() {
					public void run() {
						version();
					}
				}.start();
			} else {
				sendMessage("connected!");
			}
			break;
		default:
			break;
		}
	}

	private void sendMessage(String str) {
		_mHandler.sendMessage(str);
	}

	boolean flag = false;
	boolean ssy = false;

	synchronized private void version() {
		// TODO Auto-generated method stub
		String version = iccardController.ICcardController_readVersion();
		if (null == version) {
			sendMessage("version:null");
			return;
		}
		String[] ss = version.replaceAll("..", "$0 ").split(" ");
		StringBuffer sb = new StringBuffer();
		try {
			for (String d : ss) {
				sb.append((char) Integer.parseInt(d, 16));
			}
		} catch (Exception e) {
			// TODO: handle exception
			sb.append("readVersion exception");
		}

		sendMessage(sb.toString());
	}

	synchronized private void sle() {
		// TODO Auto-generated method stub
		boolean nRet = iccardController.ICcardController_sle4442Init();
		if (nRet)
			_mHandler.sendMessage("SLE4442 init successful!");
		else {
			_mHandler.sendMessage("SLE4442 init failure!");
			return;
		}
		sendMessage("RSC(before):"
				+ iccardController.ICcardController_sle4442RSC());
		sendMessage("CSC:"
				+ iccardController.ICcardController_sle4442CSC("FFFFFF"));
		sendMessage("RSC(after):"
				+ iccardController.ICcardController_sle4442RSC());
		sendMessage("RSTC:" + iccardController.ICcardController_sle4442RSTC());
		sendMessage("SRD:"
				+ iccardController.ICcardController_sle4442SRD(0, 50));
		sendMessage("PRD:" + iccardController.ICcardController_sle4442PRD());
	}

	synchronized private void at24() {
		// TODO Auto-generated method stub
		boolean nRet = iccardController.ICcardController_at24Reset();
		if (nRet)
			sendMessage("AT24 reset successful!");
		else {
			sendMessage("AT24 reset failure!");
			return;
		}
		sendMessage("AT24 R(0x00~0x0a):"
				+ iccardController.ICcardController_at24Read(0, 10,
						Settings.AT_24C16));
		nRet = iccardController.ICcardController_at24Write(2, 8,
				Settings.AT_24C16, "9988776655443322");
		if (nRet)
			sendMessage("AT24 write successful!");
		else {
			sendMessage("AT24 write failure!");
			return;
		}
	}

	synchronized private void ic() {
		// TODO Auto-generated method stub
		if(iccardController.ICcardController_isConnected()){
		if (flag) {
			sendMessage("Running......");
			return;
		}
		flag = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
			//	Log.v("ICcardControllerActivity","209------->>>>>>>>");
			//	String[] emv=iccardController.emv();
			//	sendMessage("IC CardNo:"+emv[0]);
			//	sendMessage("IC CardHolder:"+emv[1]);
			//	Log.v("ICcardControllerActivity","129<<<<<<<<<<<<-------");
			sendMessage("IC CardNo:"
						+ iccardController.ICcardController_icCardNo());
				flag = false;
			}
		}).start();
		}else{
			Toast.makeText(this, "Please connect again", Toast.LENGTH_SHORT)
			.show();
		}
	}

	private void disconnect() {
		// TODO Auto-generated method stub
		if (iccardController.ICcardController_Close()) {
			_mHandler.sendMessage("DisConnect Res:true");
			setbtnEnabledFalse();
			Toast.makeText(this, "disconnect_Success", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, "disconnect_Failure", Toast.LENGTH_SHORT)
					.show();
		}
	}

	synchronized private void connect() {
		// TODO Auto-generated method stub
		if (iccardController.ICcardController_Open(this)) {
			Toast.makeText(this, "connect_Success", Toast.LENGTH_SHORT).show();
			_mHandler.sendMessage("Connect Res:true");
			ssy = true;
			setbtnEnabledTrue();
		} else {
			ssy = false;
			Toast.makeText(this, "connect_Failure", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	synchronized protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		iccardController.ICcardController_Close();
	}
}
