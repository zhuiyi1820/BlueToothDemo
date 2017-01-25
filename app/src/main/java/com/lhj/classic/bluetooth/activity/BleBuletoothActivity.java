package com.lhj.classic.bluetooth.activity;

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
import com.lhj.classic.bluetooth.adapter.BleBuletoothAdapter;
import com.lhj.classic.bluetooth.base.BaseActivity;
import com.lhj.classic.bluetooth.model.BleBuletoothDeviceBean;
import com.lhj.classic.bluetooth.model.EventBusEntity;
import com.lhj.classic.bluetooth.utils.IbeaconUtils;

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
 * 描述：低功耗蓝牙扫描ibeacon基站
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
    private boolean bleFlag;

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
     * 搜索ibeancon设备
     */
    private void startSearch() {
        snackbar = Snackbar.make(fab, "搜索蓝牙中...", Snackbar.LENGTH_LONG).setAction("Action", null);
        snackbar.show();
        startAnimation(fab);
        handler.sendEmptyMessageDelayed(111, 5000);
        mBluetoothAdapter.startLeScan(this);
        flag = true;
    }

    public void onEventMainThread(EventBusEntity eventBusEntity) {
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
     * 蓝牙停止搜索ibeancon设备
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

                case 111://搜索5秒后停止搜索
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
            Intent it = new Intent(BleBuletoothActivity.this, BleGattActivity.class);
            it.putExtra("device", device.getDevice());
            startActivity(it);
            Toast.makeText(this, "继续做你想做的事情" + device.getProximityUuid(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "设备不存在！" + device.getProximityUuid(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 测试数据
     *
     * @param de
     */
    public void addTempData(BluetoothDevice de) {
        BleBuletoothDeviceBean iBeacon = new BleBuletoothDeviceBean();
        iBeacon.setDistance("111");
        iBeacon.setTxPower(111);
        iBeacon.setRssi(111);
        iBeacon.setProximityUuid("测试1");
        iBeacon.setDevice(de);
        iBeacon.setMajor(1);
        iBeacon.setMinor(1);
        iBeacon.setType(1);
        mainList.add(iBeacon);
        BleBuletoothDeviceBean iBeacon2 = new BleBuletoothDeviceBean();
        iBeacon2.setDistance("222");
        iBeacon2.setTxPower(222);
        iBeacon2.setRssi(222);
        iBeacon2.setProximityUuid("测试2");
        iBeacon2.setDevice(de);
        iBeacon2.setMajor(2);
        iBeacon2.setMinor(2);
        iBeacon2.setType(1);
        mainList.add(iBeacon2);
        BleBuletoothDeviceBean iBeacon3 = new BleBuletoothDeviceBean();
        iBeacon3.setDistance("333");
        iBeacon3.setTxPower(333);
        iBeacon3.setRssi(333);
        iBeacon3.setProximityUuid("测试3");
        iBeacon3.setDevice(de);
        iBeacon3.setMajor(3);
        iBeacon3.setMinor(3);
        iBeacon3.setType(1);
        mainList.add(iBeacon3);
        BleBuletoothDeviceBean iBeacon4 = new BleBuletoothDeviceBean();
        iBeacon4.setType(0);
        iBeacon4.setDevice(de);
        mainList.add(iBeacon4);
        BleBuletoothDeviceBean iBeacon5 = new BleBuletoothDeviceBean();
        iBeacon5.setDistance("555");
        iBeacon5.setTxPower(555);
        iBeacon5.setRssi(555);
        iBeacon5.setProximityUuid("测试5");
        iBeacon5.setDevice(de);
        iBeacon5.setMajor(5);
        iBeacon5.setMinor(5);
        iBeacon5.setType(1);
        mainList.add(iBeacon5);
        BleBuletoothDeviceBean iBeacon6 = new BleBuletoothDeviceBean();
        iBeacon6.setType(0);
        iBeacon6.setDevice(de);
        mainList.add(iBeacon6);

    }

    private void addData(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device != null) {
            BleBuletoothDeviceBean mdb = IbeaconUtils.fromScanData(new BleBuletoothDeviceBean(), device, rssi, scanRecord);
            if (mainList.size() <= 0) {
                mainList.add(mdb);
//                addTempData(mdb.getDevice());
                lvAdapter.notifyDataSetChanged();
                return;
            }
            for (int i = 0; i < mainList.size(); i++) {
                if (!mdb.getDevice().getAddress().equals(mainList.get(i).getDevice().getAddress())) {
                    mainList.add(mdb);
                } else {
                    if (mainList.get(i).getType() == 1) {
                        if (!mdb.getDistance().equals(mainList.get(i).getDistance())) {
                            mainList.get(i).setDistance(mdb.getDistance());
                        }
                        if (mdb.getRssi() != mainList.get(i).getRssi()) {
                            mainList.get(i).setRssi(mdb.getRssi());
                        }
                        if (mdb.getTxPower() != mainList.get(i).getTxPower()) {
                            mainList.get(i).setTxPower(mdb.getTxPower());
                        }
                    }

                }
            }
            Log.e(TAG, mdb.getDevice().getAddress() + "========" + mdb.getMajor());
            Log.e(TAG, "" + mainList.size());
            lvAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        //onLeScan 方法在Android 5.0以下及Android 5.0及以上所运行的线程不同。
        if (Looper.myLooper() == Looper.getMainLooper()) {//// Android 5.0 及以上
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