package com.kehui.t_h200.bean;

import java.util.List;

/**
 * Created by jwj on 2018/4/16.
 * 语言信息bean
 */

public class LanguageBean {
    /**
     * Code : 1
     * Message : 操作成功
     * data : [{"RCID":"1","RCName":"简体中文","RCBiaoShi":"zh-Hans","RCSort":1,"InTime":"2018-04-10 10:13:38"},{"RCID":"2","RCName":"英语","RCBiaoShi":"en","RCSort":2,"InTime":"2018-04-10 10:13:38"},{"RC~~~~ID":"3","RCName":"德语","RCBiaoShi":"de","RCSort":3,"InTime":"2018-04-10 10:13:38"},{"RCID":"4","RCName":"法语","RCBiaoShi":"fr","RCSort":4,"InTime":"2018-04-10 10:13:38"},{"RCID":"5","RCName":"西班牙语","RCBiaoShi":"es","RCSort":5,"InTime":"2018-04-10 10:13:38"}]
     * action : AppReplyClassList
     */

    public String Code;
    public String Message;
    public String action;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * RCID : 1
         * RCName : 简体中文
         * RCBiaoShi : zh-Hans
         * RCSort : 1
         * InTime : 2018-04-10 10:13:38
         */

        public String RCID;
        public String RCName;
        public String RCBiaoShi;
        public int RCSort;
        public String InTime;

    }
}
