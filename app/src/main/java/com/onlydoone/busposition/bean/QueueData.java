package com.onlydoone.busposition.bean;

/**
 * Created by zhaohui on 2017/3/7.
 */

public class QueueData {
    private String queueName;
    private String QueueId;

    public QueueData() {
    }

    public QueueData(String queueName, String queueId) {
        this.queueName = queueName;
        QueueId = queueId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueId() {
        return QueueId;
    }

    public void setQueueId(String queueId) {
        QueueId = queueId;
    }
}
