package com.onlydoone.busposition.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.bean.Node;
import com.onlydoone.busposition.bean.TreeHelper;

public class SimpleTreeAdapter<T> extends TreeListViewAdapter<T> {

    List<T> datas;
    int defaultExpandLevel;

    public SimpleTreeAdapter(ListView mTree, Context context, List<T> datas,
                             int defaultExpandLevel) throws IllegalArgumentException,
            IllegalAccessException {
        super(mTree, context, datas, defaultExpandLevel);
        this.datas = datas;
        this.defaultExpandLevel = defaultExpandLevel;
    }

    public void addData(List<T> data) {
        this.datas.addAll(data);
        /**
         * 对所有的Node进行排序
         */
        try {
            mAllNodes = TreeHelper.getSortedNodes(datas, defaultExpandLevel);
            /**
             * 过滤出可见的Node
             */
            mNodes = TreeHelper.filterVisibleNode(mAllNodes);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getConvertView(Node node, int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.monitor_vehicle_search_lv_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView
                    .findViewById(R.id.id_treenode_icon);
            viewHolder.label = (TextView) convertView
                    .findViewById(R.id.id_treenode_label);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (node.getLevel() == 0){
            viewHolder.label.setTextColor(Color.parseColor("#748596"));
        }
        if (node.getLevel() == 1){
            viewHolder.label.setTextColor(Color.parseColor("#746931"));
        }
        if (node.getLevel() == 2){
            viewHolder.label.setTextColor(Color.parseColor("#565656"));
        }
        if (node.getLevel() == 3){
            viewHolder.label.setTextColor(Color.parseColor("#746931"));
        }
        if (node.getIcon() == -1) {
            //如果icon为-1则设置图标为未展开状态
            viewHolder.icon.setImageResource(R.drawable.tree_ec);
            viewHolder.icon.setVisibility(View.INVISIBLE);
            //node.setIcon(R.drawable.tree_ec);
        } else {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
        }

        viewHolder.label.setText(node.getName());


        return convertView;
    }

    private final class ViewHolder {
        ImageView icon;
        TextView label;
    }

}
