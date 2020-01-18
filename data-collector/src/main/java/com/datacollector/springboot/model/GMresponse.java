package com.datacollector.springboot.model;

import crypto.GroupPublicKey;
import org.json.JSONObject;

public class GMresponse{
    private long timestamp;
    private GroupPublicKey gpk;
    private String x509;
    private String messageSignature = null;

    public GMresponse() {}

    public GroupPublicKey getGpk() {
        return gpk;
    }

    public void setGpk(GroupPublicKey gpk) {
        this.gpk = gpk;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getX509() {
        return x509;
    }

    public void setX509(String x509) {
        this.x509 = x509;
    }

    public String getMessageSignature() {
        return messageSignature;
    }

    public void setMessageSignature(String messageSignature) {
        this.messageSignature = messageSignature;
    }
}
