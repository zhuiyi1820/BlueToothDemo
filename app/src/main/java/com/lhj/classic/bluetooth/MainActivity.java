package com.lhj.classic.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lhj.classic.bluetooth.ble.activity.BleBuletoothActivity;
import com.lhj.classic.bluetooth.cls.activity.ClassicBluetoothActivity;

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
 * 时间：2017/1/9 15:26
 * 描述：
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.come_classic).setOnClickListener(this);
        findViewById(R.id.come_ble).setOnClickListener(this);
    }

    public boolean classicbluebooth() {
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public boolean blebluebooth() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "该设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (!classicbluebooth()) return;
        switch (v.getId()) {
            case R.id.come_classic:
                startActivity(new Intent(MainActivity.this, ClassicBluetoothActivity.class));
                break;
            case R.id.come_ble:
                if (!blebluebooth()) return;
                startActivity(new Intent(MainActivity.this, BleBuletoothActivity.class));
                break;

        }
    }
}
