package com.POS.apis.PSAMController;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android_jb.com.POSD.controllers.PSAMController;
import android_jb.com.POSD.controllers.Tools;
import jepower.com.t508ac_demo.R;

public class PSAMControllerActivity extends Activity implements OnClickListener {
	private Button btn_connect;
	private Button btn_disconnect;
	private Button btn_reset;
	private Button btn_hz;
	private Spinner sp_hz;
	private Spinner sp_psam;
	private ArrayAdapter<String> adapter_hz;
	private ArrayAdapter<String> adapter_psam;
	private PSAMController psamController = null;

	private Button btn_read;
	private EditText et_send;
	private TextView tv_read;
	private Button btn_send;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.psam_controller_layout);
		initview();
		initpsam();
	}

	private void setViewEnabledFalse() {
		btn_connect.setEnabled(true);
		btn_disconnect.setEnabled(false);
		btn_reset.setEnabled(false);
		btn_hz.setEnabled(false);
		sp_hz.setEnabled(false);

		btn_read.setEnabled(false);
		btn_send.setEnabled(false);
		et_send.setEnabled(false);
		et_send.setClickable(false);

	}

	private void setViewEnabledTrue() {
		btn_connect.setEnabled(false);
		btn_disconnect.setEnabled(true);
		btn_reset.setEnabled(true);
		btn_hz.setEnabled(true);
		sp_hz.setEnabled(true);

		btn_read.setEnabled(true);
		btn_send.setEnabled(true);
		et_send.setEnabled(true);
		et_send.setClickable(true);
	}

	private void initview() {
		// TODO Auto-generated method stub
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
		btn_reset = (Button) findViewById(R.id.btn_reset);
		btn_hz = (Button) findViewById(R.id.btn_hz);
		btn_connect.setOnClickListener(this);
		btn_disconnect.setOnClickListener(this);
		btn_reset.setOnClickListener(this);
		btn_hz.setOnClickListener(this);

		sp_hz = (Spinner) findViewById(R.id.sp_hz);
		adapter_hz = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.hz));
		adapter_hz
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_hz.setAdapter(adapter_hz);

		sp_psam = (Spinner) findViewById(R.id.sp_psam);
		adapter_psam = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.psam));
		adapter_psam
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_psam.setAdapter(adapter_psam);

		btn_read = (Button) findViewById(R.id.btn_read);
		et_send = (EditText) findViewById(R.id.et_send);
		tv_read = (TextView) findViewById(R.id.tv_read);
		btn_send = (Button) findViewById(R.id.btn_send);
		btn_send.setOnClickListener(this);
		btn_read.setOnClickListener(this);

		setViewEnabledFalse();
	}

	private void initpsam() {
		// TODO Auto-generated method stub
		psamController = PSAMController.getInstance();
	}

	private String clearBlank(String str) {
		String dest = "";
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(str);
		dest = m.replaceAll("");
		return dest;
	}

	private int hz_int = 0;

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_connect:
			int flagO = psamController.PSAM_Open();
			if (flagO == 0) {
				setViewEnabledTrue();
				Toast.makeText(this, "PSAM_Open_Success", Toast.LENGTH_SHORT)
						.show();
			} else
				Toast.makeText(this, "PSAM_Open_Failure", Toast.LENGTH_SHORT)
						.show();
			break;
		case R.id.btn_disconnect:
			int flagC = psamController.PSAM_Close();
			if (flagC == 0) {
				setViewEnabledFalse();
				Toast.makeText(this, "PSAM_Close_Success", Toast.LENGTH_SHORT)
						.show();
			} else
				Toast.makeText(this, "PSAM_Close_Failure", Toast.LENGTH_SHORT)
						.show();
			break;
		case R.id.btn_reset:
			String spstr = new String();
			spstr = clearBlank(sp_psam.getSelectedItem().toString().trim());
			if (spstr.equals("psam1")) {
				reset(1);
			} else if (spstr.equals("psam2")) {
				reset(2);
			}
			break;
		case R.id.btn_hz:
			String spstr1 = new String();
			spstr1 = clearBlank(sp_hz.getSelectedItem().toString().trim());
			if ('1' == spstr1.charAt(0)) {
				if ('2' == spstr1.charAt(1)) {
					hz_int = 12;
				} else if ('M' == spstr1.charAt(1)) {
					hz_int = 1;
				}
			} else {
				hz_int = spstr1.charAt(0) - 48;
			}
			changeHz(hz_int);
			break;
		case R.id.btn_send:
			tv_read.setText("");
			byte[] createRandomCom = new byte[] { 0x00, (byte) 0x84, 0x00,
					0x00, 0x04 };// 产生随机数指令
			if ("".equals(et_send.getText().toString())) {
				String str = new String();
				str = clearBlank(sp_psam.getSelectedItem().toString().trim());
				if (str.equals("psam1")) {
					try {
						byte[] data = psamController.PSAM_executeCosMommand(0,
								createRandomCom);
						if (null == data) {
							tv_read.setText("PSAM" + 1 + ": Random_Failure");
						} else {
							byte[] random = new byte[data.length - 6];
							for (int i = 0; i < data.length - 6; i++) {
								random[i] = data[i + 5];
							}
							String strr = bytesToHexString123(random);
							tv_read.setText("PSAM" + 1 + ": " + strr);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (str.equals("psam2")) {
					try {
						byte[] data = psamController.PSAM_executeCosMommand(1,
								createRandomCom);
						if (null == data) {
							tv_read.setText("PSAM" + 2 + ": Random_Failure");
						} else {
							byte[] random = new byte[data.length - 6];
							for (int i = 0; i < data.length - 6; i++) {
								random[i] = data[i + 5];
							}
							String strr = bytesToHexString123(random);
							tv_read.setText("PSAM" + 2 + ": " + strr);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				String str = clearBlank(sp_psam.getSelectedItem().toString()
						.trim());
				if (str.equals("psam1")) {
					try {
						byte[] data = psamController
								.PSAM_executeCosMommand(0, parseHexStr(et_send
										.getText().toString().trim()));
						if (null == data) {
							tv_read.setText("PSAM" + 2 + ": Random_Failure");
						} else {
							byte[] random = new byte[data.length - 6];
							for (int i = 0; i < data.length - 6; i++) {
								random[i] = data[i + 5];
							}
							String strr = bytesToHexString123(random);
							tv_read.setText("PSAM" + 1 + ": " + strr);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (str.equals("psam2")) {
					try {
						byte[] sends = parseHexStr(et_send.getText().toString()
								.trim());
						System.out.println(Tools.bytesToHexString(sends));
						byte[] data = psamController
								.PSAM_executeCosMommand(1, parseHexStr(et_send
										.getText().toString().trim()));
						if (null == data) {
							tv_read.setText("PSAM" + 2 + ": Random_Failure");
						} else {
							byte[] random = new byte[data.length - 6];
							for (int i = 0; i < data.length - 6; i++) {
								random[i] = data[i + 5];
							}
							String strr = bytesToHexString123(random);
							tv_read.setText("PSAM" + 2 + ": " + strr);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			break;
		case R.id.btn_read:
			byte[] str = psamController.PSAM_Read();
			if (str != null)
				tv_read.setText(str.toString());
			break;
		default:
			break;
		}
	}

	private void changeHz(int hz_int) {
		try {
			byte[] sourceByteArr = new byte[7];
			sourceByteArr[0] = (byte) 0xAA;
			sourceByteArr[1] = (byte) 0x66;
			sourceByteArr[2] = (byte) 0x00;
			sourceByteArr[3] = (byte) 0x04;
			sourceByteArr[4] = (byte) 0x36;
			sourceByteArr[5] = (byte) (hz_int & 0xFF);
			sourceByteArr[6] = (byte) (((sourceByteArr[2] & 0xFF)
					+ (sourceByteArr[3] & 0xFF) + (sourceByteArr[4] & 0xFF) + (sourceByteArr[5] & 0xFF)) & 0xFF);// 校验位
			psamController.PSAM_Write(sourceByteArr);
			byte[] buffer = psamController.PSAM_Read();
			// byte[] buffer = psamController.PSAM_CMD((byte) 0x36,
			// new byte[] { (byte) (hz_int & 0xFF) });
			if (null != buffer && buffer.length > 0) {
				if (buffer.length >= 5) {
					if (buffer[4] == (byte) 0x36) {
						Toast.makeText(this,
								"Change_Hz " + hz_int + "M Success",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(this, "Change_Hz_Failure",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(this, "Change_Hz_Failure",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "Change_Hz_Failure", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void reset(int n) {
		// TODO Auto-generated method stub
		int flag = psamController.PSAM_Reset(n);
		if (flag == 0) {
			Toast.makeText(this, "PSAM" + n + "Reset_Success",
					Toast.LENGTH_SHORT).show();
		} else if (flag == -1) {
			Toast.makeText(this, "PSAM" + n + "Reset_Failure",
					Toast.LENGTH_SHORT).show();
		}
	}

	// public static final byte[] random1 = { (byte) 0xAA, 0x66, 0x00, 0x09,
	// 0x38,
	// 0x00, 0x00, (byte) 0x84, 0x00, 0x00, 0x04, (byte) 0xC9 };
	// public static final byte[] random2 = { (byte) 0xAA, 0x66, 0x00, 0x09,
	// 0x38,
	// 0x01, 0x00, (byte) 0x84, 0x00, 0x00, 0x04, (byte) 0xCA };
	//
	// private void random(int n) {
	// // TODO Auto-generated method stub
	// if (n == 1)
	// psamController.PSAM_Write(random1);
	// else if (n == 2)
	// psamController.PSAM_Write(random2);
	// try {
	// Thread.sleep(300);
	// byte[] data = psamController.PSAM_Read();
	// if (null == data) {
	// tv_read.setText("PSAM" + n + " Random_Failure");
	// } else {
	// byte[] random = new byte[data.length - 6];
	// for (int i = 0; i < data.length - 6; i++) {
	// random[i] = data[i + 5];
	// }
	// String str = bytesToHexString123(random);
	// tv_read.setText("PSAM" + n + " " + str);
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	private byte[] parseHexStr(String cosCommandStr) {
		// TODO Auto-generated method stub

		if (cosCommandStr != null && !cosCommandStr.trim().equals("")) {
			if (cosCommandStr.contains(",")) {
				String[] commandArr = cosCommandStr.split(",");
				byte[] command = new byte[commandArr.length];
				for (int i = 0; i < commandArr.length; i++) {
					command[i] = hexStrToByte(commandArr[i]);
				}
				return command;
			}
		}
		return null;
	}

	private byte hexStrToByte(String string) {
		// TODO Auto-generated method stub
		int value = 0;
		if (string != null) {
			switch (string.length()) {
			case 1:
				value = getValueByHexChar(string.charAt(0));
				break;
			case 2:
				value = getValueByHexChar(string.charAt(1));
				value += getValueByHexChar(string.charAt(0)) * 16;
				break;
			default:
				break;
			}
		}
		return (byte) (value & 0xFF);
	}

	private int getValueByHexChar(char charAt) {
		// TODO Auto-generated method stub
		int i = 0;
		if (charAt >= '0' && charAt <= '9') {
			i = charAt - '0';
		} else if (charAt >= 'A' && charAt <= 'F') {
			i = charAt - 'A' + 10;
		} else if (charAt >= 'a' && charAt <= 'f') {
			i = charAt - 'a' + 10;
		}
		return i;
	}

	private String bytesToHexString123(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);

			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	@Override
	protected void onDestroy() {
		psamController.PSAM_Close();
		super.onDestroy();
	}
}
