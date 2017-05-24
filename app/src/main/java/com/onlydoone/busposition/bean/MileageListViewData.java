package com.onlydoone.busposition.bean;

/**
 * Created by zhaohui on 2017/2/20.
 */

public class MileageListViewData {
    private String tv_vehicleNo;
    private String tv_vehicle_owner;
    private String tv_vehicle_times1;
    private String tv_vehicle_times2;
    private String tv_vehicle_times3;
    private String tv_vehicle_times4;
    private String tv_vehicle_times5;
    private String tv_vehicle_times6;
    private String tv_vehicle_times7;

    int width;
    int width2;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth2() {
        return width2;
    }

    public void setWidth2(int width2) {
        this.width2 = width2;
    }


    public MileageListViewData() {

    }


    public MileageListViewData(int width, int width2,String tv_vehicleNo, String tv_vehicle_owner, String tv_vehicle_times1,
                               String tv_vehicle_times2, String tv_vehicle_times3, String tv_vehicle_times4,
                               String tv_vehicle_times5, String tv_vehicle_times6, String tv_vehicle_times7) {
        this.width = width;
        this.width2 = width2;
        this.tv_vehicleNo = tv_vehicleNo;
        this.tv_vehicle_owner = tv_vehicle_owner;
        this.tv_vehicle_times1 = tv_vehicle_times1;
        this.tv_vehicle_times2 = tv_vehicle_times2;
        this.tv_vehicle_times3 = tv_vehicle_times3;
        this.tv_vehicle_times4 = tv_vehicle_times4;
        this.tv_vehicle_times5 = tv_vehicle_times5;
        this.tv_vehicle_times6 = tv_vehicle_times6;
        this.tv_vehicle_times7 = tv_vehicle_times7;
    }

    public String getTv_vehicle_times1() {
        return tv_vehicle_times1;
    }

    public void setTv_vehicle_times1(String tv_vehicle_times1) {
        this.tv_vehicle_times1 = tv_vehicle_times1;
    }

    public String getTv_vehicle_times2() {
        return tv_vehicle_times2;
    }

    public void setTv_vehicle_times2(String tv_vehicle_times2) {
        this.tv_vehicle_times2 = tv_vehicle_times2;
    }

    public String getTv_vehicle_times3() {
        return tv_vehicle_times3;
    }

    public void setTv_vehicle_times3(String tv_vehicle_times3) {
        this.tv_vehicle_times3 = tv_vehicle_times3;
    }

    public String getTv_vehicle_times4() {
        return tv_vehicle_times4;
    }

    public void setTv_vehicle_times4(String tv_vehicle_times4) {
        this.tv_vehicle_times4 = tv_vehicle_times4;
    }

    public String getTv_vehicle_times5() {
        return tv_vehicle_times5;
    }

    public void setTv_vehicle_times5(String tv_vehicle_times5) {
        this.tv_vehicle_times5 = tv_vehicle_times5;
    }

    public String getTv_vehicle_times6() {
        return tv_vehicle_times6;
    }

    public void setTv_vehicle_times6(String tv_vehicle_times6) {
        this.tv_vehicle_times6 = tv_vehicle_times6;
    }

    public String getTv_vehicle_times7() {
        return tv_vehicle_times7;
    }

    public void setTv_vehicle_times7(String tv_vehicle_times7) {
        this.tv_vehicle_times7 = tv_vehicle_times7;
    }

    public String getTv_vehicleNo() {
        return tv_vehicleNo;
    }

    public void setTv_vehicleNo(String tv_vehicleNo) {
        this.tv_vehicleNo = tv_vehicleNo;
    }


    public String getTv_vehicle_owner() {
        return tv_vehicle_owner;
    }

    public void setTv_vehicle_owner(String tv_vehicle_owner) {
        this.tv_vehicle_owner = tv_vehicle_owner;
    }

}
