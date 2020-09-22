package android_jb.com.POSD;

import android.content.Context;
import android.content.SharedPreferences;

import android_jb.com.POSD.util.ResourceUtil;


public class Preference {
	private static SharedPreferences getSP(Context context) {
		return context.getSharedPreferences(
		// context.getString(R.string.app_name), Context.MODE_PRIVATE);
				context.getString(ResourceUtil.getStringResIDByName(context,
						"app_name")), Context.MODE_PRIVATE);
	}

	public static boolean isScreenOn(Context context) {
		return getSP(context).getBoolean("IsScreenOn", true);
	}

	public static void setScreenOn(Context context, boolean value) {
		getSP(context).edit().putBoolean("IsScreenOn", value).commit();
	}

	/**
	 * 扫描头类型 默认为-1
	 * 
	 * @param context
	 * @return
	 */
	public static int getScannerModel(Context context) {
		return getSP(context).getInt("ScannerModel", -1);
	}

	/**
	 * 扫描头类型
	 * 
	 * @param context
	 * @param value
	 */
	public static void setScannerModel(Context context, int value) {
		getSP(context).edit().putInt("ScannerModel", value).commit();
	}

	/**
	 * 扫描头协议编号 默认为-1
	 * 
	 * @param context
	 * @return
	 */
	public static int getScannerPrefix(Context context) {
		return getSP(context).getInt("ScannerPrefix", -1);
	}

	/**
	 * 扫描头协议编号
	 * 
	 * @param context
	 * @param value
	 */
	public static void setScannerPrefix(Context context, int value) {
		getSP(context).edit().putInt("ScannerPrefix", value).commit();
	}

	/**
	 * 扫描头是否恢复出厂设置
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getScannerIsReturnFactory(Context context) {
		return getSP(context).getBoolean("IsReturnFactory", false);
	}

	/**
	 * 设置扫描头恢复出厂设置
	 * 
	 * @param context
	 * @param value
	 */
	public static void setScannerIsReturnFactory(Context context,
			boolean isReturnFactory) {
		getSP(context).edit().putBoolean("IsReturnFactory", isReturnFactory)
				.commit();
	}

	/**
	 * 扫描类型（一维、二维）
	 * 
	 * @param context
	 * @return
	 */
	public static int getScanDeviceType(Context context) {
		return getSP(context).getInt("ScanDeviceType", 1);
	}

	/**
	 * 扫描类型（一维、二维）
	 * 
	 * @param context
	 * @param value
	 */
	public static void setScanDeviceType(Context context, int value) {
		getSP(context).edit().putInt("ScanDeviceType", value).commit();
	}

	/**
	 * 后台扫描输出模式  默认快速扫描
	 * @param context
	 * @param ScanOutMode 1.快速扫描（文本框） 2.模拟键盘 3.广播
	 */
	public static void setScanOutMode(Context context,
			int ScanOutMode) {
		getSP(context).edit()
				.putInt("ScanOutMode", ScanOutMode)
				.commit();
	}

	public static int getScanOutMode(Context context) {
		return getSP(context).getInt("ScanOutMode", 1);
	}

	public static void setNetPageSupport(Context context,
			boolean isNetPageSupport) {
		getSP(context).edit().putBoolean("IsNetPageSupport", isNetPageSupport)
				.commit();
	}

	public static boolean getNetPageSupport(Context context,
			boolean defaultValues) {
		return getSP(context).getBoolean("IsNetPageSupport", defaultValues);
	}

	public static void setScanSound(Context context, boolean isScanSound) {
		getSP(context).edit().putBoolean("IsScanSound", isScanSound).commit();
	}

	public static boolean getScanSound(Context context, boolean defaultValues) {
		return getSP(context).getBoolean("IsScanSound", defaultValues);
	}

	public static void setScanVibration(Context context, boolean isScanVibration) {
		getSP(context).edit().putBoolean("IsScanVibration", isScanVibration)
				.commit();
	}

	public static boolean getScanVibration(Context context,
			boolean defaultValues) {
		return getSP(context).getBoolean("IsScanVibration", defaultValues);
	}

	public static void setScanSaveTxt(Context context, boolean isScanSaveTxt) {
		getSP(context).edit().putBoolean("IsScanSaveTxt", isScanSaveTxt)
				.commit();
	}

	public static boolean getScanSaveTxt(Context context, boolean defaultValues) {
		return getSP(context).getBoolean("IsScanSaveTxt", defaultValues);
	}
	
	public static void setScanTest(Context context, boolean isScanSaveTxt) {
		getSP(context).edit().putBoolean("IsScanTest", isScanSaveTxt)
				.commit();
	}

	public static boolean getScanTest(Context context) {
		return getSP(context).getBoolean("IsScanSaveTxt", false);
	}

	public static void setScanShortcutSupport(Context context,
			boolean isScanShortcutSupport) {
		getSP(context).edit()
				.putBoolean("IsScanShortcutSupport", isScanShortcutSupport)
				.commit();
	}

	public static boolean getScanShortcutSupport(Context context,
			boolean defaultValues) {
		return getSP(context)
				.getBoolean("IsScanShortcutSupport", defaultValues);
	}

	/**
	 * 设置扫描快捷键
	 * 
	 * @param context
	 * @param scanShortcutMode
	 *            1:左侧橙色按键 2:中间橙色按键 3:右边橙色按键
	 */
	public static void setScanShortcutMode(Context context,
			String scanShortcutMode) {
		getSP(context).edit().putString("ScanShortcutMode", scanShortcutMode)
				.commit();
	}

	public static String getScanShortcutMode(Context context,
			String defaultValues) {
		return getSP(context).getString("ScanShortcutMode", defaultValues);
	}

	/**
	 * 设置扫描快捷键按键模式
	 * 
	 * @param context
	 * @param scanShortcutMode
	 *            1:3秒后自动收光 2:抬起按键收光
	 */
	public static void setScanShortCutPressMode(Context context,
			int scanShortCutPressMode) {
		getSP(context).edit()
				.putInt("ScanShortCutPressMode", scanShortCutPressMode)
				.commit();
	}

	public static int getScanShortCutPressMode(Context context) {
		return getSP(context).getInt("ScanShortCutPressMode", 1);
	}

	public static void setScanSelfopenSupport(Context context,
			boolean isScanSelfopenSupport) {
		getSP(context).edit()
				.putBoolean("IsScanSelfopenSupport", isScanSelfopenSupport)
				.commit();
	}

	public static boolean getScanSelfopenSupport(Context context,
			boolean defaultValues) {
		return getSP(context)
				.getBoolean("IsScanSelfopenSupport", defaultValues);
	}

	/**
	 * 设置后台扫描的后缀
	 * 
	 * @param context
	 * @param suffixModel
	 *            -1为没有，0为回车，1为分号
	 */
	public static void setScanSuffixModel(Context context, int suffixModel) {
		if (suffixModel >= -1 && suffixModel <= 1)
			getSP(context).edit().putInt("ScanSuffixModel", suffixModel)
					.commit();
	}

	/**
	 * 获取后台扫描的后缀
	 * 
	 * @param context
	 * @param defalutValue
	 *            -1为没有，0为回车，1为分号
	 * @return -1为没有，0为回车，1为分号
	 */
	public static int getScanSuffixModel(Context context, int defalutValue) {
		if (defalutValue >= -1 && defalutValue <= 1)
			return getSP(context).getInt("ScanSuffixModel", defalutValue);
		else
			return getSP(context).getInt("ScanSuffixModel", 0);
	}

	/**
	 * 设置NFC一件出光
	 * 
	 * @param context
	 * @param isScanSelfopenSupport
	 */
	public static void setNfcBackgroundSupport(Context context,
			boolean isScanSelfopenSupport) {
		getSP(context).edit()
				.putBoolean("IsScanSelfopenSupport", isScanSelfopenSupport)
				.commit();
	}

	public static boolean getNfcBackgroundSupport(Context context,
			boolean defaultValues) {
		return getSP(context)
				.getBoolean("IsScanSelfopenSupport", defaultValues);
	}

	/**
	 * 设置NFC一件出光模式
	 * 
	 * @param context
	 * @param isScanSelfopenSupport
	 */
	public static void setNfcSimulateKeySupport(Context context,
			boolean isNfcSimulateKeySupport) {
		getSP(context).edit()
				.putBoolean("IsNfcSimulateKeySupport", isNfcSimulateKeySupport)
				.commit();
	}

	public static boolean getNfcSimulateKeySupport(Context context,
			boolean defaultValues) {
		return getSP(context).getBoolean("IsNfcSimulateKeySupport",
				defaultValues);
	}

	/**
	 * 设置应用已经启动过
	 * 
	 * @param context
	 * @param isScanSelfopenSupport
	 */
	public static void setIsFirstStartNot(Context context) {
		getSP(context).edit().putBoolean("IsFirstStart", false).commit();
	}

	public static boolean getIsFirstStartNot(Context context) {
		return getSP(context).getBoolean("IsFirstStart", true);
	}
	
	/**
	 * 设置扫描是否已经初始化过
	 * 
	 * @param context
	 * @param isScanSelfopenSupport
	 */
	public static void setScanInit(Context context , boolean isture) {
		getSP(context).edit().putBoolean("IsScanInit", isture).commit();
	}

	public static boolean getIsScanInit(Context context) {
		return getSP(context).getBoolean("IsScanInit", false);
	}
}
