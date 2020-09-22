package android_jb.cn.gzjb.n20epp;


public class PinPadN20Utils {

	private N20Epp N20EppApp;

	// This method should be the first to be called.
	// The purpose of this method is to open the serial port.
	// return value:
	// zero representative succeed,
	// non-zero representative failure
	public int PinPadOpen() {
		N20EppApp = new N20Epp();
		int iret;
		//Ioctl.convertRJ11();
		String strPortName = "/dev/ttyS2";
		iret = N20EppApp.pinpad_open(strPortName.getBytes());
		return iret;
	}

	// The purpose of this method is to close the serial port.
	// return value:
	// zero representative succeed,
	// non-zero representative failure
	public int PinPadClose() {
		return N20EppApp.pinpad_close();
	}

	// This method only needs to be called once.
	// The purpose of this method is to inject the master key and the work keys.
	// return value:
	// zero representative succeed,
	// non-zero representative failure
	public int PinPadInit() {
		int iret = -1;
		try {
			byte masterindex = 0x01;
			byte desflag = '0';
			byte[] keydata = { 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31,
					0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31 };
			int datalen = 16;

			iret = N20EppApp.pinpad_inject_masterkey(masterindex, desflag,
					keydata, datalen);
			if (iret != 0)
				return iret;
			byte pinindex = 0x02;
			byte macindex = 0x04;
			byte desindex = 0x06;
			byte[] workkeydata = { (byte) 0xC6, (byte) 0x97, (byte) 0xFF,
					(byte) 0xE6, (byte) 0xEC, (byte) 0x86, (byte) 0x9A, 0x10,
					(byte) 0xC1, (byte) 0xD3, 0x46, 0x19, (byte) 0xA7, 0x43,
					(byte) 0xF4, 0x6E, (byte) 0x9C, (byte) 0xA2, (byte) 0xC6,
					(byte) 0x89, (byte) 0xF0, 0x44, (byte) 0x86, 0x35,
					(byte) 0xAC, 0x44, 0x32, 0x69, 0x00, 0x00, 0x00, 0x00,
					0x00, 0x00, 0x00, 0x00, (byte) 0xA2, 0x46, (byte) 0xBB,
					(byte) 0xB8, (byte) 0xC6, (byte) 0x97, (byte) 0xFF,
					(byte) 0xE6, (byte) 0xEC, (byte) 0x86, (byte) 0x9A, 0x10,
					(byte) 0xC1, (byte) 0xD3, 0x46, 0x19, (byte) 0xA7, 0x43,
					(byte) 0xF4, 0x6E, (byte) 0x9C, (byte) 0xA2, (byte) 0xC6,
					(byte) 0x89 };
			desflag = '0';
			datalen = 60;
			iret = N20EppApp.pinpad_inject_workkey(masterindex, pinindex,
					macindex, desindex, desflag, workkeydata, datalen);
			if (iret != 0)
				return iret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iret;
	}

	public int PinPad_clear() {
		try {
			return N20EppApp.pinpad_clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int sendToPinPad(String text) {
		try {
			byte strLogo[] = text.getBytes();
			return N20EppApp.pinpad_display_string(strLogo, strLogo.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	// The purpose of this method is to get plaintext pin
	public String getFromPinPad(String heading) {
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
