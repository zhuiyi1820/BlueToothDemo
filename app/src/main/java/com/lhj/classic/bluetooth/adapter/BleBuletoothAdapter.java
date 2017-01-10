package com.lhj.classic.bluetooth.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lhj.classic.bluetooth.R;
import com.lhj.classic.bluetooth.model.IbeaconBean;

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
 * 时间：2016/10/31 16:08
 * 描述：扫描蓝牙适配器
 */
public class BleBuletoothAdapter extends BaseAdapter {
    private ArrayList<IbeaconBean> mainList;
    Context context;
    BleSignListener sl;

    public BleBuletoothAdapter(Context context, ArrayList<IbeaconBean> mainList, BleSignListener sl) {
        this.context = context;
        this.mainList = mainList;
        this.sl = sl;
    }

    @Override
    public int getCount() {
        return mainList.size();
    }

    @Override
    public Object getItem(int position) {
        return mainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = convertView.inflate(context, R.layout.item_ble, null);
            holder = new ViewHolder();
            holder.uuid = (TextView) convertView.findViewById(R.id.uuid);//厂商识别号
            holder.major = (TextView) convertView.findViewById(R.id.major);//相当于群组号，同一个组里Beacon有相同的Major
            holder.minor = (TextView) convertView.findViewById(R.id.minor);//相当于识别群组里单个的Beacon
//            UUID+Major+Minor就构成了一个Beacon的识别号，有点类似于网络中的IP地址。TX Power用于测距，iBeacon目前只定义了大概的3个粗略级别：
//            非常近（Immediate）: 大概10厘米内
//            近（Near）:1米内
//            远（Far）:1米外
            holder.txPower = (TextView) convertView.findViewById(R.id.txPower);//用于测量设备离Beacon的距离
            holder.mac = (TextView) convertView.findViewById(R.id.mac);//mac地址
            holder.distance = (TextView) convertView.findViewById(R.id.distance);//距离
            holder.rssi = (TextView) convertView.findViewById(R.id.rssi);//信号强度
            holder.item_btn = (TextView) convertView.findViewById(R.id.item_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final IbeaconBean device = mainList.get(position);
        holder.uuid.setText(device.getProximityUuid());
        holder.major.setText(device.getMajor() + "");
        holder.minor.setText(device.getMinor() + "");
        holder.mac.setText(device.getBluetoothAddress());
        holder.distance.setText(device.getDistance() + "米");
        holder.rssi.setText(device.getRssi() + "");
        holder.txPower.setText(device.getTxPower() + "");
        holder.item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sl.signOperation(device);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView uuid;
        TextView major;
        TextView minor;
        TextView mac;
        TextView distance;
        TextView rssi;
        TextView txPower;
        TextView item_btn;
    }

    public interface BleSignListener {

        void signOperation(IbeaconBean device);

    }
}
