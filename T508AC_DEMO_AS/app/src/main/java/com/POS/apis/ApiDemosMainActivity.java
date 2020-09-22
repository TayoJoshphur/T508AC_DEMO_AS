package com.POS.apis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import com.POS.apis.BuzzerController.BuzzerControllerActivity;
import com.POS.apis.CaptureController.*;
import com.POS.apis.CashboxController.CashboxControllerActivity;
import com.POS.apis.FingerprintController.BRFingerprintControllerActivity;
import com.POS.apis.FingerprintController.FingerPrintScanActivity;
import com.POS.apis.FingerprintController.ZAFingerprintControllerActivity;
import com.POS.apis.ICcardController.ICcardControllerActivity;
import com.POS.apis.IDcardController.IDcardControllerActivity;
import com.POS.apis.LaserlightController.LaserlightControllerActivity;
import com.POS.apis.LedControllers.LedControllerActivity;
import com.POS.apis.MagnetCardController.MagnetCardControllerActivity;
import com.POS.apis.NFCController.NFCControllerActivity;
import com.POS.apis.PINPadController.N20PINPadControllerActivity;
import com.POS.apis.PINPadController.PP300PinPadControllerActivity;
import com.POS.apis.PINPadController.SP10PINPadControllerActivity;
import com.POS.apis.PINPadController.VanstoneV10Activity;
import com.POS.apis.PSAMController.PSAMControllerActivity;
import com.POS.apis.PrinterBluetooth.BluetoothActivity;
import com.POS.apis.PrinterController.PrinterControllerActivity;
import com.POS.apis.RS232Controller.*;
import com.POS.apis.ScanController.ScanActivity;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import android_jb.com.POSD.Win8View.Win8View;
import android_jb.com.POSD.util.MachineVersion;
import jepower.com.t508ac_demo.R;

public class ApiDemosMainActivity extends Activity implements
		Win8View.OnViewClickListener {
	private String vstr;
	private boolean language;
	private boolean imagexz = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		vstr = MachineVersion.getMachineVersion();
		if (vstr.equals("J001(D)")) {
			setContentView(R.layout.j001_apidemo_main_layout);
		} else if (vstr.equals("T001(Q)")) {
			setContentView(R.layout.t001_apidemo_main_layout);
			imagexz = true;
		} else {
			setContentView(R.layout.j001_apidemo_main_layout);
		}
		data = getData();
		language = isZh();
		initview();
	}

	private void initwv(int Rd) {
		Win8View wv = (Win8View) findViewById(Rd);
		if (language) {
		} else {
			switch (Rd) {
			case R.id.wv_buzzer:
				wv.setImageResource(R.drawable.wv_buzzer_e);
				break;
			case R.id.wv_laserlight:
				if (imagexz) {
					wv.setImageResource(R.drawable.wv_laserlight_e);
				} else {
					wv.setImageResource(R.drawable.wvlaserlight2_e);
				}
				break;
			case R.id.wv_cashbox:
				wv.setImageResource(R.drawable.wv_cashbox_e);
				break;
			case R.id.wv_rs232:
				wv.setImageResource(R.drawable.wv_rs232_e);
				break;
			case R.id.wv_idcard:
				wv.setImageResource(R.drawable.wv_idcard_e);
				break;
			case R.id.wv_pinpad:
				wv.setImageResource(R.drawable.wv_pinpad_e);
				break;
			case R.id.wv_psam:
				wv.setImageResource(R.drawable.wv_psam_e);
				break;
			case R.id.wv_iccard:
				wv.setImageResource(R.drawable.wv_iccard_e);
				break;
			case R.id.wv_nfc:
				wv.setImageResource(R.drawable.wv_nfc_e);
				break;
			case R.id.wv_printer:
				if (imagexz) {
					wv.setImageResource(R.drawable.wv_printer_l_e);
				} else {
					wv.setImageResource(R.drawable.wv_printer_e);
				}
				break;
			case R.id.wv_magneticcard:
				wv.setImageResource(R.drawable.wv_magneticcard_e);
				break;
			case R.id.wv_fingerprintrecognition:
				wv.setImageResource(R.drawable.wv_fingerprintrecognition_e);
				break;
			case R.id.wv_capture:
				wv.setImageResource(R.drawable.wv_capture_e);
				break;
			case R.id.wv_led:
				wv.setImageResource(R.drawable.wv_led_e);
				break;
			case R.id.wv_scan:
				wv.setImageResource(R.drawable.wv_two_scan_e);
				break;
			}
		}
		wv.setOnClickIntent(this);
	}

	private boolean isZh() {
		Locale locale = getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (language.endsWith("zh"))
			return true;
		else
			return false;
	}

	private int[] r = { R.id.wv_buzzer, R.id.wv_laserlight, R.id.wv_cashbox,
			R.id.wv_rs232, R.id.wv_idcard, R.id.wv_pinpad, R.id.wv_psam,
			R.id.wv_iccard, R.id.wv_nfc, R.id.wv_printer, R.id.wv_magneticcard,
			R.id.wv_fingerprintrecognition, R.id.wv_capture, R.id.wv_led,
			R.id.wv_scan };

	private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

	private void initview() {
		// TODO Auto-generated method stub
		for (int i = 0; i < r.length; ++i) {
			initwv(r[i]);
		}
	}

	private ArrayList<HashMap<String, Object>> getData() {
		ArrayList<HashMap<String, Object>> myData = new ArrayList<HashMap<String, Object>>();
		addItem(myData,
				getResources().getString(R.string.buzzer_controller_label),
				new Intent(this, BuzzerControllerActivity.class));
		addItem(myData,
				getResources().getString(R.string.laserlight_controller_label),
				new Intent(this, LaserlightControllerActivity.class));
		addItem(myData,
				getResources().getString(R.string.cashbox_controller_label),
				new Intent(this, CashboxControllerActivity.class));
		addItem(myData,
				getResources().getString(R.string.rs232_controller_label),
				new Intent(this, RS232ControllerActivity.class));
		addItem(myData,
				getResources().getString(R.string.idcard_controller_label),
				new Intent(this, IDcardControllerActivity.class));
		addItem(myData,
				getResources().getString(R.string.pinpad_controller_label),
				new Intent(this, PP300PinPadControllerActivity.class));
		addItem(myData, getResources()
				.getString(R.string.psam_controller_label), new Intent(this,
				PSAMControllerActivity.class));
		addItem(myData,
				getResources().getString(R.string.iccard_controller_label),
				new Intent(this, ICcardControllerActivity.class));
		addItem(myData,
				getResources().getString(R.string.nfc_controller_label),
				new Intent(this, NFCControllerActivity.class));
		addItem(myData,
				getResources().getString(R.string.printer_controller_label),
				new Intent(this, PrinterControllerActivity.class));
		addItem(myData,
				getResources()
						.getString(R.string.magneticcard_controller_label),
				new Intent(this, MagnetCardControllerActivity.class));
		addItem(myData,
				getResources().getString(
						R.string.fingerprintrecognition_controller_label),
				new Intent(this, BRFingerprintControllerActivity.class));
		addItem(myData,
				getResources().getString(R.string.capture_controller_label),
				new Intent(this, CaptureActivity.class));
		addItem(myData, getResources()
				.getString(R.string.scan_controller_label), new Intent(this,
				ScanActivity.class));
		return myData;
	}

	protected void addItem(ArrayList<HashMap<String, Object>> data,
			String name, Intent intent) {
		HashMap<String, Object> temp = new HashMap<String, Object>();
		temp.put("title", name);
		temp.put("intent", intent);
		data.add(temp);
	}

	private static final int REQUEST_CODE = 234;

	@SuppressLint("InlinedApi")
	private void photo() {
		Intent innerIntent = new Intent();
		if (Build.VERSION.SDK_INT < 19) {
			innerIntent.setAction(Intent.ACTION_GET_CONTENT);
		} else {
			innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
		}
		innerIntent.setType("image/*");
		Intent wrapperIntent = Intent.createChooser(innerIntent, "photo");
		startActivityForResult(wrapperIntent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE:
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(data.getData(),
						proj, null, null, null);
				if (cursor.moveToFirst()) {
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					photo_path = cursor.getString(column_index);
					if (photo_path == null) {
						photo_path = Utils.getPath(getApplicationContext(),
								data.getData());
					}
				}
				cursor.close();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Result result = scanningImage(photo_path);
						if (result == null) {
							Looper.prepare();
							Toast.makeText(getApplicationContext(),
									"photo is bad", Toast.LENGTH_SHORT).show();
							Looper.loop();
						} else {
							String recode = recode(result.toString());
							Intent i = new Intent(ApiDemosMainActivity.this,
									ResultPhotoActivity.class);
							i.putExtra("result", recode);
							startActivity(i);
						}
					}
				}).start();
				break;
			}
		}
	}

	private String recode(String str) {
		String formart = "";
		try {
			boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
					.canEncode(str);
			if (ISO) {
				formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
			} else {
				formart = str;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return formart;
	}

	private String photo_path;
	private Bitmap scanBitmap;

	private byte[] rgb2YUV(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		int len = width * height;
		byte[] yuv = new byte[len * 3 / 2];
		int y, u, v;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int rgb = pixels[i * width + j] & 0x00FFFFFF;
				int r = rgb & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb >> 16) & 0xFF;
				y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
				u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
				v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
				y = y < 16 ? 16 : (y > 255 ? 255 : y);
				u = u < 0 ? 0 : (u > 255 ? 255 : u);
				v = v < 0 ? 0 : (v > 255 ? 255 : v);
				yuv[i * width + j] = (byte) y;
			}
		}
		return yuv;
	}

	protected Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		LuminanceSource lsource = new PlanarYUVLuminanceSource(
				rgb2YUV(scanBitmap), scanBitmap.getWidth(),
				scanBitmap.getHeight(), 0, 0, scanBitmap.getWidth(),
				scanBitmap.getHeight());

		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
				lsource));
		MultiFormatReader formatreader = new MultiFormatReader();
		try {
			return formatreader.decode(binaryBitmap);
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap, hints);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onViewClick(Win8View view) {
		// TODO Auto-generated method stub
		for (int i = 0; i < r.length; ++i) {
			if (view.getId() == r[i]) {
				if (r[i] == R.id.wv_printer) {
					AlertDialog dialog = new AlertDialog.Builder(this)
							.setMessage(getString(R.string.action_type))
							.setPositiveButton(
									getString(R.string.action_serialport),
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											Intent intent = new Intent(
													ApiDemosMainActivity.this,
													PrinterControllerActivity.class);
											startActivity(intent);
										}
									})
							.setNegativeButton(
									getString(R.string.action_bluetooth),
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											Intent intent = new Intent(
													ApiDemosMainActivity.this,
													BluetoothActivity.class);
											startActivity(intent);
										}
									}).create();
					dialog.show();
				} else if (r[i] == R.id.wv_capture) {
					final int n = i;
					AlertDialog dialog = new AlertDialog.Builder(this)
							.setMessage(getString(R.string.action_type))
							.setPositiveButton(
									getString(R.string.action_photo),
									new OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											photo();
										}
									})
							.setNegativeButton(
									getString(R.string.action_capture),
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											HashMap<String, Object> map = data
													.get(n);
											Intent intent = (Intent) map
													.get("intent");
											startActivity(intent);
										}
									}).create();
					dialog.show();
				} else if (r[i] == R.id.wv_led) {
					Intent intent = new Intent(ApiDemosMainActivity.this,
							LedControllerActivity.class);
					startActivity(intent);
				} else if (r[i] == R.id.wv_fingerprintrecognition) {
					AlertDialog dialog = new AlertDialog.Builder(this)
							.setMessage(getString(R.string.action_type))
//							.setPositiveButton(getString(R.string.finger_br),
//									new OnClickListener() {
//
//										@Override
//										public void onClick(
//												DialogInterface dialog,
//												int which) {
//											//TODO Auto-generated method stub
//											Intent intent = new Intent(
//													ApiDemosMainActivity.this,
//													BRFingerprintControllerActivity.class);
//											startActivity(intent);
//										}
//									})
							.setPositiveButton(getString(R.string.finger_fp),
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											Intent intent = new Intent(
													ApiDemosMainActivity.this,
													FingerPrintScanActivity.class);
											startActivity(intent);
										}
									})
							.setNegativeButton(getString(R.string.finger_za),
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											Intent intent = new Intent(
													ApiDemosMainActivity.this,
													ZAFingerprintControllerActivity.class);
											startActivity(intent);
										}
									}).create();
					dialog.show();
				} else if (r[i] == R.id.wv_pinpad) {
//					AlertDialog dialog = new AlertDialog.Builder(this)
//							.setMessage(getString(R.string.action_type))
//							.setPositiveButton(
//									getString(R.string.pinpad_controllerN20),
//									new OnClickListener() {
//
//										@Override
//										public void onClick(
//												DialogInterface dialog,
//												int which) {
//											// TODO Auto-generated method stub
//											Intent intent = new Intent(
//													ApiDemosMainActivity.this,
//													N20PINPadControllerActivity.class);
//											startActivity(intent);
//										}
//									})
//							.setNegativeButton(
//									getString(R.string.pinpad_controllerSP10),
//									new OnClickListener() {
//
//										@Override
//										public void onClick(
//												DialogInterface dialog,
//												int which) {
//											// TODO Auto-generated method stub
//											Intent intent = new Intent(
//													ApiDemosMainActivity.this,
//													SP10PINPadControllerActivity.class);
//											startActivity(intent);
//										}
//									}).create();
//					dialog.show();
					
					Intent intent = new Intent(ApiDemosMainActivity.this,VanstoneV10Activity.class);
					startActivity(intent);
				} else if (r[i] == R.id.wv_scan) {
					Intent intent = new Intent(ApiDemosMainActivity.this,
							ScanActivity.class);
					startActivity(intent);
				} else if (r[i] == R.id.wv_idcard) {
					if (vstr.equals("T001(Q)")) {
						final int n = i;
						AlertDialog dialog = new AlertDialog.Builder(this)
								.setMessage(getString(R.string.action_type))
								.setPositiveButton(
										getString(R.string.action_prot_1),
										new OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												HashMap<String, Object> map = data
														.get(n);
												Intent intent = (Intent) map
														.get("intent");
												intent.putExtra("PROT", 1);
												startActivity(intent);
											}
										})
								.setNegativeButton(
										getString(R.string.action_prot_2),
										new OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												HashMap<String, Object> map = data
														.get(n);
												Intent intent = (Intent) map
														.get("intent");
												intent.putExtra("PROT", 2);
												startActivity(intent);
											}
										}).create();
						dialog.show();
					} else {
						HashMap<String, Object> map = data.get(i);
						Intent intent = (Intent) map.get("intent");
						intent.putExtra("PROT", 1);
						startActivity(intent);
					}
				} else {
					HashMap<String, Object> map = data.get(i);
					Intent intent = (Intent) map.get("intent");
					startActivity(intent);
				}
			}
		}
	}

}
