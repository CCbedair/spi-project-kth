package com.groupmanager.springboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupmanager.springboot.SingletonKeyHandler;
import com.groupmanager.springboot.pojo.ErrorResponse;
import com.groupmanager.springboot.pojo.GetKeysResponse;
import com.groupmanager.springboot.util.SSLUtils;
import crypto.Oracle;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@RestController
public class KeysController {

    /**
     * GET /keys
     *
     * This controller handler they keys endpoint.
     * This endpoint is meant to be used by DC.
     *
     * Request:
     *
     * Response:
     *
     *  // TODO: Add example response
     * 200 OK
     * {
     *     timestamp: <long>,
     *     gpk: <Crypto GPK>,
     *     x509: <String>,
     *     messageSignature: <String>
     * }
     *
     * 500 Internal Server Error
     * {"message": "Bad Key Spec/Algo"}
     * {"message": "Invalid Key/Signature"}
     * {"message": "Failed to read file!"}
     *
     *
     * @return Serialized Object According to Response Code
     */
    @GetMapping(path = "/keys")
    public ResponseEntity keys() {
        Object response;
        GetKeysResponse res = new GetKeysResponse();
        int statusCode = 200;

        try {
            res.setTimestamp(new Date().getTime());
            res.setGpk(SingletonKeyHandler.getInstance().getKeys().getGpk());
            res.setX509(SingletonKeyHandler.getInstance().getX509());
            ObjectMapper objectMapper = new ObjectMapper();
            String resStr = objectMapper.writeValueAsString(res);
            String resSignature = SSLUtils.signMessageEC(resStr,
                    SingletonKeyHandler.getInstance().getPrivateKeyPem());
            res.setMessageSignature(resSignature);
            response = res;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // TODO: Stack Trace in Logs...or ignore
            e.printStackTrace();
            statusCode = 500;
            ErrorResponse err = new ErrorResponse("Bad Key Spec/Algo");
            response = err;
        } catch (InvalidKeyException | SignatureException e) {
            statusCode = 500;
            ErrorResponse err = new ErrorResponse("Invalid Key/Signature");
            e.printStackTrace();
            response = err;
        } catch (IOException e) {
            statusCode = 500;
            ErrorResponse err = new ErrorResponse("Failed to read file!");
            e.printStackTrace();
            response = err;
        }
        catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            ErrorResponse err = new ErrorResponse(e.getMessage());
            response = err;
        }
        return ResponseEntity.status(statusCode).body(response);
    }
}
