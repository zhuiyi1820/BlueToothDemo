/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lhj.classic.bluetooth.ble.comm;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.List;

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
 * <p>
 * 作者：linhongjie
 * 时间：2017/1/25 16:36
 * 描述：用于管理蓝牙设备连接和数据服务托管在GATT服务器通信
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class GattConnectControl {
    private final static String TAG = GattConnectControl.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress = "";
    private BluetoothGatt mBluetoothGatt;
    private MyGattListener mgl;
    private Context mContext;

    public GattConnectControl(Context mContext, BluetoothAdapter mBluetoothAdapter) {
        this.mContext = mContext;
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public void setMyGattListener(MyGattListener mgl) {
        this.mgl = mgl;
    }

    /**
     * GATT回调监听：连接更改和服务发现
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {//成功连接后发现服务的尝试
                if (mgl != null)
                    mgl.onConnect(gatt);
                Log.e(TAG, "Connected to GATT server.");
                Log.e(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//断开
                if (mgl != null) mgl.onDisconnect(gatt);
                Log.e(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {//services被发现
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mgl != null) {
                    mgl.onServiceDiscover(gatt);
                }

            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {//characteristic被读到
            if (mgl != null)
                mgl.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {//characteristic被写入
            if (mgl != null)
                mgl.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

        }
    };

    /**
     * 连接到托管在蓝牙LE设备GATT服务器
     *
     * @param address 蓝牙设备的设备地址
     * @return 如果连接成功启动，返回true。连接结果异步通过onConnectionStateChange回调
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        if (!mBluetoothDeviceAddress.equals("") && address.equals(mBluetoothDeviceAddress)
                && !Gatt()) {
            Log.e(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        Log.e(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    /**
     * 断开现有连接或取消挂起的连接。断开结果异步通过onConnectionStateChange回调
     */
    public void disconnect() {
        if (Gatt()) {
            Log.e(TAG, "BluetoothGatt null !");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * 使用一个给定的BLE设备后，应用程序必须调用这个方法来确保资源的正确释放
     */
    public void close() {
        if (Gatt())
            return;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * 读BluetoothGattCharacteristic的结果异步通过onCharacteristicRead回调
     *
     * @param characteristic
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (Gatt()) {
            Log.e(TAG, "BluetoothGatt null !");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
     *
     * @param characteristic
     * @param enabled
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (Gatt()) {
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);


        /*BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString(GattUtils.CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);*/

        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
        for (BluetoothGattDescriptor dp : descriptors) {
            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(dp);
        }

    }

    /**
     * GATT是否为空
     *
     * @return
     */
    public boolean Gatt() {
        return mBluetoothGatt == null;
    }

    /**
     * 写入
     *
     * @param characteristic
     */
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * 检索的连接设备支持的关贸总协定的服务列表。只有在成功完成后才能调用该函数。
     *
     * @return
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (Gatt()) return null;
        return mBluetoothGatt.getServices();
    }
}
