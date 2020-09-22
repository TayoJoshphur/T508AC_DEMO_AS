package android_jb.com.POSD.controllers;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import android.app.Activity;

import android_jb.com.POSD.util.MachineVersion;
import android_serialport_api.SerialPort;

public class ZAFingerprintController {
	private byte[] gemumg = { (byte) 0xEF, 0x01, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, 0x01, 0x00, 0x03, 0x01, 0x00, 0x05 };
	private byte[] Img2Tz1 = { (byte) 0xEF, 0x01, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, 0x01, 0x00, 0x04, 0x02, 0x01, 0x00, 0x08 };
	private byte[] Img2Tz2 = { (byte) 0xEF, 0x01, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, 0x01, 0x00, 0x04, 0x02, 0x02, 0x00, 0x09 };
	private byte[] UpChar = { (byte) 0xEF, 0x01, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, 0x01, 0x00, 0x04, 0x08, 0x01, 0x00, 0x0E };
	private byte[] RegModel = { (byte) 0xEF, 0x01, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, 0x01, 0x00, 0x03, 0x05, 0x00, 0x09 };
	private byte[] store = { (byte) 0xEF, 0x01, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, 0x01, 0x00, 0x06, 0x06, 0x02, 0x00, 0x00,
			0x00, 0x0F };
	private byte[] empty = { (byte) 0xEF, 0x01, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, 0x01, 0x00, 0x03, 0x0D, 0x00, 0x11 };
	private byte[] Search = { (byte) 0xEF, 0x01, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, 0x01, 0x00, 0x08, 0x04, 0x01, 0x00, 0x00,
			0x03, (byte) 0xA1, 0x00, (byte) 0xB2 };
	private byte[] DeletChar = { (byte) 0xEF, 0x01, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, 0x01, 0x00, 0x07, 0x0C, 0x00, 0x00, 0x00,
			0x01, (byte) 0x00, 0x00 };

	private static ZAFingerprintController FingerprintController = null;
	private Activity context;
	private SerialPort serialPort;
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	private String version = MachineVersion.getMachineVersion();
	private String IO_OE = "/proc/jbcommon/gpio_control/UART3_EN"; // 默认值：1，其他值无效
	private String IO_CS0 = "/proc/jbcommon/gpio_control/UART3_SEL0";// A默认值：1，其他值无效
	private String IO_CS1 = "/proc/jbcommon/gpio_control/UART3_SEL1";// B默认值：1，其他值无效
	private String power = "/proc/jbcommon/gpio_control/Finger_CTL";// B默认值：1，其他值无效
	public static ZAFingerprintController getInstance() {
		if (null == FingerprintController) {
			FingerprintController = new ZAFingerprintController();
		}
		return FingerprintController;
	}

	/**
	 * 功能：打开指纹模块
	 * 
	 * @param a
	 *            Activity
	 * @return 0:成功，-1:失败
	 */
	public int FingerprintController_Open(Activity a) {
		try {
			writeFile(new File(power),"1");
			writeFile(new File(IO_OE),"0");
			writeFile(new File(IO_CS0),"0");
			writeFile(new File(IO_CS1),"0");
//			serialPort = new SerialPort(new File("/dev/ttyS3"), 57600, 0);
			serialPort = new SerialPort(new File("/dev/ttyS3"), 57600,8,'0',1,0,0);
			mOutputStream = serialPort.getOutputStream();
			mInputStream = serialPort.getInputStream();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
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
	 * 功能：关闭指纹模块
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int FingerprintController_Close() {
		try {
			writeFile(new File(power),"0");
			if (mOutputStream != null) {
				mOutputStream.close();
				mOutputStream = null;
			}
			if (mInputStream != null) {
				mInputStream.close();
				mInputStream = null;
			}
			if (serialPort != null) {
				serialPort.close();
				serialPort = null;
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 功能：删除全部指纹
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int FingerprintController_FingerprintClear() {
		try {
			mOutputStream.write(empty);
			Thread.sleep(300);
			int cout = mInputStream.available();
			byte[] buffer = new byte[cout];
			mInputStream.read(buffer);
			if (0 == buffer[9]) {
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
	 * 功能：删除单个指纹
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int FingerprintController_FingerprintDelete() {
		try {
			int index = FingerprintController_ReadFigerIndex();
			DeletChar[11] = (byte) index;
			byte xn = 0;
			for (int i = 6; i < DeletChar.length - 2; i++) {
				xn += DeletChar[i];
			}
			DeletChar[DeletChar.length - 1] = xn;
			mOutputStream.write(DeletChar);
			Thread.sleep(300);
			int cout = mInputStream.available();
			byte[] buffer = new byte[cout];
			mInputStream.read(buffer);
			if (0 == buffer[9]) {
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
	 * 功能：探测手指
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int FingerprintController_ProbeFinger() {
		try {
			mOutputStream.write(gemumg);
			Thread.sleep(300);
			int cout = mInputStream.available();
			byte[] buffer = new byte[cout];
			mInputStream.read(buffer);
			if (cout == 0) {
				return -1;
			}
			if (0 == buffer[9]) {
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
	 * 功能：指纹录入
	 * 
	 * @return -2 ：指纹已满 ，-1 ：失败， 0 ：成功
	 */
	public int FingerprintController_FingerprintRecord(int index) {
		int runcout = 0;
		try {
			// 连续两录入指纹与合成特征
			while (true) {
				if (2 == runcout) {
					break;
				}
				mOutputStream.write(gemumg);
				Thread.sleep(500);
				int cout = mInputStream.available();
				byte[] buffer = new byte[cout];
				mInputStream.read(buffer);
				if (0 == buffer[9]) {
					if (1 == runcout) {
						mOutputStream.write(Img2Tz2);
					} else {
						mOutputStream.write(Img2Tz1);
					}
					Thread.sleep(300);
					cout = mInputStream.available();
					while (true) {
						if (cout < 12) {
							cout = mInputStream.available();
						} else {
							break;
						}
					}
				}
				buffer = new byte[cout];
				mInputStream.read(buffer);
				if (0 == buffer[9]) {
					runcout++;
				} else {
					runcout = 0;
				}
				if (2 == runcout) {
					mOutputStream.write(RegModel);
					Thread.sleep(300);
					cout = mInputStream.available();
					buffer = new byte[cout];
					mInputStream.read(buffer);
					if (0 == buffer[9]) {
						store[12] = (byte) index;
						byte xn = 0;
						for (int i = 6; i < store.length - 2; i++) {
							xn += (store[i] & 0xff);
						}
						store[store.length - 1] = xn;
						mOutputStream.write(store);
						Thread.sleep(300);
						cout = mInputStream.available();
						buffer = new byte[cout];
						mInputStream.read(buffer);
						if (0 == buffer[9]) {
							return 0;
						} else {
							return -1;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 功能：搜索匹配的指纹模块
	 * 
	 * @return -1:失败，其他请看状态表
	 */
	public int FingerprintController_FingerprintQuery() {
		try {
			mOutputStream.write(gemumg);
			Thread.sleep(500);
			int cout = mInputStream.available();
			byte[] buffer = new byte[cout];
			mInputStream.read(buffer);
			if (0 == buffer[9]) {
				mOutputStream.write(Img2Tz1);
				cout = mInputStream.available();
				while (true) {
					if (cout < 12) {
						cout = mInputStream.available();
					} else {
						break;
					}
				}
			} else {
				return -1;
			}
			buffer = new byte[cout];
			mInputStream.read(buffer);
			if (0 == buffer[9]) {
				mOutputStream.write(Search);
				Thread.sleep(300);
				cout = mInputStream.available();
				while (true) {
					if (cout < 16) {
						cout = mInputStream.available();
					} else {
						break;
					}
				}
				buffer = new byte[cout];
				mInputStream.read(buffer);
				System.out.println("页码 == " + buffer[11]);
				if (0 == buffer[9]) {
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
	 * 功能：读取指纹模板索引
	 * 
	 * @return 返回的是指纹的索引 ,-1:表示失败
	 */
	private int FingerprintController_ReadFigerIndex() {
		try {
			mOutputStream.write(gemumg);
			Thread.sleep(500);
			int cout = mInputStream.available();
			byte[] buffer = new byte[cout];
			mInputStream.read(buffer);
			if (0 == buffer[9]) {
				mOutputStream.write(Img2Tz1);
				cout = mInputStream.available();
				while (true) {
					if (cout < 12) {
						cout = mInputStream.available();
					} else {
						break;
					}
				}
			} else {
				return -1;
			}
			buffer = new byte[cout];
			mInputStream.read(buffer);
			if (0 == buffer[9]) {
				mOutputStream.write(Search);
				Thread.sleep(300);
				cout = mInputStream.available();
				while (true) {
					if (cout < 16) {
						cout = mInputStream.available();
					} else {
						break;
					}
				}
				buffer = new byte[cout];
				mInputStream.read(buffer);
				if (0 == buffer[9]) {
					return buffer[11];
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
	 * 功能：获取指纹数据用于两个指纹数据的对比
	 * 
	 * @return 返回的字符串长度为1024:成功，null:失败
	 */
	public String FingerprintController_GetFingerData() {
		String up = "";
		try {
			mOutputStream.write(gemumg);
			Thread.sleep(300);
			int cout = mInputStream.available();
			byte[] buffer = new byte[cout];
			mInputStream.read(buffer);
			if (0 == buffer[9]) {
				mOutputStream.write(Img2Tz1);
				cout = mInputStream.available();
				while (true) {
					if (cout < 12) {
						cout = mInputStream.available();
					} else {
						break;
					}
				}
				buffer = new byte[cout];
				mInputStream.read(buffer);
				if (0 == buffer[9]) {
					mOutputStream.write(UpChar);
					cout = mInputStream.available();
					while (true) {
						if (cout < 12) {
							cout = mInputStream.available();
						} else {
							break;
						}
					}
					buffer = new byte[cout];
					mInputStream.read(buffer);
					Thread.sleep(300);
					if (0 == buffer[9]) {
						cout = mInputStream.available();
						Thread.sleep(300);
						cout = mInputStream.available();
						buffer = new byte[cout];
						mInputStream.read(buffer);
						String acs = "";
						for (int i = 0; i < buffer.length; i++) {
							if (i >= 417 && i <= 425) {
								continue;
							} else if (i >= 278 && i <= 286) {
								continue;
							} else if (i >= 139 && i <= 147) {
								continue;
							} else if (i >= 0 && i <= 8) {
								continue;
							}
							if (i >= 554 && i <= 555) {
								continue;
							} else if (i >= 415 && i <= 416) {
								continue;
							} else if (i >= 276 && i <= 277) {
								continue;
							} else if (i >= 137 && i <= 138) {
								continue;
							} else {

								acs = Integer.toHexString(buffer[i] & 0xff);
								if (acs.length() < 2) {
									acs = "0" + acs;
								}
								up += acs;
								acs = "";
							}
						}
					}
					return up;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
