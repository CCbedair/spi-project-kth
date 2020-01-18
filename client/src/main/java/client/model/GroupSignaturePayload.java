package client.model;
import crypto.SdhSignature;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupSignaturePayload {
    private long timestamp;
    private int nonce;
    private String message;
    private SdhSignature clientSignature;

    public GroupSignaturePayload(long timestamp, int nonce, String message, SdhSignature clientSignature){
        this.setClientSignature(clientSignature);
        this.setMessage(message);
        this.setNonce(nonce);
        this.setTimestamp(timestamp);
    }
}
