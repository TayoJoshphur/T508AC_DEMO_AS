package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;





import android.R.integer;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android_serialport_api.SerialPort;

@SuppressLint("HandlerLeak") public class PP300PinPadController {
	private static final String TAG = "PP300PinPadController";
	private static PP300PinPadController pinPadController = null;
	private SerialPort mSerialPort = null;
	private OutputStream mOutputStream = null;
	private InputStream mInputStream = null;
	private boolean mRead = false;
	private boolean wRead = false;
	private boolean pRead = false;
	private boolean begin =false;
	ReadThread readThread ;
	MkThread mkReadThread;
	PinkThread pinkReadThread;
	private PassCallBack mCallBack;
	private String IO_CS0 = "/proc/jbcommon/gpio_control/UART2_SEL0";// A默认值：1，其他值无效
	private String IO_CS1 = "/proc/jbcommon/gpio_control/UART2_SEL1";// B默认值：1，其他值无效
	private String power = "/proc/jbcommon/gpio_control/RJ11_CTL";// 默认值：1，其他值无效

	public static PP300PinPadController getInstance() {
		Log.e(TAG, "getInstance");
		if (null == pinPadController) {
			pinPadController = new PP300PinPadController();
		}
		return pinPadController;
	}
	
	public int PINPad_Open(PassCallBack passCallBack) {
		try {
			//Ioctl.convertRJ11();
			writeFile(new File(power),"1");
			writeFile(new File(IO_CS0),"1");
			writeFile(new File(IO_CS1),"1");
			mSerialPort = new SerialPort(new File("/dev/ttyS2"), 38400, 8, '0',1, 0, 0);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			mCallBack = passCallBack;
			begin = true;
			if (mkReadThread == null) {
				mkReadThread = new MkThread();
				mkReadThread.start();
			}
			if (pinkReadThread == null) {
				pinkReadThread = new PinkThread();
				pinkReadThread.start();
			}
			if(readThread ==null){
			 readThread =new ReadThread();
			 readThread.start();
		    }
			if (mSerialPort != null) {
				Log.e(TAG, "PINPad_Open success!");
				return 0;
			}
			else{
				return -1;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
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
	
	public int PINPad_Close() {
		try {
			begin = false;
			mRead = false;
			wRead = false;
			pRead = false;
			
			mkReadThread = null;
			pinkReadThread = null;
			readThread =null;
			writeFile(new File(power),"0");
			writeFile(new File(IO_CS0),"0");
			writeFile(new File(IO_CS1),"0");
			Log.e(TAG, "PINPad_Close");
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void PINPad_updateMKey(byte[] mkBuff,int iLenght) {
		
		mRead=false;
		if (iLenght != 8 && iLenght != 16 && iLenght != 24) {
			Message msg =  Message.obtain();
			msg.obj = "master key length error!";
			msg.what = 0;
			mHandler.sendMessage(msg);
			return ;
		}
		Log.e(TAG, "PINPad_updateMKey");
		int nSendLenght = 0;
		byte[] sendData = new byte[mkBuff.length+6];
		byte[] tmp = new byte[]{0x00,0x22};
		System.arraycopy(tmp, 0, sendData, nSendLenght, 2);
		nSendLenght += 2;
		sendData[nSendLenght] = 0x04;
		
		if (iLenght == 8) {
			sendData[nSendLenght] |= 0x08;
		}
		else if (iLenght == 16) {
			sendData[nSendLenght] |= 0x10;
		}else {
			sendData[nSendLenght] |= 0x18;
		}
		nSendLenght += 1;
		
		sendData[nSendLenght] = 0x01;
		nSendLenght += 1;
		
		sendData[nSendLenght] = (byte) 0xFF;    
		nSendLenght += 1;
		
		sendData[nSendLenght] = 0x00;      
		nSendLenght += 1;
		
		if (iLenght == 8) {
			System.arraycopy(mkBuff, 0, sendData, nSendLenght, 8);
			nSendLenght += 8;
		}
		else if (iLenght == 16) {
			System.arraycopy(mkBuff, 0, sendData, nSendLenght, 16);
			nSendLenght += 16;
		}else {
			System.arraycopy(mkBuff, 0, sendData, nSendLenght, 24);
			nSendLenght += 24;
		}
		
		sendSerialData(1,sendData);
		Message msg =  Message.obtain();
		msg.obj = "master key send success!";
		msg.what = 0;
		mHandler.sendMessage(msg);
		return ;
	}
	
	public void PINPad_updatepinkey(byte[] pinkBuff,int iLenght) {
		Log.e(TAG, "PINPad_updatepinkey");
		byte bMode = 0;
		Message msg =  Message.obtain();
		wRead = false;
		if (iLenght%8 == 4) {
			bMode = (byte) (0x01|bMode);
		}
		
		int nSendLenght = 0;
		byte[] sendData = new byte[pinkBuff.length+6];
		byte[] tmp = new byte[]{0x00,0x22};
		System.arraycopy(tmp, 0, sendData, nSendLenght, 2);
		nSendLenght += 2;
		sendData[nSendLenght] = bMode;
		
		if (iLenght/8 == 0) {
			sendData[nSendLenght] |= 0x08;
		}
		else if (iLenght/8 == 2) {
			sendData[nSendLenght] |= 0x10;
		}else {
			sendData[nSendLenght] |= 0x18;
		}
		nSendLenght += 1;
		
		sendData[nSendLenght] = 0x26;
		nSendLenght += 1;
		
		sendData[nSendLenght] = (byte) 0x01;    
		nSendLenght += 1;
		
		sendData[nSendLenght] = 0x00;      
		nSendLenght += 1;
		
		System.arraycopy(pinkBuff, 0, sendData, nSendLenght, iLenght);
		nSendLenght += iLenght;
		
		sendSerialData(2,sendData);
		msg.obj = "work key send success!";
		msg.what = 0;
		mHandler.sendMessage(msg);
		return ;
	}
	
	public int PINPad_updatemackey(byte[] mackBuff,int iLenght) {
		byte bMode = 0;
		
		if (iLenght%8 == 4) {
			bMode = (byte) (0x41|bMode);
		}
		
		int nSendLenght = 0;
		byte[] sendData = new byte[mackBuff.length+6];
		byte[] tmp = new byte[]{0x00,0x22};
		System.arraycopy(tmp, 0, sendData, nSendLenght, 2);
		nSendLenght += 2;
		sendData[nSendLenght] = bMode;
		
		if (iLenght/8 == 0) {
			sendData[nSendLenght] |= 0x08;
		}
		else if (iLenght/8 == 2) {
			sendData[nSendLenght] |= 0x10;
		}else {
			sendData[nSendLenght] |= 0x18;
		}
		nSendLenght += 1;
		
		sendData[nSendLenght] = 0x27;
		nSendLenght += 1;
		
		sendData[nSendLenght] = (byte) 0x01;    
		nSendLenght += 1;
		
		sendData[nSendLenght] = 0x00;      
		nSendLenght += 1;
		
		System.arraycopy(mackBuff, 0, sendData, nSendLenght, iLenght);
		nSendLenght += iLenght;
		
		sendSerialData(4,sendData);
		return 0;
	}
	
	public void PINPad_Get_PinX98(byte[] panData,byte[]amountBcd,byte[]promptMessage){
		Log.e(TAG, "PINPad_Get_PinX98");
		int nPromptMsgLen = 0;
//		Message msg =  Message.obtain();
		Message msg = new Message();
		pRead = false;
		if (panData.length != 12) {
			msg.obj = "panData length error!";
			msg.what = 0;
			mHandler.sendMessage(msg);
			return ;
		}
		if (amountBcd.length != 6) {
			msg.obj = "amount length error!";
			msg.what = 0;
			mHandler.sendMessage(msg);
			return ;
		}
		
		if (promptMessage.length > 15) {
			nPromptMsgLen = 15;
		}
		else {
			nPromptMsgLen = promptMessage.length;
		}
		
		int nSendLenght = 0;
		byte[] sendData = new byte[panData.length+amountBcd.length+nPromptMsgLen+5];
		byte[] tmp = new byte[]{0x00,0x12};
		System.arraycopy(tmp, 0, sendData, nSendLenght, 2);
		nSendLenght += 2;
		sendData[nSendLenght] = 60;
		nSendLenght += 1;
		sendData[nSendLenght] = 0x61;
		nSendLenght += 1;
		
		sendData[nSendLenght] = 0x26;
		nSendLenght += 1;
		
		System.arraycopy(panData,0, sendData, nSendLenght,12);
		nSendLenght += 12;
		
		System.arraycopy(amountBcd,0, sendData, nSendLenght,6);
		nSendLenght += 6;
		
		System.arraycopy(promptMessage,0, sendData, nSendLenght,nPromptMsgLen);
		nSendLenght += nPromptMsgLen;
		
		sendSerialData(3,sendData);
//		msg.obj = "work key send success!";
//		msg.what = 0;
//		mHandler.sendMessage(msg);
		return ;
	}
	
	public static byte[] unsignedShortToByte2(int s) {  
        byte[] targets = new byte[2];  
        targets[0] = (byte) (s >> 8 & 0xFF);  
        targets[1] = (byte) (s & 0xFF);  
        return targets;  
    } 
	
	private void sendSerialData(int type,byte[] datas){
		Log.e(TAG, "sendSerialData");
		byte lrc = 0;
		byte[] lenBuff = new byte[2];
		lenBuff = unsignedShortToByte2(datas.length);
		try {
			byte[] buff = new byte[datas.length+4];
			buff[0] = 0x01;
			
			buff[1] = lenBuff[0];
			buff[2] = lenBuff[1];
			
			System.arraycopy(datas,0,buff, 3, datas.length);
			
			byte[] buffLrc = new byte[datas.length+2];
			System.arraycopy(buff, 1, buffLrc, 0, datas.length+2);
			lrc = getLRC(buffLrc,buffLrc.length);
			buff[buff.length-1] = lrc;
			
			mOutputStream.write(buff);
			mOutputStream.flush();
			Thread.sleep(200);
			if (type == 1) {
				mRead = true;
			}
			else if(type == 2) {
				wRead = true;
			}
			else if(type == 3){
				pRead = true;
			}
			
			
//			int cout = mInputStream.available();
//			byte[] buffer = new byte[cout];
//			int cum = mInputStream.read(buffer);
//			dumpHex(null,buffer);
//			if (buffer.length > 0) {
//				if ((buffer[0] & 0xff) == 0x01) {
//					return 0;
//				} else {
//					return -1;
//				}
//			} else {
//				return -1;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class MkThread extends Thread{
		public void run(){
			int count = 0;
			while(begin){
				if(mRead){
					if(mInputStream!=null){
						try {
							count = mInputStream.available();
							if(count>6){
								byte[] buffer = new byte[count];
								mRead = false;
								dumpHex("MkThread count:",IntToBcd(count,1));
								
								mInputStream.read(buffer);
								dumpHex("MkThread read:",buffer);
								String str = bcdToAsc(buffer, count);
								if (str.contains("0022") == true) {
//									Message msg =  Message.obtain();
									Message msg = new Message();
									msg.obj = buffer;
									msg.what = 1;
									msg.arg1 = 1;
									mHandler.sendMessage(msg);
								}

							}
							else {
//								dumpHex("no data MkThread count:",IntToBcd(count,1));
							}
							
						} catch (Exception e) {
							// TODO: handle exception
							dumpHex("Exception MkThread count:",IntToBcd(count,1));
						}
					}
				}
			}
		}
	}
	
	private class PinkThread extends Thread{
		public void run(){
			while(begin){
				if(wRead){
					if(mInputStream!=null){
						try {
							int count;
							count = mInputStream.available();
							if(count>6){
								
								byte[] buffer = new byte[count];
								mInputStream.read(buffer);
								dumpHex("PinkThread read:",buffer);
								String str = bcdToAsc(buffer, count);
								if (str.contains("0022") == true) {
									Message msg =  Message.obtain();
									msg.obj = buffer;
									msg.what = 1;
									msg.arg1 = 2;
									mHandler.sendMessage(msg);
									wRead = false;
								}
								
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
		}
	}
	
	private class ReadThread extends Thread {
		public void run(){
			super.run();
			int count;
			while(begin){
				if(pRead){
				if(mInputStream!=null){
					try {
						count = mInputStream.available();
						if(count > 14){
							
							byte[] buffer = new byte[count];
							mInputStream.read(buffer);
							dumpHex("ReadThread read:",buffer);
							bcdToAsc(buffer, count);
							
							d("ReadThread", bcdToAsc(buffer, count));
							String str = bcdToAsc(buffer, count);
							if (str.contains("0012") == true) {
								pRead = false;
//								Message msg = Message.obtain();
								Message msg = new Message();
								msg.obj = buffer;
								msg.what = 1;
								msg.arg1 = 3;
								mHandler.sendMessage(msg);
								
							}
							
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			}
		}
	}
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage (Message msg){
			switch(msg.what){
			case 0:
				mCallBack.ReturnMsg(msg.obj.toString());
				break;
			case 1:
				mCallBack.displayReturnMsg(msg.arg1, (byte[]) msg.obj);
				break;
			}
		}
	};
	
	public String intToString(int value){
		Integer integer = new Integer(value);
		return integer.toString();
		
	}
	
	public final  int getInt(byte[] buf, boolean asc) {  
        if (buf == null) {  
          throw new IllegalArgumentException("byte array is null!");  
        }  
        if (buf.length > 4) {  
          throw new IllegalArgumentException("byte array size > 4 !");  
        }  
        int r = 0;  
        if (asc)  
          for (int i = buf.length - 1; i >= 0; i--) {  
            r <<= 8;  
            r |= (buf[i] & 0x000000ff);  
          }  
        else  
          for (int i = 0; i < buf.length; i++) {  
            r <<= 8;  
            r |= (buf[i] & 0x000000ff);  
          }  
        return r;  
    }
	
	public byte getLRC(byte array[],int iLength){
		byte LRC = 0;
		LRC = array[0];
		for(int i = 1;i < iLength;i ++){
			LRC = (byte) (LRC^array[i]);
		}
		return LRC;
	}
	
	public static void dumpHex(String msg, byte[] bytes) {
        int length = bytes.length;
        msg = (msg == null) ? "" : msg;
        System.out.printf("-------------------------- " + msg + "(len:%d) --------------------------\n", length);
        for (int i = 0; i < bytes.length; i++) {
            if (i % 16 == 0) {
                if (i != 0) 
                	System.out.println();
                System.out.printf("0x%08X    ", i);
            }
            System.out.printf("%02X ", bytes[i]);
        }
        System.out.println("");
    }
	
	public static void d(String tag, String msg) {
		tag = (tag == null) ? "" : tag;
        System.out.printf("-------------------------- " + tag + "--------------------------\n");
		System.out.printf(msg);
	}
	
	public static byte[] IntToBcd(int iSrcValue, int iBcdLen)
	{
		String strAsc = String.format("%0" + iBcdLen*2 + "d",iSrcValue);
		return AscToBcd(strAsc.getBytes(),strAsc.length());
	}
	
	public static byte[] AscToBcd(String strAsc) {
		int j = 0;
		int nStrLen = strAsc.length();
		byte[] inArray = strAsc.getBytes();
		int nLen = (nStrLen +1)/ 2;
		byte[] outArray = new byte[nLen];
		for (int i = 0; i < nLen; i++) {
			outArray[i] = (byte)(AsciiToHex(inArray[j++]) << 4);
			outArray[i] |= (j >= nStrLen) ? 0x00 : AsciiToHex(inArray[j++]);
		}
		return outArray;
	}
	
	public static byte[] AscToBcd(byte[] inArray, int iInArrayLength) {
		int j = 0;
		int nLen = (iInArrayLength +1)/ 2;
		byte[] outArray = new byte[nLen];
		for (int i = 0; i < nLen; i++) {
			outArray[i] = (byte)(AsciiToHex(inArray[j++]) << 4);
			outArray[i] |= (j >= iInArrayLength) ? 0x00 : AsciiToHex(inArray[j++]);
		}
		return outArray;
	}
	
	public static  String  bcdToAsc(byte[] inArray, int iInArrayLength) {
    	byte[] outArray=new byte[iInArrayLength*2];
    	int j = 0;
		 for(int i = 0; i< iInArrayLength; i++){			
			 outArray[j] = hexToAscii((inArray[i] & 0xf0) >>4);
			 j++;
			 outArray[j] = hexToAscii(inArray[i] & 0x0f);
			 j++;
			
		 }
		return  new String(outArray) ;//outArray.toString();
	 }
	
	public static   byte hexToAscii(int iNum){
		if(iNum>=0 && iNum<=9){
			return  (byte)(iNum+0x30);
		} else  {
			return ((byte)(0x40+(byte)(iNum-9)));			
		}
		 
	}	
	private static byte AsciiToHex(int iNum) {

	     if((iNum >= '0') && (iNum <= '9'))
		{
			return (byte) (iNum - '0');
		} 
		else if((iNum >= 'A') && (iNum <= 'F'))
		{
			return (byte) (iNum - 'A' + 10);
		}
		else if((iNum >= 'a') && (iNum <= 'f'))
		{
			return (byte) (iNum - 'a' + 10);
		}
		else if((iNum >= 0x39) && (iNum <= 0x3f))
		{
			return (byte) (iNum - '0');
		}
		else
			return 0x0f;
	}
	
	public interface PassCallBack {
		public void displayReturnMsg(int type, byte[] retBuff);
		public void ReturnMsg(String strMsg);
	}

}
