package com.onlydoone.busposition.app.monitor;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.TraceLocation;
import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.dialog.Dialog_Vehicle_Trail;
import com.onlydoone.busposition.Utils.http.HttpCallbackListener;
import com.onlydoone.busposition.Utils.http.HttpUtil;
import com.onlydoone.busposition.app.Login;
import com.onlydoone.busposition.classs.ConstantClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * 车辆轨迹
 * Created by zhaohui on 2017/1/20.
 */
public class MonitorVehicleTrail extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    Dialog_Vehicle_Trail.Builder builder = new Dialog_Vehicle_Trail.Builder(this);


    /**
     * 车辆轨迹查询等待条
     */
    private ProgressDialog dialogTrail;
    private MapView mapView = null;
    private AMap aMap;
    /**
     * 车辆轨迹页面title（车牌号信息）
     */
    private String context;

    /**
     * 初始化marker
     */
    private Marker marker;
    /**
     * 自定义标记
     */
    private MarkerOptions markerOption;
    /**
     * title显示的文字
     */
    private TextView title_context;
    /**
     * 销毁该页面，返回上一个页面
     */
    private ImageView title_break;
    /**
     * 更改车辆轨迹的（日期，车牌号）
     */
    private ImageView vehicle_trail_date;
    /**
     * dialog的车牌号信息
     */
    private EditText dialog_vehicle_vehicleid;
    /**
     * //dialog的内容（日期信息）
     */
    private String dialog_date = "";
    /**
     * //dialog的内容（起始时间信息）
     */
    private String dialog_start_time = "";
    /**
     * //dialog的内容（终止时间信息）
     */
    private String dialog_end_time = "";
    /**
     * 车辆轨迹播放按钮
     */
    private ImageView trailStartPlay;
    /**
     * 车辆轨迹停止播放按钮
     */
    private ImageView trailStopPlay;
    /**
     * 车辆轨迹回放是否播放状态
     * false（未开始）true（正在播放）
     */
    private Boolean isStartPlay = false;
    /**
     * 车辆轨迹实时位置
     */
    private TextView trailLatlng;
    /**
     * 车辆轨迹实时车速
     */
    private TextView tvSpeeds;
    /**
     * 车辆轨迹实时位置经纬度
     */
    private TextView tvGPStime;
    /**
     * 车辆轨迹点
     */
    private List<LatLng> points;
    /**
     * 车辆轨迹角度
     */
    private List<String> angles;
    /**
     * 车辆轨迹播放速度滑动条
     */
    private SeekBar seekBar;
    /**
     * 初始化轨迹播放总时长
     */
    private Long totalDouration = (100 - 75) * 500L;
    /**
     * 车辆轨迹是否播放完毕
     */
    private Boolean isEndPlay = true;
    Timer timerAddMarker;
    int i = 0;
    /**
     * 车辆轨迹点实时车速list
     */
    private List<String> speeds;
    /**
     * 是否有播放轨迹可以播放
     */
    private Boolean isHaveTrailPlay = false;
    /**
     * 车辆轨迹点实时定位时间list
     */
    private List<String> GPStimes;

    private SharedPreferences sp1;
    /**
     * 接收数据更新UI
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //查询成功隐藏等待条
                    //dialogTrail.cancel();
                    //获取json对象，解析数据
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(msg.obj.toString());
                        //获取车辆是否存在的状态码 0（存在） -1（不存在）
                        String result = jsonObject.getString("result");
                        if (result.equals("0")) {
                            //解析车辆轨迹点
                            JSONArray jsonArray = jsonObject.getJSONArray("vehicles");
                            //坐标转换
                            CoordinateConverter converter = new CoordinateConverter(MonitorVehicleTrail.this);
                            // CoordType.GPS 待转换坐标类型
                            converter.from(CoordinateConverter.CoordType.GPS);

                            speeds = new ArrayList<String>();
                            GPStimes = new ArrayList<String>();
                            points = new ArrayList<LatLng>();
                            angles = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObjectVehiclesTrail = (JSONObject) jsonArray.opt(i);
                                //获取车辆位置经纬度
                                String latitude = jsonObjectVehiclesTrail.getString("latitude");
                                String longitude = jsonObjectVehiclesTrail.getString("longitude");
                                //获取车辆方向角
                                String angle = jsonObjectVehiclesTrail.getString("angle");
                                int ang = Integer.valueOf(angle);
//                                if (ang <= 90 || (ang > 180 && ang <= 270)){
//                                    ang = 360 - ang;
//                                }else{
//                                    ang = (180 - ang) * 2 + ang;
//                                }
                                ang = 360 - ang;
                                //经纬度转换成Double类型
                                Double lat = Double.valueOf(latitude);
                                Double log = Double.valueOf(longitude);
                                //获取车辆速度
                                String speed = jsonObjectVehiclesTrail.getString("speed");
                                //获取GPS定位时间
                                String gps_time = jsonObjectVehiclesTrail.getString("time");
                                //车辆轨迹角度
                                angles.add(String.valueOf(ang));
                                //车辆轨迹点速度list
                                speeds.add(speed);
                                //车辆轨迹点定位时间list
                                GPStimes.add(gps_time);
                                float sp = Float.parseFloat(speed);
                                //Long time = Long.valueOf(gps_time);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                long ts = 0L;
                                try {
                                    Date time = simpleDateFormat.parse(gps_time);
                                    ts = time.getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                LatLng latLng = new LatLng(lat, log);
                                // sourceLatLng待转换坐标点 LatLng类型
                                converter.coord(latLng);
                                // 执行转换操作
                                LatLng desLatLng = converter.convert();
                                points.add(new LatLng((float) desLatLng.latitude, (float) desLatLng.longitude));
                            }
                            //添加起点的marker
                            markerOption.icon(BitmapDescriptorFactory
                                    .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_origin)));
                            marker = aMap.addMarker(markerOption);
                            marker.setPosition(points.get(0));
                            marker.setAnchor(0.5f, 0.5f);
                            aMap.moveCamera(CameraUpdateFactory.changeLatLng(points.get(i)));
                            dialogTrail.cancel();

                            isHaveTrailPlay = true;
                            //测试
                            System.out.println("测试结果：" + msg.obj.toString());
                        } else if (result.equals("-1")) {
                            dialogTrail.cancel();
                            //如果返回状态码为-1，则车辆不存在
                            Toast toast = Toast.makeText(MonitorVehicleTrail.this, "无车辆轨迹", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(MonitorVehicleTrail.this, "无车辆轨迹", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    trailLatlng.setText(msg.obj.toString());
                    break;
                case 2:
                    trailStartPlay.setImageResource(R.mipmap.ic_start_play);
                    isStartPlay = false;
                    isEndPlay = true;
                    break;
                case 3:
                    //更新车速
                    tvSpeeds.setText(msg.getData().getString("sp") + "km/h");
                    //更新定位时间
                    tvGPStime.setText(msg.getData().getString("times"));
                    //更新位置（经纬度）
                    trailLatlng.setText(msg.getData().getString("latlons"));
                    break;
                case 4:
                    marker.setPosition(points.get(i));
                    marker.setPeriod(0);
                    marker.setToTop();
                    marker.setAnchor(0.5f, 0.5f);
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(points.get(i)));
                case -1:
                    Toast.makeText(MonitorVehicleTrail.this, "查询失败", Toast.LENGTH_LONG).show();
                    dialogTrail.cancel();
                    break;
            }
        }
    };


    PolylineOptions polt = new PolylineOptions();


    /**
     * 开始播放车辆轨迹
     */
    private void startVehicleTrail() {
//        if(points.size() == 0)
//            return;
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(points.get(0)));
        //开启定时器，每n秒添加一个marker
        timerAddMarker = new Timer();
        timerAddMarker.schedule(new TimerTask() {
            @Override
            public void run() {
                // 暂停
                if (ConstantClass.isTimerStart.equals("-1")) {
                    return;
                } else {
                    //开始播放车辆轨迹
                    if (i == 0) {
                        //添加起点线路
                        markerOption.icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_origin)));
                        marker = aMap.addMarker(markerOption);
                        marker.setAnchor(0.5f, 0.5f);

                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(points.get(0)));

                    } else if (i == angles.size() - 1) {

                        //添加终点的线路marker
                        markerOption.icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_destination)));
                        marker = aMap.addMarker(markerOption);
                        marker.setAnchor(0.5f, 0.5f);

                        //marker.setRotateAngle(0);
                    } else {
                        markerOption.icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_angle)));
                        marker.setRotateAngle(Float.parseFloat(angles.get(i)));
                        marker = aMap.addMarker(markerOption);
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(points.get(i)));
                        //设置marker偏移量
                        marker.setAnchor(0.5f, 0.5f);

                    }

                    //aMap.moveCamera(CameraUpdateFactory.changeLatLng(points.get(0)));
                    if (i <= points.size() - 1) {

                        if (i == 0) {
                            marker.setToTop();
                        }
                        marker.setPosition(points.get(i));
                        //marker.setRotateAngle(Float.parseFloat(angles.get(i)));
                        if (i > 0) {
                            List<LatLng> path = new ArrayList<LatLng>();
                            LatLng lg1 = new LatLng(points.get(i - 1).latitude, points.get(i - 1).longitude);
                            LatLng lg2 = new LatLng(points.get(i).latitude, points.get(i).longitude);

                            if (!lg1.equals(lg2)) {
                                path.add(points.get(i - 1));
                                path.add(points.get(i));
                                polt.setPoints(path);
                                polt.width(13).geodesic(true).color(Color.GREEN);
                                aMap.addPolyline(polt);
                            }
                        }
                    }
                    //如果i等于points的长度则停止定时器，播放完毕
                    if (i == points.size() - 1) {
                        ConstantClass.isTimerStart = "-1";
                        //播放结束
                        isEndPlay = true;
                        timerAddMarker.cancel();
                        timerAddMarker.purge();
                        i = 0;
                        Message msg = new Message();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                    Message message = new Message();
                    message.what = 3;
                    Bundle bundle = new Bundle();
                    bundle.putString("sp", speeds.get(i));
                    bundle.putString("times", GPStimes.get(i));
                    bundle.putString("latlons", points.get(i).toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
                    i++;
                }
            }
        }, 1, ConstantClass.time);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_vehicl_trail);
        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map_trail);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);

        initView();


    }

    /**
     * 初始化控件
     */
    private void initView() {
        sp1 = this.getSharedPreferences("login_state", 0);

        //车辆轨迹播放速度滑动条
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        //车辆轨迹实时位置
        trailLatlng = (TextView) findViewById(R.id.trailLatlng1);
        //车辆轨迹实时车速
        tvSpeeds = (TextView) findViewById(R.id.tvSpeeds);
        //车辆轨迹实时位置（经纬度）
        tvGPStime = (TextView) findViewById(R.id.tvGPStime);
        //title文字显示内容
        title_context = (TextView) findViewById(R.id.title_context);
        //返回按钮（上个页面）
        title_break = (ImageView) findViewById(R.id.title_break);
        //更改车辆，日期（轨迹）按钮
        vehicle_trail_date = (ImageView) findViewById(R.id.vehicle_trail_date);
        //开始播放按钮
        trailStartPlay = (ImageView) findViewById(R.id.trailStartPlay);
        //开始停止播放按钮
        trailStopPlay = (ImageView) findViewById(R.id.trailStopPlay);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        //title 设置文字
        Bundle bundle = getIntent().getExtras();
        context = bundle.getString("vehicleid");
        title_context.setText(context);
        //滑动条监听事件
        seekBar.setOnSeekBarChangeListener(this);
        //title 返回点击监听事件
        title_break.setOnClickListener(this);
        //更改车辆轨迹的（日期，车牌号）按钮点击监听事件
        vehicle_trail_date.setOnClickListener(this);
        //开始播放按钮点击事件
        trailStartPlay.setOnClickListener(this);
        //停止播放按钮点击事件
        trailStopPlay.setOnClickListener(this);

        changeVehicleMessage();
    }

    String sp = "0.00km/h";
    String times = "0000 - 00 - 00 00:00:00";
    String latlons = "0.00000,0.00000";

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_break:
                //销毁页面，返回到上一个页面
                finish();
                isHaveTrailPlay = false;
                break;
            case R.id.vehicle_trail_date:
                //更改车辆轨迹的（日期，车牌号）
                changeVehicleMessage();
                break;
            case R.id.trailStartPlay:
                if (isHaveTrailPlay == true) {
                    //判断是否播放结束，如果为true则再次点击是，重新播放轨迹
                    if (isEndPlay == true) {
                        trailStartPlay.setImageResource(R.mipmap.ic_end_play);
                        isEndPlay = false;
                        isStartPlay = true;
                        i = 0;
                        //查询服务器数据
                        aMap.clear();
                        ConstantClass.isTimerStart = "0";
                        startVehicleTrail();
                    } else {
                        if (isStartPlay == false) {
                            //如果播放按钮为false 点击按钮时开始播放车辆轨迹，并将isStartPlay设置为true，
                            //如果播放按钮为true 点击按钮时停止播放车辆轨迹，并将isStartPlay设置为false
                            trailStartPlay.setImageResource(R.mipmap.ic_end_play);

                            isStartPlay = true;
                            //查询服务器数据
                            ConstantClass.isTimerStart = "0";
                            //在地图上添加mark显示车辆具体位置信息
//                        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
//                                .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_origin)))
//                                .draggable(true);
//                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(points.get(0)));
//                        //marker = aMap.addMarker(markerOption);
//                        marker.setPosition(points.get(0));
                        } else {
                            trailStartPlay.setImageResource(R.mipmap.ic_start_play);
                            ConstantClass.isTimerStart = "-1";
                            isStartPlay = false;
                        }
                    }
                } else {
                    Toast.makeText(MonitorVehicleTrail.this, "无车辆轨迹播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.trailStopPlay:
                if (isHaveTrailPlay == true) {
                    //判断是否播放结束，如果为true则再次点击是，重新播放轨迹
                    //清空地图
                    aMap.clear();
                    timerAddMarker.cancel();
                    timerAddMarker.purge();
                    ConstantClass.isTimerStart = "-1";
                    i = 0;
                    trailStartPlay.setImageResource(R.mipmap.ic_start_play);
                    //findVehicleTrails(vehicleid, tate, startTime, endTime);
                    isEndPlay = true;
                    isStartPlay = false;
                    if (isHaveTrailPlay == true) {
                        markerOption.icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_origin)));
                        marker = aMap.addMarker(markerOption);
                        marker.setPosition(points.get(0));
                    }
                }
            default:
                break;
        }
    }

    private String vehicleid;
    //日期
    private String tate;
    private String startTime;
    private String endTime;

    /**
     * 更改车辆轨迹的（日期，车牌号，时间）
     */
    private void changeVehicleMessage() {

        //builder.setMessage(vehicleid, "(" + latitude + "," + longitude + ")", angle, icon_type, speed, miles, gps_time);
        /**
         * dialog确定按钮点击事件
         */
        builder.setTV_break_onClickListener("确  定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //清空地图（清空上次查询的车辆轨迹）
                aMap.clear();
                //获取dialog上的车辆号信息
                vehicleid = builder.dialog_vehicle_vehicleid.getText().toString();
                //将dialog上的车辆号信息设置到页面title
                title_context.setText(vehicleid);
                context = vehicleid;
                dialog_date = builder.vehicleTrailDate.getText().toString();
                dialog_start_time = builder.startTime.getText().toString();
                dialog_end_time = builder.endTime.getText().toString();
                //如果dialog的车辆号信息为空，则显示错误提示信息 ，否则关闭dialog并查询服务器数据
                if ("".equals(builder.dialog_vehicle_vehicleid.getText().toString())) {
                    builder.dialog_vehicle_vehicleid_isNull.setVisibility(View.VISIBLE);
                } else {
                    //关闭弹出框
                    dialog.dismiss();
                    //车辆查询进度条
                    dialogTrail = new ProgressDialog(MonitorVehicleTrail.this);
                    dialogTrail.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialogTrail.setCanceledOnTouchOutside(false);
                    dialogTrail.setMessage("正在查询中···");
                    dialogTrail.show();
                    //日期
                    tate = builder.vehicleTrailDate.getText().toString();
                    startTime = builder.startTime.getText().toString();
                    endTime = builder.endTime.getText().toString();

                    i = 0;
                    ConstantClass.isTimerStart = "-1";
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                    //查询服务器数据
                    findVehicleTrails(vehicleid, tate, startTime, endTime);
                    markerOption = new MarkerOptions();
                    //marker.setRotateAngle(0);
                }
            }
        });

        /**
         * dialog取消按钮点击事件
         */
        builder.setdialog_vehicle_trail_cancelOnClickListener("取 消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /**
         * dialog更改车牌号点击事件
         */
        builder.setVehicle_vehicleidClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MonitorVehicleTrail.this,"dialog车牌号点击事件",Toast.LENGTH_LONG).show();
                if (sp1.getString("id_owner","").equals("")){
                    Intent intent = new Intent(MonitorVehicleTrail.this,MonitorOwnerSearch.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("class","MonitorVehicleTrail");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MonitorVehicleTrail.this,MonitorQueueSearch.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("class","MonitorVehicleTrail");
                    bundle.putString("name",sp1.getString("username",""));
                    bundle.putString("id_owner","");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        /**
         * dialog更改日期点击事件
         */
        builder.setContext_date_onClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                //Toast.makeText(MonitorVehicleTrail.this,"dialog日期点击事件",Toast.LENGTH_LONG).show();
                //显示修改日期的dialog
                Calendar c = Calendar.getInstance();
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                int y = 0;
                int m = 0;
                int d = 0;
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(builder.vehicleTrailDate.getText().toString());
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(date);
                    y = c1.get(Calendar.YEAR);
                    m = c1.get(Calendar.MONTH);
                    d = c1.get(Calendar.DATE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new DatePickerDialog(MonitorVehicleTrail.this,
                        // 绑定监听器
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                //更改dialog上的日期内容
                                builder.vehicleTrailDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }
                        // 设置初始日期
                        , y, m, d).show();
            }
        });
        /**
         * dialog更改起始时间点击事件
         */
        builder.setContext_start_timeClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MonitorVehicleTrail.this,"dialog起始时间点击事件",Toast.LENGTH_LONG).show();
                //显示修改日期的dialog
                Calendar c = Calendar.getInstance();
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                int h = 0;
                int m = 0;
                try {
                    Date date = new SimpleDateFormat("HH:mm").parse(builder.startTime.getText().toString());
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(date);
                    h = c1.get(Calendar.HOUR);
                    m = c1.get(Calendar.MINUTE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new TimePickerDialog(MonitorVehicleTrail.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        builder.startTime.setText((hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute));
                    }
                }, h, m, true).show();
            }
        });
        /**
         * dialog更改终止时间点击事件
         */
        builder.setContext_end_timeClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MonitorVehicleTrail.this,"dialog终止时间点击事件",Toast.LENGTH_LONG).show();
                //显示修改日期的dialog
                Calendar c = Calendar.getInstance();
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                int h = 0;
                int m = 0;
                try {
                    Date date = new SimpleDateFormat("HH:mm").parse(builder.endTime.getText().toString());
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(date);
                    h = c1.get(Calendar.HOUR);
                    m = c1.get(Calendar.MINUTE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new TimePickerDialog(MonitorVehicleTrail.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        builder.endTime.setText((hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute));
                    }
                }, h, m, true).show();
            }
        });

        //弹出dialog
        builder.create().show();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        builder.vehicleTrailDate.setText(sf.format(new Date()));

        if (!(context.equals("车辆轨迹回放"))) {
            builder.dialog_vehicle_vehicleid.setText(context);
            builder.dialog_vehicle_vehicleid.setTextColor(Color.parseColor("#15B2DB"));
        } else {
            builder.dialog_vehicle_vehicleid.setHint("请选择查询的车牌号");
            builder.dialog_vehicle_vehicleid.setHintTextColor(Color.parseColor("#B0F0F0"));
            builder.dialog_vehicle_vehicleid.setTextColor(Color.parseColor("#15B2DB"));
        }
        //显示dialog查询车辆轨迹的缓存信息
        if (!dialog_date.equals("") || !dialog_start_time.equals("") || !dialog_end_time.equals("")) {
            builder.vehicleTrailDate.setText(dialog_date);
            builder.startTime.setText(dialog_start_time);
            builder.endTime.setText(dialog_end_time);
        }
    }

    /**
     * 器车辆轨迹点
     */
    private void findVehicleTrails(String vehicleNo, String date, String startTime, String endTime) {
        /**
         * 初始话用户信息文件
         */
        SharedPreferences sp1 = this.getSharedPreferences("login_state", this.MODE_PRIVATE);
        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleNo", vehicleNo);
        params.put("vehicleDate", date);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("id_owner", sp1.getString("id_owner", ""));
        HttpUtil.sendHttpRequestForPost(sp1.getString("URL", "") + ConstantClass.URL_VEHICLE_TRAIL, params, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                System.out.print("车辆轨迹点" + response);
                Message msg = new Message();
                msg.obj = response;
                msg.what = 0;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
                Message msg = new Message();
                msg.obj = "请检查网络是否连接/";
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });

    }

    /**
     * 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        totalDouration = ((100 - progress) * 10L + 80L);
        ConstantClass.time = totalDouration;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //Logs.e("------------", "开始滑动！");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //Logs.e("------------", "停止滑动！");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    /**
     * activity从不可见到可见时调用
     */
    @Override
    protected void onStart() {
        super.onStart();
        builder.dialog_vehicle_vehicleid.setText(ConstantClass.vehicleNO);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }
}
