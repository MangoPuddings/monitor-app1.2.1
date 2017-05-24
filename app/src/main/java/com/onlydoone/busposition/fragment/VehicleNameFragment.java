package com.onlydoone.busposition.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.Utils.http.HttpCallbackListener;
import com.onlydoone.busposition.Utils.http.HttpUtil;
import com.onlydoone.busposition.app.monitor.MonitorOwnerSearch;
import com.onlydoone.busposition.app.monitor.MonitorVehicle4G;
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
 * 车辆信息类
 * Created by zhaohui on 2017/3/7.
 */
public class VehicleNameFragment extends Fragment implements AdapterView.OnItemClickListener {
    SharedPreferences sp;
    /**
     * 车辆号list集合
     */
    private List<VehicleNameData> list;
    /**
     * 车辆号listview
     */
    private ListView lv_Vehicle_name;
    /**
     * 车辆号adapter
     */
    private MonitorListViewVehicleNameAdapter mVehicleNameAdapter;


    private View view;

    public static ObjectMapper objectMapper = new ObjectMapper();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //Logs.e("车辆信息", msg.obj.toString());
                    try {
                        Map map = objectMapper.readValue(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map.get("result").equals("0")) {
                            VehicleNameData data;
                            list = new ArrayList<>();
                            for (Object li : (List) map.get("vehicles")) {
                                Map temp = (Map) li;
                                data = new VehicleNameData(temp.get("icon_type").toString(), temp.get("vehicleid").toString(), temp.get("sim_no").toString());
                                list.add(data);
                            }
                            mVehicleNameAdapter = new MonitorListViewVehicleNameAdapter(getActivity(), list);
                            lv_Vehicle_name.setAdapter(mVehicleNameAdapter);

                        } else {
                            ToastUtils.showToast(getActivity(), "暂无车辆信息");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    //Logs.e("车辆信息：",msg.obj.toString());
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.vehiclename_fragment, container, false);

        initView();
        return view;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        sp = getActivity().getSharedPreferences("login_state", 0);
        lv_Vehicle_name = (ListView) view.findViewById(R.id.lv_Vehicle_name);

        //车辆listview的item点击事件
        lv_Vehicle_name.setOnItemClickListener(this);

        if (ConstantClass.isSearch) {
            getHttpVehicleNo();
        } else {
            getHttpVehicleDate(2, ConstantClass.QUEUE_ID);
        }
    }

    /**
     * 搜索框自动完成
     */
    private void getHttpVehicleNo() {

        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleNo", ConstantClass.searchContext);
        params.put("id_owner", ConstantClass.OWNER_ID);
        //设置请求地址
        final String mUrl = sp.getString("URL", "") + ConstantClass.URL_VEHICLE_VAGUE;
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

    //加载车辆信息
    private void getHttpVehicleDate(final int type, String name) {

        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", type);
        params.put("name", name);
        //请求数据类型（为0 时请求该用户所属业户，1 车队信息，0 时请求车队的全部车辆信息）

        HttpUtil.sendHttpRequestForPost(sp.getString("URL", "") + ConstantClass.URL_VEHICLE_MONITOR, params, new HttpCallbackListener() {
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
     * 车辆号item点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (ConstantClass.INTENT_CLASS.equals("Monitor")) {
            Intent intent = new Intent(getActivity(), MonitorVehicle4G.class);
            Bundle bundle = new Bundle();
            bundle.putString("vehicleid", list.get(position).getVehicleName().toString());
            //bundle.putString("sim_no", list.get(position).getSimNo().toString());
            ConstantClass.sim_no = list.get(position).getSimNo().toString();
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            ConstantClass.vehicleNO = list.get(position).getVehicleName().toString();
            ConstantClass.sim_no = "0" + list.get(position).getSimNo().toString();
            getActivity().finish();
            MonitorOwnerSearch.mActivity.finish();
        }
    }
}
