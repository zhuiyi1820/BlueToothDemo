/*
 * Copyright 2014 Akexorcist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.lhj.classic.bluetooth.cls.chat;

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
 * 描述：蓝牙聊天状态常量等
 */
public class ChatState {
    /**
     * 未连接
     */
    public static final int STATE_NONE = 0;
    /**
     * 准备连接
     */
    public static final int STATE_LISTEN = 1;
    /**
     * 连接中
     */
    public static final int STATE_CONNECTING = 2;
    /**
     * 已连接
     */
    public static final int STATE_CONNECTED = 3;

    /**
     * 连接异常
     */
    public static final int STATE_NULL = -1;

    /**
     *
     */
    public static final int MESSAGE_STATE_CHANGE = 1;
    /**
     * 读取
     */
    public static final int MESSAGE_READ = 2;
    /**
     * 写入
     */
    public static final int MESSAGE_WRITE = 3;
    /**
     * 设备信息
     */
    public static final int MESSAGE_DEVICE_INFO = 4;

    /**
     * 设备名
     */
    public static final String DEVICE_NAME = "device_name";
    /**
     * 设备地址
     */
    public static final String DEVICE_ADDRESS = "device_address";

    /**
     * Android设备
     */
    public static final boolean DEVICE_ANDROID = true;
    /**
     * 其他设备(暂无用)
     */
    public static final boolean DEVICE_OTHER = false;

}
