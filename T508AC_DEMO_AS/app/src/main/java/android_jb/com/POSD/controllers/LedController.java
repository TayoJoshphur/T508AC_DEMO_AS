package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;


public class LedController {

	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private static LedController ledController;

	public static LedController getInstance() {
		if (null == ledController) {
			ledController = new LedController();
		}
		return ledController;
	}

	/**
	 * 功能：对led硬件的控制，
	 * 
	 * @param cont
	 *            true:上电，false:下电
	 * @return 0：成功，1：失败
	 */
	private int ledCont(boolean cont) {
		try {
			String ctrl_gpio_path = "/dev/ctrl_gpio";
			FileInputStream mCalfdIn = null;
			String str = "00LED_CTL ";
			byte[] buff = str.getBytes();
			buff[(buff.length - 1)] = 0;
			if (cont) {
				buff[1] = 49;
			} else {
				buff[1] = 48;
			}
			mCalfdIn = new FileInputStream(new File(ctrl_gpio_path));
			mCalfdIn.read(buff);
			mCalfdIn.close();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	/**
	 * 功能：打开客显
	 * 
	 * @return 0：成功，1：失败
	 */
	public int LedController_Open() {
		try {
			//Ioctl.convertLed();
			mSerialPort = new SerialPort(new File("/dev/ttyS3"), 9600,8,'0',1,0,0);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			int c = ledCont(true);
			if (0 == c) {
				return 0;
			} else {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 功能：关闭客显
	 * 
	 * @return 0：成功，1：失败
	 */
	public int LedController_Close() {
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
			int c = ledCont(false);
			if (0 == c) {
				return 0;
			} else {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	/**
	 * 功能：让客显显示的数字
	 * 
	 * @param nums
	 *            :需要显示的数字
	 * @return：0：成功，1：失败
	 */
	public int LedController_Close_ShowNums(String nums) {
		try {
			byte[] showHead = { 0x1B, 0x51, 0x41 };
			byte[] showContent = nums.getBytes();
			byte[] writeByte = new byte[4 + showContent.length];
			for (int i = 0; i < showHead.length; i++) {
				writeByte[i] = showHead[i];
			}
			for (int i = 0; i < showContent.length; i++) {
				writeByte[3 + i] = showContent[i];
			}
			writeByte[writeByte.length - 1] = 0x0D;
			mOutputStream.write(writeByte);
			mOutputStream.flush();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

}
