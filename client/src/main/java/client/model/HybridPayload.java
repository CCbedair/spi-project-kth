package client.model;

import crypto.Oracle;
import crypto.SdhSignature;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class HybridPayload {
    private String message;
    private String messageSignature;
    private String clientGeneratedPublicKey;
    private SdhSignature clientGeneratedPublicKeySignature;
    private Oracle.Scheme scheme;

    public HybridPayload(String message, SdhSignature clientGeneratedPublicKeySignature,
                         String messageSignature, String clientGeneratedPublicKey,
                         Oracle.Scheme scheme) {
        this.message = message;
        this.messageSignature = messageSignature;
        this.clientGeneratedPublicKey = clientGeneratedPublicKey;
        this.clientGeneratedPublicKeySignature = clientGeneratedPublicKeySignature;
        this.scheme = scheme;
    }
}
