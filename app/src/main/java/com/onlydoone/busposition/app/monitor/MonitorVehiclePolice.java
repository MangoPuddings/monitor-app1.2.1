package com.onlydoone.busposition.app.monitor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.Utils.http.HttpCallbackListener;
import com.onlydoone.busposition.Utils.http.HttpUtil;
import com.onlydoone.busposition.adapter.VehiclePoliceListViewAdapter;
import com.onlydoone.busposition.bean.VehiclePoliceListViewData;
import com.onlydoone.busposition.classs.ConstantClass;

import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车辆报警查询
 * Created by zhaohui on 2017/3/14.
 */
public class MonitorVehiclePolice extends Activity implements View.OnClickListener {
    int page = 0;
    String[] mun;
    String[] types;
    /**
     * 初始话用户信息文件
     */
    private SharedPreferences sp;
    /**
     * dialog
     */
    private ProgressDialog dialog;
    /**
     * 车辆报警查询按钮
     */
    private TextView tv_police_query, tv_police_clear, tv_police_cancel;
    /**
     * 选择业户，车辆选择，报警类型选择
     */
    private LinearLayout layout_police_owner, layout_police_vehicleid, layout_police_type, layout_policle_query;
    /**
     * 车辆，报警类型,业户
     */
    private TextView tv_police_vehicleid, tv_police_type, tv_police_owner;
    /**
     * title删选按钮,返回按钮
     */
    private ImageView title_search, title_break;
    /**
     * title显示内容
     */
    private TextView title_context;
    /**
     * 车辆报警信息listview
     */
    private ListView lv_police_vehicle;
    /**
     * 车辆报警listview适配器
     */
    private VehiclePoliceListViewAdapter mVehiclePoliceAdapter;
    /**
     * 动画
     */
    private AlphaAnimation mAnimation;
    /**
     * 报警信息list集合
     */
    private List<VehiclePoliceListViewData> list;
    //是否第一次查询 -1 第一次查询，0不是第一次查询
    int i = -1;
    //是否有查询条件 -1 没有 0有
    int t = -1;
    private View mFooterView;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //Logs.e("---", msg.obj.toString());
                    try {
                        Map map = VehicleQuery.objectMapper.readValue(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map.get("result").equals("0")) {
                            VehiclePoliceListViewData data;
                            list = new ArrayList<>();
                            for (Object li : (List) map.get("vehicles")) {
                                Map temp = (Map) li;
                                data = new VehiclePoliceListViewData(temp.get("vehicleid").toString(), temp.get("operatedname").toString(),
                                        temp.get("queuename").toString(), temp.get("warnType").toString(), temp.get("starttime").toString());
                                list.add(data);
                            }

                            mVehiclePoliceAdapter = new VehiclePoliceListViewAdapter(MonitorVehiclePolice.this, list);
                            lv_police_vehicle.setAdapter(mVehiclePoliceAdapter);
                            mVehiclePoliceAdapter.notifyDataSetChanged();
                            page = page + 20;
                            if (dialog.isShowing()){
                                dialog.cancel();
                            }

                        } else {
                            ToastUtils.showToast(MonitorVehiclePolice.this, "暂无报警信息");
                            if (dialog.isShowing()){
                                dialog.cancel();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (dialog.isShowing()){
                            dialog.cancel();
                        }
                    }
                    break;
                case 1:
                    if (ConstantClass.isFootitemPolice == -1) {
                        ConstantClass.isFootitemPolice = 0;
                        lv_police_vehicle.addFooterView(mFooterView);
                        mVehiclePoliceAdapter.notifyDataSetChanged();

                        if (t == 0) {
                            getHttpPoliceData(tv_police_vehicleid.getText().toString().trim(), "0");
                        }else {
                            getHttpPoliceData("", "");
                        }
                    }
                    break;
                case 2:
                    //Logs.e("---", msg.obj.toString());
                    try {
                        Map map = VehicleQuery.objectMapper.readValue(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map.get("result").equals("0")) {
                            if (dialog.isShowing()){
                                dialog.cancel();
                            }
                            VehiclePoliceListViewData data;

                            for (Object li : (List) map.get("vehicles")) {
                                Map temp = (Map) li;
                                data = new VehiclePoliceListViewData(temp.get("vehicleid").toString(), temp.get("operatedname").toString(),
                                        temp.get("queuename").toString(), temp.get("warnType").toString(), temp.get("starttime").toString());
                                mVehiclePoliceAdapter.addItem(data);
                            }
                            lv_police_vehicle.removeFooterView(mFooterView);
                            mVehiclePoliceAdapter.notifyDataSetChanged();
                            ConstantClass.isFootitemPolice = -1;
                            page = page + 20;
                        } else {
                            if (dialog.isShowing()){
                                dialog.cancel();
                            }
                            lv_police_vehicle.removeFooterView(mFooterView);
                            ToastUtils.showToast(MonitorVehiclePolice.this,"已加载全部数据");
                        }
                    } catch (IOException e) {
                        if (dialog.isShowing()){
                            dialog.cancel();
                        }
                        e.printStackTrace();
                    }
                    break;
                case 3:

                    break;
                case -1:
                    ToastUtils.showToast(MonitorVehiclePolice.this, ConstantClass.CONNECTION_EXCEPTION);
                    if (dialog.isShowing()){
                        dialog.cancel();
                    }
                    break;
            }
        }
    };

    private void sendMsg() {
        Message msg = new Message();
        msg.what = 3;
        handler.sendMessage(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_vehicle_police);

        initView();

        dialog = new ProgressDialog(MonitorVehiclePolice.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("查询中···");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //读取用户登录状态
        sp = this.getSharedPreferences("login_state", this.MODE_PRIVATE);
        //添加底部正在加载布局
        mFooterView = this.getLayoutInflater().inflate(R.layout.listview_footer_view, null);
        tv_police_cancel = (TextView) findViewById(R.id.tv_police_cancel);
        title_break = (ImageView) findViewById(R.id.title_break);
        title_search = (ImageView) findViewById(R.id.title_search);
        title_context = (TextView) findViewById(R.id.title_context);
        tv_police_query = (TextView) findViewById(R.id.tv_police_query);
        layout_policle_query = (LinearLayout) findViewById(R.id.layout_policle_query);
        layout_police_vehicleid = (LinearLayout) findViewById(R.id.layout_police_vehicleid);
        layout_police_owner = (LinearLayout) findViewById(R.id.layout_police_owner);
        layout_police_type = (LinearLayout) findViewById(R.id.layout_police_type);
        tv_police_vehicleid = (TextView) findViewById(R.id.tv_police_vehicleid);
        tv_police_type = (TextView) findViewById(R.id.tv_police_type);
        tv_police_owner = (TextView) findViewById(R.id.tv_police_owner);
        tv_police_clear = (TextView) findViewById(R.id.tv_police_clear);

        lv_police_vehicle = (ListView) findViewById(R.id.lv_police_vehicle);

        //获取服务器车辆报警信息
        getHttpPoliceData("", "");

        //listview滑动监听
        lv_police_vehicle.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 判断滚动到底部
                if (lv_police_vehicle.getLastVisiblePosition() == (lv_police_vehicle.getCount() - 1)) {
                    i = 0;
                    if (scrollState == 0) {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        title_context.setText("车辆报警");
        //title返回点击事件
        title_break.setOnClickListener(this);
        //title筛选点击事件
        title_search.setOnClickListener(this);
        title_search.setVisibility(View.VISIBLE);
        //车辆号码点击事件
        layout_police_vehicleid.setOnClickListener(this);
        //报警类型点击事件
        layout_police_type.setOnClickListener(this);
        //业户点击事件
        layout_police_owner.setOnClickListener(this);
        //查询按钮点击事件
        tv_police_query.setOnClickListener(this);
        //清空查询条件
        tv_police_clear.setOnClickListener(this);
        //关闭查询按钮点击事件
        tv_police_cancel.setOnClickListener(this);

    }

    /**
     * 获取服务器车辆报警信息
     */
    private void getHttpPoliceData(String vehicleNo, String type) {
        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleid", vehicleNo);
        params.put("page", page);
        params.put("rows", 20);
        params.put("id_owner", sp.getString("id_owner",""));
        if (!type.equals("0")) {
            params.put("num", 0);
            //params.put("id_warning", type);
        } else {
            params.put("num", 0);
            if (mun != null) {
                params.put("num", mun.length);
                for (int i = 0; i < mun.length; i++) {
                    params.put("id_warning" + i, mun[i]);
                }
            }
        }
        HttpUtil.sendHttpRequestForPost(sp.getString("URL","") + ConstantClass.URL_VEHICLE_POLICE, params, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if (i == -1) {
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = 0;
                    handler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onError(Exception e) {
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });
    }

    int width;

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_break:
                //返回
                finish();
                break;
            case R.id.title_search:
                //筛选
                /**
                 * 获取屏幕宽度并且除以4
                 */
                DisplayMetrics dm = new DisplayMetrics();
                this.getWindowManager().getDefaultDisplay().getMetrics(dm);
                width = dm.widthPixels;
                if (layout_policle_query.getVisibility() == View.GONE) {

                    TranslateAnimation translateAnimation = new TranslateAnimation(width, 0, 0, 0);
                    translateAnimation.setDuration(500);
                    layout_policle_query.setAnimation(translateAnimation);
                    layout_policle_query.setVisibility(View.VISIBLE);
                } else {

                    TranslateAnimation translateAnimation = new TranslateAnimation(0, width, 0, 0);
                    translateAnimation.setDuration(500);
                    layout_policle_query.setAnimation(translateAnimation);
                    layout_policle_query.setVisibility(View.GONE);
                }
                break;
            case R.id.layout_police_vehicleid:
                //车辆号码
                Intent intent = new Intent(this, VehicleQuery.class);
                Bundle bundle = new Bundle();
                bundle.putString("vehicleid", tv_police_vehicleid.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
                break;
            case R.id.layout_police_type:
                //报警类型
                Intent intent1 = new Intent(this, PoliceTypeQuery.class);
                Bundle bundle3 = new Bundle();
                String type = "";
                for (int i = 0; i < ConstantClass.num; i++){
                    bundle3.putString("type" + i, mun[i]);
                    bundle3.putString("num" + i, types[i]);
                }
                intent1.putExtras(bundle3);
                startActivityForResult(intent1, 3);
                break;
            case R.id.layout_police_owner:
                //业户
                Intent intent2 = new Intent(this, MonitorOwnerSearch.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString("class", "MonitorVehiclePolice");
                intent2.putExtras(bundle2);
                startActivityForResult(intent2, 5);
                break;
            case R.id.tv_police_query:
                //筛选条件确定按钮
                if (tv_police_vehicleid.getText().equals("") && tv_police_type.getText().equals("")) {
                    ToastUtils.showToast(this, "查询条件不能为空");
                } else {
                    dialog.show();
                    page = 0;
                    i = -1;
                    t = 0;
                    getHttpPoliceData(tv_police_vehicleid.getText().toString().trim(), "0");
                    TranslateAnimation translateAnimation = new TranslateAnimation(0, width, 0, 0);
                    translateAnimation.setDuration(500);
                    layout_policle_query.setAnimation(translateAnimation);
                    layout_policle_query.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_police_clear:
                //筛选条件重置按钮
                ConstantClass.isFootitemPolice = -1;
                page = 0;
                i = -1;
                t = -1;
                tv_police_vehicleid.setText("");
                tv_police_type.setText("");
                tv_police_owner.setText("");
                ConstantClass.num = 0;
                mun = null;
                types = null;
                getHttpPoliceData("","");
                break;
            case R.id.tv_police_cancel:
                TranslateAnimation translateAnimation = new TranslateAnimation(0, width, 0, 0);
                translateAnimation.setDuration(500);
                layout_policle_query.setAnimation(translateAnimation);
                layout_policle_query.setVisibility(View.GONE);
                break;
        }
    }


    /**
     * onActivityResult接收数据的方法
     * requestCode：请求的标志
     * resultCode：第二个页面返回的标志，哪个页面跳转的标识
     * data：第二个页面回传的数据，data是回传一个intent对象
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            //通过请求码(去SActivity)和回传码（回传数据到第一个页面）判断回传的页面
            tv_police_vehicleid.setText(data.getStringExtra("vehicleid"));//textView得到字符串
        }
        if (requestCode == 3 && resultCode == 4) {
            //通过请求码(去SActivity)和回传码（回传数据到第一个页面）判断回传的页面
            //int num = Integer.valueOf(data.getStringExtra("num"));
            mun = new String[ConstantClass.num];
            types = new String[ConstantClass.num];
            String type = "";
            for (int i = 0; i < ConstantClass.num; i++) {
                mun[i] = data.getStringExtra("num" + i);
                types[i] = data.getStringExtra("num" + i);
                type = type + data.getStringExtra("type" + i) + ",";
            }
            tv_police_type.setText(type);//textView得到字符串
        }
        if (requestCode == 5 && resultCode == 6) {
            //通过请求码(去SActivity)和回传码（回传数据到第一个页面）判断回传的页面
            tv_police_owner.setText(data.getStringExtra("id_owner"));//textView得到字符串
        }

    }

}
