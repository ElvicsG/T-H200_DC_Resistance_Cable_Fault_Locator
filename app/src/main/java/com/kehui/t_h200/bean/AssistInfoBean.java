package com.kehui.t_h200.bean;

import java.util.List;

/**
 * Created by jwj on 2018/4/18.
 * 磁场声音信息bean
 */

public class AssistInfoBean {
    /**
     * Code : 1
     * Message : 操作成功
     * data : [{"InfoCiChang":"xxxxxx","InfoShengYin":"xxxxx"}]
     * action : AppInfoBinary
     */

    public String Code;
    public String Message;
    public String action;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * InfoCiChang : xxxxxx
         * InfoShengYin : xxxxx
         */

        public String InfoCiChang;
        public String InfoShengYin;
        public String InfoCiCangVol;
        public String InfoShengYinVol;
        public String InfoLvBo;
    }
}
