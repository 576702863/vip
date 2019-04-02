package com.example.administrator.jay;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * ================================================
 *
 * @author :wangguibin
 * @version :V1.0.0
 * @date :2019/4/2/10:33
 * @ProjectNameDescribe :ToastMiuiUsed 仿MIUI的带有动画的Toast效果实现，基于WindowManager【可以控制显示样式、位置、显示时间、动画，不可触发】
 * 修订历史：Toast布局在源码中的布局是采用LinearLayout
 * ================================================
 */
public class ToastMiui {

    /**
     * 显示的时间（长）
     */
    public static final int LENGTH_LONG = 3500;
    /**
     * 显示的时间（短）
     */
    public static final int LENGTH_SHORT = 20000;
    /**
     * 整个Android的窗口机制是基于一个叫做 WindowManager
     **/
    private WindowManager mWindowManager;
    private View mToastView;
    private WindowManager.LayoutParams mParams;
    private Handler mHandler;
    private Context mContext;
    /**
     * 记录Toast的显示长短类型LENGTH_LONG/LENGTH_SHORT
     **/
    private int mShowTime = LENGTH_SHORT;
    /**
     * 记录当前Toast的内容是否已经在显示
     **/
    private boolean mIsShow;

    /**
     * 逻辑简单粗暴，直接调用构造函数实例化一个MiuiToast对象并返回。
     */
    public static ToastMiui MakeText(Context context, CharSequence text, int duration) {
        ToastMiui toastMiui = new ToastMiui(context, text, duration);
        return toastMiui;
    }

    public static ToastMiui MakeText(Context context, int toastStrId, int showTime) {
        ToastMiui toastMiui = new ToastMiui(context, context.getString(toastStrId), showTime);
        return toastMiui;
    }

    /**
     * 在构造方法中，更多的是对数据的初始化，由于设置布局参数比较多，所以单独抽出一个函数来
     */
    @SuppressLint("InflateParams")
    public ToastMiui(Context context, CharSequence text, int duration) {
        mContext = context;
        //记录Toast的显示长短类型
        mShowTime = duration;
        //记录当前Toast的内容是否已经在显示
        mIsShow = false;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //通过inflate获取自定义的Toast布局文件
        mToastView = LayoutInflater.from(context).inflate(R.layout.toast_custom_view, null);
        TextView tv = (TextView) mToastView.findViewById(R.id.tv_toast);
        tv.setText(text);
        //设置布局参数
        setParams();
    }

    /**
     * 设置布局参数
     * 这个设置参数更多是参考源代码中原生Toast的设置参数的类型
     * 在这里我们需要注意的是 mParams.windowAnimations = R.style.anim_view;这个是我们这个仿MIUI的Toast动画实现的基石。
     */
    private void setParams() {
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        //设置进入退出动画效果
        mParams.windowAnimations = R.style.anim_view;
        if (Build.VERSION.SDK_INT > 25) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mParams.alpha = 1.0f;
        mParams.setTitle("ToastMiui");
        mParams.packageName = mContext.getPackageName();
        //设置显示的位置
        mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        //设置偏移量
        mParams.y = 50;
    }

    /**
     * 1、gravity是输入Toast需要显示的位置，例如CENTER_VERTICAL（垂直居中）、CENTER_HORIZONTAL（水平居中）、TOP（顶部）等等。
     * 2、xOffset则是决定Toast在水平方向（x轴）的偏移量，偏移量单位为，大于0向右偏移，小于0向左偏移
     * 3、yOffset决定Toast在垂直方向（y轴）的偏移量，大于0向下偏移，小于0向上偏移，想设大值也没关系，反正Toast不会跑出屏幕。
     */
    public void setGravity(int gravity, int xOffset, int yOffset) {
        mParams.gravity = gravity;
        mParams.x = xOffset;
        mParams.y = yOffset;
    }

    public void setText(CharSequence s) {
        TextView tv = mToastView.findViewById(R.id.tv_toast);
        tv.setText(s);
    }

    public void setText(int resId) {
        TextView tv =  mToastView.findViewById(R.id.tv_toast);
        tv.setText(resId);
    }

    public void show() {
        //如果Toast没有显示，则开始加载显示
        if (!mIsShow) {
            mIsShow = true;
            mWindowManager.addView(mToastView, mParams);
            if (mHandler == null) {
                mHandler = new Handler();
            }
            mHandler.postDelayed(timerRunnable, mShowTime);
        }
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            removeView();
        }
    };

    public void removeView() {
        if (mToastView != null && mToastView.getParent() != null) {
            mWindowManager.removeView(mToastView);
            mHandler.removeCallbacks(timerRunnable);
            mIsShow = false;
        }
    }

    public boolean isShowIng() {
        return mIsShow;
    }
}
