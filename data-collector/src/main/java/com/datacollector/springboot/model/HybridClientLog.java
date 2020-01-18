package com.datacollector.springboot.model;

import crypto.Oracle;
import crypto.SdhSignature;

public class HybridClientLog {
    private String message;
    private SdhSignature clientGeneratedPublicKeySignature;
    private String messageSignature;
    private String clientGeneratedPublicKey;
    private Oracle.Scheme scheme;

    public HybridClientLog() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SdhSignature getClientGeneratedPublicKeySignature() {
        return clientGeneratedPublicKeySignature;
    }

    public void setClientGeneratedPublicKeySignature(SdhSignature clientGeneratedPublicKeySignature) {
        this.clientGeneratedPublicKeySignature = clientGeneratedPublicKeySignature;
    }

    public String getMessageSignature() {
        return messageSignature;
    }

    public void setMessageSignature(String messageSignature) {
        this.messageSignature = messageSignature;
    }

    public String getClientGeneratedPublicKey() {
        return clientGeneratedPublicKey;
    }

    public void setClientGeneratedPublicKey(String clientGeneratedPublicKey) {
        this.clientGeneratedPublicKey = clientGeneratedPublicKey;
    }

    public Oracle.Scheme getScheme() {
        return scheme;
    }

    public void setScheme(Oracle.Scheme scheme) {
        this.scheme = scheme;
    }
}
