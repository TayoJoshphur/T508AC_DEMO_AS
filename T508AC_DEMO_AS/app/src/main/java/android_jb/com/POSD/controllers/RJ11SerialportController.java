package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import android.util.Log;

import android_jb.com.POSD.interfaces.RS232ReadCallback;
import android_serialport_api.SerialPort;

public class RJ11SerialportController {
	
	private static RJ11SerialportController rs232Controller = null;
	private static final String TAG = "RS232Controller";
	private String mFile;
	private int mBaud;
	private int mBits;
	private char mEvent;
	private int mStopBits;
	private SerialPort mSerialPort = null;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private RS232ReadCallback rs232ReadCallback;
	private ReadThread mReadThread = null;
	private boolean begin;

	public static RJ11SerialportController getInstance() {
		Log.i(TAG, "getInstance");
		if (null == rs232Controller) {
			rs232Controller = new RJ11SerialportController();
		}
		return rs232Controller;
	}

	/**
	 * 功能：打开设备
	 * 
	 * @return 0:成功，-1:失败
	 */
	public int Rs232_Open(String file, int baud, int bits, char event,
			int stopBits, RS232ReadCallback l) {
		Log.i(TAG, "Rs232_Open");
		this.mFile = file;
		this.mBaud = baud;
		this.mBits = bits;
		this.mEvent = event;
		this.mStopBits = stopBits;
		this.rs232ReadCallback = l;
		try {
			//Ioctl.convertRJ11();
			mSerialPort = new SerialPort(new File(mFile), mBaud, mBits, mEvent,
					mStopBits, 0, 1);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			begin = true;
			this.mReadThread = new ReadThread();
			this.mReadThread.start();
			return 0;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private class ReadThread extends Thread {
		public void run() {
			while (begin) {
				try {
					Thread.sleep(100);
					int size = 0;
					int coutq = 0;
					int couth = 0;
					if (null != mInputStream) {
						coutq = mInputStream.available();
					} else {
						break;
					}
					while (coutq != couth) {
						Thread.sleep(10);
						couth = mInputStream.available();
						Thread.sleep(10);
						coutq = mInputStream.available();
					}
					byte[] buffer1 = new byte[couth];
					size = mInputStream.read(buffer1);
					if (size > 0) {
						if (null != rs232ReadCallback) {
							rs232ReadCallback.RS232_Read(buffer1);
							buffer1 = null;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void doRead() {
		int size = 0;
		try {
			if (mSerialPort != null && mInputStream != null) {
				int cout = mInputStream.available();
				byte[] buffer = new byte[cout];
				size = mInputStream.read(buffer);
				Thread.sleep(450);
				if (size > 0) {
					if (null != rs232ReadCallback) {
						rs232ReadCallback.RS232_Read(buffer);
						buffer = null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 功能：写数据
	 * 
	 * @param command
	 *            数据
	 * @return void
	 */
	public void Rs232_Write(final byte[] command) {
		if (null != mOutputStream && mSerialPort != null) {
			new Thread() {
				public void run() {
					try {
						if (null == command) {
							mOutputStream.write("".getBytes());
							mOutputStream.flush();
						} else {
							mOutputStream.write(command);
							mOutputStream.flush();
						}
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

	/**
	 * 功能：关闭
	 * 
	 * @return void
	 */
	public void Rs232_Close() {
		begin = false;
		if (mOutputStream != null || mInputStream != null) {
			try {
				mOutputStream.close();
				mInputStream.close();
				mOutputStream = null;
				mInputStream = null;
				if (null != mSerialPort) {
					mSerialPort.close();
					mSerialPort = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
