package com.kehui.t_h200.base;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.kehui.t_h200.utils.MultiLanguageUtil;

/**
 * @author Gong
 * @date 2020/7/9
 * 基类activity
 */
public class BaseActivity extends AppCompatActivity {
    public int screenWidth;
    public int screenHeight;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();

    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
    /**
     * 让屏幕变暗
     */
    public void makeWindowDark() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.5f;
        window.setAttributes(lp);
    }

    /**
     * 让屏幕变亮
     */
    public void makeWindowLight() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 1f;
        window.setAttributes(lp);
    }

    public boolean isNetVisible() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
//        ConnectivityManager mConnectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        TelephonyManager mTelephony = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//// 检查网络连接，如果无网络可用，就不需要进行连网操作等
//        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
//        if (info == null ||
//                !mConnectivity.getBackgroundDataSetting()) {
//            return false;
//        }
////判断网络连接类型，只有在3G或wifi里进行一些数据更新。
//        int netType = info.getType();
//        int netSubtype = info.getSubtype();
//        if (netType == ConnectivityManager.TYPE_WIFI) {
//            return info.isConnected();
//        } else if (netType == ConnectivityManager.TYPE_MOBILE
//                && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
//                && !mTelephony.isNetworkRoaming()) {
//            return info.isConnected();
//        } else {
//            return false;
//        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MultiLanguageUtil.attachBaseContext(newBase));
    }
}

/*更改记录*/
//GC20200703    去网络断开提示
//GC20200707    蓝牙搜索列表添加
//GC20200709    蓝牙数据处理
//G?
//GT