package com.groupmanager.springboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupmanager.springboot.SingletonKeyHandler;
import com.groupmanager.springboot.pojo.ErrorResponse;
import com.groupmanager.springboot.pojo.PostRegisterRequest;
import com.groupmanager.springboot.pojo.PostRegisterResponse;
import com.groupmanager.springboot.util.SSLUtils;
import crypto.Oracle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Date;
import org.apache.log4j.Logger;

//Controller
@Slf4j
@RestController
public class RegisterController {

    /**
     * POST /register
     *
     * This controller handler they register endpoint.
     * This endpoint is meant to be used by Clients.
     *
     * Request:
     *
     * // TODO: Add example request
     * {
     *     timestamp: <long>,
     *     x509: <JSON>
     * }
     *
     * Response:
     *
     *  // TODO: Add example response
     * 200 OK
     * {
     *     timestamp: <long>,
     *     gsk: <Crypto GSK>,
     *     gpk: <Crypto GPK>,
     *     x509: <String>
     * }
     *
     * 403 Forbidden
     * {"message": "Client Already Registered"}
     *
     * 404 Not Found
     * {"message": "Client Limit Reached. Resource No longer available."}
     *
     * 500 Internal Server Error
     * {"message": "Bad Key Spec/Algo"}
     * {"message": "Invalid Key/Signature"}
     * {"message": "Failed to read file!"}
     *
     * @return Serialized Object According to Response Code
     */

    static Logger logger = Logger.getLogger(RegisterController.class);


    @PostMapping(
            path = "/register",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity register(@RequestBody PostRegisterRequest req) {
        Object response;
        int statusCode = 200;
        // TODO: Verify x509, add user to Database
        try {
            X509Certificate clientCert = SSLUtils.pemStrToX509Cert(req.getX509());
            ObjectMapper objectMapper = new ObjectMapper();
            String reqSignature = req.getMessageSignature();
            req.setMessageSignature(null);
            String reqStr = objectMapper.writeValueAsString(req);
            boolean verify = SSLUtils.verifySignatureEC(reqStr, reqSignature, req.getX509());

            if (verify && SSLUtils
                    .verifyCertificate(clientCert, SingletonKeyHandler.getInstance().getIntermediateX509())) {
                log.info("Request signature is VERIFIED and x509 is verified");
                PostRegisterResponse res = new PostRegisterResponse();
                String x509 = SingletonKeyHandler.getInstance().getX509();
                res.setTimestamp(new Date().getTime());
                res.setGpk(SingletonKeyHandler.getInstance().getKeys().getGpk());
                res.setX509(x509);

                res.setGsk(SingletonKeyHandler.getInstance().getNextGsk());
                String respStr = objectMapper.writeValueAsString(res);
                String respSignature = SSLUtils.signMessageEC(respStr,
                        SingletonKeyHandler.getInstance().getPrivateKeyPem());
                res.setMessageSignature(respSignature);

                response = res;
            } else {
                log.error("Request signature is NOT verified or x509 is NOT verified");
                statusCode = 403;
                ErrorResponse errorResponse = new ErrorResponse("Request signature is NOT verified");
                response = errorResponse;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            statusCode = 404;
            ErrorResponse err = new ErrorResponse("Client Limit Reached. Resource No longer available.");
            response = err;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            statusCode = 500;
            ErrorResponse err = new ErrorResponse("Bad Key Spec/Algo");
            response = err;
        } catch (InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            statusCode = 500;
            ErrorResponse err = new ErrorResponse("Invalid Key/Signature");
            response = err;
        } catch (IOException e) {
            e.printStackTrace();
            statusCode = 500;
            ErrorResponse err = new ErrorResponse("Failed to read file!");
            log.error(e.getMessage());
            response = err;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            statusCode = 500;
            ErrorResponse err = new ErrorResponse(e.getMessage());
            response = err;
        }

        return ResponseEntity.status(statusCode).body(response);
    }


}


