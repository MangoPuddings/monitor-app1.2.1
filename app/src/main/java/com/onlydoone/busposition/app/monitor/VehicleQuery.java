package com.onlydoone.busposition.app.monitor;

import android.app.Activity;
import android.content.Intent;
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
import com.onlydoone.busposition.adapter.MonitorListViewVehicleNameAdapter;
import com.onlydoone.busposition.bean.VehicleNameData;
import com.onlydoone.busposition.classs.ConstantClass;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaohui on 2017/3/15.
 */
public class VehicleQuery extends Activity{
    /**
     * 初始话用户信息文件
     */
    private SharedPreferences sp;
    /**
     * 车辆号list集合
     */
    private List<VehicleNameData> list;
    /**
     * 车辆号adapter
     */
    private MonitorListViewVehicleNameAdapter mVehicleNameAdapter;
    private EditText tv_search;
    private TextView tv_cancel;
    private ImageView iv_police_search_delete;

    private ListView lv_police_vehicle_name;
    public static ObjectMapper objectMapper = new ObjectMapper();

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //Logs.e("车辆信息", msg.obj.toString());
                    try {
                        Map map = objectMapper.readValue(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map.get("result").equals("0")) {
                            list = new ArrayList<>();
                            VehicleNameData data;
                            for (Object li : (List) map.get("vehicles")) {
                                Map temp = (Map) li;
                                data = new VehicleNameData(temp.get("icon_type").toString(), temp.get("vehicleid").toString(),temp.get("sim_no").toString());
                                list.add(data);
                            }
                            mVehicleNameAdapter = new MonitorListViewVehicleNameAdapter(VehicleQuery.this, list);
                            lv_police_vehicle_name.setAdapter(mVehicleNameAdapter);
                            mVehicleNameAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtils.showToast(VehicleQuery.this, "暂无车辆信息");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_query);
        //初始化控件
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //读取用户登录状态
        sp = this.getSharedPreferences("login_state", this.MODE_PRIVATE);

        lv_police_vehicle_name = (ListView) findViewById(R.id.lv_police_vehicle_name);
        tv_search = (EditText) findViewById(R.id.tv_police_search);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        iv_police_search_delete = (ImageView) findViewById(R.id.iv_police_search_delete);
        iv_police_search_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_search.setText("");
            }
        });
        Bundle bundle = getIntent().getExtras();
        String vehicleid = bundle.getString("vehicleid");
        tv_search.setText(vehicleid);
        getHttpVehicleNo(vehicleid);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchContext = tv_search.getText().toString().trim();
                if (!searchContext.equals("")){
                    getHttpVehicleNo(searchContext);
                }

            }
        });



        //车辆listview的item点击事件
        lv_police_vehicle_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent  data =new Intent();
                data.putExtra("vehicleid",list.get(position).getVehicleName().toString());//数据放到data里面去
                setResult(2,data);//返回data，2为result，data为intent对象  d
                finish();//页面销毁
            }
        });
    }

    /**
     * 搜索框自动完成
     */
    private void getHttpVehicleNo(String searchContext) {
        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleNo", searchContext);
        params.put("id_owner", sp.getString("id_owner", ""));
        //设置请求地址
        String mUrl = sp.getString("URL","") + ConstantClass.URL_VEHICLE_VAGUE;
        HttpUtil.sendHttpRequestForPost(mUrl, params, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 0;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
