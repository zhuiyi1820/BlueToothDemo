package com.lhj.classic.bluetooth.ble.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lhj.classic.bluetooth.R;
import com.lhj.classic.bluetooth.base.BaseActivity;
import com.lhj.classic.bluetooth.ble.comm.GattConnectControl;
import com.lhj.classic.bluetooth.ble.comm.MyGattListener;
import com.lhj.classic.bluetooth.ble.utils.GattUtils;

import java.util.ArrayList;
import java.util.HashMap;
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
public class BleGattActivity extends BaseActivity implements View.OnClickListener, MyGattListener, ExpandableListView.OnChildClickListener {
    private final static String TAG = BleGattActivity.class.getSimpleName();
    private final static String UUID_KEY_DATA = "0783b03e-8535-b5a0-7140-a304d2495cbb";
    private final static String LIST_NAME = "NAME";
    private final static String LIST_UUID = "UUID";

    RelativeLayout base_left_rl;
    RelativeLayout base_right_rl;
    TextView base_left_tv;
    TextView base_title;
    TextView base_right_tv;
    TextView device_address;
    TextView connection_state;
    TextView data_value;

    ExpandableListView gatt_services_list;
    SimpleExpandableListAdapter gattAdapter;

    GattConnectControl gc;
    BluetoothDevice device;

    ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
    ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
    ArrayList<ArrayList<BluetoothGattCharacteristic>> bleGattCharacteristicList = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

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
        gatt_services_list = (ExpandableListView) findViewById(R.id.gatt_services_list);
        base_left_rl = (RelativeLayout) findViewById(R.id.base_left_rl);
        base_right_rl = (RelativeLayout) findViewById(R.id.base_right_rl);
        base_left_tv = (TextView) findViewById(R.id.base_left_tv);
        base_title = (TextView) findViewById(R.id.base_title);
        base_right_tv = (TextView) findViewById(R.id.base_right_tv);
        device_address = (TextView) findViewById(R.id.device_address);
        connection_state = (TextView) findViewById(R.id.connection_state);
        data_value = (TextView) findViewById(R.id.data_value);
        base_title.setText("GATT连接");
        base_right_tv.setText("重连");
        base_left_tv.setText("返回");
    }

    /**
     * 初始化GATT配置
     */
    public void initDataAndListener() {
        device = getIntent().getParcelableExtra("device");
        device_address.setText(device.getAddress());
        base_left_rl.setOnClickListener(this);
        base_right_rl.setOnClickListener(this);
        //初始化gatt控制器
        gc = new GattConnectControl(this, mBluetoothAdapter);
        //设置gatt监听
        gc.setMyGattListener(this);
        //首次连接gatt
        GattConnect();
        gattAdapter = new SimpleExpandableListAdapter(this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{LIST_NAME, LIST_UUID},
                new int[]{android.R.id.text1, android.R.id.text2},
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{LIST_NAME, LIST_UUID},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        gatt_services_list.setAdapter(gattAdapter);
        gatt_services_list.setOnChildClickListener(this);
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
                if (gc == null) return;
                if (base_right_tv.getText().toString().equals("断开")) {
                    gc.disconnect();
                } else if (base_right_tv.getText().toString().equals("重连")) {
                    GattConnect();
                }
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
     * gatt连接
     *
     * @param gatt
     */
    @Override
    public void onConnect(BluetoothGatt gatt) {
        if (gatt.discoverServices()) {
            updateUi(1);
        } else {
            updateUi(-1);
        }

    }

    /**
     * gatt断开
     *
     * @param gatt
     */
    @Override
    public void onDisconnect(BluetoothGatt gatt) {
        if (gatt.discoverServices()) {
            updateUi(0);
        } else {
            updateUi(-1);
        }
    }

    /**
     * 搜索到BLE终端服务的事件
     */
    @Override
    public void onServiceDiscover(BluetoothGatt gatt) {
        Log.e(TAG, "搜索到BLE终端服务的事件");
        displayGattServices(gc.getSupportedGattServices());
    }

    /**
     * UI界面更新
     *
     * @param state
     */
    public void updateUi(int state) {
        switch (state) {
            case 0:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        base_right_tv.setText("重连");
                        connection_state.setText("已断开");
                        data_value.setText("无数据");
                        gattServiceData.clear();
                        gattCharacteristicData.clear();
                        bleGattCharacteristicList.clear();
                        gattAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case 1:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        base_right_tv.setText("断开");
                        connection_state.setText("连接中");
                        data_value.setText("无数据");
                    }
                });
                break;
            case 2:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gattAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case 3:
                break;
            case -1://异常
                break;
        }

    }


    /**
     * @param gattServices
     */
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        Log.e(TAG, "Service-size:" + gattServices.size());
        for (BluetoothGattService gattService : gattServices) {
            //-----Service的字段信息-----//
            Log.e(TAG, "Service的字段信息-start");
            Log.e(TAG, "-->service type:" + gattService.getType());
            Log.e(TAG, "-->includedServices size:" + gattService.getIncludedServices().size());
            Log.e(TAG, "-->service uuid:" + gattService.getUuid().toString());
            Log.e(TAG, "Service的字段信息-end");
            HashMap<String, String> sh = new HashMap<>();
            sh.put(LIST_NAME, GattUtils.lookup(gattService.getUuid().toString(), "serviceName==null"));
            sh.put(LIST_UUID, gattService.getUuid().toString());
            gattServiceData.add(sh);
            //-----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            Log.e(TAG, "Characteristics-size:" + gattCharacteristics.size());

            ArrayList<HashMap<String, String>> ch = new ArrayList<HashMap<String, String>>();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                Log.e(TAG, "Characteristics的字段信息-start");
                Log.e(TAG, "---->char uuid:" + gattCharacteristic.getUuid());
                Log.e(TAG, "---->char permission:" + gattCharacteristic.getPermissions());
                Log.e(TAG, "---->char property:" + gattCharacteristic.getProperties());
                if (gattCharacteristic.getValue() != null && gattCharacteristic.getValue().length > 0) {
                    Log.e(TAG, "---->char value:" + new String(gattCharacteristic.getValue()));
                }
                charas.add(gattCharacteristic);
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put(LIST_NAME, GattUtils.lookup(gattCharacteristic.getUuid().toString(), "characteristicsName==null"));
                hm.put(LIST_UUID, gattCharacteristic.getUuid().toString());
                ch.add(hm);
                Log.e(TAG, "Characteristics的字段信息-end");
                //-----Descriptors的字段信息-----//
                List<BluetoothGattDescriptor> descriptors = gattCharacteristic.getDescriptors();
                Log.e(TAG, "Descriptors-size:" + descriptors.size());
                for (BluetoothGattDescriptor gattDescriptor : descriptors) {
                    Log.e(TAG, "Descriptors的字段信息-start");
                    Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
                    Log.e(TAG, "-------->desc permission:" + gattDescriptor.getPermissions());
                    if (gattDescriptor.getValue() != null && gattDescriptor.getValue().length > 0) {
                        Log.e(TAG, "-------->desc value:" + new String(gattDescriptor.getValue()));
                    }
                    Log.e(TAG, "Descriptors的字段信息-end");
                }
                /*//UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
                Log.e(TAG, "你是什么：" + gattCharacteristic.getUuid().toString());
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
                }*/

            }
            bleGattCharacteristicList.add(charas);
            gattCharacteristicData.add(ch);

        }
        updateUi(2);
    }


    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }
}
