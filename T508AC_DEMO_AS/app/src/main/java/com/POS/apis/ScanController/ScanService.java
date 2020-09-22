package com.POS.apis.ScanController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.jb.barcode.BarcodeUntil;
import com.POS.apis.ScanController.Tools;
import com.POS.apis.ScanController.WakeLockUtil;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.POS.apis.ScanController.ScanListener;

import android_jb.com.POSD.Preference;
import android_jb.com.POSD.controllers.ScanController;
import jepower.com.t508ac_demo.R;

public class ScanService extends Service {
	private int scan_time_limit = 200; // 扫描间隔控制
	private static final String TAG = "ScanService";
	private boolean isActivityUp = false;
	private ActivityManager am;
	private ComponentName cn;
	private ScanListener mScanListener;
	private KeyguardManager km;
	private ScanController scanManager = null;
	public static boolean keyF2DownOrNot = false;
	public static boolean isServiceOn = false;
	public static boolean isScanActivityUp = false; // 是否有Acitivty与服务绑定
	private boolean isScanShortcutSupport = false;
	private WakeLockUtil mWakeLockUtil = null;
	public static String uhfName = "com.jiebao.ht380k.uhf.UHFMainActivity";
	public static String chaobiaoName = "com.jiebao.ht380k.chaobiao.ChaoBiaoActivity";
	public static String rs232Name = "com.jiebao.ht380.rs232.RS232Activity";
	public static String psamName = "com.jiebao.ht380k.psam.PSamActivity";
	public static String uhfName2 = "com.jiebao.ht380k.uhf.UHFActivityMode2";
	public static String uhfName3 = "com.jiebao.ht380k.uhf.UHFActivityMode3";
	public static String scanName = "com.jiebao.ht380k.scan.ScanActivity";
	private FileOutputStream fileOutputStream;
	private File scanFile;
	private boolean sdCard;
	
	public BeepManager mBeepManager;
	private Intent webAddressintent;
	private Intent scanDataIntent;
	private Intent EditTextintent;
	//private BarcodeUntil scanUtil;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 3001:
				Toast.makeText(
						ScanService.this,
						getResources().getString(
								R.string.scan_init_failure_info), 5000).show();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void setActivityUp(boolean isActivityUp) {
		this.isActivityUp = isActivityUp;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		isServiceOn = true;
		km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		System.out.println("Service onCreate()");
		mWakeLockUtil = new WakeLockUtil(this);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.jb.action.F4key");
		intentFilter.addAction("ReLoadCom");
		intentFilter.addAction("ReleaseCom");
		registerReceiver(f4Receiver, intentFilter);
		// 解决重新唤醒后 讯宝扫描乱码
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		registerReceiver(ScreenBroadcastReceiver, filter);
		System.out.println("onCreateend");

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				sdCard = true;
				File file = new File("mnt/sdcard/Scan Record");
				if (!file.exists()) {
					file.mkdirs();
				}

				scanFile = new File("mnt/sdcard/" + "Scan Record" + "/"
						+ "Scan Record.txt");
				if (!scanFile.exists())
					scanFile.createNewFile();
				fileOutputStream = new FileOutputStream(scanFile, true);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mBeepManager = new BeepManager(this,
				Preference.getScanSound(this, true),
				Preference.getScanVibration(this, false));

		// if (Preference.getScannerModel(this) == ScanController.MODEL_N3680) {
		// scan_time_limit = 500;
		// } else {
		// scan_time_limit = 100;
		// }

		IntentFilter jbScanFilter = new IntentFilter();
		jbScanFilter.addAction("com.jb.action.SCAN_SWITCH");
		jbScanFilter.addAction("com.jb.action.START_SCAN");
		jbScanFilter.addAction("com.jb.action.STOP_SCAN");
		jbScanFilter.addAction("com.jb.action.START_SCAN_CONTINUE");
		jbScanFilter.addAction("com.jb.action.STOP_SCAN_CONTINUE");
		this.registerReceiver(jbScanReceiver, jbScanFilter);
		
		scanDataIntent = new Intent("com.jb.action.GET_SCANDATA");
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		isScanShortcutSupport = Preference.getScanShortcutSupport(this, true);
		if (scanManager == null) {
			scanManager = ScanController.getInstance();
		} else {
			// if (scanManager.isSerialPort_isOpen()) {
			scanManager.Barcode_Close();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// }
		}
		Log.e(TAG, "onStart()");
		scanManager.Barcode_Open(ScanService.this, dataReceived);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Service onStart()");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		System.out.println("Service onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}

	public ScanController getScanManager() {
		if (null == scanManager) {
			scanManager = ScanController.getInstance();
			 Log.e(TAG, "getScanManager()");
			scanManager.Barcode_Open(ScanService.this, dataReceived);
			// scanManager.SetReadListener(dataReceived);
		}
		return scanManager;
	}

	/**
	 * 扫描数据接收类
	 * 
	 * @author Administrator
	 * 
	 */
	ScanController.Callback dataReceived = new ScanController.Callback() {

		@Override
		public void Barcode_Read(byte[] buffer, String codeId, int errorCode) {
			mBeepManager.play();
			String codeType = Tools.returnType(buffer);
			String val = null;
			if (codeType.equals("default")) {
				val = new String(buffer);
			} else {
				try {
					val = new String(buffer, codeType);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (null != mScanListener) {
				System.out.println("val:" + val);
				mScanListener.henResult(codeId, val);
			}
			if (null != scanManager) {
				if (!isActivityUp) {
					houtai_result(codeId , val);
				}
			}
			
//			if (sdCard && getScanSaveTxt()) {
//				scanUtil = new BarcodeUntil();
//				scanUtil.setScanDate(Tools.getNowTimeString());
//				scanUtil.setScanResult(val);
//				saveInTxt(scanUtil.getScanResult() + "\n"
//						+ "Scan time" + ": " + scanUtil.getScanDate() + "\n");
//			}
//			Log.e(TAG, "onDataReceived val:" + val);
		}
	};

	public void setOnScanListener(ScanListener scanListener) {
		this.mScanListener = scanListener;

	}

	public Binder myBinder = new MyBinder();

	public class MyBinder extends Binder {

		public ScanService getService() {

			return ScanService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	long nowTime = 0;
	long lastTime = 0;
	/**
	 * 捕获扫描物理按键广播
	 */
	private BroadcastReceiver f4Receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("ReLoadCom".equals(intent.getAction())) {
				System.out.println("ReLoadCom()");
				if (scanManager != null) {
					scanManager.Barcode_Close();
					scanManager = ScanController.getInstance();
					Log.e(TAG, "ReLoadCom");
					scanManager.Barcode_Open(ScanService.this, dataReceived);
					System.out.println("scanManager.init()");
				}
				// init();
				return;
			}
			if ("ReleaseCom".equals(intent.getAction())) {
				System.out.println("ReleaseCom()");
				if (scanManager != null) {
					scanManager.Barcode_Close();
				}
				return;
			}
			if (km.inKeyguardRestrictedInputMode()) {// 屏已锁
				System.out.println("lock");
				return;
			}
			// Bundle bundle = intent.getExtras();
			if (intent.hasExtra("F4key")) {
				if (intent.getStringExtra("F4key").equals("down")) {
					Log.e("trig", "key down");
					keyF2DownOrNot = true;
					mHandler.removeCallbacks(stopKeyDown);
					if (null != scanManager) {
						if (getScanShortcutSupport()) {
							nowTime = System.currentTimeMillis();
							mWakeLockUtil.lock();

							if (nowTime - lastTime > scan_time_limit) {
								Log.e("ScanService trig", "key down");
								scanManager.Barcode_Stop();

								lastTime = nowTime;
								scanManager.Barcode_Start();
								// Log.e("time", "doScan");
							}
						}
					}
				} else if (intent.getStringExtra("F4key").equals("up")) {
					Log.e("trig", "key up");
					if (null != scanManager) {
						if (getScanShortcutSupport()
								&& getScanShortCutMode().equals("2")) {
							mWakeLockUtil.unLock();
							mHandler.postDelayed(stopKeyDown, 1000);
							if (Preference
									.getScanShortCutPressMode(ScanService.this) == 2) {
								scanManager.Barcode_Stop();
							}
						}
					}
				}
			}
		}
	};

	private BroadcastReceiver ScreenBroadcastReceiver = new BroadcastReceiver() {
		private String action = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				// 开屏
				System.out.println("ACTION_SCREEN_ON");
				if (!checkIsInFirst()) {
					if (scanManager == null) {
						scanManager = ScanController.getInstance();
					} else {
						// if (scanManager.isSerialPort_isOpen()) {
						scanManager.Barcode_Close();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// }
					}
					// Log.e(TAG, "ACTION_SCREEN_ON()");
					scanManager.Barcode_Open(ScanService.this, dataReceived);
					// scanManager.SetReadListener(dataReceived);
					System.out.println("scanManager.init()");
				}
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				// 锁屏
				System.out.println("ACTION_SCREEN_OFF");
				// 以防别使用该的串口的被下电s
				if (!checkIsInFirst()) {
					if (scanManager != null) {
						scanManager.Barcode_Close();
						scanManager.Barcode_Stop();
						// scanManager = null;
					}
				}
				// scanManager.power("0");
			} else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
				System.out.println("ACTION_USER_PRESENT");
			}
		}
	};

	private BroadcastReceiver jbScanReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.v(TAG,"jbScanReceiver intent.getAction(): "+intent.getAction());
			if ("com.jb.action.SCAN_SWITCH".equals(intent.getAction())
					&& intent.hasExtra("extra")) {
				int extra = intent.getIntExtra("extra", -1);
				if (extra == 1) {
					if (null != scanManager) {
						scanManager
								.Barcode_Open(ScanService.this, dataReceived);
					}
				} else if (extra == 0) {
					if (null != scanManager) {
						scanManager.Barcode_Close();
					}
				}
			} 
			if ("com.jb.action.START_SCAN".equals(intent.getAction())) {
				keyF2DownOrNot = true;
				mHandler.removeCallbacks(stopKeyDown);
				if (null != scanManager) {
					scanManager.Barcode_Start();
				}
			} 
			if ("com.jb.action.STOP_SCAN".equals(intent.getAction())) {
				mHandler.postDelayed(stopKeyDown, 1000);
				if (null != scanManager) {
					scanManager.Barcode_Stop();
				}
			}
			if ("com.jb.action.START_SCAN_CONTINUE".equals(intent.getAction())) {
				long continue_interval = 1000;
				if(intent.hasExtra("continue_interval")){
					continue_interval = intent.getIntExtra("continue_interval", 1000);
					Log.d("ScanService", "continue_interval:"+continue_interval);
				}
				if (null != scanManager) {
					scanManager.Barcode_Continue_Start(continue_interval);
				}
			} 
			if ("com.jb.action.STOP_SCAN_CONTINUE".equals(intent.getAction())) {
				if (null != scanManager) {
					scanManager.Barcode_Continue_Stop();
				}
			}
		};
	};

	@Override
	public void onDestroy() {
		super.onDestroy();

		scanManager.Barcode_Close();
		scanManager.Barcode_Stop();
		unregisterReceiver(f4Receiver);
		unregisterReceiver(ScreenBroadcastReceiver);
		unregisterReceiver(jbScanReceiver);
		// power("0");
		// scan("1");
		Log.i(TAG, "onDestroy");
		mWakeLockUtil.unLock();
		sendBroadcast(new Intent("ScanServiceDestroy"));
		// unregisterReceiver(screenOnBroadcastReceiver);
		System.err.println("ScanService_Destroy");
		isServiceOn = false;
	}

	private Runnable stopKeyDown = new Runnable() {

		@Override
		public void run() {
			// System.out.println("keyF2DownOrNot:" + keyF2DownOrNot);
			keyF2DownOrNot = false;
		}
	};

	/**
	 * 检查最上层Activity 是否为使用相同串口Activity 防冲突
	 * 
	 * @return
	 */
	private boolean checkIsInFirst() {
		cn = am.getRunningTasks(1).get(0).topActivity;
		Log.d("", "pkg:" + cn.getPackageName());
		Log.d("", "cls:" + cn.getClassName());
		if (cn.getClassName().equals(rs232Name)
				|| cn.getClassName().equals(psamName)
				|| cn.getClassName().equals(chaobiaoName)
				|| cn.getClassName().equals(uhfName)
				|| cn.getClassName().equals(uhfName2)
				|| cn.getClassName().equals(uhfName3))
			return true;
		return false;
	}

	/**
	 * 模拟键盘输出文本
	 * 
	 * @param data
	 *            数据
	 */
//	public void simulatekey(String data) {
//		// TODO Auto-generated method stub
//		int suffixModel = Preference.getScanSuffixModel(this,
//				ScanSetupActivity.SuffixModel_Enter);
//		StringBuilder sb = new StringBuilder();
//		sb.append(data);
//		EditTextintent = new Intent("com.haier.DeITTEXTs");
//		switch (suffixModel) {
//		case ScanSetupActivity.SuffixModel_Enter:
//			EditTextintent.putExtra("showvalue", sb.toString() + "\n");
//			this.sendBroadcast(EditTextintent);
//			break;
//		case ScanSetupActivity.SuffixModel_Semicolon:
//			sb.append(";");
//			EditTextintent.putExtra("showvalue", sb.toString());
//			this.sendBroadcast(EditTextintent);
//			break;
//		default:
//			EditTextintent.putExtra("showvalue", sb.toString());
//			this.sendBroadcast(EditTextintent);
//			break;
//		}
//			System.out.println("simulatekey");
//	}


	/**
	 * 将内容显示到文本框
	 * 
	 * @param dataStr
	 */
//	public void sendEditTextBroadcast(String dataStr) {
//		// TODO Auto-generated method stub
//		// webAddressHandler(dataStr);
//		int suffixModel = Preference.getScanSuffixModel(this,
//				ScanSetupActivity.SuffixModel_Enter);
//		StringBuilder sb = new StringBuilder();
//		sb.append(dataStr);
//		EditTextintent = new Intent("com.haier.DeITTEXT");
//		switch (suffixModel) {
//		case ScanSetupActivity.SuffixModel_Enter:
//			EditTextintent.putExtra("showvalue", sb.toString() + "\n");
//			this.sendBroadcast(EditTextintent);
//			break;
//		case ScanSetupActivity.SuffixModel_Semicolon:
//			sb.append(";");
//			EditTextintent.putExtra("showvalue", sb.toString());
//			this.sendBroadcast(EditTextintent);
//			break;
//		default:
//			EditTextintent.putExtra("showvalue", sb.toString());
//			this.sendBroadcast(EditTextintent);
//			break;
//		}
//	}

	/**
	 * 将内容发送广播
	 * 
	 * @param dataStr
	 */
	public void sendScanBroadcast(String codeid , String dataStr) {
		// TODO Auto-generated method stub
		scanDataIntent.putExtra("data", dataStr);
		scanDataIntent.putExtra("codetype", codeid);
		this.sendBroadcast(scanDataIntent);
	}

	/**
	 * 网页支持
	 * 
	 * @param dataStr
	 */
	public void webAddressHandler(String dataStr) {
		try {
			webAddressintent = new Intent();
			webAddressintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			webAddressintent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(dataStr);
			webAddressintent.setData(content_url);
			this.startActivity(webAddressintent);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 
	 * 扫描内容保存至TXT
	 * 
	 * @param data
	 */
	public void saveInTxt(String data) {
		byte[] bytes = data.getBytes();
		try {
			fileOutputStream.write(bytes);
			fileOutputStream.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//1快速扫描  2是模拟键盘 3广播
	public void houtai_result(String codeid , String data) {
		if (data != null && !data.trim().equals("")) {
			int out_mode = 3;
			switch (out_mode) {
			case 3:
				sendScanBroadcast(codeid , data);
				break;

			case 2:
				//simulatekey(data);
				break;

			case 1:
			//sendEditTextBroadcast(data);
				break;

			default:
				break;
			}

			if (getScanNetSupport()) {
				webAddressHandler(data);
			}
		}
	}

	/**
	 * 是否支持扫描快捷键 默认支持
	 * 
	 * @return <code>true</code> 支持 <code>false</code>不支持
	 */
	public boolean getScanShortcutSupport() {
		return isScanShortcutSupport;
	}

	/**
	 * 是否支持网页扫描 默认不支持
	 * 
	 * @return <code>true</code> 支持 <code>false</code>不支持
	 */
	public boolean getScanNetSupport() {
		return Preference.getNetPageSupport(this, false);
	}

//	/**
//	 * 支持扫描快捷自定义后缀 默认后缀回车 -1:无后缀 0：回车 1：分号
//	 */
//	public int getScanSuffixModel() {
//		return Preference.getScanSuffixModel(this,
//				ScanSetupActivity.SuffixModel_Enter);
//	}

	/**
	 * 是否支持扫描记录保存至TXT文档 默认不支持 <code>true</code> 支持 <code>false</code> 不支持
	 */
	public boolean getScanSaveTxt() {
		return Preference.getScanSaveTxt(this, false);
	}

	/**
	 * 获取扫描快捷键 默认为中间橙色按键
	 * 
	 * @param scanShortcutMode
	 *            1:左侧橙色按键 2:中间橙色按键 3:右边橙色按键
	 */
	public String getScanShortCutMode() {
		return Preference.getScanShortcutMode(this, "2");
	}

	/**
	 * 后台扫描输出模式 默认快速扫描
	 * 
	 * @param ScanOutMode
	 *            1.快速扫描（文本框） 2.模拟键盘 3.广播
	 */
	public int getScanOutMode() {
		return Preference.getScanOutMode(this);
	}
	
	public void setIsScanShortcutSupport(boolean isScanShortcutSupport){
		this.isScanShortcutSupport = isScanShortcutSupport;
	}
}
