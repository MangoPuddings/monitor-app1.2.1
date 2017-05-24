package com.onlydoone.busposition.bean;

/**
 * Created by Administrator on 2016/12/22 0022.
 */

public class Search_Data {

    private String tv_data;
    private String simNo;

    public String getSimNo() {
        return simNo;
    }

    public void setSimNo(String simNo) {
        this.simNo = simNo;
    }

    public Search_Data(String tv_data, String simNo) {
        this.tv_data = tv_data;
        this.simNo = simNo;
    }

    public Search_Data(String tv_data){
        this.tv_data = tv_data;
    }

    public String getTv_data() {
        return tv_data;
    }

    public void setTv_data(String tv_data) {
        this.tv_data = tv_data;
    }
}
