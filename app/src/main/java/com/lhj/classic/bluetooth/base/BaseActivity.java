package com.lhj.classic.bluetooth.base;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.lhj.classic.bluetooth.model.EventBusEntity;

import java.util.ArrayList;
import java.util.Set;

import de.greenrobot.event.EventBus;

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
 * 时间：2017/1/9 11:20
 * 描述：base基类
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BaseActivity extends AppCompatActivity {
    public BluetoothAdapter adapter;//经典蓝牙
    private AnimationSet animset;//动画
    public BluetoothAdapter mBluetoothAdapter;//低功耗蓝牙
    public BluetoothManager bluetoothManager;//低功耗蓝牙管理

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void onEventMainThread(EventBusEntity ebe) {
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 低功耗蓝牙初始化
     * @return
     */
    public boolean initBle(){
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        return  mBluetoothAdapter!=null;
    }

    /**
     * 蓝牙功能是否开启
     *
     * @return
     */
    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 搜索动画停止
     *
     * @param view
     */
    public void stopAnimation(View view) {
        view.clearAnimation();
    }

    /**
     * 搜索动画启动
     *
     * @param view
     */
    public void startAnimation(View view) {
        animset = new AnimationSet(false);
        RotateAnimation mrotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mrotate.setDuration(800);
        LinearInterpolator ddd = new LinearInterpolator();
        mrotate.setInterpolator(ddd);
        mrotate.setRepeatCount(10000);
        mrotate.setFillAfter(true);
        animset.addAnimation(mrotate);
        view.startAnimation(animset);
    }

    /**
     * 打开蓝牙
     */
    public boolean openBluetooth() {
        if (adapter == null) {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (!adapter.isEnabled()) { // 打开蓝牙   
                // 设置蓝牙可见性，最多300秒   
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivityForResult(intent, 0);
                return false;
            }
        }
        return true;
    }

    /**
     * 打开蓝牙
     */
    public boolean openBleBluetooth() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "该设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mBluetoothAdapter.isEnabled()) { // 打开蓝牙   
            // 设置蓝牙可见性，最多300秒   
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(intent, 0);
            return false;
        }
        return true;
    }

    /**
     * 添加蓝牙设备列表
     *
     * @param mainList
     */
    public void addBluetooth(ArrayList<BluetoothDevice> mainList) {
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (!mainList.contains(device)) {
                    mainList.add(device);
                }
            }
        }
        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去   
        adapter.startDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Toast.makeText(this, "开启蓝牙成功", Toast.LENGTH_SHORT).show();
//                startSearch();
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "开启蓝牙失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
