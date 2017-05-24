package com.onlydoone.busposition.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.bean.VehiclePoliceListViewData;

import java.util.List;

/**
 * Created by zhaohui on 2017/3/16.
 */

public class VehiclePoliceListViewAdapter extends BaseAdapter{

    private TextView tv_vehicle_police_vehicleid,tv_vehicle_police_type,
            tv_vehicle_police_owner,tv_vehicle_police_startTime;
    private ImageView iv_vehicle_police_type;
    private Context context;
    private List<VehiclePoliceListViewData> data;

    public VehiclePoliceListViewAdapter() {
    }

    public VehiclePoliceListViewAdapter(Context context, List<VehiclePoliceListViewData> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(VehiclePoliceListViewData data){
        this.data.add(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.vehicle_police_lv_item1,null);

        tv_vehicle_police_vehicleid = (TextView) convertView.findViewById(R.id.tv_vehicle_police_vehicleid);
        tv_vehicle_police_owner = (TextView) convertView.findViewById(R.id.tv_vehicle_police_owner);
        tv_vehicle_police_type = (TextView) convertView.findViewById(R.id.tv_vehicle_police_type);
        tv_vehicle_police_startTime = (TextView) convertView.findViewById(R.id.tv_vehicle_police_startTime);
        iv_vehicle_police_type = (ImageView) convertView.findViewById(R.id.iv_vehicle_police_type1);

        tv_vehicle_police_vehicleid.setText(data.get(position).getTv_vehicle_police_vehicleid());
        tv_vehicle_police_owner.setText(data.get(position).getTv_vehicle_police_owner());
        tv_vehicle_police_type.setText(data.get(position).getTv_vehicle_police_type());
        tv_vehicle_police_startTime.setText(data.get(position).getTv_vehicle_police_startTime());

        switch (data.get(position).getTv_vehicle_police_type()){
            case "GNSS天线未接或被剪断":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police_not_connected);
                break;
            case "GNSS天线短路":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police_short_circuit);
                break;
            case "GNSS模块发生故障":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police1);
                break;
            case "超速报警":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police_speeding);
                break;
            case "超时停车":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police1);
                break;
            case "摄像头故障":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police_camera_fault);
                break;
            case "终端主电源欠压":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police_undervoltage);
                break;
            case "终端LCD或显示器故障":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police_monitor);
                break;
            case "道路类型超速报警":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police1);
                break;
            case "TTS模块故障":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police1);
                break;
            case "终端主电源掉电":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police_outage);
                break;
            case "停车报警":
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police1);
                break;
            default:
                iv_vehicle_police_type.setImageResource(R.mipmap.ic_police1);
                break;

        }
        return convertView;
    }
}
