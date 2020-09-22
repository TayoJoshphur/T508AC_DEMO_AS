package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import android_jb.com.POSD.util.BS;
import android_jb.com.POSD.util.MachineVersion;
import android_serialport_api.SerialPort;


public class PrinterController {
	private static PrinterController printerController = null;
	private SerialPort mSerialPort;;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private int language = 0;
	int mun1;
	private static Context context;
	private boolean pintimage = true;
	// 获取打印机状态指令                ox1c 0x73 x00,  0x1c 0x73 x01,0x1c 0x73 x02  
	private static final byte[] Set_Type = new byte[] { 0x10, 0x04, 0x05 };
	// 自检：打印机自检功能
	private static final byte[] PRINTE_TEST = new byte[] { 0x1D, 0x28, 0x41 };
	//裁纸指令
	private static final byte[] PRINTE_CUT = new byte[] { 0x1D, 0x56, 0x42,0x00};
	// 走纸
	private static final byte[] Take_The_Paper = new byte[] { 0x1B, 0x4A, 0x40 };
	// 换行
	private static final byte Line_feed = 0x0A;
	// 设置打印的灰度（打印的字符颜色深浅 分 8 个等级 1 ~ 8，"1"为最浅，"8"为最深）
	private static final byte[] Gray = new byte[] { 0x1B, 0x6D, 0x08 };
	// 正常模式
	private static final byte[] Font_Normal_mode = new byte[] { 0x1B, 0x21,
			0x00 };
	// 斜体
	private static final byte[] Font_Italics = new byte[] { 0x1B, 0x21, 0x02 };
	// 加粗
	private static final byte[] Font_Bold = new byte[] { 0x1B, 0x21, 0x08 };
	// 倍宽
	private static final byte[] Font_Double_width = new byte[] { 0x1B, 0x21,
			0x20 };
	// 倍高
	private static final byte[] Font_Times = new byte[] { 0x1B, 0x21, 0x10 };
	// 下划线
	private static final byte[] Font_Underline = new byte[] { 0x1B, 0x21,
			(byte) 0x80 };
	// 靠右
	private static final byte[] Set_Right = new byte[] { 0x1B, 0x61, 0x02 };
	// 靠左
	private static final byte[] Set_Left = new byte[] { 0x1B, 0x61, 0x00 };
	// 居中
	private static final byte[] Set_Center = new byte[] { 0x1B, 0x61, 0x01 };
	// 复位打印机命令
	private static final byte[] PRINTE_RESET = new byte[] { 0x1B, 0x40 };
	
	private static final byte[] lSpeed =new byte[] {0x1c,0x73,0x00};//低速
    private static final byte[] hSpeed =new byte[] {0x1c,0x73,0x02};//高速
    private static final byte[] mSpeed =new byte[] {0x1c,0x73,0x01};//高速
    
	private String IR_PWR_EN = "/proc/jbcommon/gpio_control/Printer_CTL";// 1开，0关
	private String IO_OE = "/proc/jbcommon/gpio_control/UART3_EN"; // 默认值：1，其他值无效
	private String IO_CS0 = "/proc/jbcommon/gpio_control/UART3_SEL0";// 默认值：1，其他值无效
	private String IO_CS1 = "/proc/jbcommon/gpio_control/UART3_SEL1";// 默认值：1，其他值无效
	
	String version = MachineVersion.getMachineVersion().substring(0, 7);

	public static PrinterController getInstance(Context contexts) {
		context = contexts;
		if (null == printerController) {
			printerController = new PrinterController();
		}
		return printerController;
	}

	/**
	 * 功能：打开设备
	 * 
	 * @return 0:成功，false:失败
	 */
	public int PrinterController_Open() {
		try {
			// mSerialPort = new SerialPort(new File("/dev/ttyS3"), 115200, 8,
			// '0', 1, 0,0);
			//58mm use 115200,80mm use 9600 or 115200
			writeFile(new File(IR_PWR_EN),"1");
			writeFile(new File(IO_OE),"0");
			writeFile(new File(IO_CS0),"0");
			writeFile(new File(IO_CS1),"1");
			Thread.sleep(100);
			mSerialPort = new SerialPort(new File("/dev/ttyS3"),115200 , 8,
					'0', 1, 0, 0);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			new ReadThread().start();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	private class ReadThread extends Thread {
		public void run() {
			while (true) {
				try {
					int size = 0;
					int coutq = 0;
					coutq = mInputStream.available();
					byte[] buffer1 = new byte[coutq];
					size = mInputStream.read(buffer1);
					if (size > 0) {
						System.out.println("返回  === " + (buffer1[0] & 0xff));
						if ((buffer1[0] & 0xff) == 19) {
							pintimage = false;
						} else {
							pintimage = true;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void writeFile(File file, String value) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(value);
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 功能：关闭设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PrinterController_Close() {
		writeFile(new File(IR_PWR_EN),"0");
		writeFile(new File(IO_CS1),"0");
		try {
			if (mSerialPort != null) {
				mOutputStream.close();
				mOutputStream = null;
				mInputStream.close();
				mInputStream = null;
				mSerialPort.close();
				mSerialPort = null;
				return 0;
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 功能：复位设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PrinterController_reset() {
		return Write_Command(PRINTE_RESET);
	}

	/**
	 * 功能：发送打印机指令
	 * 
	 * @param command
	 *            打印机指令
	 * @return 0：成功，-1：失败
	 */
	public int Write_Command(byte[] command) {
		System.out.println(command.length);
		try {
			if (mOutputStream == null) {
				return -1;
			}
			if (null != command) {
				mOutputStream.write(command);
				mOutputStream.flush();
				Log.v("RS232Controller","Rs232_Write:"
						+ bytesToHexString(command, 0, command.length));
				return 0;
			} else {
				return -1;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Convert bytes to string,actually display only
	 * 
	 * @param bytes
	 * @return String
	 */
	private String bytesToHexString(byte[] src, int start, int size) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || size <= 0) {
			return null;
		}
		for (int i = start; i < size; i++) {
			int v = src[i] & 0xFF;

			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	public int Write_Command(byte command) {
		try {
			if (mOutputStream == null) {
				return -1;
			}
			mOutputStream.write(command);
			mOutputStream.flush();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 功能：换行
	 * 
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Linefeed() {
		return Write_Command(Line_feed);
	}

	/**
	 * 功能：打印字符
	 * 
	 * @param bytes
	 *            字符数据
	 * @return 0：成功，-1：失败
	 */
	@SuppressWarnings("rawtypes")
	public int PrinterController_Print(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			System.out.println(bytes[i]);
		}
		try {
			String str = new String(bytes);
			System.out.println(str);
			int couts = 0;
			if (0 == language) {
				String[] strs = new String[str.length()];
				for (int i = 0; i < str.length(); i++) {
					strs[i] = str.substring(i, i + 1);
				}
				for (int j = 0; j < strs.length; j++) {
					if ("\n".equals(strs[j])) {
						PrinterController_Linefeed();
					} else {
						couts++;
						byte[] a = { strs[j].getBytes("unicode")[3],
								strs[j].getBytes("unicode")[2] };

						mOutputStream.write(a);
					}
				}
				return 0;
			} else if (1 == language) {
				ArrayList strs16 = str16(str);
				for (int j = 0; j < strs16.size(); j++) {
					int a = (Integer) strs16.get(j);
					if (10 == a) {
						PrinterController_Linefeed();
					} else {
						mOutputStream.write(a);
					}
				}
				return 0;
			} else if (2 == language) {
				mOutputStream.write(bytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
	}

	/**
	 * 功能：打印 Bitmap
	 * 
	 * @param bmp
	 *            把需要打印的图片转换成 Bitmap
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Bitmap(Bitmap bmp) {
		try {
			if (bmp != null) {
				byte[] command = decodeBitmap(bmp);
				mOutputStream.write(command);
				return 0;
			} else {
				return -1;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 功能：打印图片与二维码 （图片（二维码）必须放在assets目录，目前只支持该目录图片（二维码）的打印）
	 * 
	 * @param ICname
	 *            ：只传图片名称即可
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_ImageAddCode(String ICname) {
		AssetManager asm = context.getResources().getAssets();
		InputStream is;
		try {
			is = asm.open(ICname);
			Bitmap bmp = BitmapFactory.decodeStream(is);
			is.close();
			if (bmp != null) {
				byte[] command = decodeBitmap(bmp);
				Log.v("PrinterController","command-----:"+bytesToHexString(command));
				mOutputStream.write(command);
				return 0;
			} else {
				return -1;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			// System.out.println(buffer);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString();
	}
	/**
	 * 功能：获取打印机状态
	 * 
	 * @return -1：设备没有打开，0：打印机正常，4：压轴开和缺纸
	 */
	public int PrinterController_PrinterStatus() {

		if (mInputStream == null) {
			return -1;
		}
		int cout;
		try {
			Write_Command(Set_Type);
			Thread.sleep(50);
			cout = mInputStream.available();
			byte[] buffer = new byte[cout];
			mInputStream.read(buffer);
			return buffer[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 功能：设置语言
	 * 
	 * @param language
	 *            语言
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_PrinterLanguage(int language) {
		this.language = language;
		return 0;
	}

	/**
	 * 功能：打印机自检
	 * 
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_PrintText() {
		int tf = Write_Command(PRINTE_TEST);
		return tf;
	}

	/**
	 * 功能：打印机自检
	 * 
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Cut() {
		int tf = Write_Command(PRINTE_CUT);
		return tf;
	}
	
	/**
	 * 功能：走纸
	 * 
	 * @param l
	 *            走几行纸
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Take_The_Paper(int l) {
		if (l < 1) {
			return -1;
		}
		int tf = -1;
		for (int i = 0; i < l; i++) {
			tf = Write_Command(Take_The_Paper);
		}
		return tf;
	}

	/**
	 * 功能：设置打印的灰度（打印的字符颜色深浅 分 8 个等级 1 ~ 8，"1"为最浅，"8"为最深）
	 * 
	 * @param i
	 *            灰度级别
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Gray(int i) {
		Gray[2] = (byte) i;
		int tf = Write_Command(Gray);
		return tf;
	}

	/**
	 * 功能：设置字体正常模式
	 * 
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Font_Normal_mode() {
		int tf = Write_Command(Font_Normal_mode);
		return tf;
	}

	/**
	 * 功能：设置字体斜体模式
	 * 
	 * @return 0：成功，-1：失败
	 */
	// public int PrinterController_Font_Italics() {
	// int tf = Write_Command(Font_Italics);
	// return tf;
	// }

	/**
	 * 功能：设置字体加粗模式
	 * 
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Font_Bold() {
		int tf = Write_Command(Font_Bold);
		return tf;
	}

	/**
	 * 功能：设置字体倍宽模式
	 * 
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Font_Double_width() {
		int tf = Write_Command(Font_Double_width);
		return tf;
	}

	/**
	 * 功能：设置字体倍高模式
	 * 
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Font_Times() {
		int tf = Write_Command(Font_Times);
		return tf;
	}

	/**
	 * 功能：设置字体带下划线模式
	 * 
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Font_Underline() {
		int tf = Write_Command(Font_Underline);
		return tf;
	}

	/**
	 * 功能：设置打印起始位置为右边
	 * 
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Set_Right() {
		int tf = Write_Command(Set_Right);
		return tf;
	}

	/**
	 * 功能：设置打印起始位置为左边
	 * 
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Set_Left() {
		int tf = Write_Command(Set_Left);
		return tf;
	}

	/**
	 * 功能：设置打印内容居中
	 * 
	 * @return 0：成功，-1：失败
	 */
	public int PrinterController_Set_Center() {
		int tf = Write_Command(Set_Center);
		return tf;
	}
   /*
    * 设置打印低速
    *  @return 0：成功，-1：失败
    */
	public int PrinterController_Set_lowSpeed() {
		int tf = Write_Command(lSpeed);
		return tf;
	}
	  /*
	    * 设置打印高速
	    *  @return 0：成功，-1：失败
	    */
		public int PrinterController_Set_highSpeed() {
			int tf = Write_Command(hSpeed);
			return tf;
		}
		  /*
		    * 设置打印中速
		    *  @return 0：成功，-1：失败
		    */
			public int PrinterController_Set_midSpeed() {
				int tf = Write_Command(mSpeed);
				return tf;
			}
	// 《=====阿拉伯字符与波斯字符的处理
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	private ArrayList str16(String s) {

		int str16s[] = new int[s.length() + 1];
		int str16sb[] = new int[s.length() + 1];

		ArrayList albstr = new ArrayList();
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			str16s[i] = ch;

		}
		BS bs = new BS();
		boolean zj = false;
		int c = bs.IsIncludeArbic(str16s);
		if (c == 1) {
			zj = true;
		}
		if (zj) {
			bs.Arbic_Convert(str16s, str16sb);
		} else {
			str16sb = str16s;
		}
		ArrayList<Integer> sgb1 = new ArrayList<Integer>();
		ArrayList<Integer> sgb2 = new ArrayList<Integer>();
		for (int i = 0; i < str16sb.length; i++) {
			if (i == str16sb.length - 1) {
				for (int i1 = sgb1.size(); i1 > 0; i1--) {
					sgb2.add(sgb1.get(i1 - 1));
				}
			}
			if (str16sb[i] != 10) {
				sgb1.add(str16sb[i]);
			} else {
				for (int c1 = sgb1.size(); c1 > 0; c1--) {
					sgb2.add(sgb1.get(c1 - 1));
				}
				sgb1.clear();
				sgb2.add(10);
			}
		}
		for (int i = 0; i < sgb2.size(); i++) {
			if (sgb2.get(i) == 10) {
				albstr.add(sgb2.get(i));
			} else {
				int b = sgb2.get(i) / 256;
				albstr.add(b);
				int d = sgb2.get(i) % 256;
				albstr.add(d);
			}
		}
		return albstr;
	}

	@SuppressWarnings("unused")
	private byte[] decodeBitmap(Bitmap bmp) {
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();

		List<String> list = new ArrayList<String>(); // binaryString list
		StringBuffer sb;

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

		return hexList2Byte(commandList);
	}

	/**
	 * 指令list转换为byte[]指令
	 */
	private byte[] hexList2Byte(List<String> list) {

		List<byte[]> commandList = new ArrayList<byte[]>();

		for (String hexStr : list) {
			commandList.add(hexStringToBytes(hexStr));
		}
		byte[] bytes = sysCopy(commandList);
		return bytes;
	}

	/** Convert hexString to bytes(可用) */
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

	/**
	 * 系统提供的数组拷贝方法arraycopy
	 * */
	private static byte[] sysCopy(List<byte[]> srcArrays) {
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

	/** 二进制List<String>转为HexString */
	private List<String> binaryListToHexStringList(List<String> list) {
		List<String> hexList = new ArrayList<String>();
		for (String binaryStr : list) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < binaryStr.length(); i += 8) {
				String str = binaryStr.substring(i, i + 8);
				// 转成16进制
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

	public boolean printBitmap(Bitmap bmp) {
		try {
			if (bmp != null) {
				int e = bmp.getWidth();
				int height = bmp.getHeight();
				int w = e + 8 - e % 8;
				int h = w * height / e;
				Bitmap bitmap = this.scaleBitmap(bmp, w, h);
				byte[] head = new byte[] { (byte) 29, (byte) 118, (byte) 48,
						(byte) 48, (byte) (w / 8 >> 0), (byte) (w / 8 >> 8),
						(byte) (h >> 0), (byte) (h >> 8) };
				byte[] data = this.decodeBitmap(bitmap);
				byte[] command = new byte[head.length + data.length];
				System.arraycopy(head, 0, command, 0, head.length);
				System.arraycopy(data, 0, command, head.length, data.length);
				Thread.sleep(1000L);
				this.mOutputStream.write(command);
				return true;
			} else {
				return false;
			}
		} catch (Exception var10) {
			var10.printStackTrace();
			return false;
		}
	}

	private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
		if (origin == null) {
			return null;
		} else {
			int height = origin.getHeight();
			int width = origin.getWidth();
			float scaleWidth = (float) newWidth / (float) width;
			float scaleHeight = (float) newHeight / (float) height;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height,
					matrix, false);
			if (!origin.isRecycled()) {
				origin.recycle();
			}

			return newBM;
		}
	}
}
