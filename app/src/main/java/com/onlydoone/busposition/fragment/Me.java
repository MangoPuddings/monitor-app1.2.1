package com.onlydoone.busposition.fragment;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.Utils.dialog.Dialog_QrCode;
import com.onlydoone.busposition.Utils.dialog.Dialog_Version;
import com.onlydoone.busposition.Utils.dialog.Dialog_Version_APK;
import com.onlydoone.busposition.Utils.http.HttpCallbackListener;
import com.onlydoone.busposition.Utils.http.HttpUtil;
import com.onlydoone.busposition.Utils.versionUtil.VersionInfo;
import com.onlydoone.busposition.app.About;
import com.onlydoone.busposition.app.Login;
import com.onlydoone.busposition.app.Set;
import com.onlydoone.busposition.classs.ConstantClass;

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
 * Created by Administrator on 2016/12/17 0017.
 */

public class Me extends Fragment implements View.OnClickListener {
            SharedPreferences sp;
            File file;
            private VersionInfo versionInfo;
            private Dialog_Version.Builder builder = null;
            private Dialog_QrCode.Builder builderQrCode = null;
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
                            version_dialog.hide();
                            //获取json对象，解析数据
                            JSONObject jsonObject = null;
                            try {

                                jsonObject = new JSONObject(formatString(msg.obj.toString()));
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

                                    builder = new Dialog_Version.Builder(getActivity());
                                    builder.setDialog_update_title(versionNameServer);
                                    builder.setDialog_update_content(versionDesc);
                                    builder.setDialog_update_msg_size(versionSize);

                                    //弹出新版本更新提示对话框
                                    dialogVersionUpdateShow();
                                } else if (result.equals("-1")) {
                                    ToastUtils.showToast(getActivity(), "已是最新版本");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastUtils.showToast(getActivity(), "json解析错误");
                            }
                            break;
                        case 1:
                            version_dialog.hide();

                            break;
                        case 2:
                            //更新下載進度條
                            builderApk.progressBar.setProgress(msg.arg1);
                            if (builderApk.progressBar.getMax() == builderApk.progressBar.getProgress()){
                                builderApk.btn_update_id_ok_apk.setVisibility(View.VISIBLE);
                                builderApk.btn_update_id_cancel_apk.setVisibility(View.VISIBLE);
                            }
                            break;
                        case -1:
                            version_dialog.hide();
                            ToastUtils.showToast(getActivity(), "获取版本号失败");
                            break;
                        case -2:
                            version_dialog.hide();
                            ToastUtils.showToast(getActivity(), "下载APK失败，请检查网络是否连接");
                            break;
                    }
                }
            };

            /**
             * 版本检测version_dialog
             */
            private ProgressDialog version_dialog;
            /**
             * 用户名/账号id
             */
            private TextView username,userId;
            /**
             * 当前版本
             */
            private TextView versionName;
            /**
             * 版本更新检测/分享应用/设置/退出当前账号
             */
            private LinearLayout tv_version_update,tv_share,tv_sets,tv_quit_login;
            /**
             * ic_about
             */
            private LinearLayout about;
            /**
             * title返回按钮
             */
            private ImageView iv_break;
            /**
             * title显示内容
             */
            private TextView tv_context;
            /**
             * 初始化view
             */
            private View view;

            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
                view = inflater.inflate(R.layout.me, container, false);

                //初始化控件
                initView();
                return view;
            }

            /**
             * 初始化控件
             */
        private void initView() {
            username = (TextView) view.findViewById(R.id.username);
            tv_version_update = (LinearLayout) view.findViewById(R.id.version_update);
            about = (LinearLayout) view.findViewById(R.id.about);
            tv_share = (LinearLayout) view.findViewById(R.id.tv_share);
            tv_sets = (LinearLayout) view.findViewById(R.id.tv_sets);
            userId = (TextView) view.findViewById(R.id.userId);
            versionName = (TextView) view.findViewById(R.id.versionName);
            tv_quit_login = (LinearLayout) view.findViewById(R.id.tv_quit_login);

            tv_context = (TextView) view.findViewById(R.id.title_context);
            iv_break = (ImageView) view.findViewById(R.id.title_break);
            iv_break.setVisibility(View.GONE);

            //获取当前版本
            versionName.setText("当前版本：" + getVersion().get("versionName").toString());

            //读取用户登录状态
            sp = view.getContext().getSharedPreferences("login_state", view.getContext().MODE_PRIVATE);
            //获取用户名并显示，如果不存在则显示默认值“登录”
            String user = sp.getString("userName", "");
            //账号
            userId.setText(sp.getString("username",""));
            tv_context.setText("个人中心");
            //获取用户登录状态（是否登录ture（已登录）false（未登录）），如果不存在默认为false
            String login_info = sp.getString("login_info", "false");
            if (user.equals("")) {
                username.setText("未登录");
            } else {
                //用户名
                username.setText(user);
            }

            //版本检测点击监听事件
            tv_version_update.setOnClickListener(this);
        //关于点击监听事件
        about.setOnClickListener(this);
        //分享应用点击事件
        tv_share.setOnClickListener(this);
        //设置点击事件
        tv_sets.setOnClickListener(this);
        //关于点击监听事件
        about.setOnClickListener(this);
        //退出当前账号点击事件
        tv_quit_login.setOnClickListener(this);

    }

    /**
     * 更新新版本进度条
     */
    private void builderApkShow() {
        builderApk = new Dialog_Version_APK.Builder(getActivity());
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
                            ToastUtils.showToast(getActivity(), "请检查网络是否连接");
                        }
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

    }

    /**
     * 点击监听事件
     *
     * @param view
     */
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.version_update:
                //获取服务器版本号
                getHttpVersion();
                break;
            case R.id.about:
                //关于
                Intent intent1 = new Intent(getActivity(), About.class);
                startActivity(intent1);
                break;
            case R.id.tv_share:
                //弹出二维码dialog
                builderQrCode = new Dialog_QrCode.Builder(getActivity());
                builderQrCode.create().show();
                break;
            case R.id.tv_sets:
                //设置
                Intent intent = new Intent(getActivity(), Set.class);
                startActivity(intent);
                break;
            case R.id.tv_quit_login:
                //退出登录，跳转至登录界面
                Intent intent2 = new Intent(getActivity(),Login.class);
                startActivity(intent2);
                break;
        }
    }

    /**
     * 获取服务器版本号
     */
    private void getHttpVersion() {
        //车辆查询进度条
        version_dialog = new ProgressDialog(getActivity());
        version_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        version_dialog.setMessage("正在查询中···");
        version_dialog.setCanceledOnTouchOutside(false);
        version_dialog.show();
        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("versionCode", getVersion().get("versionCode").toString());

        HttpUtil.sendHttpRequestForPost( sp.getString("URL","") + ConstantClass.URL_VERSION, params, new HttpCallbackListener() {
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
    public  Map<Object, Object> getVersion() {
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
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

    /**
     * 去除bom报头
     */
    public static String formatString(String s) {
        if (s != null) {
            s = s.replaceAll("\ufeff", "");
        }
        return s;
    }

}
