package com.groupmanager.springboot.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PostRegisterRequest {
    private long timestamp;
    private String x509;
    private String messageSignature = null;
}
