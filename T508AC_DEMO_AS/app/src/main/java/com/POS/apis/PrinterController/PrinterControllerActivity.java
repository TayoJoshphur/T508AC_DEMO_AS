package com.POS.apis.PrinterController;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.EncodeHintType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import android_jb.com.POSD.controllers.PrinterController;
import android_jb.com.POSD.util.MachineVersion;
import jepower.com.t508ac_demo.R;

@SuppressLint({ "DefaultLocale", "ShowToast" })
public class PrinterControllerActivity extends Activity implements
		OnClickListener, OnItemSelectedListener {

	private final String IMAGE_TYPE = "image/*";

	private final int IMAGE_CODE = 0; // 这里的IMAGE_CODE是自己任意定义的

	private Bitmap bm = null;

	private byte[] imagebyte;

	private Button btn_connect;
	private Button btn_disconnect;
	private Button btn_clear;
	private Button btn_print;
	private Button btn_printreset;

	private Button btn_printest;
	private Button btn_TakeThePaper;
	private Button btn_Gray;
	private Button btn_FontNormalmode;
	private Button btn_FontItalics;
	private Button btn_FontBold;
	private Button btn_FontDoublewidth;
	private Button btn_FontTimes;
	private Button btn_FontUnderline;
	private Button btn_SetRight;
	private Button btn_SetLeft;
	private Button btn_SetCenter;
	private Button btn_Image;
	private Button btn_ticket;
	private Button btn_stringtocode;
	private Button btn_cut;
	private EditText ex;

	private EditText et_printdata;
	private Spinner sp_language;
	private PrinterController printerController = null;
	private ArrayAdapter<String> adapter = null;
	private int Language = 0;
	private boolean mCut =false;
    private byte[] lSpeed ={0x1c,0x73,0x00};
    private byte[] hSpeed ={0x1c,0x73,0x02};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.printer_controller_layout);
		initprinter();
		initview();
	}

	private void initprinter() {
		printerController = PrinterController.getInstance(this);
	}

	private void initview() {
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_print = (Button) findViewById(R.id.btn_print);
		btn_printreset = (Button) findViewById(R.id.btn_printreset);
		btn_connect.setOnClickListener(this);
		btn_disconnect.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		btn_printreset.setOnClickListener(this);
		btn_print.setOnClickListener(this);
		et_printdata = (EditText) findViewById(R.id.et_printdata);
		et_printdata.requestFocus();
		sp_language = (Spinner) findViewById(R.id.sp_language);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.language));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_language.setAdapter(adapter);
		sp_language.setOnItemSelectedListener(this);

		btn_printest = (Button) findViewById(R.id.btn_printest);
		btn_printest.setOnClickListener(this);
		btn_TakeThePaper = (Button) findViewById(R.id.btn_TakeThePaper);
		btn_TakeThePaper.setOnClickListener(this);
		btn_Gray = (Button) findViewById(R.id.btn_Gray);
		btn_Gray.setOnClickListener(this);
		btn_FontNormalmode = (Button) findViewById(R.id.btn_FontNormalmode);
		btn_FontNormalmode.setOnClickListener(this);
		btn_FontItalics = (Button) findViewById(R.id.btn_FontItalics);
		btn_FontItalics.setOnClickListener(this);
		btn_FontBold = (Button) findViewById(R.id.btn_FontBold);
		btn_FontBold.setOnClickListener(this);
		btn_FontDoublewidth = (Button) findViewById(R.id.btn_FontDoublewidth);
		btn_FontDoublewidth.setOnClickListener(this);
		btn_FontTimes = (Button) findViewById(R.id.btn_FontTimes);
		btn_FontTimes.setOnClickListener(this);
		btn_FontUnderline = (Button) findViewById(R.id.btn_FontUnderline);
		btn_FontUnderline.setOnClickListener(this);
		btn_SetRight = (Button) findViewById(R.id.btn_SetRight);
		btn_SetRight.setOnClickListener(this);
		btn_SetLeft = (Button) findViewById(R.id.btn_SetLeft);
		btn_SetLeft.setOnClickListener(this);
		btn_SetCenter = (Button) findViewById(R.id.btn_SetCenter);
		btn_SetCenter.setOnClickListener(this);
		btn_Image = (Button) findViewById(R.id.btn_imagae);
		btn_Image.setOnClickListener(this);
		btn_ticket = (Button) findViewById(R.id.btn_ticket);
		btn_ticket.setOnClickListener(this);
		btn_stringtocode = (Button) findViewById(R.id.btn_stringtocode);
		btn_stringtocode.setOnClickListener(this);
		btn_cut=(Button)findViewById(R.id.btn_cut);
		btn_cut.setOnClickListener(this);
		btn_cut.setVisibility(View.GONE);
		setfalse();
	}

	int flag;

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_connect:
			if (null == printerController) {
				printerController = PrinterController.getInstance(this);
			}
			flag = printerController.PrinterController_Open();
			if (flag == 0) {
				settrue();
				Toast.makeText(this, "connect_Success", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this, "connect_Failure", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.btn_disconnect:
			flag = printerController.PrinterController_Close();
			if (flag == 0) {
				setfalse();
				printerController = null;
				Toast.makeText(this, "disconnect_Success", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this, "disconnect_Failure", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.btn_print:
			printerController.PrinterController_PrinterLanguage(Language);
			printerController.PrinterController_Take_The_Paper(1);
			print();
			printerController.PrinterController_Take_The_Paper(1);
			if (mCut) {
	            cut();
				}
			Toast.makeText(this, "print", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_clear:
			clear();
			Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_printest:
			printerController.PrinterController_PrintText();
//			printerController.PrinterController_PrinterLanguage(Language);
//			printerController.PrinterController_Take_The_Paper(1);
//			printSelf();
//			printerController.PrinterController_Take_The_Paper(1);
//			if (mCut) {
//	            cut();
//				}
			Toast.makeText(this, "printest", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_TakeThePaper:
			printerController.PrinterController_Take_The_Paper(1);
			Toast.makeText(this, "TakeThePaper", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_Gray:
			ShowDaig();
			Toast.makeText(this, "Gray", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_FontNormalmode:
			printerController.PrinterController_Font_Normal_mode();
			Toast.makeText(this, "FontNormalmode", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_FontBold:
			printerController.PrinterController_Font_Bold();
			Toast.makeText(this, "FontBold", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_FontDoublewidth:
			printerController.PrinterController_Font_Double_width();
			Toast.makeText(this, "FontDoublewidth", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_FontTimes:
			printerController.PrinterController_Font_Times();
			Toast.makeText(this, "FontTimes", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_FontUnderline:
			printerController.PrinterController_Font_Underline();
			Toast.makeText(this, "FontUnderline", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_SetRight:
			printerController.PrinterController_Set_Right();
			Toast.makeText(this, "SetRight", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_SetLeft:
			printerController.PrinterController_Set_Left();
			Toast.makeText(this, "SetLeft", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_SetCenter:
			printerController.PrinterController_Set_Center();
			Toast.makeText(this, "SetCenter", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_imagae:
			 printerController.PrinterController_ImageAddCode("pic.bmp");
			 Toast.makeText(this, "imagae", Toast.LENGTH_SHORT).show();
			 //从图库中选择图片打印
//			Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
//			getAlbum.setType(IMAGE_TYPE);
//			startActivityForResult(getAlbum, IMAGE_CODE);
			break;
		case R.id.btn_ticket:
			ticket();
			if (mCut) {
	            cut();
				}
			Toast.makeText(this, "ticket", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_stringtocode:
			if (et_printdata.getText().toString().equals("")) {
				Toast.makeText(this, "stringtocode", 0).show();
				return;
			} else {
				new Thread() {
					public void run() {
						stringtocode();
						if (mCut) {
				            cut();
							}
					};
				}.start();
			}
			break;
		case R.id.btn_printreset:
			printreset();
			Toast.makeText(this, "reset", 0).show();
			break;
		case R.id.btn_cut:
			mCut = mCut?false:true;
			break;
		default:
			break;
		}
	}

	private void cut() {
		// TODO Auto-generated method stub
		if (null != printerController) {
			printerController.PrinterController_Cut();
		}
	}

	private void printreset() {
		// TODO Auto-generated method stub
		if (null != printerController) {
			printerController.PrinterController_reset();
		}
	}

	private void stringtocode() {
		// TODO Auto-generated method stub
		try {
			printerController.PrinterController_PrinterLanguage(2);
			Bitmap btm = Create2DCode(et_printdata.getText().toString());
			if (0 == printerController.Write_Command(decodeBitmap(btm)))
				System.out.println("printerController.Write_Commandok");
			else {
				System.out.println("printerController.Write_Commandno");
			}
			printerController.PrinterController_Linefeed();
			printerController.PrinterController_Linefeed();
			printerController.PrinterController_Linefeed();
			printerController.PrinterController_Linefeed();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Bitmap Create2DCode(String str) throws WriterException {

		BitMatrix matrix = null;
		try {
			if ("T001(Q)".equals(MachineVersion.getMachineVersion())) {
				System.out.println("508");
				matrix = new MultiFormatWriter().encode(
						new String(str.getBytes("GBK"), "ISO-8859-1"),
						BarcodeFormat.QR_CODE, 200, 200);
			} else {
				System.out.println("762");
				matrix = new MultiFormatWriter().encode(
						new String(str.getBytes("GB2312"), "ISO-8859-1"),
						BarcodeFormat.QR_CODE, 200, 200);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				} else {
					pixels[y * width + x] = 0xffffffff;
				}
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private byte[] decodeBitmap(Bitmap bmp) {
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		Log.v("PrinterControllerActivity","bmpWidth: "+bmpWidth+" bmpHeight: "+bmpHeight);
		List<String> list = new ArrayList<String>(); // binaryString list
		StringBuffer sb;

		@SuppressWarnings("unused")
		int bitLen = bmpWidth / 8;
		int zeroCount = bmpWidth % 8;
		String zeroStr = "";
		if (zeroCount > 0) {
			bitLen = bmpWidth / 8 + 1;
			for (int i = 0; i < (8 - zeroCount); i++) {
				zeroStr = zeroStr + "0";
			}
		}
		for (int i = 0; i < bmpHeight; i++) {
			sb = new StringBuffer();
			for (int j = 0; j < bmpWidth; j++) {
				int color = bmp.getPixel(j, i);

				int r = (color >> 16) & 0xff;
				int g = (color >> 8) & 0xff;
				int b = color & 0xff;

				if (r > 160 && g > 160 && b > 160)
					sb.append("0");
				else
					sb.append("1");
			}
			if (zeroCount > 0) {
				sb.append(zeroStr);
			}
			list.add(sb.toString());
		}
		List<String> bmpHexList = binaryListToHexStringList(list);
		String commandHexString = "1D763000";
		String widthHexString = Integer
				.toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
						: (bmpWidth / 8 + 1));
		if (widthHexString.length() > 2) {
			return null;
		} else if (widthHexString.length() == 1) {
			widthHexString = "0" + widthHexString;
		}
		widthHexString = widthHexString + "00";

		String heightHexString = Integer.toHexString(bmpHeight);
		if (heightHexString.length() > 2) {
			return null;
		} else if (heightHexString.length() == 1) {
			heightHexString = "0" + heightHexString;
		}
		heightHexString = heightHexString + "00";

		List<String> commandList = new ArrayList<String>();
		commandList.add(commandHexString + widthHexString + heightHexString);
		commandList.addAll(bmpHexList);
		byte[] bytes = hexList2Byte(commandList);
		return bytes;
	}

	private byte[] hexList2Byte(List<String> list) {

		List<byte[]> commandList = new ArrayList<byte[]>();

		for (String hexStr : list) {
			commandList.add(hexStringToBytes(hexStr));
		}
		byte[] bytes = sysCopy(commandList);
		return bytes;
	}

	private byte[] sysCopy(List<byte[]> srcArrays) {
		int len = 0;
		for (byte[] srcArray : srcArrays) {
			len += srcArray.length;
		}
		byte[] destArray = new byte[len];
		int destLen = 0;
		for (byte[] srcArray : srcArrays) {
			System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
			destLen += srcArray.length;
		}
		return destArray;
	}

	private List<String> binaryListToHexStringList(List<String> list) {
		List<String> hexList = new ArrayList<String>();
		for (String binaryStr : list) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < binaryStr.length(); i += 8) {
				String str = binaryStr.substring(i, i + 8);
				String hexString = myBinaryStrToHexString(str);
				sb.append(hexString);
			}
			hexList.add(sb.toString());
		}
		return hexList;

	}

	private String hexStr = "0123456789ABCDEF";
	private String[] binaryArray = { "0000", "0001", "0010", "0011", "0100",
			"0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100",
			"1101", "1110", "1111" };

	private String myBinaryStrToHexString(String binaryStr) {
		String hex = "";
		String f4 = binaryStr.substring(0, 4);
		String b4 = binaryStr.substring(4, 8);
		for (int i = 0; i < binaryArray.length; i++) {
			if (f4.equals(binaryArray[i]))
				hex += hexStr.substring(i, i + 1);
		}
		for (int i = 0; i < binaryArray.length; i++) {
			if (b4.equals(binaryArray[i]))
				hex += hexStr.substring(i, i + 1);
		}

		return hex;
	}

	private byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	private void ticket() {
		// TODO Auto-generated method stub
		printerController.PrinterController_PrinterLanguage(0);
		printerController
				.PrinterController_Print("--------------------------------"
						.getBytes());
		printerController.PrinterController_Linefeed();
		printerController
				.PrinterController_Print("           Ticket  de  Venta"
						.getBytes());
		printerController.PrinterController_Linefeed();
		printerController
				.PrinterController_Print("   Tu tienda amiga S.A de C.V."
						.getBytes());
		printerController.PrinterController_Linefeed();
		printerController.PrinterController_Print("   06/08/2015   13:15:45"
				.getBytes());
		printerController.PrinterController_Linefeed();
		printerController
				.PrinterController_Print("   1 Aceite Patrona   $20.00"
						.getBytes());
		printerController.PrinterController_Linefeed();
		printerController.PrinterController_Print("   2 Gansitos         $8.00"
				.getBytes());
		printerController.PrinterController_Linefeed();
		printerController
				.PrinterController_Print("   Total Venta:       $28.00"
						.getBytes());
		printerController.PrinterController_Linefeed();
		printerController.PrinterController_Print("   Gracias por su Compra"
				.getBytes());
		printerController.PrinterController_Linefeed();
		printerController
				.PrinterController_Print("--------------------------------"
						.getBytes());
		printerController.PrinterController_Linefeed();
		printerController.PrinterController_Linefeed();
		printerController.PrinterController_Linefeed();
		printerController.PrinterController_Linefeed();
	}

	private void clear() {
		et_printdata.setText("");
	}

	private void print() {
		String data = et_printdata.getText().toString();
		// byte[] test = new byte[] { 0x1B, 0x21, 0x38 };
		// printerController.Write_Command(test);
		// String str = null;
		// try {
		// str = new String(data.getBytes(), "GBK");
		// printerController.PrinterController_Print(str.getBytes());
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		if (Language == 2) {
			try {
				printerController.PrinterController_Print(data
						.getBytes("GB2312"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			printerController.PrinterController_Print(data.getBytes());
		}
	}
	
	private void printSelf() {
		//String data = "80MM热敏打印机  默认波特率:9600  支持字符集:1.ASCII 2.GB2312 程序版本:3.9";
		// byte[] test = new byte[] { 0x1B, 0x21, 0x38 };
		// printerController.Write_Command(test);
		// String str = null;
		// try {
		// str = new String(data.getBytes(), "GBK");
		// printerController.PrinterController_Print(str.getBytes());
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		String data = getText(R.string.printer_printest).toString();
		if (Language == 2) {
			try {
				printerController.PrinterController_Print(data
						.getBytes("GB2312"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			printerController.PrinterController_Print(data.getBytes());
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (null != printerController)
			printerController.PrinterController_Close();
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Language = arg2;
		System.out.println("Language ===" + Language);
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

	public void setfalse() {
		btn_connect.setEnabled(true);
		btn_print.setEnabled(false);
		btn_clear.setEnabled(false);
		btn_disconnect.setEnabled(false);
		btn_printest.setEnabled(false);
		btn_printreset.setEnabled(false);
		btn_TakeThePaper.setEnabled(false);
		btn_Gray.setEnabled(false);
		btn_FontNormalmode.setEnabled(false);
		btn_FontItalics.setEnabled(false);
		btn_FontBold.setEnabled(false);
		btn_FontDoublewidth.setEnabled(false);
		btn_FontTimes.setEnabled(false);
		btn_FontUnderline.setEnabled(false);
		btn_SetRight.setEnabled(false);
		btn_SetLeft.setEnabled(false);
		btn_SetCenter.setEnabled(false);
		btn_Image.setEnabled(false);
		btn_ticket.setEnabled(false);
		btn_stringtocode.setEnabled(false);
		et_printdata.setFocusable(false);
		et_printdata.setFocusableInTouchMode(false);
		btn_cut.setEnabled(false);
	}

	public void settrue() {
		btn_connect.setEnabled(false);
		btn_print.setEnabled(true);
		btn_clear.setEnabled(true);
		btn_disconnect.setEnabled(true);
		btn_printest.setEnabled(true);
		btn_printreset.setEnabled(true);
		btn_TakeThePaper.setEnabled(true);
		btn_Gray.setEnabled(true);
		btn_FontNormalmode.setEnabled(true);
		btn_FontItalics.setEnabled(true);
		btn_FontBold.setEnabled(true);
		btn_FontDoublewidth.setEnabled(true);
		btn_FontTimes.setEnabled(true);
		btn_FontUnderline.setEnabled(true);
		btn_SetRight.setEnabled(true);
		btn_SetLeft.setEnabled(true);
		btn_SetCenter.setEnabled(true);
		btn_Image.setEnabled(true);
		btn_ticket.setEnabled(true);
		btn_stringtocode.setEnabled(true);
		et_printdata.setFocusable(true);
		et_printdata.setFocusableInTouchMode(true);
		btn_cut.setEnabled(true);
	}

	public void ShowDaig() {
		ex = new EditText(this);
		ex.setText("4");
		ex.setGravity(Gravity.CENTER);
		new AlertDialog.Builder(this)
				.setTitle("Please enter the gray levels:1~8(default 4)")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(ex)
				.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {
								String jb = ex.getText().toString().trim();
								printerController
										.PrinterController_Gray(Integer
												.parseInt(jb));
							}
						}).setNegativeButton("NO", null).show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// RESULT_OK 是系统自定义得一个常量
		if (resultCode != RESULT_OK) {
			Log.e("onActivityResult", "返回的resultCode出错");
			return;
		}

		// 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
		ContentResolver resolver = getContentResolver();

		// 判断接收的Activity是不是选择图片的
		if (requestCode == IMAGE_CODE) {

			try {
				// 获得图片的地址Uri
				Uri originalUri = data.getData();
				// 根据图片的URi生成bitmap
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
				//bm = rotateBitmap(bm,-90); //图片逆时针旋转90度，因为我们摄像头拍出来的图片问题。当然也可以原样打印，看需求调整
				imagebyte = PrintPicture(bm, 529, 0);
				new Thread() {
					public void run() {
//						printerController.Write_Command(new byte[] { 0x1c,
//								0x73, 0x01 });
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						printerController.Write_Command(imagebyte);
						if (mCut) {
			            cut();
						}
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						printerController.Write_Command(new byte[] { 0x1c,
//								0x73, 0x02 });
					};
				}.start();
			} catch (Exception e) {
				Log.e("getImg", e.toString());

			}
		}
	}

	/**
	 * 对图片进行压缩（去除透明度）
	 * 
	 * @param bitmapOrg
	 */
	public static Bitmap compressPic(Bitmap bitmap) {
		// 获取这个图片的宽和高
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 指定调整后的宽度和高度
		int newWidth = 384;
		int newHeight = 300;
		Bitmap targetBmp = Bitmap.createBitmap(newWidth, newHeight,
				Config.ARGB_8888);
		Canvas targetCanvas = new Canvas(targetBmp);
		targetCanvas.drawColor(0xffffffff);
		targetCanvas.drawBitmap(bitmap, new Rect(0, 0, width, height),
				new Rect(0, 0, newWidth, newHeight), null);
		return targetBmp;
	}

	public Bitmap convertGreyImgByFloyd(Bitmap img) {

		int width = img.getWidth(); // 获取位图的宽

		int height = img.getHeight(); // 获取位图的高

		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

		img.getPixels(pixels, 0, width, 0, 0, width, height);

		int[] gray = new int[height * width];

		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {

				int grey = pixels[width * i + j];

				int red = ((grey & 0x00FF0000) >> 16);

				gray[width * i + j] = red;

			}

		}

		int e = 0;

		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {

				int g = gray[width * i + j];

				if (g >= 128) {

					pixels[width * i + j] = 0xffffffff;

					e = g - 255;

				} else {

					pixels[width * i + j] = 0xff000000;

					e = g - 0;

				}

				if (j < width - 1 && i < height - 1) {

					// 右边像素处理

					gray[width * i + j + 1] += 3 * e / 8;

					// 下

					gray[width * (i + 1) + j] += 3 * e / 8;

					// 右下

					gray[width * (i + 1) + j + 1] += e / 4;

				} else if (j == width - 1 && i < height - 1) {// 靠右或靠下边的像素的情况

					// 下方像素处理

					gray[width * (i + 1) + j] += 3 * e / 8;

				} else if (j < width - 1 && i == height - 1) {

					// 右边像素处理

					gray[width * (i) + j + 1] += e / 4;

				}

			}

		}

		Bitmap mBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);

		mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

		return mBitmap;

	}
	
	/**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

	

	public static byte[] PrintPicture(Bitmap mBitmap, int nWidth, int nMode) {

		// 先转黑白，再调用函数缩放位图
		// 不转黑白
		int width = ((nWidth + 7) / 8) * 8;
		int height = mBitmap.getHeight() * width / mBitmap.getWidth();
		height = ((height + 7) / 8) * 8;
		Bitmap rszBitmap = ImageProcessing.resizeImage(mBitmap, width, height);
		Bitmap grayBitmap = ImageProcessing.toGrayscale(rszBitmap);
		byte[] dithered = bitmapToBWPix(grayBitmap);
		byte[] data = eachLinePixToCmd(dithered, width, nMode);
		return data;
	}

	public static byte[] PrintBWPic(Bitmap mBitmap, int nWidth, int nMode) {
		// 先转黑白，再调用函数缩放位图
		// 不转黑白
		int width = ((nWidth + 7) / 8) * 8;
		int height = mBitmap.getHeight() * width / mBitmap.getWidth();
		height = ((height + 7) / 8) * 8;

		Bitmap rszBitmap = mBitmap;
		if (mBitmap.getWidth() != width)
			rszBitmap = ImageProcessing.resizeImage(mBitmap, width, height);

		Bitmap grayBitmap = ImageProcessing.toGrayscale(rszBitmap);
		byte[] dithered = thresholdToBWPic(grayBitmap);
		// 将转换过的图片，保存到文件，以下图片，仅作调试使用。
		byte[] data = eachLinePixToCmd(dithered, width, nMode);

		return data;
	}

	/**
	 * 将ARGB图转换为二值图，0代表黑，1代表白
	 * 
	 * @param mBitmap
	 * @return
	 */
	public static byte[] bitmapToBWPix(Bitmap mBitmap) {

		int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
		byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];

		mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0,
				mBitmap.getWidth(), mBitmap.getHeight());

		// for the toGrayscale, we need to select a red or green or blue color
		ImageProcessing.format_K_dither16x16(pixels, mBitmap.getWidth(),
				mBitmap.getHeight(), data);

		return data;
	}

	private static byte[] thresholdToBWPic(Bitmap mBitmap) {
		int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
		byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];

		mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0,
				mBitmap.getWidth(), mBitmap.getHeight());

		// for the toGrayscale, we need to select a red or green or blue color
		ImageProcessing.format_K_threshold(pixels, mBitmap.getWidth(),
				mBitmap.getHeight(), data);

		return data;
	}

	private static byte[] eachLinePixToCmd(byte[] src, int nWidth, int nMode) {
		int nHeight = src.length / nWidth;
		int nBytesPerLine = nWidth / 8;
		Log.v("yaqin.tang","eachLinePixToCmd nWidth:"+nWidth);
		byte[] data = new byte[nHeight * (8 + nBytesPerLine)];
		int offset = 0;
		int k = 0;
		for (int i = 0; i < nHeight; i++) {
			offset = i * (8 + nBytesPerLine);
			data[offset + 0] = 0x1d;
			data[offset + 1] = 0x76;
			data[offset + 2] = 0x30;
			data[offset + 3] = (byte) (nMode & 0x01);
			data[offset + 4] = (byte) (nBytesPerLine % 0x100);
			data[offset + 5] = (byte) (nBytesPerLine / 0x100);
			data[offset + 6] = 0x01;
			data[offset + 7] = 0x00;
			Log.v("yaqin.tang","eachLinePixToCmd nBytesPerLine:"+nBytesPerLine);
			for (int j = 0; j < nBytesPerLine; j++) {
				data[offset + 8 + j] = (byte) (p0[src[k]] + p1[src[k + 1]]
						+ p2[src[k + 2]] + p3[src[k + 3]] + p4[src[k + 4]]
						+ p5[src[k + 5]] + p6[src[k + 6]] + src[k + 7]);
				k = k + 8;
			}
		}

		return data;
	}

	// nWidth必须为8的倍数,这个只需在上层控制即可
	// 之所以弄成一维数组，是因为一维数组速度会快一点
	private static int[] p0 = { 0, 0x80 };
	private static int[] p1 = { 0, 0x40 };
	private static int[] p2 = { 0, 0x20 };
	private static int[] p3 = { 0, 0x10 };
	private static int[] p4 = { 0, 0x08 };
	private static int[] p5 = { 0, 0x04 };
	private static int[] p6 = { 0, 0x02 };
}
