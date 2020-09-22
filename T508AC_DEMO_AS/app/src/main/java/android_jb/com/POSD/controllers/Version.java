package android_jb.com.POSD.controllers;

import android.util.Log;

public class Version {
	private static String Version = "V3.5";

	public static String getVersion() {
		Log.i("Library Version:", Version);
		return Version;
	}
}
