package com.kehui.t_h200.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jwj on 2018/4/14.
 */

public class Util {
    public static void showToast(final Context activity, final String word) {

        final Toast toast = Toast.makeText(activity, word, Toast.LENGTH_SHORT);
        toast.show();

    }

    /**
     * @param is 流对象
     * @return 流转换成字符串 返回null代表异常
     */
    public static String streamToString(InputStream is) {
        // 1、在读取的过程中，将读取的内容存储到缓存中，然后一次性的转换成字符串返回
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 2、 读流操作，读到没有为止（循环）
        byte[] buffer = new byte[1024];
        // 3、记录读取内容的临界值(临时变量)
        int temp = -1;
        try {
            while ((temp = is.read(buffer)) != -1) {
                bos.write(buffer, 0, temp);
            }
            // 返回读取的数据

            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                bos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 判断某activity是否处于栈顶
     *
     * @return true在栈顶 false不在栈顶
     */
    public static boolean isActivityTop(Class cls, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }
    /**
     * dp转换成px
     */
    public static int dp2px(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    /**
     * px转换成dp
     */
    public static int px2dp(Context context,float pxValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/scale+0.5f);
    }

}
