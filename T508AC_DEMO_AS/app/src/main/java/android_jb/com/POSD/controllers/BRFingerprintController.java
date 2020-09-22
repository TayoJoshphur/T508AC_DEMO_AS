package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import android_jb.com.BRMicro.IUsbConnState;
import android_jb.com.BRMicro.NETLH_E;


public class BRFingerprintController {
	private static BRFingerprintController FingerprintController = null;
	private NETLH_E netlh;
	private int ret;
	private Activity context;
	private int iBuffer = 1;
	private String IO_OE = "/proc/jbcommon/gpio_control/UART3_EN"; // 默认值：1，其他值无效
	private String IO_CS0 = "/proc/jbcommon/gpio_control/UART3_SEL0";// A默认值：1，其他值无效
	private String IO_CS1 = "/proc/jbcommon/gpio_control/UART3_SEL1";// B默认值：1，其他值无效
	private String power = "/proc/jbcommon/gpio_control/Finger_CTL";// B默认值：1，其他值无效

	public static BRFingerprintController getInstance() {
		if (null == FingerprintController) {
			FingerprintController = new BRFingerprintController();
		}
		return FingerprintController;
	}

	private final IUsbConnState m_IConnectionHandler = new IUsbConnState() {
		public void onUsbConnected() {
			@SuppressWarnings("unused")
			String[] w_strInfo = new String[1];
			@SuppressWarnings("unused")
			int ret = 1;
		}

		public void onUsbPermissionDenied() {
		}

		public void onDeviceNotFound() {
		}
	};
	

	/**
	 * 功能：打开指纹模块
	 * 
	 * @param a
	 *            Activity
	 * @return 0:成功，-1:失败
	 */
	public int FingerprintController_Open(Activity activity) {
		//Ioctl.convertFinger();
		//Ioctl.activate(12, 1);
		writeFile(new File(power),"1");
		writeFile(new File(IO_OE),"0");
		writeFile(new File(IO_CS0),"0");
		writeFile(new File(IO_CS1),"0");
		this.context = activity;
		netlh = new NETLH_E(context, m_IConnectionHandler);
		ret = netlh.ConfigCommParameterCom("/dev/ttyS3", 115200, 8, 2, 0,
				0xffffffff, 0xffffffff);
		if (1 == ret) {
			return 0;
		}
		return -1;
	}

	/**
	 * 功能：关闭指纹模块
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int FingerprintController_Close() {
		int flag = -1;
		if (null != netlh) {
			netlh.CommClose();
			//flag = Ioctl.activate(12, 0);
			SystemClock.sleep(200);
		}
		if (flag == 0) {
			return 0;
		} else if (flag == 1)
			return -1;
		return -1;
	}

	/**
	 * 功能：复位指纹模块
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int FingerprintController_Reset() {
		int[] _ErrFlag = new int[10];
		ret = netlh.CmdDeviceReset(_ErrFlag);
		if ((1 == ret) && (0 == _ErrFlag[0])) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 功能：探测手指
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int FingerprintController_ProbeFinger() {
		if (null != netlh) {
			int[] _ErrFlag = new int[10];
//			ret = netlh.CmdDetectFinger(_ErrFlag);
			ret = netlh.CmdGetRedressImage(0, _ErrFlag);
			if ((1 == ret) && (0 == _ErrFlag[0])) {
				return 0;
			} else {
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
	 * 功能：指纹录入
	 * 
	 * @return -2 指纹已满 -1 失败 返回其他请看状态表
	 */
	public int FingerprintController_FingerprintRecord() {
		if (null != netlh) {
			iBuffer = 1;
			int[] _ErrFlag = new int[10];
			int[] _RetMbIndex = new int[10];
			int[] _RetScore = new int[10];
			for (int i = 0; i < 3; i++) {
				ret = netlh.CmdGetRedressImage(0, _ErrFlag);
				if ((1 == ret) && (0 == _ErrFlag[0])) {
					ret = netlh.CmdGenChar(iBuffer, _ErrFlag);
				}
				iBuffer++;
			}
			if ((1 == ret) && (0 == _ErrFlag[0])) {
				ret = netlh.CmdMergeChar(_RetScore, _ErrFlag);
			} else {
				return _ErrFlag[0];
			}
			if ((1 == ret) && (0 == _ErrFlag[0]) && (_RetScore[0] == 100)) {
				int index = FingerprintController_FigerIndex();
				if (-1 == index) {
					return -2;
				}
				ret = netlh.CmdStoreChar(index, _RetMbIndex, _RetScore,
						_ErrFlag);
			}
			return _ErrFlag[0];
		}
		return -1;
	}

	/**
	 * 功能：读取指纹模板索引
	 * 
	 * @return 返回的是指纹的索引 ,-1:表示失败
	 */
	private int FingerprintController_ReadFigerIndex() {
		if (null != netlh) {
			int[] _ErrFlag = new int[10];
			int[] _RetMbIndex = new int[10];
			int[] _RetScore = new int[10];
			ret = netlh.CmdGetRedressImage(0, _ErrFlag);
			if (1 == ret && _ErrFlag[0] == 0) {
				ret = netlh.CmdGenChar(1, _ErrFlag);
				if (1 == ret && _ErrFlag[0] == 0) {
					ret = netlh.CmdSearchChar(1, _RetMbIndex, _RetScore,
							_ErrFlag);
					if (1 == ret && 0 == _ErrFlag[0]) {
						return _RetMbIndex[0];
					} else {
						return -1;
					}
				} else {
					return _ErrFlag[0];
				}
			} else {
				return _ErrFlag[0];
			}
		}
		return -1;
	}

	private int FingerprintController_FigerIndex() {
		if (null != netlh) {
			int[] _ErrFlag = new int[10];
			byte[] gMBIndex = new byte[1000];
			for (int i = 0; i <= 127; i++) {
				ret = netlh.CmdGetMBIndex(gMBIndex, i, 128, _ErrFlag);
				if (1 == ret && _ErrFlag[0] == 0) {
					if (0 == gMBIndex[0]) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * 功能：搜索匹配的指纹模块
	 * 
	 * @return -1:失败，其他请看状态表
	 */
	public int FingerprintController_FingerprintQuery() {
		if (null != netlh) {
			int[] _ErrFlag = new int[10];
			int[] _RetMbIndex = new int[10];
			int[] _RetScore = new int[10];
			ret = netlh.CmdGetRedressImage(0, _ErrFlag);
			if (1 == ret && _ErrFlag[0] == 0) {
				ret = netlh.CmdGenChar(1, _ErrFlag);
				if (1 == ret && _ErrFlag[0] == 0) {
					ret = netlh.CmdSearchChar(1, _RetMbIndex, _RetScore,
							_ErrFlag);
					if (1 == ret) {
						return _ErrFlag[0];
					} else {
						return -1;
					}
				} else {
					return _ErrFlag[0];
				}
			} else {
				return _ErrFlag[0];
			}
		}
		return -1;
	}

	/**
	 * 功能：删除全部指纹
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int FingerprintController_FingerprintClear() {
		if (null != netlh) {
			int[] _ErrFlag = new int[10];
			ret = netlh.CmdEmptyChar(_ErrFlag);
			if ((1 == ret) && (0 == _ErrFlag[0])) {
				return 0;
			} else {
				return -1;
			}
		}
		return -1;
	}

	/**
	 * 功能：删除单个指纹
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int FingerprintController_FingerprintDelete() {
		int index = FingerprintController_ReadFigerIndex();
		if (index == -1) {
			return -1;
		}
		if (null != netlh) {
			int[] _ErrFlag = new int[10];
			ret = netlh.CmdDelChar(index, _ErrFlag);
			if ((1 == ret) && (0 == _ErrFlag[0])) {
				return 0;
			} else {
				return -1;
			}
		}
		return -1;
	}
}
