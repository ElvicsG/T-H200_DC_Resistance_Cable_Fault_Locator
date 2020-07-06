package com.kehui.t_h200.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.kehui.t_h200.R;
import com.kehui.t_h200.adapter.TextListAdapter;
import com.kehui.t_h200.app.App;
import com.kehui.t_h200.app.AppConfig;
import com.kehui.t_h200.app.Constants;
import com.kehui.t_h200.app.URLs;
import com.kehui.t_h200.base.BaseActivity;
import com.kehui.t_h200.bean.AssistDetailsBean;
import com.kehui.t_h200.bean.LanguageBean;
import com.kehui.t_h200.bean.LanguageContentBean;
import com.kehui.t_h200.bean.RequestBean;
import com.kehui.t_h200.bean.ResponseBean;
import com.kehui.t_h200.retrofit.APIService;
import com.kehui.t_h200.utils.DES3Utils;
import com.kehui.t_h200.utils.PrefUtils;
import com.kehui.t_h200.utils.Util;
import com.kehui.t_h200.view.CustomDialog;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 协助详情页面
 */
public class AssistDetailsActivity extends BaseActivity {

    @BindView(R.id.iv_title_logo)
    ImageView ivTitleLogo;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.tv_common_title)
    TextView commonTitleTv;

    @BindView(R.id.common_title_tb)
    Toolbar commonTitleTb;
    @BindView(R.id.tv_test_time)
    TextView tvTestTime;
    @BindView(R.id.tv_test_name)
    TextView tvTestName;
    @BindView(R.id.tv_test_position)
    TextView tvTestPosition;
    @BindView(R.id.tv_cable_length)
    TextView tvCableLength;
    @BindView(R.id.tv_cable_type)
    TextView tvCableType;
    @BindView(R.id.tv_fault_type)
    TextView tvFaultType;
    @BindView(R.id.tv_fault_length)
    TextView tvFaultLength;
    @BindView(R.id.tv_short_note_information)
    TextView tvShortNoteInformation;
    @BindView(R.id.btn_over_voice_magnetic_field_data)
    Button btnOverVoiceMagneticFieldData;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.rl_language_selection)
    RelativeLayout rlLanguageSelection;
    @BindView(R.id.tv_reply_content)
    TextView tvReplyContent;
    @BindView(R.id.ll_reply_content)
    LinearLayout llReplyContent;
    @BindView(R.id.btn_reply)
    Button btnReply;
    @BindView(R.id.ll_reply_view)
    LinearLayout llReplyView;
    @BindView(R.id.tv_reply_status)
    TextView tvReplyStatus;
    @BindView(R.id.tv_reply_content_2)
    TextView tvReplyContent2;
    @BindView(R.id.ll_reply_content_view)
    LinearLayout llReplyContentView;
    private String assistId = "";
    private List<String> languageList;//语言列表集合
    private List<String> languageIDList;//语言列表集合
    private List<String> languageContentList;//语言内容集合
    private PopupWindow languagePopupWindow;
    private PopupWindow languageContentPopupWindow;
    private View languageView;
    private View languageContentView;
    private TextListAdapter languageTextListAdapter;
    private TextListAdapter languageContentTextListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assist_details);
        ButterKnife.bind(this);
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        initToolbar();
        initData();
        initView();

    }

    private void initView() {
//        tvReplyContent.setMovementMethod(ScrollingMovementMethod.getInstance());//会影响点击事件
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            assistId = intent.getStringExtra("assistId");
        }
        languageList = new ArrayList<>();
        languageContentList = new ArrayList<>();
        languageIDList = new ArrayList<>();
        languageTextListAdapter = new TextListAdapter(languageList, AssistDetailsActivity.this);
        languageContentTextListAdapter = new TextListAdapter(languageContentList, AssistDetailsActivity.this);
        requestAssistDetails();
        requestLanguage();
        initLanguagePopupWindow();
        initLanguageContentPopupWindow();
    }

    /**
     * 请求语言分类
     */
    private void requestLanguage() {
        final Gson gson = new Gson();
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
        Call<String> call = service.api("AppReplyClassList", "");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    byte[] srcBytes = DES3Utils.decryptMode(App.keyBytes, response.body());
                    String result = new String(srcBytes);
                    LanguageBean languageBean = gson.fromJson(result, LanguageBean.class);
                    if (languageBean.Code.equals("1")) {
                        if (languageBean.data.size() > 0) {
                            languageList.clear();
                            List<String> tempList = new ArrayList<String>();
                            for (int i = 0; i < languageBean.data.size(); i++) {
                                tempList.add(languageBean.data.get(i).RCName);
                                languageIDList.add(languageBean.data.get(i).RCID);
                                if (PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "ch").equals(languageBean.data.get(i).RCBiaoShi)) {
                                    updateLanguageView(languageBean.data, i);
                                    requestLanguageContent(languageIDList.get(i));
                                }
                            }
                            languageList.addAll(tempList);
                            languageTextListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Util.showToast(AssistDetailsActivity.this, languageBean.Message);
                    }
                } catch (Exception e) {
                    Log.e("打印-请求报异常-检查代码", "AppInfoDetail");
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 修改语言显示数据
     *
     * @param data
     */
    private void updateLanguageView(List<LanguageBean.DataBean> data, int i) {
        tvLanguage.setText(data.get(i).RCName);
    }

    /**
     * 请求详情内容
     */
    private void requestAssistDetails() {
        final RequestBean requestBean = new RequestBean();
        requestBean.InfoID = assistId;
        final Gson gson = new Gson();
        String json = gson.toJson(requestBean);
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
        Call<String> call = service.api("AppInfoDetail", json);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    byte[] srcBytes = DES3Utils.decryptMode(App.keyBytes, response.body());
                    String result = new String(srcBytes);
                    AssistDetailsBean assistDetailsBean = gson.fromJson(result, AssistDetailsBean.class);
                    if (assistDetailsBean.Code.equals("1")) {
                        if (assistDetailsBean.data.size() > 0) {
                            updateView(assistDetailsBean.data);
                        }
                    } else {
                        Util.showToast(AssistDetailsActivity.this, assistDetailsBean.Message);
                    }
                } catch (Exception e) {
                    onFailure(call, e);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Util.showToast(AssistDetailsActivity.this, getString(R.string.check_net_retry));

            }
        });
    }

    /**
     * 修改详情显示内容
     *
     * @param data
     */
    private void updateView(List<AssistDetailsBean.DataBean> data) {
        if (data.get(0).ReplyStatus == 0) {//未回复
            llReplyView.setVisibility(View.VISIBLE);
            llReplyContentView.setVisibility(View.GONE);

        } else if (data.get(0).ReplyStatus == 1) {//已回复
            llReplyView.setVisibility(View.GONE);
            llReplyContentView.setVisibility(View.VISIBLE);
            tvReplyContent2.setText(data.get(0).ReplyContent.trim());
        }
        tvTestTime.setText(data.get(0).InfoTime.trim());
        tvTestName.setText(data.get(0).InfoUName.trim());
        tvTestPosition.setText(data.get(0).InfoAddress.trim());
        tvCableLength.setText(data.get(0).InfoLength + "".trim());
        tvCableType.setText(data.get(0).InfoLineType.trim());
        tvFaultType.setText(data.get(0).InfoFaultType.trim());
        tvFaultLength.setText(data.get(0).InfoFaultLength.trim());
        tvShortNoteInformation.setText(data.get(0).InfoMemo.trim());
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
        commonTitleTb.setPadding(0, getStatusBarHeight(AssistDetailsActivity.this), 0, 0);
        commonTitleTv.setText(R.string.assistance_details);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 显示语言选择弹窗
     */
    private void initLanguagePopupWindow() {
        languageView = LayoutInflater.from(AssistDetailsActivity.this).inflate(R.layout.layout_listview, null);
        final ListView lvText = (ListView) languageView.findViewById(R.id.lv_text);
        languagePopupWindow = new PopupWindow(languageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置以下代码，即背景颜色还有外部点击事件的处理才可以点击外部消失,
        languagePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        languagePopupWindow.setOutsideTouchable(true);
        languagePopupWindow.setWidth(screenWidth / 2);
        languagePopupWindow.setHeight(300);
        lvText.setAdapter(languageTextListAdapter);
        lvText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tvLanguage.setText(languageList.get(i));
                languagePopupWindow.dismiss();
                requestLanguageContent(languageIDList.get(i));
            }
        });
    }

    /**
     * 初始化语言内容弹窗
     */
    private void initLanguageContentPopupWindow() {
        languageContentView = LayoutInflater.from(AssistDetailsActivity.this).inflate(R.layout.layout_listview, null);
        final ListView lvText = (ListView) languageContentView.findViewById(R.id.lv_text);
        languageContentPopupWindow = new PopupWindow(languageContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置以下代码，即背景颜色还有外部点击事件的处理才可以点击外部消失,
        languageContentPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        languageContentPopupWindow.setOutsideTouchable(true);
        languageContentPopupWindow.setWidth(screenWidth - Util.dp2px(AssistDetailsActivity.this, 80));
        languageContentPopupWindow.setHeight(300);
        lvText.setAdapter(languageContentTextListAdapter);
        lvText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tvReplyContent.setText(languageContentList.get(i));
                languageContentPopupWindow.dismiss();
            }
        });
    }

    /**
     * 请求对应语言的内容
     *
     * @param Id
     */
    private void requestLanguageContent(String Id) {

        RequestBean requestBean = new RequestBean();
        requestBean.RCID = Id;
        final Gson gson = new Gson();
        String json = gson.toJson(requestBean);
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
        Call<String> call = service.api("AppReplyContentList", json);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    byte[] srcBytes = DES3Utils.decryptMode(App.keyBytes, response.body());
                    String result = new String(srcBytes);
                    LanguageContentBean languageContentBean = gson.fromJson(result, LanguageContentBean.class);
                    if (languageContentBean.Code.equals("1")) {
                        if (languageContentBean.data.size() > 0) {
                            updateLanguageContentView(languageContentBean.data);
                            languageContentList.clear();
                            List<String> tempList = new ArrayList<String>();
                            for (int i = 0; i < languageContentBean.data.size(); i++) {
                                tempList.add(languageContentBean.data.get(i).RContent);
                            }
                            languageContentList.addAll(tempList);
                            languageContentTextListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Util.showToast(AssistDetailsActivity.this, languageContentBean.Message);
                    }
                } catch (Exception e) {
                    onFailure(call, e);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 修改语言显示内容
     *
     * @param data
     */
    private void updateLanguageContentView(List<LanguageContentBean.DataBean> data) {
        tvReplyContent.setText(data.get(0).RContent);
    }

    /**
     * 显示选择语言弹窗
     */
    private void showLanguagePopupWindow() {
        languagePopupWindow.showAsDropDown(rlLanguageSelection);
    }

    /**
     * 显示语言内容弹窗
     */
    private void showLanguageContentPopupWindow() {
        languageContentPopupWindow.showAsDropDown(llReplyContent);
    }


    /**
     * view的点击事件
     *
     * @param view
     */
    @OnClick({R.id.btn_over_voice_magnetic_field_data, R.id.rl_language_selection, R.id.ll_reply_content, R.id.btn_reply})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //查看声音数据
            case R.id.btn_over_voice_magnetic_field_data:
                Intent intent = new Intent();
                intent.putExtra("infoId", assistId);
                intent.setClass(AssistDetailsActivity.this, ShowAssistInfoActivity.class);
                startActivity(intent);
                break;
            //点击选择语言
            case R.id.rl_language_selection:
                showLanguagePopupWindow();
                break;
            //点击选择回复内容
            case R.id.ll_reply_content:
                showLanguageContentPopupWindow();
                break;
            //点击回复
            case R.id.btn_reply:
                showDialog();
                break;
        }
    }

    /**
     * 显示CustomDialog
     */
    private void showDialog() {
        final CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        dialog.setHintText(getString(R.string.confirm_reply));
        dialog.setLeftButton(getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(100);
                uploadReply();
                dialog.dismiss();
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
     * 回复协助
     */
    private void uploadReply() {
        final RequestBean requestBean = new RequestBean();
        requestBean.InfoID = assistId;
        if (TextUtils.isEmpty(tvLanguage.getText().toString())) {
            Util.showToast(AssistDetailsActivity.this, getString(R.string.selection_language_is_empty));
            return;
        }
        if (TextUtils.isEmpty(tvReplyContent.getText().toString())) {
            Util.showToast(AssistDetailsActivity.this, getString(R.string.reply_content_is_empty));
            return;
        }
        requestBean.ReplyContent = tvReplyContent.getText().toString();
        if (TextUtils.isEmpty(PrefUtils.getString(AssistDetailsActivity.this, AppConfig.myUserName, ""))) {
            Util.showToast(AssistDetailsActivity.this, getString(R.string.user_name_is_not_empty));
            return;
        }
        requestBean.ReplyUser = PrefUtils.getString(AssistDetailsActivity.this, AppConfig.myUserName, "");
        requestBean.InfoYuYan = "";//待定
        final Gson gson = new Gson();
        String json = gson.toJson(requestBean);
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
        Call<String> call = service.api("AppInfoDetailReply", json);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    byte[] srcBytes = DES3Utils.decryptMode(App.keyBytes, response.body());
                    String result = new String(srcBytes);
                    ResponseBean responseBean = gson.fromJson(result, ResponseBean.class);
                    if (responseBean.Code.equals("1")) {
                        Util.showToast(AssistDetailsActivity.this, getString(R.string.reply_success));
                    } else {
                        Util.showToast(AssistDetailsActivity.this, responseBean.Message);
                    }
                } catch (Exception e) {
                    onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Util.showToast(AssistDetailsActivity.this, getString(R.string.check_net_retry));
            }
        });

    }


}
