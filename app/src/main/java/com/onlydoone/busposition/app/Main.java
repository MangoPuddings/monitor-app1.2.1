package com.onlydoone.busposition.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.IsServiceWorkedUtil;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.Utils.dialog.Dialog_Version;
import com.onlydoone.busposition.Utils.dialog.Dialog_Version_APK;
import com.onlydoone.busposition.Utils.http.HttpCallbackListener;
import com.onlydoone.busposition.Utils.http.HttpUtil;
import com.onlydoone.busposition.Utils.versionUtil.VersionInfo;
import com.onlydoone.busposition.classs.ConstantClass;
import com.onlydoone.busposition.classs.SysApplication;
import com.onlydoone.busposition.fragment.Inquiry;
import com.onlydoone.busposition.fragment.Me;
import com.onlydoone.busposition.fragment.monitor.Monitor_;
import com.onlydoone.busposition.service.GuardNettyService;
import com.onlydoone.busposition.service.MyNettyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 车帘定位App   main类
 */
public class Main extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layout_inquiry, layout_monitor, layout_set;
    private TextView tv_inquiry, tv_monitor, tv_set;
    private ImageView iv_inquiry, iv_monitor, iv_set;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    //
    private Monitor_ monitor_fragment;
    private Inquiry map_fragment;
    private Me me_fragment;

    private long exitTime = 0;
    SharedPreferences sp;

    File file;
    private VersionInfo versionInfo;
    private Dialog_Version.Builder builder = null;
    private Dialog_Version_APK.Builder builderApk;

    /**
     * 接收子线程发送的数据进行UI更新
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //获取json对象，解析数据
                    JSONObject jsonObject = null;
                    try {

                        jsonObject = new JSONObject(Me.formatString(msg.obj.toString()));
                        //获取版本号返回状态码 0（发现新版本） -1（无新版本）
                        String result = jsonObject.getString("result");
                        //发现新版本，解析数据
                        if (result.equals("0")) {
                            //解析json数据
                            //如果返回状态码为0，则解析车辆信息
                            JSONObject json = jsonObject.getJSONObject("version");
                            String versionNameServer = json.getString("versionNameServer");
                            String versionSize = json.getString("versionSize");
                            String versionDesc = json.getString("versionDesc");

                            versionInfo = new VersionInfo();
                            versionInfo.setDownloadUrl(json.getString("versionURL"));
                            versionInfo.setVersionNameServer(versionNameServer);
                            versionInfo.setVersionSize(versionSize);

                            builder = new Dialog_Version.Builder(Main.this);
                            builder.setDialog_update_title(versionNameServer);
                            builder.setDialog_update_content(versionDesc);
                            builder.setDialog_update_msg_size(versionSize);

                            //弹出新版本更新提示对话框
                            dialogVersionUpdateShow();
                        } else if (result.equals("-1")) {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    break;
                case 2:
                    //更新下載進度條
                    builderApk.progressBar.setProgress(msg.arg1);
                    if (builderApk.progressBar.getMax() == builderApk.progressBar.getProgress()) {
                        builderApk.btn_update_id_ok_apk.setVisibility(View.VISIBLE);
                        builderApk.btn_update_id_cancel_apk.setVisibility(View.VISIBLE);
                    }
                    break;
                case -1:
                    ToastUtils.showToast(Main.this, "获取版本号失败");
                    break;
                case -2:
                    ToastUtils.showToast(Main.this, "下载APK失败，请检查网络是否连接");
                    break;
            }
        }
    };

    /**
     * 版本更新提示dialog
     */
    private void dialogVersionUpdateShow() {

        /**
         * 立即更新点击事件
         */
        builder.setDialog_version_ok_onClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获取服务端APK文件
                getHttpVersionAPK();
            }
        });

        /**
         * 下次再说点击事件
         */
        builder.setDialog_version_break_OnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //关闭弹出框
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    /**
     * 获取服务端apk文件
     */
    private void getHttpVersionAPK() {

        builderApkShow();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(versionInfo.getDownloadUrl());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    if (conn.getResponseCode() == 200) {
                        builderApk.progressBar.setMax(conn.getContentLength());
                        InputStream is = conn.getInputStream();
                        file = new File(Environment.getExternalStorageDirectory(), "app-debug.apk");
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buffer = new byte[1024];

                        int len;
                        int total = 0;
                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            total += len;
                            //获取当前下载量
                            Message msg = new Message();
                            msg.arg1 = total;
                            msg.what = 2;
                            handler.sendMessage(msg);
                        }
                        fos.close();
                        bis.close();
                        is.close();
                    } else {
                        ToastUtils.showToast(Main.this, "请检查网络是否连接");
                    }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 更新新版本进度条
     */
    private void builderApkShow() {
        builderApk = new Dialog_Version_APK.Builder(this);
        String name = versionInfo.getVersionNameServer();
        String size = versionInfo.getVersionSize();

        //暂停，继续，安装点击事件
        builderApk.setDialog_version_ok_apk_onClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                installApk(file);
                dialog.dismiss();
            }
        });
        //取消安装点击事件
        builderApk.setDialog_version_break_apk_OnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //关闭弹出框
                dialog.dismiss();
            }
        });
        builderApk.create().show();
        builderApk.tv_update_title.setText("新版本:" + name);
        builderApk.tv_update_msg_size.setText("新版本大小:" + size);
    }

    //安装apk
    public void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getSupportActionBar().hide();
        //读取用户登录状态
        SharedPreferences sp = this.getSharedPreferences("login_state", this.MODE_PRIVATE);
        //获取用户登录状态（是否登录ture（已登录）false（未登录）），如果不存在默认为false
        String isSwitch = sp.getString("switch", "close");
        //如果车辆在线率报警状态为open，则开启服务
        if (isSwitch.equals("open")){
            boolean b = IsServiceWorkedUtil.isServiceWorked(Main.this, "com.onlydoone.busposition.service.GuardNettyService");
            //如果报警服务未开启则从新开启该服务
            if(!b) {
                Log.e("GuardNettyService","启动服务 GuardNettyService");
                Intent service = new Intent(Main.this, MyNettyService.class);
                startService(service);
            }
        }

        //初始化控件
        initView();
        SysApplication.getInstance().addActivity(this);

    }

    /**
     * 2次返回键退出程序监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            //SysApplication.getInstance().exit();
            //System.exit(0);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        sp = this.getSharedPreferences("login_state", 0);
        tv_inquiry = (TextView) findViewById(R.id.tv_inquiry);
        tv_monitor = (TextView) findViewById(R.id.tv_monitor);
        tv_set = (TextView) findViewById(R.id.tv_set);
        iv_inquiry = (ImageView) findViewById(R.id.iv_inquiry);
        iv_monitor = (ImageView) findViewById(R.id.iv_monitor);
        iv_set = (ImageView) findViewById(R.id.iv_set);

        layout_inquiry = (LinearLayout) findViewById(R.id.layout_inquiry1);
        layout_monitor = (LinearLayout) findViewById(R.id.layout_monitor1);
        layout_set = (LinearLayout) findViewById(R.id.layout_set);

        //默认选中菜单inquiry
        iv_monitor.setImageResource(R.mipmap.monitor);
        iv_inquiry.setImageResource(R.mipmap.inquiry_no);
        iv_set.setImageResource(R.mipmap.set_no);

        tv_inquiry.setTextColor(Color.parseColor("#515151"));
        tv_monitor.setTextColor(Color.parseColor("#1396DB"));
        tv_set.setTextColor(Color.parseColor("#515151"));

        // 开启一个Fragment事务
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        //默认显示的fragment为inquiry_fragment
        monitor_fragment = new Monitor_();
        transaction.add(R.id.fragment_main, monitor_fragment);
        transaction.commit();

        //菜单点击事件
        layout_inquiry.setOnClickListener(this);
        layout_monitor.setOnClickListener(this);
        layout_set.setOnClickListener(this);

        //获取服务器版本号
        getHttpVersion();
    }

    /**
     * 获取服务器版本号
     */
    private void getHttpVersion() {
        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("versionCode", getVersion().get("versionCode").toString());

        HttpUtil.sendHttpRequestForPost(sp.getString("URL","") + ConstantClass.URL_VERSION, params, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message msg = new Message();
                msg.what = 0;
                msg.obj = response;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 获取当前版本号
     */
    private Map<Object, Object> getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String versionName = info.versionName;
            int versionCode = info.versionCode;
            Map<Object, Object> map = new HashMap<>();
            map.put("versionName", versionName);
            map.put("versionCode", versionCode);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            //Logs.e("VersionInfo", "Exception", e);
            return null;
        }
    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.layout_inquiry1:
                //点击菜单时选中第一个fragment
                setFragment(0);
                break;
            case R.id.layout_monitor1:
                //点击菜单时选中第二个fragment
                setFragment(1);
                break;
            case R.id.layout_set:
                //点击菜单时选中第三个fragment
                setFragment(2);
                break;
        }

    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 0表示inquiry，1表示content，2表示set。
     */
    private void setFragment(int index) {
        // 开启一个Fragment事务
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);

        //根据传进来的index，来切换fragment
        switch (index) {
            case 0:
                // 每次选中之前先清楚掉上次的选中状态
                clearSelection();

                iv_inquiry.setImageResource(R.mipmap.inquiry);
                tv_inquiry.setTextColor(Color.parseColor("#1396DB"));
                if (map_fragment == null) {
                    // 如果inquiry_fragment为空，则创建一个inquiry_fragment并添加到界面上
                    map_fragment = new Inquiry();
                    transaction.add(R.id.fragment_main, map_fragment);
                } else {
                    //如果inquiry_fragment不为空，则直接将它显示出来
                    transaction.show(map_fragment);
                }
                break;
            case 1:
                // 每次选中之前先清楚掉上次的选中状态
                clearSelection();
                tv_monitor.setTextColor(Color.parseColor("#1396DB"));
                iv_monitor.setImageResource(R.mipmap.monitor);

                if (monitor_fragment == null) {
                    // 如果content_fragment为空，则创建一个content_fragment并添加到界面上
                    monitor_fragment = new Monitor_();
                    transaction.add(R.id.fragment_main, monitor_fragment);

                } else {
                    //如果content_fragment不为空，则直接将它显示出来
                    transaction.show(monitor_fragment);
                }
                break;
            case 2:
                // 每次选中之前先清楚掉上次的选中状态
                clearSelection();

                iv_set.setImageResource(R.mipmap.set);
                tv_set.setTextColor(Color.parseColor("#1396DB"));
                if (me_fragment == null) {
                    // 如果set_fragment为空，则创建一个set_fragment并添加到界面上
                    me_fragment = new Me();
                    transaction.add(R.id.fragment_main, me_fragment);

                } else {
                    //如果set_fragment不为空，则直接将它显示出来
                    transaction.show(me_fragment);
                }
                break;
        }
        transaction.commit();

    }

    /**
     * 隐藏掉所有的fragment
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {

        if (map_fragment != null) {
            transaction.hide(map_fragment);
        }
        if (monitor_fragment != null) {
            transaction.hide(monitor_fragment);
        }
        if (me_fragment != null) {
            transaction.hide(me_fragment);
        }
    }

    /**
     * 清除所有菜单的选中状态
     */
    private void clearSelection() {
        iv_inquiry.setImageResource(R.mipmap.inquiry_no);
        iv_monitor.setImageResource(R.mipmap.monitor_no);
        iv_set.setImageResource(R.mipmap.set_no);

        tv_inquiry.setTextColor(Color.parseColor("#515151"));
        tv_monitor.setTextColor(Color.parseColor("#515151"));
        tv_set.setTextColor(Color.parseColor("#515151"));
    }

}
