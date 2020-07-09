package com.kehui.t_h200.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kehui.t_h200.R;
import com.kehui.t_h200.app.App;
import com.kehui.t_h200.app.AppConfig;
import com.kehui.t_h200.app.Constants;
import com.kehui.t_h200.app.URLs;
import com.kehui.t_h200.base.BaseActivity;
import com.kehui.t_h200.bean.RequestBean;
import com.kehui.t_h200.bean.ResponseBean;
import com.kehui.t_h200.retrofit.APIService;
import com.kehui.t_h200.utils.DES3Utils;
import com.kehui.t_h200.utils.PrefUtils;
import com.kehui.t_h200.utils.Util;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 登录页面
 * @author 34238
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.tv_logo_text)
    TextView tvLogoText;
    @BindView(R.id.et_user_name)
    EditText etUserName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.cb_forget_password)
    CheckBox cbForgetPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.ll_input)
    LinearLayout llInput;

    public LoginActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        App.getInstances().switchLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "ch"));
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
//        initData();
        initView();

    }

    private void initData() {
        String userName = PrefUtils.getString(LoginActivity.this, AppConfig.myUserName, "");
        String password = PrefUtils.getString(LoginActivity.this, AppConfig.myPassword, "");
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            showMain();
        }
        if (!TextUtils.isEmpty(userName) && TextUtils.isEmpty(password)) {
            etUserName.setText(userName);
        }
    }

    private void initView() {
        //设置字体
        Typeface type = Typeface.createFromAsset(tvLogoText.getContext().getAssets(), "founderBlack.ttf");
        tvLogoText.setTypeface(type);
        /*cbForgetPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            }
        });
        cbForgetPassword.setChecked(true);*/
        llInput.setVisibility(View.GONE);
    }

    /**
     * 登录
     */
    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        /*btnLogin.setEnabled(false);
        if (TextUtils.isEmpty(etUserName.getText().toString().trim())) {
            Util.showToast(LoginActivity.this, getString(R.string.user_name_is_empty));
            btnLogin.setEnabled(true);
            return;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            Util.showToast(LoginActivity.this, getString(R.string.password_is_empty));
            btnLogin.setEnabled(true);
            return;
        }
        requestLoginApi(etUserName.getText().toString(), etPassword.getText().toString());*/
        //蓝牙搜索列表    //GC20200707
        Util.showToast(LoginActivity.this, getString(R.string.connecting_wait));
//        Intent intent = new Intent(LoginActivity.this, SeekDeviceActivity.class);
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this,SeekDeviceActivity.class);
        startActivity(intent);
        finish();
//        showMain();
    }

    /**
     * 显示主界面
     */
    private void showMain() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestLoginApi(final String userName, final String password) {
        RequestBean req = new RequestBean();
        req.UserName = userName;
        req.UserPwd = DES3Utils.encryptMode(App.keyBytes, password.getBytes());
        final Gson gson = new Gson();
        String json = gson.toJson(req);
        json = DES3Utils.encryptMode(App.keyBytes, json.getBytes());
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.AppUrl + URLs.AppPort)
                .addConverterFactory(ScalarsConverterFactory.create()).client(client)
                .build();
        APIService service = retrofit.create(APIService.class);
        Call<String> call = service.api("UserLogin", json);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> resp) {
                try {
                    byte[] srcBytes = DES3Utils.decryptMode(App.keyBytes, resp.body());
                    String result = new String(srcBytes);
                    ResponseBean responseBean = gson.fromJson(result, ResponseBean.class);
                    if (responseBean.Code.equals("1")) {//请求成功
                        handleLoginSuccess(userName, password);
                    } else {//失败
                        Util.showToast(LoginActivity.this, responseBean.Message);
                    }
                    btnLogin.setEnabled(true);

                } catch (Exception e) {
                    Log.e("打印-请求报异常-检查代码", "UserLogin");
                    btnLogin.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Util.showToast(LoginActivity.this, getString(R.string.check_net_retry));
                btnLogin.setEnabled(true);
            }
        });

    }

    private void handleLoginSuccess(String userName, String password) {
        PrefUtils.setString(LoginActivity.this, AppConfig.myUserName, userName);
        if (cbForgetPassword.isChecked()) {
            PrefUtils.setString(LoginActivity.this, AppConfig.myPassword, password);
        }
        //跳转主页面
        showMain();
    }
}
