package android_jb.com.BRMicro;

import java.io.DataOutputStream;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

@SuppressLint({ "DefaultLocale", "SdCardPath" })
public class NETLH_E {

	private final String TAG = "=NETLH_E=";

	private static final short PACK_HEAD = 0x02EF;
	private static final short PACK_CMD = 0x01;
	private static final short PACK_DATA = 0x02;
	private static final short BROADCASTADDR = 0xFFFFFFFF;
	private static final byte CMD_READ = (byte) 0x85;
	private static final byte CMD_WRITE = (byte) 0x86;
	private static final byte CMD_JUDGEDISK = (byte) 0x87;
	private static final short DEFAULT_DEVICE_JUDGE_HEART = 0x3355;

	private static final short NETLH_PACKHEAD = 17;
	private static final short NETLH_PACKDATA = 820;
	private static final short NETLH_CRC_CHECK = 2;
	private static final short NETLH_PACKLENGTH = (NETLH_PACKHEAD
			+ NETLH_PACKDATA + NETLH_CRC_CHECK);

	private static final short NETLH_PACKLEN_POS_LH = 15;

	private static final short NETLH_DATA_ACK = 2;

	private static final short CMD_DEVICE_RESET = 0x0320;
	private static final short CMD_DETECT_FINGER = 0x0321;
	private static final short CMD_GET_RAW_IMAGE = 0x0322;
	private static final short CMD_GET_REDRESS_IMAGE = 0x0323;
	private static final short CMD_UPLOAD_IMAGE = 0x0324;
	private static final short CMD_GEN_CHAR = 0x0325;
	private static final short CMD_MATCH_CHAR = 0x0326;
	private static final short CMD_STORE_CHAR = 0x0327;
	private static final short CMD_SEARCH_CHAR = 0x0328;
	private static final short CMD_DELETE_CHAR = 0x0329;
	private static final short CMD_EMPTY_CHAR = 0x032A;
	private static final short CMD_VERIFY_CHAR = 0x032B;
	private static final short CMD_GET_CHAR = 0x032C;
	private static final short CMD_PUT_CHAR = 0x032D;
	private static final short CMD_GET_MBINDEX = 0x032E;
	private static final short CMD_READ_NOTE_BOOK = 0x032F;
	private static final short CMD_WRITE_NOTE_BOOK = 0x0330;
	private static final short CMD_READ_PAR_TABLE = 0x0331;
	private static final short CMD_SET_BAUD_RATE = 0x0332;
	private static final short CMD_SET_SECURLEVEL = 0x0333;
	private static final short CMD_SET_CMOS_PARA = 0x0334;
	private static final short CMD_RESUME_FACTORY = 0x0335;
	private static final short CMD_MERGE_CHAR = 0x0336;
	private static final short CMD_SET_PSW = 0x0337;
	private static final short CMD_SET_ADDRESS = 0x0338;
	private static final short CMD_GET_RANDOM = 0x0339;
	private static final short CMD_DOWNLOAD_IMAGE = 0x0340;
	private static final short CMD_ERASE_PROGRAM = 0x0341;
	private static final short CMD_STORE_CHAR_DIRECT = 0x0342;
	private static final short CMD_READ_CHAR_DIRECT = 0x0343;

	private static final short CMD_SET_CARD_TYPE = 0x0410;
	private static final short CMD_GET_CARD_ID_TYPEA = 0x0411;
	private static final short CMD_GET_CARD_ID_TYPEB = 0x0412;

	private static final short CMD_RT_OK = 0x0000;
	private static final short CMD_RT_PACKGE_ERR = 0x0001;
	private static final short CMD_RT_DEVICE_ADDRESS_ERR = 0x0002;
	private static final short CMD_RT_COM_PASSWORD_ERR = 0x0003;
	private static final short CMD_RT_NO_FINGER = 0x0004;
	private static final short CMD_RT_GET_IMAGE_FAILE = 0x0005;
	private static final short CMD_RT_GEN_CHAR_ERR = 0x0006;
	private static final short CMD_RT_FINGER_MATCH_ERR = 0x0007;
	private static final short CMD_RT_FINGER_SEARCH_FAILE = 0x0008;
	private static final short CMD_RT_MERGE_TEMPLET_FAILE = 0x0009;
	private static final short CMD_RT_ADDRESS_OVERFLOW = 0x000A;
	private static final short CMD_RT_READ_TEMPLET_ERR = 0x000B;
	private static final short CMD_RT_UP_TEMPLET_ERR = 0x000C;
	private static final short CMD_RT_UP_IMAGE_FAILE = 0x000D;
	private static final short CMD_RT_DELETE_TEMPLET_ERR = 0x000E;
	private static final short CMD_RT_CLEAR_TEMPLET_LIB_ERR = 0x000F;
	private static final short CMD_RT_FINGER_NOT_MOVE = 0x0010;
	private static final short CMD_RT_NO_TEMPLET_IN_ADDRESS = 0x0011;
	private static final short CMD_RT_CHAR_REPEAT = 0x0012;
	private static final short CMD_RT_MB_NOT_EXIST_IN_ADDRESS = 0x0013;
	private static final short CMD_RT_GET_MBINDEX_OVERFLOW = 0x0014;
	private static final short CMD_RT_SET_BAUD_RATE_FAILE = 0x0015;
	private static final short CMD_RT_ERASE_FLAG_FAILE = 0x0016;
	private static final short CMD_RT_SYSTEM_RESET_FAILE = 0x0017;
	private static final short CMD_RT_OPERATION_FLASH_ERR = 0x0018;
	private static final short CMD_RT_NOTE_BOOK_ADDRESS_OVERFLOW = 0x0018;
	private static final short CMD_RT_PARA_ERR = 0x0019;
	private static final short CMD_RT_NO_CMD = 0x001A;

	// ic card
	private static final short IC_BLOCK_SIZE = 16;
	private static final short CMD_READ_ID_CARD = 0x040D;
	private static final short LOADKEY_LENGTH = (6);

	private static final short CMD_IC_REQUEST = 0x0413;
	private static final short CMD_IC_ANTICOLL = 0x0414;
	private static final short CMD_IC_SELECT = 0x0415;
	private static final short CMD_IC_HALT = 0x0416;
	private static final short CMD_IC_LOAD_KEY = 0x0417;
	private static final short CMD_IC_CHECK_KEY = 0x0418;
	private static final short CMD_IC_READ_BIOCK = 0x0419;
	private static final short CMD_IC_WRITE_BIOCK = 0x041A;
	private static final short CMD_IC_INIT_MONEY = 0x041B;
	private static final short CMD_IC_INCREMENT_MONEY = 0x041C;
	private static final short CMD_IC_DECREMENT_MONEY = 0x041D;
	private static final short CMD_IC_TRANSFER_MONEY = 0x041E;

	public static final int CMD_RT_CONNECTIONED = 1;
	public static final int CMD_RT_DISCONNECTION = 0;

	private static final short DEFAULT_PACK_NUM = 0;

	public static final String DEFAULT_BMP_FILE_PATH = "/mnt/sdcard/Finger.bmp";
	public static final short RAW_IMAGE_X = 312;
	public static final short RAW_IMAGE_Y = 232;
	public static final short REDRESS_IMAGE_X = 256;
	public static final short REDRESS_IMAGE_Y = 288;
	public static final short REDRESS_IMAGE_BIG_Y = 360;
	public static final int DEFAULT_FINGER_IMAGE_MAX_LENG = 256 * 360 * 2; // 184320
	public static final int USB_MAX_PACKAGE = 32768;
	private static final short CHAR_EALG_SIZE = 256;
	private static final short CHAR_XALG_SIZE = 810;
	private static final short NOTE_BOOK_PAGE_SIZE = 32;

	private final Context mApplicationContext;
	private Activity m_parentAcitivity;
	private static final int VID = 0x0483; // 0x2009; 0x2109;//
	private static final int PID = 0x5720; // 0x7638;0x7638;//

	private UsbController m_usbBase;

	private final int USB_NO_ROOT_TYPE = 0;
	private final int USB_TYPE = 1;
	private final int SERIAL_TYPE = 2;
	private int mTimeOutValue = 1000;

	private int mConnType = USB_TYPE;

	public NETLH_E(Activity parentActivity, IUsbConnState usbConnState) {
		m_parentAcitivity = parentActivity;
		mApplicationContext = parentActivity.getApplicationContext();
		m_usbBase = new UsbController(parentActivity, usbConnState, VID, PID);
	}

	public boolean IsInit() {
		return m_usbBase.IsInit();
	}

	public boolean OpenComm() {
		m_usbBase.init();

		return true;
	}

	public boolean CloseComm() {
		m_usbBase.uninit();
		return true;
	}

	private final IUsbConnState m_IConnectionHandler = new IUsbConnState() {
		@SuppressWarnings("unused")
		@Override
		public void onUsbConnected() {
			String[] w_strInfo = new String[1];
		}

		@Override
		public void onUsbPermissionDenied() {
			Log.d(TAG, "Permission denied!");
		}

		@Override
		public void onDeviceNotFound() {
			Log.d(TAG, "Can not find usb device!");
		}
	};

	public native int CmdDeviceInitGetPath(byte[] path);

	public int CmdDeviceGetChmod(int ErrCode) {
		int ret = 1;
		byte[] path = new byte[128];

		CmdDeviceInitGetPath(path);
		String spath = new String(path);
		String sspath = spath.substring(0, spath.indexOf('\0'));
		Process process = null;
		DataOutputStream os = null;
		String command = "chmod 777 " + sspath;
		Log.d(TAG, " exec " + command);
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			ret = 0;
		}

		return ret;
	}

	public int CmdDeviceGetChmod(String path) {
		String command = "chmod 777 " + path;
		Log.d(TAG, " exec " + command);
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
		}
		return 0;
	}

	public native int GetComList(char[] ComList); // /

	public native int AsciiToHex(char[] _pInData, int _nInLength,
			char[] _pOutData, int[] _nOutLength);// /

	public native int GetCurrentDirectoryPath(char[] _pCurrentPath, int _pLenth);// /

	public native int GetAppDirectoryPath(char[] _pCurrentPath, int _pLenth); // /

	public native int SetAppDirectoryPath(char[] _pCurrentPath, int _pLenth); // /

	public native int NConfigCommParameterUDisk(int _DeviceAdd, int _Password);

	public native int NConfigCommParameterCom(String _COM, int _BaudRate,
			int _DataBit, int _StopBit, int _CheckMode, int _DeviceAdd,
			int _Password);

	public native int NCmdDeviceReset(int _ErrFlag[]); //

	public native int NCmdDetectFinger(int _ErrFlag[]);//

	public native int NCmdGetRawImage(int _ErrFlag[]);//

	public native int NCmdGetRedressImage(int _DetectDn, int _ErrFlag[]);// /

	public native int NCmdUpLoadRawImage(byte[] _ImageBuf);//

	public native int NCmdUpLoadRedressImage(byte[] _ImageBuf);//

	public native int NCmdGenChar(int iBuffer, int _ErrFlag[]);//

	public native int NCmdMatchChar(int _RetScore[], int _ErrFlag[]);//

	public native int NCmdStoreChar(int m_Addr, int _RetMbIndex[],
			int _RetScore[], int _ErrFlag[]); //

	public native int NCmdSearchChar(int iBuffer, int _RetMbIndex[],
			int _RetScore[], int _ErrFlag[]); //

	public native int NCmdGetChar_eAlg(int iBuffer, byte[] CharBuf,
			int[] _ErrFlag);// /

	public native int NCmdGetChar_xAlg(int iBuffer, byte[] CharBuf,
			int[] _ErrFlag);// /

	public native int NCmdPutChar_eAlg(int iBuffer, byte[] CharBuf,
			int[] _ErrFlag);// /

	public native int NCmdPutChar_xAlg(int iBuffer, byte[] CharBuf,
			int[] _ErrFlag);// /

	public native int NCmdGetMBIndex(byte[] gMBIndex, int gMBIndexStart,
			int gMBIndexNum, int _ErrFlag[]); //

	public native int NCmdEmptyChar(int _ErrFlag[]);//

	public native int NCmdDelChar(int m_Addr, int _ErrFlag[]);//

	public native int NCmdVerifyChar(int iBuffer, int m_Addr, int _RetScor[],
			int _ErrFlag[]); //

	public native int NCmdReadNoteBook(int _PageID, byte[] _NoteText,
			int _ErrFlag[]); //

	public native int NCmdWriteNoteBook(int _PageID, byte[] _NoteText,
			int _ErrFlag[]); //

	public native int NCmdReadParaTable(PARA_TABLE _ParaTable, int _ErrFlag[]); //

	/*
	 * PARA_TABLE ParaTable = new PARA_TABLE(); CmdReadParaTable(ParaTable, );
	 */
	public native int NCmdSetBaudRate(int _BaudRate, int _ErrFlag[]); //

	public native int NCmdSetSecurLevel(int _SecurLevel, int _ErrFlag[]); //

	public native int NCmdSetCmosPara(int _ExposeTimer, int DetectSensitive,
			int _ErrFlag[]); //

	public native int CmdGetRawImageBuf(byte[] _ImageBuf);

	public native int NCmdEraseProgram(int _ErrFlag[]); //

	public native int NCmdResumeFactory(int _ErrFlag[]);//

	public native void NCommClose();

	public native int GetLastCommErr();

	public native int GetLastCommSystemErr();

	public native void NSetTimeOutValue(int _TimeOutValue);

	public native int NGetTimeOutValue();

	public native int NCmdUpLoadRedressImage256x360(byte[] _ImageBuf);

	public native int NCmdMergeChar(int[] _RetScore, int[] _ErrFlag);

	public native int NCmdStoreCharDirect_eAlg(int m_Addr, byte[] _FingerChar,
			int[] _ErrFlag);

	public native int NCmdStoreCharDirect_xAlg(int m_Addr, byte[] _FingerChar,
			int[] _ErrFlag);

	public native int NCmdReadCharDirect_eAlg(int m_Addr, byte[] _FingerChar,
			int[] _ErrFlag);

	public native int NCmdReadCharDirect_xAlg(int m_Addr, byte[] _FingerChar,
			int[] _ErrFlag);

	public native int NCmdSetPsw(int _NewPsw, int[] _ErrFlag);

	public native int NCmdSetDeviceAddress(int _NewAddress, int[] _ErrFlag);

	public native int NCmdGetRandom(int[] _Random, int[] _ErrFlag);

	public native int CmdSendDemon(byte[] data, int[] ErrFlag);

	// IC CARD

	public native int NCmdICRequest(int _Mode, byte _CardType[],/* 2 bytes */
			int _ErrFlag[]);

	public native int NCmdICAnticoll(int _Bcnt, byte _CardNum[],/* 4 bytes */
			int _ErrFlag[]);

	public native int NCmdICSelect(byte _Size[],/* 1 bytes */int _CardNum,
			int _ErrFlag[]);

	public native int NCmdICHalt(int _ErrFlag[]);

	public native int NCmdICLoadKey(byte _LoadKey[], /* 6 bytes */int _ErrFlag[]);

	public native int NCmdICAuthentication(int _Sector, int _AuthMode,
			int _CardNum, int _ErrFlag[]);

	public native int NCmdICReadBlock(int _SectorIndex, int _BlockIndex,
			byte _BlockBuf[], int _ErrFlag[]);

	public native int NCmdICWriteBlock(int _SectorIndex, int _BlockIndex,
			byte _BlockBuf[], /* IC_BLOCK_SIZE 16 bytes */int _ErrFlag[]);

	public native int NCmdICInitMoney(int _SectorIndex, int _BlockIndex,
			int _Value, int _ErrFlag[]);

	public native int NCmdICIncrementMoney(int _SectorIndex, int _BlockIndex,
			int _Value, int _ErrFlag[]);

	public native int NCmdICDecrementMoney(int _SectorIndex, int _BlockIndex,
			int _Value, int _ErrFlag[]);

	public native int NCmdICTransferMoney(int _SectorIndex, int _BlockIndex,
			int _ErrFlag[]);

	public native int NCmdGetCardIdTypeB(byte _Card_ID[], /* 8 bytes */
			int _ErrFlag[]);

	public native int NCmdGetCardIdTypeA(byte _Card_ID[], /* 4 bytes */
			int _ErrFlag[]);

	public native int NCmdSetCardType(int _CardType, int _ErrFlag[]);

	private native int NCmdGetParaTable(PARA_TABLE _ParaTable, byte[] rxbuff);

	public int ConfigCommParameterUDisk(int _DeviceAdd, int _Password) {
		mConnType = USB_NO_ROOT_TYPE;
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NConfigCommParameterUDisk(_DeviceAdd, _Password);
		}
		NConfigCommParameter(_DeviceAdd, _Password);
		this.CloseComm();
		if (!IsInit()) {
			if (OpenComm()) {
				// success
			} else {
				// failed
				return 0;
			}
		} else {
			if (CmdMyJudgeDisk())
				return 1;
			else
				return 0;
		}

		return 2;
	}

	public native int NConfigCommParameter(int _DeviceAdd, int _Password);

	public int ConfigCommParameterCom(String _COM, int _BaudRate, int _DataBit,
			int _StopBit, int _CheckMode, int _DeviceAdd, int _Password) {
		mConnType = SERIAL_TYPE;
		return NConfigCommParameterCom(_COM, _BaudRate, _DataBit, _StopBit,
				_CheckMode, _DeviceAdd, _Password);
	}

	public int CmdDetectFinger(int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdDetectFinger(_ErrFlag);
		}

		boolean w_bRet;
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_DETECT_FINGER,
				PACK_CMD, 0, new byte[1], m_abyPacket);

		w_bRet = USB_post(CMD_DETECT_FINGER);

		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;

		w_bRet = USB_get(CMD_DETECT_FINGER);

		// byteArrToHexString(m_abyPacket, "-");

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;

		_ErrFlag[0] = GetRetCode();
		return CMD_RT_CONNECTIONED;
	}

	public int CmdReadParaTable(PARA_TABLE _ParaTable, int _ErrFlag[]) {

		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdReadParaTable(_ParaTable, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_READ_PAR_TABLE,
				PACK_CMD, 0, new byte[1], m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_READ_PAR_TABLE);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_READ_PAR_TABLE);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		// 7. get the result
		NCmdGetParaTable(_ParaTable, m_abyPacket);

		return CMD_RT_CONNECTIONED;

	}

	public int CmdGetRawImage(int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdGetRawImage(_ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_GET_RAW_IMAGE,
				PACK_CMD, 0, new byte[1], m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_GET_RAW_IMAGE);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_GET_RAW_IMAGE);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdGetRedressImage(int _DetectDn, int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdGetRedressImage(_DetectDn, _ErrFlag);
		}
		boolean w_bRet;
		byte[] detectDn = new byte[1];
		detectDn[0] = (byte) (_DetectDn & 0xFF);
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_GET_REDRESS_IMAGE,
				PACK_CMD, 1, detectDn, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_GET_REDRESS_IMAGE);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_GET_REDRESS_IMAGE);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdUpLoadRawImage(byte[] _ImageBuf) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdUpLoadRawImage(_ImageBuf);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_UPLOAD_IMAGE,
				PACK_CMD, 0, new byte[1], m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_UPLOAD_IMAGE);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response

		byte[] FingerImageBuf = new byte[DEFAULT_FINGER_IMAGE_MAX_LENG];
		w_bRet = USB_get(CMD_UPLOAD_IMAGE, FingerImageBuf,
				DEFAULT_FINGER_IMAGE_MAX_LENG, USB_MAX_PACKAGE);
		if (!w_bRet)
			return CMD_RT_DISCONNECTION;

		if (null == _ImageBuf) {
			NCmdCreatFileOption(DEFAULT_BMP_FILE_PATH, FingerImageBuf,
					RAW_IMAGE_X, RAW_IMAGE_Y);
		} else {
			System.arraycopy(FingerImageBuf, 0, _ImageBuf, 0, RAW_IMAGE_X
					* RAW_IMAGE_Y);
		}

		return CMD_RT_CONNECTIONED;

	}

	public int CmdUpLoadRedressImage256x360(byte[] _ImageBuf) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdUpLoadRedressImage256x360(_ImageBuf);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_UPLOAD_IMAGE,
				PACK_CMD, 0, new byte[1], m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_UPLOAD_IMAGE);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response

		byte[] FingerImageBuf = new byte[256 * 360 * 2];
		w_bRet = USB_get(CMD_UPLOAD_IMAGE, FingerImageBuf, 256 * 360 * 2,
				USB_MAX_PACKAGE);
		if (!w_bRet)
			return CMD_RT_DISCONNECTION;

		if (null == _ImageBuf) {
			NCmdCreatFileOption(DEFAULT_BMP_FILE_PATH, FingerImageBuf,
					REDRESS_IMAGE_X, REDRESS_IMAGE_BIG_Y);
		} else {
			System.arraycopy(FingerImageBuf, 0, _ImageBuf, 0, REDRESS_IMAGE_X
					* REDRESS_IMAGE_BIG_Y);
		}

		return CMD_RT_CONNECTIONED;

	}

	public int CmdUpLoadRedressImage(byte[] _ImageBuf) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdUpLoadRedressImage(_ImageBuf);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_UPLOAD_IMAGE,
				PACK_CMD, 0, new byte[1], m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_UPLOAD_IMAGE);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response

		byte[] FingerImageBuf = new byte[REDRESS_IMAGE_X * REDRESS_IMAGE_Y];
		w_bRet = USB_get(CMD_UPLOAD_IMAGE, FingerImageBuf, REDRESS_IMAGE_X
				* REDRESS_IMAGE_Y, USB_MAX_PACKAGE);
		if (!w_bRet)
			return CMD_RT_DISCONNECTION;

		if (null == _ImageBuf) {
			NCmdCreatFileOption(DEFAULT_BMP_FILE_PATH, FingerImageBuf,
					REDRESS_IMAGE_X, REDRESS_IMAGE_Y);
		} else {
			System.arraycopy(FingerImageBuf, 0, _ImageBuf, 0, REDRESS_IMAGE_X
					* REDRESS_IMAGE_Y);
		}
		return CMD_RT_CONNECTIONED;
	}

	public boolean CmdMyJudgeDisk() {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return true;
		}
		boolean w_bRet = false;
		byte[] btCDB = new byte[16];
		byte[] w_WaitPacket = new byte[2];
		int[] w_nLen = new int[1];
		memset(btCDB, (byte) 0, 8);
		Arrays.fill(w_WaitPacket, (byte) 0);

		btCDB[0] = (byte) 0x87;
		btCDB[1] = 0;
		btCDB[2] = 0;
		btCDB[3] = 0;
		btCDB[4] = (byte) 0xFF;
		w_nLen[0] = 2;

		w_bRet = m_usbBase.UsbSCSIRead(btCDB, 6, w_WaitPacket, w_nLen,
				SCSI_TIMEOUT, false);

		short now = (short) ((w_WaitPacket[1] << 8) & 0xFF00 | (w_WaitPacket[0]) & 0xFF);
		if (w_bRet) {
			if (DEFAULT_DEVICE_JUDGE_HEART == now && CmdSCSIExecuteInquiry()) {
				Log.d(TAG, "MyJudgeDisk: This is my devices.");

			} else {
				Log.d(TAG, "MyJudgeDisk: This is not my devices:" + now);
				return false;
			}
		} else {
			Log.d(TAG, "MyJudgeDisk: Connect failed");

		}
		return w_bRet;

	}

	public void CommClose() {
		if (USB_NO_ROOT_TYPE != mConnType) {
			NCommClose();
		}
		this.CloseComm();
	}

	public int CmdGenChar(int iBuffer, int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdGenChar(iBuffer, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (iBuffer & 0xFF); // low
		wiBuffer[1] = (byte) ((iBuffer >> 8) & 0xFF);
		wiBuffer[2] = (byte) ((iBuffer >> 16) & 0xFF);
		wiBuffer[3] = (byte) ((iBuffer >> 24) & 0xFF); // high

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_GEN_CHAR, PACK_CMD, 4,
				wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_GEN_CHAR);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_GEN_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdSearchChar(int iBuffer, int _RetMbIndex[], int _RetScore[],
			int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdSearchChar(iBuffer, _RetMbIndex, _RetScore, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (iBuffer & 0xFF); // low
		wiBuffer[1] = (byte) ((iBuffer >> 8) & 0xFF);
		wiBuffer[2] = (byte) ((iBuffer >> 16) & 0xFF);
		wiBuffer[3] = (byte) ((iBuffer >> 24) & 0xFF); // high

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_SEARCH_CHAR, PACK_CMD,
				4, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_SEARCH_CHAR);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_SEARCH_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		// 7. get the arg
		_RetMbIndex[0] = (int) MAKEWORD(m_abyPacket[NETLH_PACKHEAD
				+ NETLH_DATA_ACK], m_abyPacket[NETLH_PACKHEAD + NETLH_DATA_ACK
				+ 1]);
		_RetScore[0] = (int) MAKEWORD(m_abyPacket[NETLH_PACKHEAD
				+ NETLH_DATA_ACK + 2], m_abyPacket[NETLH_PACKHEAD
				+ NETLH_DATA_ACK + 3]);
		return CMD_RT_CONNECTIONED;

	}

	public int CmdMergeChar(int[] _RetScore, int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdMergeChar(_RetScore, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_MERGE_CHAR, PACK_CMD,
				0, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_MERGE_CHAR);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_MERGE_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		// 7. get the arg
		_RetScore[0] = (int) MAKEWORD(m_abyPacket[NETLH_PACKHEAD
				+ NETLH_DATA_ACK], m_abyPacket[NETLH_PACKHEAD + NETLH_DATA_ACK
				+ 1]);
		return CMD_RT_CONNECTIONED;

	}

	public int CmdStoreChar(int m_Addr, int _RetMbIndex[], int _RetScore[],
			int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdStoreChar(m_Addr, _RetMbIndex, _RetScore, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (m_Addr & 0xFF); // low
		wiBuffer[1] = (byte) ((m_Addr >> 8) & 0xFF);// high

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_STORE_CHAR, PACK_CMD,
				2, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_STORE_CHAR);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_STORE_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		// 7. get the arg
		_RetMbIndex[0] = (int) MAKEWORD(m_abyPacket[NETLH_PACKHEAD
				+ NETLH_DATA_ACK], m_abyPacket[NETLH_PACKHEAD + NETLH_DATA_ACK
				+ 1]);
		_RetScore[0] = (int) MAKEWORD(m_abyPacket[NETLH_PACKHEAD
				+ NETLH_DATA_ACK + 2], m_abyPacket[NETLH_PACKHEAD
				+ NETLH_DATA_ACK + 3]);
		return CMD_RT_CONNECTIONED;

	}

	public int CmdGetMBIndex(byte[] gMBIndex, int gMBIndexStart,
			int gMBIndexNum, int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdGetMBIndex(gMBIndex, gMBIndexStart, gMBIndexNum,
					_ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (gMBIndexStart & 0xFF); // low
		wiBuffer[1] = (byte) ((gMBIndexStart >> 8) & 0xFF);// high
		wiBuffer[2] = (byte) (gMBIndexNum & 0xFF); // low
		wiBuffer[3] = (byte) ((gMBIndexNum >> 8) & 0xFF);// high

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_GET_MBINDEX, PACK_CMD,
				4, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_GET_MBINDEX);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_GET_MBINDEX);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		// 7. get the arg

		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK,
				gMBIndex, 0, m_nPacketSize - 12);

		return CMD_RT_CONNECTIONED;

	}

	public int CmdEmptyChar(int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdEmptyChar(_ErrFlag);
		}

		boolean w_bRet;
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_EMPTY_CHAR, PACK_CMD,
				0, new byte[1], m_abyPacket);

		w_bRet = USB_post(CMD_EMPTY_CHAR);

		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;

		w_bRet = USB_get(CMD_EMPTY_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;

		_ErrFlag[0] = GetRetCode();
		return CMD_RT_CONNECTIONED;

	}

	public int CmdVerifyChar(int iBuffer, int m_Addr, int _RetScor[],
			int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdVerifyChar(iBuffer, m_Addr, _RetScor, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[6];
		wiBuffer[0] = (byte) (iBuffer & 0xFF); // low
		wiBuffer[1] = (byte) ((iBuffer >> 8) & 0xFF);
		wiBuffer[2] = (byte) ((iBuffer >> 16) & 0xFF);
		wiBuffer[3] = (byte) ((iBuffer >> 24) & 0xFF);

		wiBuffer[4] = (byte) (m_Addr & 0xFF);
		wiBuffer[5] = (byte) ((m_Addr >> 8) & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_VERIFY_CHAR, PACK_CMD,
				6, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_VERIFY_CHAR);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_VERIFY_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		// 7. get the arg
		_RetScor[0] = (int) MAKEWORD(m_abyPacket[NETLH_PACKHEAD
				+ NETLH_DATA_ACK], m_abyPacket[NETLH_PACKHEAD + NETLH_DATA_ACK
				+ 1]);
		return CMD_RT_CONNECTIONED;

	}

	public int CmdDeviceReset(int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdDeviceReset(_ErrFlag);
		}

		boolean w_bRet;
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_DEVICE_RESET,
				PACK_CMD, 0, new byte[1], m_abyPacket);

		w_bRet = USB_post(CMD_DEVICE_RESET);

		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;

		w_bRet = USB_get(CMD_DEVICE_RESET);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;

		_ErrFlag[0] = GetRetCode();
		return CMD_RT_CONNECTIONED;

	}

	public int CmdMatchChar(int _RetScore[], int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdMatchChar(_RetScore, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[6];

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_MATCH_CHAR, PACK_CMD,
				0, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_MATCH_CHAR);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_MATCH_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		// 7. get the arg
		_RetScore[0] = (int) MAKEWORD(m_abyPacket[NETLH_PACKHEAD
				+ NETLH_DATA_ACK], m_abyPacket[NETLH_PACKHEAD + NETLH_DATA_ACK
				+ 1]);
		return CMD_RT_CONNECTIONED;

	}

	public int CmdGetChar_eAlg(int iBuffer, byte[] CharBuf, int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdGetChar_eAlg(iBuffer, CharBuf, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (iBuffer & 0xFF); // low
		wiBuffer[1] = (byte) ((iBuffer >> 8) & 0xFF);
		wiBuffer[2] = (byte) ((iBuffer >> 16) & 0xFF);
		wiBuffer[3] = (byte) ((iBuffer >> 24) & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_GET_CHAR, PACK_CMD, 4,
				wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_GET_CHAR);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_GET_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		// 7. get the arg
		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK, CharBuf,
				0, CHAR_EALG_SIZE);
		return CMD_RT_CONNECTIONED;

	}

	public int CmdGetChar_xAlg(int iBuffer, byte[] CharBuf, int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return CmdGetChar_xAlg(iBuffer, CharBuf, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (iBuffer & 0xFF); // low
		wiBuffer[1] = (byte) ((iBuffer >> 8) & 0xFF);
		wiBuffer[2] = (byte) ((iBuffer >> 16) & 0xFF);
		wiBuffer[3] = (byte) ((iBuffer >> 24) & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_GET_CHAR, PACK_CMD, 4,
				wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_GET_CHAR);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_GET_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		// 7. get the arg
		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK, CharBuf,
				0, CHAR_XALG_SIZE);
		return CMD_RT_CONNECTIONED;

	}

	public int CmdPutChar_eAlg(int iBuffer, byte[] CharBuf, int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdPutChar_eAlg(iBuffer, CharBuf, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[CHAR_EALG_SIZE + 4];
		wiBuffer[0] = (byte) (iBuffer & 0xFF); // low
		wiBuffer[1] = (byte) ((iBuffer >> 8) & 0xFF);
		wiBuffer[2] = (byte) ((iBuffer >> 16) & 0xFF);
		wiBuffer[3] = (byte) ((iBuffer >> 24) & 0xFF);

		System.arraycopy(CharBuf, 0, wiBuffer, 4, CHAR_EALG_SIZE);
		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_PUT_CHAR, PACK_CMD,
				CHAR_EALG_SIZE + 4, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_PUT_CHAR);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_PUT_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdPutChar_xAlg(int iBuffer, byte[] CharBuf, int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdPutChar_xAlg(iBuffer, CharBuf, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[CHAR_XALG_SIZE + 4];
		wiBuffer[0] = (byte) (iBuffer & 0xFF); // low
		wiBuffer[1] = (byte) ((iBuffer >> 8) & 0xFF);
		wiBuffer[2] = (byte) ((iBuffer >> 16) & 0xFF);
		wiBuffer[3] = (byte) ((iBuffer >> 24) & 0xFF);

		System.arraycopy(CharBuf, 0, wiBuffer, 4, CHAR_XALG_SIZE);
		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_PUT_CHAR, PACK_CMD,
				CHAR_XALG_SIZE + 4, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_PUT_CHAR);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_PUT_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdDelChar(int m_Addr, int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdDelChar(m_Addr, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (m_Addr & 0xFF); // low
		wiBuffer[1] = (byte) ((m_Addr >> 8) & 0xFF);// high

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_DELETE_CHAR, PACK_CMD,
				2, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_DELETE_CHAR);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_DELETE_CHAR);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdReadNoteBook(int _PageID, byte[] _NoteText, int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdReadNoteBook(_PageID, _NoteText, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (_PageID & 0xFF); // low

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_READ_NOTE_BOOK,
				PACK_CMD, 1, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_READ_NOTE_BOOK);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_READ_NOTE_BOOK);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();
		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK,
				_NoteText, 0, NOTE_BOOK_PAGE_SIZE);

		return CMD_RT_CONNECTIONED;

	}

	public int CmdWriteNoteBook(int _PageID, byte[] _NoteText, int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdWriteNoteBook(_PageID, _NoteText, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[NOTE_BOOK_PAGE_SIZE + 1];
		wiBuffer[0] = (byte) (_PageID & 0xFF); // low
		System.arraycopy(_NoteText, 0, wiBuffer, 1, NOTE_BOOK_PAGE_SIZE);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_WRITE_NOTE_BOOK,
				PACK_CMD, 1 + NOTE_BOOK_PAGE_SIZE, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_WRITE_NOTE_BOOK);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_WRITE_NOTE_BOOK);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdSetBaudRate(int _BaudRate, int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdSetBaudRate(_BaudRate, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (_BaudRate & 0xFF); // low
		wiBuffer[1] = (byte) ((_BaudRate >> 8) & 0xFF);
		wiBuffer[2] = (byte) ((_BaudRate >> 16) & 0xFF);
		wiBuffer[3] = (byte) ((_BaudRate >> 24) & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_SET_BAUD_RATE,
				PACK_CMD, 4, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_SET_BAUD_RATE);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_SET_BAUD_RATE);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdSetSecurLevel(int _SecurLevel, int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdSetSecurLevel(_SecurLevel, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (_SecurLevel & 0xFF); // low

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_SET_SECURLEVEL,
				PACK_CMD, 1, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_SET_SECURLEVEL);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_SET_SECURLEVEL);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdSetCmosPara(int _ExposeTimer, int DetectSensitive,
			int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdSetCmosPara(_ExposeTimer, DetectSensitive, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (_ExposeTimer & 0xFF); // low
		wiBuffer[1] = (byte) ((_ExposeTimer >> 8) & 0xFF);
		wiBuffer[2] = (byte) (DetectSensitive & 0xFF); // low

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_SET_CMOS_PARA,
				PACK_CMD, 3, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_SET_CMOS_PARA);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_SET_CMOS_PARA);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdEraseProgram(int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdEraseProgram(_ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_ERASE_PROGRAM,
				PACK_CMD, 0, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_ERASE_PROGRAM);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_ERASE_PROGRAM);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;
	}

	public int CmdResumeFactory(int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdResumeFactory(_ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_RESUME_FACTORY,
				PACK_CMD, 0, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_RESUME_FACTORY);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_RESUME_FACTORY);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;

		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;
	}

	public void SetTimeOutValue(int _TimeOutValue) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			NSetTimeOutValue(_TimeOutValue);
			return;
		}
		mTimeOutValue = _TimeOutValue;
	}

	public int GetTimeOutValue() {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NGetTimeOutValue();
		}
		return mTimeOutValue;
	}

	public int CmdStoreCharDirect_eAlg(int m_Addr, byte[] _FingerChar,
			int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdStoreCharDirect_eAlg(m_Addr, _FingerChar, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[CHAR_EALG_SIZE + 4];
		wiBuffer[0] = (byte) (m_Addr & 0xFF); // low
		wiBuffer[1] = (byte) ((m_Addr >> 8) & 0xFF); // low
		wiBuffer[2] = (byte) ((m_Addr >> 16) & 0xFF); // low
		wiBuffer[3] = (byte) ((m_Addr >> 24) & 0xFF); // low

		System.arraycopy(_FingerChar, 0, wiBuffer, 4, CHAR_EALG_SIZE);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_STORE_CHAR_DIRECT,
				PACK_CMD, 4 + CHAR_EALG_SIZE, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_STORE_CHAR_DIRECT);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_STORE_CHAR_DIRECT);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdStoreCharDirect_xAlg(int m_Addr, byte[] _FingerChar,
			int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdStoreCharDirect_xAlg(m_Addr, _FingerChar, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[CHAR_XALG_SIZE + 4];
		wiBuffer[0] = (byte) (m_Addr & 0xFF); // low
		wiBuffer[1] = (byte) ((m_Addr >> 8) & 0xFF); // low
		wiBuffer[2] = (byte) ((m_Addr >> 16) & 0xFF); // low
		wiBuffer[3] = (byte) ((m_Addr >> 24) & 0xFF); // low

		System.arraycopy(_FingerChar, 0, wiBuffer, 4, CHAR_XALG_SIZE);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_STORE_CHAR_DIRECT,
				PACK_CMD, 4 + CHAR_XALG_SIZE, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_STORE_CHAR_DIRECT);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_STORE_CHAR_DIRECT);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdReadCharDirect_eAlg(int m_Addr, byte[] _FingerChar,
			int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdReadCharDirect_eAlg(m_Addr, _FingerChar, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (m_Addr & 0xFF); // low
		wiBuffer[1] = (byte) ((m_Addr >> 8) & 0xFF); // low
		wiBuffer[2] = (byte) ((m_Addr >> 16) & 0xFF); // low
		wiBuffer[3] = (byte) ((m_Addr >> 24) & 0xFF); // low

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_READ_CHAR_DIRECT,
				PACK_CMD, 4, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_READ_CHAR_DIRECT);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_READ_CHAR_DIRECT);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK,
				_FingerChar, 0, CHAR_EALG_SIZE);

		return CMD_RT_CONNECTIONED;

	}

	public int CmdReadCharDirect_xAlg(int m_Addr, byte[] _FingerChar,
			int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdReadCharDirect_xAlg(m_Addr, _FingerChar, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (m_Addr & 0xFF); // low
		wiBuffer[1] = (byte) ((m_Addr >> 8) & 0xFF); // low
		wiBuffer[2] = (byte) ((m_Addr >> 16) & 0xFF); // low
		wiBuffer[3] = (byte) ((m_Addr >> 24) & 0xFF); // low

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_READ_CHAR_DIRECT,
				PACK_CMD, 4, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_READ_CHAR_DIRECT);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_READ_CHAR_DIRECT);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK,
				_FingerChar, 0, CHAR_XALG_SIZE);

		return CMD_RT_CONNECTIONED;

	}

	public int CmdSetPsw(int _NewPsw, int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdSetPsw(_NewPsw, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (_NewPsw & 0xFF);
		wiBuffer[1] = (byte) ((_NewPsw >> 8) & 0xFF);
		wiBuffer[2] = (byte) ((_NewPsw >> 16) & 0xFF);
		wiBuffer[3] = (byte) ((_NewPsw >> 24) & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_SET_PSW, PACK_CMD, 4,
				wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_SET_PSW);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_SET_PSW);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();
		if (CMD_RT_OK == _ErrFlag[0]) {
			NConfigCommParameter(0, _NewPsw);

		}
		return CMD_RT_CONNECTIONED;

	}

	public int CmdSetDeviceAddress(int _NewAddress, int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdSetDeviceAddress(_NewAddress, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (_NewAddress & 0xFF);
		wiBuffer[1] = (byte) ((_NewAddress >> 8) & 0xFF);
		wiBuffer[2] = (byte) ((_NewAddress >> 16) & 0xFF);
		wiBuffer[3] = (byte) ((_NewAddress >> 24) & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_SET_ADDRESS, PACK_CMD,
				4, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_SET_ADDRESS);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_SET_ADDRESS);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();
		if (CMD_RT_OK == _ErrFlag[0]) {
			NConfigCommParameter(_NewAddress, 0);
		}
		return CMD_RT_CONNECTIONED;

	}

	public int CmdGetRandom(int[] _Random, int[] _ErrFlag) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdGetRandom(_Random, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_GET_RANDOM, PACK_CMD,
				0, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_GET_RANDOM);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_GET_RANDOM);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK, _Random,
				0, 4);

		return CMD_RT_CONNECTIONED;

	}

	// IC CARD

	public int CmdICRequest(int _Mode, byte _CardType[],/* 2 bytes */
			int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICRequest(_Mode, _CardType, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (_Mode & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_REQUEST, PACK_CMD,
				1, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_REQUEST);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_REQUEST);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK,
				_CardType, 0, 2);

		return CMD_RT_CONNECTIONED;

	}

	public int CmdICAnticoll(int _Bcnt, byte _CardNum[],/* 4 bytes */
			int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICAnticoll(_Bcnt, _CardNum, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (_Bcnt & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_ANTICOLL, PACK_CMD,
				1, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_ANTICOLL);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_ANTICOLL);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK,
				_CardNum, 0, 4);

		return CMD_RT_CONNECTIONED;

	}

	public int CmdICSelect(byte _Size[],/* 1 bytes */int _CardNum,
			int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICSelect(_Size, _CardNum, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (_CardNum & 0xFF);
		wiBuffer[1] = (byte) ((_CardNum >> 8) & 0xFF);
		wiBuffer[2] = (byte) ((_CardNum >> 16) & 0xFF);
		wiBuffer[3] = (byte) ((_CardNum >> 24) & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_SELECT, PACK_CMD,
				4, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_SELECT);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_SELECT);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK, _Size,
				0, 1);

		return CMD_RT_CONNECTIONED;
	}

	public int CmdICHalt(int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICHalt(_ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_HALT, PACK_CMD, 0,
				wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_HALT);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_HALT);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdICLoadKey(byte _LoadKey[], /* 6 bytes */int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICLoadKey(_LoadKey, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_LOAD_KEY, PACK_CMD,
				LOADKEY_LENGTH, _LoadKey, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_LOAD_KEY);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_LOAD_KEY);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	public int CmdICAuthentication(int _Sector, int _AuthMode, int _CardNum,
			int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICAuthentication(_Sector, _AuthMode, _CardNum, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[6];
		wiBuffer[0] = (byte) (_Sector & 0xFF);
		wiBuffer[1] = (byte) (_AuthMode & 0xFF);
		wiBuffer[2] = (byte) ((_CardNum >> 0) & 0xFF);
		wiBuffer[3] = (byte) ((_CardNum >> 8) & 0xFF);
		wiBuffer[4] = (byte) ((_CardNum >> 16) & 0xFF);
		wiBuffer[5] = (byte) ((_CardNum >> 24) & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_CHECK_KEY,
				PACK_CMD, 6, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_CHECK_KEY);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_CHECK_KEY);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;
	}

	public int CmdICReadBlock(int _SectorIndex, int _BlockIndex,
			byte _BlockBuf[], int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICReadBlock(_SectorIndex, _BlockIndex, _BlockBuf,
					_ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];
		wiBuffer[0] = (byte) (_SectorIndex & 0xFF);
		wiBuffer[1] = (byte) (_BlockIndex & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_READ_BIOCK,
				PACK_CMD, 2, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_READ_BIOCK);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_READ_BIOCK);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK,
				_BlockBuf, 0, IC_BLOCK_SIZE);

		return CMD_RT_CONNECTIONED;

	}

	public int CmdICWriteBlock(int _SectorIndex, int _BlockIndex,
			byte _BlockBuf[], /* IC_BLOCK_SIZE 16 bytes */int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICWriteBlock(_SectorIndex, _BlockIndex, _BlockBuf,
					_ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[IC_BLOCK_SIZE + 4];
		wiBuffer[0] = (byte) (_SectorIndex & 0xFF);
		wiBuffer[1] = (byte) (_BlockIndex & 0xFF);
		System.arraycopy(_BlockBuf, 0, wiBuffer, 2, IC_BLOCK_SIZE);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_WRITE_BIOCK,
				PACK_CMD, IC_BLOCK_SIZE + 2, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_WRITE_BIOCK);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_WRITE_BIOCK);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;
	}

	public int CmdICInitMoney(int _SectorIndex, int _BlockIndex, int _Value,
			int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICInitMoney(_SectorIndex, _BlockIndex, _Value, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[6];
		wiBuffer[0] = (byte) (_SectorIndex & 0xFF);
		wiBuffer[1] = (byte) (_BlockIndex & 0xFF);
		wiBuffer[2] = (byte) ((_Value >> 0) & 0xFF);
		wiBuffer[3] = (byte) ((_Value >> 8) & 0xFF);
		wiBuffer[4] = (byte) ((_Value >> 16) & 0xFF);
		wiBuffer[5] = (byte) ((_Value >> 24) & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_INIT_MONEY,
				PACK_CMD, 6, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_INIT_MONEY);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_INIT_MONEY);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;
	}

	public int CmdICIncrementMoney(int _SectorIndex, int _BlockIndex,
			int _Value, int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICIncrementMoney(_SectorIndex, _BlockIndex, _Value,
					_ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[6];
		wiBuffer[0] = (byte) (_SectorIndex & 0xFF);
		wiBuffer[1] = (byte) (_BlockIndex & 0xFF);
		wiBuffer[2] = (byte) ((_Value >> 0) & 0xFF);
		wiBuffer[3] = (byte) ((_Value >> 8) & 0xFF);
		wiBuffer[4] = (byte) ((_Value >> 16) & 0xFF);
		wiBuffer[5] = (byte) ((_Value >> 24) & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_INCREMENT_MONEY,
				PACK_CMD, 6, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_INCREMENT_MONEY);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_INCREMENT_MONEY);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;
	}

	public int CmdICDecrementMoney(int _SectorIndex, int _BlockIndex,
			int _Value, int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICDecrementMoney(_SectorIndex, _BlockIndex, _Value,
					_ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[6];
		wiBuffer[0] = (byte) (_SectorIndex & 0xFF);
		wiBuffer[1] = (byte) (_BlockIndex & 0xFF);
		wiBuffer[2] = (byte) ((_Value >> 0) & 0xFF);
		wiBuffer[3] = (byte) ((_Value >> 8) & 0xFF);
		wiBuffer[4] = (byte) ((_Value >> 16) & 0xFF);
		wiBuffer[5] = (byte) ((_Value >> 24) & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_DECREMENT_MONEY,
				PACK_CMD, 6, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_DECREMENT_MONEY);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_DECREMENT_MONEY);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;
	}

	public int CmdICTransferMoney(int _SectorIndex, int _BlockIndex,
			int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdICTransferMoney(_SectorIndex, _BlockIndex, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[6];
		wiBuffer[0] = (byte) (_SectorIndex & 0xFF);
		wiBuffer[1] = (byte) (_BlockIndex & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_IC_TRANSFER_MONEY,
				PACK_CMD, 2, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_IC_TRANSFER_MONEY);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_IC_TRANSFER_MONEY);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;
	}

	public int CmdGetCardIdTypeB(byte _Card_ID[], /* 8 bytes */int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdGetCardIdTypeB(_Card_ID, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_GET_CARD_ID_TYPEB,
				PACK_CMD, 0, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_GET_CARD_ID_TYPEB);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_GET_CARD_ID_TYPEB);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK,
				_Card_ID, 0, 8);

		return CMD_RT_CONNECTIONED;
	}

	public int CmdGetCardIdTypeA(byte _Card_ID[], /* 4 bytes */int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdGetCardIdTypeA(_Card_ID, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[4];

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_GET_CARD_ID_TYPEA,
				PACK_CMD, 0, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_GET_CARD_ID_TYPEA);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_GET_CARD_ID_TYPEA);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		System.arraycopy(m_abyPacket, NETLH_PACKHEAD + NETLH_DATA_ACK,
				_Card_ID, 0, 4);

		return CMD_RT_CONNECTIONED;

	}

	public int CmdSetCardType(int _CardType, int _ErrFlag[]) {
		if (USB_NO_ROOT_TYPE != mConnType) {
			return NCmdSetCardType(_CardType, _ErrFlag);
		}
		boolean w_bRet;
		// 1. empty the buffer
		Arrays.fill(m_abyPacket, (byte) 0);

		// 2. set the packet
		byte[] wiBuffer = new byte[6];
		wiBuffer[0] = (byte) (_CardType & 0xFF);

		m_nPacketSize = CmdSetPack(DEFAULT_PACK_NUM, CMD_SET_CARD_TYPE,
				PACK_CMD, 1, wiBuffer, m_abyPacket);
		// 3. post the packet
		w_bRet = USB_post(CMD_SET_CARD_TYPE);
		// 4. empty the send buffer
		Arrays.fill(m_abyPacket, (byte) 0);
		m_nPacketSize = 0;
		// 5. get the response
		w_bRet = USB_get(CMD_SET_CARD_TYPE);

		if (!w_bRet)
			return CMD_RT_DISCONNECTION;
		// 6. get the result code
		_ErrFlag[0] = GetRetCode();

		return CMD_RT_CONNECTIONED;

	}

	private native int NCmdCreatFileOption(String pFilePath, byte[] _pImage,
			int _xLen, int _yLen);

	public native int CmdSetPack(int _pack_num, int _dictate, int _pack_id,
			int _data_len, byte[] _databuf, byte[] _NetLH_SendBuf);

	public native int CmdGetProtocolCheck(byte[] RxBuf);

	public native int CmdCheckInquiry(byte[] buff);

	public boolean CmdSCSIExecuteInquiry() {
		// SEND inquiry
		if (USB_NO_ROOT_TYPE != mConnType) {
			return true;
		}

		boolean w_bRet = false;
		byte[] btCDB = new byte[16];
		byte[] w_WaitPacket = new byte[600];
		int[] w_nLen = new int[1];
		memset(btCDB, (byte) 0, 8);
		Arrays.fill(w_WaitPacket, (byte) 0);

		btCDB[0] = (byte) 0x12;
		btCDB[1] = 0;
		btCDB[2] = 0;
		btCDB[3] = 0;
		btCDB[4] = (byte) 0x24;
		w_nLen[0] = 0x24;

		w_bRet = m_usbBase.UsbSCSIRead(btCDB, 6, w_WaitPacket, w_nLen,
				SCSI_TIMEOUT, false);
		if (w_bRet && 1 != CmdCheckInquiry(w_WaitPacket)) {
			return false;
		}
		return w_bRet;

	}

	private boolean memcmp(byte[] p1, byte[] p2, int nLen) {
		int i;

		for (i = 0; i < nLen; i++) {
			if (p1[i] != p2[i])
				return false;
		}

		return true;
	}

	private void memset(byte[] p1, byte nValue, int nLen) {
		Arrays.fill(p1, 0, nLen, nValue);
	}

	private void memcpy(byte[] p1, byte nValue, int nLen) {
		Arrays.fill(p1, 0, nLen, nValue);
	}

	private short MAKEWORD(byte low, byte high) {
		short s;
		s = (short) ((int) ((high << 8) & 0x0000FF00) | (int) (low & 0x000000FF));
		return s;
	}

	private byte LOBYTE(short s) {
		return (byte) (s & 0xFF);
	}

	private byte HIBYTE(short s) {
		return (byte) ((s >> 8) & 0xFF);
	}

	private static final int SCSI_TIMEOUT = 5000; // ms
	private static final int COMM_SLEEP_TIME = 40; // ms

	private static final int CMD_PACKET_LEN = 26;

	private static final int RCM_PACKET_LEN = 26;
	private static final int RCM_DATA_OFFSET = 10;
	// --------------- For Usb Communication ------------//
	public int m_nPacketSize;
	public byte m_bySrcDeviceID = 1, m_byDstDeviceID = 1;
	public byte[] m_abyPacket = new byte[NETLH_PACKLENGTH * 10];
	public byte[] m_abyPacket2 = new byte[NETLH_PACKLENGTH * 10];

	private byte[] mTxBuf = new byte[NETLH_PACKLENGTH * 10];
	private byte[] mRxBuf = new byte[NETLH_PACKLENGTH * 10];

	private boolean USB_post(short wCMD) {
		byte[] btCDB = new byte[8]; // it must by 16 .
		boolean w_bRet;

		Arrays.fill(btCDB, (byte) 0);

		btCDB[0] = (byte) 0x86;
		btCDB[1] = 0;
		btCDB[2] = 0;
		btCDB[3] = (byte) 0x3C;

		btCDB[4] = (byte) 0xFF;

		w_bRet = m_usbBase.UsbSCSIWrite(btCDB, 6, m_abyPacket, m_nPacketSize,
				SCSI_TIMEOUT);
		return w_bRet;
	}

	@SuppressWarnings("unused")
	private boolean USB_get(short wCMD) {
		byte[] btCDB = new byte[16];
		byte[] w_WaitPacket = new byte[2];
		int[] w_nLen = new int[1];
		int lastLen = 0, allLen = 0;
		memset(btCDB, (byte) 0, 8);

		Arrays.fill(w_WaitPacket, (byte) 0);
		Arrays.fill(m_abyPacket, (byte) 0);

		btCDB[0] = (byte) CMD_READ;
		btCDB[1] = 0;
		btCDB[2] = 0;
		btCDB[3] = 0;
		btCDB[4] = (byte) 0xFF;
		w_nLen[0] = NETLH_PACKLENGTH;

		if (!m_usbBase.UsbSCSIRead(btCDB, 6, m_abyPacket, w_nLen, SCSI_TIMEOUT,
				false)) {
			return false;
		}
		m_nPacketSize = w_nLen[0];

		if (w_nLen[0] > NETLH_PACKLENGTH) {
			return false;
		}

		if (!CheckReceive(m_abyPacket, m_nPacketSize, (short) PACK_HEAD, wCMD)) {
			return false;
		}

		return true;

	}

	@SuppressWarnings("unused")
	private boolean USB_get(short wCMD, byte[] rxbuff, int expetLen,
			int page_max) {
		boolean nRet = false;
		byte[] btCDB = new byte[16];
		int[] w_nLen = new int[1];
		int lastLen = 0, allLen = 0;
		int n = 0, r = 0, i = 0;
		boolean isBreak = false;
		n = expetLen / page_max;
		r = expetLen % page_max;

		memset(btCDB, (byte) 0, 8);

		Arrays.fill(m_abyPacket, (byte) 0);
		byte[] pBuffer = new byte[page_max];

		for (i = 0; i < n; i++) {
			w_nLen[0] = page_max;
			Arrays.fill(pBuffer, (byte) 0);
			nRet = USB_ReceiveRawData(pBuffer, w_nLen, false);
			if (nRet) {
				System.arraycopy(pBuffer, 0, rxbuff, allLen, w_nLen[0]);
				allLen += w_nLen[0];
			}

		}
		Log.d(TAG, " Get the less " + r);
		if (r > 0) {
			w_nLen[0] = r;
			Arrays.fill(pBuffer, (byte) 0);
			nRet = USB_ReceiveRawData(pBuffer, w_nLen, false);
			if (nRet) {
				System.arraycopy(pBuffer, 0, rxbuff, allLen, w_nLen[0]);
				allLen += w_nLen[0];
			}
		}
		Log.d(TAG, " Get all length is " + allLen + ",expetLen=" + expetLen);
		m_nPacketSize = allLen;
		return true;

	}

	// --------------------------- Send, Receive Communication Packet Functions
	// ---------------------//
	private boolean USB_SendPacket(short wCMD) {
		byte[] btCDB = new byte[8]; // it must by 16 .
		boolean w_bRet;

		Arrays.fill(btCDB, (byte) 0);

		btCDB[0] = (byte) CMD_WRITE;
		btCDB[1] = 0;
		btCDB[2] = 0;
		btCDB[3] = (byte) 0x3C;
		btCDB[4] = (byte) 0xFF;

		w_bRet = m_usbBase.UsbSCSIWrite(btCDB, 6, m_abyPacket, m_nPacketSize,
				SCSI_TIMEOUT);
		// Log.d(TAG, "USB_SendPacket UsbSCSIWrite w_bRet = " + w_bRet);

		if (!w_bRet) {
			return false;
		}

		return USB_ReceiveAck(wCMD);
	}

	boolean USB_SendDataPacket(short wCMD) {
		byte[] btCDB = new byte[16];

		memset(btCDB, (byte) 0, 8);

		btCDB[0] = (byte) CMD_WRITE;
		btCDB[1] = 0;
		btCDB[2] = 0;
		btCDB[3] = (byte) 0x3C;
		btCDB[4] = (byte) 0xFF;

		if (!m_usbBase.UsbSCSIWrite(btCDB, 6, m_abyPacket, m_nPacketSize,
				SCSI_TIMEOUT))
			return false;

		return USB_ReceiveDataAck(wCMD);
	}

	boolean USB_ReceiveRawData(byte[] pBuffer, int[] nDataLen, boolean must) {
		byte[] btCDB = new byte[16];

		memset(btCDB, (byte) 0, 8);

		btCDB[0] = (byte) CMD_READ;
		btCDB[1] = 0;
		btCDB[2] = 0;
		btCDB[3] = 0;
		btCDB[4] = (byte) 0xFF;

		if (!m_usbBase.UsbSCSIRead(btCDB, 6, pBuffer, nDataLen, SCSI_TIMEOUT,
				must))
			return false;

		return true;
	}

	boolean USB_ReceiveRawData(byte[] pBuffer, int nDataLen) {
		byte[] btCDB = new byte[16];

		memset(btCDB, (byte) 0, 8);

		btCDB[0] = (byte) CMD_READ;
		btCDB[1] = 0;
		btCDB[2] = 0;
		btCDB[3] = 0;
		btCDB[4] = (byte) 0xFF;

		if (!m_usbBase.UsbSCSIRead(btCDB, 6, pBuffer, nDataLen, SCSI_TIMEOUT))
			return false;

		return true;
	}

	@SuppressWarnings("unused")
	private boolean USB_ReceiveAck(short wCMD) {
		int c, w_nLen, w_nReadCount = 0;
		byte[] btCDB = new byte[8];
		byte[] w_abyWaitPacket = new byte[CMD_PACKET_LEN];

		Arrays.fill(btCDB, (byte) 0);

		c = 0;
		Arrays.fill(w_abyWaitPacket, (byte) 0xAF);

		do {
			Arrays.fill(m_abyPacket, (byte) 0);

			btCDB[0] = (byte) CMD_READ;
			btCDB[1] = (byte) 0x12;

			w_nLen = RCM_PACKET_LEN;

			if (!m_usbBase.UsbSCSIRead(btCDB, 8, m_abyPacket, w_nLen,
					SCSI_TIMEOUT)) {
				return false;
			}

			SystemClock.sleep(COMM_SLEEP_TIME);

			c++;
		} while (memcmp(m_abyPacket, w_abyWaitPacket, CMD_PACKET_LEN) == true);

		m_nPacketSize = w_nLen;

		if (!CheckReceive(m_abyPacket, m_nPacketSize, (short) PACK_HEAD, wCMD))
			return false;

		return true;
	}

	@SuppressWarnings("unused")
	boolean USB_ReceiveDataAck(short wCMD) {

		byte[] btCDB = new byte[16];
		byte[] w_WaitPacket = new byte[2];
		int w_nLen;
		int lastLen = 0, allLen = 0;
		memset(btCDB, (byte) 0, 8);

		Arrays.fill(w_WaitPacket, (byte) 0);
		w_WaitPacket[0] = (byte) (PACK_HEAD & 0xFF);
		w_WaitPacket[1] = (byte) ((PACK_HEAD >> 8) & 0xFF);

		do {
			btCDB[0] = (byte) CMD_READ;
			btCDB[1] = 0;
			btCDB[2] = 0;
			btCDB[3] = 0;
			btCDB[4] = (byte) 0xFF;
			w_nLen = NETLH_PACKHEAD;

			if (!m_usbBase.UsbSCSIRead(btCDB, 6, m_abyPacket, w_nLen,
					SCSI_TIMEOUT)) {
				return false;
			}

			SystemClock.sleep(COMM_SLEEP_TIME);

		} while (memcmp(m_abyPacket, w_WaitPacket, 2) == true);

		w_nLen = (short) ((int) ((m_abyPacket[NETLH_PACKLEN_POS_LH + 1] << 8) & 0x0000FF00) | (int) (m_abyPacket[NETLH_PACKLEN_POS_LH] & 0x000000FF))
				+ NETLH_CRC_CHECK;

		allLen = w_nLen + NETLH_PACKHEAD + NETLH_CRC_CHECK;

		if (allLen > NETLH_PACKLENGTH) {
			return false;
		}

		Arrays.fill(m_abyPacket2, (byte) 0);
		if (USB_ReceiveRawData(m_abyPacket2, w_nLen) == false) {
			return false;
		}

		System.arraycopy(m_abyPacket2, 0, m_abyPacket, NETLH_PACKHEAD, w_nLen);

		m_nPacketSize = NETLH_PACKHEAD + w_nLen;

		if (!CheckReceive(m_abyPacket, m_nPacketSize, (short) PACK_HEAD, wCMD)) {
			return false;
		}

		return true;
	}

	private short GetRetCode() {
		return (short) ((int) ((m_abyPacket[NETLH_PACKHEAD + 1] << 8) & 0x0000FF00) | (int) (m_abyPacket[NETLH_PACKHEAD] & 0x000000FF));
	}

	/***************************************************************************
	 * Check Packet
	 ***************************************************************************/
	@SuppressWarnings("unused")
	boolean CheckReceive(byte[] pbyPacket, int nPacketLen, short wPrefix,
			short wCMDCode) {
		short w_wCalcCheckSum, w_wCheckSum, w_wTmp;
		int ret = 0;
		// . Check prefix code
		w_wTmp = (short) ((int) ((pbyPacket[1] << 8) & 0x0000FF00) | (int) (pbyPacket[0] & 0x000000FF));

		if (wPrefix != w_wTmp) {
			Log.d(TAG, String.format(
					"CheckReceive error1, wPrefix=%d, w_wTmp=%d", wPrefix,
					w_wTmp));
			return false;
		}

		// . Check checksum

		ret = CmdGetProtocolCheck(pbyPacket);

		if (1 != ret) {
			Log.d(TAG, String
					.format("CheckReceiveCheck CmdGetProtocolCheck error2 "));
			return false;
		}

		// . Check Command Code
		w_wTmp = (short) ((int) ((pbyPacket[12 + 1] << 8) & 0x0000FF00) | (int) (pbyPacket[12] & 0x000000FF));
		if (wCMDCode != w_wTmp) {
			Log.d(TAG,
					String.format(
							"CheckReceive Check Command Code error3, wCMDCode=%d, w_wTmp=%d",
							wCMDCode, w_wTmp));
			return false;
		}

		return true;
	}

	@SuppressLint("DefaultLocale")
	public String byteArrToHexString(byte[] b, String bk) {
		String log = "";
		for (int i = 0; i < b.length; i++) {
			if (i > 40) {
				break;
			}
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			log += hex.toUpperCase();
			log += bk;
		}
		Log.d(TAG, " " + log);
		return log;
	}

	static {
		System.loadLibrary("NETLH_E");
	}

}
