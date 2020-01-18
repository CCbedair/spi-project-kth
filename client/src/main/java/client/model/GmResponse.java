package client.model;

import crypto.GroupPublicKey;
import crypto.GroupSecretKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmResponse {
    private long timestamp;
    private GroupPublicKey gpk;
    private GroupSecretKey gsk;
    private String x509;
    private String messageSignature;
}
