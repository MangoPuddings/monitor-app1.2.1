package com.onlydoone.busposition.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.bean.QueueData;

import java.util.List;

/**
 * Created by Administrator on 2017/1/1 0001.
 */

public class MonitorListViewQueueAdapter extends BaseAdapter{
    private int id = -1;

    private TextView tvQueue;
    private Context context;
    private List<QueueData> data;

    public MonitorListViewQueueAdapter() {
    }

    public MonitorListViewQueueAdapter(Context context, List<QueueData> data){
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

    /**
     * 设置item点击时候的颜色
     * @param id
     */
    public void setTvQueueColor(int id){
        this.id = id;
    };

    public List<QueueData>  getQueueId(){

        return data;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context).inflate(R.layout.monitor_queue_lv_item,null);


        tvQueue = (TextView) view.findViewById(R.id.tv_queue);

        tvQueue.setText(data.get(i).getQueueName());

        //如果id != -1 则设置该车队id的颜色
        if (id != -1){
            if (id == i) {
                tvQueue.setTextColor(Color.parseColor("#393108"));
                tvQueue.setBackgroundColor(Color.parseColor("#31BFE5"));
            }
        }

        return view;
    }
}
