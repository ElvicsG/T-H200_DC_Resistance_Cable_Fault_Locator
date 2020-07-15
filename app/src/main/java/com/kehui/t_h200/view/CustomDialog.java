package com.kehui.t_h200.view;

/**
 * Created by jwj on 2018/4/16.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.kehui.t_h200.R;

/**
 * 自定义Dialog弹窗
 * Created by zhuwentao on 2016-08-19.
 */
public class CustomDialog extends Dialog {

    /**
     * 提示
     */
    protected TextView hintTv;

    /**
     * 左边按钮
     */
    protected Button doubleLeftBtn;

    /**
     * 右边按钮
     */
    protected Button doubleRightBtn;
    private final WindowManager wm;

    public CustomDialog(Context context) {
        super(context, R.style.CustomDialogStyle);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
        hintTv = (TextView) findViewById(R.id.tv_notice_text);
        doubleLeftBtn = (Button) findViewById(R.id.btn_confirm);
        doubleRightBtn = (Button) findViewById(R.id.btn_cancel);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.65);

        win.setAttributes(lp);
        //禁止外部点击和返回按钮响应
        setCanceledOnTouchOutside(false);
        setCancelable(false);

    }

    /**
     * 设置右键文字和点击事件
     *
     * @param rightStr      文字
     * @param clickListener 点击事件
     */
    public void setRightButton(String rightStr, View.OnClickListener clickListener) {
        doubleRightBtn.setOnClickListener(clickListener);
        doubleRightBtn.setText(rightStr);
    }

    /**
     * 设置左键文字和点击事件
     *
     * @param leftStr       文字
     * @param clickListener 点击事件
     */
    public void setLeftButton(String leftStr, View.OnClickListener clickListener) {
        doubleLeftBtn.setOnClickListener(clickListener);
        doubleLeftBtn.setText(leftStr);
    }

    /**
     * 设置提示内容
     *
     * @param str 内容
     */
    public void setHintText(String str) {
        hintTv.setText(str);
        hintTv.setVisibility(View.VISIBLE);
    }

    /**
     * 给两个按钮 设置文字
     *
     * @param leftStr  左按钮文字
     * @param rightStr 右按钮文字
     */
    public void setBtnText(String leftStr, String rightStr) {
        doubleLeftBtn.setText(leftStr);
        doubleRightBtn.setText(rightStr);
    }
}
