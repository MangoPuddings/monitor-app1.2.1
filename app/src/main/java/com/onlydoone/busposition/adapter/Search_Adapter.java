package com.onlydoone.busposition.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.bean.Search_Data;

import java.util.ArrayList;
import java.util.List;

/**
 *车辆模糊查询适配器
 * Created by Administrator on 2016/12/22 0022.
 */
public class Search_Adapter extends BaseAdapter implements Filterable {

    private ArrayFilter mFilter;
    private Context context;
    private List<Search_Data> data;
    private ArrayList<Search_Data> mdata;

    public Search_Adapter() {

    }

    public Search_Adapter(Context context, List<Search_Data> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.auto_tv_item, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.auto_tv_item);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(data.get(i).getTv_data());

        return convertView;
    }

    /**
     * 内部类优化加载数据，复用textView
     */
    private static class ViewHolder {
        private TextView tv;
    }
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        /**
         * 实现过滤的具体方法在子类中实现
         *
         * @param charSequence
         * @return
         */
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults filterResults = new FilterResults();

            if (mdata == null) {
                mdata = new ArrayList<Search_Data>(data);
            }

            if (charSequence == null || charSequence.length() == 0) {
                ArrayList<Search_Data> list = mdata;
                filterResults.values = list;
                filterResults.count = list.size();
            } else {
                String prefixString = charSequence.toString().toLowerCase();

                ArrayList<Search_Data> unfilteredValues = mdata;
                int count = unfilteredValues.size();

                ArrayList<Search_Data> newValues = new ArrayList<Search_Data>(count);

                for (int i = 0; i < count; i++) {
                    Search_Data pc = unfilteredValues.get(i);
                    if (pc != null) {
                        if (pc.getTv_data() != null) {
                            newValues.add(pc);
                        }
                    }
                }
                filterResults.values = newValues;
                filterResults.count = newValues.size();
            }

            return filterResults;
        }

        /**
         * 当过滤结束后调用的函数
         *
         * @param charSequence
         * @param filterResults
         */
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            //noinspection unchecked
            data = (List<Search_Data>) filterResults.values;
            if (filterResults.count > 0) {
                notifyDataSetChanged();//更新可见区
            } else {
                notifyDataSetInvalidated();//更新整个组件
            }
        }
    }
}
