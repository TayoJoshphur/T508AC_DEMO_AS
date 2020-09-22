package com.POS.apis.FingerprintController;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android_jb.com.POSD.controllers.ZAFingerprintController;
import jepower.com.t508ac_demo.R;

public class FingerPrintScanActivity extends Activity implements
        View.OnClickListener {

//    private Button btn_connect;
//    private Button btn_disconnect;
//    private Button btn_reset;
//    private Button btn_delete;
//    private Button btn_clear;
//    private Button btn_fingerprintrecord;
    private Button btn_fingerprintquery;
    private TextView tv_result;
    private ZAFingerprintController fingerprintRecognitionController = null;
    private static int[] storindex = new int[255];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print_scan);
        initfingerprintrecognition();
        initview();

        connect();
    }

    private void setBtnEnabledFalse() {
//        btn_connect.setEnabled(true);
//        btn_disconnect.setEnabled(false);
//        btn_delete.setEnabled(false);
//        btn_clear.setEnabled(false);
//        btn_reset.setEnabled(false);
//        btn_fingerprintrecord.setEnabled(false);
        btn_fingerprintquery.setVisibility(View.GONE);
    }

    private void setBtnEnabledTrue() {
//        btn_connect.setEnabled(false);
//        btn_disconnect.setEnabled(true);
//        btn_delete.setEnabled(true);
//        btn_reset.setEnabled(true);
//        btn_clear.setEnabled(true);
//        btn_fingerprintrecord.setEnabled(true);
        btn_fingerprintquery.setEnabled(true);
    }

    private void initview() {
//        btn_connect = (Button) findViewById(R.id.btn_connect);
//        btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
//        btn_reset = (Button) findViewById(R.id.btn_fingerreset);
//        btn_reset.setVisibility(View.GONE);
//        btn_delete = (Button) findViewById(R.id.btn_delete);
//        btn_clear = (Button) findViewById(R.id.btn_clear);
//        btn_fingerprintrecord = (Button) findViewById(R.id.btn_fingerprintrecord);
        btn_fingerprintquery = (Button) findViewById(R.id.btn_fingerprintquery);
//        btn_connect.setOnClickListener(this);
//        btn_disconnect.setOnClickListener(this);
//        btn_delete.setOnClickListener(this);
//        btn_clear.setOnClickListener(this);
//        btn_reset.setOnClickListener(this);
//        btn_fingerprintrecord.setOnClickListener(this);
        btn_fingerprintquery.setOnClickListener(this);
        tv_result = (TextView) findViewById(R.id.tv_result);
        // tv_information = (TextView) findViewById(R.id.tv_information);
        setBtnEnabledFalse();
    }

    private void initfingerprintrecognition() {
        fingerprintRecognitionController = ZAFingerprintController
                .getInstance();
    }

    ProgressDialog MyDialog = null;

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//		MyDialog = ProgressDialog.show(ZAFingerprintControllerActivity.this,
//				" ", " Loading. Please wait ... ", true);
//		MyDialog.show();
        fingerprintRecognitionController.FingerprintController_Close();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.btn_connect:
                connect();
                break;
//            case R.id.btn_disconnect:
//                disconnect();
//                break;
//            case R.id.btn_delete:
//                new Thread() {
//                    public void run() {
//                        delete();
//                    };
//                }.start();
//                break;
//            case R.id.btn_clear:
//                new Thread() {
//                    public void run() {
//                        clear();
//                    };
//                }.start();
//                break;
            case R.id.btn_fingerprintrecord:
                fingerprintrecord();
                break;
            case R.id.btn_fingerprintquery:
                fingerprintquery();
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String str = (String) msg.obj;
                    tv_result.setText(str);
                    break;
                case 2:
                    String strs = (String) msg.obj;
                    tv_result.setText(strs);
                    btn_fingerprintquery.setVisibility(View.VISIBLE);
                    break;
            }
        };
    };

    // 指纹识别
    private void fingerprintquery() {
        Message message = new Message();
        message.what = 1;
        message.obj = getResources().getString(
                R.string.scan_your_fingerprint_to_verify_it_is_you);
        handler.sendMessage(message);
        new Thread() {
            public void run() {
                int ret = -1;
                while (true) {
                    if (fingerprintRecognitionController
                            .FingerprintController_ProbeFinger() == 0) {
                        ret = 0;
                        break;
                    }
                }
                Message message = new Message();
                if (0 == ret) {
                    message = new Message();
                    message.what = 1;
                    message.obj = getResources().getString(
                            R.string.processing_you_fingerprint);
                    handler.sendMessage(message);
                    ret = fingerprintRecognitionController
                            .FingerprintController_FingerprintQuery();
                }
                if (ret == -1) {
                    message = new Message();
                    message.what = 2;
                    message.obj = getResources().getString(
                            R.string.fingerprintre_string_fingerprint_failed);
                    handler.sendMessage(message);
                } else {
                    sendMessage(getResources().getString(
                            R.string.fingerprintre_string_recognition_success));

                }
            }
        }.start();
    }

    // 指纹录入
    private void fingerprintrecord() {
        // TODO Auto-generated method stub
        Message message = new Message();
        message.what = 1;
        message.obj = getResources().getString(
                R.string.fingerprintre_string_finger);
        handler.sendMessage(message);
        new Thread() {
            public void run() {
                int ret = -1;
                while (true) {
                    if (fingerprintRecognitionController
                            .FingerprintController_ProbeFinger() == 0) {
                        ret = 0;
                        break;
                    }
                }

                ret = fingerprintRecognitionController
                        .FingerprintController_FingerprintQuery();
                System.out.println("rest ==== " + ret);
                Message message = new Message();
                if (ret == 0) {
                    message = new Message();
                    message.what = 1;
                    message.obj = getResources().getString(
                            R.string.fingerprintre_string_existed);
                    handler.sendMessage(message);
                    System.out.println("2342342");
                } else {
                    ret = -1;
                    while (true) {
                        if (fingerprintRecognitionController
                                .FingerprintController_ProbeFinger() == 0) {
                            ret = 0;
                            break;
                        }
                    }
                    if (0 == ret) {
                        message = new Message();
                        message.what = 1;
                        message.obj = getResources().getString(
                                R.string.fingerprintre_string_wait);
                        handler.sendMessage(message);
                        int index = 0;
                        for (int i = 0; i < storindex.length; i++) {
                            if (0 == storindex[i]) {
                                storindex[i] = i + 1;
                                index = (byte) (i + 1 - 1);
                                break;
                            }
                        }
                        ret = fingerprintRecognitionController
                                .FingerprintController_FingerprintRecord(index);
                    }
                    if (-2 == ret) {
                        message = new Message();
                        message.what = 1;
                        message.obj = getResources().getString(
                                R.string.fingerprintre_string_full);
                        handler.sendMessage(message);
                    } else if (-1 == ret) {
                        message = new Message();
                        message.what = 1;
                        message.obj = getResources().getString(
                                R.string.fingerprintre_string_input_failed);
                        handler.sendMessage(message);
                    } else {
                        sendMessage(getResources().getString(
                                R.string.fingerprintre_string_input_success));
                    }
                }

            }
        }.start();

    }

    private void sendMessage(String str) {
        Message message = new Message();
        message.what = 1;
        message.obj = str;
        handler.sendMessage(message);
    }

//    private void clear() {
//        // TODO Auto-generated method stub
//        if (fingerprintRecognitionController
//                .FingerprintController_FingerprintClear() == 0) {
//            Message message = new Message();
//            message.what = 1;
//            message.obj = getResources().getString(
//                    R.string.fingerprintre_string_empty_success);
//            handler.sendMessage(message);
//        } else {
//            Message message = new Message();
//            message.what = 1;
//            message.obj = getResources().getString(
//                    R.string.fingerprintre_string_empty_failed);
//            handler.sendMessage(message);
//        }
//    }

    private void delete() {
        // TODO Auto-generated method stub
        Message message = new Message();
        message.what = 1;
        message.obj = getResources().getString(
                R.string.fingerprintre_string_finger);
        handler.sendMessage(message);
        new Thread() {
            @SuppressWarnings("unused")
            public void run() {
                int ret = -1;
                while (true) {
                    if (fingerprintRecognitionController
                            .FingerprintController_ProbeFinger() == 0) {
                        ret = 0;
                        break;
                    }
                }
                Message message = new Message();
                int n = -2;
                if (0 == ret) {
                    message = new Message();
                    message.what = 1;
                    message.obj = getResources().getString(
                            R.string.fingerprintre_string_wait);
                    handler.sendMessage(message);
                }
                if (fingerprintRecognitionController
                        .FingerprintController_FingerprintDelete() == 0) {
                    message = new Message();
                    message.what = 1;
                    message.obj = getResources().getString(
                            R.string.fingerprintre_string_deleted_successfully);
                    handler.sendMessage(message);
                } else {
                    message = new Message();
                    message.what = 1;
                    message.obj = getResources().getString(
                            R.string.fingerprintre_string_deletion_failed);
                    handler.sendMessage(message);
                }
            };
        }.start();
    }

    private void disconnect() {
        // TODO Auto-generated method stub
        if (fingerprintRecognitionController.FingerprintController_Close() == 0) {
            setBtnEnabledFalse();
            tv_result.setText("");
            Toast.makeText(this, "disconnect_success", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, "disconnect_failure", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void connect() {
        // TODO Auto-generated method stub
        if (fingerprintRecognitionController.FingerprintController_Open(this) == 0) {
//            setBtnEnabledTrue();
//            fingerprintrecord();
            fingerprintquery();
            Toast.makeText(this, "connect_success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "connect_failure", Toast.LENGTH_SHORT).show();
        }
    }
}