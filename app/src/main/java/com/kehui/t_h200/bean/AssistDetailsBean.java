package com.kehui.t_h200.bean;

import java.util.List;

/**
 * Created by jwj on 2018/4/16.
 * 协助详情bean对象
 */

public class AssistDetailsBean {
    /**
     * Code : 1
     * Message : 操作成功
     * data : [{"InfoID":"1","InfoTime":"2018-04-09 11:14:02","InfoUName":"张三","InfoAddress":"淄博市张店区","InfoLength":12.5,"InfoLineType":"类型1","InfoFaultType":"错误类型1","InfoFaultLength":"1.4","InfoMemo":"","ReplyContent":"OK","ReplyStatus":1}]
     * action : AppInfoDetail
     */

    public String Code;
    public String Message;
    public String action;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * InfoID : 1
         * InfoTime : 2018-04-09 11:14:02
         * InfoUName : 张三
         * InfoAddress : 淄博市张店区
         * InfoLength : 12.5
         * InfoLineType : 类型1
         * InfoFaultType : 错误类型1
         * InfoFaultLength : 1.4
         * InfoMemo :
         * ReplyContent : OK
         * ReplyStatus : 1
         */

        public String InfoID;
        public String InfoTime;
        public String InfoUName;
        public String InfoAddress;
        public String InfoLength;
        public String InfoLineType;
        public String InfoFaultType;
        public String InfoFaultLength;
        public String InfoMemo;
        public String ReplyContent;
        public int ReplyStatus;
    }
}
