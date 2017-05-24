package com.onlydoone.busposition.bean;

/**
 * Created by Administrator on 2017/1/16 0016.
 */

public class Vehicle_Data {
    /**
     * 服务端车辆查询接口url
     */
    private String url;
    /**
     * 用户输入的车辆号
     */
    private String search_content;
    /**
     * 服务器返回的车辆状态码
     *      0（存在） -1（不存在）
     */
    private String result;
    /**
     * 服务器返回的车辆号
     */
    private String vehicleid;
    /**
     * 服务器返回的车辆纬度
     */
    private Double lat;
    /**
     * 服务器返回的车辆经度
     */
    private Double lon;

    public Vehicle_Data() {
    }

    /**
     * 用户输入车牌号数据
     *      构造参数
     *          search_content（用户输入车牌号
     */
    public Vehicle_Data(String search_content) {
        this.search_content = search_content;
    }

    /**
     * 车辆信息数据
     *      构造参数
     *          vehicleid（车牌号）lat（纬度）lon（经度）
     */
    public Vehicle_Data(String vehicleid, Double lat, Double lon) {
        this.vehicleid = vehicleid;
        this.lat = lat;
        this.lon = lon;
    }

    public String getSearch_content() {
        return search_content;
    }

    public void setSearch_content(String search_content) {
        this.search_content = search_content;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getVehicleid() {
        return vehicleid;
    }

    public void setVehicleid(String vehicleid) {
        this.vehicleid = vehicleid;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
