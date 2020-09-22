package com.POS.apis.IDcardController;
/**
 * 串口读二代证demo
 */
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android_jb.com.POSD.controllers.IDcardController;
import android_serialport_api.SerialPort;
import jepower.com.t508ac_demo.R;

import com.synjones.idcard.IDCard;
import com.synjones.idcard.IDcardReader;
import com.synjones.multireaderlib.MultiReader;


public class IDcardControllerActivity extends Activity {
    /** Called when the activity is first created. */
	private Context mContext;
	
	Button buttonReadCard;
	Button BtnReadOnce;
	Button buttonExit;
	OnClickListener listenerReadCard = null;
	OnClickListener listenerExit = null;
	private IDCard idcard = null;
	private Bitmap bmp;

	private TextView tvTime;
	private long StartTime;
	private boolean reading=false;
	private long ReadCount;
	private long SuccessCount;
	private long FailCount;
	private TextView tvCount;
	private TextView tvSuccessCount;
	private TextView tvFailCount;
	private long eclipseTime;
	private static final int ReadOnceDone=0x01;
	private static Handler mHandler = null;
	private ReadCardThread ReadCardThreadhandler;
	private boolean ShowFinish=false;
	
	private TextView tvSoftVersion;
	private IDcardController controller;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        int port = 1;
        if(intent.hasExtra("PROT")){
        	port = intent.getIntExtra("PROT", 1);
        }
        mContext = this;
        controller = IDcardController.getInstance();
        controller.IDcardController_Open(this , port);
		setContentView(R.layout.readid);

		tvCount=(TextView) findViewById(R.id.tvCount);
        tvSuccessCount=(TextView) findViewById(R.id.tvSuccessCount);
        tvFailCount=(TextView) findViewById(R.id.tvFailCount);

        listenerReadCard = new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(reading==false)
				{					
					ReadCount=0;
			        SuccessCount=0;
			        FailCount=0;
			        StartTime=0;
					reading=true;
					ShowFinish=false;
					buttonReadCard.setText(getResources().getString(R.string.idcard_controller_btn_stop));
					ReadCardThreadhandler=new ReadCardThread();
					ReadCardThreadhandler.start();
					
				}
				else
				{
					if(ReadCardThreadhandler!=null)
					{
						ReadCardThreadhandler.stopRead();
						ReadCardThreadhandler=null;
					}
					buttonReadCard.setText(getResources().getString(R.string.idcard_controller_btn_readcard_2));
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}			
			
			}
        };
        
        
        mHandler = new Handler() {
     			@Override
     			public void handleMessage(Message msg) {
     				switch (msg.what) {
     				case ReadOnceDone:
     					ShowFinish=true;
     					long time=System.currentTimeMillis();
     					showIDcardInfo();
     					break;										
     				}
     				super.handleMessage(msg);
     			}
             };
        
      
        buttonReadCard = (Button) findViewById(R.id.buttonReadCard);
        buttonReadCard.setOnClickListener(listenerReadCard);
        
        
        BtnReadOnce=(Button) findViewById(R.id.btnReadOnce);
        BtnReadOnce.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				StartTime=System.currentTimeMillis();
				idcard = controller.IDcardController_Read();
				showIDcardInfo();

			}
		});
       
        
        tvTime=(TextView)findViewById(R.id.tvTime);
        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(ReadCardThreadhandler!=null)
				{
					ReadCardThreadhandler.stopRead();
					ReadCardThreadhandler=null;
				}

				if(controller!=null)
		    	{
		    	//	reader.EnterSavePowerMode();
		    		controller.IDcardController_Close();		    		
		    		controller=null;		    		
		    	}
				IDcardControllerActivity.this.finish();
			}
		});
        
    }//onCreat
    private String TAG = "PL2303HXD_APLog ";   
	public void onResume() {
	        super.onResume();
	       

			//ShowVersion();
			reading=false;
	        ReadCount=0;
	        SuccessCount=0;
	        FailCount=0;
	        StartTime=0;
	        buttonReadCard.setText(getResources().getString(R.string.idcard_controller_btn_readcard_2));
	    } 
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0) {
            //do something...
    		if(ReadCardThreadhandler!=null)
    		{
    			ReadCardThreadhandler.stopRead();
    			ReadCardThreadhandler=null;
    		}
            finish();
         }
         return super.onKeyDown(keyCode, event);
     }
	

    @Override
    protected void onDestroy() {

		if(ReadCardThreadhandler!=null)
		{
			ReadCardThreadhandler.stopRead();
			ReadCardThreadhandler=null;
		}
    	if(controller!=null)
    	{
    		//reader.EnterSavePowerMode();
    		controller.IDcardController_Close(); 
    		controller=null;   		
    	}
    	super.onDestroy();
    }
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("IDcardControllerActivity","on pause");

		if(ReadCardThreadhandler!=null)
		{
			ReadCardThreadhandler.stopRead();
			ReadCardThreadhandler=null;
		}
    }
    
    
    public void showIDcardInfo(){
    	TextView tv;
		ReadCount++;
		Log.v("IDcardControllerActivity","showIDcardInfo idcard: "+idcard);
		if (idcard != null) {
			SuccessCount++;
			tv = (TextView) findViewById(R.id.textViewName);
			tv.setText(getString(R.string.idcard_controller_tv_name) + idcard.getName());
			tv = (TextView) findViewById(R.id.textViewSex);
			tv.setText(getString(R.string.idcard_controller_tv_sex) + idcard.getSex());
			tv = (TextView) findViewById(R.id.textViewNation);
			tv.setText(getString(R.string.idcard_controller_tv_ethnic) + idcard.getNation());
			tv = (TextView) findViewById(R.id.textViewBirthday);
			tv.setText(getString(R.string.idcard_controller_tv_birth) 
					+ idcard.getBirthday().substring(0,4)	+ "年" 
					+ idcard.getBirthday().substring(4, 6)	+ "月"
					+ idcard.getBirthday().substring(6,8)	+ "日");
			tv = (TextView) findViewById(R.id.textViewAddress);
			tv.setText(getString(R.string.idcard_controller_tv_address) + idcard.getAddress());
			tv = (TextView) findViewById(R.id.textViewPIDNo);
			tv.setText(getString(R.string.idcard_controller_tv_idnumber) + idcard.getIDCardNo());
			tv = (TextView) findViewById(R.id.textViewGrantDept);
			tv.setText(getString(R.string.idcard_controller_tv_authority) + idcard.getGrantDept());
			tv = (TextView) findViewById(R.id.textViewUserLife);
			tv.setText(getString(R.string.idcard_controller_tv_period) + idcard.getUserLifeBegin() + "-" + idcard.getUserLifeEnd());
			tv = (TextView) findViewById(R.id.textViewStatus);
			tv.setText(getString(R.string.sdtstatus));

			Log.e("fp", "指位信息:"+idcard.getFpName());
			ImageView imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);

			try {
				bmp =idcard.getPhoto();
				if (bmp!=null) {									
					imageViewPhoto.setImageBitmap(bmp);													
				} else {						
					Resources res = getResources();
					bmp = BitmapFactory.decodeResource(res, R.drawable.face);
					imageViewPhoto.setImageBitmap(bmp);
					Log.d("IDcardControllerActivity","状态：照片解码错");
					// imageViewPhoto.setImageResource(R.drawable.photo);
					//tvMessage.setText("状态：照片解码错,错误号:" + result);
				}
				Log.d("IDcardControllerActivity","decode wlt finish");
				System.gc();

			} catch (Exception ioe) {
				ioe.printStackTrace();
				Log.d("IDcardControllerActivity","photo display error:" + ioe.getMessage());
				//tvMessage.setText("状态：照片显示错" + ioe.getMessage());
			}
		} else {
			FailCount++;
			tv = (TextView) findViewById(R.id.textViewName);
			tv.setText(getString(R.string.idcard_controller_tv_name));
			tv = (TextView) findViewById(R.id.textViewSex);
			tv.setText(getString(R.string.idcard_controller_tv_sex));
			tv = (TextView) findViewById(R.id.textViewNation);
			tv.setText(getString(R.string.idcard_controller_tv_ethnic));
			tv = (TextView) findViewById(R.id.textViewBirthday);
			tv.setText(getString(R.string.idcard_controller_tv_birth));
			tv = (TextView) findViewById(R.id.textViewAddress);
			tv.setText(getString(R.string.idcard_controller_tv_address));
			tv = (TextView) findViewById(R.id.textViewPIDNo);
			tv.setText(getString(R.string.idcard_controller_tv_idnumber));
			tv = (TextView) findViewById(R.id.textViewGrantDept);
			tv.setText(getString(R.string.idcard_controller_tv_authority));
			tv = (TextView) findViewById(R.id.textViewUserLife);
			tv.setText(getString(R.string.idcard_controller_tv_period));
			tv = (TextView) findViewById(R.id.textViewStatus);
			tv.setText(getString(R.string.sdtstatus) + " " + Integer.toHexString(IDCard.SW1) + " " + Integer.toHexString(IDCard.SW2) + " " + Integer.toHexString(IDCard.SW3));
			ImageView imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
			imageViewPhoto.setImageResource(R.drawable.face);
		}
		
		tvCount.setText(getString(R.string.idcard_count)+ReadCount);
		tvFailCount.setText(getString(R.string.idcard_count_failed)+FailCount);
		tvSuccessCount.setText(getString(R.string.idcard_count_success)+SuccessCount);
		eclipseTime=(System.currentTimeMillis()-StartTime)/1000;
		tvTime.setText("time："+eclipseTime);
    }
    
    public static String bytesToHexString(byte[] src){
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	       // stringBuilder.append("0x");
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        
	        stringBuilder.append(hv.toUpperCase());  
	      //  stringBuilder.append(",");
	    }  
	    return stringBuilder.toString();  
	}
    
    class ReadCardThread extends Thread{
    	public void run() {
    		Looper.prepare();
	    	//Message m= new Message();
	    	StartTime=System.currentTimeMillis();
	    	long oldtime=0;
	    	while(reading)
			{			
				oldtime=System.currentTimeMillis();
				long n_time=oldtime;
				idcard = controller.IDcardController_Read();
				Log.d("IdCard","ReadCardThread working");
				
				Message m= new Message();
				
				m.what = ReadOnceDone;
				mHandler.sendMessage(m);
				
				if(idcard==null) continue;
				ShowFinish=false;
				while(ShowFinish==false && System.currentTimeMillis()-oldtime<2000 );
/*				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}//while

    	}//run
    	
    	public void stopRead(){
    		reading=false;
    		ShowFinish=true;
    		try {
				join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }//class
    
}