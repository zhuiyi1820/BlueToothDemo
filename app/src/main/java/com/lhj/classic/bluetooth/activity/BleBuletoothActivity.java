package com.lhj.classic.bluetooth.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lhj.classic.bluetooth.R;
import com.lhj.classic.bluetooth.adapter.BleBuletoothAdapter;
import com.lhj.classic.bluetooth.base.BaseActivity;
import com.lhj.classic.bluetooth.model.EventBusEntity;
import com.lhj.classic.bluetooth.model.IbeaconBean;
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
 * <p>
 * 作者：linhongjie
 * 时间：2016/11/1 09:48
 * 描述：低功耗蓝牙扫描ibeacon基站
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleBuletoothActivity extends BaseActivity implements View.OnClickListener, BleBuletoothAdapter.BleSignListener, BluetoothAdapter.LeScanCallback {

    private Snackbar snackbar;
    ImageView fab;
    ArrayList<IbeaconBean> mainList = new ArrayList<>();
    BleBuletoothAdapter lvAdapter;
    RelativeLayout base_left_rl;
    RelativeLayout base_right_rl;
    TextView base_left_tv;
    TextView base_title;
    TextView base_right_tv;
    ListView lv;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_bluetooth);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        initView();
        initData();
        initListener();
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
     * 初始化和控件
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
    }

    public void onEventMainThread(EventBusEntity eventBusEntity) {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_right_rl:
                if (openBleBluetooth()) {
                    if (flag) {
                        stopSearch();
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
                    flag = true;
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

    public void addItems(IbeaconBean commodity) {
        if (mainList.size() <= 0) {
            mainList.add(commodity);
            lvAdapter.notifyDataSetChanged();
            return;
        }
        for (int i = 0; i < mainList.size(); i++) {
            if (!commodity.getBluetoothAddress().equals(mainList.get(i).getBluetoothAddress())) {
                mainList.add(commodity);
            } else {
                if (!commodity.getDistance().equals(mainList.get(i).getDistance())) {
                    mainList.get(i).setDistance(commodity.getDistance());
                }
                if (commodity.getRssi() != mainList.get(i).getRssi()) {
                    mainList.get(i).setRssi(commodity.getRssi());
                }
                if (commodity.getTxPower() != mainList.get(i).getTxPower()) {
                    mainList.get(i).setTxPower(commodity.getTxPower());
                }
            }
        }
        lvAdapter.notifyDataSetChanged();

    }

    @Override
    public void signOperation(IbeaconBean device) {

        Toast.makeText(this, "继续做你想做的事情" + device.getProximityUuid(), Toast.LENGTH_SHORT).show();

    }


    private void connect(BluetoothDevice device, int rssi, byte[] scanRecord) {
        final IbeaconBean ibeacon = IbeaconUtils.fromScanData(device, rssi, scanRecord);
        if (ibeacon != null && ibeacon.getBluetoothAddress() != null && !ibeacon.getBluetoothAddress().equals("")) {
            addItems(ibeacon);
        }
    }

    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        //onLeScan 方法在Android 5.0以下及Android 5.0及以上所运行的线程不同。
        if (Looper.myLooper() == Looper.getMainLooper()) {//// Android 5.0 及以上
            connect(device, rssi, scanRecord);
        } else {
            runOnUiThread(new Runnable() {// Android 5.0 以下
                @Override
                public void run() {
                    connect(device, rssi, scanRecord);
                }
            });
        }
    }
}