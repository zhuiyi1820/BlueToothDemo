package com.lhj.classic.bluetooth.ble.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lhj.classic.bluetooth.R;
import com.lhj.classic.bluetooth.base.BaseActivity;
import com.lhj.classic.bluetooth.ble.adapter.BleBuletoothAdapter;
import com.lhj.classic.bluetooth.ble.model.BleBuletoothDeviceBean;
import com.lhj.classic.bluetooth.ble.utils.IbeaconUtils;

import java.util.ArrayList;

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
 * 时间：2016/11/1 09:48
 * 描述：BLE终端设备发现(扫描ibeacon基站)
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleBuletoothActivity extends BaseActivity implements View.OnClickListener, BleBuletoothAdapter.BleSignListener, BluetoothAdapter.LeScanCallback {
    private final static String TAG = BleBuletoothActivity.class.getSimpleName();
    private Snackbar snackbar;
    ImageView fab;
    ArrayList<BleBuletoothDeviceBean> mainList = new ArrayList<>();
    BleBuletoothAdapter lvAdapter;
    RelativeLayout base_left_rl;
    RelativeLayout base_right_rl;
    TextView base_left_tv;
    TextView base_title;
    TextView base_right_tv;
    ListView lv;
    boolean flag;
    private final static int SCANNER_TIME = 10000;
    private final static int SCANNER_STOP = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_ble);
        if (!initBle()) {
            Toast.makeText(this, "低功耗蓝牙初始化异常", Toast.LENGTH_LONG).show();
            finish();
        } else {
            initView();
            initData();
            initListener();
        }

    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        base_right_rl.setOnClickListener(this);
        base_left_rl.setOnClickListener(this);
//        lv.setOnItemClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        base_title.setText("低功耗蓝牙");
        base_right_tv.setText("");
        base_left_tv.setText("返回");
        fab.setVisibility(View.VISIBLE);
        lvAdapter = new BleBuletoothAdapter(this, mainList, this);
        lv.setAdapter(lvAdapter);
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
        lv = (ListView) findViewById(R.id.main_lv);
        fab = (ImageView) findViewById(R.id.base_right_iv2);
    }

    /**
     * 搜索BLE设备
     */
    private void startSearch() {
        snackbar = Snackbar.make(fab, "搜索蓝牙中...", Snackbar.LENGTH_LONG).setAction("Action", null);
        snackbar.show();
        startAnimation(fab);
        handler.sendEmptyMessageDelayed(SCANNER_STOP, SCANNER_TIME);
        mBluetoothAdapter.startLeScan(this);
        flag = true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_right_rl:
                if (openBleBluetooth()) {
                    if (flag) {
                        Toast.makeText(this, "正在搜索...", Toast.LENGTH_SHORT).show();
                    } else {
                        startSearch();
                    }
                }
                break;
            case R.id.base_left_rl:
                finish();
                break;
        }
    }

    /**
     * 蓝牙停止搜索BLE设备
     */
    private void stopSearch() {
        stopAnimation(fab);
        mBluetoothAdapter.stopLeScan(this);
        Toast.makeText(this, "停止搜索", Toast.LENGTH_SHORT).show();
        flag = false;
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case SCANNER_STOP:
                    flag = false;
                    stopSearch();
                    break;

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void signOperation(BleBuletoothDeviceBean device) {

        if (device.getDevice() != null) {
            stopSearch();
//            Log.e(TAG,device.getDevice().getName()+"=="+device.getDevice().getAddress());
            Intent it = new Intent(BleBuletoothActivity.this, BleGattActivity.class);
            it.putExtra("device", device.getDevice());
            startActivity(it);
        } else {
            Toast.makeText(this, "设备不存在！" + device.getProximityUuid(), Toast.LENGTH_SHORT).show();
        }

    }

    public void addData(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device != null) {
            /**
             * 这里调用ibeacon工具类封装bieacon并转换距离
             * 非ibeacon设备可替换自己的业务
             */
            BleBuletoothDeviceBean mdb = IbeaconUtils.fromScanData(new BleBuletoothDeviceBean(), device, rssi, scanRecord);
            if (mainList.size() < 1) {
                mainList.add(mdb);
                lvAdapter.notifyDataSetChanged();
                return;
            }
            for (int i = 0; i < mainList.size(); i++) {
                BleBuletoothDeviceBean bbdb = mainList.get(i);
                if (bbdb.getType() == 1) {
                    if (bbdb.getDevice().getAddress().equals(device.getAddress())) {
                        bbdb.setDistance(mdb.getDistance());
                        bbdb.setRssi(mdb.getRssi());
                        bbdb.setTxPower(mdb.getTxPower());
                    }
                } else {
                    if (!bbdb.getDevice().getAddress().equals(mdb.getDevice().getAddress())) {
                        mainList.add(mdb);
                    }
                }
            }

            Log.e(TAG, mdb.getDevice().getAddress() + "========" + mdb.getMajor() + "========" + IbeaconUtils.bytesToHexString(scanRecord));
            Log.e(TAG, "size：" + mainList.size());
            lvAdapter.notifyDataSetChanged();

        }
    }

    /**
     * onLeScan 方法在Android 5.0以下及Android 5.0及以上所运行的线程不同。
     *
     * @param device     BLE设备
     * @param rssi       信号强度
     * @param scanRecord 数据byte[]
     */
    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        if (Looper.myLooper() == Looper.getMainLooper()) {// Android 5.0 及以上
            addData(device, rssi, scanRecord);
        } else {
            runOnUiThread(new Runnable() {// Android 5.0 以下
                @Override
                public void run() {
                    addData(device, rssi, scanRecord);
                }
            });
        }
    }
}