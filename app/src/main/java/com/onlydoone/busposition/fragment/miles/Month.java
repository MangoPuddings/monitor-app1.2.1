package com.onlydoone.busposition.fragment.miles;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.Utils.http.HttpCallbackListener;
import com.onlydoone.busposition.Utils.http.HttpUtil;
import com.onlydoone.busposition.classs.CHScrollViewMonth;
import com.onlydoone.busposition.classs.ConstantClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaohui on 2017/2/20.
 */

public class Month extends Fragment {
    private Context mContext;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //Logs.e("miles", msg.obj.toString());

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(msg.obj.toString());
                        //获取车辆是否存在的状态码 0（存在） -1（不存在）
                        String result = jsonObject.getString("result");
                        if (result.equals("0")) {
                            String[] date = new String[8];
                            for (int d = 0; d < 8; d++) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(new java.util.Date());
                                calendar.add(Calendar.MONTH, -(d + 1));
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
                                String da = format.format(calendar.getTime());
                                date[d] = da;
                            }
                            tvVehicleid.setVisibility(View.VISIBLE);
                            tvOwner.setText("所属业户");
                            tvData1.setText(date[0]);
                            tvData2.setText(date[1]);
                            tvData3.setText(date[2]);
                            tvData4.setText(date[3]);
                            tvData5.setText(date[4]);
                            tvData6.setText(date[5]);
                            tvData7.setText(date[6]);
                            tvData8.setText(date[7]);

                            //如果返回状态码为0，则解析车辆信息
                            JSONArray jsonArrayVehicles = jsonObject.getJSONArray("vehicles");
                            List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
                            Map<String, String> data = null;
                            for (int i = 0; i < jsonArrayVehicles.length(); i++) {
                                JSONObject jsonObjectVehicles = (JSONObject) jsonArrayVehicles.opt(i);
                                //获取车牌号
                                final String vehicleid = jsonObjectVehicles.getString("vehicleid");
                                //获取车牌所属业户
                                final String ownername = jsonObjectVehicles.getString("ownername");

                                data = new HashMap<String, String>();
                                data.put("title", vehicleid);
                                for (int j = 1; j < cols.length; j++) {
                                    if (j == 1) {
                                        data.put("data_" + j, ownername);
                                    } else {
                                        data.put("data_" + j, jsonObjectVehicles.getString("m" + (j - 1)));
                                    }
                                }
                                datas.add(data);
                            }
                            mAdapter = new ScrollAdapter(mContext, datas, R.layout.common_item_hlistview_month   //R.layout.item
                                    , cols
                                    , new int[]{R.id.item_titlev
                                    , R.id.item_datav1
                                    , R.id.item_datav2
                                    , R.id.item_datav3
                                    , R.id.item_datav4
                                    , R.id.item_datav5
                                    , R.id.item_datav6
                                    , R.id.item_datav7
                                    , R.id.item_datav8});
                            mListView.setAdapter(mAdapter);
                        } else {
                            tvOwner.setText("暂无数据");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtils.showToast(getActivity(), "json解析异常");
                        tvOwner.setText("暂无数据");
                    }
                    break;
                case 1:
                    if (ConstantClass.isFootitemMonth == -1) {
                        ConstantClass.isFootitemMonth = 0;
                        mListView.addFooterView(mFooterView);
                        mAdapter.notifyDataSetChanged();
                        getHttpMiless(page, 20);
                        page = page + page;
                    }
                    break;
                case 2:
                    //ToastUtils.showToast(getActivity(), msg.obj.toString());
                    //Logs.e("miles", msg.obj.toString());
                    //获取json对象，解析数据
                    JSONObject jsonObjects = null;
                    try {
                        jsonObjects = new JSONObject(msg.obj.toString());
                        //获取车辆是否存在的状态码 0（存在） -1（不存在）
                        String result = jsonObjects.getString("result");
                        if (result.equals("0")) {
                            //如果返回状态码为0，则解析车辆信息
                            JSONArray jsonArrayVehicles = jsonObjects.getJSONArray("vehicles");
                            List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
                            Map<String, String> data = null;
                            for (int i = 0; i < jsonArrayVehicles.length(); i++) {
                                JSONObject jsonObjectVehicles = (JSONObject) jsonArrayVehicles.opt(i);
                                //获取车牌号
                                final String vehicleid = jsonObjectVehicles.getString("vehicleid");
                                //获取车牌所属业户
                                final String ownername = jsonObjectVehicles.getString("ownername");

                                data = new HashMap<String, String>();
                                data.put("title", vehicleid);
                                for (int j = 1; j < cols.length; j++) {
                                    if (j == 1) {
                                        data.put("data_" + j, ownername);
                                    } else {
                                        data.put("data_" + j, jsonObjectVehicles.getString("m" + (j - 1)));
                                    }
                                }
                                mAdapter.addNewsItem(data);
                                mListView.removeFooterView(mFooterView);
                                ConstantClass.isFootitemMonth = -1;
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            sendMsg();
                            ToastUtils.showToast(getActivity(),"已加载全部数据");
                            mListView.removeFooterView(mFooterView);
                            ConstantClass.isFootitemMonth = -1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtils.showToast(getActivity(), "json解析异常");
                        tvOwner.setText("抱歉，数据加载失败");
                    }
                    break;
                case 3:
                    TextView date = (TextView) mFooterView.findViewById(R.id.textViewDate);
                    ProgressBar progressBarDate = (ProgressBar) mFooterView.findViewById(R.id.ProgressBarDate);
                    progressBarDate.setVisibility(View.GONE);
                    date.setText("已加载全部数据");
                    break;
                case -1:
                    ToastUtils.showToast(getActivity(), ConstantClass.CONNECTION_EXCEPTION);
                    tvOwner.setText("暂无数据");
                    break;
            }
        }
    };

    private void sendMsg() {
        Message msg = new Message();
        msg.what = 3;
        handler.sendMessage(msg);
    }

    /**
     * 初始话用户信息文件
     */
    private SharedPreferences sp;
    private ListView mListView;
    private View mFooterView;
    private int page = 20;
    private TextView tvVehicleid, tvOwner, tvData1, tvData2, tvData3, tvData4, tvData5, tvData6, tvData7, tvData8;

    //方便测试，直接写的public
    public HorizontalScrollView mTouchView;
    //装入所有的HScrollView
    protected List<CHScrollViewMonth> mHScrollViews = new ArrayList<CHScrollViewMonth>();
    private String[] cols = new String[]{"title", "data_1", "data_2", "data_3", "data_4", "data_5",
            "data_6", "data_7", "data_8", "data_9",};

    private ScrollAdapter mAdapter;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.month, container, false);
        mContext = getActivity();
        //初始化控件
        initView();
        return view;
    }

    /**
     * 初始化控件
     */
    private void initView() {

        //查询服务器车辆行驶里程统计
        getHttpMiles();


        CHScrollViewMonth headerScroll = (CHScrollViewMonth) view.findViewById(R.id.item_scroll_title_month);
        //添加头滑动事件
        mHScrollViews.add(headerScroll);
        mListView = (ListView) view.findViewById(R.id.hlistview_scroll_list_month);

        tvVehicleid = (TextView) view.findViewById(R.id.tvVehicleid_m);
        tvOwner = (TextView) view.findViewById(R.id.tvOwner_m);
        tvOwner.setText("拼命加载中...");
        tvData1 = (TextView) view.findViewById(R.id.tvData1_m);
        tvData2 = (TextView) view.findViewById(R.id.tvData2_m);
        tvData3 = (TextView) view.findViewById(R.id.tvData3_m);
        tvData4 = (TextView) view.findViewById(R.id.tvData4_m);
        tvData5 = (TextView) view.findViewById(R.id.tvData5_m);
        tvData6 = (TextView) view.findViewById(R.id.tvData6_m);
        tvData7 = (TextView) view.findViewById(R.id.tvData7_m);
        tvData8 = (TextView) view.findViewById(R.id.tvData8_m);

        //添加底部正在加载布局
        mFooterView = getActivity().getLayoutInflater().inflate(R.layout.listview_footer_view, null);
        mListView.addFooterView(mFooterView);

        //设置setOnScrollListener会自动调用onscroll方法。
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //Logs.i("onScrollStateChanged", scrollState + "");

                // 判断滚动到底部
                if (mListView.getLastVisiblePosition() == (mListView.getCount() - 1)) {

                    if (scrollState == 0) {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Logs.i("onScroll", totalItemCount + "");
            }
        });
    }

    /**
     * 获取服务器车辆行驶里程
     */
    private void getHttpMiles() {
        //读取用户登录状态
        sp = view.getContext().getSharedPreferences("login_state", view.getContext().MODE_PRIVATE);
        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("id_owner", sp.getString("id_owner", ""));
        //按月查询
        params.put("type", "0");
        //从第几条查询
        params.put("page", 0);
        //查询几条数据
        params.put("rows", 20);

        HttpUtil.sendHttpRequestForPost(sp.getString("URL","") + ConstantClass.URL_VEHICLE_MILES, params, new HttpCallbackListener() {
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
     * 获取服务器车辆行驶里程(分页加载数据）
     */
    private void getHttpMiless(int page, int rows) {
        //读取用户登录状态
        sp = view.getContext().getSharedPreferences("login_state", view.getContext().MODE_PRIVATE);
        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("id_owner", sp.getString("id_owner", ""));
        //按月查询
        params.put("type", "0");
        //从第几条查询
        params.put("page", page);
        //查询几条数据
        params.put("rows", rows);

        HttpUtil.sendHttpRequestForPost(sp.getString("URL","") + ConstantClass.URL_VEHICLE_MILES, params, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message msg = new Message();
                msg.what = 2;
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

    class ScrollAdapter extends SimpleAdapter {

        private List<Map<String, String>> datas;
        private int res;
        private String[] from;
        private int[] to;
        private Context context;

        public ScrollAdapter(Context context,
                             List<Map<String, String>> data, int resource,
                             String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.context = context;
            this.datas = data;
            this.res = resource;
            this.from = from;
            this.to = to;
        }

        public void addNewsItem(Map<String, String> newsitem) {
            datas.add(newsitem);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(context).inflate(res, null);
                //第一次初始化的时候装进来
                addHViews((CHScrollViewMonth) v.findViewById(R.id.item_chscroll_scroll_month));
                View[] views = new View[to.length];
                //单元格点击事件
                for (int i = 0; i < to.length; i++) {
                    View tv = v.findViewById(to[i]);
                    //tv.setOnClickListener(clickListener);
                    views[i] = tv;
                }
                //每行点击事件
//				for(int i = 0 ; i < from.length; i++) {
//					View tv = v.findViewById(row_hlistview[i]);
//				}
                //
                v.setTag(views);
            }
            View[] holders = (View[]) v.getTag();
            int len = holders.length;
            for (int i = 0; i < len; i++) {
                ((TextView) holders[i]).setText(this.datas.get(position).get(from[i]).toString());
                ((TextView) holders[i]).setGravity(Gravity.CENTER);
            }
            return v;
        }
    }


    public void addHViews(final CHScrollViewMonth hScrollView) {
        if (!mHScrollViews.isEmpty()) {
            int size = mHScrollViews.size();
            CHScrollViewMonth scrollView = mHScrollViews.get(size - 1);
            final int scrollX = scrollView.getScrollX();
            //第一次满屏后，向下滑动，有一条数据在开始时未加入
            if (scrollX != 0) {
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        //当listView刷新完成之后，把该条移动到最终位置
                        hScrollView.scrollTo(scrollX, 0);
                    }
                });
            }
        }
        mHScrollViews.add(hScrollView);
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        for (CHScrollViewMonth scrollView : mHScrollViews) {
            //防止重复滑动
            if (mTouchView != scrollView)
                scrollView.smoothScrollTo(l, t);
        }
    }
}
