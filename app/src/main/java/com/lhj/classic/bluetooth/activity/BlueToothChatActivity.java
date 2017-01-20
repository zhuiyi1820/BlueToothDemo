package com.lhj.classic.bluetooth.activity;

import android.bluetooth.BluetoothDevice;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lhj.classic.bluetooth.R;
import com.lhj.classic.bluetooth.adapter.BluetoothChatAdapter;
import com.lhj.classic.bluetooth.base.BaseActivity;
import com.lhj.classic.bluetooth.chat.BluetoothChatListener;
import com.lhj.classic.bluetooth.chat.ChatConnect;
import com.lhj.classic.bluetooth.chat.ChatState;
import com.lhj.classic.bluetooth.model.ChatItemBean;
import com.lhj.classic.bluetooth.model.EventBusEntity;
import com.lhj.classic.bluetooth.utils.ClsUtils;

import java.util.ArrayList;
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
 * 时间：2017/1/6 16:41
 * 描述：蓝牙聊天
 */
public class BlueToothChatActivity extends BaseActivity implements View.OnClickListener, BluetoothChatListener {

    ChatConnect bt;
    private ListView mListView;
    private EditText mEditText;
    private Button mButton_send;
    String mEditTextContent = "";
    private BluetoothChatAdapter mAdapter;
    private List<ChatItemBean> mdata = new ArrayList<ChatItemBean>();
    ;
    private ChatItemBean chatOut;
    private ChatItemBean chatIn;
    private BluetoothDevice device;
    private TextView title;
    private TextView right_tv;
    private RelativeLayout right;
    private RelativeLayout left;
    private TextView left_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth_chat);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onDeviceConnected(String name, String address) {
        Log.e("initListener", name + "-----" + address);
    }

    @Override
    public void onDeviceDisconnected() {
        Log.e("initListener", "onDeviceDisconnected");
    }

    @Override
    public void onDeviceConnectionFailed() {
        Log.e("initListener", "onDeviceConnectionFailed");
        Toast.makeText(BlueToothChatActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceStateChanged(String state) {
        Log.e("onServiceStateChanged", state);
        if (state.equals("1->2")) {
            Toast.makeText(BlueToothChatActivity.this, "正在连接", Toast.LENGTH_SHORT).show();
        }
        if (state.equals("2->3")) {
            Toast.makeText(BlueToothChatActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
        }
        if (state.equals("1->3")) {
            Toast.makeText(BlueToothChatActivity.this, "对方与你连接", Toast.LENGTH_SHORT).show();
        }
        if (state.equals("3->1")) {
            Toast.makeText(BlueToothChatActivity.this, "对方断开连接", Toast.LENGTH_SHORT).show();
        }
        if (state.equals("3->0")) {
            Toast.makeText(BlueToothChatActivity.this, "主动断开连接", Toast.LENGTH_SHORT).show();
        }
        if (state.equals("1->0")) {
            Toast.makeText(BlueToothChatActivity.this, "退出连接", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 接受对方消息
     *
     * @param data
     * @param message
     */
    @Override
    public void onDataReceived(byte[] data, String message) {

        if (!message.equals("")) {
            chatIn = new ChatItemBean();
            chatIn.setType(0);
            chatIn.setIcon(BitmapFactory.decodeResource(getResources(),
                    R.mipmap.in_icon));
            chatIn.setText(message);
            mdata.add(chatIn);
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(mdata.size() - 1);
        }

    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        bt.setBluetoothChatListener(this);
        mButton_send.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        device = getIntent().getParcelableExtra("device");
        bt = new ChatConnect(this, adapter);
        title.setText(device.getName());
        right_tv.setText("手动连接");
        left_tv.setText("返回");
        mAdapter = new BluetoothChatAdapter(this, mdata);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mdata.size() - 1);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        title = (TextView) findViewById(R.id.base_title);
        right = (RelativeLayout) findViewById(R.id.base_right_rl);
        right_tv = (TextView) findViewById(R.id.base_right_tv);
        left = (RelativeLayout) findViewById(R.id.base_left_rl);
        left_tv = (TextView) findViewById(R.id.base_left_tv);
        mListView = (ListView) findViewById(R.id.listView_chat);
        mEditText = (EditText) findViewById(R.id.mEditText);
        mButton_send = (Button) findViewById(R.id.mButton_send);
    }

    /**
     * 连接通信
     */
    private void connect() {
        if (BluetoothDevice.BOND_BONDED == device.getBondState()) {//已配对，未对接上，重新连接
            bt.connect(device.getAddress());
        } else {
            try {
                ClsUtils.createBond(device.getClass(), device);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "与" + title.getText().toString().trim() + "设备连接失败", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * 发送消息
     */
    private void send() {
//        Toast.makeText(this, "" + bt.getServiceState(), Toast.LENGTH_SHORT).show();
        mEditTextContent = mEditText.getText().toString();
        if (mEditTextContent.equals("")) {
            Toast.makeText(BlueToothChatActivity.this, "请输入你想发送的内容", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bt.getServiceState() == ChatState.STATE_CONNECTED) {
            chatOut = new ChatItemBean();
            chatOut.setType(1);
            chatOut.setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            chatOut.setText(mEditTextContent);
            mdata.add(chatOut);
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(mdata.size() - 1);
            mEditText.setText("");
            bt.send(mEditTextContent);
        } else {
            Toast.makeText(BlueToothChatActivity.this, "发送失败，设备未对接", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!bt.isServiceAvailable()) {
            bt.setupService();
            bt.startService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mButton_send:
                send();
                break;
            case R.id.base_left_rl:
                finish();
                break;
            case R.id.base_right_rl:
                if (!openBluetooth()) {
                    return;
                }
                if (bt.getServiceState() == ChatState.STATE_CONNECTED) {
                    Toast.makeText(this, "已连接上", Toast.LENGTH_SHORT).show();
                    return;
                }
                connect();
                break;
        }
    }


}
