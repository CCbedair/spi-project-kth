package com.datacollector.springboot.service;

import com.datacollector.springboot.controller.ClientLogController;
import com.datacollector.springboot.model.GMresponse;
import com.datacollector.springboot.util.SSLUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import crypto.GroupPublicKey;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@Component
public class GroupManagerService {
    private GroupPublicKey groupPublicKey = null;

    static Logger logger = Logger.getLogger(ClientLogController.class);

    public GroupManagerService() {}

    public GroupPublicKey getGroupPublicKey() {
        if (groupPublicKey == null) {
            RestTemplate restTemplate = new RestTemplate();
            String gmURL = System.getenv("GM_URL");

            GMresponse gmResponse = restTemplate.getForObject(gmURL + "/keys", GMresponse.class);

            // verify the incoming x509 is not needed since it's already done in the TLS layer
            ObjectMapper objectMapper = new ObjectMapper();
            String respSignature = gmResponse.getMessageSignature();
            gmResponse.setMessageSignature(null);
            String respStr = null;
            try {
                respStr = objectMapper.writeValueAsString(gmResponse);
                if (SSLUtils.verifySignatureEC(respStr, respSignature, gmResponse.getX509())) {
                    System.out.println("Signature is verified");
                    groupPublicKey = gmResponse.getGpk();
                } else {
                    System.err.println("Signature is NOT verified");
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                System.err.println("Json parsing exception: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Verification exception: " + e.getMessage());
            }

            // for debugging purpose only
            try {
                logger.debug(objectMapper.writeValueAsString(gmResponse));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return groupPublicKey;
    }
}
