package com.example.administrator.jay;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

/**
 * ================================================
 *
 * @author :Administrator
 * @version :
 * @date :2019/4/2/10:46
 * @ProjectNameDescribe :MainActivity主页面
 * 修订历史：
 * ================================================
 */
public class MainActivity extends AppCompatActivity {
    private XWalkSettings webSettings;
    XWalkView xWalkView;
    ToastMiui toastMiui = null;
    private Handler handler = new Handler();
    /**
     * 已经抬手，且2秒未触碰屏幕
     **/
    private long time = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + MainActivity.this.getPackageName()));
                startActivity(intent);
                return;
            }
            Toast.makeText(MainActivity.this,"获得了权限",Toast.LENGTH_SHORT).show();
        }
        //设置允许访问浏览器页面的js方法
        XWalkPreferences.setValue("enable-javascript", true);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        xWalkView = (XWalkView) findViewById(R.id.xwalk);
        xWalkView.setHorizontalScrollBarEnabled(false);
        xWalkView.setVerticalScrollBarEnabled(false);
        xWalkView.setScrollBarStyle(XWalkView.SCROLLBARS_OUTSIDE_INSET);
        xWalkView.setScrollbarFadingEnabled(true);
      // xWalkView.load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552647291033&di=99f08a9b847448f5af669a68f1ae03ad&imgtype=jpg&src=http%3A%2F%2Fimg3.imgtn.bdimg.com%2Fit%2Fu%3D1376799768%2C881308082%26fm%3D214%26gp%3D0.jpg", null);
         xWalkView.load("http://m.okhqb.com/item/description/1000334264.html?fromApp=true", null);
        //不使用缓存
        xWalkView.setDrawingCacheEnabled(false);
        //清除历史记录
        xWalkView.getNavigationHistory().clear();
        //清楚包括磁盘缓存
        xWalkView.clearCache(true);
        //焦点
        xWalkView.setFocusable(false);
        webSettings = xWalkView.getSettings();
        //缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        xWalkView.setResourceClient(new XWalkResourceClient(xWalkView) {
            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
            }

            @Override
            public void onLoadStarted(XWalkView view, String url) {
                super.onLoadStarted(view, url);
            }
        });
        //页面按下了
        xWalkView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //移动过程
                    case MotionEvent.ACTION_MOVE:
                        //使移动过程始终只有一个弹窗
                        if (toastMiui == null) {
                            toastMiui = ToastMiui.MakeText(MainActivity.this, "我的弹窗", ToastMiui.LENGTH_SHORT);
                        } else {
                            toastMiui.setText("我的弹窗");
                        }
                        toastMiui.show();
                        //移动过程移除handler
                        handler.removeCallbacks(runnable);
                        break;
                    //抬手
                    case MotionEvent.ACTION_UP:
                        //抬手先移再绑定，启动监听
                        startAD();
                        break;

                    default:
                        break;
                }

                return false;
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (xWalkView != null) {
            xWalkView.pauseTimers();
            xWalkView.onHide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (xWalkView != null) {
            xWalkView.resumeTimers();
            xWalkView.onShow();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (xWalkView != null) {
            xWalkView.onDestroy();
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (toastMiui!=null){
                toastMiui.removeView();
            }

        }
    };

    public void startAD() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, time);
    }

}
