package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import android.util.Log;
import android_serialport_api.SerialPort;

public class PSAMController {
	private static final String TAG = "PSAMController";
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private static PSAMController psamController = null;
	private String IO_CS0 = "/proc/jbcommon/gpio_control/UART2_SEL0";// 默认值：1，其他值无效
	private String IO_CS1 = "/proc/jbcommon/gpio_control/UART2_SEL1";// 默认值：1，其他值无效
	private String PSAM_CTL = "/proc/jbcommon/gpio_control/Psam_CTL";// 默认值：1，其他值无效
	public static PSAMController getInstance() {
		Log.i(TAG, "getInstance");
		if (null == psamController) {
			psamController = new PSAMController();
		}
		return psamController;
	}

	/**
	 * 执行cos命令
	 * 
	 * @param samNum
	 *            要操作哪张sam卡
	 * @param cosMommand
	 *            cos命令
	 */
	public byte[] PSAM_executeCosMommand(int samNum, byte[] cosMommand)
			throws IOException {
		byte[] result = null;
		if (psamController != null && cosMommand != null) {
			byte[] sourceByteArr = new byte[7 + cosMommand.length];
			sourceByteArr[0] = (byte) 0xAA;// 数据长度
			sourceByteArr[1] = 0x66;
			sourceByteArr[2] = (byte) 0x00;// 数据长度 高位
			sourceByteArr[3] = (byte) (sourceByteArr.length - 3);// 长度 地位
			sourceByteArr[4] = 0x38;// 指令码
			sourceByteArr[5] = (byte) (samNum & 0xFF);// 要操作的卡代号
			// 填充cos指令
			for (int i = 0; i < cosMommand.length; i++) {
				sourceByteArr[i + 6] = cosMommand[i];
			}
			// 算校验
			for (int i = 2; i < sourceByteArr.length - 1; i++) {
				sourceByteArr[sourceByteArr.length - 1] = (byte) (((sourceByteArr[sourceByteArr.length - 1] & 0xFF) + (sourceByteArr[i] & 0xFF)) & 0xFF);
			}
			psamController.PSAM_Write(sourceByteArr);
			System.out.println(Tools.bytesToHexString(sourceByteArr));
			
			byte[] buffer = psamController.PSAM_Read();
			if (null != buffer && buffer.length > 0) {
				return buffer;
			}
		}
		return result;
	}

	/**
	 * 功能：打开设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PSAM_Open() {
		Log.i(TAG, "PSAM_Open");
		//Ioctl.convertPSAM();
		writeFile(new File(IO_CS0),"1");
		writeFile(new File(IO_CS1),"0");
		writeFile(new File(PSAM_CTL),"0");//上电
		if (mSerialPort == null) {
			try {
				mSerialPort = new SerialPort(new File("/dev/ttyS2"), 19200, 8,
						'0', 1, 0, 0);
				mOutputStream = mSerialPort.getOutputStream();
				mInputStream = mSerialPort.getInputStream();
				if (mOutputStream != null && null != mInputStream)
					return 0;
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}
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
	 * 功能： 复位
	 * 
	 * @param n
	 *            1 第一个psam卡 2第二个psam卡
	 * 
	 * @return 0:成功，-1:失败
	 */
	private static final byte[] reset_sam1 = { (byte) 0xAA, 0x66, 0x00, 0x04,
			0x37, 0x00, 0x3B };
	private static final byte[] reset_sam2 = { (byte) 0xAA, 0x66, 0x00, 0x04,
			0x37, 0x10, 0x4b };

	public int PSAM_Reset(int n) {
		int flag = -1;
		if (n == 1) {
			flag = PSAM_Reset(reset_sam1);
			if (flag == 0) {
				return 0;
			} else {
				return -1;
			}
		} else if (n == 2) {
			flag = PSAM_Reset(reset_sam2);
			if (flag == 0) {
				return 0;
			} else {
				return -1;
			}
		}
		return -1;
	}

	private int PSAM_Reset(byte[] reset) {
		Log.i(TAG, "PSAM_Reset");
		if (null == mOutputStream || null == mInputStream) {
			return -1;
		}
		try {
			mOutputStream.write(reset);
			Thread.sleep(500);
			int cout = mInputStream.available();
			byte[] buffer1 = new byte[cout];

			cout = 0;
			buffer1 = null;

			cout = mInputStream.available();
			buffer1 = new byte[cout];
			int size = mInputStream.read(buffer1);
			// byte[] buffer2 = PSAM_CMD((byte) 0x37, new byte[] { 0x00 });
			System.out.println(Tools.bytesToHexString(buffer1));
			if (size > 2 && buffer1[4] == (byte) 0x37) {
				return 0;
			} else {
				return -1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 执行命令
	 * 
	 * @param cmd
	 *            命令
	 * @param data
	 *            数据 没有的话就传null
	 * @return byte[] 返回数据
	 */

	@SuppressWarnings("unused")
	private byte[] PSAM_CMD(byte cmd, byte[] data) throws IOException {
		byte[] result = null;
		byte[] sourceByteArr = null;
		if (null != data) {
			sourceByteArr = new byte[3 + 1 + 1 + data.length + 1];
			sourceByteArr[0] = (byte) 0xAA;
			sourceByteArr[1] = (byte) 0x66;
			sourceByteArr[2] = (byte) 0x00;
			sourceByteArr[3] = (byte) (sourceByteArr.length - 3);
			sourceByteArr[4] = (byte) cmd;
			for (int j = 0; j < data.length; ++j) {
				sourceByteArr[5 + j] = data[j];
			}
		} else {
			sourceByteArr = new byte[3 + 1 + 0 + 1];
			sourceByteArr[0] = (byte) 0xAA;
			sourceByteArr[1] = (byte) 0x66;
			sourceByteArr[2] = (byte) 0x00;
			sourceByteArr[3] = (byte) (sourceByteArr.length - 3);
			sourceByteArr[4] = (byte) cmd;
		}
		for (int i = 2; i < sourceByteArr.length - 1; i++) {
			sourceByteArr[sourceByteArr.length - 1] = (byte) (((sourceByteArr[sourceByteArr.length - 1] & 0xFF) + (sourceByteArr[i] & 0xFF)) & 0xFF);
		}
		PSAM_Write(sourceByteArr);
		byte[] buffer = PSAM_Read();
		if (null != buffer && buffer.length > 0) {
			return buffer;
		}
		return result;
	}

	/**
	 * 功能： 关闭
	 * 
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PSAM_Close() {
		Log.i(TAG, "PSAM_Close");
		writeFile(new File(IO_CS0),"0");
		writeFile(new File(PSAM_CTL),"1");//下电
		if (mOutputStream != null || null != mInputStream) {
			try {
				mOutputStream.close();
				mInputStream.close();
				mInputStream = null;
				mOutputStream = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
			return 0;
		}
		return -1;
	}

	/**
	 * 功能： 写数据
	 * 
	 * @param command
	 *            数据
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PSAM_Write(byte[] command) {
		Log.i(TAG, "PSAM_Write");
		if (null == mOutputStream || null == mInputStream) {
			return -1;
		}
		try {
			if (command == null)
				command = "".getBytes();
			mOutputStream.write(command);
			mOutputStream.flush();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 功能： 读数据
	 * 
	 * 
	 * @return byte[] 返回的数据
	 */
	public byte[] PSAM_Read() {
		Log.i(TAG, "PSAM_Read");
		int size = 0;
		if (mInputStream == null) {
			return null;
		}
		int cout;
		try {
			cout = mInputStream.available();
			byte[] buffer1 = new byte[cout];

			cout = 0;
			buffer1 = null;
			Thread.sleep(300);
			cout = mInputStream.available();
			buffer1 = new byte[cout];

			size = mInputStream.read(buffer1);
			System.out.println(Tools.bytesToHexString(buffer1));
			if (0 == size) {
				return null;
			}
			return buffer1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
