package com.kehui.t_h200.bean;

import java.util.List;

/**
 * Created by jwj on 2018/4/16.
 * 协助列表bean
 */

public class AssistListBean {
    public String Code;
    public String Message;
    public String action;
    public List<DataBean> data;

    public static class DataBean {
        public String InfoID;
        public String InfoUName;
        public String ReplyStatus;
        public String InTime;
        public String InfoTime;
    }
}
