package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import android.util.Log;

import android_jb.com.POSD.util.MachineVersion;

public class BuzzerController {
	private static BuzzerController buzzerController = null;
	private static final String TAG = "BuzzerController";
	private String version = MachineVersion.getMachineVersion();
	private String Beep_PWR_EN = "/proc/jbcommon/gpio_control/Beep_CTL";// 1开，0关

	public static BuzzerController getInstance() {
		Log.i(TAG, "getInstance");
		if (null == buzzerController) {
			buzzerController = new BuzzerController();
		}
		return buzzerController;
	}

	/**
	 * 功能：打开设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int BuzzerController_Open() {
		Log.i(TAG, "BuzzerController_Open");
		int flag = -1;
		flag = writeFile(new File(Beep_PWR_EN),"1");
		if (0 == flag) {
			return 0;
		} else if (-1 == flag) {
			return -1;
		}
		return -1;
	}

	/**
	 * 功能：关闭设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int BuzzerController_Close() {
		Log.i(TAG, "BuzzerController_Close");
		int flag = -1;
		flag = writeFile(new File(Beep_PWR_EN),"0");
		if (0 == flag) {
			return 0;
		} else if (-1 == flag) {
			return -1;
		}
		return -1;
	}
	
	private int writeFile(File file, String value) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(value);
			writer.flush();
			writer.close();
			return 0;
		} catch (IOException e1) {
			e1.printStackTrace();
			return -1;
		}
	}
}
