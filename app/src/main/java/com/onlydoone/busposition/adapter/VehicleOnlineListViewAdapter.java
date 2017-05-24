package com.onlydoone.busposition.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.bean.VehicleOnlineListViewData;

import java.util.List;

/**
 * 车辆在线率适配器
 * Created by zhaohui on 2017/3/21.
 */
public class VehicleOnlineListViewAdapter extends BaseAdapter{
    Context context;
    TextView tv_vehicle_queue,tv_vehicle_onlines,tv_vehicle_alls,tv_vehicle_online_rate;
    private List<VehicleOnlineListViewData> data;

    public VehicleOnlineListViewAdapter() {
    }

    public VehicleOnlineListViewAdapter(Context context,List<VehicleOnlineListViewData> data) {
        this.data = data;
        this.context = context;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.monitor_online_lv_item,null);

        tv_vehicle_queue = (TextView) convertView.findViewById(R.id.tv_vehicle_queue);
        tv_vehicle_onlines = (TextView) convertView.findViewById(R.id.tv_vehicle_onlines);
        tv_vehicle_alls = (TextView) convertView.findViewById(R.id.tv_vehicle_alls);
        tv_vehicle_online_rate = (TextView) convertView.findViewById(R.id.tv_vehicle_online_rate);

        tv_vehicle_queue.setText(data.get(position).getVehicleQueue());
        //业户/车队/线路点击事件，
        tv_vehicle_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击时获取焦点
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                v.requestFocus();
                v.requestFocusFromTouch();
            }
        });
        //业户/车队/线路焦点改变监听
        tv_vehicle_queue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    //获得焦点  滚动业户/车队/线路
                    v.setSelected(true);
                }else{
                    //失去焦点  不滚动
                    v.setSelected(false);
                }
            }
        });

        tv_vehicle_onlines.setText(data.get(position).getOnlineNum());
        tv_vehicle_alls.setText(data.get(position).getAllNum());
        tv_vehicle_online_rate.setText(data.get(position).getOnlineRate());
        return convertView;
    }
}
