package com.lhj.classic.bluetooth.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lhj.classic.bluetooth.R;
import com.lhj.classic.bluetooth.base.BaseActivity;
import com.lhj.classic.bluetooth.utils.GattUtils;

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
 * <p/>
 * 作者：linhongjie
 * 时间：2017/1/25 15:42
 * 描述：GATT连接
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleGattActivity extends BaseActivity implements View.OnClickListener, GattConnectControl.OnServiceDiscoverListener, GattConnectControl.OnDataAvailableListener {
    private final static String TAG = BleGattActivity.class.getSimpleName();
    private final static String UUID_KEY_DATA = "0783b03e-8535-b5a0-7140-a304d2495cbb";
    private Handler mHandler = new Handler();
    /**
     * 读写BLE终端
     */
    private GattConnectControl gc;
    private BluetoothDevice device;

    RelativeLayout base_left_rl;
    RelativeLayout base_right_rl;
    TextView base_left_tv;
    TextView base_title;
    TextView base_right_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_gatt);
        initView();
        if (!initBle()) {
            Toast.makeText(this, "低功耗蓝牙初始化异常", Toast.LENGTH_LONG).show();
            finish();
        } else {
            initDataAndListener();
        }

    }

    /**
     * 初始化控件
     */
    private void initView() {
        base_left_rl = (RelativeLayout) findViewById(R.id.base_left_rl);
        base_right_rl = (RelativeLayout) findViewById(R.id.base_right_rl);
        base_left_tv = (TextView) findViewById(R.id.base_left_tv);
        base_title = (TextView) findViewById(R.id.base_title);
        base_right_tv = (TextView) findViewById(R.id.base_right_tv);
        base_title.setText("GATT连接");
        base_right_tv.setText("重连");
        base_left_tv.setText("返回");
    }

    /**
     * 初始化GATT配置
     */
    public void initDataAndListener() {
        Log.e(TAG,"aaaa");
        device = getIntent().getParcelableExtra("device");
        base_left_rl.setOnClickListener(this);
        base_right_rl.setOnClickListener(this);
        gc = new GattConnectControl(this, mBluetoothAdapter);
        //发现BLE终端的Service时回调
        gc.setOnServiceDiscoverListener(this);
        //收到BLE终端数据交互的事件
        gc.setOnDataAvailableListener(this);
        GattConnect();
    }

    /**
     * 连接
     */
    public void GattConnect() {
        if (device == null) {
            Toast.makeText(this, "连接的蓝牙模块异常", Toast.LENGTH_LONG).show();
            return;
        }
        boolean connect = gc.connect(device.getAddress());
        Log.e("flag", "开始连接gatt=" + connect);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_left_rl:
                finish();
                break;
            case R.id.base_right_rl:
                GattConnect();
                Toast.makeText(this, "手动连接", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * BLE终端数据被读的事件
     */
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.e(TAG, "调onCharacteristicRead");
        if (status == BluetoothGatt.GATT_SUCCESS)
            Log.e(TAG, "BLE终端数据被读的事件onCharRead " + gatt.getDevice().getName()
                    + " read "
                    + characteristic.getUuid().toString()
                    + " -> "
                    + GattUtils.bytesToHexString(characteristic.getValue()));
        else Log.e(TAG, "没有读到");

    }

    /**
     * 收到BLE终端写入数据回调
     */
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.e(TAG, "调onCharacteristicWrite");
        Log.e(TAG, "收到BLE终端写入数据回调onCharWrite " + gatt.getDevice().getName()
                + " write "
                + characteristic.getUuid().toString()
                + " -> "
                + new String(characteristic.getValue()));
    }

    /**
     * 搜索到BLE终端服务的事件
     */
    @Override
    public void onServiceDiscover(BluetoothGatt gatt) {
        Log.e(TAG, "搜索到BLE终端服务的事件");
        displayGattServices(gc.getSupportedGattServices());
    }


    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        for (BluetoothGattService gattService : gattServices) {
            //-----Service的字段信息-----//
            int type = gattService.getType();
            Log.e(TAG, "-->service type:" + GattUtils.getServiceType(type));
            Log.e(TAG, "-->includedServices size:" + gattService.getIncludedServices().size());
            Log.e(TAG, "-->service uuid:" + gattService.getUuid());

            //-----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                Log.e(TAG, "---->char uuid:" + gattCharacteristic.getUuid());

                int permission = gattCharacteristic.getPermissions();
                Log.e(TAG, "---->char permission:" + GattUtils.getCharPermission(permission));

                int property = gattCharacteristic.getProperties();
                Log.e(TAG, "---->char property:" + GattUtils.getCharPropertie(property));

                byte[] data = gattCharacteristic.getValue();
                if (data != null && data.length > 0) {
                    Log.e(TAG, "---->char value:" + new String(data));
                }
                Log.e(TAG, "你是什么：" + gattCharacteristic.getUuid().toString());
                //UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
                if (gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)) {
                    //测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gc.readCharacteristic(gattCharacteristic);
                        }
                    }, 500);

                    //接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    gc.setCharacteristicNotification(gattCharacteristic, true);
                    //设置数据内容
                    gattCharacteristic.setValue("123456789");
                    //往蓝牙模块写入数据
                    gc.writeCharacteristic(gattCharacteristic);
                }

                //-----Descriptors的字段信息-----//
                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                    Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
                    int descPermission = gattDescriptor.getPermissions();
                    Log.e(TAG, "-------->desc permission:" + GattUtils.getDescPermission(descPermission));

                    byte[] desData = gattDescriptor.getValue();
                    if (desData != null && desData.length > 0) {
                        Log.e(TAG, "-------->desc value:" + new String(desData));
                    }
                }
            }
        }
    }


}
