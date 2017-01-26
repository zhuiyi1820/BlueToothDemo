package com.lhj.classic.bluetooth.cls.chat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * ┴┬┴┬／￣＼＿／￣＼
 * ┬┴┬┴▏　　▏▔▔▔▔＼ 这
 * ┴┬┴／＼　／　　　　　　﹨ 地
 * ┬┴∕　　　　　　　／　　　） 方
 * ┴┬▏　　　　　　　　●　　▏ 不
 * ┬┴▏　　　　　　　　　　　▔█◤ 错，
 * ┴◢██◣　　　　　　 ＼＿＿／
 * ┬█████◣　　　　　　　／　 让我用PP踩踩！
 * ┴█████████████◣
 * ◢██████████████▆▄
 * █◤◢██◣◥█████████◤＼
 * ◥◢████　████████◤　　 ＼
 * ┴█████　██████◤　　　　　 ﹨
 * ┬│　　　│█████◤　　　　　　　　▏
 * ┴│　　　│ PP熊在此！借贵宝地一踩！ 　▏
 * ┬∕　　　∕　　　　／▔▔▔＼　　　　 ∕
 * *∕＿＿_／﹨　　　∕　　　　　 ＼　　／＼
 * ┴┬┴┬┴┬┴ ＼＿＿＿＼　　　　 ﹨／▔＼﹨／▔＼ ╃天天开心╃
 * ▲△▲▲╓╥╥╥╥╥╥╥╥＼　　 ∕　 ／▔﹨　／▔
 * 　＊＊＊╠╬╬╬╬╬╬╬╬＊﹨　　／　　／／ ╃事事顺心╃整和不错
 * <p/>
 * 作者：linhongjie
 * 时间：2017/1/9 15:16
 * 描述：蓝牙聊天通信控制
 */
public class ChatConnectControl {
    public static String TAG = "ChatConnectControl";
    /**
     * 蓝牙会话监听
     */
    private BluetoothChatListener mBluetoothChatListener = null;

    /**
     * 蓝牙默认的适配器
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * 蓝牙聊天服务
     */
    private ChatService mChatService = null;

    private String mDeviceName = null;
    private String mDeviceAddress = null;

    /**
     * 是否已连接
     */
    private boolean isConnected = false;
    /**
     * 是否福连接中
     */
    private boolean isConnecting = false;

    private Context mContext;

    public ChatConnectControl(Context context, BluetoothAdapter mBluetoothAdapter) {
        mContext = context;
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    /**
     * 服务是否启动
     *
     * @return
     */
    public boolean isServiceAvailable() {
        return mChatService != null;
    }

    /**
     * 初始化蓝牙聊天服务
     */
    public void setupService() {
        mChatService = new ChatService(mContext, mHandler, mBluetoothAdapter);
    }

    /**
     * 蓝牙聊天当前连接状态
     *
     * @return
     */
    public int getServiceState() {
        if (mChatService != null)
            return mChatService.getState();
        else
            return -1;
    }

    /**
     * 启动
     */
    public void startService() {
        if (mChatService != null) {
            if (mChatService.getState() == ChatState.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    /**
     * 停止
     */
    public void stopService() {
        if (mChatService != null) {
            mChatService.stop();
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (mChatService != null) {
                    mChatService.stop();
                }
            }
        }, 500);
    }

    /**
     * 重启/切换
     */
    public void setDeviceTarget() {
        stopService();
        startService();
    }

    /**
     * 连接
     *
     * @param address
     */
    public void connect(String address) {
        Log.e(TAG, "connect address=" + address);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device);
    }

    /**
     * 断开
     */
    public void disconnect() {
        Log.i(TAG, "disconnect");
        if (mChatService != null) {
            mChatService.stop();
            if (mChatService.getState() == ChatState.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    /**
     * 发送
     *
     * @param data
     */
    public void send(String data) {
        if (mChatService.getState() == ChatState.STATE_CONNECTED) {
            Log.i(TAG, "send data.getBytes()=" + data.getBytes());
            mChatService.write(data.getBytes());
        }
    }


    /**
     * 配置监听
     *
     * @param listener
     */
    public void setBluetoothChatListener(BluetoothChatListener listener) {
        mBluetoothChatListener = listener;
    }

    /**
     * 处理Handler接收的内容
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ChatState.MESSAGE_WRITE:
                    byte[] readBuf_write = (byte[]) msg.obj;
                    Log.i(TAG, "MESSAGE_READ readBuf_write=" + readBuf_write);
                    Log.i(TAG, "MESSAGE_READ readBuf_write.length=" + readBuf_write.length);
                    Log.i(TAG, "MESSAGE_WRITE");
                    break;
                case ChatState.MESSAGE_READ:
                    Log.i(TAG, "MESSAGE_READ");
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf);
                    Log.i(TAG, "MESSAGE_READ readBuf=" + readBuf);
                    Log.i(TAG, "MESSAGE_READ readBuf.length=" + readBuf.length);
                    Log.i(TAG, "MESSAGE_READ readMessage=" + readMessage);
                    Log.i(TAG, "MESSAGE_READ readMessage.length=" + readMessage.length());
                    if (readBuf != null && readBuf.length > 0) {
                        if (mBluetoothChatListener != null) {
                            Log.i(TAG, "MESSAGE_READ onDataReceived");
                            mBluetoothChatListener.onDataReceived(readBuf, readMessage);
                        }
                    }
                    break;
                case ChatState.MESSAGE_DEVICE_INFO:
                    mDeviceName = msg.getData().getString(ChatState.DEVICE_NAME);
                    mDeviceAddress = msg.getData().getString(ChatState.DEVICE_ADDRESS);
                    if (mBluetoothChatListener != null)
                        mBluetoothChatListener.onDeviceConnected(mDeviceName, mDeviceAddress);
                    Log.i(TAG, "MESSAGE_DEVICE_NAME" + mDeviceName);
                    Log.i(TAG, "MESSAGE_DEVICE_NAME" + mDeviceAddress);
                    isConnected = true;
                    break;

                case ChatState.MESSAGE_STATE_CHANGE:
                    Log.i(TAG, "MESSAGE_STATE_CHANGE：" + msg.arg2 + "->" + msg.arg1);
                    if (mBluetoothChatListener != null)
                        mBluetoothChatListener.onServiceStateChanged(msg.arg2 + "->" + msg.arg1);
                    if (isConnected && msg.arg1 != ChatState.STATE_CONNECTED) {
                        if (mBluetoothChatListener != null)
                            mBluetoothChatListener.onDeviceDisconnected();
                        isConnected = false;
                        mDeviceName = null;
                        mDeviceAddress = null;
                    }
                    if (!isConnecting && msg.arg1 == ChatState.STATE_CONNECTING) {
                        isConnecting = true;
                    } else if (isConnecting) {
                        if (msg.arg1 != ChatState.STATE_CONNECTED) {
                            if (mBluetoothChatListener != null)
                                mBluetoothChatListener.onDeviceConnectionFailed();
                        }
                        isConnecting = false;
                    }
                    break;
            }
        }
    };

}
