package com.onlydoone.busposition.bean;

/**
 * Created by zhaohui on 2017/3/7.
 */

public class VehicleNameData {
    /**
     * 车辆状态
     */
    private String state;
    /**
     * 车牌号
     */
    private String vehicleName;
    /**
     * 车辆终端SIM号
     */
    private String simNo;

    public VehicleNameData() {
    }

    public VehicleNameData(String state, String vehicleName, String simNo) {
        this.state = state;
        this.vehicleName = vehicleName;
        this.simNo = simNo;
    }

    public VehicleNameData(String state, String vehicleName) {
        this.state = state;
        this.vehicleName = vehicleName;
    }

    public String getSimNo() {
        return simNo;
    }

    public void setSimNo(String simNo) {
        this.simNo = simNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }
}
