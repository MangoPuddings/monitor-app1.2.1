package com.onlydoone.busposition.bean;

/**
 * Created by zhaohui on 2017/3/16.
 */

public class VehiclePoliceListViewData {
    private String tv_vehicle_police_vehicleid, tv_vehicle_police_state, tv_vehicle_police_type,
            tv_vehicle_police_owner, tv_vehicle_police_startTime;

    public VehiclePoliceListViewData() {
    }

    public VehiclePoliceListViewData(String tv_vehicle_police_vehicleid, String tv_vehicle_police_state,
                                     String tv_vehicle_police_owner,String tv_vehicle_police_type,
                                     String tv_vehicle_police_startTime) {
        this.tv_vehicle_police_vehicleid = tv_vehicle_police_vehicleid;
        this.tv_vehicle_police_state = tv_vehicle_police_state;
        this.tv_vehicle_police_type = tv_vehicle_police_type;
        this.tv_vehicle_police_owner = tv_vehicle_police_owner;
        this.tv_vehicle_police_startTime = tv_vehicle_police_startTime;
    }

    public String getTv_vehicle_police_vehicleid() {
        return tv_vehicle_police_vehicleid;
    }

    public void setTv_vehicle_police_vehicleid(String tv_vehicle_police_vehicleid) {
        this.tv_vehicle_police_vehicleid = tv_vehicle_police_vehicleid;
    }

    public String getTv_vehicle_police_state() {
        return tv_vehicle_police_state;
    }

    public void setTv_vehicle_police_state(String tv_vehicle_police_state) {
        this.tv_vehicle_police_state = tv_vehicle_police_state;
    }

    public String getTv_vehicle_police_type() {
        return tv_vehicle_police_type;
    }

    public void setTv_vehicle_police_type(String tv_vehicle_police_type) {
        this.tv_vehicle_police_type = tv_vehicle_police_type;
    }

    public String getTv_vehicle_police_owner() {
        return tv_vehicle_police_owner;
    }

    public void setTv_vehicle_police_owner(String tv_vehicle_police_owner) {
        this.tv_vehicle_police_owner = tv_vehicle_police_owner;
    }

    public String getTv_vehicle_police_startTime() {
        return tv_vehicle_police_startTime;
    }

    public void setTv_vehicle_police_startTime(String tv_vehicle_police_startTime) {
        this.tv_vehicle_police_startTime = tv_vehicle_police_startTime;
    }
}
