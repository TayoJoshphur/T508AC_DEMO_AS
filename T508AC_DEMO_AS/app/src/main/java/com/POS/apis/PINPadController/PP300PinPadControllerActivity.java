package com.POS.apis.PINPadController;



import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android_jb.com.POSD.controllers.PP300PinPadController;
import jepower.com.t508ac_demo.R;


public class PP300PinPadControllerActivity<passCallBack> extends Activity implements OnClickListener {
	private static final String TAG = "PP300PinPadControllerActivity";
	private Button btn_connect;
	private Button btn_disconnect;
	private Button btn_clear;
	
	private Button btn_amountprompt;
	private EditText et_amount;
	
	private Button btn_recv;
	private TextView tv_read;
	
	private PP300PinPadController PinPadController;
//	byte[] databuff= new byte[]{0x00 ,0x03 ,0x00 ,0x22 ,0x00};
	//06 01 00 03 00 22 00 21 01 00 03 00 22 00 21 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		byte lrc = 0;
//		String strLrc;
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pp300_pinpad_controller_activity_layout);
		initview();
		PinPadController = PP300PinPadController.getInstance();
//		lrc = PinPadController.getLRC(databuff, 5);
//		strLrc = lrc + "";
//		Log.e(TAG,"lrc: "+strLrc);
//		PP300PinPadController.AscToBcd(strAmountString);
//		PP300PinPadController.dumpHex(null, PP300PinPadController.AscToBcd(strAmountString));
	}


	@SuppressLint("DefaultLocale") @Override
	public void onClick(View v) {
		byte[] panData = new byte[12];
		String strPanData = "600994119001";
		panData = strPanData.getBytes();
		String strPromptMsg = "Pls input pwd: ";
		byte[] promptMessage = new byte[strPromptMsg.length()];
		promptMessage = strPromptMsg.getBytes();
		byte[] amountBcd = new byte[]{0x00,0x00,0x00,0x10,0x00,0x00};
		String strAmountString = "0.00";
		String[] strArray = null;
		String strDealAmount;
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_connect:
			connect();
			break;
		case R.id.btn_disconnect:
			disconnect();
			break;
		case R.id.pinpad_controller_btn_recv:
			
			PinPadController.PINPad_Close();
			tv_read.setText("");
			PinPadController.PINPad_Open(mPassCallBack );
			
			Log.d(TAG,"amount: "+ et_amount.getText().toString());
			strAmountString = et_amount.getText().toString();
			strArray = strAmountString.split("\\.");
			strDealAmount = strArray[0]+strArray[1];
			
//			long lAmount = Integer.valueOf(strDealAmount);
			long lAmount = Long.parseLong(strDealAmount);
			strDealAmount = String.format("%012d", lAmount);
			Log.d(TAG,"strDealAmount: "+ strDealAmount);
			PP300PinPadController.dumpHex(null, PP300PinPadController.AscToBcd(strDealAmount));
			amountBcd = PP300PinPadController.AscToBcd(strDealAmount);
			PinPadController.PINPad_Get_PinX98(panData, amountBcd, promptMessage);
			break;
		case R.id.btn_clear:
			tv_read.setText("");
			et_amount.setText("0.00");
			break;
		default:
			break;
		}
	}
	
	
	private void connect() {
		Log.v(TAG,"connect");
		int iRet = -1;
		iRet = PinPadController.PINPad_Open(mPassCallBack );
		while (iRet != 0) {
			iRet = PinPadController.PINPad_Open( mPassCallBack);
		}
		if (iRet == 0) {
			Toast.makeText(this, "PINPad_Open_Success", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this, "PINPad_Open_Failure", Toast.LENGTH_SHORT).show();
		}
		
		byte[] mkData = {0x11 ,0x11 ,0x11 ,0x11 ,0x11 ,0x11 ,0x11 ,0x11 ,0x22 ,0x22 ,0x22 ,0x22 ,0x22 ,0x22 ,0x22 ,0x22};
		PinPadController.PINPad_updateMKey(mkData, 16);
		byte[] pinkData = {(byte) 0xE0 ,0x19 ,(byte) 0xD5 ,0x3B ,(byte) 0xC7 ,0x06 ,0x68 ,(byte) 0xCB ,0x37 ,0x2F ,0x54 ,(byte) 0xA4 ,0x41 ,0x48 ,0x77 ,0x58 ,(byte) 0xB9 ,(byte) 0x87 ,0x60 ,0x4D};
		PinPadController.PINPad_updatepinkey(pinkData, 20);
	}
	
	private void disconnect() {
		if (null != PinPadController) {
			PinPadController.PINPad_Close();
			tv_read.setText("");
			et_amount.setText("0.00");
			setViewEnabledFalse();
			Toast.makeText(this, "disconnect", Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void onDestroy(){
		super.onDestroy();
		disconnect();
	}
	private void initview() {
		// TODO Auto-generated method stub
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_recv = (Button) findViewById(R.id.pinpad_controller_btn_recv);
		
		btn_connect.setOnClickListener(this);
		btn_disconnect.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		btn_recv.setOnClickListener(this);
		
		btn_amountprompt = (Button) findViewById(R.id.pinpad_controller_btn_amountprompt);
		et_amount = (EditText) findViewById(R.id.pinpad_controller_et_amount);
		et_amount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
		et_amount.addTextChangedListener(new TextWatcher() {
			private String  numberStr;
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				int lenght = s.length();            
	              Log.e("Sun", s + "====" + start + "=======" + before + "======" + count);     
	              double number = 0.00;           //初始金额
	              //第一次输入初始化 金额值
	              if (lenght <= 1){            
	              number = Double.parseDouble(s.toString());  
	              number =number /100;//第一次 长度等于   
	              numberStr = number + "";          
	              }else {         
	                       //之后的输入带入算法后将值设置给 金额值
	                       if (s.toString().contains(".")){     
	                       numberStr = getMoneyString(s.toString());  //这个方法看第三步  
	                       }              
	               }       
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String aa = et_amount.getText().toString();  
				Log.d(TAG, aa + "*****" + numberStr);
                 //在此判断输入框的值是否等于金额的值，如果不相同则赋值，如果不判断监听器将会出现死循环
                if (!TextUtils.isEmpty(et_amount.getText().toString()) && !et_amount.getText().toString().equals(numberStr)){ 
                	
                
                Log.d(TAG, numberStr+"*****");
                et_amount.setText(numberStr);        //赋值到editText上
                et_amount.setSelection(numberStr.length()); //将光标定位到结尾
                }
			}
		});
		
		tv_read = (TextView) findViewById(R.id.pinpad_controller_tv_recv);
		setViewEnabledFalse();
	}
	
	private String getMoneyString(String money){ 
	     String overMoney = "";//结果  
	     String[] pointBoth = money.split("\\.");//分隔点前点后    
	     String beginOne = pointBoth[0].substring(pointBoth[0].length()-1);//前一位    
	     String endOne = pointBoth[1].substring(0, 1);//后一位  
	    //小数点前一位前面的字符串，小数点后一位后面 
	     String beginPoint = pointBoth[0].substring(0,pointBoth[0].length()-1);   
	     String endPoint = pointBoth[1].substring(1);   
	     Log.e("Sun", pointBoth[0]+"==="+pointBoth[1] + "====" + beginOne + "=======" + endOne+"===>"+beginPoint+"=="+endPoint ); 
	    //根据输入输出拼点  
	     if (pointBoth[1].length()>2){//说明输入，小数点要往右移    
	           overMoney=  pointBoth[0]+endOne+"."+endPoint;//拼接实现右移动    
	     }else if (
	           pointBoth[1].length()<2){//说明回退,小数点左移        
	           overMoney = beginPoint+"."+beginOne+pointBoth[1];//拼接实现左移  
	     }else {
	             overMoney = money;   
	        }   
	   //去除点前面的0 或者补 0    
	   String overLeft = overMoney.substring(0,overMoney.indexOf("."));//得到前面的字符串 
	   Log.e("Sun","左邊:"+overLeft+"===去零前"+overMoney); 
	   if (overLeft ==null || overLeft == ""||overLeft.length()<1){//如果没有就补零        
	     overMoney = "0"+overMoney; 
	   }else if(overLeft.length() > 1 && "0".equals(overLeft.subSequence(0, 1))){//如果前面有俩个零      
	     overMoney = overMoney.substring(1);//去第一个0  
	   }    
	   Log.e("Sun","結果:"+overMoney);  
	   return overMoney;
	}
	
	private void setViewEnabledFalse() {
		btn_connect.setEnabled(true);
		btn_amountprompt.setEnabled(false);
		btn_disconnect.setEnabled(false);
		btn_recv.setEnabled(false);
		btn_clear.setEnabled(false);
	}

	private void setViewEnabledTrue() {
		btn_connect.setEnabled(false);
		btn_amountprompt.setEnabled(false);
		btn_disconnect.setEnabled(true);
		btn_recv.setEnabled(true);
		btn_clear.setEnabled(true);
	}
	
	public static String bcd2Str(byte[] bytes) {  
        char temp[] = new char[bytes.length * 2], val;  
  
        for (int i = 0; i < bytes.length; i++) {  
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);  
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
  
            val = (char) (bytes[i] & 0x0f);  
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
        }  
        return new String(temp);  
    }
	
	PP300PinPadController.PassCallBack mPassCallBack = new PP300PinPadController.PassCallBack() {
		
		@Override
		public void displayReturnMsg(int type, byte[] retBuff) {
			// TODO Auto-generated method stub
//			PP300PinPadController.d("displayReturnMsg", bcd2Str(retBuff));
			PP300PinPadController.dumpHex("displayReturnMsg", retBuff);
			String strRetBuff;
			strRetBuff = bcd2Str(retBuff);
			int nPos = -1;
			nPos = strRetBuff.indexOf("01");
			
			if (nPos != -1) {
				char cData1,cData2;
				cData1 = strRetBuff.charAt(10+nPos);
				cData2 = strRetBuff.charAt(11+nPos);
				if (type == 1 || type == 2) {
					if ((cData1 == '0' && cData2 == '0') || (cData1 == '0' && cData2 == '2')) {
						setViewEnabledTrue();
						PP300PinPadController.dumpHex("master key return ", retBuff);
						if (type == 1) {
							Toast.makeText(PP300PinPadControllerActivity.this,"Inject master key success!",Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(PP300PinPadControllerActivity.this,"Inject work key success!",Toast.LENGTH_SHORT).show();
						}
						
					}
					else {
						PP300PinPadController.dumpHex("master key return ", retBuff);
						Toast.makeText(PP300PinPadControllerActivity.this,"Inject master key fail!",Toast.LENGTH_SHORT).show();
					}
				}
				else if(type == 3){
					if ((cData1 == '0' && cData2 == '0') ) {
						PP300PinPadController.dumpHex("get pin return ", retBuff);
						Toast.makeText(PP300PinPadControllerActivity.this,"Get pin success!", Toast.LENGTH_SHORT).show();
						String str = PP300PinPadController.bcdToAsc(retBuff, retBuff.length);
						if (str.contains("0012") == true) {
							int start = 0;
							start = str.indexOf("0012", 0);
							String strPin;
							
							strPin = str.substring(start+6, start+6+8+8);
							tv_read.setText(strPin);
						}
						
					}
					else  if ((cData1 == '0' && cData2 == '2')){
						PP300PinPadController.dumpHex("get pin return ", retBuff);
						Toast.makeText(PP300PinPadControllerActivity.this,"Clear pin input!", Toast.LENGTH_SHORT).show();
					}else  if ((cData1 == '0' && cData2 == '3')){
						PP300PinPadController.dumpHex("get pin return ", retBuff);
						Toast.makeText(PP300PinPadControllerActivity.this,"Get pin timeout!", Toast.LENGTH_SHORT).show();
					}
					else {
						PP300PinPadController.dumpHex("get pin return ", retBuff);
						Toast.makeText(PP300PinPadControllerActivity.this,"get pin  fail!",Toast.LENGTH_SHORT).show();
					}
					
				}
				
			}
		}
		
		@Override
		public void ReturnMsg(String strMsg) {
			// TODO Auto-generated method stub
			Toast.makeText(PP300PinPadControllerActivity.this, strMsg, Toast.LENGTH_SHORT).show();
		}
	};
	
}
