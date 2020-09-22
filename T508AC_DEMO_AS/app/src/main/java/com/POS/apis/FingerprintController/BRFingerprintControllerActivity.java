package com.POS.apis.FingerprintController;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android_jb.com.POSD.controllers.BRFingerprintController;
import jepower.com.t508ac_demo.R;

public class BRFingerprintControllerActivity extends Activity implements
		OnClickListener {
	private static final short CMD_RT_OK = 0x0000; // 指令执行完毕或OK
	private static final short CMD_RT_PACKGE_ERR = 0x0001; // 数据包接收错误
	private static final short CMD_RT_DEVICE_ADDRESS_ERR = 0x0002; // 设备地址错误
	private static final short CMD_RT_COM_PASSWORD_ERR = 0x0003; // 通信密码错误
	private static final short CMD_RT_NO_FINGER = 0x0004; // 传感器上没有手指
	private static final short CMD_RT_GET_IMAGE_FAILE = 0x0005; // 从传感器上获取图像失败
	private static final short CMD_RT_GEN_CHAR_ERR = 0x0006; // 生成特征失败
	private static final short CMD_RT_FINGER_MATCH_ERR = 0x0007; // 指纹不匹配
	private static final short CMD_RT_FINGER_SEARCH_FAILE = 0x0008; // 没搜索到指纹
	private static final short CMD_RT_MERGE_TEMPLET_FAILE = 0x0009; // 特征合并失败
	private static final short CMD_RT_ADDRESS_OVERFLOW = 0x000A; // 将模板存库时地址序号超出指纹库范围
	private static final short CMD_RT_READ_TEMPLET_ERR = 0x000B; // 从指纹库读模板出错
	private static final short CMD_RT_UP_TEMPLET_ERR = 0x000C; // 上传特征失败
	private static final short CMD_RT_UP_IMAGE_FAILE = 0x000D; // 上传图像失败
	private static final short CMD_RT_DELETE_TEMPLET_ERR = 0x000E; // 删除模板失败
	private static final short CMD_RT_CLEAR_TEMPLET_LIB_ERR = 0x000F; // 清空指纹库失败
	private static final short CMD_RT_FINGER_NOT_MOVE = 0x0010; // 残留指纹或传感器窗口的按指长时间未移开
	private static final short CMD_RT_NO_TEMPLET_IN_ADDRESS = 0x0011; // 指定位置没有有效模板
	private static final short CMD_RT_CHAR_REPEAT = 0x0012; // 指纹重复，需要注册的指纹已经在FLASH中注册
	private static final short CMD_RT_MB_NOT_EXIST_IN_ADDRESS = 0x0013; // 该地址中不存在指纹模板
	private static final short CMD_RT_GET_MBINDEX_OVERFLOW = 0x0014; // 获取模板索引长度溢出

	private Button btn_connect;
	private Button btn_disconnect;
	private Button btn_reset;
	private Button btn_delete;
	private Button btn_clear;
	private Button btn_fingerprintrecord;
	private Button btn_fingerprintquery;
	private TextView tv_result;
	// private TextView tv_information;
	private BRFingerprintController fingerprintRecognitionController = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fingerprint_controller_layout);
		initfingerprintrecognition();
		initview();
	}

	private void setBtnEnabledFalse() {
		btn_connect.setEnabled(true);
		btn_disconnect.setEnabled(false);
		btn_delete.setEnabled(false);
		btn_clear.setEnabled(false);
		btn_reset.setEnabled(false);
		btn_fingerprintrecord.setEnabled(false);
		btn_fingerprintquery.setEnabled(false);
	}

	private void setBtnEnabledTrue() {
		btn_connect.setEnabled(false);
		btn_disconnect.setEnabled(true);
		btn_delete.setEnabled(true);
		btn_reset.setEnabled(true);
		btn_clear.setEnabled(true);
		btn_fingerprintrecord.setEnabled(true);
		btn_fingerprintquery.setEnabled(true);
	}

	private void initview() {
		// TODO Auto-generated method stub
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
		btn_reset = (Button) findViewById(R.id.btn_fingerreset);
		btn_delete = (Button) findViewById(R.id.btn_delete);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_fingerprintrecord = (Button) findViewById(R.id.btn_fingerprintrecord);
		btn_fingerprintquery = (Button) findViewById(R.id.btn_fingerprintquery);
		btn_connect.setOnClickListener(this);
		btn_disconnect.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		btn_reset.setOnClickListener(this);
		btn_fingerprintrecord.setOnClickListener(this);
		btn_fingerprintquery.setOnClickListener(this);
		tv_result = (TextView) findViewById(R.id.tv_result);
		// tv_information = (TextView) findViewById(R.id.tv_information);
		setBtnEnabledFalse();
	}

	private void initfingerprintrecognition() {
		// TODO Auto-generated method stub
		fingerprintRecognitionController = BRFingerprintController
				.getInstance();
	}

	ProgressDialog MyDialog = null;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		MyDialog = ProgressDialog.show(BRFingerprintControllerActivity.this,
//				" ", " Loading. Please wait ... ", true);
//		MyDialog.show();
		fingerprintRecognitionController.FingerprintController_Close();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_connect:
			connect();
			break;
		case R.id.btn_disconnect:
			disconnect();
			break;
		case R.id.btn_delete:
			new Thread() {
				public void run() {
					delete();
				};
			}.start();
			break;
		case R.id.btn_clear:
			new Thread() {
				public void run() {
					clear();
				};
			}.start();
			break;
		case R.id.btn_fingerprintrecord:
			fingerprintrecord();
			break;
		case R.id.btn_fingerprintquery:
			fingerprintquery();
			break;
		case R.id.btn_fingerreset:
			new Thread() {
				public void run() {
					fingerreset();
				};
			}.start();
			break;
		default:
			break;
		}
	}

	private void fingerreset() {
		// TODO Auto-generated method stub
		if (fingerprintRecognitionController.FingerprintController_Reset() == 0) {
			Message message = new Message();
			message.what = 1;
			message.obj = getResources().getString(
					R.string.fingerprintre_string_reset_success);
			handler.sendMessage(message);
		} else {
			Message message = new Message();
			message.what = 1;
			message.obj = getResources().getString(
					R.string.fingerprintre_string_reset_failed);
			handler.sendMessage(message);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				String str = (String) msg.obj;
				tv_result.setText(str);
				break;
			}
		};
	};

	// 指纹识别
	private void fingerprintquery() {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 1;
		message.obj = getResources().getString(
				R.string.fingerprintre_string_finger);
		handler.sendMessage(message);
		new Thread() {
			public void run() {
				int ret = -1;
				while (true) {
					if (fingerprintRecognitionController
							.FingerprintController_ProbeFinger() == 0) {
						ret = 0;
						break;
					}
				}
				Message message = new Message();
				if (0 == ret) {
					message = new Message();
					message.what = 1;
					message.obj = getResources().getString(
							R.string.fingerprintre_string_wait);
					handler.sendMessage(message);
					ret = fingerprintRecognitionController
							.FingerprintController_FingerprintQuery();
				}
				if (ret == -1) {
					message = new Message();
					message.what = 1;
					message.obj = getResources().getString(
							R.string.fingerprintre_string_fingerprint_failed);
					handler.sendMessage(message);
				} else {
					switch (ret) {
					case CMD_RT_OK:
						sendMessage(getResources()
								.getString(
										R.string.fingerprintre_string_recognition_success));
						break; // 指令执行完毕或OK
					case CMD_RT_PACKGE_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_PACKGE_ERR));
						break; // 数据包接收错误
					case CMD_RT_DEVICE_ADDRESS_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_DEVICE_ADDRESS_ERR));
						break; // 设备地址错误
					case CMD_RT_COM_PASSWORD_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_COM_PASSWORD_ERR));
						break; // 通信密码错误
					case CMD_RT_NO_FINGER:
						sendMessage(getResources().getString(
								R.string.CMD_RT_NO_FINGER));
						break; // 传感器上没有手指
					case CMD_RT_GET_IMAGE_FAILE:
						sendMessage(getResources().getString(
								R.string.CMD_RT_GET_IMAGE_FAILE));
						break; // 从传感器上获取图像失败
					case CMD_RT_GEN_CHAR_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_GEN_CHAR_ERR));
						break; // 生成特征失败
					case CMD_RT_FINGER_MATCH_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_FINGER_MATCH_ERR));
						break; // 指纹不匹配
					case CMD_RT_FINGER_SEARCH_FAILE:
						sendMessage(getResources().getString(
								R.string.CMD_RT_FINGER_SEARCH_FAILE));
						break; // 没搜索到指纹
					case CMD_RT_MERGE_TEMPLET_FAILE:
						sendMessage(getResources().getString(
								R.string.CMD_RT_MERGE_TEMPLET_FAILE));
						break; // 特征合并失败
					case CMD_RT_ADDRESS_OVERFLOW:
						sendMessage(getResources().getString(
								R.string.CMD_RT_ADDRESS_OVERFLOW));
						break; // 将模板存库时地址序号超出指纹库范围
					case CMD_RT_READ_TEMPLET_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_READ_TEMPLET_ERR));
						break; // 从指纹库读模板出错
					case CMD_RT_UP_TEMPLET_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_UP_TEMPLET_ERR));
						break; // 上传特征失败
					case CMD_RT_UP_IMAGE_FAILE:
						sendMessage(getResources().getString(
								R.string.CMD_RT_UP_IMAGE_FAILE));
						break; // 上传图像失败
					case CMD_RT_DELETE_TEMPLET_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_DELETE_TEMPLET_ERR));
						break; // 删除模板失败
					case CMD_RT_CLEAR_TEMPLET_LIB_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_CLEAR_TEMPLET_LIB_ERR));
						break; // 清空指纹库失败
					case CMD_RT_FINGER_NOT_MOVE:
						sendMessage(getResources().getString(
								R.string.CMD_RT_FINGER_NOT_MOVE));
						break; // 残留指纹或传感器窗口的按指长时间未移开
					case CMD_RT_NO_TEMPLET_IN_ADDRESS:
						sendMessage(getResources().getString(
								R.string.CMD_RT_NO_TEMPLET_IN_ADDRESS));
						break; // 指定位置没有有效模板
					case CMD_RT_CHAR_REPEAT:
						sendMessage(getResources().getString(
								R.string.CMD_RT_CHAR_REPEAT));
						break; // 指纹重复，需要注册的指纹已经在FLASH中注册
					case CMD_RT_MB_NOT_EXIST_IN_ADDRESS:
						sendMessage(getResources().getString(
								R.string.CMD_RT_MB_NOT_EXIST_IN_ADDRESS));
						break; // 该地址中不存在指纹模板
					case CMD_RT_GET_MBINDEX_OVERFLOW:
						sendMessage(getResources().getString(
								R.string.CMD_RT_GET_MBINDEX_OVERFLOW));
						break; // 获取模板索引长度溢出
					default:
						break;
					}
				}
			};
		}.start();
	}

	// 指纹录入
	private void fingerprintrecord() {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 1;
		message.obj = getResources().getString(
				R.string.fingerprintre_string_finger);
		handler.sendMessage(message);
		new Thread() {
			public void run() {
				int ret = -1;
				while (true) {
					if (fingerprintRecognitionController
							.FingerprintController_ProbeFinger() == 0) {
						ret = 0;
						break;
					}
				}
				Message message = new Message();
				if (0 == ret) {
					message = new Message();
					message.what = 1;
					message.obj = getResources().getString(
							R.string.fingerprintre_string_wait);
					handler.sendMessage(message);
					ret = fingerprintRecognitionController
							.FingerprintController_FingerprintRecord();
				}
				if (-2 == ret) {
					message = new Message();
					message.what = 1;
					message.obj = getResources().getString(
							R.string.fingerprintre_string_full);
					handler.sendMessage(message);
				} else if (-1 == ret) {
					message = new Message();
					message.what = 1;
					message.obj = getResources().getString(
							R.string.fingerprintre_string_input_failed);
					handler.sendMessage(message);
				} else {
					switch (ret) {
					case CMD_RT_OK:
						sendMessage(getResources().getString(
								R.string.fingerprintre_string_input_success));
						break; // 指令执行完毕或OK
					case CMD_RT_PACKGE_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_PACKGE_ERR));
						break; // 数据包接收错误
					case CMD_RT_DEVICE_ADDRESS_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_DEVICE_ADDRESS_ERR));
						break; // 设备地址错误
					case CMD_RT_COM_PASSWORD_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_COM_PASSWORD_ERR));
						break; // 通信密码错误
					case CMD_RT_NO_FINGER:
						sendMessage(getResources().getString(
								R.string.CMD_RT_NO_FINGER));
						break; // 传感器上没有手指
					case CMD_RT_GET_IMAGE_FAILE:
						sendMessage(getResources().getString(
								R.string.CMD_RT_GET_IMAGE_FAILE));
						break; // 从传感器上获取图像失败
					case CMD_RT_GEN_CHAR_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_GEN_CHAR_ERR));
						break; // 生成特征失败
					case CMD_RT_FINGER_MATCH_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_FINGER_MATCH_ERR));
						break; // 指纹不匹配
					case CMD_RT_FINGER_SEARCH_FAILE:
						sendMessage(getResources().getString(
								R.string.CMD_RT_FINGER_SEARCH_FAILE));
						break; // 没搜索到指纹
					case CMD_RT_MERGE_TEMPLET_FAILE:
						sendMessage(getResources().getString(
								R.string.CMD_RT_MERGE_TEMPLET_FAILE));
						break; // 特征合并失败
					case CMD_RT_ADDRESS_OVERFLOW:
						sendMessage(getResources().getString(
								R.string.CMD_RT_ADDRESS_OVERFLOW));
						break; // 将模板存库时地址序号超出指纹库范围
					case CMD_RT_READ_TEMPLET_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_READ_TEMPLET_ERR));
						break; // 从指纹库读模板出错
					case CMD_RT_UP_TEMPLET_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_UP_TEMPLET_ERR));
						break; // 上传特征失败
					case CMD_RT_UP_IMAGE_FAILE:
						sendMessage(getResources().getString(
								R.string.CMD_RT_UP_IMAGE_FAILE));
						break; // 上传图像失败
					case CMD_RT_DELETE_TEMPLET_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_DELETE_TEMPLET_ERR));
						break; // 删除模板失败
					case CMD_RT_CLEAR_TEMPLET_LIB_ERR:
						sendMessage(getResources().getString(
								R.string.CMD_RT_CLEAR_TEMPLET_LIB_ERR));
						break; // 清空指纹库失败
					case CMD_RT_FINGER_NOT_MOVE:
						sendMessage(getResources().getString(
								R.string.CMD_RT_FINGER_NOT_MOVE));
						break; // 残留指纹或传感器窗口的按指长时间未移开
					case CMD_RT_NO_TEMPLET_IN_ADDRESS:
						sendMessage(getResources().getString(
								R.string.CMD_RT_NO_TEMPLET_IN_ADDRESS));
						break; // 指定位置没有有效模板
					case CMD_RT_CHAR_REPEAT:
						sendMessage(getResources().getString(
								R.string.CMD_RT_CHAR_REPEAT));
						break; // 指纹重复，需要注册的指纹已经在FLASH中注册
					case CMD_RT_MB_NOT_EXIST_IN_ADDRESS:
						sendMessage(getResources().getString(
								R.string.CMD_RT_MB_NOT_EXIST_IN_ADDRESS));
						break; // 该地址中不存在指纹模板
					case CMD_RT_GET_MBINDEX_OVERFLOW:
						sendMessage(getResources().getString(
								R.string.CMD_RT_GET_MBINDEX_OVERFLOW));
						break; // 获取模板索引长度溢出
					default:
						break;
					}

				}
			};
		}.start();
	}

	private void sendMessage(String str) {
		Message message = new Message();
		message.what = 1;
		message.obj = str;
		handler.sendMessage(message);
	}

	private void clear() {
		// TODO Auto-generated method stub
		if (fingerprintRecognitionController
				.FingerprintController_FingerprintClear() == 0) {
			Message message = new Message();
			message.what = 1;
			message.obj = getResources().getString(
					R.string.fingerprintre_string_empty_success);
			handler.sendMessage(message);
		} else {
			Message message = new Message();
			message.what = 1;
			message.obj = getResources().getString(
					R.string.fingerprintre_string_empty_failed);
			handler.sendMessage(message);
		}
	}

	private void delete() {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 1;
		message.obj = getResources().getString(
				R.string.fingerprintre_string_finger);
		handler.sendMessage(message);
		new Thread() {
			@SuppressWarnings("unused")
			public void run() {
				int ret = -1;
				while (true) {
					if (fingerprintRecognitionController
							.FingerprintController_ProbeFinger() == 0) {
						ret = 0;
						break;
					}
				}
				Message message = new Message();
				int n = -2;
				if (0 == ret) {
					message = new Message();
					message.what = 1;
					message.obj = getResources().getString(
							R.string.fingerprintre_string_wait);
					handler.sendMessage(message);
				}
				if (fingerprintRecognitionController
						.FingerprintController_FingerprintDelete() == 0) {
					message = new Message();
					message.what = 1;
					message.obj = getResources().getString(
							R.string.fingerprintre_string_deleted_successfully);
					handler.sendMessage(message);
				} else {
					message = new Message();
					message.what = 1;
					message.obj = getResources().getString(
							R.string.fingerprintre_string_deletion_failed);
					handler.sendMessage(message);
				}
			};
		}.start();
	}

	private void disconnect() {
		// TODO Auto-generated method stub
		if (fingerprintRecognitionController.FingerprintController_Close() == 0) {
			setBtnEnabledFalse();
			tv_result.setText("");
			Toast.makeText(this, "disconnect_success", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, "disconnect_failure", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void connect() {
		// TODO Auto-generated method stub
		if (fingerprintRecognitionController.FingerprintController_Open(this) == 0) {
			setBtnEnabledTrue();
			Toast.makeText(this, "connect_success", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "connect_failure", Toast.LENGTH_SHORT).show();
		}
	}
}
