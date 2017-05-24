package com.onlydoone.busposition.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhaohui on 2017/4/13.
 */

public class GuardNettyService extends Service{
    SharedPreferences sp;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sp = this.getSharedPreferences("login_state", this.MODE_PRIVATE);
        //开启子线程守护
        timer();
        return START_STICKY;
    }

    /**
     * 进程守护
     */
    private void timer() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                boolean b = isServiceWorked(GuardNettyService.this, "com.onlydoone.busposition.service.MyNettyService");
                if(!b) {
                    Intent service = new Intent(GuardNettyService.this, MyNettyService.class);
                    startService(service);
                }
            }
        };
        timer.schedule(task, 0, 1000);
    }

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
