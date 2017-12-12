package com.nanchen.aiyagirl;

import android.app.Activity;
import android.app.Application;

import com.nanchen.aiyagirl.utils.Utils;
import com.squareup.leakcanary.LeakCanary;

import java.util.HashSet;
import java.util.Set;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackManager;

/**
 * 应用程序
 * <p>
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-04-07  14:41
 */

public class App extends Application {
    private static App INSTANCE;
    private Set<Activity> mActivities;

    public static synchronized App getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;

        // 初始化 LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);


        //必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回
        //第一个参数：应用程序上下文
        //第二个参数：如果发现滑动返回后立即触摸界面时应用崩溃，请把该界面里比较特殊的 View 的 class 添加到该集合中，目前在库中已经添加了 WebView 和 SurfaceView
        BGASwipeBackManager.getInstance().init(this);

        ConfigManage.INSTANCE.initConfig(this);
        Utils.init(this);
    }

    public void addActivity(Activity activity) {
        if (mActivities == null) {
            mActivities = new HashSet<>();
        }
        mActivities.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (mActivities != null) {
            mActivities.remove(activity);
        }
    }

    public void exitApp() {
        if (mActivities != null) {
            synchronized (mActivities) {
                for (Activity activity :
                        mActivities) {
                    activity.finish();
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
