
package com.POS.apis.PINPadController;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import android_jb.com.POSD.controllers.VanstoneV10PinPadController;
import android_jb.com.POSD.util.LogUtil;
import jepower.com.t508ac_demo.R;

/**
 * <功能描述> 使用RJ11接口，需要手动上电使用密码键盘
 * 
 * @author Administrator
 */
public class VanstoneV10Activity extends Activity implements OnClickListener {
    private static final String TAG = VanstoneV10Activity.class.getSimpleName();

    private Button mBtnPowerUp;
    private Button mBtnGetControlStream;
    private Button mBtnDisplayString;
    private Button mBtnClearContent;

    private Button mBtnGetPinPwd;
    private TextView mTvPinPwd;

    private Button mBtnGetEncryptPwd;
    private TextView mTvEncryptPwd;

    private Button mBtnPowerOff;

    private VanstoneV10PinPadController mPinPadController;
    private boolean isWriteKey = false;

    // command 14 显示指定字符串；无反馈
    private static final byte[] DISPLAY_STRING = new byte[] {
            0x02, 0x00, 0x0c, 0x00, 0x14, 0x00, 0x00, 0x04, 0x00, 0x04, 0x31,
            0x32, 0x33, 0x34, 0x1C, 0x03
    };

    // command 09 清屏；无反馈
    private static final byte[] CLEAR_DISPLAY = new byte[] {
            0x02, 0x00, 0x03, 0x00, 0x09, 0x0A, 0x03
    };

    // command 16 明文获取密码；反馈操作结果：02/0005/0016/00（成功）31（密码）/22/03 >=8个字节
    private static final byte[] PLAIN_PWD = new byte[] {
            0x02, 0x00, 0x03, 0x00, 0x16, 0x15, 0x03
    };

    // command 17 计算得到的XOR值为：0xF2；反馈操作结果：02/0004/0017/00/1303（表示成功）
    private static final byte[] MASTER_KEY = new byte[] {
            0x02, 0x00, 0x0D, 0x00, 0x17, 0x01, 0x01, 0x12, 0x34, 0x56, 0x78,
            (byte) 0x90, 0x12, 0x34, 0x56, (byte) 0xF2, 0x03
    };

    private static final byte[] CMD_WRITE_MASTER_KEY = new byte[] {
            0x00, 0x17
    };

    // 对应的是command 17 写入Master 密钥
    private static final byte[] MASTER_KEY_PARAMETERS = new byte[] {
            0x01, 0x01, 0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34, 0x56
    };

    // 对应的是command 18；反馈操作结果：02/0004/0018/07/1B03 异常；02/0004/0018/001C/03 正常
    private static final byte[] PINKEY = new byte[] {
            0x02, 0x00, 0x16, 0x00, 0x18, 0x01, 0x02, (byte) 0x01, 0x11, 0x11,
            0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x22, 0x22, 0x22, 0x22, 0x22,
            0x22, 0x22, 0x22, (byte) 0x0C, 0x03
    };
    // command 18
    private static final byte[] CMD_PINKEY = new byte[] {
            0x00, 0x18
    };

    // command 53 设置输入密码前缀
    private static final byte[] DISPLAY_INPUT_PWD = new byte[] {
            0x02, 0x00, 0x21, 0x00, 0x53, 0x03, 0x00, 0x04, 0x06, 0x19, 0x01,
            0x01, 0x20, 0x02, 0x01, 0x01, 0x03, 0x11, 0x01, 0x00, 0x50, 0x6C,
            0x73, 0x20, 0x69, 0x6E, 0x70, 0x75, 0x74, 0x20, 0x70, 0x77, 0x64,
            0x3A, 0x04, 0x3F, 0x03
    };

    // command 15
    // 获取密文密码；反馈：02/0014/0015/00（成功）/38B6B5698E15EB4C00000000000000006F/03
    private static final byte[] ENCRYPT_PIN_PWD = new byte[] {
            0x02, 0x00, 0x19, 0x00, 0x15, 0x02, 0x00, 0x09, 0x01, 0x00, 0x10,
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31,
            0x32, 0x33, 0x34, 0x35, 0x36, 0x10, 0x03
    };

    // command 15 获取密文密码
    private static final byte[] ENCRYPT_PIN_PWD_PARA = new byte[] {
            0x02, 0x00, 0x09, 0x01, 0x00, 0x10, 0x31, 0x32, 0x33, 0x34, 0x35,
            0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36
    };

    // command 15
    private static final byte[] CMD_ENCRYPT_PIN_PWD = new byte[] {
            0x00, 0x15
    };

    // command 1 power off
    private static final byte[] POWER_OFF = new byte[] {
            0x02, 0x00, 0x03, 0x00, 0x01, 0x02, 0x03
    };

    private VanstoneV10PinPadController.PassCallBack mPassCallBack = new VanstoneV10PinPadController.PassCallBack() {

        @Override
        public void returnMsg(int type, String retBuff) {
            if (type == 0x16) {
                mTvPinPwd.setText(resolveVales(retBuff));
            } else if (type == 0x15) {
                mTvEncryptPwd.setText("" + retBuff);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vanstone_v10_pinpad_activity);
        initView();
        initListener();

        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPinPadController.setModulePowerOff();
        mPinPadController.setReadThreadStop();
    }

    private void initData() {
        mPinPadController = VanstoneV10PinPadController.getInstance();
    }

    private void initListener() {
        mBtnPowerUp.setOnClickListener(this);
        mBtnGetControlStream.setOnClickListener(this);

        mBtnDisplayString.setOnClickListener(this);
        mBtnClearContent.setOnClickListener(this);

        mBtnGetPinPwd.setOnClickListener(this);

        mBtnGetEncryptPwd.setOnClickListener(this);

        mBtnPowerOff.setOnClickListener(this);
    }

    private void initView() {
        mBtnPowerUp = (Button) VanstoneV10Activity.this
                .findViewById(R.id.btn_power_up);
        mBtnGetControlStream = (Button) VanstoneV10Activity.this
                .findViewById(R.id.btn_get_serial_port);
        mBtnClearContent = (Button) VanstoneV10Activity.this
                .findViewById(R.id.btn_clear);
        mBtnDisplayString = (Button) VanstoneV10Activity.this
                .findViewById(R.id.btn_display_string);

        mBtnGetPinPwd = (Button) VanstoneV10Activity.this
                .findViewById(R.id.btn_get_pin_pwd);
        mTvPinPwd = (TextView) VanstoneV10Activity.this
                .findViewById(R.id.tv_pin_pwd);

        mBtnGetEncryptPwd = (Button) VanstoneV10Activity.this
                .findViewById(R.id.btn_get_encrypt_pwd);
        mTvEncryptPwd = (TextView) VanstoneV10Activity.this
                .findViewById(R.id.tv_encrypt_pin);

        mBtnPowerOff = (Button) VanstoneV10Activity.this
                .findViewById(R.id.btn_power_off);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_power_up:
                mPinPadController.setModulePowerUp();
                break;

            case R.id.btn_get_serial_port:
                // 密码键盘和设备连接，即可上电使用，获取通讯串口实例
                boolean retGetStreamPort = mPinPadController.getStreamPort();
                LogUtil.d(TAG, "retGetStreamPort::" + retGetStreamPort);
                mPinPadController.setCallback(mPassCallBack);
                break;

            case R.id.btn_display_string:
                if (mPinPadController.isOutputStreamNull()) {
                    Toast.makeText(VanstoneV10Activity.this,
                            "未获取到通信通道，请点击连接按钮...", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 在密码键盘上显示指定内容：1234
                boolean retDisplayString = mPinPadController
                        .sendCmdString(DISPLAY_STRING);
                LogUtil.d(TAG, "retDisplayString::" + retDisplayString);
                break;

            case R.id.btn_clear:
                if (mPinPadController.isOutputStreamNull()) {
                    Toast.makeText(VanstoneV10Activity.this,
                            "未获取到通信通道，请点击连接按钮...", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 清屏
                mPinPadController.sendCmdString(CLEAR_DISPLAY);
                break;

            case R.id.btn_get_pin_pwd:
                if (mPinPadController.isOutputStreamNull()) {
                    Toast.makeText(VanstoneV10Activity.this,
                            "未获取到通信通道，请点击连接按钮...", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 在密码键盘上键入密码 明文密钥
                mPinPadController.sendCmdString(PLAIN_PWD);
                // 设置键入密码前缀：Pls input pwd:
                mPinPadController.setReadThreadRunning();
                break;

            case R.id.btn_get_encrypt_pwd:
                if (mPinPadController.isOutputStreamNull()) {
                    Toast.makeText(VanstoneV10Activity.this,
                            "未获取到通信通道，请点击连接按钮...", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 启动读线程
                mPinPadController.setReadThreadRunning();

                if (!isWriteKey) {
                    // 写入 Master 密钥
                    mPinPadController.sendCmdString(MASTER_KEY);

                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    // 写入 Pinkey 密钥
                    mPinPadController.sendCmdString(PINKEY);

                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    isWriteKey = true;
                }

                // 写入前缀
                mPinPadController.sendCmdString(DISPLAY_INPUT_PWD);

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mPinPadController.sendCmdString(ENCRYPT_PIN_PWD);
                    }
                }).start();
                break;

            case R.id.btn_power_off:
                if (mPinPadController.isOutputStreamNull()) {
                    Toast.makeText(VanstoneV10Activity.this,
                            "未获取到通信通道，请点击连接按钮...", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPinPadController.sendCmdString(POWER_OFF);
                mPinPadController.setModulePowerOff();
                mPinPadController.setReadThreadStop();
                break;

            default:
                break;
        }

    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    /**
     * <功能描述> 计算控制指令中的 XOR 值
     * 
     * @return [参数说明]
     * @return string [返回类型说明] 十六进制的字符串值
     */
    private String calculateXor(byte[] cmd, byte[] parameters) {
        int length = calculatorCmdLength(parameters);
        byte xorResult = 0x00;

        xorResult ^= length;

        for (int index = 0; index < cmd.length; index++) {
            xorResult ^= cmd[index];
        }

        for (int index = 0; index < parameters.length; index++) {
            xorResult ^= parameters[index];
        }

        return Integer.toHexString(xorResult);
    }

    /**
     * <功能描述> 计算控制指令的长度，包含：CMD（2个字节），参数，XOR（异或值）
     * 
     * @return [参数说明] The value is from CMD to XOR(include XOR). (take the value
     *         as field_len)
     * @return int [返回类型说明]
     */
    private int calculatorCmdLength(byte[] parameters) {
        return parameters.length + 2 + 1;
    }

    private String resolveVales(String content) {
        String result = "";
        int value = 0;
        if (content.length() <= 2) {
            value = Integer.parseInt(content) - 30;
            LogUtil.d(TAG, "resolveVales::value=" + value);
            return result + value;
        }

        ArrayList<String> list = new ArrayList<String>();
        int i = 0;
        // 313233343536
        for (i = 0; i < content.length() - 1; i += 2) {
            list.add(content.substring(i, i + 2));
        }

        for (i = 0; i < list.size(); i++) {
            LogUtil.d(TAG, "resolveVales::list.get(i)=" + list.get(i));
            value = Integer.parseInt(list.get(i)) - 30;
            result += value;
        }

        return result;
    }

}
