package com.groupmanager.springboot.pojo;

import crypto.GroupPublicKey;
import crypto.GroupSecretKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PostRegisterResponse {
    private long timestamp;
    private GroupPublicKey gpk;
    private GroupSecretKey gsk;
    private String x509;
    private String messageSignature = null;
}
