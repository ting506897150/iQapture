package com.example.vcserver.iqapture.util;

import android.app.Activity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by 韩莫熙 on 2017/5/17.
 * 用于管理所有activity,和在前台的 activity
 * 可以通过直接持有AppManager对象执行对应方法
 * 也可以通过eventbus post事件,远程遥控执行对应方法
 */

public class AppManager {
    private static AppManager mManager;
    //管理所有activity
    public List<Activity> mActivityList;

    private AppManager() {
    }

    public static AppManager getInstant() {
        if (mManager == null) {
            synchronized (AppManager.class) {
                if (mManager == null) {
                    mManager = new AppManager();
                }
            }
        }
        return mManager;
    }


    /**
     * 返回一个存储所有未销毁的activity的集合
     *
     * @return
     */
    public List<Activity> getActivityList() {
        if (mActivityList == null) {
            mActivityList = new LinkedList<>();
        }
        return mActivityList;
    }



    /**
     * 添加Activity到集合
     */
    public void addActivity(Activity activity) {
        if (mActivityList == null) {
            mActivityList = new LinkedList<>();
        }
        synchronized (AppManager.class) {
            if (!mActivityList.contains( activity )) {
                mActivityList.add( activity );
            }
        }
    }


    /**
     *移除解绑 - 防止内存泄漏(当前activity销毁时从mActivityList里面删除记录)
     *@param detachActivity
     */
    public synchronized void destory(Activity detachActivity) {
        int size = mActivityList.size();
        for (int i = 0; i < size; i++) {
            if (mActivityList.get(i) == detachActivity) {
                mActivityList.remove(i);
                size--;
                i--;
            }
        }
    }


    /** * 根据Activity的类名关闭 Activity */
    public void finish(Class<? extends Activity> activityClass) {
        for (int i = 0; i < mActivityList.size(); i++) {
            Activity activity = mActivityList.get(i);
            if (activity.getClass().getCanonicalName().equals(activityClass.getCanonicalName())) {
                activity.finish();
                break;
            }
        }
    }


    /**
     * 关闭所有activity
     */
    public void killAll() {
        Iterator<Activity> iterator = getActivityList().iterator();
        while (iterator.hasNext()) {
            iterator.next().finish();
            iterator.remove();
        }
    }


    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            killAll();
            if (mActivityList != null)
                mActivityList = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
