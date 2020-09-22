package android_jb.com.POSD.util;

public class MachineVersion {
	

	public static String getMachineVersion() {
		try {
			String version = android.os.Build.DISPLAY;
			version = version.substring(0, 7);
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
