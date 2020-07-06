package com.kehui.t_h200.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kehui.t_h200.R;
import com.kehui.t_h200.adapter.AssistListAdapter;
import com.kehui.t_h200.app.App;
import com.kehui.t_h200.app.Constants;
import com.kehui.t_h200.app.URLs;
import com.kehui.t_h200.base.BaseActivity;
import com.kehui.t_h200.bean.AssistListBean;
import com.kehui.t_h200.bean.RequestBean;
import com.kehui.t_h200.retrofit.APIService;
import com.kehui.t_h200.utils.DES3Utils;
import com.kehui.t_h200.utils.DateUtils;
import com.kehui.t_h200.utils.Util;
import com.kehui.t_h200.view.CustomDialog;
import com.kehui.t_h200.view.HorizontalProgressDialog;
import com.kehui.t_h200.view.LinearSpacingItemDecoration;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
 * 协助列表页面
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.iv_title_logo)
    ImageView ivTitleLogo;
    @BindView(R.id.tv_common_title)
    TextView commonTitleTv;
    @BindView(R.id.common_title_tb)
    Toolbar commonTitleTb;
    @BindView(R.id.ll_report_time)
    LinearLayout llReportTime;
    @BindView(R.id.ll_reply_content)
    LinearLayout llReplyContent;
    @BindView(R.id.ll_other_filter)
    LinearLayout llOtherFilter;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.ll_shadow)
    LinearLayout llShadow;
    @BindView(R.id.view_bg)
    View viewBg;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.iv_exception)
    ImageView ivException;
    @BindView(R.id.ll_exception)
    LinearLayout llException;
    @BindView(R.id.tv_exception)
    TextView tvException;
    /**
     * T-F200UI布局
     */
    @BindView(R.id.ll_container)
    LinearLayout llContainer;
    @BindView(R.id.ll_home)
    LinearLayout llHome;
    @BindView(R.id.ll_sheath)
    LinearLayout llSheath;
    @BindView(R.id.ll_main)
    LinearLayout llMain;
    @BindView(R.id.iv_sheath)
    ImageView ivSheath;
    @BindView(R.id.iv_main)
    ImageView ivMain;
    @BindView(R.id.ll_sheath_picture)
    LinearLayout llSheathPicture;
    @BindView(R.id.ll_sheath_test)
    LinearLayout llSheathTest;
    @BindView(R.id.ll_main_picture)
    LinearLayout llMainPicture;
    @BindView(R.id.ll_main_test)
    LinearLayout llMainTest;
    @BindView(R.id.tv_sheath_title)
    TextView tvSheathTitle;
    @BindView(R.id.tv_main_title)
    TextView tvMainTitle;
    @BindView(R.id.tv_sheath_attention2)
    TextView tvSheathAttention2;
    @BindView(R.id.tv_sheath_attention3)
    TextView tvSheathAttention3;
    @BindView(R.id.tv_main_attention2)
    TextView tvMainAttention2;
    @BindView(R.id.rl_sheath_test_resistance)
    RelativeLayout rlSheathTestResistance;
    @BindView(R.id.rl_sheath_distance_percentage)
    RelativeLayout rlSheathDistancePercentage;
    @BindView(R.id.rl_sheath_test_resistivity)
    RelativeLayout rlSheathTestResistivity;
    @BindView(R.id.rl_sheath_fault_location)
    RelativeLayout rlSheathFaultLocation;
    @BindView(R.id.rl_sheath_test_resistance2)
    RelativeLayout rlSheathTestResistance2;
    @BindView(R.id.rl_sheath_distance_percentage2)
    RelativeLayout rlSheathDistancePercentage2;
    @BindView(R.id.rl_sheath_test_resistivity2)
    RelativeLayout rlSheathTestResistivity2;
    @BindView(R.id.rl_sheath_fault_location2)
    RelativeLayout rlSheathFaultLocation2;
    @BindView(R.id.rl_main_test_resistance)
    RelativeLayout rlMainTestResistance;
    @BindView(R.id.rl_main_distance_percentage)
    RelativeLayout rlMainDistancePercentage;
    @BindView(R.id.rl_main_test_resistivity)
    RelativeLayout rlMainTestResistivity;
    @BindView(R.id.rl_main_fault_location)
    RelativeLayout rlMainFaultLocation;
    @BindView(R.id.rl_main_test_resistance2)
    RelativeLayout rlMainTestResistance2;
    @BindView(R.id.rl_main_distance_percentage2)
    RelativeLayout rlMainDistancePercentage2;
    @BindView(R.id.rl_main_test_resistivity2)
    RelativeLayout rlMainTestResistivity2;
    @BindView(R.id.rl_main_fault_location2)
    RelativeLayout rlMainFaultLocation2;
    @BindView(R.id.tv_sheath)
    TextView tvSheath;
    @BindView(R.id.tv_main)
    TextView tvMain;
    @BindView(R.id.tv_sheath_calculate)
    TextView tvSheathCalculate;
    @BindView(R.id.tv_main_calculate)
    TextView tvMainCalculate;


    private PopupWindow replyPopupWindow;
    private View replyView;//回复内容弹窗view
    /**
     * 回复内容控件
     */
    private RadioButton rbAll;
    private RadioButton rbReplied;
    private RadioButton rbUnReply;
    private RadioGroup rgReply;
    /**
     * -----------------------
     */

    private PopupWindow datePopupWindow;
    private View dateView;//选择日期弹窗view
    public TextView etStartTime;
    public TextView etEndTime;
    public Button btnClearData;
    private LinearLayoutManager linearLayoutManager;

    public int page = 1;//代表请求第几页
    private List<AssistListBean.DataBean> assistList;
    private AssistListAdapter assistListAdapter;
    private boolean isRequest;//判断是否还有下拉数据

    private static String startTime = ""; //查询的开始时间
    private static String endTime = ""; //结束时间
    public String replyStatus = "2"; //回复状态
    public static int currentSettingTime = 0; //标记当前设置的是开始时间还是结束时间 0是开始 1是结束
    public static MainActivity instance;
    public boolean isToast = false;
    private CustomDialog customDialog;

    private int pageCoding = 1;
    private boolean modeState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        ButterKnife.bind(this);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //初始化
        llContainer.setVisibility(View.GONE);
        srlRefresh.setVisibility(View.GONE);
        llException.setVisibility(View.GONE);
        llHome.setVisibility(View.VISIBLE);
        llSheath.setVisibility(View.GONE);
        llMain.setVisibility(View.GONE);

        //检测版本更新
        initUpdateApk();
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        initToolbar();
        initReplyPopupWindow();
        initDatePopupWindow();
        initDate();
        initView();

    }

    private void initDate() {
        assistList = new ArrayList<>();
        assistListAdapter = new AssistListAdapter(MainActivity.this, assistList);
        rvList.setAdapter(assistListAdapter);
        page = 1;
        srlRefresh.setRefreshing(true);
        request(page + "", startTime, endTime, replyStatus);
    }

    private void initView() {
        srlRefresh.setColorSchemeResources(R.color.main_color);
        srlRefresh.setDistanceToTriggerSync(0);
        srlRefresh.setProgressBackgroundColor(R.color.white); // 设定下拉圆圈的背景
        srlRefresh.setSize(2); // 设置圆圈的大小
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setItemAnimator(new DefaultItemAnimator());
        rvList.setLayoutManager(linearLayoutManager);
        rvList.addItemDecoration(new LinearSpacingItemDecoration(1, getResources().getColor(R.color.gray3)));

        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                request(page + "", startTime, endTime, replyStatus);
            }
        });
        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == assistListAdapter.getItemCount()) {
                    //设置正在加载更多
                    assistListAdapter.changeMoreStatus(assistListAdapter.LOADING_MORE);
                    if (isRequest) {
                        page++;
                        request(page + "", startTime, endTime, replyStatus);
                    } else {
                        assistListAdapter.changeMoreStatus(assistListAdapter.NO_LOAD_MORE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSettingTime = 0;
                showDatePickerDialog(MainActivity.this, DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, etStartTime, Calendar.getInstance());
            }
        });
        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSettingTime = 1;
                showDatePickerDialog(MainActivity.this, DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, etEndTime, Calendar.getInstance());

            }
        });
    }

    @Override
    protected void onDestroy() {
        currentSettingTime = 0;
        startTime = "";
        endTime = "";
        super.onDestroy();
    }

    /**
     * 初始化标题栏
     */

    private void initToolbar() {
//        ivSetting.setVisibility(View.VISIBLE);
        ivTitleLogo.setVisibility(View.VISIBLE);
        setSupportActionBar(commonTitleTb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        commonTitleTb.setPadding(0, getStatusBarHeight(MainActivity.this), 0, 0);
        commonTitleTv.setText(R.string.app_number);

    }

    @OnClick({R.id.ll_report_time, R.id.ll_reply_content, R.id.ll_other_filter, R.id.tv_cable_sheath, R.id.tv_cable_main,
            R.id.btn_sheath_previous, R.id.btn_sheath_next, R.id.btn_main_previous, R.id.btn_main_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_report_time:
                showDateDialog();
                break;
            case R.id.ll_reply_content:
                showReplyStatusDialog();
                break;
            case R.id.ll_other_filter:
                break;
            case R.id.tv_cable_sheath:
                //护层测试界面
                modeState = false;
                commonTitleTv.setText(R.string.btn_sheath);
                llContainer.setVisibility(View.GONE);
                srlRefresh.setVisibility(View.GONE);
                llException.setVisibility(View.GONE);
                llHome.setVisibility(View.GONE);
                llSheath.setVisibility(View.VISIBLE);
                llSheathTest.setVisibility(View.GONE);
                llMain.setVisibility(View.GONE);
                pageCoding = 1;
                switchInterface();
                break;
            case R.id.tv_cable_main:
                //主绝缘测试界面
                modeState = true;
                commonTitleTv.setText(R.string.btn_main);
                llContainer.setVisibility(View.GONE);
                srlRefresh.setVisibility(View.GONE);
                llException.setVisibility(View.GONE);
                llHome.setVisibility(View.GONE);
                llSheath.setVisibility(View.GONE);
                llMain.setVisibility(View.VISIBLE);
                llMainTest.setVisibility(View.GONE);
                pageCoding = 1;
                switchInterface();
                break;
            case R.id.btn_sheath_previous:
                if (pageCoding > 0) {
                    pageCoding--;
                    switchInterface();
                }
                break;
            case R.id.btn_sheath_next:
                if (pageCoding < 4) {
                    pageCoding++;
                    switchInterface();
                }
                break;
            case R.id.btn_main_previous:
                if (pageCoding > 0) {
                    pageCoding--;
                    switchInterface();
                }
                break;
            case R.id.btn_main_next:
                if (pageCoding < 4) {
                    pageCoding++;
                    switchInterface();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 界面显示切换
     */
    private void switchInterface() {
        switch (pageCoding) {
            case 0:
                //显示主页界面
                commonTitleTv.setText(R.string.app_number);
                llHome.setVisibility(View.VISIBLE);
                if (!modeState) {
                    llSheath.setVisibility(View.GONE);
                } else {
                    llMain.setVisibility(View.GONE);
                }
                break;
            case 1:
                //从主页进入 图片——测试完好相或护层
                if (!modeState) {
                    llSheathPicture.setVisibility(View.VISIBLE);
                    llSheathTest.setVisibility(View.GONE);
                    ivSheath.setImageResource(R.drawable.ic_sheath1);
                    tvSheath.setText(R.string.line_sheath);
                } else {
                    llMainPicture.setVisibility(View.VISIBLE);
                    llMainTest.setVisibility(View.GONE);
                    ivMain.setImageResource(R.drawable.ic_main1);
                    tvMain.setText(R.string.line_main);
                }
                break;
            case 2:
                //第一次点击下一页 显示测量电阻率
                if (!modeState) {
                    llSheathPicture.setVisibility(View.GONE);
                    llSheathTest.setVisibility(View.VISIBLE);
                    tvSheathTitle.setText(R.string.measure_resistivity);
                    tvSheathAttention2.setVisibility(View.VISIBLE);
                    tvSheathAttention3.setVisibility(View.VISIBLE);
                    rlSheathTestResistance.setVisibility(View.VISIBLE);
                    rlSheathTestResistance2.setVisibility(View.VISIBLE);
                    rlSheathTestResistivity.setVisibility(View.VISIBLE);
                    rlSheathTestResistivity2.setVisibility(View.VISIBLE);
                    rlSheathDistancePercentage.setVisibility(View.GONE);
                    rlSheathDistancePercentage2.setVisibility(View.GONE);
                    rlSheathFaultLocation.setVisibility(View.GONE);
                    rlSheathFaultLocation2.setVisibility(View.GONE);
                    tvSheathCalculate.setText(R.string.btn_calculate_resistivity);
                } else {
                    llMainPicture.setVisibility(View.GONE);
                    llMainTest.setVisibility(View.VISIBLE);
                    tvMainTitle.setText(R.string.measure_resistivity);
                    tvMainAttention2.setVisibility(View.VISIBLE);
                    rlMainTestResistance.setVisibility(View.VISIBLE);
                    rlMainTestResistance2.setVisibility(View.VISIBLE);
                    rlMainTestResistivity.setVisibility(View.VISIBLE);
                    rlMainTestResistivity2.setVisibility(View.VISIBLE);
                    rlMainDistancePercentage.setVisibility(View.GONE);
                    rlMainDistancePercentage2.setVisibility(View.GONE);
                    rlMainFaultLocation.setVisibility(View.GONE);
                    rlMainFaultLocation2.setVisibility(View.GONE);
                    tvMainCalculate.setText(R.string.btn_calculate_resistivity);
                }
                break;
            case 3:
                //第二次点击 图片——测试故障相或护层
                if (!modeState) {
                    llSheathPicture.setVisibility(View.VISIBLE);
                    llSheathTest.setVisibility(View.GONE);
                    ivSheath.setImageResource(R.drawable.ic_sheath2);
                    tvSheath.setText(R.string.line_sheath2);
                } else {
                    llMainPicture.setVisibility(View.VISIBLE);
                    llMainTest.setVisibility(View.GONE);
                    ivMain.setImageResource(R.drawable.ic_main2);
                    tvMain.setText(R.string.line_main2);
                }
                break;
            case 4:
                //第三次点击 给出故障距离
                if (!modeState) {
                    llSheathPicture.setVisibility(View.GONE);
                    llSheathTest.setVisibility(View.VISIBLE);
                    tvSheathTitle.setText(R.string.fault_location);
                    tvSheathAttention2.setVisibility(View.GONE);
                    tvSheathAttention3.setVisibility(View.GONE);
                    rlSheathTestResistance.setVisibility(View.GONE);
                    rlSheathTestResistance2.setVisibility(View.GONE);
                    rlSheathTestResistivity.setVisibility(View.GONE);
                    rlSheathTestResistivity2.setVisibility(View.GONE);
                    rlSheathDistancePercentage.setVisibility(View.VISIBLE);
                    rlSheathDistancePercentage2.setVisibility(View.VISIBLE);
                    rlSheathFaultLocation.setVisibility(View.VISIBLE);
                    rlSheathFaultLocation2.setVisibility(View.VISIBLE);
                    tvSheathCalculate.setText(R.string.btn_calculate_distance);
                } else {
                    llMainPicture.setVisibility(View.GONE);
                    llMainTest.setVisibility(View.VISIBLE);
                    tvMainTitle.setText(R.string.fault_location);
                    tvMainAttention2.setVisibility(View.GONE);
                    rlMainTestResistance.setVisibility(View.GONE);
                    rlMainTestResistance2.setVisibility(View.GONE);
                    rlMainTestResistivity.setVisibility(View.GONE);
                    rlMainTestResistivity2.setVisibility(View.GONE);
                    rlMainDistancePercentage.setVisibility(View.VISIBLE);
                    rlMainDistancePercentage2.setVisibility(View.VISIBLE);
                    rlMainFaultLocation.setVisibility(View.VISIBLE);
                    rlMainFaultLocation2.setVisibility(View.VISIBLE);
                    tvMainCalculate.setText(R.string.btn_calculate_distance);
                }
                break;
            default:
                break;
        }
    }


    private void initReplyPopupWindow() {
        replyView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_select_reply_content_layout, null);
        rgReply = (RadioGroup) replyView.findViewById(R.id.rg_reply);
        rbAll = (RadioButton) replyView.findViewById(R.id.rb_all);
        rbReplied = (RadioButton) replyView.findViewById(R.id.rb_replied);
        rbUnReply = (RadioButton) replyView.findViewById(R.id.rb_un_reply);
        replyPopupWindow = new PopupWindow(replyView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置以下代码，即背景颜色还有外部点击事件的处理才可以点击外部消失,
        replyPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        replyPopupWindow.setOutsideTouchable(true);
        replyPopupWindow.setWidth(screenWidth / 3);
//        replyPopupWindow.setHeight((int) (400));
        replyPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                makeWindowLight();
                viewBg.setVisibility(View.GONE);

            }
        });
        rgReply.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                //全部
                if (rbAll.getId() == checkId) {
                    replyStatus = 2 + "";
                } else if (rbReplied.getId() == checkId) {//已回复
                    replyStatus = 1 + "";
                } else if (rbUnReply.getId() == checkId) {//未回复
                    replyStatus = 0 + "";
                }
                page = 1;
                request(page + "", startTime, endTime, replyStatus);
                replyPopupWindow.dismiss();
                viewBg.setVisibility(View.GONE);

//                makeWindowLight();
            }
        });

    }

    private void showReplyStatusDialog() {
        replyPopupWindow.showAsDropDown(llReplyContent);
//        replyPopupWindow.showAtLocation(llShadow, Gravity.CENTER_HORIZONTAL, 0, 0);
//        makeWindowDark();
        viewBg.setVisibility(View.VISIBLE);

    }

    private void initDatePopupWindow() {
        dateView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_select_date_layout, null);
        etStartTime = (TextView) dateView.findViewById(R.id.tv_start_time);
        etEndTime = (TextView) dateView.findViewById(R.id.tv_end_time);
        btnClearData = (Button) dateView.findViewById(R.id.btn_clear_data);
        datePopupWindow = new PopupWindow(dateView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置以下代码，即背景颜色还有外部点击事件的处理才可以点击外部消失,
        datePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePopupWindow.setOutsideTouchable(true);
        datePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                makeWindowLight();
                viewBg.setVisibility(View.GONE);

            }
        });
        btnClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!startTime.equals("")) {
                    startTime = "";
                    endTime = "";
                    etStartTime.setText(getString(R.string.start_time));
                    etEndTime.setText(getString(R.string.end_time));
                    page = 1;
                    request(page + "", startTime, endTime, replyStatus);
                }
                datePopupWindow.dismiss();
            }
        });

    }

    private void showDateDialog() {
        datePopupWindow.showAsDropDown(llReportTime);
//        makeWindowDark();
        viewBg.setVisibility(View.VISIBLE);

    }

    /**
     * 日期选择
     *
     * @param activity
     * @param themeResId
     * @param textView
     * @param calendar
     */
    public static void showDatePickerDialog(final MainActivity activity, int themeResId, final TextView textView, Calendar calendar) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(activity, themeResId, new DatePickerDialog.OnDateSetListener() {
            // 绑定监听器(How the parent is notified that the date is set.)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 此处得到选择的时间，可以进行你想要的操作
                String time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                textView.setText(time);

                if (currentSettingTime == 0) {
                    startTime = time;
                    activity.page = 1;
                    if (!endTime.equals(""))
                        activity.request(activity.page + "", startTime, endTime, activity.replyStatus);

                } else {
                    endTime = time;
                    if (!DateUtils.isDateOneBigger(endTime, startTime)) {
                        Util.showToast(activity, activity.getString(R.string.end_time_is_big));
                        return;
                    }
                    activity.page = 1;
                    activity.request(activity.page + "", startTime, endTime, activity.replyStatus);
                }


            }
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 第几页
     *
     * @param page
     */
    private void request(final String page, String startTime, String endTime, String replyStatus) {
        final RequestBean requestBean = new RequestBean();
        requestBean.page = page;
        requestBean.rows = Constants.rows;
        requestBean.InTimeStart = startTime;
        requestBean.InTimeEnd = endTime;
        requestBean.ReplyStatus = replyStatus;
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
        Call<String> call = service.api("AppInfoList", json);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    byte[] srcBytes = DES3Utils.decryptMode(App.keyBytes, response.body());
                    String result = new String(srcBytes);
                    AssistListBean assistListBean = gson.fromJson(result, AssistListBean.class);
                    if (assistListBean.Code.equals("1")) {
                        if (assistListBean.data.size() > 0) {
                            if (page.equals("1")) {
                                llException.setVisibility(View.GONE);
                                srlRefresh.setVisibility(View.VISIBLE);
                                assistList.clear();
                                assistList.addAll(assistListBean.data);
                                assistListAdapter.notifyDataSetChanged();
                                isRequest = true;
                            } else {
                                assistList.addAll(assistListBean.data);
                                assistListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (page.equals("1")) {
                                llException.setVisibility(View.VISIBLE);
                                srlRefresh.setVisibility(View.GONE);
                                ivException.setImageResource(R.drawable.ic_no_data);
                                tvException.setText(R.string.no_data);
                            }
                        }
                        if (assistListBean.data.size() < Integer.parseInt(Constants.rows)) {
                            isRequest = false;
                            assistListAdapter.changeMoreStatus(assistListAdapter.NO_LOAD_MORE);
                        }
                        srlRefresh.setRefreshing(false);
                    } else {
                        Util.showToast(MainActivity.this, assistListBean.Message);
                        srlRefresh.setRefreshing(false);
                    }
                } catch (Exception e) {
                    onFailure(call, e);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //GC20200703    去提示
//                Util.showToast(MainActivity.this, getString(R.string.check_net_retry));
                srlRefresh.setRefreshing(false);
            }
        });
    }

    private int mLocalVersionCode;//本地的版本号
    public boolean isCheckOver = true;

    /**
     * 获取数据方法
     */
    public void initUpdateApk() {
        // 1，应用版本名称
//        tv_version_name.setText("版本名称:" + getVersionName());
        // 2、检测（本地版本）是否有更新，，如果有更新，提示用户下载(member)
        mLocalVersionCode = getVersionCode();
        // 3、获取服务器版本号（客户端发请求，服务端给响应，（json,xml））
        // http://www.oxxx.com/update74.json?key=value 返回200，请求成功，流的方式将数据都取下来
        // json中内容包含：
        // 新版本应用描述
        // 新版本版本号(本地版本比大小)
        // 新版本版本名称
        // 新版本apk下载地址
        if (isCheckOver == true) {
            checkVersion();
        }
        //有状态开关时的操作
  /*      if (SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false)) {
            checkVersion();
        } else {
            // 直接进入应用程序主界面
            // mHandler.sendMessageDelayed(msg, 4000);
            // 在发送消息4秒后去处理。ENTER_HOME状态码指向的消息
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 2000);
        }*/
    }

    /**
     * 返回版本号
     *
     * @return 非0代表获取成功
     */
    private int getVersionCode() {
        // 1，包管理者对象packageManager
        PackageManager pm = MainActivity.this.getPackageManager();
        // 2,从包的管理者对象中，获取指定的包名的基本信息（版本名称，版本号），传0代表获取基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(MainActivity.this.getPackageName(), 0);
            // 3,获取版本名称
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String mVersionDos;
    public static String mDownloadUrl;

    /**
     * 检测版本号
     */
    private void checkVersion() {
        isCheckOver = false;
        if (isToast) {
            Util.showToast(MainActivity.this, getString(R.string.checking_update));
        }
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();//当前时间戳
                // 发送请求获取数据，参数则为请求的json的链接地址
                // http://localhost:8080/update74.json仅限于模拟器访问tomcat
                try {
                    // 1、封装url地址
                    URL url = new URL(URLs.AppUrl + URLs.AppPort + "/update.json");

                    // 2、开启一个链接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // 3、设置常见请求参数(请求头)
                    connection.setRequestMethod("GET");// 请求方法
                    // 请求超时
                    connection.setConnectTimeout(60000);


                    // 读取超时
                    connection.setReadTimeout(60000);

                    // 4、获取请求响应码
                    if (connection.getResponseCode() == 200) {
                        // 5、以流的形式，将数据获取下来d
                        InputStream is = connection.getInputStream();
                        // 6、将流转换成字符串,封装类StreamUtil
                        String json = Util.streamToString(is);
                        // 7、json解析
                        JSONObject jsonObject = new JSONObject(json);

                        String versionName = jsonObject.getString("versionName");
                        mVersionDos = jsonObject.getString("versionDos");
                        String versionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        // 8、比对版本号（服务器版本号>本地版本号，提示用户更新）
                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            // 提示用户更新，弹出对话框（UI），消息机制
                            msg.what = UPDATE_VERSION;
                        } else {
                            // 进入应用程序界面
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    // 指定睡眠时间，请求网络的时常超过四秒不做处理
                    // 请求网络时长 小于3秒，强制让其睡眠满四秒
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 2000) {
                        try {
                            Thread.sleep(2000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 更新新版本的状态码
     */
    protected static final int UPDATE_VERSION = 100;
    /**
     * 进入应用程序主界面的状态码
     */
    protected static final int ENTER_HOME = 101;
    /**
     * URL出错的状态码
     */
    protected static final int URL_ERROR = 102;
    protected static final int IO_ERROR = 103;
    protected static final int JSON_ERROR = 104;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    // 弹出对话框，提示用户更新
                    if (MainActivity.this != null && Util.isActivityTop(MainActivity.class, MainActivity.this))
                        showUpdateDialog();
                    else if (SettingActivity.instance != null) {
                        SettingActivity.instance.showUpdateDialog();
                    }

                    isCheckOver = true;
                    break;
                case ENTER_HOME:
                    if (isToast) {
                        Util.showToast(MainActivity.this, getString(R.string.latest_version));
                    }
                    isCheckOver = true;
                    break;
                case URL_ERROR:
                    Log.e("update", "URL异常");
                    isCheckOver = true;
                    break;
                case IO_ERROR:
                    Log.e("update", "IO读取异常");
                    isCheckOver = true;
                    break;
                case JSON_ERROR:
                    Log.e("update", "JSON解析异常");
                    isCheckOver = true;
                    break;
                default:
                    break;
            }
        }
    };

    public static boolean isCancel = true; // 用来判断是否点击了取消


    /**
     * 弹出对话框，提示用户更新
     */
    protected void showUpdateDialog() {
        customDialog = new CustomDialog(MainActivity.this);
        customDialog.show();

        customDialog.setHintText(getString(R.string.version_update));
        customDialog.setLeftButton(getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancel = true;
                // 下载apk，，apk链接地址，downloadUrl
                DownloadApk(MainActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 100) {
            page = 1;
            srlRefresh.setRefreshing(true);
            request(page + "", startTime, endTime, replyStatus);
        }
    }

    public static HorizontalProgressDialog mpdialog;

    public static void DownloadApk(final Context context) {
        // apk下载链接地址，放置apk的所在路径
        // 1，判断sd卡是否可用，是否挂载上
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 2,获取sd卡路径，File.separator相当于”/“
            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "kehuiApp.apk";
            // 3,发送请求，获取apk，并放置在指定目录
            HttpUtils httpUtils = new HttpUtils();
            // 4，发送请求，传递参数（下载地址，下载应用放置位置）
            httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    // 下载成功
                    Util.showToast(context, context.getString(R.string.download_success));
                    mpdialog.cancel();
                    File file = responseInfo.result;
                    if (isCancel == true) {
                        // 提示用户安装
                        installApk(file, context);
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Util.showToast(context, context.getString(R.string.download_failure));
                }

                // 刚开始下载的方法
                @Override
                public void onStart() {
                    mpdialog = new HorizontalProgressDialog(context);
                    mpdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); //设置横向进度条风格
//                    mpdialog.setTitle("正在下载请稍后...");                       //设置标题
//                    mpdialog.setIcon(R.drawable.logo_iv);             //设置图标

                    mpdialog.setOnOkAndCancelListener(new HorizontalProgressDialog.OnOkAndCancelListener() {
                        @Override
                        public void onCancel(View v) {
                            mpdialog.dismiss();
                            mpdialog.setProgress(0);
                            isCancel = false;
                        }
                    });

                    super.onStart();
                }

                // 下载过程中的方法（下载apk的总大小，当前的下载位置，是否正在下载）
                @Override
                public void onLoading(long total, long current, final boolean isUploading) {
                    super.onLoading(total, current, isUploading);

                    if (isCancel == true) {
                        //保留两位小数
                        DecimalFormat df = new DecimalFormat("#.##");
                        String format = df.format(total * 1.0f / 1024 / 1024);
                        mpdialog.setMessage(context.getString(R.string.this_update) + format + context.getString(R.string.wait_downloading));               //设置内容
                        mpdialog.setMax((int) total);
                        mpdialog.setProgress((int) current);
                        mpdialog.setIndeterminate(false);              //设置进度条是否可以不明确
                        mpdialog.setCancelable(false);                  //设置进度条是否可以取消
                        mpdialog.show();
                    }
                }
            });

        } else {
            Util.showToast(context, context.getString(R.string.download_failure));
        }
    }

    /**
     * 安装对应apk
     *
     * @param file 提示用户安装
     */
    public static void installApk(File file, Context context) {
        // 系统应用界面，源码，安装apk入口
        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //android4.0以后需要添加这行代码
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
        // 文件作为数据源
        // intent.setData(Uri.fromFile(file));
        // 设置安装的类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);


//        startActivityForResult(intent, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//加载menu文件到布局
        return true;
    }

    // 动态设置右侧菜单状态
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_setting:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 按两次返回按键才退出程序，防止无意中按错键退出
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //System.currentTimeMillis()无论何时调用，肯定大于2000
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), getString(R.string.exit_prompt), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
