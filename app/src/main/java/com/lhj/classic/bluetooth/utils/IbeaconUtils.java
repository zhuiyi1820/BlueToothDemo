package com.lhj.classic.bluetooth.utils;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.lhj.classic.bluetooth.model.BleBuletoothDeviceBean;

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
 * 时间：2017/1/10 15:05
 * 描述：ibeacon工具类
 */
public class IbeaconUtils {

    public static BleBuletoothDeviceBean fromScanData(BleBuletoothDeviceBean mdb, BluetoothDevice device, int rssi, byte[] scanData) {
        mdb.setDevice(device);
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int) scanData[startByte + 2] & 0xff) == 0x02 && ((int) scanData[startByte + 3] & 0xff) == 0x15) {
                patternFound = true;
                break;
            } else if (((int) scanData[startByte] & 0xff) == 0x2d && ((int) scanData[startByte + 1] & 0xff) == 0x24
                    && ((int) scanData[startByte + 2] & 0xff) == 0xbf
                    && ((int) scanData[startByte + 3] & 0xff) == 0x16) {

            } else if (((int) scanData[startByte] & 0xff) == 0xad && ((int) scanData[startByte + 1] & 0xff) == 0x77
                    && ((int) scanData[startByte + 2] & 0xff) == 0x00
                    && ((int) scanData[startByte + 3] & 0xff) == 0xc6) {
            }
            startByte++;
        }
        if (patternFound == false) {
            mdb.setType(0);
            return mdb;
        }
//        BleBuletoothDeviceBean iBeacon = new BleBuletoothDeviceBean();
        byte[] proximityUuidBytes = new byte[16];
        System.arraycopy(scanData, startByte + 4, proximityUuidBytes, 0, 16);
        String hexString = bytesToHexString(proximityUuidBytes);
        mdb.setProximityUuid(hexString);
        mdb.setMajor((scanData[startByte + 20] & 0xff) * 0x100 + (scanData[startByte + 21] & 0xff));
        mdb.setMinor((scanData[startByte + 22] & 0xff) * 0x100 + (scanData[startByte + 23] & 0xff));
        mdb.setTxPower((int) scanData[startByte + 24]);
        mdb.setRssi(rssi);
        mdb.setDistance(String.format("%.2f", calculateAccuracy(mdb.getTxPower(), rssi)));
        mdb.setType(1);
        Log.e("iBeacon", mdb.getProximityUuid() + "," + mdb.getMajor() + "=======" + mdb.getType());
        return mdb;
    }

    /**
     * 转换十进制
     *
     * @param src
     * @return
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return "";
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 估算用户设备到ibeacon的距离
     *
     * @param txPower
     * @param rssi
     * @return
     */
    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

}
