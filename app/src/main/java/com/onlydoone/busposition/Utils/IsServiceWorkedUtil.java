package com.onlydoone.busposition.Utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;

/**
 *      判断service是否存在工具类
 * Created by zhaohui on 2017/4/13.
 */
public class IsServiceWorkedUtil {
    /**
     *  判断服务是否活着
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceWorked(Context context, String serviceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
