
package android_jb.com.POSD.controllers;

import android.os.Handler;
import android.os.Message;

import android_jb.com.POSD.util.LogUtil;
import android_serialport_api.SerialPort;

import com.synjones.Helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <功能描述>
 * 
 * @author Administrator
 */
public class VanstoneV10PinPadController {
    private static final String TAG = VanstoneV10PinPadController.class
            .getSimpleName();
    private static final String FILE_PATH = "/dev/ttyS2";
    private static final String IO_CS0 = "/proc/jbcommon/gpio_control/UART2_SEL0";// 默认值：1，其他值无效
    private static final String IO_CS1 = "/proc/jbcommon/gpio_control/UART2_SEL1";// 默认值：1，其他值无效
    private static final String POWER_MODULE = "/proc/jbcommon/gpio_control/RJ11_CTL";// 默认值：1，其他值无效

    private static VanstoneV10PinPadController pinPadController = null;
    private SerialPort mSerialPort = null;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    private ReadThread mReadThread = null;
    private boolean mReadThreadBegin = false;
    private boolean mIsReadThreadRunning = false;
    private PassCallBack mCallBack;

    public interface PassCallBack {
        public void returnMsg(int type, String retBuff);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mCallBack.returnMsg(msg.what, (String) msg.obj);
        }
    };

    private class ReadThread extends Thread {

        @Override
        public void run() {
            LogUtil.trace();
            super.run();

            int count = 0;
            while (mReadThreadBegin) {
                // 判断何时读取有效数据
                if (mIsReadThreadRunning) {
                    if (mInputStream != null) {
                        try {
                            count = mInputStream.available();

                            if (count > 0) {
                                byte[] buffer = new byte[count];
                                mInputStream.read(buffer);

                                dumpHex("ReadThread read:", buffer);
                                // TODO 什么含义？
                                String str = bcdToAsc(buffer, count);
                                LogUtil.trace("str:" + str);

                                if (!str.endsWith("03")) {
                                    // 表示反馈还未结束，继续读数据
                                    do {
                                        count = mInputStream.available();
                                        if (count != 0) {
                                            byte[] other = new byte[count];
                                            mInputStream.read(other);

                                            dumpHex("ReadThread read:", other);
                                            String otherString = bcdToAsc(
                                                    other, count);
                                            LogUtil.trace("otherStr:"
                                                    + otherString);

                                            str += otherString;

                                            LogUtil.d(TAG, "all string::" + str);
                                        }
                                    } while (count != 0);
                                }

                                // 获取到全部反馈数据后，和对应的控制指令匹配
                                if (str.contains("001600") == true) {
                                    // command 16 获取明文密码
                                    LogUtil.d(TAG, "<--- Command 16 --->");
                                    // mIsReadThreadRunning = false;

                                    String pwd = separateResultPwd(16, str);
                                    LogUtil.d(TAG, "pwd:" + pwd);

                                    if (pwd != null) {
                                        Message msg = new Message();
                                        msg.obj = pwd;
                                        msg.what = 0x16;
                                        mHandler.sendMessage(msg);

                                        LogUtil.trace("send message:" + msg
                                                + "; buffer:"
                                                + Helper.ByteArrToHex(buffer));
                                    }
                                } else if (str.contains("17") == true) {
                                    LogUtil.d(TAG, "<--- Command 17 --->");
                                } else if (str.contains("18") == true) {
                                    LogUtil.d(TAG, "<--- Command 18 --->");
                                } else if (str.contains("001500") == true) {
                                    // 获取密钥密码
                                    LogUtil.d(TAG, "<--- Command 15 --->");

                                    // 02/0014/0015/00（成功）/38B6B5698E15EB4C0000000000000000/6F/03
                                    String encryptPwd = separateResultPwd(15,
                                            str);
                                    LogUtil.d(TAG, "encryptPwd:" + encryptPwd);

                                    if (encryptPwd != null) {
                                        Message msg = new Message();
                                        msg.obj = encryptPwd;
                                        msg.what = 0x15;
                                        mHandler.sendMessage(msg);

                                        LogUtil.trace("send message:" + msg
                                                + "; buffer:"
                                                + Helper.ByteArrToHex(buffer));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } // end --> if (mInputStream != null)
                } // end --> if (mReadThreadRead)
            } // end --> while (begin)
        }
    }

    public static VanstoneV10PinPadController getInstance() {
        if (null == pinPadController) {
            pinPadController = new VanstoneV10PinPadController();
        }
        return pinPadController;
    }

    public void setReadThreadRunning() {
        this.mIsReadThreadRunning = true;
    }

    public void setReadThreadStop() {
        this.mReadThreadBegin = false;
    }

    public void setCallback(PassCallBack callback) {
        this.mCallBack = callback;
    }

    /**
     * <功能描述> 模块上电
     * 
     * @return void [返回类型说明]
     */
    public void setModulePowerUp() {
        writeFile(new File(IO_CS0), "1");
        writeFile(new File(IO_CS1), "1");
        writeFile(new File(POWER_MODULE), "1");
    }

    /**
     * <功能描述> 模块下电
     * 
     * @return void [返回类型说明]
     */
    public void setModulePowerOff() {
        writeFile(new File(IO_CS0), "0");
        writeFile(new File(IO_CS1), "0");
        writeFile(new File(POWER_MODULE), "0");

        if (mOutputStream != null || null != mInputStream) {
            try {
                mOutputStream.close();
                mInputStream.close();
                mInputStream = null;
                mOutputStream = null;
                LogUtil.d(TAG,
                        "setModulePowerOff:: close input/output stream success.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    /**
     * <功能描述> 获取通讯串口实例
     * 
     * @return void [返回类型说明]
     */
    public boolean getStreamPort() {
        // 转换：1路转4路
        // Ioctl.convertRJ11();

        try {
            if (mSerialPort == null) {
                mSerialPort = new SerialPort(new File(FILE_PATH), 38400, 8,
                        '0', 1, 0);
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();

                // 获取串口实例，得到InputStream实例即可开始读线程
                mReadThread = new ReadThread();
                mReadThreadBegin = true;
                mReadThread.start();
            }

            LogUtil.d(TAG, "getStreamPort::return serial port success...");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * <功能描述> 发送控制指令
     * 
     * @param cmd
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public boolean sendCmdString(byte[] cmd) {
        // byte[] 转化为16进制字符串：02 00 03 00 16 15 03
        LogUtil.d(TAG, "Send Command data:" + Helper.ByteArrToHex(cmd));
        try {
            mOutputStream.write(cmd);
            mOutputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isOutputStreamNull() {
        if (null == mOutputStream) {
            return true;
        }
        return false;
    }

    /**
     * <功能描述> 解析出密码 // 02/000A/0016/00313233343536/1B03 1~10位密码均支持 //
     * 020005001600/31/2203 12
     * 
     * @param value
     * @return [参数说明]
     * @return String [返回类型说明]
     */
    private String separateResultPwd(int type, String value) {
        int length = value.length();
        LogUtil.d(TAG, "separateResultPwd::length=" + length);

        int indexValueLast = length - 5;
        LogUtil.d(TAG, "separateResultPwd::indexValueLast=" + indexValueLast);

        int indexValueStart = 0;
        if (16 == type) {
            indexValueStart = value.indexOf("001600") + 6;
        } else if (15 == type) {
            indexValueStart = value.indexOf("001500") + 6;
        }
        LogUtil.d(TAG, "separateResultPwd::indexValueStart=" + indexValueStart);

        if (indexValueLast <= indexValueStart) {
            LogUtil.d(TAG, "separateResultPwd pwd error...");
            return null;
        }
        String pwd = value.substring(indexValueStart, indexValueLast + 1);
        LogUtil.d(TAG, "separateResultPwd::number:" + pwd);
        return pwd;
    }

    public static void dumpHex(String msg, byte[] bytes) {
        int length = bytes.length;
        msg = (msg == null) ? "" : msg;
        LogUtil.trace("-------------------------- " + msg
                + "(len:%d) --------------------------" + "; length:" + length);
        for (int i = 0; i < bytes.length; i++) {
            if (i % 16 == 0) {
                if (i != 0)
                    System.out.println();
                System.out.printf("0x%08X    ", i);
            }
            System.out.printf("%02X ", bytes[i]);
        }
        System.out.println("");
    }

    public static String bcdToAsc(byte[] inArray, int iInArrayLength) {
        byte[] outArray = new byte[iInArrayLength * 2];
        int j = 0;
        for (int i = 0; i < iInArrayLength; i++) {
            outArray[j] = hexToAscii((inArray[i] & 0xf0) >> 4);
            j++;
            outArray[j] = hexToAscii(inArray[i] & 0x0f);
            j++;
        }
        return new String(outArray);
    }

    public static byte hexToAscii(int iNum) {
        if (iNum >= 0 && iNum <= 9) {
            return (byte) (iNum + 0x30);
        } else {
            return ((byte) (0x40 + (byte) (iNum - 9)));
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
            LogUtil.d(TAG, "writeFile ERROR...");
            e1.printStackTrace();
        }
    }

}
