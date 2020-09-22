package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import android.util.Log;

public class LaserlightController {
	private static LaserlightController laserlightController = null;
	private static final String TAG = "LaserlightController";
	private String power_j = "/proc/jbcommon/gpio_control/PosLight_CTL";// B默认值：1，其他值无效
	private String power_n = "/proc/jbcommon/gpio_control/Led_CTL";// B默认值：1，其他值无效
	
	public static LaserlightController getInstance() {
		Log.i(TAG, "getInstance");
		if (null == laserlightController) {
			laserlightController = new LaserlightController();
		}
		return laserlightController;
	}

	/**
	 * 功能：打开设备 
	 * option: 激光灯控制
	 * 
	 * @return 1:成功，0:失败
	 */
	public int LaserlightController_J_Open() {
		Log.i(TAG, "LaserlightController_Open");
		//int flag = Ioctl.activate(option, 1);
		int flag = writeFile(new File(power_j),"1");
		if (1 == flag) {
			return 1;
		} else if (0 == flag) {
			return 0;
		}
		return 0;
	}

	/**
	 * 功能：打开设备 
	 * option: 内部网摄像头灯光控制
	 * 
	 * @return 1:成功，0:失败
	 */
	public int LaserlightController_N_Open() {
		Log.i(TAG, "LaserlightController_Open");
		//int flag = Ioctl.activate(option, 1);
		int flag = writeFile(new File(power_n),"1");
		if (1 == flag) {
			return 1;
		} else if (0 == flag) {
			return 0;
		}
		return 0;
	}
	
	private static int writeFile(File file, String value) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(value);
			writer.flush();
			writer.close();
			return 1;
		} catch (IOException e1) {
			e1.printStackTrace();
			return 0;
		}
	}
	/**
	 * 功能：关闭设备  
	 * @return 0:成功，-1:失败
	 */
	public int LaserlightController_Close() {
		Log.i(TAG, "LaserlightController_Close");
		int flag1 = writeFile(new File(power_j),"0");
		int flag2 = writeFile(new File(power_n),"0");
		if (1 == flag1&&1 == flag2) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * 功能：关闭设备  
	 * @return 0:成功，-1:失败
	 */
	public int LaserlightController_J_Close() {
		Log.i(TAG, "LaserlightController_J_Close");
		int flag = writeFile(new File(power_j),"0");
		if (1 == flag) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * 功能：关闭设备  
	 * @return 0:成功，-1:失败
	 */
	public int LaserlightController_N_Close() {
		Log.i(TAG, "LaserlightController_N_Close");
		int flag = writeFile(new File(power_n),"0");
		if (1 == flag) {
			return 1;
		} else {
			return 0;
		}
	}
}