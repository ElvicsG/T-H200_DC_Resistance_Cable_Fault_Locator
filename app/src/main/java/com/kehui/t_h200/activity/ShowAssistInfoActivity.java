package com.kehui.t_h200.activity;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.kehui.t_h200.R;
import com.kehui.t_h200.adapter.MyChartAdapter;
import com.kehui.t_h200.app.App;
import com.kehui.t_h200.app.Constants;
import com.kehui.t_h200.app.URLs;
import com.kehui.t_h200.base.BaseActivity;
import com.kehui.t_h200.bean.AssistInfoBean;
import com.kehui.t_h200.bean.PackageBean;
import com.kehui.t_h200.bean.RequestBean;
import com.kehui.t_h200.retrofit.APIService;
import com.kehui.t_h200.ui.SparkView.SparkView;
import com.kehui.t_h200.utils.DES3Utils;
import com.kehui.t_h200.utils.Util;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * 展示波形的页面
 */
public class ShowAssistInfoActivity extends BaseActivity {

    @BindView(R.id.tv_magnetic_field_gain)
    TextView tvMagneticFieldGain;
    @BindView(R.id.rl_magnetic_field)
    RelativeLayout rlMagneticField;
    @BindView(R.id.linechart_cichang)
    SparkView linechartCichang;
    @BindView(R.id.tv_shengyin_gain)
    TextView tvShengyinGain;
    @BindView(R.id.rl_shengyin)
    RelativeLayout rlShengyin;
    @BindView(R.id.linechart_shengyin)
    SparkView linechartShengyin;
    @BindView(R.id.iv_pause)
    ImageView ivPause;
    @BindView(R.id.ll_pause)
    LinearLayout llPause;
    @BindView(R.id.ll_memory)
    LinearLayout llMemory;
    @BindView(R.id.ll_compare)
    LinearLayout llCompare;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.tv_pause)
    TextView tvPause;
    @BindView(R.id.tv_filter)
    TextView tvFilter;

    private String infoId = "";

    //播放声音的工具
    private AudioTrack mAudioTrack;
    //从assets文件夹获取crctable数组
    private long[] mCrcTable;
    private int mShengyinCount; //GN要画的声音包的个数
    private boolean mShengyinFlag;  //是否开始截取声音的标志
    private int mShengyinMarkNum;   //GN触发时数据点所在的位置
    private int mTempLength = 0;    //GN 缓存蓝牙输入流的临时数组长度
    private int mTempcount = 0;     //GN 临时数组内的蓝牙输入流的个数
    private int mTempLength2 = 0;   //GN 进行处理的蓝牙数据长度
    private int[] mTemp = new int[102400];    //GN 缓存蓝牙输入流的临时数组
    private int[] mTemp2 = new int[102400];   //GN 进行分包处理的蓝牙数据
    private boolean doTemp2Flag;    //GN 是否正在处理蓝牙数据的标志
    private String infoData;
    private boolean isExit;     //是否退出软件的标志


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        App.getInstances().switchLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "ch"));
        setContentView(R.layout.activity_show_assist_info);
        ButterKnife.bind(this);
        initData();
        getCrcTable();
        setAudioTrack();

    }

    private void initData() {
        isDraw = true;
        isExit = false;
        mShengyinArray = new int[500];
        mTempShengyinArray = new int[500];
        mCompareShengyinArray = new int[500];

        mCichangArray = new int[400];
        mTempCichangArray = new int[400];
        mCompareArray = new int[500];
        Intent intent = getIntent();
        if (intent != null) {
            infoId = intent.getStringExtra("infoId");
        }
        requestInfo();
        getCichangData();
        getShengyinData();
//        myChartAdapterCichang = new MyChartAdapter(mTempCichangArray, null,
//                false, 0, false);
//
//        linechartCichang.setAdapter(myChartAdapterCichang);
//        myChartAdapterShengyin = new MyChartAdapter(mTempShengyinArray, null,
//                false, 0, false);
//
//        linechartShengyin.setAdapter(myChartAdapterShengyin);
    }

    private void requestInfo() {
        final RequestBean requestBean = new RequestBean();
        requestBean.InfoID = infoId;
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
        Call<String> call = service.api("AppInfoBinary", json);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    byte[] srcBytes = DES3Utils.decryptMode(App.keyBytes, response.body());
                    String result = new String(srcBytes);
                    AssistInfoBean assistInfoBean = gson.fromJson(result, AssistInfoBean.class);
                    updateView(assistInfoBean.data.get(0));
                    infoData = assistInfoBean.data.get(0).InfoCiChang;
                    mReadThread.start();
                    DoTempThread.start();
                } catch (Exception e) {
                    Log.e("打印-请求报异常-检查代码", "AppInfoBinary");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Util.showToast(ShowAssistInfoActivity.this, getString(R.string.check_net_retry));
            }
        });


    }

    private void updateView(AssistInfoBean.DataBean dataBean) {
        tvMagneticFieldGain.setText(dataBean.InfoCiCangVol);
        tvShengyinGain.setText(dataBean.InfoShengYinVol);

        switch (Integer.parseInt(dataBean.InfoLvBo)) {
            case 0:
                tvFilter.setText(getString(R.string.quantong));
                break;
            case 1:
                tvFilter.setText(getString(R.string.ditong));
                break;
            case 2:
                tvFilter.setText(getString(R.string.gaotong));
                break;
            case 3:
                tvFilter.setText(getString(R.string.daitong));
                break;

        }
    }

    @OnClick({R.id.ll_pause, R.id.ll_memory, R.id.ll_compare, R.id.ll_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_pause:
                clickPause();
                break;
            case R.id.ll_memory:
                clickMemory();
                break;
            case R.id.ll_compare:
                clickCompare();
                break;
            case R.id.ll_back:
                isExit = true;
                mAudioTrack.release();// 关闭并释放资源
                finish();
                break;
        }
    }

    //GN 缓存蓝牙数据的线程
    Thread mReadThread = new Thread(new Runnable() {
        @Override
        public void run() {
            //待处理的数据
            String[] infoDataArray = infoData.split(" ");
            //已发送长度标记
            int sendIndex = 0;
            while (sendIndex < infoDataArray.length - 50) {
                for (int i = 0; i < 50; i++, sendIndex++) {
                    mTemp[mTempLength] = Integer.valueOf(infoDataArray[sendIndex], 16);
                    mTempLength++;
                }
                mTempcount++;
                //GC20171129 缓存5个以上的蓝牙输入流并且没有在处理蓝牙数据时
                if (mTempcount >= 5 && !doTemp2Flag) {
                    for (int i = 0; i < mTempLength; i++) {
                        mTemp2[i] = mTemp[i];
                    }
                    mTempLength2 = mTempLength;
                    Log.i("streamBytes", "Sum:" + mTempLength2);  //GT20180321 进行处理的蓝牙数据的长度
                    mTempLength = 0;
                    mTempcount = 0;
                    doTemp2Flag = true;
                }

                try {
                    Thread.sleep(8);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    });

    //GN 处理蓝牙数据的线程
    Thread DoTempThread = new Thread(new Runnable() {
        @Override
        public void run() {

            while (true) {
                if (doTemp2Flag) {
                    doTemp2(mTemp2, mTempLength2);
                    doTemp2Flag = false;
                }
//                try {
//                    Thread.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

        }

    });


    private int hasLeftLen = 0;     //GN 将蓝牙数据分包后(每包59个字节)，剩余数据的个数
    private int[] mTempLeft = new int[59];    //GN 存放将蓝牙数据分包后有剩余数据的临时数组
    private byte[] tempRequest = new byte[7];
    private boolean hasLeft;    //GN 将蓝牙数据分包后，是否有剩余数据的标志
    private boolean hasSendMessage;

    //GN 处理接收到的蓝牙数据
    private void doTemp2(int[] mTemp3, int mTempLength3) {

        int i = 0;
        int dataNum = 0;                // 处理过的数据个数
        int[] tempints = new int[59];   //GN 数据包的临时数组
        int[] tempints2 = new int[7];   //GN 蓝牙客户端控制命令的临时数组
        //GN 如果有剩余数据，与新数据合并后再进行处理
        if (hasLeft) {
            for (int j = hasLeftLen, k = 0; j < 59; j++, k++) {
                mTempLeft[j] = mTemp3[k];
            }
            for (int i1 = 0; i1 < 59; i1++) {
                //GN 如果i1的值是开头（0x53：声音  0x4d：磁场  0x60：T-506）
                if (mTempLeft[i1] == 83 || mTempLeft[i1] == 77 || mTempLeft[i1] == 96) {

                    for (int i2 = 0, j = i1; i2 < 59; i2++, j++) {
                        if (j >= 59) {
                            tempints[i2] = mTemp3[i + j - hasLeftLen];
                        } else {
                            tempints[i2] = mTempLeft[j];    //GN 截取一包数据
                        }
                        boolean isCrc = doTempCrc(tempints);
                        if (isCrc) {    //GN CRC校验成功，判断为数据包数据
                            doTempBean(tempints);
                            i1 += 58;
                        } else {        //GN CRC校验失败，判断为有可能是控制命令
                            if (mTempLeft[i1] == 96) {

                                for (int i3 = 0, k = i1; i3 < 7; i3++, k++) {
                                    if (k >= 59) {
                                        tempints2[i3] = mTemp3[i + k - hasLeftLen];
                                    } else {
                                        tempints2[i3] = mTempLeft[k];
                                    }
                                }

                                boolean isCrc2 = doTempCrc2(tempints2); //GN 对控制命令进行CRC校验
                                if (isCrc2) {
                                    hasSendMessage = false;
                                    i1 += 6;
                                    if (tempints2[2] != 1) {

                                        seekbarType = 0;
                                        // sendString(tempRequest);
                                    } else if (tempints2[1] == tempRequest[1]) {
                                        seekbarType = 0;

                                    }

                                }

                            }

                        }

                    }

                }

            }
            hasLeft = false;
        }
        //开始遍历
        for (; i < mTempLength3 - 59; i++) {
            if (mTemp3[i] == 83 || mTemp3[i] == 77 || mTemp3[i] == 96) {
                for (int j = i, k = 0; j < (i + 59); j++, k++) {
                    tempints[k] = mTemp3[j];    //GN 截取一包数据
                }
                boolean isCrc = doTempCrc(tempints);
                if (isCrc) {    //GN CRC校验成功，判断为数据包数据
                    doTempBean(tempints);
                    i += 58;
                } else {
                    if (mTemp3[i] == 96) {
                        for (int j = i, k = 0; j < (i + 7); j++, k++) {
                            tempints2[k] = mTemp3[j];   //GN 截取一条控制命令
                        }
                        boolean isCrc2 = doTempCrc2(tempints2);
                        if (isCrc2) {
                            hasSendMessage = false;
                            if (tempints2[2] != 1) {    //GN Respond：响应值，命令未响应

                            } else if (tempints2[1] == tempRequest[1]) {
                                seekbarType = 0;
                            }
                            i += 6;
                        }
                    }
                }
            }
            dataNum = i;
        }
        if (dataNum == mTempLength3) {
            hasLeft = false;
        } else {    //如果剩余的数少于59个,退出循环 并且把剩下的数据存到临时数组中 同时设置有剩余的数组
            for (int j = dataNum + 1, k = 0; j < mTempLength3; j++, k++) {
                mTempLeft[k] = mTemp3[j];
            }
            hasLeftLen = mTempLength3 - i;
            hasLeft = true;
        }

    }

    private boolean isClickRem;     //是否点击了记忆
    private int seekbarType;

    private int[] mShengyinArray;
    private int[] mTempShengyinArray;
    private int[] mCompareShengyinArray;

    private int[] mCichangArray;

    private int[] mTempCichangArray;
    private int[] mCompareArray;
    private int streamVolumenow;

    private MyChartAdapter myChartAdapterShengyin;
    private MyChartAdapter myChartAdapterCichang;
    private boolean isDraw;     //是否画波形的标志
    private boolean isCom;      //是否画声音比较波形的标志

    private int WHAT_REFRESH = 3;   //子线程通知主线程刷新UI的what

    //解密需要的解析常量数组
    private int[] IndexTable = {-1, -1, -1, -1, 2, 4, 6, 8, -1, -1, -1, -1, 2, 4, 6, 8};
    private int[] StepSizeTable = {7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 19, 21, 23, 25, 28, 31,
            34, 37, 41, 45, 50, 55, 60, 66, 73, 80, 88, 97, 107, 118, 130, 143, 157, 173, 190,
            209, 230, 253, 279, 307, 337, 371, 408, 449, 494, 544, 598, 658, 724, 796, 876, 963,
            1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066, 2272, 2499, 2749, 3024, 3327, 3660,
            4026, 4428, 4871, 5358, 5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635,
            13899, 15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767};

    //GN全局handle更新UI
    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //GN子线程通知主线程刷新UI
            if (msg.what == WHAT_REFRESH) {

                if (isDraw) {
                    myChartAdapterShengyin.setmTempArray(mTempShengyinArray);
                    myChartAdapterShengyin.setShowCompareLine(isCom);
                    if (isCom) myChartAdapterShengyin.setmCompareArray(mCompareArray);
                    myChartAdapterShengyin.notifyDataSetChanged();
                    myChartAdapterCichang.setmTempArray(mTempCichangArray);
                    myChartAdapterCichang.notifyDataSetChanged();

                }
            }
        }
    };

    //对59个数据进行bean对象的处理
    private void doTempBean(int[] tempints) {

        PackageBean packageBean = new PackageBean();
        packageBean.setSM(tempints[0]);
        packageBean.setMark(tempints[1]);
        packageBean.setIndex(tempints[2]);
        packageBean.setPredsample(new int[]{tempints[3], tempints[4]});
        int[] date = new int[50];
        for (int i1 = 5, j = 0; i1 < 55; i1++, j++) {
            date[j] = tempints[i1];
        }
        packageBean.setDate(date);
        doPackageBean(packageBean);

    }

    //每当解析完生成一个packageBean,对其进行相应的操作
    public void doPackageBean(PackageBean packageBean) {
        Log.e("FILE", "packageBean:" + packageBean.toString());
        int[] results = decodeData(packageBean);
        if (packageBean.getSM() == 83) {    //GN 0x53 声音

            int mark = packageBean.getMark();
            //GN 仪器触发，获取组成放电声音的第一个包的有效数据
            if (binaryStartsWithOne(mark)) {    //GN 获取Mark最高位

                mShengyinFlag = true;       //GN开始截取声音包
                mShengyinMarkNum = getMarkLastSeven(mark);  //GN获取Mark后7位，触发时数据点所在的位置
                for (int i = 0, j = mShengyinMarkNum; j < 100; i++, j++) {
                    mShengyinArray[i] = results[j];
                }
                mShengyinCount++;   //GN要画的声音包的个数，声音数据从包头开始只截取4包 否则要截取5包

            }
            //仪器这一时刻未触发时，判断是否需要获取声音包
            else {
                //GN 开始截取声音了，继续截取声音包
                if (mShengyinFlag) {

                    if (mShengyinCount <= 4) {

                        for (int j = 0, i = mShengyinCount * 100 - mShengyinMarkNum; j < 100; j++, i++) {
                            mShengyinArray[i] = results[j];
                        }
                        mShengyinCount++;

                    } else {
                        mShengyinFlag = false;
                        mShengyinCount = 0;

                    }

                }

            }
            playSound(results);

        } else if (packageBean.getSM() == 77) {   //GN 0x4d 磁场

            if (packageBean.getMark() >= 128) { //GN  Mark "128" = 10000000 代表位7是1
                packageBean.setMark(packageBean.getMark() - 128); //令位7为0

            }
            //GN 按照顺序（00 01 10 11）将磁场数据拼接起来，1包含有100个数据点
            for (int i = 0, j = packageBean.getMark() * 100; i < 100; i++, j++) {
                mCichangArray[j] = results[i];
            }
            if (packageBean.getMark() == 3) {   //GN 4个磁场包拼接完成，开始画主界面波形

                if (isDraw) {

                    for (int i = 0; i < 400; i++) {
                        mTempShengyinArray[i] = mShengyinArray[i];
                    }
                    for (int i = 0; i < 400; i++) {
                        mTempCichangArray[i] = mCichangArray[i];
                    }
                    for (int i = 0; i < 400; i++) {
                        mCompareShengyinArray[i] = mTempShengyinArray[i];
                    }
                    mShengyinCount = 0;

                }

                Message message = new Message();
                message.what = WHAT_REFRESH;
                mHandle.sendMessage(message);

            }

        }

    }

    //播放声音
    private void playSound(int[] results) {

        byte[] bytes = new byte[results.length * 2];
        for (int i = 0; i < results.length; i++) {
            short sh = (short) ((results[i] - 2048) * 16);
            byte[] bytes1 = shortToByte(sh);
            //Log.e("FILE", "byte.length:  " + bytes1.length);
            bytes[i * 2] = bytes1[0];
            bytes[i * 2 + 1] = bytes1[1];
        }

        if (!isExit) mAudioTrack.write(bytes, 0, bytes.length);

//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
//        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//        String str = formatter.format(curDate);
//        Log.e("TAG4playSound", str);    //GT20171129

    }

    //解密数据
    private int[] decodeData(PackageBean bean1) {
        int index = bean1.getIndex();
        int[] predsample = bean1.getPredsample();
        int predsample1 = predsample[0];
        int predsample2 = predsample[1];
        int pred = predsample1 * 256 + predsample2;
        int[] date = bean1.getDate();
        /* for (int i : date) {
            Log.e("FILE", i+"");
        }*/
        int[] dateArray = new int[100];

        int count = 0;
        for (int da : date) {
            dateArray[count] = da >> 4;
            dateArray[count + 1] = da - dateArray[count] * 16;
            count += 2;

        }

        return decodeDataSecond(index, pred, dateArray);

    }

    //二次解密
    private int[] decodeDataSecond(int index, int pred, int[] dateArray) {
        int prevsample = pred;
        int previndex = index;
        int PREDSAMPLE = 0;
        int INDEX = 0;
        int[] result = new int[dateArray.length];

        for (int i = 0; i < dateArray.length; i++) {
            PREDSAMPLE = prevsample;
            INDEX = previndex;
            int step = StepSizeTable[INDEX];
            int code = dateArray[i];
            int diffq = step / 8;
            if ((code & 4) == 4) diffq = diffq + step;
            if ((code & 2) == 2) diffq = diffq + step / 2;
            if ((code & 1) == 1) diffq = diffq + step / 4;
            if ((code & 8) == 8) PREDSAMPLE = PREDSAMPLE - diffq;
            else PREDSAMPLE = PREDSAMPLE + diffq;

            if (PREDSAMPLE > 4095)
                PREDSAMPLE = 4095;
            if (PREDSAMPLE < 0)
                PREDSAMPLE = 0;
            //Log.e("FILE", code+"");
            INDEX = INDEX + IndexTable[code];

            if (INDEX < 0)
                INDEX = 0;
            if (INDEX > 88)
                INDEX = 88;
            prevsample = PREDSAMPLE;
            previndex = INDEX;
            result[i] = prevsample;

        }

        return result;

    }

    //判断二进制的最高位是否是1
    private boolean binaryStartsWithOne(int tByte) {

        String tString = Integer.toBinaryString((tByte & 0xFF) + 0x100).substring(1);
        return tString.startsWith("1");

    }

    //获取mark的后7位
    private Integer getMarkLastSeven(int mark) {

        String tString = Integer.toBinaryString((mark & 0xFF) + 0x100).substring(1);
        String substring = tString.substring(1, tString.length());
        return Integer.valueOf(substring, 2);

    }

    //short转byte
    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();//

            temp = temp >> 8; // 向右移8位
        }
        return b;
    }


    //点击静音按钮执行的方法
//    public void clickSilence(View v) {
//        int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//     /*   System.out.println("streamMaxVolume:" + streamMaxVolume);
//        System.out.println("streamVolume:" + streamVolume);*/
//
//
//        if (isSilence) {
//
//            if (streamVolumenow == 0) {
//                streamVolumenow = streamMaxVolume / 2;
//            }
//
//            //audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolumenow,
//                    AudioManager
//                            .FLAG_PLAY_SOUND);
//            ibtSilence.setBackground(getResources().getDrawable(R.drawable.silence_on));
//        } else {
//            streamVolumenow = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            //audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager
//                    .FLAG_PLAY_SOUND);
//            ibtSilence.setBackground(getResources().getDrawable(R.drawable.silence_off));
//        }
//        isSilence = !isSilence;
//
//        //AplicationUtil.makeToast(this, "clickSilence");
//    }

    //点击暂停按钮执行的方法
    public void clickPause() {
        //mMediaPlayer.stop();
        isDraw = !isDraw;
        if (isDraw) {
            ivPause.setImageResource(R.drawable.ic_pause);
            tvPause.setText(getString(R.string.pause));
            llPause.setBackgroundResource(R.drawable.bg_assist_data_btn);

        } else {
            ivPause.setImageResource(R.drawable.ic_play);
            tvPause.setText(getString(R.string.start));
            llPause.setBackgroundResource(R.drawable.bg_assist_data_btn_2);
        }
    }

    //点击记忆按钮执行的方法
    public void clickMemory() {
        isClickRem = true;

        for (int i = 0; i < 400; i++) {
            mCompareArray[i] = mCompareShengyinArray[i];
        }

        llMemory.setBackgroundResource(R.drawable.bg_assist_data_btn_2);
        llMemory.setClickable(false);
        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                llMemory.setBackgroundResource(R.drawable.bg_assist_data_btn);
                llMemory.setClickable(true);
            }
        }, 250);


    }

    //点击比较按钮执行的方法
    public void clickCompare() {

        if (isClickRem) {
            isCom = !isCom;
        } else {
            Toast.makeText(this, getResources().getString(R.string
                    .You_have_no_memory_data_can_not_compare), Toast.LENGTH_SHORT).show();
        }
        if (!isCom) {
            llCompare.setBackgroundResource(R.drawable.bg_assist_data_btn);
        } else {
            llCompare.setBackgroundResource(R.drawable.bg_assist_data_btn_2);
        }
        myChartAdapterShengyin.setmTempArray(mTempShengyinArray);
        myChartAdapterShengyin.setShowCompareLine(isCom);
        myChartAdapterShengyin.setmCompareArray(mCompareArray);
        myChartAdapterShengyin.notifyDataSetChanged();
    }

    //GN 对数据包进行CRC校验
    private boolean doTempCrc(int[] tempcrc) {

        int[] ints = new int[55];
        int[] ints2 = new int[4];   //crc校验返回回来进行比对的4个字符

        for (int i1 = 0, j = 0; i1 < 55; i1++, j++) {
            ints[j] = tempcrc[i1];
        }
        for (int i1 = 55, j = 0; i1 < 59; i1++, j++) {
            ints2[j] = tempcrc[i1];
        }

        return isCrc(ints, ints2);

    }

    //GN 对控制命令进行CRC校验
    private boolean doTempCrc2(int[] tempcrc) {

        int[] ints = new int[3];
        int[] ints2 = new int[4];   //crc校验返回回来进行比对的4个字符

        for (int i1 = 0, j = 0; i1 < 3; i1++, j++) {
            ints[j] = tempcrc[i1];
        }
        for (int i1 = 3, j = 0; i1 < 7; i1++, j++) {
            ints2[j] = tempcrc[i1];
        }
        return isCrc(ints, ints2);

    }

    //判断Crc校验的结果
    private boolean isCrc(int[] ints, int[] ints2) {

        long l = testCrc(ints);

        long ll = (long) (ints2[0] * Math.pow(2, 24) + ints2[1] * Math.pow(2, 16) + ints2[2] *
                Math.pow(2, 8) + ints2[3]);

        return l == ll;
    }

    //测试CRC
    private long testCrc(int[] ints) {

        long nReg = Long.valueOf("4294967295");
        long integer = Long.valueOf("4294967295");

        for (int i = 0; i < ints.length; i++) {
            nReg = nReg ^ ints[i];
//            Log.e("FILE","nReg=nReg^bytes[i];");
            for (int j = 0; j < 4; j++) {
                long a = nReg >> 24;
//                Log.e("FILE", "a:" + a);
                long b = a & 255;
//                Log.e("FILE", "b:" + b);
                long nTemp = mCrcTable[(int) b];
//                Log.e("FILE", "nTemp:" + nTemp);

                //4294967295 -1
                nReg = (nReg << 8) & integer;
                nReg = nReg ^ nTemp;
            }
        }
        return nReg;
    }

    //设置音频播放工具
    private void setAudioTrack() {
        try {


            int minBufferSize = AudioTrack.getMinBufferSize(8000,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
//            int minBufferSize =  android.media.AudioTrack.getMinBufferSize(8000,
//                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT);
            Log.e("Size", "minBufferSize:" + minBufferSize);    //GT20171129  内部的音频缓冲区的大小 输出结果1392
//STREAM_MUSIC
//            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
//                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT,
//                    minBufferSize,
//                    AudioTrack.MODE_STREAM);
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, //GN当前应用使用的哪一种音频管理策略
                    // STREAM_ALARM：警告声
                    // STREAM_MUSCI：音乐声，例如music等
                    // STREAM_RING：铃声
                    // STREAM_SYSTEM：系统声音
                    // STREAM_VOCIE_CALL：电话声音
                    8000,// 设置音频数据的采样率
                    AudioFormat.CHANNEL_OUT_MONO,   //GN单通道
                    AudioFormat.ENCODING_PCM_16BIT, //GN数据位宽
                    //minBufferSize * 6, AudioTrack.MODE_STREAM);   //GC20171129 减少350ms左右延时
//                    minBufferSize / 6, AudioTrack.MODE_STREAM);
                    minBufferSize, AudioTrack.MODE_STREAM);//解决个别手机崩溃
            //GN手动计算一帧“音频帧”（Frame）的大小（12） int size = 采样率 x 位宽 x 采样时间 x 通道数
            //GN播放模式
            // AudioTrack中有MODE_STATIC和MODE_STREAM两种分类。
            // STREAM方式表示由用户通过write方式把数据一次一次得写到audiotrack中。
            // 这种方式的缺点就是JAVA层和Native层不断地交换数据，效率损失较大。
            // 而STATIC方式表示是一开始创建的时候，就把音频数据放到一个固定的buffer，然后直接传给audiotrack，
            // 后续就不用一次次得write了。AudioTrack会自己播放这个buffer中的数据。
            // 这种方法对于铃声等体积较小的文件比较合适。
            mAudioTrack.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //从assets文件夹中获取crctable中的数据
    private void getCrcTable() {

        InputStream mResourceAsStream = this.getClassLoader().getResourceAsStream("assets/" + "crctable.txt");
        BufferedInputStream bis = new BufferedInputStream(mResourceAsStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int c = 0;//读取bis流中的下一个字节
        try {
            c = bis.read();
            while (c != -1) {
                baos.write(c);
                c = bis.read();
            }
            bis.close();
            String s = baos.toString();         //Log.e("FILE", s);
            String[] split = s.split("\\s+");   //Log.e("FILE","splitSize:"+split.length);
            mCrcTable = new long[256];
            for (int i = 0; i < split.length; i++) {
                mCrcTable[i] = Long.parseLong(split[i], 16);    // Log.e("FILE","crcTable[i]:"+mCrcTable[i]);
            }
            /* byte retArr[]=baos.toByteArray();
            for (byte b : retArr) {
                Log.e("FILE",""+b);
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //获得初始化声音数据
    private void getShengyinData() {

        InputStream mResourceAsStream = this.getClassLoader().getResourceAsStream("assets/" +
                "shengyin.txt");
        BufferedInputStream bis = new BufferedInputStream(mResourceAsStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int c = 0;//读取bis流中的下一个字节
        try {
            c = bis.read();
            while (c != -1) {
                baos.write(c);
                c = bis.read();
            }
            bis.close();
            String s = baos.toString();
            //Log.e("FILE", s);
            String[] split = s.split("\\s+");
            //Log.e("FILE","splitSize:"+split.length);
            /* for (String s1 : split) {
                mTempShengyinList.add(Integer.parseInt(s1));

            }*/
            /*for (int i = 0; i < split.length; i++) {
                mTempShengyinArray[i] = Integer.parseInt(split[i]);
            }*/
            for (int i = 0; i < split.length; i++) {
                mTempShengyinArray[i] = 0;
            }


            /*for (Integer integer : mTempShengyinList) {
                Log.e("HEJIA", integer + "");

            }*/
            //Log.e("HEJIA", "size:             " + mTempShengyinList.size());

            //refreshUi(false, 10);
            myChartAdapterShengyin = new MyChartAdapter(mTempShengyinArray, null,
                    false, 0, false);

            linechartShengyin.setAdapter(myChartAdapterShengyin);

           /* byte retArr[]=baos.toByteArray();
            for (byte b : retArr) {
                Log.e("FILE",""+b);
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //获得初始化磁场数据
    private void getCichangData() {

        InputStream mResourceAsStream = this.getClassLoader().getResourceAsStream("assets/" + "cichang.txt");
        BufferedInputStream bis = new BufferedInputStream(mResourceAsStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int c = 0;//读取bis流中的下一个字节
        try {
            c = bis.read();
            while (c != -1) {
                baos.write(c);
                c = bis.read();
            }
            bis.close();
            String s = baos.toString();
//            Log.e("FILE", s);
            String[] split = s.split("\\s+");
            //Log.e("FILE","splitSize:"+split.length);
            /*for (String s1 : split) {
                mTempCichangList.add(Integer.parseInt(s1));

            }*/
           /* for (int i = 0; i < split.length; i++) {
                mTempCichangArray[i] = Integer.parseInt(split[i]);
            }*/
            for (int i = 0; i < split.length; i++) {
                mTempCichangArray[i] = 0;
            }
            myChartAdapterCichang = new MyChartAdapter(mTempCichangArray, null,
                    false, 0, false);

            linechartCichang.setAdapter(myChartAdapterCichang);
            //refreshUi(false, 10);

           /* byte retArr[]=baos.toByteArray();
            for (byte b : retArr) {
                Log.e("FILE",""+b);
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    @Override
//    protected void onPause() {
//        isExit = true;
//        mAudioTrack.release();// 关闭并释放资源
//        super.onDestroy();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isExit = true;
            mAudioTrack.release();// 关闭并释放资源
            finish();
        }
        return false;

    }


}
