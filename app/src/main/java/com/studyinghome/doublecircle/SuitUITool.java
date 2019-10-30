package com.studyinghome.doublecircle;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * 计算宽高比例的工具类
 *
 * @author Leslie
 * @email panxiang_work@163.com
 * @create 2019-10-30 11:47
 */
public class SuitUITool {
    private static final float standard_width = 1080;
    private static final float standard_height = 1920;

    private static float scale_width = -1;
    private static float scale_height = -1;

    private static SuitUITool instance;
    private static Context mContext;

    public SuitUITool(Context mContext) {
        //这儿需要用应用的上下文。因为这是单例，如果不使用应用上下文，切换页面后，上下文还保持原来的context，会导致旋转的时候获取不到当前页面正确的值
        this.mContext = mContext.getApplicationContext();
    }

    public static SuitUITool getInstance(Context context) {
        if (instance == null) {
            instance = new SuitUITool(context);
        }
        //每次清空，旋转或者新页面调用的时候，如果不重置，会影响measure
        scale_width = -1;
        scale_height = -1;
        return instance;
    }

    public float getScale_width() {
        if (scale_width == -1) {
            initScale();
        }
        return scale_width;
    }

    public float getScale_height() {
        if (scale_height == -1) {
            initScale();
        }
        return scale_height;
    }

    private void initScale() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        int statusBarHeight = getStatusBarHeight();
        Log.e("wang", "initScale:width->" + width + "  height->" + height + "  statusBarHeight==>" + statusBarHeight);
        Configuration mConfiguration = mContext.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            scale_width = (float) height / standard_width;
            scale_height = (float) width / standard_height;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            scale_width = (float) width / standard_width;
            scale_height = (float) height / standard_height;
        }
//        scale_width = (float) width / standard_width;
//        scale_height = (float) (height - statusBarHeight) / standard_height;
        Log.e("wang", "initScale:scale_width->" + scale_width + "  scale_height->" + scale_height + "  ori==>" + ori);
    }

    private int getStatusBarHeight() {
        int statusBarHeight2 = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight2 = mContext.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight2;
    }
}
