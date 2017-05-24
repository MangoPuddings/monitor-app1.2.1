package com.onlydoone.busposition.app.monitor;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.Utils.http.HttpCallbackListener;
import com.onlydoone.busposition.Utils.http.HttpUtil;
import com.onlydoone.busposition.adapter.MonitorListViewQueueAdapter;
import com.onlydoone.busposition.bean.QueueData;
import com.onlydoone.busposition.fragment.VehicleNameFragment;
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
 * 所属车队信息
 * Created by zhaohui on 2017/3/7.
 */
public class MonitorQueueSearch extends Activity implements View.OnClickListener ,AdapterView.OnItemClickListener{

    public static ObjectMapper objectMapper = new ObjectMapper();
    SharedPreferences sp;
    /**
     * 业户id
     */
    private String id_owner;
    /**
     * 车队listview
     */
    private ListView lvQueue;
    /**
     * 车队listview适配器
     */
    private MonitorListViewQueueAdapter mQueueAdapter;
    /**
     * 车队Data
     */
    private List<QueueData> list;
    /**
     * 车辆搜索框
     */
    private EditText edit_search;
    /**
     * //初始化搜索框用户输入的内容
     */
    private String search_content;
    /**
     * 搜索框清空按钮点击事件
     */
    private ImageView iv_search_del;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    /**
     * fragment
     */
    private VehicleNameFragment mVehicleNameFragment;

    private ImageView titleBreak;
    private TextView titleContext;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //Logs.e("车队信息", msg.obj.toString());
                    try {
                        Map map = objectMapper.readValue(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map.get("result").equals("0")) {
                            list = new ArrayList<QueueData>();
                            QueueData queueData;
                            for (Object li : (List) map.get("vehicles")) {
                                Map temp = (Map) li;
                                queueData = new QueueData(temp.get("name").toString(),temp.get("id_queue").toString());
                                list.add(queueData);
                            }



                            mQueueAdapter = new MonitorListViewQueueAdapter(MonitorQueueSearch.this,list);
                            lvQueue.setAdapter(mQueueAdapter);
                            //默认选中第一个车队，并改变选中状态的颜色
                            mQueueAdapter.setTvQueueColor(0);
                            mQueueAdapter.notifyDataSetChanged();

                            //添加默认加载的车队id
                            ConstantClass.QUEUE_ID = mQueueAdapter.getQueueId().get(0).getQueueId();
                            // 开启一个Fragment事务
                            fragmentManager = getFragmentManager();
                            transaction = fragmentManager.beginTransaction();

                            //默认显示的fragment为inquiry_fragment
                            mVehicleNameFragment = new VehicleNameFragment();
                            transaction.add(R.id.layout_vehicle, mVehicleNameFragment);
                            transaction.commit();

                            editSearchTextChange();

                        }else {
                            ToastUtils.showToast(MonitorQueueSearch.this, "暂无车队信息");
                        }
                        //VersionInfo vehicleInfo = objectMapper.readValue(msg.obj.toString(), VersionInfo.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:

                    break;
                case -1:
                    break;
            }
        }
    };

    /**
     * 搜索框内容改变监听事件
     */
    private void editSearchTextChange() {
        //搜索框内容改变监听事件
        edit_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * 输入框监听事件
             * Text改变最后被调用
             */
            @Override
            public void afterTextChanged(Editable s) {
                search_content = edit_search.getText().toString().trim();
                //如果搜索框内容为空则隐藏清除按钮，否则显示清除按钮
                if (search_content.equals("")) {
                    ConstantClass.isSearch = false;
                    iv_search_del.setVisibility(View.GONE);
                    //getHttpVehicleDate(1,id_owner);
                }
                if (!(search_content.equals(""))) {
                    mQueueAdapter.setTvQueueColor(-1);
                    mQueueAdapter.notifyDataSetChanged();
                    ConstantClass.isSearch = true;
                    ConstantClass.searchContext = search_content;
                    ConstantClass.OWNER_ID = id_owner;
                    iv_search_del.setVisibility(View.VISIBLE);
                    // 开启一个Fragment事务
                    fragmentManager = getFragmentManager();
                    transaction = fragmentManager.beginTransaction();

                    //默认显示的fragment为inquiry_fragment
                    VehicleNameFragment mVehicleNameFragment1 = new VehicleNameFragment();
                    transaction.replace(R.id.layout_vehicle, mVehicleNameFragment1);
                    transaction.commit();
                }

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_vehicleid_search);
        //添加activity到activity栈
        MonitorApplication.getInstance().addActivity(MonitorQueueSearch.this);
        //初始化控件
        initVeiw();
    }

    /**
     * 初始化控件
     */
    private void initVeiw() {
        sp = this.getSharedPreferences("login_state", 0);
        titleBreak = (ImageView) findViewById(R.id.title_break);
        titleContext = (TextView) findViewById(R.id.title_context);
        lvQueue = (ListView) findViewById(R.id.lv_queue);
        iv_search_del = (ImageView) findViewById(R.id.iv_search_del);

        edit_search = (EditText) findViewById(R.id.edit_vehicle_search);

        iv_search_del.setOnClickListener(this);


        Bundle bundle = this.getIntent().getExtras();
        ConstantClass.INTENT_CLASS = bundle.getString("class");
        titleContext.setText(bundle.getString("name"));
        id_owner = bundle.getString("id_owner");

        getHttpVehicleDate(1,id_owner);

        //title返回按钮点击事件
        titleBreak.setOnClickListener(this);
        lvQueue.setOnItemClickListener(this);
    }

    //加载所属车队信息
    private void getHttpVehicleDate(final int type, String name) {

        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", type);
        params.put("name", name);
        //请求数据类型（为0 时请求该用户所属业户，1 车队信息，0 时请求车队的全部车辆信息）

        HttpUtil.sendHttpRequestForPost(sp.getString("URL","")+ ConstantClass.URL_VEHICLE_MONITOR, params, new HttpCallbackListener() {
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
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_break:
                edit_search.setText("");
                finish();
                break;
            case R.id.iv_search_del:
                edit_search.setText("");
                break;
        }
    }

    /**
     * 车队（list）item点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //String queueid = list.get(position).getQueueId();

        //车队id
        ConstantClass.QUEUE_ID = list.get(position).getQueueId();
        // 开启一个Fragment事务
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();

        //默认显示的fragment为inquiry_fragment
        VehicleNameFragment mVehicleNameFragment1 = new VehicleNameFragment();
        transaction.replace(R.id.layout_vehicle, mVehicleNameFragment1);
        transaction.commit();

        mQueueAdapter.setTvQueueColor(position);
        mQueueAdapter.notifyDataSetChanged();

    }
}
