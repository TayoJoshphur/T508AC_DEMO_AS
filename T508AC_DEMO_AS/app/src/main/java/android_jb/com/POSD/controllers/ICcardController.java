package android_jb.com.POSD.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.imagpay.Apdu_Send;
import com.imagpay.Settings;
import com.imagpay.emv.EMVApp;
import com.imagpay.emv.EMVCapk;
import com.imagpay.emv.EMVConstants;
import com.imagpay.emv.EMVParam;
import com.imagpay.emv.EMVRevoc;
import com.imagpay.ttl.TTLHandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import android_jb.com.POSD.util.MachineVersion;

public class ICcardController implements Serializable {
	private static final long serialVersionUID = -4498293031062441096L;
	private static final String TAG = "ICcardController";
	private static ICcardController iccardController = null;
	private TTLHandler handler = null;
	private Settings settings = null;
	private String version = MachineVersion.getMachineVersion().substring(0, 7);
    
	private String IO_OE = "/proc/jbcommon/gpio_control/UART3_EN"; // 默认值：1，其他值无效
	private String IO_CS0 = "/proc/jbcommon/gpio_control/UART3_SEL0";// A默认值：1，其他值无效
	private String IO_CS1 = "/proc/jbcommon/gpio_control/UART3_SEL1";// B默认值：1，其他值无效
	private String power = "/proc/jbcommon/gpio_control/ICCard_CTL";// 默认值：1，其他值无效
	public static ICcardController getInstance() {
		Log.i(TAG, "getInstance");
		if (null == iccardController) {
			iccardController = new ICcardController();
		}
		return iccardController;
	}

	/*
	 * 
	 * 初始化之前，做好准备
	 */
	public void ICcardController_Init(Context c){
		if (null == handler && null == settings) {
			writeFile(new File(IO_OE),"0");
			writeFile(new File(IO_CS0),"1");
			writeFile(new File(IO_CS1),"1");
			writeFile(new File(power),"1");
			//writeFile(new File(power),"1");
			handler = new TTLHandler(c);
			handler.setParameters("/dev/ttyS3", 115200);
			settings = new Settings(handler);
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
	 * 功能：打开设备
	 * 
	 * @return true:成功，false:失败
	 */
	public boolean ICcardController_Open(Context c) {
		
		boolean flag = false;
		if (!handler.isConnected()) {
			handler.close();
			flag = handler.connect();
		} else {
			handler.close();
			flag = handler.connect();
		}
		return flag;
	}
	
	public boolean ICcardController_isConnected() {
		return handler.isConnected();
	}
	
	public String ICcardController_readVersion() {
		return (null != settings) ? settings.readVersion() : "";
	}

	public boolean ICcardController_Close() {
		writeFile(new File(IO_CS0),"0");
		writeFile(new File(IO_CS1),"0");
		writeFile(new File(power),"0");
		if (null != handler) {
			handler.close();
			handler = null;
			if (null != settings) {
				settings = null;
			}
			return true;
		}
		return false;
	}

	public String ICcardController_icReset() {
		return (null != settings) ? settings.icReset() : "";
	}

	public String ICcardController_icOff() {
		return (null != settings) ? settings.icOff() : "";
	}

	public String ICcardController_icCardNo() {
		try {
			return (null != settings) ? settings.icCardNo() : "";
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public String[] emv() {
//		handleros.sendEmptyMessage(showReadDailog);
		long start = System.currentTimeMillis();
		String[] emv={"","",""};
		EMVParam param = new EMVParam();
		param.setSlot((byte) 0x00);
		// param.setReadOnly(true);
		// if call setReadOnly(true), SDK will only read card data
		// if call setReadOnly(false), SDK will read card data and verify pin
		// and submit data
		param.setMerchName("4368696E61");// hex string of china
		param.setMerchCateCode("0001");
		param.setMerchId("313233343536373839303132333435");
		param.setTermId("3132333435363738");
		param.setTerminalType((byte) 0x22);
		param.setCapability("E0F8C8");
		// param.setCapability("E028C8");//do not support pin
		param.setExCapability("F00000A001");
		param.setTransCurrExp((byte) 0x02);
		param.setCountryCode("0840");
		param.setTransCurrCode("0840");
		param.setTransType((byte) 0x00);// EMVConstants.TRANS_TYPE_GOODS/EMVConstants.TRANS_TYPE_CASH/EMVConstants.TRANS_TYPE_CASHBACK
		param.setTermIFDSn("3838383838383838");// SN is 88888888
		param.setAuthAmnt(8000000);// transaction amount
		param.setOtherAmnt(0);
		Date date = new Date();
		DateFormat sdf = new SimpleDateFormat("yyMMdd");
		param.setTransDate(sdf.format(date));
		sdf = new SimpleDateFormat("HHmmss");
		param.setTransTime(sdf.format(date));

		// FIME parameters(MasterCard Test Card), if other card type, need to change.
		loadMasterCardAIDs(param);
		loadMasterCardCapks(param);
		loadMasterCardRevocs(param);
		// Visa
		loadVisaAIDs(param);
		loadVisaCapks(param);
		loadVisaRevocs(param);
		loadChinaAIDs(param);

		handler.kernelInit(param);
		settings.icReset();
		String icReset=handler.icReset();
//		if (icReset != null) {
			handler.process();
//		}
		handler.icOff();
		long end = System.currentTimeMillis();
		String dataNo = handler.getTLVDataByTag(0x5a);
		if (dataNo != null)
		emv[0]=dataNo;
	     else
		emv[0]="";
		String dataHolder = handler.getTLVDataByTag(0x5F20);
		if (dataHolder != null) {
			StringBuffer sb = new StringBuffer();
			String[] holder_data = dataHolder.replaceAll("..", "$0 ").trim()
					.split(" ");
			for (String s : holder_data) {
				sb.append((char) Integer.parseInt(s, 16));
			}
			emv[1]= sb.toString();
		} else
			emv[1]="";
		String dataDate = handler.getTLVDataByTag(0x5F24);
		if (dataDate != null)
			emv[2] =dataDate;
		else
			emv[2]="";
		return emv;
		//sendMessage("IC卡读卡时间:" + (end - start) + "ms");
	}

	private void loadVisaCapks(EMVParam ep) {
		// 01
		EMVCapk ec = new EMVCapk();
		ec.setRID("A000000003");
		ec.setKeyID((byte) 0x01);
		ec.setModul("C696034213D7D8546984579D1D0F0EA5" + "19CFF8DEFFC429354CF3A871A6F7183F"
				+ "1228DA5C7470C055387100CB935A712C" + "4E2864DF5D64BA93FE7E63E71F25B1E5"
				+ "F5298575EBE1C63AA617706917911DC2" + "A75AC28B251C7EF40F2365912490B939"
				+ "BCA2124A30A28F54402C34AECA331AB6" + "7E1E79B285DD5771B5D9FF79EA630B75");
		ec.setExponent("03");
		ec.setExpDate("491231");// YYMMDD
		ec.setCheckSum("D34A6A776011C7E7CE3AEC5F03AD2F8CFC5503CC");
		ep.addCapk(ec);
		// 07
		ec = new EMVCapk();
		ec.setRID("A000000003");
		ec.setKeyID((byte) 0x07);
		ec.setModul("A89F25A56FA6DA258C8CA8B40427D927" + "B4A1EB4D7EA326BBB12F97DED70AE5E4"
				+ "480FC9C5E8A972177110A1CC318D06D2" + "F8F5C4844aC5FA79A4DC470BB11ED635"
				+ "699C17081B90F1B984F12E92C1C52927" + "6D8AF8EC7F28492097D8CD5BECEA16FE"
				+ "4088F6CFAB4A1B42328A1B996F9278B0" + "B7E3311CA5EF856C2F888474B83612A8"
				+ "2E4E00D0CD4069A6783140433D50725F");
		ec.setExponent("03");
		ec.setExpDate("491231");// YYMMDD
		ec.setCheckSum("B4BC56CC4E88324932CBC643D6898F6FE593B172");
		ep.addCapk(ec);
		// 08
		ec = new EMVCapk();
		ec.setRID("A000000003");
		ec.setKeyID((byte) 0x08);
		ec.setModul("D9FD6ED75D51D0E30664BD157023EAA1" + "FFA871E4DA65672B863D255E81E137A5"
				+ "1DE4F72BCC9E44ACE12127F87E263D3a" + "F9DD9CF35CA4A7B01E907000BA85D249"
				+ "54C2FCA3074825DDD4C0C8F186CB020F" + "683E02F2DEAD3969133F06F7845166AC"
				+ "EB57CA0FC2603445469811D293BFEFBA" + "FAB57631B3DD91E796BF850A25012F1A"
				+ "E38F05AA5C4D6D03B1DC2E5686127859" + "38BBC9B3CD3A910C1DA55A5A9218ACE0"
				+ "F7A21287752682F15832A678D6E1ED0B");
		ec.setExponent("03");
		ec.setExpDate("491231");// YYMMDD
		ec.setCheckSum("20D213126955DE205ADC2FD2822BD22DE21CF9A8");
		ep.addCapk(ec);
		// 09
		ec = new EMVCapk();
		ec.setRID("A000000003");
		ec.setKeyID((byte) 0x09);
		ec.setModul("9D912248DE0A4E39C1A7DDE3F6D25889" + "92C1A4095AFBD1824D1BA74847F2BC49"
				+ "26D2EFD904B4B54954CD189A54C5D117" + "9654F8F9B0D2AB5F0357EB642FEDA95D"
				+ "3912C6576945FAB897E7062CAA44A4AA" + "06B8FE6E3DBA18AF6aE3738E30429EE9"
				+ "BE03427C9D64F695FA8CAB4BFE376853" + "EA34AD1D76BFCAD15908C077FFE6DC55"
				+ "21ECEF5D278A96E26F57359FFAEDA194" + "34B937F1AD999DC5C41EB11935B44C18"
				+ "100E857F431A4A5A6BB65114F174C2D7" + "B59FDF237D6BB1DD0916E644D709DED5"
				+ "6481477C75D95CDD68254615F7740EC0" + "7F330AC5D67BCD75BF23D28a140826C0"
				+ "26DBDE971A37CD3EF9B8DF644AC38501" + "0501EFC6509D7A41");
		ec.setExponent("03");
		ec.setExpDate("491231");
		ec.setCheckSum("1FF80A40173F52D7D27E0F26A146A1C8CCB29046");
		ep.addCapk(ec);
		// 92
		ec = new EMVCapk();
		ec.setRID("A000000003");
		ec.setKeyID((byte) 0x92);
		ec.setModul("996AF56F569187D09293C14810450ED8" + "EE3357397B18A2458EFAA92DA3B6DF65"
				+ "14EC060195318FD43BE9B8F0CC669E3F" + "844057CBDDF8BDA191BB64473BC8DC9A"
				+ "730DB8F6B4EDE3924186FFD9B8C77357" + "89C23A36BA0B8AF65372EB57EA5D89E7"
				+ "D14E9C7B6B557460F10885DA16AC923F" + "15AF3758F0F03EBD3C5C2C949CBA306D"
				+ "B44E6A2C076C5F67E281D7EF56785DC4" + "D75945E491F01918800A9E2DC66F6008"
				+ "0566CE0DAF8D17EAD46AD8E30A247C9F");
		ec.setExponent("03");
		ec.setExpDate("491231");// YYMMDD
		ec.setCheckSum("429C954A3859CEF91295F663C963E582ED6EB253");
		ep.addCapk(ec);
		// 94
		ec = new EMVCapk();
		ec.setRID("A000000003");
		ec.setKeyID((byte) 0x94);
		ec.setModul("ACD2B12302EE644F3F835ABD1FC7A6F6" + "2CCE48FFEC622AA8EF062BEF6FB8BA8B"
				+ "C68BBF6AB5870EED579BC3973E121303" + "D34841A796D6DCBC41DBF9E52C460979"
				+ "5C0CCF7EE86FA1D5CB041071ED2C51D2" + "202F63F1156C58A92D38BC60BDF424E1"
				+ "776E2BC9648078A03B36FB554375FC53" + "D57C73F5160EA59F3AFC5398EC7B6775"
				+ "8D65C9BFF7828B6B82D4BE124A416AB7" + "301914311EA462C19F771F31B3B57336"
				+ "000DFF732D3B83DE07052D730354D297" + "BEC72871DCCF0E193F171ABA27EE464C"
				+ "6A97690943D59BDABB2A27EB71CEEBDA" + "FA1176046478FD62FEC452D5CA393296"
				+ "530AA3F41927ADFE434A2DF2AE3054F8" + "840657A26E0FC617");
		ec.setExponent("03");
		ec.setExpDate("491231");// YYMMDD
		ec.setCheckSum("C4A3C43CCF87327D136B804160E47D43B60E6E0F");
		ep.addCapk(ec);
		// 95
		ec = new EMVCapk();
		ec.setRID("A000000003");
		ec.setKeyID((byte) 0x95);
		ec.setModul("BE9E1FA5E9A803852999C4AB432DB286" + "00DCD9DAB76DFAAA47355A0FE37B1508"
				+ "AC6BF38860D3C6C2E5B12A3CAAF2A700" + "5A7241EBAA7771112C74CF9A0634652F"
				+ "BCA0E5980C54A64761EA101A114E0F0B" + "5572ADD57D010B7C9C887E104CA4EE12"
				+ "72DA66D997B9A90B5A6D624AB6C57E73" + "C8F919000EB5F684898EF8C3DBEFB330"
				+ "C62660BED88EA78E909AFF05F6DA627B");
		ec.setExponent("03");
		ec.setExpDate("491231");// YYMMDD
		ec.setCheckSum("EE1511CEC71020A9B90443B37B1D5F6E703030F6");
		ep.addCapk(ec);
	}

	private void loadMasterCardCapks(EMVParam ep) {
		// FE
		EMVCapk ec = new EMVCapk();
		ec.setRID("A000000004");
		ec.setKeyID((byte) 0xFE);
		ec.setModul("A653EAC1C0F786C8724F737F172997D63D1C3251C4" + "4402049B865BAE877D0F398CBFBE8A6035E24AFA08"
				+ "6BEFDE9351E54B95708EE672F0968BCD50DCE40F78" + "3322B2ABA04EF137EF18ABF03C7DBC5813AEAEF3"
				+ "AA7797BA15DF7D5BA1CBAF7FD520B5A482D8D3FE" + "E105077871113E23A49AF3926554A70FE10ED728CF793B62A1");
		ec.setExponent("03");
		ec.setExpDate("491231");// YYMMDD
		ec.setCheckSum("9A295B05FB390EF7923F57618A9FDA2941FC34E0");
		ep.addCapk(ec);
		// F3
		ec = new EMVCapk();
		ec.setRID("A000000004");
		ec.setKeyID((byte) 0xF3);
		ec.setModul("98F0C770F23864C2E766DF02D1E833DFF4FFE92D696E"
				+ "1642F0A88C5694C6479D16DB1537BFE29E4FDC6E6E8AFD1B0EB7EA012"
				+ "4723C333179BF19E93F10658B2F776E829E87DAEDA9C94A8B3382199A3"
				+ "50C077977C97AFF08FD11310AC950A72C3CA5002EF513FCCC286E646E3C"
				+ "5387535D509514B3B326E1234F9CB48C36DDD44B416D23654034A66F403BA511C5EFA3");
		ec.setExponent("03");
		ec.setExpDate("491231");// YYMMDD
		ec.setCheckSum("A69AC7603DAF566E972DEDC2CB433E07E8B01A9A");
		ep.addCapk(ec);
		// F8
		ec = new EMVCapk();
		ec.setRID("A000000004");
		ec.setKeyID((byte) 0xF8);
		ec.setModul(
				"A1F5E1C9BD8650BD43AB6EE56B891EF7459C0A24FA8" + "4F9127D1A6C79D4930F6DB1852E2510F18B61CD354DB83A356BD19"
						+ "0B88AB8DF04284D02A4204A7B6CB7C5551977A9B36379CA3DE1A08E"
						+ "69F301C95CC1C20506959275F41723DD5D2925290579E5A95B0DF632"
						+ "3FC8E9273D6F849198C4996209166D9BFC973C361CC826E1");
		ec.setExponent("03");
		ec.setExpDate("491231");// YYMMDD
		ec.setCheckSum("F06ECC6D2AAEBF259B7E755A38D9A9B24E2FF3DD");
		ep.addCapk(ec);
		// FA
		ec = new EMVCapk();
		ec.setRID("A000000004");
		ec.setKeyID((byte) 0xFA);
		ec.setModul(
				"A90FCD55AA2D5D9963E35ED0F440177699832F49C6" + "BAB15CDAE5794BE93F934D4462D5D12762E48C38BA83D8445DEAA"
						+ "74195A301A102B2F114EADA0D180EE5E7A5C73E0C4E11F67A43DDA"
						+ "B5D55683B1474CC0627F44B8D3088A492FFAADAD4F42422D0E70135"
						+ "36C3C49AD3D0FAE96459B0F6B1B6056538A3D6D44640F94467B10886" + "7DEC40FAAECD740C00E2B7A8852D");
		ec.setExponent("03");
		ec.setExpDate("491231");
		ec.setCheckSum("5BED4068D96EA16D2D77E03D6036FC7A160EA99C");
		ep.addCapk(ec);
		// EF
		ec = new EMVCapk();
		ec.setRID("A000000004");
		ec.setKeyID((byte) 0xEF);
		ec.setModul("A191CB87473F29349B5D60A88B3EAEE0973AA6F1A08"
				+ "2F358D849FDDFF9C091F899EDA9792CAF09EF28F5D22404B88A2293"
				+ "EEBBC1949C43BEA4D60CFD879A1539544E09E0F09F60F065B2BF2A1"
				+ "3ECC705F3D468B9D33AE77AD9D3F19CA40F23DCF5EB7C04DC8F69EBA"
				+ "565B1EBCB4686CD274785530FF6F6E9EE43AA43FDB02CE00DAEC15C7B"
				+ "8FD6A9B394BABA419D3F6DC85E16569BE8E76989688EFEA2DF22FF7D35"
				+ "C043338DEAA982A02B866DE5328519EBBCD6F03CDD686673847F84DB65"
				+ "1AB86C28CF1462562C577B853564A290C8556D818531268D25CC98A4CC"
				+ "6A0BDFFFDA2DCCA3A94C998559E307FDDF915006D9A987B07DDAEB3B" + "7DEC40FAAECD740C00E2B7A8852D");
		ec.setExponent("03");
		ec.setExpDate("491231");// YYMMDD
		ec.setCheckSum("21766EBB0EE122AFB65D7845B73DB46BAB65427A");
		ep.addCapk(ec);
		// F1
		ec = new EMVCapk();
		ec.setRID("A000000004");
		ec.setKeyID((byte) 0xF1);
		ec.setModul("A0DCF4BDE19C3546B4B6F0414D174DDE294AABBB828C"
				+ "5A834D73AAE27C99B0B053A90278007239B6459FF0BBCD7B4B9C6C5"
				+ "0AC02CE91368DA1BD21AAEADBC65347337D89B68F5C99A09D05BE02D"
				+ "D1F8C5BA20E2F13FB2A27C41D3F85CAD5CF6668E75851EC66EDBF9885"
				+ "1FD4E42C44C1D59F5984703B27D5B9F21B8FA0D93279FBBF69E0906429"
				+ "09C9EA27F898959541AA6757F5F624104F6E1D3A9532F2A6E51515AEAD1" + "B43B3D7835088A2FAFA7BE7");
		ec.setExponent("03");
		ec.setExpDate("491231");// YYMMDD
		ec.setCheckSum("D8E68DA167AB5A85D8C3D55ECB9B0517A1A5B4BB");
		ep.addCapk(ec);
	}

	private void loadVisaRevocs(EMVParam ep) {
		EMVRevoc er = new EMVRevoc();
		er.setUCRID("A000000003");
		er.setUCIndex((byte) 0x50);
		er.setUCCertSn("024455");
		ep.addRecov(er);
	}

	private void loadMasterCardRevocs(EMVParam ep) {
		EMVRevoc er = new EMVRevoc();
		er.setUCRID("A000000004");
		er.setUCIndex((byte) 0xFE);
		er.setUCCertSn("082355");
		ep.addRecov(er);
	}
	private void loadChinaAIDs(EMVParam ep) {
		// PBOC_TEST_APP
		EMVApp ea = new EMVApp();
		ea.setAppName("");
		ea.setAID("A0000003330101");
		ea.setSelFlag(EMVConstants.PART_MATCH);
		ea.setPriority((byte) 0x00);
		ea.setTargetPer((byte) 0x00);
		ea.setMaxTargetPer((byte) 0x00);
		ea.setFloorLimitCheck((byte) 0x01);
		ea.setFloorLimit(2000);
		ea.setThreshold((byte) 0x00);
		ea.setTACDenial("0000000000");
		ea.setTACOnline("0000001000");
		ea.setTACDefault("0000000000");
		ea.setAcquierId("000000123456");
		ea.setDDOL("039F3704");
		ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
		ea.setVersion("0096");
		ep.addApp(ea);
	}
	private void loadVisaAIDs(EMVParam ep) {
//		// Visa Credit/Debit
		EMVApp ea = new EMVApp();
		ea.setAppName("");
		ea.setAID("A0000000031010");
		ea.setSelFlag(EMVConstants.PART_MATCH);
		ea.setPriority((byte) 0x00);
		ea.setTargetPer((byte) 0x00);
		ea.setMaxTargetPer((byte) 0x00);
		ea.setFloorLimitCheck((byte) 0x01);
		ea.setFloorLimit(2000);
		ea.setThreshold((byte) 0x00);
		ea.setTACDenial("0000000000");
		ea.setTACOnline("0000001000");
		ea.setTACDefault("0000000000");
		ea.setAcquierId("000000123456");
		ea.setDDOL("039F3704");
		ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
		ea.setVersion("008C");
		ep.addApp(ea);

		// Visa Electron
		ea = new EMVApp();
		ea.setAppName("");
		ea.setAID("A0000000032010");
		ea.setSelFlag(EMVConstants.PART_MATCH);
		ea.setPriority((byte) 0x00);
		ea.setTargetPer((byte) 0x00);
		ea.setMaxTargetPer((byte) 0x00);
		ea.setFloorLimitCheck((byte) 0x01);
		ea.setFloorLimit(2000);
		ea.setThreshold((byte) 0x00);
		ea.setTACDenial("0000000000");
		ea.setTACOnline("0000001000");
		ea.setTACDefault("0000000000");
		ea.setAcquierId("000000123456");
		ea.setDDOL("039F3704");
		ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
		ea.setVersion("008C");
		ep.addApp(ea);

		// Visa Plus
		ea = new EMVApp();
		ea.setAppName("");
		ea.setAID("A0000000038010");
		ea.setSelFlag(EMVConstants.PART_MATCH);
		ea.setPriority((byte) 0x00);
		ea.setTargetPer((byte) 0x00);
		ea.setMaxTargetPer((byte) 0x00);
		ea.setFloorLimitCheck((byte) 0x01);
		ea.setFloorLimit(2000);
		ea.setThreshold((byte) 0x00);
		ea.setTACDenial("0000000000");
		ea.setTACOnline("0000001000");
		ea.setTACDefault("0000000000");
		ea.setAcquierId("000000123456");
		ea.setDDOL("039F3704");
		ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
		ea.setVersion("008C");
		ep.addApp(ea);
		}
	private void loadJCBAIDs(EMVParam ep) {
			//JCB
		EMVApp ea = new EMVApp();
		ea.setAppName("");
		ea.setAID("A0000000651010");
		ea.setSelFlag(EMVConstants.PART_MATCH);
		ea.setPriority((byte) 0x00);
		ea.setTargetPer((byte) 0x00);
		ea.setMaxTargetPer((byte) 0x00);
		ea.setFloorLimitCheck((byte) 0x01);
		ea.setFloorLimit(2000);
		ea.setThreshold((byte) 0x00);
		ea.setTACDenial("0000000000");
		ea.setTACOnline("0000001000");
		ea.setTACDefault("0000000000");
		ea.setAcquierId("000000123456");
		ea.setDDOL("039F3704");
		ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
		ea.setVersion("0001");
		ep.addApp(ea);
	}

	private void loadMasterCardAIDs(EMVParam ep) {
		// MasterCard Credit/Debit
		EMVApp ea = new EMVApp();
		ea.setAppName("");
		ea.setAID("A0000000041010");
		ea.setSelFlag(EMVConstants.PART_MATCH);
		ea.setPriority((byte) 0x00);
		ea.setTargetPer((byte) 0x00);
		ea.setMaxTargetPer((byte) 0x00);
		ea.setFloorLimitCheck((byte) 0x01);
		ea.setFloorLimit(2000);
		ea.setThreshold(0x00);
		ea.setTACDenial("0000000000");
		ea.setTACOnline("0000001000");
		ea.setTACDefault("0000000000");
		ea.setAcquierId("000000123456");
		ea.setDDOL("039F3704");
		ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
		ea.setVersion("008C");
		ep.addApp(ea);

		// Maestro
		ea = new EMVApp();
		ea.setAppName("");
		ea.setAID("A0000000043060");
		ea.setSelFlag(EMVConstants.PART_MATCH);
		ea.setPriority((byte) 0x00);
		ea.setTargetPer((byte) 0x00);
		ea.setMaxTargetPer((byte) 0x00);
		ea.setFloorLimitCheck((byte) 0x01);
		ea.setFloorLimit(2000);
		ea.setThreshold(0x00);
		ea.setTACDenial("0000000000");
		ea.setTACOnline("0000001000");
		ea.setTACDefault("0000000000");
		ea.setAcquierId("000000123456");
		ea.setDDOL("039F3704");
		ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
		ea.setVersion("008C");
		ep.addApp(ea);

		// Cirrus
		ea = new EMVApp();
		ea.setAppName("");
		ea.setAID("A0000000046000");
		ea.setSelFlag(EMVConstants.PART_MATCH);
		ea.setPriority((byte) 0x00);
		ea.setTargetPer((byte) 0x00);
		ea.setMaxTargetPer((byte) 0x00);
		ea.setFloorLimitCheck((byte) 0x01);
		ea.setFloorLimit(2000);
		ea.setThreshold(0x00);
		ea.setTACDenial("0000000000");
		ea.setTACOnline("0000001000");
		ea.setTACDefault("0000000000");
		ea.setAcquierId("000000123456");
		ea.setDDOL("039F3704");
		ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
		ea.setVersion("008C");
		ep.addApp(ea);

	}

	private void loadAmericanExpressAIDs(EMVParam ep) {
		// American
		EMVApp ea = new EMVApp();
		ea.setAppName("");
		ea.setAID("A00000002501");
		ea.setSelFlag(EMVConstants.PART_MATCH);
		ea.setPriority((byte) 0x00);
		ea.setTargetPer((byte) 0x00);
		ea.setMaxTargetPer((byte) 0x00);
		ea.setFloorLimitCheck((byte) 0x01);
		ea.setFloorLimit(2000);
		ea.setThreshold((byte) 0x00);
		ea.setTACDenial("0000000000");
		ea.setTACOnline("0000001000");
		ea.setTACDefault("0000000000");
		ea.setAcquierId("000000123456");
		ea.setDDOL("039F3704");
		ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
		ea.setVersion("008C");
		ep.addApp(ea);
	}

	private void loadInteracAIDs(EMVParam ep) {
		// Interac
		EMVApp ea = new EMVApp();
		ea.setAppName("");
		ea.setAID("A0000002771010");
		ea.setSelFlag(EMVConstants.PART_MATCH);
		ea.setPriority((byte) 0x00);
		ea.setTargetPer((byte) 0x00);
		ea.setMaxTargetPer((byte) 0x00);
		ea.setFloorLimitCheck((byte) 0x01);
		ea.setFloorLimit(2000);
		ea.setThreshold((byte) 0x00);
		ea.setTACDenial("0000000000");
		ea.setTACOnline("0000001000");
		ea.setTACDefault("0000000000");
		ea.setAcquierId("000000123456");
		ea.setDDOL("039F3704");
		ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
		ea.setVersion("008C");
		ep.addApp(ea);
	}

	private void loadDiscoverAIDs(EMVParam ep) {
		// Discover
		EMVApp ea = new EMVApp();
		ea.setAppName("");
		ea.setAID("A0000001523010");
		ea.setSelFlag(EMVConstants.PART_MATCH);
		ea.setPriority((byte) 0x00);
		ea.setTargetPer((byte) 0x00);
		ea.setMaxTargetPer((byte) 0x00);
		ea.setFloorLimitCheck((byte) 0x01);
		ea.setFloorLimit(2000);
		ea.setThreshold((byte) 0x00);
		ea.setTACDenial("0000000000");
		ea.setTACOnline("0000001000");
		ea.setTACDefault("0000000000");
		ea.setAcquierId("000000123456");
		ea.setDDOL("039F3704");
		ea.setTDOL("0F9F02065F2A029A039C0195059F3704");
		ea.setVersion("008C");
		ep.addApp(ea);
	}

	public String ICcardController_icDetect() {
		return (null != settings) ? settings.icDetect() : "";
	}

	public String ICcardController_getDataWithAPDU(Apdu_Send apdu) {
		return (null != settings) ? settings.getDataWithAPDU(apdu) : "";
	}

	public boolean ICcardController_sle4442Init() {
		return (null != settings) ? settings.sle4442Init() : false;
	}

	public String ICcardController_sle4442SRD(int offSet, int length) {
		return (null != settings) ? settings.sle4442SRD(offSet, length) : "";
	}

	public boolean ICcardController_sle4442SWR(int offSet, int length,
			String data) {
		return (null != settings) ? settings.sle4442SWR(offSet, length, data)
				: false;
	}

	public boolean ICcardController_sle4442CSC(String key) {
		return (null != settings) ? settings.sle4442CSC(key) : false;
	}

	public String ICcardController_sle4442RSC() {
		return (null != settings) ? settings.sle4442RSC() : "";
	}

	public boolean ICcardController_sle4442WSC(String key) {
		return (null != settings) ? settings.sle4442WSC(key) : false;
	}

	public String ICcardController_sle4442RSTC() {
		return (null != settings) ? settings.sle4442RSTC() : "";
	}

	public String ICcardController_sle4442PRD() {
		return (null != settings) ? settings.sle4442PRD() : "";
	}

	public boolean ICcardController_sle4442PWR(int offSet, int length,
			String data) {
		return (null != settings) ? settings.sle4442PWR(offSet, length, data)
				: false;
	}

	public boolean ICcardController_sle4428Init() {
		return (null != settings) ? settings.sle4428Init() : false;
	}

	public String ICcardController_sle4428SRD(int offSet, int length) {
		return (null != settings) ? settings.sle4428SRD(offSet, length) : "";
	}

	public boolean ICcardController_sle4428SWR(int offSet, int length,
			String data) {
		return (null != settings) ? settings.sle4428SWR(offSet, length, data)
				: false;
	}

	public boolean ICcardController_sle4428CSC(String key) {
		return (null != settings) ? settings.sle4428CSC(key) : false;
	}

	public String ICcardController_sle4428RSC() {
		return (null != settings) ? settings.sle4428RSC() : "";
	}

	public boolean ICcardController_sle4428WSC(String key) {
		return (null != settings) ? settings.sle4428WSC(key) : false;
	}

	public String ICcardController_sle4428RSTC() {
		return (null != settings) ? settings.sle4428RSTC() : "";
	}

	public String ICcardController_sle4428PRD(int offSet, int length) {
		return (null != settings) ? settings.sle4428PRD(offSet, length) : "";
	}

	public boolean ICcardController_sle4428PWR(int offSet, int length,
			String data) {
		return (null != settings) ? settings.sle4428PWR(offSet, length, data)
				: false;
	}

	public boolean ICcardController_at24Reset() {
		return (null != settings) ? settings.at24Reset() : false;
	}

	public boolean ICcardController_at24Write(int offSet, int length,
			String type, String data) {
		return (null != settings) ? settings.at24Write(offSet, length, type,
				data.trim()) : false;
	}

	public String ICcardController_at24Read(int offSet, int length, String type) {
		return (null != settings) ? settings.at24Read(offSet, length, type)
				: "";
	}
}
