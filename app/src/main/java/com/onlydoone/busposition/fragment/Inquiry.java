package com.onlydoone.busposition.fragment;


import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.dialog.Dialog_Vehicle_Trail;
import com.onlydoone.busposition.Utils.http.HttpCallbackListener;
import com.onlydoone.busposition.Utils.http.HttpUtil;
import com.onlydoone.busposition.adapter.Search_Adapter;
import com.onlydoone.busposition.app.monitor.MonitorOwnerSearch;
import com.onlydoone.busposition.app.monitor.MonitorVehicle4G;
import com.onlydoone.busposition.app.monitor.MonitorVehicleTrail;
import com.onlydoone.busposition.bean.Search_Data;
import com.onlydoone.busposition.classs.ConstantClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 2016/12/17 0017.
 */
public class Inquiry extends Fragment implements LocationSource, AMapLocationListener, TextWatcher,
        AdapterView.OnItemClickListener, AMap.InfoWindowAdapter {
    private String simNo;

    //获取车牌号
    private String vehicleid;
    //获取车辆速度
    private String speed;
    //获取车辆行驶里程
    private String miles;
    //获取海拔
    private String altitude;
    //获取车牌颜色状态
    private String vclidcolor;
    //获取车辆位置经纬度
    private String lonlat;
    //获取方位角度
    private String angle;
    //获取车辆所属业户
    private String ownername;
    //获取车辆所属车队
    private String queuename;
    //获取GPS定位时间
    private String gps_time;
    //获取车辆状态
    private String state;


    /**
     * 检索更多车辆信息按钮
     */
    private ImageView iv_search_more;
    /**
     * 全局变量（marker是否被点击）
     * 默认为false（未被点击）
     */
    private Boolean isMarker = false;
    /**
     * 全局变量（车辆）
     * isTimerVehicle为ture时开启新任务
     */
    private Boolean isTimerVehicle = false;
    /**
     * 全局变量（车辆状态）
     * isTimerVehicleState为ture时开启新任务
     */
    private Boolean isTimerVehicleState = false;
    /**
     * 定时器工具类
     */
    private Timer timer_vehicle;
    /**
     * 定时器工具类
     */
    private Timer timer_vehicleState;

    private TextView mLocationErrText;
    /**
     * 车辆经纬度
     */
    private LatLng latlng;
    /**
     * 车辆位置信息图标
     */
    private Marker marker;
    /**
     * c车辆查询进度条
     */
    private ProgressDialog vehicle_dialog;
    /**
     * 自定义标记
     */
    private MarkerOptions markerOption;
    /**
     * 初始化搜索框
     */
    private AutoCompleteTextView tv_search;
    /**
     * 初始化清空搜索框内容按钮
     */
    private ImageView iv_search_delete;
    /**
     * 初始化搜索框适配器
     */
    private Search_Adapter search_adapter;
    /**
     * 初始化搜索框按钮
     */
    private ImageView iv_search;
    /**
     * 搜索提示数据源
     */
    private List<Search_Data> search_data;
    /**
     * //初始化搜索框用户输入的内容
     */
    private String search_content;
    /**
     * 初始话用户信息文件
     */
    private SharedPreferences sp;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);

    private AMap aMap;
    private MapView mapView;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;


    //车辆经纬度
    private Double lat;
    private Double lon;

    /**
     * 接收子线程传来的数据
     * what  0(车辆信息） 1（车辆状态信息） -1（服务器连接异常信息）
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    vehicle_dialog.hide();

                    //Toast.makeText(getActivity(), msg.obj.toString() + " ", Toast.LENGTH_LONG).show();
                    try {
                        //获取json对象，解析数据
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        //获取车辆是否存在的状态码 0（存在） -1（不存在）
                        String result = jsonObject.getString("result");
                        if (result.equals("0")) {
                            //如果返回状态码为0，则解析车辆信息
                            JSONArray jsonArrayVehicles = jsonObject.getJSONArray("vehicles");
                            JSONObject jsonObjectVehicles = (JSONObject) jsonArrayVehicles.opt(0);
                            //获取车牌号
                            final String vehicleid = jsonObjectVehicles.getString("vehicleid");
                            //获取车辆位置经纬度
                            final String latitude = jsonObjectVehicles.getString("latitude");
                            final String longitude = jsonObjectVehicles.getString("longitude");
                            //获取方位角度
                            String angle = jsonObjectVehicles.getString("angle");
                            if (angle.equals("")){
                                angle = "0";
                                //如果返回状态码为-1，则车辆不存在
                                Toast toast = Toast.makeText(getActivity(), "查询车辆不存在", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                timer_vehicle.cancel();
                                timer_vehicle.purge();
                                return;
                            }

                            float ang = Float.valueOf(angle);

                            //转换字符串为double类型
                            lat = Double.valueOf(latitude);
                            lon = Double.valueOf(longitude);
                            //设置车辆位置的经纬度
                            latlng = new LatLng(lat, lon);
                            //坐标转换
                            CoordinateConverter converter = new CoordinateConverter(getActivity());
                            // CoordType.GPS 待转换坐标类型
                            converter.from(CoordinateConverter.CoordType.GPS);
                            // sourceLatLng待转换坐标点 LatLng类型
                            converter.coord(latlng);
                            // 执行转换操作
                            LatLng desLatLng = converter.convert();
                            //设置marker的方位角度
                            ang = 360 - ang;
                            marker.setRotateAngle(ang);
                            marker.setPosition(desLatLng);
                            //设置地图中心点

                            aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    //请求服务器数据
                                    if (!isTimerVehicleState) {
                                        httpPostVehicleState(sp.getString("URL","") + ConstantClass.URL_VEHICLE_STATE, vehicleid);
                                        isTimerVehicleState = true;
                                        isMarker = true;
                                    } else {
                                        timer_vehicleState.cancel();
                                        timer_vehicleState.purge();
                                        httpPostVehicleState(sp.getString("URL","") + ConstantClass.URL_VEHICLE_STATE, vehicleid);
                                    }
                                    return false;
                                }
                            });
                            vehicle_dialog.hide();
                            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latlng));
                        } else if (result.equals("-1")) {
                            //如果返回状态码为-1，则车辆不存在
                            Toast toast = Toast.makeText(getActivity(), "查询车辆不存在", Toast.LENGTH_LONG);
                            //addMarkersToMap();
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            timer_vehicle.cancel();
                            timer_vehicle.purge();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast toast = Toast.makeText(getActivity(), "json解析失败", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        timer_vehicle.cancel();
                        timer_vehicle.purge();
                    }
                    break;
                case 1:
                    //Toast.makeText(getActivity(), msg.obj.toString() + " ", Toast.LENGTH_LONG).show();
                    System.out.print("输入框提示数据" + msg.obj.toString());
                    //如果用户输入的字符串大于1，显示提示内容
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        //获取车辆是否存在的状态码 0（存在） -1（不存在）
                        String result = jsonObject.getString("result");
                        if (result.equals("0")) {
                            //如果返回状态码为0，则解析车辆信息
                            //Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();
                            JSONArray jsonArrayVehicles = jsonObject.getJSONArray("vehicles");
                            //解析json数据
                            jsonDate(jsonArrayVehicles);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                    break;
                case -1:
                    vehicle_dialog.hide();
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_LONG).show();
                    timer_vehicle.cancel();
                    timer_vehicle.purge();
                    break;
            }
        }
    };

    /**
     * 解析json数据
     */
    private void jsonDate(final JSONArray jsonArrayVehicles) {
        try {
            search_data = new ArrayList<Search_Data>();
            for (int i = 0; i < jsonArrayVehicles.length(); i++) {
                JSONObject jsonObjectVehicles = (JSONObject) jsonArrayVehicles.get(i);
                Search_Data sd = new Search_Data(jsonObjectVehicles.getString("vehicleid"),jsonObjectVehicles.getString("sim_no"));
                search_data.add(sd);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        search_adapter = new Search_Adapter(this.getActivity(), search_data);
        tv_search.setAdapter(search_adapter);
    }

    /**
     * post请求车辆状态信息
     */
    private void httpPostVehicleState(String url, String search) {
        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleNo", search);
        //设置请求地址
        final String mUrl = url;

        timer_vehicleState = new Timer();
        timer_vehicleState.schedule(new TimerTask() {
            @Override
            public void run() {

                HttpUtil.sendHttpRequestForPost(mUrl, params, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        System.out.println("车辆状态" + response);
                        //获取json对象，解析数据
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.toString());
                            //获取车辆是否存在的状态码 0（存在） -1（不存在）
                            String result = jsonObject.getString("result");
                            if (result.equals("0")) {
                                //如果返回状态码为0，则解析车辆信息
                                JSONArray jsonArrayVehicles = jsonObject.getJSONArray("vehicles");
                                JSONObject jsonObjectVehicles = (JSONObject) jsonArrayVehicles.opt(0);
                                //获取车牌号
                                vehicleid = jsonObjectVehicles.getString("vehicleid");
                                //获取车辆速度
                                speed = jsonObjectVehicles.getString("speed");
                                //获取车辆行驶里程
                                miles = jsonObjectVehicles.getString("miles");
                                //获取海拔
                                altitude = jsonObjectVehicles.getString("altitude");
                                //获取车辆颜色
                                vclidcolor = jsonObjectVehicles.getString("vclidcolor");
                                //获取车辆位置经纬度
                                lonlat = jsonObjectVehicles.getString("lonlat");
                                //获取方位角度
                                angle = jsonObjectVehicles.getString("angle");
                                //获取车辆所属业户
                                ownername = jsonObjectVehicles.getString("ownername");
                                //获取车辆所属车队
                                queuename = jsonObjectVehicles.getString("queuename");
                                //获取GPS定位时间
                                gps_time = jsonObjectVehicles.getString("gps_time");
                                //获取车辆状态
                                state = jsonObjectVehicles.getString("state");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        marker.showInfoWindow();
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
        }, 0, 30 * 1000);
    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.inquiry, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        //初始化控件
        initView();

        return view;
    }

    /**
     * 初始化地图控件
     */
    private void initView() {
        //初始化地图变量
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
            setUpMap();
            //addMarkersToMap();// 往地图上添加marker
        }
        mLocationErrText = (TextView) view.findViewById(R.id.location_errInfo_text);
        mLocationErrText.setVisibility(View.GONE);


        //读取用户登录状态
        sp = view.getContext().getSharedPreferences("login_state", view.getContext().MODE_PRIVATE);

        //检索更多车辆信息按钮
        iv_search_more = (ImageView) view.findViewById(R.id.iv_search_more);
        //检索更多车辆信息按钮点击事件

        //清空搜索框内容按钮
        iv_search_delete = (ImageView) view.findViewById(R.id.iv_search_delete);
        //设置清空搜索框内容按钮点击事件
        iv_search_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MonitorOwnerSearch.class);
                Bundle bundle = new Bundle();
                bundle.putString("class","inquiry");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        iv_search_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.clear();
                ConstantClass.vehicleNO = "";
                if (isTimerVehicle) {
                    timer_vehicle.cancel();
                    timer_vehicle.purge();
                    isTimerVehicle = false;
                }
                if (isTimerVehicleState) {
                    timer_vehicleState.cancel();
                    timer_vehicleState.purge();
                    isTimerVehicleState = false;
                }
                tv_search.setText("");
                iv_search_delete.setVisibility(View.INVISIBLE);
            }
        });

        //搜索框
        tv_search = (AutoCompleteTextView) view.findViewById(R.id.tv_search);
        //搜索框监听事件
        tv_search.addTextChangedListener(this);
        //搜索框item点击事件
        tv_search.setOnItemClickListener(this);
        //搜索框按钮
        iv_search = (ImageView) view.findViewById(R.id.iv_search);
        //搜索按钮点击事件
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //searchItemOnClick();
            }
        });
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种

        setupLocationStyle();
    }

    private void setupLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.mipmap.map));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this.getActivity());
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                //Logs.e("AmapErr", errText);
            }
        }
    }

    /**
     * 输入框监听事件
     * Text改变之后被调用
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    /**
     * 输入框监听事件
     * Text改变之前被调用
     */
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    /**
     * 输入框监听事件
     * Text改变最后被调用
     */
    @Override
    public void afterTextChanged(Editable editable) {
        search_content = tv_search.getText().toString().trim();

        //如果搜索框内容为空则隐藏清除按钮，否则显示清除按钮
        if (search_content.equals("")) {
            iv_search_delete.setVisibility(View.GONE);
        }
        if (!(search_content.equals(""))) {
            iv_search_delete.setVisibility(View.VISIBLE);
        }

        //查询服务器车辆数据
        findVehicleVegue(editable.toString());

    }

    /**
     * 输入框自动完成
     *
     * @param search
     */
    private void findVehicleVegue(String search) {
        String mSearch_content = search;

        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleNo", mSearch_content);
        params.put("id_owner", sp.getString("id_owner", ""));
        //设置请求地址
        final String mUrl = sp.getString("URL","") + ConstantClass.URL_VEHICLE_VAGUE;
        HttpUtil.sendHttpRequestForPost(mUrl, params, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 1;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * 搜索按钮点击事件
     */
    public void searchItemOnClick() {
        //清除定位小蓝点
        aMap.clear();
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示

        //在地图上添加mark显示车辆具体位置信息
        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.car_trail_2)))
                .draggable(true);
        //设置自定义infowindow
        aMap.setInfoWindowAdapter(Inquiry.this);
        marker = aMap.addMarker(markerOption);
        marker.setAnchor(0.75f, 0.5f);
        marker.setTitle("1");

        //获取用户输入的车牌号
        search_content = tv_search.getText().toString();
        //清空车辆数据缓存
        clear();
        //查询服务器车辆位置信息  第一次进入程序isTimerVehicle默认为ture，可以开启新定时任务，之后isTimer为false则需要先关闭定时任务
        if (!isTimerVehicle) {
            findVehicle(sp.getString("URL","") + ConstantClass.URL_VEHICLE, search_content);
            isTimerVehicle = true;
        } else {
            timer_vehicle.cancel();
            timer_vehicle.purge();
            findVehicle(sp.getString("URL","") + ConstantClass.URL_VEHICLE, search_content);
        }

        //当切换车辆时或再次点击搜索按钮时，并且isMarker为true时执行该方法
        if (isTimerVehicleState) {
            timer_vehicleState.cancel();
            timer_vehicleState.purge();
        }

    }

    /**
     * 初始化车辆状态信息
     */
    private void clear() {
        vehicleid = null;
        speed = null;
        miles = null;
        altitude = null;
        vclidcolor = null;
        lonlat = null;
        angle = null;
        ownername = null;
        queuename = null;
        gps_time = null;
        state = null;
    }

    /**
     * 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
     */
    private void findVehicle(String url, String search) {
        //车辆查询进度条
        vehicle_dialog = new ProgressDialog(getActivity());
        vehicle_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        vehicle_dialog.setMessage("正在查询中···");
        vehicle_dialog.setCanceledOnTouchOutside(false);
        vehicle_dialog.show();

        //设置请求参数
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleNo", search);
        params.put("id_owner", sp.getString("id_owner", ""));
        //设置请求地址
        final String mUrl = url;
        timer_vehicle = new Timer();
        timer_vehicle.schedule(new TimerTask() {
            @Override
            public void run() {
                HttpUtil.sendHttpRequestForPost(mUrl, params, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {

                        System.out.println("车辆位置" + response);
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
        }, 1, 30 * 1000);
    }

    /**
     * 搜索框item点击事件
     *
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Search_Data pc = search_data.get(i);
        tv_search.setText(pc.getTv_data());
        simNo = pc.getSimNo();
        ConstantClass.sim_no = "0" + simNo;
        searchItemOnClick();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 被覆盖到下面或者锁屏时被调用
     */
    @Override
    public void onPause() {
        //Logs.e("onPause","onPause");
        super.onPause();
        //如果isTimerVehicle,isTimerVehicleState不为true则关闭定时器,并将其值设置为true
        if (isTimerVehicle) {
            timer_vehicle.cancel();
            timer_vehicle.purge();
            isTimerVehicle = false;
        }

        if (isTimerVehicleState) {
            timer_vehicleState.cancel();
            timer_vehicleState.purge();
            isTimerVehicleState = false;
        }
        mapView.onPause();
        deactivate();
    }

    /**
     * 界面从不可见状态重新回到可见状态时调用
     */
    @Override
    public void onStart() {
        //Logs.e("onStart","onStart");
        super.onStart();
        aMap.clear();

        //在地图上添加mark显示车辆具体位置信息
        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.car_trail_2)))
                .draggable(true);
        //设置自定义infowindow
        aMap.setInfoWindowAdapter(Inquiry.this);
        marker = aMap.addMarker(markerOption);
        marker.setAnchor(0.75f, 0.5f);
        marker.setTitle("1");

        if (!ConstantClass.vehicleNO.equals("")){
            tv_search.setText(ConstantClass.vehicleNO);
        }
        //获取用户输入的车牌号
        search_content = tv_search.getText().toString();
        if (!(search_content.equals(""))) {
            //查询服务器车辆位置信息  进入程序isTimer默认为ture，可以开启新定时任务，之后isTimer为false则需要先关闭定时任务
            if (!isTimerVehicle) {
                findVehicle(sp.getString("URL","") + ConstantClass.URL_VEHICLE, search_content);
                isTimerVehicle = true;
            } else {
                timer_vehicle.cancel();
                timer_vehicle.purge();
                findVehicle(sp.getString("URL","") + ConstantClass.URL_VEHICLE, search_content);
            }
            //如果marker等于true则开启新定时器
//            if (isMarker) {
//                //查询服务器车辆状态信息(marker)  第一次进入程序isTimer默认为ture，可以开启新定时任务，之后isTimer为false则需要先关闭定时任务
//                if (!isTimerVehicleState) {
//
//                    httpPostVehicleState(sp.getString("URL","") + ConstantClass.URL_VEHICLE_STATE, vehicleid);
//                    //marker.isInfoWindowShown();
//                    isTimerVehicleState = true;
//                } else {
//                    timer_vehicleState.cancel();
//                    timer_vehicleState.purge();
//                    httpPostVehicleState(sp.getString("URL","") + ConstantClass.URL_VEHICLE_STATE, vehicleid);
//                    if (isMarker){
//                        marker.hideInfoWindow();
//                    }
//                }
//            }
        }
    }

    /**
     * 被销毁时调用
     */
    @Override
    public void onDestroy() {
        //Logs.e("onDestroy","onDestroy");
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 自定义mark窗口监听回调
     * infoWindow
     *
     * @param marker
     * @return
     */
    @Override
    public View getInfoWindow(final Marker marker) {
        View infoWindow = LayoutInflater.from(getActivity()).inflate(R.layout.info_window, null);
        //自定义控件添加数据
        TextView inforwindow_vehicleid = (TextView) infoWindow.findViewById(R.id.inforwindow_vehicleid);
        TextView inforwindow_speed = (TextView) infoWindow.findViewById(R.id.inforwindow_speed);
        TextView inforwindow_miles = (TextView) infoWindow.findViewById(R.id.inforwindow_miles);
        TextView inforwindow_angle = (TextView) infoWindow.findViewById(R.id.inforwindow_angle);
        TextView inforwindow_altitude = (TextView) infoWindow.findViewById(R.id.inforwindow_altitude);
        TextView inforwindow_latlong = (TextView) infoWindow.findViewById(R.id.inforwindow_latlong);
        TextView inforwindow_vclidcolor = (TextView) infoWindow.findViewById(R.id.inforwindow_vclidcolor);
        TextView inforwindow_ownername = (TextView) infoWindow.findViewById(R.id.inforwindow_ownername);
        TextView inforwindow_queuename = (TextView) infoWindow.findViewById(R.id.inforwindow_queuename);
        TextView inforwindow_GPS_time = (TextView) infoWindow.findViewById(R.id.inforwindow_GPS_time);
        TextView inforwindow_state = (TextView) infoWindow.findViewById(R.id.inforwindow_state);

        ImageView inforwindow_monitor = (ImageView) infoWindow.findViewById(R.id.inforwindow_monitor);
        ImageView inforwindow_break = (ImageView) infoWindow.findViewById(R.id.inforwindow_break);
        ImageView inforwindow_contrail = (ImageView) infoWindow.findViewById(R.id.inforwindow_contrail);

        //车牌号
        inforwindow_vehicleid.setText(vehicleid);
        //车速
        inforwindow_speed.setText(speed);
        //车辆所属里程
        inforwindow_miles.setText(miles + "km");
        //车辆方位
        inforwindow_angle.setText(angle);
        //海拔
        inforwindow_altitude.setText(altitude);
        //经纬度
        inforwindow_latlong.setText(lonlat);
        //车牌颜色
        inforwindow_vclidcolor.setText(vclidcolor);
        //所属业户
        inforwindow_ownername.setText(ownername);
        //所属车队
        inforwindow_queuename.setText(queuename);
        //GPS定位时间
        inforwindow_GPS_time.setText(gps_time);
        //车辆状态
        inforwindow_state.setText(state);

        /**
         * marker窗体返回按钮点击监听事件
         */
        inforwindow_break.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.hideInfoWindow();
                //如果isTimerVehicle,isTimerVehicleState不为true则关闭定时器,并将其值设置为true, isMarker是否被点击设置为false
                if (isTimerVehicleState) {
                    timer_vehicleState.cancel();
                    timer_vehicleState.purge();
                    isTimerVehicleState = false;
                    isMarker = false;
                }
            }
        });

        /**
         * marker窗体车辆实时监听按钮点击监听事件
         */
        inforwindow_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果isTimerVehicle,isTimerVehicleState不为true则关闭定时器,并将其值设置为true
                if (isTimerVehicle) {
                    timer_vehicle.cancel();
                    timer_vehicle.purge();
                    isTimerVehicle = false;
                }
                if (isTimerVehicleState) {
                    timer_vehicleState.cancel();
                    timer_vehicleState.purge();
                    isTimerVehicleState = false;

                }
                //跳转到车辆实时监控界面（并将车辆号传递过去）
                Intent intent = new Intent(getActivity(), MonitorVehicle4G.class);
                Bundle bundle = new Bundle();
                bundle.putString("vehicleid", vehicleid);
                //bundle.putString("sim_no","0" + simNo);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        /**
         * marker窗体车辆轨迹按钮点击监听事件
         */
        inforwindow_contrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerVehicle) {
                    timer_vehicle.cancel();
                    timer_vehicle.purge();
                    isTimerVehicle = false;
                }
                if (isTimerVehicleState) {
                    timer_vehicleState.cancel();
                    timer_vehicleState.purge();
                    isTimerVehicleState = false;
                }
                //跳转到车辆实时监控界面（并将车辆号传递过去）
                Dialog_Vehicle_Trail.Builder builder = new Dialog_Vehicle_Trail.Builder(getActivity());
                Intent intent = new Intent(getActivity(), MonitorVehicleTrail.class);
                Bundle bundle = new Bundle();
                bundle.putString("vehicleid", vehicleid);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
