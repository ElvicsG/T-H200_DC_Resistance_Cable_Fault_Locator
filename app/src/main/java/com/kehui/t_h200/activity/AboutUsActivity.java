package com.kehui.t_h200.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.kehui.t_h200.R;
import com.kehui.t_h200.base.BaseActivity;

/**
 * 关于我们页面
 */
public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.iv_title_logo)
    ImageView ivTitleLogo;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.tv_common_title)
    TextView commonTitleTv;
    @BindView(R.id.common_title_tb)
    Toolbar commonTitleTb;
    @BindView(R.id.tv_version)
    TextView tvVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        App.getInstances().switchLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "ch"));
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initToolbar();
        getAppVersion();
        initView();
    }

    private void initView() {
        tvVersion.setText("V" + getAppVersion());
    }

    private String getAppVersion() {

        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;

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
        commonTitleTb.setPadding(0, getStatusBarHeight(AboutUsActivity.this), 0, 0);
        commonTitleTv.setText(R.string.about_us);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
