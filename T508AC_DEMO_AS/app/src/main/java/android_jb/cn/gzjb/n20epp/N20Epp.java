package android_jb.cn.gzjb.n20epp;

public class N20Epp {
	    public native int pinpad_open(byte [] byPortName);
	    public native int pinpad_check_conn();
	    public native int pinpad_inject_masterkey(byte masterindex, byte desflag, byte []keydata, int datalen);
	    public native int pinpad_inject_workkey(byte masterindex, byte pinindex, byte macindex, byte desindex, byte desflag, byte []keydata, int datalen);
	    public native int pinpad_get_pinblock(byte pinindex, short type, byte mode, byte []amt, byte []price, byte []pan, byte []track2, byte []pinblock, short []pinblklen);
	    public native int pinpad_get_plaintext_pin(byte pinindex,  short type, char mode, byte []pin);
	    public native int pinpad_get_mac(byte macindex, byte []data, short len, byte []mac);
	    public native int pinpad_encrypted_track(byte desindex, byte mode, byte []data, int datalen, byte []endata);
	    public native int pinpad_clear();
	    public native int pinpad_confirm_amt(byte []amt, int len);
	    public native int pinpad_display_string(byte []str, int len);
	    public native int pinpad_close();
}
