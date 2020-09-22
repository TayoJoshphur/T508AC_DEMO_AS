package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.R.mipmap;
import android.util.Log;
import android_serialport_api.SerialPort;

public class SP10PINPadController {

	private static final String TAG = "PINPadController";
	private static SP10PINPadController pinPadController = null;
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private byte[] clean = { 0x1B, 0x43, 0x0D, 0x0A };
	private byte[] init = { 0x1B, 0x52, 0x0D, 0x0A };

	public static SP10PINPadController getInstance() {
		Log.i(TAG, "getInstance");
		if (null == pinPadController) {
			pinPadController = new SP10PINPadController();
		}
		return pinPadController;
	}

	/**
	 * 功能：打开设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PINPad_Open() {
		try {
			//Ioctl.convertRJ11();
			mSerialPort = new SerialPort(new File("/dev/ttyS2"), 9600, 8, '0',
					1, 0, 0);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 功能：关闭设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PINPad_Close() {
		try {
			if (mOutputStream != null) {
				mOutputStream.close();
				mOutputStream = null;
			}
			if (mInputStream != null) {
				mInputStream.close();
				mInputStream = null;
			}
			if (mSerialPort != null) {
				mSerialPort.close();
				mSerialPort = null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 功能：初始化设备,将密码键盘复位成出厂状态
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PINPad_Init() {
		try {
			mOutputStream.write(init);
			mOutputStream.flush();
			Thread.sleep(100);
			int couts = 0;
			int cout = mInputStream.available();
			while (true) {
				if (cout == couts && cout != 0 && couts != 0) {
					break;
				}
				cout = mInputStream.available();
				couts = cout;
			}
			byte[] buffer = new byte[cout];
			mInputStream.read(buffer);
			if ((buffer[0] & 0xff) == 0xAA) {
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
	 * 功能：清屏
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PINPad_clear() {
		try {
			mOutputStream.write(clean);
			mOutputStream.flush();
			Thread.sleep(200);
			int cout = mInputStream.available();
			byte[] buffer = new byte[cout];
			int cum = mInputStream.read(buffer);
			if (buffer.length > 0) {
				if ((buffer[0] & 0xff) == 0xaa) {
					//System.out.printf("%x\n", buffer[0]);
					return 0;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 功能：发数据
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PINPad_sendToPinPad(String text) {
		try {
			byte[] data = StrToByte(text);
			if (SenCommd(data) == 0) {
				return 0;
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 功能：把字符串转换成符合显示的byte形式
	 * 
	 * @param str
	 * @return
	 */
	private byte[] StrToByte(String str) {
		byte[] a = str.getBytes();
		byte[] b = new byte[5 + a.length];
		b[0] = 0x1B;
		b[1] = 0x44;
		b[2] = 0x31;
		for (int i = 0; i < a.length; i++) {
			b[3 + i] = a[i];
		}
		b[b.length - 2] = 0x0D;
		b[b.length - 1] = 0x0A;
		return b;
	}

	/**
	 * 功能：发送符合sp10格式的指令，必需参考指令文档
	 * 
	 * @param bytes
	 *            指令
	 * @return 0:成功，-1:失败
	 */
	private int SenCommd(byte[] bytes) {
		try {
			mOutputStream.write(bytes);
			mOutputStream.flush();
			Thread.sleep(200);
			int cout = mInputStream.available();
			byte[] buffer = new byte[cout];
			int cum = mInputStream.read(buffer);
			if (buffer.length > 0) {
				if ((buffer[0] & 0xff) == 0xaa) {
					//System.out.printf("%x\n", buffer[0]);
					return 0;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}

	/**
	 * 功能：让用户输入密码
	 * 
	 * @return 0:成功，-1:失败
	 */

	public int PINPad_InputPINPinPad() {
		try {
			byte[] b = { 0x1B, 0x4F, 0x0D, 0x0A };
			mOutputStream.write(b);
			mOutputStream.flush();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 功能：获取在键盘入的数据
	 * 
	 * @return null：失败， 不为null：成功
	 */
	public String PINPad_GetDataPinPad() {
		try {
			int couts = 0;
			int cout = mInputStream.available();
			boolean dbloe = true;
			int coutq = 0;
			int couth = 0;
			if (null != mInputStream) {
				coutq = mInputStream.available();
			} else {
				return "";
			}
			while (coutq != couth) {
				Thread.sleep(10);
				couth = mInputStream.available();
				Thread.sleep(10);
				coutq = mInputStream.available();
			}
			byte[] buffer = new byte[coutq];
			mInputStream.read(buffer);
			if (buffer.length > 2) {
				byte[] zs = new byte[buffer.length - 2];
				for (int i = 0; i < zs.length; i++) {
					zs[i] = buffer[2 + i];
				}
				dbloe = false;
				return new String(zs);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 功能：更新主密钥
	 * 
	 * @param i
	 *            主密钥区号
	 * @param pas
	 *            原主密钥
	 * @param npas
	 *            新主密钥
	 * @return 0:成功，-1:失败
	 */
	public int PINPad_updateMKey(int i, byte[] pas, byte[] npas) {
		byte[] zw = new byte[5 + pas.length * 2 + npas.length * 2];
		zw[0] = 27;
		zw[1] = 77;
		zw[2] = 3;
		byte[] pass = gzmhx(pas);
		byte[] npass = gzmhx(npas);
		int j = 0;
		int g = 0;
		for (int s = 3; s < zw.length - 2; s++) {
			if (j < pass.length) {
				zw[s] = pass[j];
			}
			j++;
			if (j > pass.length) {
				zw[s] = npass[g];
				g++;
			}
		}
		zw[(zw.length - 1)] = 10;
		zw[(zw.length - 2)] = 13;
		SenCommd(zw);
		byte[] buffer = doReadZM();
		if ((buffer[0] & 0xff) == 0xAA) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 功能：更新工作密钥
	 * 
	 * @param zi
	 *            主密钥区号
	 * @param wi
	 *            工作密钥区号
	 * @param wm
	 *            工作密钥
	 * @return 0:成功，-1:失败
	 */
	public int PINPad_updateWKey(int zi, int wi, byte[] wm) {
		byte[] pwd = zmhx(wm);
		SenCommd(pwd);
		int c = -1;
		byte[] res = doReadZM();
		if ((res[0] & 0xff) == 0xAA) {
			c = ActiveWKey(3, 1, 0);
			if (c == 0xAA) {
				return 0;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	/**
	 * 功能：激活工作密钥
	 * 
	 * @param m
	 *            主密钥号
	 * @param n
	 *            工作密钥号
	 * @param f
	 *            DES标志
	 * @return 0xAA:成功，0x55：失败
	 */
	private int ActiveWKey(int m, int n, int f) {
		try {
			byte[] startkey = { 27, 65, 0, 0, 0, 13, 10 };
			if (m > -1) {
				startkey[2] = ((byte) m);
			}
			if (n > -1) {
				startkey[3] = ((byte) n);
			}
			if (f != 0) {
				startkey[4] = ((byte) f);
			}
			mOutputStream.write(startkey);
			mOutputStream.flush();
			Thread.sleep(100);
			int couts = 0;
			int cout = mInputStream.available();
			while (true) {
				if (cout == couts && cout != 0 && couts != 0) {
					break;
				}
				cout = mInputStream.available();
				couts = cout;
			}
			byte[] buffer = new byte[cout];
			mInputStream.read(buffer);
			System.out.println("writeWY == " + (buffer[0] & 0xff));
			return buffer[0] & 0xff;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return 0x55;
	}

	private byte[] doReadZM() {
		try {
			String reciver = "";
			if (mInputStream == null) {
				return new byte[1];
			}
			int couts = 0;
			int cout = mInputStream.available();
			while (true) {
				if (cout == couts && cout != 0 && couts != 0) {
					break;
				}
				cout = mInputStream.available();
				couts = cout;
			}
			byte[] buffer = new byte[cout];
			mInputStream.read(buffer);
			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[1];
		}
	}

	private byte[] zmhx(byte[] ss) {
		byte[] acc = new byte[ss.length * 2 + 7];
		acc[0] = 27;
		acc[1] = 83;
		acc[2] = 3;
		acc[3] = 1;
		acc[4] = ((byte) (ss.length * 2));
		acc[(acc.length - 1)] = 10;
		acc[(acc.length - 2)] = 13;
		int g = 5;
		for (int i = 0; i < ss.length; i++) {
			int s = Integer.parseInt(ss[i] + "");
			String a = Integer.toHexString(ss[i]);
			if (1 == a.length()) {
				a = "0" + a;
			}
			if (a.length() == 2) {
				String c1 = a.substring(0, 1);
				String c2 = a.substring(1, 2);
				if (Judge(c1)) {
					acc[g] = number(c1);
					g++;
				} else {
					acc[g] = ZM(c1);
					g++;
				}
				if (Judge(c2)) {
					acc[g] = number(c2);
					g++;
				} else {
					acc[g] = ZM(c2);
					g++;
				}
			} else if (a.length() == 8) {
				String cc = a.substring(6, 8);
				for (int j = 0; j < cc.length() - 1; j++) {
					String c1 = cc.substring(0, 1);
					String c2 = cc.substring(1, 2);
					if (Judge(c1)) {
						acc[g] = number(c1);
						g++;
					} else {
						acc[g] = ZM(c1);
						g++;
					}
					if (Judge(c2)) {
						acc[g] = number(c2);
						g++;
					} else {
						acc[g] = ZM(c2);
						g++;
					}
				}
			}
		}
		return acc;
	}

	private String bytesToHexString123(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");

		if ((src == null) || (src.length <= 0)) {
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

	private byte[] gzmhx(byte[] ss) {
		byte[] acc = new byte[ss.length * 2];
		int g = 0;
		for (int i = 0; i < ss.length; i++) {
			int s = Integer.parseInt((ss[i] + "").toString());
			String a = Integer.toHexString(ss[i]);
			if (1 == a.length()) {
				a = "0" + a;
			}
			if (a.length() == 2) {
				String c1 = a.substring(0, 1);
				String c2 = a.substring(1, 2);
				if (Judge(c1)) {
					acc[g] = number(c1);
					g++;
				} else {
					acc[g] = ZM(c1);
					g++;
				}
				if (Judge(c2)) {
					acc[g] = number(c2);
					g++;
				} else {
					acc[g] = ZM(c2);
					g++;
				}
			} else if (a.length() == 8) {
				String cc = a.substring(6, 8);
				for (int j = 0; j < cc.length() - 1; j++) {
					String c1 = cc.substring(0, 1);
					String c2 = cc.substring(1, 2);
					if (Judge(c1)) {
						acc[g] = number(c1);
						g++;
					} else {
						acc[g] = ZM(c1);
						g++;
					}
					if (Judge(c2)) {
						acc[g] = number(c2);
						g++;
					} else {
						acc[g] = ZM(c2);
						g++;
					}
				}
			}
		}
		return acc;
	}

	private boolean Judge(String c1) {
		if ((c1.equals("1")) || (c1.equals("2")) || (c1.equals("3"))
				|| (c1.equals("4")) || (c1.equals("5")) || (c1.equals("6"))
				|| (c1.equals("7")) || (c1.equals("8")) || (c1.equals("9"))
				|| (c1.equals("0"))) {
			return true;
		}
		return false;
	}

	private byte number(String a) {
		byte s1 = 0;
		if ("1".equals(a))
			s1 = 66;
		else if ("2".equals(a))
			s1 = 67;
		else if ("3".equals(a))
			s1 = 68;
		else if ("4".equals(a))
			s1 = 69;
		else if ("5".equals(a))
			s1 = 70;
		else if ("6".equals(a))
			s1 = 71;
		else if ("7".equals(a))
			s1 = 72;
		else if ("8".equals(a))
			s1 = 73;
		else if ("9".equals(a))
			s1 = 74;
		else if ("0".equals(a)) {
			s1 = 65;
		}
		return s1;
	}

	private byte ZM(String a) {
		byte s1 = 0;
		if ("a".equals(a))
			s1 = 75;
		else if ("b".equals(a)) {
			s1 = 76;
		} else if ("c".equals(a)) {
			s1 = 77;
		} else if ("d".equals(a)) {
			s1 = 78;
		} else if ("e".equals(a)) {
			s1 = 79;
		} else if ("f".equals(a)) {
			s1 = 80;
		}

		return s1;
	}

	private int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
}
