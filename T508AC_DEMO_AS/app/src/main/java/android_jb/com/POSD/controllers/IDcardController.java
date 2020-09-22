package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android.content.Context;
import android.util.Log;

import android_jb.com.POSD.util.MachineVersion;
import android_jb.com.POSD.util.ReaderSerialPort;
import android_serialport_api.SerialPort;

import com.synjones.idcard.IDCard;
import com.synjones.idcard.IDcardReader;
import com.synjones.multireaderlib.MultiReader;


public class IDcardController {

	private static IDcardController idcardController = null;
	private static String TAG = "IDcardController";
	private String PORT = "/dev/ttyS1";
	private SerialPort mSerialPort = null;
	protected OutputStream mOutputStream;
	protected InputStream mInputStream;
	private IDCard idcard = null;
	
	private ReaderSerialPort rsp;
	private MultiReader reader;
	private IDcardReader idreader;
	private static Context context;
	private String version = MachineVersion.getMachineVersion();
	private String IR_PWR_EN = "/proc/jbcommon/gpio_control/RFID_CTL";// 1开，0关
	private String IO_OE = "/proc/jbcommon/gpio_control/UART1_EN"; // 默认值：1，其他值无效
	private String IO_CS0 = "/proc/jbcommon/gpio_control/UART1_SEL0";// 默认值：1，其他值无效
	private String IO_CS1 = "/proc/jbcommon/gpio_control/UART1_SEL1";// 默认值：1，其他值无效

	private SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			int baudrate = 115200;
			/* Check parameters */
			if ( (PORT.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}

			/* Open the serial port */
			mSerialPort = new SerialPort(new File(PORT), baudrate, 0);
		}
		return mSerialPort;
	}

	private void closeSerialPort() {
		if (mOutputStream != null) {
			try {
				mOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mOutputStream = null;
		}
		if (mInputStream != null) {
			try {
				mInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mInputStream = null;
		}
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
	
	public static IDcardController getInstance() {
		Log.i(TAG, "getInstance");
		if (null == idcardController) {
			idcardController = new IDcardController();
		}
		return idcardController;
	}

	/**
	 * 功能：打开设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int IDcardController_Open(Context contexts , int port) {
		try {
			Log.v("IDcardController","IDcardController_Open port: "+port+" version: "+version);
			PORT = "/dev/ttyS1";
			context = contexts;
			power_up();
			Thread.sleep(200);
			mSerialPort = getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			
			rsp=new ReaderSerialPort(context,mOutputStream,mInputStream);
			reader = MultiReader.getReader();
			reader.setDataTransInterface(rsp);
			idreader=new IDcardReader(reader);
			idreader.open(context);

			return 0;


		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	private void power_up() {
		// TODO Auto-generated method stub
		writeFile(new File(IR_PWR_EN),"1");
		writeFile(new File(IO_OE),"0");
		writeFile(new File(IO_CS0),"0");
		writeFile(new File(IO_CS1),"0");
	}

	private void power_down() {
		// TODO Auto-generated method stub
		writeFile(new File(IR_PWR_EN),"0");
	}
	/**
	 * 功能：读操作
	 * 
	 * @return UserIDCardInfo 不为空成功，否则失败。
	 */
	public IDCard IDcardController_Read() {
		if (null != idreader) {
			idcard = idreader.getIDcardBlocking();
			return idcard;
		} else {
			return null;
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
	 * 功能：关闭设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int IDcardController_Close() {
		if(null != idreader){
			idreader.close();
			idreader = null;
		}
		if(null != reader){
			reader = null;
		}
		if(rsp!=null)
    	{
    		rsp.closeSerialPort();		    		
    		rsp=null;		    		
    	}
		//Ioctl.activate(17,0);
		power_down();
		closeSerialPort();
		return 0;
	}
}
