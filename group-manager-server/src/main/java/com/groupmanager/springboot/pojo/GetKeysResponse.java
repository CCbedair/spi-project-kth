package com.groupmanager.springboot.pojo;

import crypto.GroupPublicKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class GetKeysResponse {
    private long timestamp;
    private GroupPublicKey gpk;
    private String x509;
    private String messageSignature = null;
}

