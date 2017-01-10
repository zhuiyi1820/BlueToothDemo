package com.lhj.classic.bluetooth.utils;

import android.bluetooth.BluetoothDevice;

import com.lhj.classic.bluetooth.model.IbeaconBean;

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
 * 时间：2017/1/10 15:05
 * 描述：ibeacon工具类
 */
public class IbeaconUtils {

    /**
     * 解析iBeacon信息
     *
     * @param device
     * @param rssi
     * @param scanData
     * @return
     */
    public static IbeaconBean fromScanData(BluetoothDevice device, int rssi, byte[] scanData) {

        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int) scanData[startByte + 2] & 0xff) == 0x02 && ((int) scanData[startByte + 3] & 0xff) == 0x15) {
                // 这是 iBeacon
                patternFound = true;
                break;
            } else if (((int) scanData[startByte] & 0xff) == 0x2d && ((int) scanData[startByte + 1] & 0xff) == 0x24
                    && ((int) scanData[startByte + 2] & 0xff) == 0xbf
                    && ((int) scanData[startByte + 3] & 0xff) == 0x16) {
                IbeaconBean iBeacon = new IbeaconBean();
                iBeacon.setMajor(0);
                iBeacon.setMinor(0);
                iBeacon.setProximityUuid("00000000-0000-0000-0000-000000000000");
                iBeacon.setTxPower(-55);
                iBeacon.setDistance(String.format("%.2f", calculateAccuracy(iBeacon.getTxPower(), rssi)));
                return iBeacon;
            } else if (((int) scanData[startByte] & 0xff) == 0xad && ((int) scanData[startByte + 1] & 0xff) == 0x77
                    && ((int) scanData[startByte + 2] & 0xff) == 0x00
                    && ((int) scanData[startByte + 3] & 0xff) == 0xc6) {
                IbeaconBean iBeacon = new IbeaconBean();
                iBeacon.setMajor(0);
                iBeacon.setMinor(0);
                iBeacon.setProximityUuid("00000000-0000-0000-0000-000000000000");
                iBeacon.setTxPower(-55);
                iBeacon.setDistance(String.format("%.2f", calculateAccuracy(iBeacon.getTxPower(), rssi)));
                return iBeacon;
            }
            startByte++;
        }
        if (patternFound == false) {
            // 这不是iBeacon
            return null;
        }
        IbeaconBean iBeacon = new IbeaconBean();
        iBeacon.setMajor((scanData[startByte + 20] & 0xff) * 0x100 + (scanData[startByte + 21] & 0xff));
        iBeacon.setMinor((scanData[startByte + 22] & 0xff) * 0x100 + (scanData[startByte + 23] & 0xff));
        iBeacon.setTxPower((int) scanData[startByte + 24]);
        iBeacon.setRssi(rssi);
        iBeacon.setDevice(device);
        // 格式化UUID
        byte[] proximityUuidBytes = new byte[16];
        System.arraycopy(scanData, startByte + 4, proximityUuidBytes, 0, 16);
        String hexString = bytesToHexString(proximityUuidBytes);
        StringBuilder sb = new StringBuilder();
        sb.append(hexString.substring(0, 8));
        sb.append("-");
        sb.append(hexString.substring(8, 12));
        sb.append("-");
        sb.append(hexString.substring(12, 16));
        sb.append("-");
        sb.append(hexString.substring(16, 20));
        sb.append("-");
        sb.append(hexString.substring(20, 32));
        iBeacon.setProximityUuid(sb.toString());
        if (device != null) {
            iBeacon.setBluetoothAddress(device.getAddress());
            iBeacon.setName(device.getName());
        }
        iBeacon.setDistance(String.format("%.2f", calculateAccuracy(iBeacon.getTxPower(), rssi)));
        return iBeacon;
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
            return null;
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
