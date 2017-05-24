package com.onlydoone.busposition.classs;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhaohui on 2017/2/9.
 */

public class MonitorApplication extends Application {
    private List<Activity> mList = new LinkedList<Activity>();
    private static MonitorApplication instance;

    private MonitorApplication() {
    }

    public synchronized static MonitorApplication getInstance() {
        if (null == instance) {
            instance = new MonitorApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

}
