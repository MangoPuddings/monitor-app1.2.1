package com.onlydoone.busposition.app.monitor;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.onlydoone.busposition.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.onlydoone.busposition.Utils.ButtonOnClickUtil;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.Utils.http.HttpCallbackListener;
import com.onlydoone.busposition.Utils.http.HttpUtil;

import com.onlydoone.busposition.adapter.VehicleOnlineListViewAdapter;
import com.onlydoone.busposition.bean.VehicleOnlineListViewData;
import com.onlydoone.busposition.classs.ConstantClass;

import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 在线车辆数量监控
 * Created by Administrator on 2016/12/17 0017.
 */
public class MonitorOnLine extends Activity implements View.OnClickListener {
    private Context context;
    RotateAnimation myAnimation_Rotate;
    String dates;
    /**
     * 车辆在线数量更新定时器
     */
    private Timer timer, timers;
    /**
     * //添加y轴上的数据(存放y轴数据的是一个Entry的ArrayList) 他是构建LineDataSet的参数之一
     */
    private ArrayList<Entry> yValue1 = new ArrayList<>();
    /**
     * x轴的数据   时间
     */
    private ArrayList<String> xValues = new ArrayList<>();
    /**
     * 初始话用户信息文件
     */
    private SharedPreferences sp;
    /**
     * 线性图
     */
    private LineChart chart_line;
    /**
     * 目前车辆在线数量/总车辆数/在线率/日期
     */
    private TextView tv_vehicle_online, tv_vehicle_all, tv_online_rate, tv_online_date;
    /**
     * 日期更改
     */
    private View layout_online_date;
    /**
     * 总车辆数集合
     */
    private List<String> vehicle_all = new ArrayList<String>();
    /**
     * 返回按钮/刷新按钮
     */
    private ImageView title_break,title_search;
    /**
     * title标题
     */
    private TextView title_context;
    /**
     * 车辆在线率listview
     */
    private ListView mLv_vehicle_online;
    /**
     * 车辆在线率适配器
     */
    private VehicleOnlineListViewAdapter mVehicleOnlineListViewAdapter;
    /**
     * 车辆在线率数据源
     */
    private List<VehicleOnlineListViewData> mOnlineListViewData;
    /**
     * listview无数据提醒textview
     */
    private TextView tv_noData;
    /**
     * 等待进度条
     */
    private ProgressDialog dialog;
    /**
     * 刷新进度条
     */
    private ProgressDialog rDialog;
    int y = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    Log.e("----------", msg.obj.toString());

                    if ( dialog != null && dialog.isShowing()){
                        dialog.cancel();
                    }
                    if (rDialog != null && rDialog.isShowing()){
                        rDialog.cancel();
                        ToastUtils.showToast(MonitorOnLine.this,"刷新成功");
                    }
                    //获取json对象，解析数据
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        //获取数据是否存在
                        String result = jsonObject.getString("result");

                        if (result.equals("0")) {
                            tv_online_date.setText(dates);
                            //如果返回状态码为0，则解析车辆信息
                            JSONArray jsonArrayVehicles = jsonObject.getJSONArray("vehicles");
//                            for (int i = jsonArrayVehicles.length() - 1; i >= 0; i--) {
//                                JSONObject json = (JSONObject) jsonArrayVehicles.opt(i);
//                                String create_time = json.getString("create_time");
//                                String vehicle_online_num = json.getString("vehicle_online_num");
//                                //获取用户所属业户
//                                String ownername = "";
//                                try {
//                                    ownername = json.getString("ownername");
//                                } catch (Exception e) {
//                                    ownername = "";
//                                }
//                            yValue1 = new ArrayList<>();
//                            xValues = new ArrayList<>();

                            for (int i = 0; i < jsonArrayVehicles.length(); i++) {
                                JSONObject json = (JSONObject) jsonArrayVehicles.opt(i);
                                String create_time = json.getString("create_time");
                                String vehicle_online_num = json.getString("vehicle_online_num");
                                //获取用户所属业户
                                String ownername = "";
                                try {
                                    ownername = json.getString("ownername");
                                } catch (Exception e) {
                                    ownername = "";
                                }

                                //设置描述文字
                                chart_line.setDescription(ownername);
                                vehicle_all.add(json.getString("vehicle_total_num"));
                                tv_vehicle_online.setText(vehicle_online_num);
                                tv_vehicle_all.setText(vehicle_all.get(vehicle_all.size() - 1));
                                Double online = Double.valueOf(vehicle_online_num);
                                Double all = Double.valueOf(vehicle_all.get(vehicle_all.size() - 1));
                                NumberFormat num = NumberFormat.getPercentInstance();
                                num.setMaximumIntegerDigits(3);
                                num.setMaximumFractionDigits(1);
                                Double onlineRate = online / all;
                                tv_online_rate.setText(num.format(onlineRate));

                                Integer online_num = Integer.valueOf(vehicle_online_num);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                Calendar calendar = Calendar.getInstance();
                                try {
                                    calendar.setTime(simpleDateFormat.parse(create_time));
                                    int m = calendar.get(Calendar.MINUTE);
                                    String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + (m <= 9 ? ("0" + m) : m);
                                    if (ConstantClass.isUpdate.equals("-1")) {
                                        //Logs.e("-1----------", "查询全部");
                                        xValues.add(time);
                                        yValue1.add(new Entry(online_num, y));
                                        y = y + 1;
                                    } else {
                                        //Logs.e("1----------", "查询最新");
                                        if (xValues.size() > 0) {
                                            if (!(time.equals(xValues.get(y - 1)))) {
                                                //Logs.e("1----------", "查询size()>0");
                                                xValues.add(time);
                                                yValue1.add(new Entry(online_num, y));
                                                y = y + 1;
                                            } else {
                                                return;
                                            }
                                        }
                                    }


                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            ConstantClass.isUpdate = "0";
                            sendMsg();

                        } else {
                            Toast.makeText(MonitorOnLine.this, "当天无车辆在线信息", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Logs.d("err", "json解析异常");
                    }
                    break;
                case 1:
                    tv_vehicle_online.setText(msg.arg1 + "");   //目前车辆在线数
                    tv_vehicle_all.setText(vehicle_all.get(msg.arg2));   //车辆总数
                    Double online = Double.valueOf(msg.arg1);
                    Double all = Double.valueOf(Integer.parseInt(vehicle_all.get(msg.arg2)));
                    NumberFormat num = NumberFormat.getPercentInstance();
                    num.setMaximumIntegerDigits(3);
                    num.setMaximumFractionDigits(1);
                    Double onlineRate = online / all;
                    tv_online_rate.setText(num.format(onlineRate));
                    break;
                case 2:
                    ConstantClass.isUpdate = "0";
                    //设置线状图显示样式
                    showLineChart();
                    chart_line.notifyDataSetChanged();
                    showLineChart();
                    break;
                case 3:
                    //Logs.e("-----",msg.obj.toString());
                    try {
                        Map map = VehicleQuery.objectMapper.readValue(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map.get("result").equals("0")) {
                            mOnlineListViewData = new ArrayList<>();
                            VehicleOnlineListViewData data;
                            for (Object li : (List) map.get("vehicles")) {
                                Map temp = (Map) li;
                                String onlines = temp.get("vehicle_online_num").toString();
                                String totals = temp.get("vehicle_total_num").toString();
                                Double onlineNum = Double.valueOf(onlines);
                                Double totalNum = Double.valueOf(totals);
                                NumberFormat nums = NumberFormat.getPercentInstance();
                                nums.setMaximumIntegerDigits(3);
                                nums.setMaximumFractionDigits(1);
                                Double onlineRates = onlineNum / totalNum;
                                data = new VehicleOnlineListViewData(temp.get("ownername").toString(),onlines,totals,nums.format(onlineRates));
                                mOnlineListViewData.add(data);
                            }
                            //数据加载成功隐藏提示文本
                            tv_noData.setVisibility(View.GONE);
                            mVehicleOnlineListViewAdapter = new VehicleOnlineListViewAdapter(MonitorOnLine.this, mOnlineListViewData);
                            mLv_vehicle_online.setAdapter(mVehicleOnlineListViewAdapter);
                            mVehicleOnlineListViewAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtils.showToast(MonitorOnLine.this, "暂无车辆信息");
                            //listview 提示暂无数据...
                            tv_noData.setVisibility(View.VISIBLE);
                            tv_noData.setText("很遗憾，暂无数据！");
                        }
                    } catch (IOException e) {
                        //listview 提示暂无数据...
                        tv_noData.setVisibility(View.VISIBLE);
                        tv_noData.setText("抱歉，数据加载失败！");
                        e.printStackTrace();
                    }
//
                    break;
                case -1:
                    if ( dialog != null && dialog.isShowing()){
                        dialog.cancel();
                    }
                    if (rDialog != null && rDialog.isShowing()){
                        rDialog.cancel();
                        ToastUtils.showToast(MonitorOnLine.this,"刷新失败，请检查网络是否连接！");
                    }
                    //Logs.d("err", "获取服务器数据失败");
                    tv_noData.setVisibility(View.VISIBLE);
                    tv_noData.setText("请检查网络是否连接！");
                    break;
            }
        }
    };

    /**
     * 刷新折线图
     */
    private void sendMsg() {
        Message msg = new Message();
        msg.what = 2;
        handler.sendMessage(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //只要activity为可见状态，则保持设备的屏幕打开和常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.monitor);
        context = MonitorOnLine.this;
        //初始化控件
        initView();

    }

    /**
     * 被覆盖到下面或者锁屏时被调用
     */
    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        timer.purge();
    }

    /**
     * 界面从不可见状态重新回到可见状态时调用
     */
    @Override
    public void onStart() {
        super.onStart();
        //initView();
    }

    /**
     * 界面销毁时调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConstantClass.isUpdate = "-1";
    }

    /**
     * 初始化控件
     */
    private void initView() {

        chart_line = (LineChart) findViewById(R.id.chart_line);
        tv_vehicle_online = (TextView) findViewById(R.id.tv_vehicle_online);
        tv_vehicle_all = (TextView) findViewById(R.id.tv_vehicle_all);
        tv_online_rate = (TextView) findViewById(R.id.tv_online_rate);
        title_break = (ImageView) findViewById(R.id.title_break);
        title_context = (TextView) findViewById(R.id.title_context);
        tv_online_date = (TextView) findViewById(R.id.tv_online_date);
        layout_online_date = findViewById(R.id.layout_online_date);
        mLv_vehicle_online = (ListView) findViewById(R.id.lv_vehicle_online);
        tv_noData = (TextView) findViewById(R.id.tv_noData);
        title_search = (ImageView) findViewById(R.id.title_search);
        title_search.setImageResource(R.mipmap.ic_refresh);
        title_search.setVisibility(View.VISIBLE);
        title_search.setOnClickListener(this);

        //初始化进来listview 提示数据正在加载中...
        tv_noData.setVisibility(View.VISIBLE);
        tv_noData.setText("拼命加载中...");
        //日期更改点击事件
        layout_online_date.setOnClickListener(this);
        //默认日期
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        dates = formatter.format(date);
        tv_online_date.setText(dates);

        title_context.setText("车辆在线统计");
        title_break.setOnClickListener(this);

        //线状图数据
        //LineData mLineData = getLineData();

        //读取用户登录状态
        sp = this.getSharedPreferences("login_state", this.MODE_PRIVATE);

        String date1 = tv_online_date.getText().toString();
        //折线图数据（服务器）
        findHttpData(date1);
        //listview数据（服务器）
        findHttpDatas();

        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("查询中···");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 请求服务器数据
     * listview数据
     */
    private void findHttpDatas() {
        //开启定时器5分钟更新一次数据
        timers = new Timer();
        timers.schedule(new TimerTask() {
            @Override
            public void run() {
                //设置请求参数
                final Map<String, Object> params = new HashMap<String, Object>();
                params.put("id_owner", sp.getString("id_owner", ""));
                //params.put("date", date);
                HttpUtil.sendHttpRequestForPost(sp.getString("URL","") + ConstantClass.URL_VEHICLE_ONLINE_RATE, params, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        //Logs.d("目前车辆在线数:", response);
                        Message msg = new Message();
                        msg.obj = response;
                        msg.what = 3;
                        handler.sendMessage(msg);

                    }

                    @Override
                    public void onError(Exception e) {
                        ConstantClass.isUpdate = "-1";
                        Message msg = new Message();
                        msg.obj = "请检查网络是否连接/";
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                });
            }
        }, 1, 2 * 60 * 1000);
    }

    /**
     * 请求服务器数据
     * 折线图数据
     */
    private void findHttpData(final String date) {
        //开启定时器5分钟更新一次数据
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //设置请求参数
                final Map<String, Object> params = new HashMap<String, Object>();
                params.put("isUpdate", ConstantClass.isUpdate);
                params.put("id_owner", sp.getString("id_owner", ""));
                params.put("date", date);

                HttpUtil.sendHttpRequestForPost(sp.getString("URL","") + ConstantClass.URL_VEHICLE_ON_LINE, params, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        //Logs.d("目前车辆在线数:", response);
                        Message msg = new Message();
                        msg.obj = response;
                        msg.what = 0;
                        handler.sendMessage(msg);

                    }

                    @Override
                    public void onError(Exception e) {
                        ConstantClass.isUpdate = "-1";
                        Message msg = new Message();
                        msg.obj = "请检查网络是否连接/";
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                });
            }
        }, 1, 2 * 60 * 1000);
    }

    /**
     * 设置线状图显示样式
     */
    private void showLineChart() {

        //获取x轴实例
        XAxis xAxis = chart_line.getXAxis();
        //获取右侧y轴实例
        YAxis yAxisR = chart_line.getAxisRight();
        //获得左侧y轴实例
        YAxis yAxisL = chart_line.getAxisLeft();
        // 设置能否缩放
        chart_line.setScaleEnabled(false);
        chart_line.setScaleXEnabled(true); //是否可以缩放 仅x轴
        chart_line.setNoDataTextDescription("没有数据呢(⊙o⊙)");   //没有数据时显示在中央的字符串，参数是String对象
        // 设置能否在屏幕上做多指手势
        chart_line.setPinchZoom(true);
        // 设置能否拖动
        chart_line.setDragEnabled(true);
        // 设置背景色
        chart_line.setBackgroundColor(Color.parseColor("#FFFFFF"));

        //隐藏x的坐标轴
        xAxis.setEnabled(true);
        xAxis.setGridLineWidth(10f);
        //x轴坐标间距
//        xAxis.setDrawLimitLinesBehindData(true);
//        xAxis.resetLabelsToSkip();
        //设置x轴颜色
        xAxis.setGridColor(Color.parseColor("#FFFFFF"));
        //yAxisL.setEnabled(false); //隐藏左侧y轴
        yAxisL.setTextColor(Color.parseColor("#659874"));
        yAxisL.setGridLineWidth(0.05f);
        yAxisL.setAxisLineWidth(0.01f);
        //设置y轴是否从零开始
        yAxisL.setStartAtZero(false);
        yAxisL.setXOffset(10f);

        yAxisL.setAxisLineColor(Color.parseColor("#FFFFFF"));
        //隐藏右侧y轴
        //yAxisR.setEnabled(false);
        yAxisR.setTextColor(Color.parseColor("#FFFFFF"));
        yAxisR.setAxisLineColor(Color.parseColor("#FFFFFF"));

        // 是否绘制背景颜色。
        // 如果mLineChart.setDrawGridBackground(false)，
        // 那么mLineChart.setGridBackgroundColor(Color.BLUE)将失效;
        chart_line.setDrawGridBackground(true);
        chart_line.setGridBackgroundColor(Color.parseColor("#FFFFFF"));

        // 设置LineChart的标示，每一组的 y 的 value 值
        Legend mLegend = chart_line.getLegend();
        // 设置显示的样式，此处为矩形，后文中 "车辆在线数量" 前面的图标
        mLegend.setForm(Legend.LegendForm.LINE);

        // 设置字体大小 ，(车辆在线数量的字体）
        mLegend.setFormSize(15.0f);
        // 设置颜色
        mLegend.setTextColor(Color.parseColor("#000000"));

        //设置X轴的文字在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);

        //设置描述文字
        chart_line.setDescription("");

        // 当前统计图表中最多在x轴坐标线上显示的总量
        chart_line.setVisibleXRangeMaximum(6);

        Matrix m = new Matrix();
        //m.postScale(3.5f, 1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
        //x轴坐标间距
        //xAxis.setLabelsToSkip(30);8
        chart_line.getViewPortHandler().refresh(m, chart_line, false);//将图表动画显示之前进行缩放
        chart_line.animateX(1000); // 立即执行的动画,x轴
        chart_line.moveViewToX(yValue1.size() - 1);   //从最后一个数据查看，默认从第一条数据查看

        //图表触摸事件
        chart_line.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                //Logs.e("entry----", entry.toString());
                //Logs.e("highlight----", highlight.toString());
                Message message = new Message();
                message.what = 1;
                message.arg1 = (int) entry.getVal();
                message.arg2 = entry.getXIndex();
                handler.sendMessage(message);
            }

            @Override
            public void onNothingSelected() {
                //Logs.e("1", "onNothingSelected-------");
            }
        });

        //构建一个LineDataSet 代表一组Y轴数据 （比如不同的彩票： 七星彩  双色球）
        LineDataSet dataSet = new LineDataSet(yValue1, "");
        //线宽
        dataSet.setLineWidth(3.0f);
        // 显示的圆形的大小
        dataSet.setCircleSize(5.0f);
        //折线的颜色
        dataSet.setColor(Color.parseColor("#03A7D3"));
        dataSet.setValueTextSize(14);
        dataSet.setValueTextColor(Color.parseColor("#03A7D3"));
        dataSet.setCircleColorHole(Color.YELLOW);
        //设置y轴上数据的数据类型（默认为float类型）
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                int n = (int) v;
                String s = "" + n;
                return s;
            }
        });

        //构建一个类型为LineDataSet的ArrayList 用来存放所有 y的LineDataSet   他是构建最终加入LineChart数据集所需要的参数
        ArrayList<LineDataSet> dataSets = new ArrayList<>();

        dataSets.add(dataSet);

        //构建一个LineData  将dataSets放入
        LineData lineData = new LineData(xValues, dataSets);

        //将数据插入
        chart_line.setData(lineData);

    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_break:
                //title返回按钮
                ConstantClass.isUpdate = "-1";
                finish();
                break;
            case R.id.title_search:
                //title刷新按钮
                if(ButtonOnClickUtil.isFastDoubleClick(R.id.title_search,3000)){
                    //判断刷新按钮是否在1500毫秒内再次点击则，如果为true直接return，（视为无效点击）
                    //Logs.e("----->显示点击时间和开始时间", "开始时间");
                    ToastUtils.showToast(getApplicationContext(), "点击太过频繁，请稍后刷新");
                    return;
                }
                //否则开始刷新/或再次刷新
                String date1 = tv_online_date.getText().toString();
                dates = date1;
                ConstantClass.isUpdate = "-1";
                xValues.clear();
                yValue1.clear();
                y = 0;
                //第一个参数fromDegrees为动画起始时的旋转角度
                //第二个参数toDegrees为动画旋转到的角度
                //第三个参数pivotXType为动画在X轴相对于物件位置类型
                //第四个参数pivotXValue为动画相对于物件的X坐标的开始位置
                //第五个参数pivotXType为动画在Y轴相对于物件位置类型
                //第六个参数pivotYValue为动画相对于物件的Y坐标的开始位置
                myAnimation_Rotate=new RotateAnimation(0.0f, 360f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
                myAnimation_Rotate.setDuration(800);
                //myAnimation_Rotate.setRepeatCount(-1);//设置重复次数
                //myAnimation_Rotate.setFillAfter(false);//动画执行完后是否停留在执行完的状态
                title_search.startAnimation(myAnimation_Rotate);
                //刷新等待进度条
                rDialog = new ProgressDialog(MonitorOnLine.this);
                rDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                rDialog.setMessage("刷新中···");
                rDialog.setCanceledOnTouchOutside(false);
                rDialog.show();
                //折线图数据（服务器）
                findHttpData(date1);
                //listview数据（服务器）
                findHttpDatas();
                break;
            case R.id.layout_online_date:
                //更改日期获取服务器数据
                dateChange();
                break;
        }
    }

    /**
     * 更改日期
     */
    private void dateChange() {
        //显示修改日期的dialog
        Calendar c = Calendar.getInstance();
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        int year = 0;
        int m = 0;
        int d = 0;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(tv_online_date.getText().toString());
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date);
            year = c1.get(Calendar.YEAR);
            m = c1.get(Calendar.MONTH);
            d = c1.get(Calendar.DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        new DatePickerDialog(MonitorOnLine.this,
                // 绑定监听器
                new DatePickerDialog.OnDateSetListener() {
                    Boolean mFired = false;

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //更改dialog上的日期内容
                        if (mFired == true) {
                            return;
                        } else {
                            dates = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            timer.cancel();
                            timer.purge();
                            ConstantClass.isUpdate = "-1";
                            xValues.clear();
                            yValue1.clear();
                            y = 0;
                            //查询服务器数据
                            findHttpData(dates);
                            //first time mFired
                            mFired = true;
                        }
                    }
                }
                // 设置初始日期
                , year, m, d).show();
    }
}
