package com.kehui.t_h200.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.kehui.t_h200.R;
import com.kehui.t_h200.app.AppConfig;
import com.kehui.t_h200.base.BaseActivity;
import com.kehui.t_h200.utils.PrefUtils;
import com.kehui.t_h200.view.CustomDialog;

/**
 * 设置页面
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.iv_title_logo)
    ImageView ivTitleLogo;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.tv_common_title)
    TextView commonTitleTv;

    @BindView(R.id.common_title_tb)
    Toolbar commonTitleTb;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.tv_check_new_version)
    TextView tvCheckNewVersion;
    @BindView(R.id.tv_about_us)
    TextView tvAboutUs;
    public static SettingActivity instance;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        App.getInstances().switchLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "ch"));
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        instance = this;
        initToolbar();
    }

    /**
     * 初始化标题栏
     */

    private void initToolbar() {
//        ivSetting.setVisibility(View.GONE);
        ivTitleLogo.setVisibility(View.GONE);
        llBack.setVisibility(View.VISIBLE);
        setSupportActionBar(commonTitleTb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        commonTitleTb.setPadding(0, getStatusBarHeight(SettingActivity.this), 0, 0);
        commonTitleTv.setText(R.string.settings);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick({R.id.tv_language, R.id.tv_check_new_version, R.id.tv_about_us,R.id.tv_login})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv_language:
                intent.setClass(SettingActivity.this, SwitchLanguageActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_check_new_version:
                MainActivity.instance.isToast = true;
                MainActivity.instance.isCheckOver = true;
                MainActivity.instance.initUpdateApk();
                break;
            case R.id.tv_about_us:
                intent.setClass(SettingActivity.this, AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_login:
                intent.setClass(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

                MainActivity.instance.finish();
                PrefUtils.setString(SettingActivity.this, AppConfig.myUserName, "");
                PrefUtils.setString(SettingActivity.this, AppConfig.myPassword, "");
                break;
        }
    }

    /**
     * 弹出对话框，提示用户更新
     */
    public void showUpdateDialog() {
        customDialog = new CustomDialog(SettingActivity.this);
        customDialog.show();

        customDialog.setHintText(getString(R.string.version_update));
        customDialog.setLeftButton(getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isCancel = true;
                // 下载apk，，apk链接地址，downloadUrl
                MainActivity.DownloadApk(SettingActivity.this);
                customDialog.dismiss();
            }
        });
        customDialog.setRightButton(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
    }


}
