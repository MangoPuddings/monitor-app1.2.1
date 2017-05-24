package com.onlydoone.busposition.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.bean.PoliceTypeData;

import java.util.List;

/**
 * Created by zhaohui on 2017/3/15.
 */

public class GridViewAdapter extends BaseAdapter {
    private TextView policeType;
    private Context context;
    private List<PoliceTypeData> data;

    public GridViewAdapter() {
    }

    public GridViewAdapter(Context context, List<PoliceTypeData> data) {
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

    public void addItem(PoliceTypeData data){
        this.data.add(data);
    }
    public void removeItem(int data){
        this.data.remove(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.police_type_gridview_item,null);

        policeType = (TextView) convertView.findViewById(R.id.tv_police_type_item);
        LinearLayout layout_police_type_item = (LinearLayout) convertView.findViewById(R.id.layout_police_type_item);
        policeType.setText(data.get(position).getPoliceType());

        switch (position){
            case 0:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type1);
                break;
            case 1:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type2);
                break;
            case 2:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type3);
                break;
            case 3:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type2);
                break;
            case 4:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type4);
                break;
            case 5:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type5);
                break;
            case 6:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type3);
                break;
            case 7:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type5);
                break;
            case 8:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type4);
                break;
            case 9:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type6);
                break;
            case 10:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type2);
                break;
            case 11:
                layout_police_type_item.setBackgroundResource(R.drawable.police_type6);
                break;

        }
        return convertView;
    }
}
