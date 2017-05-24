package com.onlydoone.busposition.bean;

/**
 * 车辆在线率Data类
 */
public class VehicleOnlineListViewData {
    /**
     * 车队
     */
    String vehicleQueue;
    /**
     * 车辆在在线数量
     */
    String onlineNum;
    /**
     * 车辆总数
     */
    String allNum;
    /**
     * 在线率
     */
    String onlineRate;

    public VehicleOnlineListViewData() {
    }

    public VehicleOnlineListViewData(String vehicleQueue, String onlineNum, String allNum, String onlineRate) {
        this.vehicleQueue = vehicleQueue;
        this.onlineNum = onlineNum;
        this.allNum = allNum;
        this.onlineRate = onlineRate;
    }

    public String getVehicleQueue() {
        return vehicleQueue;
    }

    public void setVehicleQueue(String vehicleQueue) {
        this.vehicleQueue = vehicleQueue;
    }

    public String getOnlineNum() {
        return onlineNum;
    }

    public void setOnlineNum(String onlineNum) {
        this.onlineNum = onlineNum;
    }

    public String getAllNum() {
        return allNum;
    }

    public void setAllNum(String allNum) {
        this.allNum = allNum;
    }

    public String getOnlineRate() {
        return onlineRate;
    }

    public void setOnlineRate(String onlineRate) {
        this.onlineRate = onlineRate;
    }
}
