package com.kehui.t_h200.app;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import java.util.Locale;

import com.kehui.t_h200.utils.DES3Utils;
import com.kehui.t_h200.utils.MultiLanguageUtil;
import com.kehui.t_h200.utils.PrefUtils;

/**
 * Created by jwj on 2018/4/14.
 */

public class App extends Application {
    public static final String key = DES3Utils.MD5Encode("KH_Key_*", "").substring(3, 27).toUpperCase();//秘钥
    public static final byte[] keyBytes = DES3Utils.hexToBytes(DES3Utils.byte2hex(key.getBytes()));//24位密钥
    public final Locale Locale_Spanisch = new Locale("Es", "es", "");
    public static App instances;

    @Override
    public void onCreate() {
        MultiLanguageUtil.init(getApplicationContext());

        instances = this;
        MultiLanguageUtil.getInstance().updateLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "follow_sys"));
//        switchLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "ch"));
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("aaaaaaaa","走我了");
        MultiLanguageUtil.getInstance().updateLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "follow_sys"));
//        switchLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "ch"));

    }




    public static App getInstances() {
        return instances;
    }

}
