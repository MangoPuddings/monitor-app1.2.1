package com.onlydoone.busposition.bean;

/**
 * Created by zhaohui on 2017/3/15.
 */
public class PoliceTypeData {
    private String policeType;
    private String num;
    private int i;

    public PoliceTypeData() {
    }

    public PoliceTypeData(String policeType,int i,String num) {
        this.policeType = policeType;
        this.i = i;
        this.num = num;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getPoliceType() {
        return policeType;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }
}
