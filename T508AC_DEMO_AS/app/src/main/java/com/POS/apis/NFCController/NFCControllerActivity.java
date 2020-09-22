package com.POS.apis.NFCController;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.ByteBuffer;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import jepower.com.t508ac_demo.R;

@SuppressLint("HandlerLeak")
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class NFCControllerActivity extends Activity {
	private TextView tv_cardtype;
	private TextView tv_cardid;
	private TextView tv_carddata;
	private NfcAdapter nfcAdapter;
	private PendingIntent mPendingIntent;
	private Tag tag;
	@SuppressWarnings("unused")
	private boolean canWrite = true;
	@SuppressWarnings("unused")
	private int cardType = CardType.unknow;
	@SuppressWarnings("unused")
	private static final String[] blocks = new String[] { "0", "1", "2" };
	@SuppressWarnings("unused")
	private byte[] key = { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
			(byte) 0xff, (byte) 0xff };
	private String mes = "";
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mes = msg.obj.toString();
				tv_readwrite.setText(mes);
				sv_sll.fullScroll(ScrollView.FOCUS_DOWN);
				break;
			case 2:
				tv_readwrite.setText("");
				break;
			}
		};
	};
	private TextView tv_readwrite;
	private ScrollView sv_sll;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_controller_layout);
		initview();
		initnfc();
	}

	@SuppressLint("NewApi")
	private void initnfc() {
		// TODO Auto-generated method stub
		nfcAdapter = NfcAdapter.getDefaultAdapter(NFCControllerActivity.this);
		if (nfcAdapter == null) {
			Toast.makeText(this, "nfc_device_not_support", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
		if (!nfcAdapter.isEnabled()) {
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setMessage(getString(R.string.nfc_not_open_go_open))
					.setPositiveButton(getString(R.string.yes),
							new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											"android.settings.NFC_SETTINGS");
									startActivityForResult(intent, 100);
								}
							}).create();
			dialog.show();		
		}
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()), 0);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null,
					null);
		}
	}

	 public static int byteToInt(byte[] b) {

	        int s = 0;

	        int s0 = b[0] & 0xff;// 最低位

	        int s1 = b[1] & 0xff;

	        int s2 = b[2] & 0xff;

	        int s3 = b[3] & 0xff;

	        s3 <<= 24;

	        s2 <<= 16;

	        s1 <<= 8;

	        s = s0 | s1 | s2 | s3;

	        return s;

	    }

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi")
	protected void onNewIntent(Intent intent) {
		Message message = new Message();
		message.what = 2;
		handler.sendMessage(message);
		tag = intent.getParcelableExtra(nfcAdapter.EXTRA_TAG);
		if (tag == null)
			return;
		@SuppressWarnings("unused")
		final byte[] id = tag.getId();

		String str = bytesToHexString(tag.getId());
		if (tv_cardid != null)
			tv_cardid.setText(str);

		if (tv_carddata != null) {
			tv_carddata.setText("");
		}
		String[] techList = tag.getTechList();
		String gsm = "";
		if (techList != null && techList.length > 0) {
			if (tv_cardtype != null) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < techList.length; i++) {

					sb.append((i + 1)
							+ " "
							+ techList[i].subSequence(
									techList[i].lastIndexOf(".") + 1,
									techList[i].length()));
					gsm += techList[i].subSequence(
							techList[i].lastIndexOf(".") + 1,
							techList[i].length());
					sb.append("\n");
				}
				tv_cardtype.setText(sb.subSequence(0, sb.length() - 1));
				if (gsm.equals("MifareClassicNfcANdefFormatable")) {
					WriteReadTag();
				} else {
					Message message2 = new Message();
					message.what = 2;
					handler.sendMessage(message2);
				}
			}

			if (techList[0].contains("MifareClassic")) {
				Card.mMifareClassic = MifareClassic.get(tag);
				cardType = CardType.MifareClassic;
				tv_carddata.setText(getString(R.string.nfc_block_count)
						+ Card.mMifareClassic.getBlockCount() + "\n"
						+ getString(R.string.nfc_max_command_len)
						+ Card.mMifareClassic.getMaxTransceiveLength());
			} else if (techList[0].contains("MifareUltralight")) {
				Card.mMifareUltralight = MifareUltralight.get(tag);
				cardType = CardType.MifareUltralight;
				tv_carddata.setText("\n"
						+ getString(R.string.nfc_max_command_len)
						+ Card.mMifareUltralight.getMaxTransceiveLength());
			} else if (techList[0].contains("NdefFormatable")) {
				Card.mNdefFormatable = NdefFormatable.get(tag);
				cardType = CardType.NdefFormatable;
			} else if (techList[0].contains("Ndef")) {
				Card.mNdef = Ndef.get(tag);
				cardType = CardType.Ndef;
				// Card.mNfcF.transceive(data)
			} else if (techList[0].contains("NfcV")) {
				Card.mNfcV = NfcV.get(tag);
				cardType = CardType.NfcV;
				tv_carddata
						.setText("DSF ID:"
								+ bytesToHexString(new byte[] { Card.mNfcV
										.getDsfId() }));
			} else if (techList[0].contains("NfcF")) {
				Card.mNfcF = NfcF.get(tag);
				cardType = CardType.NfcF;
				try {
					tv_carddata
							.setText(getString(R.string.nfc_manu)
									+ new String(Card.mNfcF.getManufacturer(),
											"GB2312") + "\n"
									+ getString(R.string.nfc_max_command_len)
									+ Card.mNfcF.getMaxTransceiveLength());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			} else if (techList[0].contains("NfcB")) {
				Card.mNfcB = NfcB.get(tag);
				cardType = CardType.NfcB;
				try {
					tv_carddata
							.setText(getString(R.string.nfc_prof)
									+ new String(Card.mNfcB.getProtocolInfo(),
											"GB2312") + "\n"
									+ getString(R.string.nfc_max_command_len)
									+ Card.mNfcB.getMaxTransceiveLength());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} else if (techList[0].contains("NfcA")) {
				Card.mNfcA = NfcA.get(tag);
				cardType = CardType.NfcA;
				tv_carddata.setText("ATQA:"
						+ bytesToHexString(Card.mNfcA.getAtqa()) + "\nSak:"
						+ Card.mNfcA.getSak() + "\n"
						+ getString(R.string.nfc_max_command_len)
						+ Card.mNfcA.getMaxTransceiveLength());

				try {
					Card.mNfcA.connect();
					Card.mNfcA.transceive(new byte[] { 0x00 });
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (techList[0].contains("IsoDep")) {
				Card.mIsoDep = IsoDep.get(tag);
				cardType = CardType.IsoDep;
				StringBuilder sb = new StringBuilder();
				sb.append(getString(R.string.nfc_max_command_len)
						+ Card.mIsoDep.getMaxTransceiveLength());
				try {
					Card.mIsoDep.connect();
					if (Card.mIsoDep.isConnected()) {

						// select the card manager applet
						byte[] mf = { (byte) '1', (byte) 'P', (byte) 'A',
								(byte) 'Y', (byte) '.', (byte) 'S', (byte) 'Y',
								(byte) 'S', (byte) '.', (byte) 'D', (byte) 'D',
								(byte) 'F', (byte) '0', (byte) '1', };
						byte[] mfRsp = Card.mIsoDep
								.transceive(getSelectCommand(mf));
						Log.d("test", "mfRsp:" + bytesToHexString(mfRsp));
						// select Main Application
						byte[] szt = { (byte) 'P', (byte) 'A', (byte) 'Y',
								(byte) '.', (byte) 'S', (byte) 'Z', (byte) 'T' };
						byte[] sztRsp = Card.mIsoDep
								.transceive(getSelectCommand(szt));
						Log.d("test", "sztRsp:" + bytesToHexString(sztRsp));

						byte[] balance = { (byte) 0x80, (byte) 0x5C, 0x00,
								0x02, 0x04 };
						byte[] balanceRsp = Card.mIsoDep.transceive(balance);
						Log.d("test", "balanceRsp:"
								+ bytesToHexString(balanceRsp));
						if (balanceRsp != null && balanceRsp.length > 4) {
							int cash = byteToInt(balanceRsp, 4);
							float ba = cash / 100.0f;
							if (tv_carddata != null) {
								sb.append("\n"
										+ getString(R.string.nfc_szt_money)
										+ ba);
							}
						}
					}
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				tv_carddata.setText(sb.toString());
			} else if (techList[0].contains("NfcBarcode")) {
				// Card.mNfcBarcode = NfcBarcode.get(tag);
				// cardType = CardType.NfcBarcode;
				cardType = CardType.unknow;
			} else {
				cardType = CardType.unknow;
			}
		}
	}

	private byte[] getSelectCommand(byte[] aid) {
		final ByteBuffer cmd_pse = ByteBuffer.allocate(aid.length + 6);
		cmd_pse.put((byte) 0x00) // CLA Class
				.put((byte) 0xA4) // INS Instruction
				.put((byte) 0x04) // P1 Parameter 1
				.put((byte) 0x00) // P2 Parameter 2
				.put((byte) aid.length) // Lc
				.put(aid).put((byte) 0x00); // Le
		return cmd_pse.array();
	}

	private int byteToInt(byte[] b, int n) {
		int ret = 0;
		for (int i = 0; i < n; i++) {
			ret = ret << 8;
			ret |= b[i] & 0x00FF;
		}
		if (ret > 100000 || ret < -100000)
			ret -= 0x80000000;
		return ret;
	}

	private String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			System.out.println(buffer);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString();
	}

	@SuppressWarnings("unused")
	@SuppressLint("NewApi")
	public void WriteReadTag() {

		MifareClassic mfc = MifareClassic.get(tag);
		String msg = "";
		try {
			mfc.connect();
			byte[] data = null;
			boolean auth = false;
			short sectorAddress = 1;
			auth = mfc.authenticateSectorWithKeyA(2, MifareClassic.KEY_DEFAULT);
			if (auth) {
				data = mfc.readBlock(8);
				msg += "read Sector 2 Block 8 data =" + bytesToHexString(data)
						+ "\n";
				String wd1 = "12345678901234567890123456789011";
				String wd2 = "11111111111234567890111111111111";
				if ("12345678901234567890123456789011"
						.equals(bytesToHexString(data))) {
					byte[] data1 = Tools.hexString2Bytes(wd2);
					mfc.writeBlock(8, data1);
					msg += "write Sector 2 Block 8 data =" + wd2 + "\n";
				} else {
					byte[] data2 = Tools.hexString2Bytes(wd1);
					mfc.writeBlock(8, data2);
					msg += "write Sector 2 Block 8 data =" + wd1 + "\n";
				}
				byte[] data3 = mfc.readBlock(8);
				msg += "read Sector 2 Block 8 data =" + bytesToHexString(data3)
						+ "\n";
			}
			mfc.close();
			count++;
			if ("" != msg) {
				Message message = new Message();
				message.what = 1;
				message.obj = count + ".\n" + msg;
				handler.sendMessage(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				mfc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			nfcAdapter = NfcAdapter.getDefaultAdapter(this);
			if (!nfcAdapter.isEnabled()) {
				Toast.makeText(this, "nfc_not_open", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void initview() {
		tv_cardtype = (TextView) findViewById(R.id.tv_cardtype);
		tv_cardid = (TextView) findViewById(R.id.tv_cardid);
		tv_carddata = (TextView) findViewById(R.id.tv_carddata);
		tv_readwrite = (TextView) findViewById(R.id.tv_readwrite);
		sv_sll = (ScrollView) findViewById(R.id.sv_sll);
	}
}
