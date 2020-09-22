package android_jb.com.POSD.controllers;

import android.util.Log;

import android_jb.cn.gzjb.n20epp.N20Epp;

public class N20PINPadController {

	private static final String TAG = "PINPadController";
	private static N20PINPadController pinPadController = null;
	private static N20Epp N20EppApp = new N20Epp();

	public static N20PINPadController getInstance() {
		Log.i(TAG, "getInstance");
		if (null == pinPadController) {
			pinPadController = new N20PINPadController();
		}
		return pinPadController;
	}

	/**
	 * 功能：打开设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PINPad_Open() {
		int iret;
		//sIoctl.convertRJ11();
		String strPortName = "/dev/ttyS2";
		iret = N20EppApp.pinpad_open(strPortName.getBytes());
		return iret;
	}

	/**
	 * 功能：关闭设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PINPad_Close() {
		return N20EppApp.pinpad_close();
	}

	/**
	 * 
	 * /** 功能：清屏
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int PINPad_clear() {
		try {
			return N20EppApp.pinpad_clear();
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
			byte strLogo[] = text.getBytes();
			return N20EppApp.pinpad_display_string(strLogo, strLogo.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 功能：更新主密钥
	 * 
	 * @param masterindex
	 *            密钥索引[1，100]
	 * @param desflag
	 *            字符1为密钥长度8 反之16
	 * @param keydata
	 *            密钥数据
	 * @param datalen
	 *            数据长度
	 * @return 0:成功,-1或其他值:失败
	 */
	public int PINPad_updateMKey(byte masterindex, byte desflag,
			byte[] keydata, int datalen) {
		int iret = -1;
		iret = N20EppApp.pinpad_inject_masterkey(masterindex, desflag, keydata,
				datalen);
		if (iret != 0) {
			return iret;
		} else {
			return 0;
		}
	}

	/**
	 * 功能：更新工作密钥
	 * 
	 * @param masterindex
	 *            密钥索引[1，100]
	 * @param pinindex
	 *            PIN密钥索引
	 * @param macindex
	 *            MAC密钥索引
	 * @param desindex
	 *            磁道密钥索引
	 * @param desflag
	 *            字符1为MAC密钥KeyData长度为12反之为20
	 * @param keydata
	 *            密钥数据
	 * @param datalen
	 *            数据长度
	 * @return 0:成功,-1或其他值:失败
	 */
	public int PINPad_updateWKey(byte masterindex, byte pinindex,
			byte macindex, byte desindex, byte desflag, byte[] keydata,
			int datalen) {
		int iret = -1;
		iret = N20EppApp.pinpad_inject_workkey(masterindex, pinindex, macindex,
				desindex, desflag, keydata, datalen);
		if (iret != 0) {
			return iret;
		} else {
			return 0;
		}
	}

	/**
	 * 功能：获取输入的密码数据
	 * 
	 * @return 不为null:成功，null:失败
	 */
	public String PINPad_getFromPinPad(String heading) {
		String password = "";
		int iret = -1;
		try {
			N20EppApp.pinpad_clear();
			byte strLogo[] = heading.getBytes();
			N20EppApp.pinpad_display_string(strLogo, strLogo.length);
			byte pinindex = 0x02;
			short type = 3;
			byte mode = 4;
			byte[] pinblock = new byte[9];
			iret = N20EppApp.pinpad_get_plaintext_pin(pinindex, type,
					(char) mode, pinblock);
			if (iret != 0)
				return "ERROR1";
			for (int i = 0; i < pinblock.length; i++) {
				if (!String.valueOf(pinblock[i]).equals("0")) {
					password += (char) pinblock[i];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return password;
	}

	static {
		try {
			System.loadLibrary("N20Epp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
