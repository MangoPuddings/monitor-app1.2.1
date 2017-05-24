package com.onlydoone.busposition.Utils.versionUtil;

import java.io.Serializable;

public class VersionInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String versionNameClient;//客户端版本名
    private int versionCodeClient;//客户端版本号
    private String versionNameServer;//服务端版本名
    private int versionCodeServer;//服务端版本号
    private String versionDesc;//版本描述信息内容
    private String downloadUrl;//新版本的下载路径
    private String versionSize;//版本大小

    public String getVersionNameClient() {
        return versionNameClient;
    }

    public void setVersionNameClient(String versionNameClient) {
        this.versionNameClient = versionNameClient;
    }

    public int getVersionCodeClient() {
        return versionCodeClient;
    }

    public void setVersionCodeClient(int versionCodeClient) {
        this.versionCodeClient = versionCodeClient;
    }

    public String getVersionNameServer() {
        return versionNameServer;
    }

    public void setVersionNameServer(String versionNameServer) {
        this.versionNameServer = versionNameServer;
    }

    public int getVersionCodeServer() {
        return versionCodeServer;
    }

    public void setVersionCodeServer(int versionCodeServer) {
        this.versionCodeServer = versionCodeServer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersionSize() {
        return versionSize;
    }

    public void setVersionSize(String versionSize) {
        this.versionSize = versionSize;
    }


    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

}
