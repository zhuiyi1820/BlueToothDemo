/*
 * Copyright 2014 Akexorcist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lhj.classic.bluetooth.cls.chat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

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
 * 时间：2017/1/9 15:26
 * 描述：蓝牙聊天服务
 */
@SuppressLint("NewApi")
public class ChatService {
    private static final String TAG = "ChatService";

    private static final String NAME_SECURE = "Bluetooth Secure";

    private static final UUID UUID_ANDROID_DEVICE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID UUID_OTHER_DEVICE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口服务

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    Context context;

    /**
     * 准备一个新的bluetoothchat会话
     *
     * @param context
     * @param handler
     * @param mAdapter
     */
    public ChatService(Context context, Handler handler, BluetoothAdapter mAdapter) {
        this.mAdapter = mAdapter;
        mState = ChatState.STATE_NONE;
        mHandler = handler;
        this.context = context;
    }


    /**
     * 设置聊天连接的当前状态
     *
     * @param state
     */
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mHandler.obtainMessage(ChatState.MESSAGE_STATE_CHANGE, state, mState).sendToTarget();
        mState = state;

    }

    /**
     * 返回当前连接状态
     *
     * @return
     */
    public synchronized int getState() {
        return mState;
    }


    /**
     * 开始聊天
     */
    public synchronized void start() {
        /**
         * 取消试图连接的任何线程
         */
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        /**
         *取消当前运行连接的任何线程
         */
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(ChatState.STATE_LISTEN);

        /**
         * 启动线程监听一个bluetoothserversocket
         */
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread();
            mSecureAcceptThread.start();
        }
    }

    /**
     * 开始connectthread发起一个连接到一个远程设备
     *
     * @param device 连接的蓝牙设备
     */
    public synchronized void connect(BluetoothDevice device) {
        /**
         * 取消试图连接的任何线程
         */
        if (mState == ChatState.STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        /**
         * 取消当前运行连接的任何线程
         */
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        /**
         *启动线程与给定设备连接
         */
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(ChatState.STATE_CONNECTING);
    }

    /**
     * 开始connectedthread开始管理蓝牙连接
     *
     * @param socket
     * @param device 已连接的蓝牙设备
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
        Log.i(TAG, "connected");
        /**
         * 取消完成连接的线程
         */
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        /**
         * 取消当前运行连接的任何线程
         */
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        /**
         * 取消接受线程，因为我们只想连接到一个设备
         */
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        /**
         * 启动线程管理连接并执行传输
         */
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        /**
         * 将连接的设备的名称返回到UI活动.
         */
        Message msg = mHandler.obtainMessage(ChatState.MESSAGE_DEVICE_INFO);
        Bundle bundle = new Bundle();
        bundle.putString(ChatState.DEVICE_NAME, device.getName());
        bundle.putString(ChatState.DEVICE_ADDRESS, device.getAddress());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(ChatState.STATE_CONNECTED);
    }

    /**
     * 停止所有的线程
     */
    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread.kill();
            mSecureAcceptThread = null;
        }
        setState(ChatState.STATE_NONE);
    }

    /**
     * 写入
     *
     * @param out
     */
    public void write(byte[] out) {
        Log.e(TAG, "write string out=" + Bytes2HexString(out));
        ConnectedThread r;
        synchronized (this) {
            if (mState != ChatState.STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        r.write(out);
    }

    /**
     * byte[]转string
     */
    public static String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /**
     * 指示连接尝试失败，重新连接
     */
    private void connectionFailed() {
        ChatService.this.start();
    }

    /**
     * 此线程在侦听传入连接时运行。它的行为像一个服务器端客户端。它一直运行到连接被接收（或者直到被取消）
     */
    private class AcceptThread extends Thread {
        private BluetoothServerSocket mmServerSocket;
        boolean isRunning = true;

        private boolean mmss() {
            if (mmServerSocket == null) {
                return false;
            }
            return true;
        }

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            /**
             *创建一个新的BluetoothServerSocket监听服务器套接字
             */
            try {
                if (ChatState.DEVICE_ANDROID)
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, UUID_ANDROID_DEVICE);
                else
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, UUID_OTHER_DEVICE);
            } catch (IOException e) {
                try {
                    throw new SocketException(context, "error-AcceptThread", e);
                } catch (SocketException e1) {
                    e1.printStackTrace();
                    Log.e("soketException", "error-AcceptThread");
                }
            }
            mmServerSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket = null;
            Log.i(TAG, "run");
            /**
             * 如果没有连接，监听服务器套接字
             */
            while (mState != ChatState.STATE_CONNECTED && isRunning) {
                try {
                    /**
                     * 这是一个阻塞调用，只会返回成功的连接或异常
                     */
                    if (mmss())
                        socket = mmServerSocket.accept();
                } catch (IOException e) {
                    try {
                        throw new SocketException(context, "error-AcceptThread-run1", e);
                    } catch (SocketException e1) {
                        e1.printStackTrace();
                        Log.e("soketException", "error-AcceptThread-run1");
                    }
                    break;
                }

                /**
                 * 如果一个连接被接受
                 */
                if (socket != null) {
                    synchronized (ChatService.this) {
                        switch (mState) {
                            case ChatState.STATE_LISTEN:
                            case ChatState.STATE_CONNECTING:
                                /**
                                 * 正常情况。启动连接的线程
                                 */
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case ChatState.STATE_NONE:
                            case ChatState.STATE_CONNECTED:
                                /**
                                 * 要么没有准备好，要么已经连接。终止新套接字。
                                 */
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    try {
                                        throw new SocketException(context, "error-AcceptThread-run2", e);
                                    } catch (SocketException e1) {
                                        e1.printStackTrace();
                                        Log.e("soketException", "error-AcceptThread-run2");
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }

        /**
         * 关闭Socket
         */
        public void cancel() {
            try {
                if (mmss()) {
                    mmServerSocket.close();
                }
                mmServerSocket = null;
            } catch (IOException e) {
                try {
                    throw new SocketException(context, "error-cancel", e);
                } catch (SocketException e1) {
                    e1.printStackTrace();
                    Log.e("soketException", "error-cancel");
                }
            }
        }

        public void kill() {
            isRunning = false;
        }
    }


    /**
     * 此线程在试图与设备进行传出连接时运行。它直接通过连接成功或失败
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        private boolean mms() {
            if (mmSocket == null) {
                return false;
            }
            return true;
        }

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            /**
             * 得到一个与给定的蓝牙设备连接BluetoothSocket
             */
            try {
                if (ChatState.DEVICE_ANDROID)
                    tmp = device.createRfcommSocketToServiceRecord(UUID_ANDROID_DEVICE);
                else
                    tmp = device.createRfcommSocketToServiceRecord(UUID_OTHER_DEVICE);
            } catch (IOException e) {
                try {
                    throw new SocketException(context, "error-ConnectThread", e);
                } catch (SocketException e1) {
                    e1.printStackTrace();
                    Log.e("soketException", "error-ConnectThread");
                }
            }
            mmSocket = tmp;
        }

        public void run() {
            /**
             * 关闭蓝牙扫描，因为它会减慢连接速度
             */
            mAdapter.cancelDiscovery();
            /**
             * 连接BluetoothSocket
             */
            try {
                /**
                 * 这是一个阻塞调用，只会返回成功的连接或异常
                 */
                if (mms())
                    mmSocket.connect();
            } catch (IOException e) {
                cancel();
                connectionFailed();
                return;
            }

            /**
             * 用完复位 ConnectThread
             */
            synchronized (ChatService.this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        /**
         * 关闭Socket
         */
        public void cancel() {
            try {
                if (mms())
                    mmSocket.close();
            } catch (IOException e) {
                try {
                    throw new SocketException(context, "error-cancel", e);
                } catch (SocketException e1) {
                    e1.printStackTrace();
                    Log.e("soketException", "error-cancel");
                }
            }
        }
    }

    /**
     * 此线程在与远程设备连接期间运行。它处理所有传入和传出的传输
     *
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private boolean mms() {
            if (mmSocket == null) {
                return false;
            }
            return true;
        }

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            Log.i(TAG, "ConnectedThread");
            /**
             * 得到BluetoothSocket的输入和输出流
             */
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                try {
                    throw new SocketException(context, "error-ConnectedThread", e);
                } catch (SocketException e1) {
                    e1.printStackTrace();
                    Log.e("soketException", "error-ConnectedThread");
                }
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            Log.i(TAG, "ConnectedThread mmInStream=" + mmInStream);
            Log.i(TAG, "ConnectedThread mmOutStream=" + mmOutStream);
        }

        public void run() {
            byte[] buffer;
            ArrayList<Integer> arr_byte = new ArrayList<Integer>();
            int before = 0;
            /**
             * 循环读取输入流
             */
            while (true) {
                try {
                    int data = mmInStream.read();
                    if (data == 0x0D && before == 0x0A) {
                        buffer = new byte[arr_byte.size()];
                        for (int i = 0; i < buffer.length; i++) {
                            buffer[i] = arr_byte.get(i).byteValue();
                        }
                        Log.i(TAG, "run arr_byte=" + arr_byte);
                        Log.i(TAG, "run arr_byte.size()=" + arr_byte.size());
                        Log.i(TAG, "run buffer=" + buffer);
                        Log.i(TAG, "run buffer=" + buffer.length);
                        mHandler.obtainMessage(ChatState.MESSAGE_READ
                                , buffer.length, -1, buffer).sendToTarget();
                        arr_byte = new ArrayList<Integer>();
                    } else {
                        arr_byte.add(data);
                    }
                    before = data;
                } catch (IOException e) {
                    connectionFailed();
                    break;
                }
            }
        }

        /**
         * 写入输出流
         *
         * @param buffer
         */
        public void write(byte[] buffer) {
            Log.i(TAG, "write buffer=" + buffer);
            Log.i(TAG, "write buffer.length=" + buffer.length);
            try {
                byte[] buffer2 = new byte[buffer.length + 2];
                for (int i = 0; i < buffer.length; i++)
                    buffer2[i] = buffer[i];
                buffer2[buffer2.length - 2] = 0x0A;
                buffer2[buffer2.length - 1] = 0x0D;
                Log.i(TAG, "write buffer2.length=" + buffer2.length);
                mmOutStream.write(buffer2);
                Log.i(TAG, "write mmOutStream=" + mmOutStream);
                mHandler.obtainMessage(ChatState.MESSAGE_WRITE
                        , -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                try {
                    throw new SocketException(context, "error-write", e);
                } catch (SocketException e1) {
                    e1.printStackTrace();
                    Log.e("soketException", "error-write");
                }
            }
        }

        /**
         * 关闭Socket
         */
        public void cancel() {
            try {
                if (mms())
                    mmSocket.close();
            } catch (IOException e) {
                try {
                    throw new SocketException(context, "error-cancel", e);
                } catch (SocketException e1) {
                    e1.printStackTrace();
                    Log.e("soketException", "error-cancel");
                }
            }
        }
    }
}
