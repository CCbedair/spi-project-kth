package client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {
    private long timestamp;
    private String x509;
    private String messageSignature = null;

    public RegistrationRequest(long timestamp, String x509) {
        this.timestamp = timestamp;
        this.x509 = x509;
    }
}

