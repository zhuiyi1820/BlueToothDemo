package com.lhj.classic.bluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lhj.classic.bluetooth.R;
import com.lhj.classic.bluetooth.adapter.ClassicBluetoothAdapter;
import com.lhj.classic.bluetooth.base.BaseActivity;
import com.lhj.classic.bluetooth.model.EventBusEntity;
import com.lhj.classic.bluetooth.utils.ClsUtils;

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
 * 描述：
 */
public class ClassicBluetoothActivity extends BaseActivity implements View.OnClickListener, ClassicBluetoothAdapter.SignListener, AdapterView.OnItemClickListener {

    Snackbar snackbar;
    ImageView fab;
    ArrayList<BluetoothDevice> mainList = new ArrayList<>();
    ClassicBluetoothAdapter lvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_bluetooth);
        initView();
        initReceiver();
    }

    private void initView() {
        RelativeLayout base_left_rl = (RelativeLayout) findViewById(R.id.base_left_rl);
        RelativeLayout base_right_rl = (RelativeLayout) findViewById(R.id.base_right_rl);
        TextView base_left_tv = (TextView) findViewById(R.id.base_left_tv);
        TextView base_title = (TextView) findViewById(R.id.base_title);
        TextView base_right_tv = (TextView) findViewById(R.id.base_right_tv);
        base_title.setText("蓝牙配对");
        base_right_tv.setText("");
        base_left_tv.setText("返回");
        base_left_rl.setOnClickListener(this);
        fab = (ImageView) findViewById(R.id.base_right_iv2);
        fab.setVisibility(View.VISIBLE);
        base_right_rl.setOnClickListener(this);
        ListView lv = (ListView) findViewById(R.id.main_lv);
        lvAdapter = new ClassicBluetoothAdapter(this, mainList, this);
        lv.setAdapter(lvAdapter);
        lv.setOnItemClickListener(this);
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();// 设置广播信息过滤   
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//设备连接状态改变的广播
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//扫描模式变化广播
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//开关模式变化广播
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//扫描结束
        // 注册广播接收器，接收并处理搜索结果   
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 搜索蓝牙
     */
    private void startSearch() {
        snackbar = Snackbar.make(fab, "搜索蓝牙中...", Snackbar.LENGTH_LONG).setAction("Action", null);
        snackbar.show();
        startAnimation(fab);
        addBluetooth(mainList);
        lvAdapter.notifyDataSetChanged();
    }

    /**
     * 蓝牙配对请求与扫描刷新状态
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) { // 发现设备的广播
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                /*if (device.getBondState() == BluetoothDevice.BOND_BONDED) {//已匹配的设备
                    if (device != null) {
                        if (!mainList.contains(device)) {
                            mainList.add(device);
                            lvAdapter.notifyDataSetChanged();
                        }
                    }
                }*/
                if (device != null) {
                    if (!mainList.contains(device)) {//未包含则添加
                        mainList.add(device);
                        lvAdapter.notifyDataSetChanged();
                    }
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {//
                Toast.makeText(ClassicBluetoothActivity.this, "搜索结束...", Toast.LENGTH_LONG).show();
                stopAnimation(fab);
            }
            Log.e("BLUE", "size = " + mainList.size());

            lvAdapter.notifyDataSetChanged();
        }

    };

    public void onEventMainThread(EventBusEntity eventBusEntity) {
        if (eventBusEntity.getMsg().equals("blue_success")) {
            if (!eventBusEntity.getAddress().equals("") && eventBusEntity.isResult()) {
                Log.e("blue:", "blue_success");
                Toast.makeText(this, "配对" + eventBusEntity.getName() + "成功", Toast.LENGTH_LONG).show();
                lvAdapter.notifyDataSetChanged();
            }

        }
    }

    /**
     * 蓝牙配对
     *
     * @param mBluetoothDevice
     */
    protected void connectDevice(BluetoothDevice mBluetoothDevice) {
        try {
            if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {//配对
                ClsUtils.createBond(mBluetoothDevice.getClass(), mBluetoothDevice);
            } else {
                ClsUtils.removeBond(BluetoothDevice.class, mBluetoothDevice);
//                ClsUtils.cancelBondProcess(BluetoothDevice.class, mBluetoothDevice);
                Toast.makeText(this, "蓝牙配对解除", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
//            Toast.makeText(this, "蓝牙配对失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_right_rl:
                if (openBluetooth()) {
                    if (adapter.isDiscovering()) {
                        adapter.cancelDiscovery();
                        Toast.makeText(this,"正在停止搜索...",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startSearch();
                }
                break;
            case R.id.base_left_rl:
                finish();
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        lvAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    @Override
    public void blueOperation(BluetoothDevice device) {
        adapter.cancelDiscovery();
        stopAnimation(fab);
        connectDevice(device);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent it = new Intent(ClassicBluetoothActivity.this, BlueToothChatActivity.class);
        it.putExtra("device", mainList.get(position));
        startActivity(it);
    }
}
