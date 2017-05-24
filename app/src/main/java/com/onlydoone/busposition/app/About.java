package com.onlydoone.busposition.app;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.fragment.Me;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaohui on 2017/4/5.
 */
public class About extends Activity implements View.OnClickListener{

    //title 返回按钮
    private ImageView titleBreak;
    //title显示内容
    private TextView titleContext;
    //版本号
    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        //初始化控件
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        titleBreak = (ImageView) findViewById(R.id.title_break);
        titleContext = (TextView) findViewById(R.id.title_context);
        tvVersion = (TextView) findViewById(R.id.tv_version);

        tvVersion.setText(getVersion().get("versionName").toString());
        titleContext.setText("关于");

        //title返回按钮点击事件
        titleBreak.setOnClickListener(this);
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_break:
                finish();
                break;
        }
    }

    /**
     * 获取当前版本号
     */
    public Map<Object, Object> getVersion() {
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
}
