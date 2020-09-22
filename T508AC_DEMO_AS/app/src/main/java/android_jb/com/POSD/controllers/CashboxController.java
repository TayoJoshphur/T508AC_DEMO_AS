package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import android.util.Log;

public class CashboxController {
	private static CashboxController cashboxController = null;
	private static final String TAG = "CashboxController";
	private String power = "/proc/jbcommon/gpio_control/MBox_CTL";// 默认值：1，其他值无效

	public static CashboxController getInstance() {
		Log.i(TAG, "getInstance");
		if (null == cashboxController) {
			cashboxController = new CashboxController();
		}
		return cashboxController;
	}

	/**
	 * 功能：操作设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int CashboxController_Controller(String value) {
		try {
			int flag = writeFile(new File(power),value);
			if (1 == flag) {
				Log.i(TAG, "CashboxController_Controller_Success");
				//Thread.sleep(300);
//				Ioctl.activate(16, 0);
//				Log.i(TAG, "CashboxController_Controller_Success status: "+Ioctl.get_status(16));
				return 1;
			} else if (0 == flag) {
				Log.i(TAG, "CashboxController_Controller_Have_No_Device");
				return 0;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(TAG, "CashboxController_Controller_Failure");
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
}
