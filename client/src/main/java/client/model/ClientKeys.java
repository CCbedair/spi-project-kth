package client.model;

import crypto.Oracle;
import crypto.SdhSignature;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.log4j.Logger;

import java.security.NoSuchAlgorithmException;


@Getter
@Setter
public class ClientKeys {
    private MutablePair<String, String> keyPair;
    private Oracle.Scheme algorithmScheme;
    private SdhSignature clientPublicKeySignature;
    static Logger logger = Logger.getLogger(ClientKeys.class);

    public ClientKeys(String HYBRID_SCHEME, GroupKeys groupKeys) throws NoSuchAlgorithmException {

        // If the scheme is valid
        // We create the key pair, according to the Scheme
        if (checkScheme(HYBRID_SCHEME)) {

                // We set the hybrid scheme algorithm: DSA, RSA, RSA2048, ECDSA
                setKeyPair(Oracle.getInstance().keyPairGen(Oracle.Scheme.valueOf(HYBRID_SCHEME)));
                //We set the Algorithm Scheme
                setAlgorithmScheme(Oracle.Scheme.valueOf(HYBRID_SCHEME));
                //We create the signature of the public key
                clientPublicKeySignature = Oracle.getInstance().sign(keyPair.getKey(),  groupKeys.getGroupPublicKey(), groupKeys.getGroupSecretKey());

        }

    }

    public static boolean checkScheme(String HYBRID_SCHEME) throws NoSuchAlgorithmException {
        long startTime = System.nanoTime();

        Oracle.Scheme[] allSchemes = Oracle.Scheme.values();
        for(Oracle.Scheme s : allSchemes){

            if(s.equals(HYBRID_SCHEME)){
                return true;
            }
        }
        logger.info(String.format("CHECKSCHEME_HY " + (System.nanoTime() - startTime)/1000000));
        throw new NoSuchAlgorithmException();
    }

}
