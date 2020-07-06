package com.kehui.t_h200.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.kehui.t_h200.R;
import com.kehui.t_h200.app.AppConfig;
import com.kehui.t_h200.base.BaseActivity;
import com.kehui.t_h200.receiver.RestartAppReceiver;
import com.kehui.t_h200.utils.MultiLanguageUtil;
import com.kehui.t_h200.utils.PrefUtils;
import com.kehui.t_h200.view.CustomDialog;

/**
 * 切换语言页面
 */
public class SwitchLanguageActivity extends BaseActivity {

    @BindView(R.id.iv_title_logo)
    ImageView ivTitleLogo;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.tv_common_title)
    TextView commonTitleTv;
    //    @BindView(R.id.iv_setting)
//    ImageView ivSetting;
    @BindView(R.id.common_title_tb)
    Toolbar commonTitleTb;
    @BindView(R.id.rb_zh)
    RadioButton rbZh;
    @BindView(R.id.rb_en)
    RadioButton rbEn;
    @BindView(R.id.rb_de)
    RadioButton rbDe;
    @BindView(R.id.rb_fr)
    RadioButton rbFr;
    @BindView(R.id.rb_spain)
    RadioButton rbSpain;
    @BindView(R.id.rg_language)
    RadioGroup rgLanguage;
    @BindView(R.id.btn_confirm_switch_language)
    Button btnConfirmSwitchLanguage;
    @BindView(R.id.rb_follow_sys)
    RadioButton rbFollowSys;
    private String languageType = "ch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        App.getInstances().switchLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "ch"));
        setContentView(R.layout.activity_switch_language);
        ButterKnife.bind(this);
        initToolbar();
        initView();
    }

    private void initView() {
        languageType = PrefUtils.getString(SwitchLanguageActivity.this, AppConfig.CURRENT_LANGUAGE, "follow_sys");
        if (languageType.equals("follow_sys")) {
            rbFollowSys.setChecked(true);
        } else if (languageType.equals("ch")) {
            rbZh.setChecked(true);
        } else if (languageType.equals("en")) {
            rbEn.setChecked(true);
        } else if (languageType.equals("de")) {
            rbDe.setChecked(true);
        } else if (languageType.equals("fr")) {
            rbFr.setChecked(true);
        } else if (languageType.equals("es")) {
            rbSpain.setChecked(true);
        }
        rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId == rbFollowSys.getId()) {
                    languageType = "follow_sys";
                } else if (checkId == rbZh.getId()) {
                    languageType = "ch";
                } else if (checkId == rbEn.getId()) {
                    languageType = "en";
                } else if (checkId == rbDe.getId()) {
                    languageType = "de";
                } else if (checkId == rbFr.getId()) {
                    languageType = "fr";
                } else if (checkId == rbSpain.getId()) {
                    languageType = "es";
                }

            }
        });
    }

    /**
     * 显示CustomDialog
     */
    private void showDialog(final String languageType) {
        final CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        dialog.setHintText(getString(R.string.switch_language_success_notice));
        dialog.setLeftButton(getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (languageType.equals("follow_sys")) {
                    MultiLanguageUtil.getInstance().updateLanguage("follow_sys");
                } else if (languageType.equals("ch")) {
//                    App.getInstances().switchLanguage("ch");
                    MultiLanguageUtil.getInstance().updateLanguage("ch");

                } else if (languageType.equals("en")) {
//                    App.getInstances().switchLanguage("en");
                    MultiLanguageUtil.getInstance().updateLanguage("en");
                } else if (languageType.equals("de")) {
//                    App.getInstances().switchLanguage("de");
                    MultiLanguageUtil.getInstance().updateLanguage("de");
                } else if (languageType.equals("fr")) {
//                    App.getInstances().switchLanguage("fr");
                    MultiLanguageUtil.getInstance().updateLanguage("fr");
                } else if (languageType.equals("es")) {
//                    App.getInstances().switchLanguage("es");
                    MultiLanguageUtil.getInstance().updateLanguage("es");
                }
                dialog.dismiss();
                finish();
                SettingActivity.instance.finish();
                Intent intent = new Intent(SwitchLanguageActivity.this, RestartAppReceiver.class);
                intent.setPackage("inno.com.kehuiphoneapp");
                intent.setAction("restartapp");
                sendBroadcast(intent);


            }
        });
        dialog.setRightButton(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
        commonTitleTb.setPadding(0, getStatusBarHeight(SwitchLanguageActivity.this), 0, 0);
        commonTitleTv.setText(R.string.language);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick(R.id.btn_confirm_switch_language)
    public void onViewClicked() {
        if (!PrefUtils.getString(SwitchLanguageActivity.this, AppConfig.CURRENT_LANGUAGE, "follow_sys").equals(languageType)) {
            showDialog(languageType);
        }
    }
}
