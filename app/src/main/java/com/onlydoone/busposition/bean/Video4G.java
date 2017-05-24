package com.onlydoone.busposition.bean;

/**
 * Created by zhaohui on 2017/2/22.
 */

public class Video4G {
    private String ip = "47.93.114.174",userName = "admin",passWord = "admin";
    private String keyid = "074938225179";//手机号码13位
    int num1 = 0;
    int num2 = 1;
    int num3 = 2;
    int num4 = 3;
    int port = 9015;

    public Video4G() {
    }

    public Video4G(String keyid) {
        this.keyid = keyid;
    }

    public Video4G(String ip, String userName, String passWord, String keyid, int num1, int num2, int num3, int num4, int port) {
        this.ip = ip;
        this.userName = userName;
        this.passWord = passWord;
        this.keyid = keyid;
        this.num1 = num1;
        this.num2 = num2;
        this.num3 = num3;
        this.num4 = num4;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getNum4() {
        return num4;
    }

    public void setNum4(int num4) {
        this.num4 = num4;
    }

    public int getNum3() {
        return num3;
    }

    public void setNum3(int num3) {
        this.num3 = num3;
    }

    public int getNum2() {
        return num2;
    }

    public void setNum2(int num2) {
        this.num2 = num2;
    }

    public int getNum1() {
        return num1;
    }

    public void setNum1(int num1) {
        this.num1 = num1;
    }

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
