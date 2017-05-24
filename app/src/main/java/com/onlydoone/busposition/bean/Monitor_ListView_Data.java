package com.onlydoone.busposition.bean;

/**
 * Created by Administrator on 2017/1/1 0001.
 */

public class Monitor_ListView_Data {

    private String tv_data;
    private String tv_number;
    private String tv_number_max;

    public Monitor_ListView_Data() {

    }

    public Monitor_ListView_Data(String tv_data) {
        this.tv_data = tv_data;
    }

    public Monitor_ListView_Data(String tv_data, String tv_number, String tv_number_max) {
        this.tv_data = tv_data;
        this.tv_number = tv_number;
        this.tv_number_max = tv_number_max;
    }

    public String getTv_data() {
        return tv_data;
    }

    public void setTv_data(String tv_data) {
        this.tv_data = tv_data;
    }

    public String getTv_number() {
        return tv_number;
    }

    public void setTv_number(String tv_number) {
        this.tv_number = tv_number;
    }

    public String getTv_number_max() {
        return tv_number_max;
    }

    public void setTv_number_max(String tv_number_max) {
        this.tv_number_max = tv_number_max;
    }

}
