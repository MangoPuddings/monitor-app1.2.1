package com.onlydoone.busposition.classs;

/**
 * Created by zhaohui on 2017/1/19.
 */
public class ConstantClass {

    //本地地址
    //public static String oil_fill = "http://192.168.2.202:9001/";

    //测试
    public static String location = "http://123.57.139.103:9001/";

    //服务器地址（油补平台）
    public static String oil_fill = "http://123.57.18.196:15000/";

    //视频服务器地址(定位平台）
    //public static String location = "http://47.93.114.174:15000/";

    //获取服务器最新版本号
    public  static String URL_VERSION = "getVersion";

    //用户登陆接口
    public  static String URL_LOGIN = "userLogon";

    //车辆状态查询接口
    public  static String URL_VEHICLE_STATE = "findVehicleState";

    //搜索框提示 模糊查询车辆接口
    public  static String URL_VEHICLE_VAGUE= "findVehicleVague";

    //搜索框提示 模糊查询车辆接口
    public  static String URL_VEHICLE_VAGUE_MONITOR = "findVehicleVagueMonitor";

    //车辆位置查询接口
    public  static String URL_VEHICLE = "findVehicle";

    //车辆轨迹点接口
    public  static String URL_VEHICLE_TRAIL = "findVehicleTrail";

    //目前在线车辆数<折线图数据>接口
    public  static String URL_VEHICLE_ON_LINE = "findVehicleOnline";

    //目前在线车辆数<listview数据>接口
    public  static String URL_VEHICLE_ONLINE_RATE = "findVehicleOnlineRate";

    //获取服务器车辆行驶里程接口
    public  static String URL_VEHICLE_MILES = "findVehicleMiles";

    //获取服务器业户车队车辆信息接口
    public  static String URL_VEHICLE_MONITOR = "findVehicleMonitor";

    //获取服务器车辆报警信息接口
    public  static String URL_VEHICLE_POLICE = "findVehiclePolice";


    //定时器是否开启（0开启  -1未开启）
    public static String isTimerStart= "-1";

    //定时器是默认每x秒执行一次
    public static Long time = 330L;

    //-1（第一次加载数据）     0（更新最新的数据（车辆在线数））
    public static String isUpdate = "-1";

    //是否第一次进入更新
    public static int isUp = -1;

    //listView的footitem是否显示（-1 未显示，0 显示）按天查询
    public static int isFootitemData = -1;

    //listView的footitem是否显示（-1 未显示，0 显示）按天查询
    public static int isFootitemPolice = -1;

    //listView的footitem是否显示（-1 未显示，0 显示） 按月查询
    public static int isFootitemMonth = -1;

    //是否第一次进入更新
    public static String CONNECTION_EXCEPTION = "连接失败，请检查网络是否连接";

    //车队id
    public static String QUEUE_ID = "";

    //业户id
    public static String OWNER_ID = "";

    //车辆手机号
    public static String sim_no = "";

    //车辆号
    public static String vehicleNO = "";

    //搜索框内容
    public static String searchContext = "";

    //是否为Monitor搜索内容
    public static boolean isSearch = false;

    //判断跳转到哪个类
    public static String INTENT_CLASS = "";

    //报警类型数量
    public static int num = 0;
}
