package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


import android.util.Log;

import android_jb.com.POSD.interfaces.MagnetCardReadCallBack;
import android_jb.com.POSD.util.MachineVersion;
import android_serialport_api.SerialPort;

public class MagnetCardController {
	private static MagnetCardController MagnetCardController = null;
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private MagnetCardReadCallBack magneticCardReadCallBack = null;
	private ReadDataThreadUnencrypted dataThreadUnencrypted = null;
	private ReadDataThreadEncryption dataThreadEncryptio = null;
	private boolean readFlag = true;
	private boolean isReadStatic;
	private String key = "";
	private String vser = "";
	private String version = MachineVersion.getMachineVersion();

	byte[] shangdian = { 0x68, 0x02, 0x01, 0x00, 0x00, 0x6B, 0x16 };
	byte[] xiadian = { 0x68, 0x02, 0x01, 0x00, 0x01, 0x6C, 0x16 };
	byte[] versions = { 0x68, 0x07, 0x01, 0x00, 0x00, 0x70, 0x16 };
	private String IR_PWR_EN = "/proc/jbcommon/gpio_control/Printer_CTL";// 1开，0关
	private String IO_OE = "/proc/jbcommon/gpio_control/UART1_EN"; // 默认值：1，其他值无效
	private String IO_CS0 = "/proc/jbcommon/gpio_control/UART1_SEL0";// 默认值：1，其他值无效
	private String IO_CS1 = "/proc/jbcommon/gpio_control/UART1_SEL1";// 默认值：1，其他值无效
	private String IO_OE3 = "/proc/jbcommon/gpio_control/UART3_EN"; // 默认值：1，其他值无效
	private String IO_CS03 = "/proc/jbcommon/gpio_control/UART3_SEL0";// 默认值：1，其他值无效
	private String IO_CS13 = "/proc/jbcommon/gpio_control/UART3_SEL1";// 默认值：1，其他值无效

	public static MagnetCardController getInstance() {
		if (null == MagnetCardController) {
			MagnetCardController = new MagnetCardController();
		}
		return MagnetCardController;
	}

	public String getVersion() {
		return vser;
	}

	private String getMSRVersion() {
		try {
			vser = "";
			int cout;
			byte[] buffer;
			int size;
			mOutputStream.write(versions);
			Thread.sleep(30);
			cout = mInputStream.available();
			buffer = new byte[cout];
			size = mInputStream.read(buffer);
			byte[] versiondata = new byte[size];
			String vs = bytesToHexString(buffer);
			int len = Integer.parseInt(vs.substring(4, 6));
			vs = vs.substring(8, vs.length() - 4);
			for (int i = 0; i < len; i++) {

				if (vs.substring(i * 2, (i + 1) * 2).equals("2e")) {
					vser += ".";
				} else {
					vser += vs.substring(i * 2, (i + 1) * 2).substring(1, 2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vser;
	}

	private String bytesToHexString(byte[] src) {
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

	/**
	 * 功能：打开设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int MagnetCardController_Open(MagnetCardReadCallBack readCallBack,
			int flag) {
		try {
			//Ioctl.convertMagcard();
			//Ioctl.activate(20, 1);
			writeFile(new File(IR_PWR_EN),"1");
			writeFile(new File(IO_OE),"0");
			writeFile(new File(IO_CS0),"1");
			writeFile(new File(IO_CS1),"1");
			writeFile(new File(IO_OE3),"0");
			writeFile(new File(IO_CS03),"0");
			writeFile(new File(IO_CS13),"1");
			Thread.sleep(100);
			mSerialPort = new SerialPort(new File("/dev/ttyS1"), 115200, 8,
					'0', 1, 0, 0);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			magneticCardReadCallBack = readCallBack;
			readFlag = true;
			Thread.sleep(200);
			mOutputStream.write(shangdian);
			mOutputStream.flush();

			byte[] buffer = new byte[1024];
			int cout = mInputStream.available();
			Thread.sleep(200);
			cout = mInputStream.available();
			Thread.sleep(20);
			mInputStream.read(buffer);
			Thread.sleep(200);
			if (flag == 1) {
				setEncryption(0);
				key = MSR_readPassword();
				dataThreadEncryptio = new ReadDataThreadEncryption();
				dataThreadEncryptio.start();
				dataThreadEncryptio.running = true;
				System.out.println("执行加密码！！！！！！！！！！！");
				getMSRVersion();
			} else {
				dataThreadUnencrypted = new ReadDataThreadUnencrypted();
				dataThreadUnencrypted.start();
			}
			magneticCardReadCallBack.MagnetCardController_Read(null);
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
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
	public int MagnetCardController_Close(int i) {
		try {
			if (null != mInputStream && null != mOutputStream) {

				if (dataThreadEncryptio != null) {
					dataThreadEncryptio.running = false;
				}
				isReadStatic = false;
				readFlag = false;
				if (i == 1){
					mOutputStream.write(xiadian);
					mOutputStream.flush();
					byte[] buffer = new byte[1024];
					int cout = mInputStream.available();
					Thread.sleep(100);
					mInputStream.read(buffer);
				}
				//Ioctl.activate(20, 0);
				writeFile(new File(IR_PWR_EN),"0");
				writeFile(new File(IO_CS0),"0");
				writeFile(new File(IO_CS1),"0");
				writeFile(new File(IO_CS13),"0");
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
				return 0;
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	private class ReadDataThreadUnencrypted extends Thread {

		public void run() {
			try {
				while (readFlag) {
					Thread.sleep(200);
					int size = 0;
					int coutq = 0;
					int couth = 0;
					if (null != mInputStream) {
						coutq = mInputStream.available();
					} else {
						break;
					}
					while (coutq != couth) {
						Thread.sleep(10);
						couth = mInputStream.available();
						Thread.sleep(10);
						coutq = mInputStream.available();
					}
					byte[] buffer1 = new byte[couth];
					size = mInputStream.read(buffer1);
					if (size > 0) {
						magneticCardReadCallBack
								.MagnetCardController_Read(buffer1);
						buffer1 = null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class ReadDataThreadEncryption extends Thread {
		public boolean running;

		public void run() {
			int cout = 0;
			int couts = 0;
			byte[] buffer = null;
			byte[] buffer1 = null;
			byte[] buffer2 = null;
			byte[] buffer3 = null;
			while (running) {
				try {
					Thread.sleep(50);
					if (mInputStream == null) {
						break;
					}

					MSR_readCard();
					cout = mInputStream.available();
					buffer = new byte[cout];
					if (cout == 7 || cout == 0) {
						mInputStream.read(buffer);
						continue;
					} else {
						buffer = new byte[cout];
						mInputStream.read(buffer);
//						System.out.println("cout1 ==== " + cout);
//						for (int i = 0; i < cout; i++){
//							System.out.format("%x", buffer[i]);
//						}
//						System.out.println("\n");
//						Thread.sleep(500);
//						cout = mInputStream.available();
//						mInputStream.read(buffer);
//						System.out.println("cout2 ==== " + cout);
//						for (int i = 0; i < cout; i++){
//							System.out.format("%x", buffer[i]);
//						}
//						System.out.println("\n");
//						Thread.sleep(500);
//						cout = mInputStream.available();
//						mInputStream.read(buffer);
//						System.out.println("cout3 ==== " + cout);
//						for (int i = 0; i < cout; i++){
//							System.out.format("%x", buffer[i]);
//						}
//						System.out.println("\n");
//						Thread.sleep(500);
//						cout = mInputStream.available();
//						mInputStream.read(buffer);
//						System.out.println("cout4 ==== " + cout);
//						for (int i = 0; i < cout; i++){
//							System.out.format("%x", buffer[i]);
//						}
					}

					byte[] temp = new byte[cout];
					temp = parserMsrData(buffer);
					if (temp != null) {
						magneticCardReadCallBack
								.MagnetCardController_Read(temp);
						isReadStatic = false;
						temp = null;
						buffer = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String bytesToHexString123(byte[] src) {

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
			stringBuilder.append(hv + ",");

		}
		return stringBuilder.toString();
	}

	public byte[] parserMsrData(byte[] buffer1) {
		ArrayList<Byte> dats = new ArrayList<Byte>();
		byte[] zss = null;
		if (buffer1 != null && buffer1.length > 0) {
			for (int i = 0; i < buffer1.length; i++) {
				if ((buffer1[i] & 0xff) == 0x68
						&& (buffer1[i + 1] & 0xff) == 0x81) {
					dats.add(buffer1[i + 2]);
				}
			}
			byte[] d0 = null;
			byte[] d1 = null;
			byte[] d2 = null;
			int zs = 0;
			for (int i = 0; i < dats.size(); i++) {
				if (i == 0) {
					d0 = new byte[(dats.get(i) & 0xff) + 6];
				} else if (i == 1) {
					d1 = new byte[(dats.get(i) & 0xff) + 6];
				} else {
					d2 = new byte[(dats.get(i) & 0xff) + 6];
				}
				zs += (dats.get(i) & 0xff);
			}
			zss = new byte[zs];
			int start = 0;
			if (null != d0) {
				for (int i = 0; i < d0.length; i++) {
					if ((d0.length - 1) == i) {
						start = i + 1;
					}
					d0[i] = buffer1[i];
				}
				d0 = ResolveData(d0);
				for (int i = 0; i < d0.length; i++) {
					zss[i] = d0[i];
				}
			}
			if (null != d1) {
				int cots = 0;
				for (int i = start; i < buffer1.length; i++) {
					if ((buffer1.length - 1) == i) {
						start = i + 1;
					}
					d1[cots] = buffer1[i];
					cots++;
				}
				d1 = ResolveData(d1);
				int cout = d0.length;
				for (int i = 0; i < d1.length; i++) {
					zss[cout] = d1[i];
					cout++;
				}
			}
			if (null != d2) {
				int cots = 0;
				for (int i = start; i < buffer1.length; i++) {
					d2[cots] = buffer1[i];
					cots++;
				}
				d2 = ResolveData(d2);
				int cout = start;
				for (int i = 0; i < d2.length; i++) {
					zss[cout] = d1[i];
					cout++;
				}
			}
			return zss;
		} else {
			return null;
		}
	}

	public byte[] ResolveData(byte[] buffer1) {
		if (buffer1.length <= 0 || buffer1 == null) {
			return null;
		}
		byte[] dataBuffer = new byte[buffer1.length - 6];
		for (int i = 0; i < dataBuffer.length; i++) {
			dataBuffer[i] = buffer1[4 + i];
		}
		int a = dataBuffer.length / 10;
		int b = dataBuffer.length % 10;
		byte[] passCode = key.getBytes();
		if (a > 0) {
			for (int i = 0; i < a; i++) {
				for (int j = 0; j < 10; j++) {
					dataBuffer[10 * i + j] = (byte) (dataBuffer[10 * i + j] ^ passCode[j]);
				}
			}
			for (int k = 0; k < b; k++) {
				dataBuffer[10 * a + k] = (byte) (dataBuffer[10 * a + k] ^ passCode[k]);
			}

		} else {
			for (int k = 0; k < b; k++) {
				dataBuffer[k] = (byte) (dataBuffer[k] ^ passCode[k]);
			}
		}
		for (int i = 0; i < dataBuffer.length; i++) {
			dataBuffer[i] = (byte) ((dataBuffer[i] & 0xf0) >> 4 | (dataBuffer[i] & 0x0f) << 4);
		}
		return dataBuffer;
	}

	private void MSR_readCard() {
		try {
			if (mOutputStream != null) {
				byte[] read = { 0x68, 0x01, 0x01, 0x00, 0x00, 0x6A, 0x16 };
				mOutputStream.write(read);
				mOutputStream.flush();
				isReadStatic = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String MSR_readPassword() throws IOException {
		try {
			byte[] bytes = { 0x68, 0x04, 0x01, 0x00, 0x00, 0x6D, 0x16 };

			mOutputStream.write(bytes);
			mOutputStream.flush();
			Thread.sleep(50);
			int cout = mInputStream.available();
			byte[] buffer = new byte[cout];
			int size = mInputStream.read(buffer);
			if (size == 0) {
				return null;
			}
			byte[] data = new byte[10];
			for (int i = 0; i < 10; i++) {
				data[i] = buffer[4 + i];
			}
			return new String(data);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	private int setEncryption(int i) throws IOException {
		byte[] a = { 0x68, 0x06, 0x01, 0x00, 0x00, 0x6F, 0x16 };

		byte[] b = { 0x68, 0x06, 0x01, 0x00, 0x01, 0x70, 0x16 };

		if (i == 0)
			mOutputStream.write(a);
		else
			mOutputStream.write(b);
		mOutputStream.flush();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (mInputStream == null) {
			Log.i("info", "inputstream is null");
			return -1;
		}
		int cout = mInputStream.available();
		byte[] buffer = new byte[cout];
		int size = mInputStream.read(buffer);
		if (size > 5) {
			if (buffer[4] == 0x00 || buffer[4] == 0x01)
				return 0;
			else
				return -1;
		} else
			return -1;

	}
}