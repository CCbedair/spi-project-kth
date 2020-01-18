package com.datacollector.springboot.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import crypto.SdhSignature;

import java.util.Date;
import java.util.List;

public class ClientLog {


    private long timestamp;
    private int nonce;
    private String message;
    private SdhSignature clientSignature;

    public long getTimestamp() {
        return timestamp;
    }

    public ClientLog(){

    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setClientSignature(SdhSignature clientSignature) {
        this.clientSignature = clientSignature;
    }

    public void ClientLog(long timestamp, int nonce, String message, SdhSignature signature){
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonSignature = "{ \"signature\"}";

        try {
            clientSignature = objectMapper.readValue(jsonSignature, SdhSignature.class);
        } catch (Exception e) {

        }
        this.clientSignature = signature;
        this.message = message;
        this.nonce = nonce;
        this.timestamp = timestamp;
    }

    public String getMessage(){
        return message;
    }

    public SdhSignature getClientSignature(){
        return clientSignature;
    }

    @Override
    public String toString() {
        return String.format(
                "Client log [timestamp=%s, message=%s]",
                timestamp, message);
    }
}
