package com.onlydoone.busposition.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.bean.VehicleNameData;

import java.util.List;

/**
 * Created by Administrator on 2017/1/1 0001.
 */

public class MonitorListViewVehicleNameAdapter extends BaseAdapter{

    private TextView tvVehicleName;
    private ImageView ivState;
    private Context context;
    private List<VehicleNameData> data;

    public MonitorListViewVehicleNameAdapter() {
    }

    public MonitorListViewVehicleNameAdapter(Context context, List<VehicleNameData> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context).inflate(R.layout.monitor_vehiclename_lv_item,null);

        tvVehicleName = (TextView) view.findViewById(R.id.tv_vehicle_name);
        ivState = (ImageView) view.findViewById(R.id.iv_state);

        tvVehicleName.setText(data.get(i).getVehicleName());

        if (data.get(i).getState().equals("online")){
            //在线状态
            ivState.setImageResource(R.mipmap.ic_vehicle_start);
        }
        if (data.get(i).getState().equals("offline")){
            //停车状态
            ivState.setImageResource(R.mipmap.ic_vehicle_stop);
        }
        if (data.get(i).getState().equals("parking")){
            //暂停状态
            ivState.setImageResource(R.mipmap.ic_vehicle_p);
        }
        return view;
    }
}
