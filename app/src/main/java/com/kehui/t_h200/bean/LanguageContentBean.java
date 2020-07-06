package com.kehui.t_h200.bean;

import java.util.List;

/**
 * Created by jwj on 2018/4/16.
 * 语言内容bean
 */

public class LanguageContentBean {

    /**
     * Code : 1
     * Message : 操作成功
     * data : [{"RID":"1","RCID":"1","RContent":"未检测到故障嘻嘻,请继续监测","RSort":1,"InUser":"张三","InTime":"2018-04-10 10:21:24"},{"RID":"2","RCID":"1","RContent":"接近故障点,请注意","RSort":2,"InUser":"李四","InTime":"2018-04-10 10:21:24"}]
     * action : AppReplyContentList
     */

    public String Code;
    public String Message;
    public String action;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * RID : 1
         * RCID : 1
         * RContent : 未检测到故障嘻嘻,请继续监测
         * RSort : 1
         * InUser : 张三
         * InTime : 2018-04-10 10:21:24
         */

        public String RID;
        public String RCID;
        public String RContent;
        public int RSort;
        public String InUser;
        public String InTime;
    }
}
