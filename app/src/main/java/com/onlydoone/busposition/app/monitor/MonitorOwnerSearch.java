package com.onlydoone.busposition.app.monitor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.Utils.http.HttpCallbackListener;
import com.onlydoone.busposition.Utils.http.HttpUtil;
import com.onlydoone.busposition.adapter.SimpleTreeAdapter;
import com.onlydoone.busposition.bean.Bean;
import com.onlydoone.busposition.bean.Node;
import com.onlydoone.busposition.adapter.TreeListViewAdapter;
import com.onlydoone.busposition.classs.ConstantClass;
import com.onlydoone.busposition.classs.MonitorApplication;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 所属业户信息
 * Created by zhaohui on 2017/3/1.
 */
public class MonitorOwnerSearch extends Activity implements View.OnClickListener {

    String intentClass;
    int ids = 0;
    int types = 0;
    int id;

    public static ObjectMapper objectMapper = new ObjectMapper();
    /**
     * 获取用户所属业户信息
     */
    private SharedPreferences sp;
    private ProgressDialog dialog;

    private TextView title_context;
    private ImageView title_break;

    private List<Bean> mDatas = new ArrayList<Bean>();
    private ListView mTree;
    private SimpleTreeAdapter mAdapter;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //Logs.e("业户信息", msg.obj.toString());
                    try {
                        Map map = objectMapper.readValue(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map.get("result").equals("0")) {
                            mDatas.add(new Bean(0, 0, "车辆信息", 0, ""));
                            Bean bean = null;
                            List<Bean> list = new ArrayList<Bean>();
                            for (Object li : (List) map.get("vehicles")) {
                                Map temp = (Map) li;
                                ids = ids + 1;
                                bean = new Bean(ids, 0, temp.get("ownername").toString(), 1, temp.get("id_owner").toString());
                                list.add(bean);
                                //Logs.e(">>>>>>>>>>", temp.get("ownername").toString());
                            }
                            mAdapter.addData(list);
                            dialog.hide();
                            mAdapter.notifyDataSetChanged();
                        }else {
                            ToastUtils.showToast(MonitorOwnerSearch.this, "暂无业户信息");
                        }
                        //VersionInfo vehicleInfo = objectMapper.readValue(msg.obj.toString(), VersionInfo.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case -1:
                    ToastUtils.showToast(MonitorOwnerSearch.this, ConstantClass.CONNECTION_EXCEPTION);
                    break;
            }
        }
    };

    public static Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_vehicle_search);
        mActivity = this;
        MonitorApplication.getInstance().addActivity(MonitorOwnerSearch.this);
        initView();
    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        //加载车队信息
        //读取用户登录状态
        sp = this.getSharedPreferences("login_state", this.MODE_PRIVATE);

        mTree = (ListView) findViewById(R.id.id_tree);
        title_context = (TextView) findViewById(R.id.title_context);
        title_context.setText("请选择所属业户");

        Bundle bundle = getIntent().getExtras();
        intentClass = bundle.getString("class");

        title_break = (ImageView) findViewById(R.id.title_break);

        //title返回按钮点击事件
        title_break.setOnClickListener(this);

        //设置进度条
        dialog = new ProgressDialog(MonitorOwnerSearch.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("正在查询中···");
        dialog.setCanceledOnTouchOutside(false);
        //防止dialog所属的activity被销毁，或者重载时，崩溃
        if ( dialog != null ) {
            Activity activity = dialog.getOwnerActivity();
            if ( activity != null && !activity.isFinishing() ) {
                dialog.show();
            }
        }

        //加载服务端业户信息
        getHttpVehicleDate(0, sp.getString("id_owner", ""));

        try {
            mAdapter = new SimpleTreeAdapter<Bean>(mTree, this, mDatas, 3);
            mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                @Override
                public void onClick(final Node node, int position) {
                    if (node.isLeaf()) {
                        switch (node.getLevel()) {
                            case 0:
                                break;
                            case 1:
                                if (intentClass.equals("MonitorVehiclePolice")){
                                    Intent  data =new Intent();
                                    data.putExtra("id_owner",node.getName());//数据放到data里面去
                                    setResult(6,data);//返回data，2为result，data为intent对象  d
                                    finish();//页面销毁
                                }else {
                                    Intent intent = new Intent(MonitorOwnerSearch.this, MonitorQueueSearch.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", node.getName());
                                    bundle.putString("id_owner", node.getAlias());
                                    bundle.putString("class", intentClass);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                                break;
                        }
                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        mTree.setAdapter(mAdapter);
    }

    //加载所属业户信息
    private void getHttpVehicleDate(final int type, String name) {

        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", type);
        params.put("name", name);
        //请求数据类型（为0 时请求该用户所属业户，1 车队信息，0 时请求车队的全部车辆信息）

        HttpUtil.sendHttpRequestForPost(sp.getString("URL","") + ConstantClass.URL_VEHICLE_MONITOR, params, new HttpCallbackListener() {
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
                msg.obj = -1;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_break:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面销毁时，销毁dialog，防止程序崩溃
        if ( dialog != null && dialog.isShowing()) {
            Activity activity = dialog.getOwnerActivity();
            if ( activity != null && !activity.isFinishing()) {
                dialog.dismiss();
            }
        }
    }
}
