package crypto;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.NoSuchElementException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import javax.crypto.SecretKey;

/**
 * Oracle class is a singleton class that provide cryptographic functions.
 * GM, Client, and Data Collection Server need to use this class to perform various cryptographic functions
 * such as generating keys, signing message, verifying message, (and reveal the signer for GM)
 *
 * @author secclo team
 */
public class Oracle {
    static {
        // "oracle": the name of the shared library (liboracle.so)
        System.loadLibrary("oracle");
    }

    private static volatile Oracle oracleInstance;

// TODO: Write Javadocs
    public enum Scheme
    {
        DSA("DSA"),
        RSA("RSA"),
        RSA2048("RSA2048"),
        EC("EC");

        private String scheme;

        Scheme(String scheme) {
            this.scheme = scheme;
        }

        public String getScheme() {
            return scheme;
        }
    }

    private Oracle() {
        if (oracleInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    /**
     * Call getInstance() to get the instance of the Oracle class
     * 
     * Example:
     * Oracle oracle = Oracle.getInstance();
     *
     * @return the singleton object of Oracle
     */
    public static Oracle getInstance() {
        if (oracleInstance == null) {
            synchronized (Oracle.class) {
                if (oracleInstance == null) {
                    oracleInstance = new Oracle();
                }
            }
        }

        return oracleInstance;
    }

    /**
     * Sign the message with Short Group Signature Scheme by Boneh et al
     *
     * Example:
     * SdhSignature signature = oracle.sign(message, gpk, gsk);
     *
     * @param message String message to be signed
     * @param groupPublicKey Group public key, obtained from GM
     * @param groupSecretKey Group secret key, obtained from GM
     * @return SdhSignature object
     */
    public native SdhSignature sign(String message, GroupPublicKey groupPublicKey,
                                           GroupSecretKey groupSecretKey);

    // TODO: Write javadocs
    public  SdhSignature sign(PublicKey pk, GroupPublicKey groupPublicKey,
                                    GroupSecretKey groupSecretKey) {
        String message =  Base64.getEncoder().encodeToString(pk.getEncoded());
        return sign(message, groupPublicKey, groupSecretKey);
    }

    // TODO: Write javadocs
    public String sign(String message, String b64SK, Scheme scheme) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        Signature sig;
        String sig_mes;
        switch (scheme) {
            case RSA:
            case RSA2048:
                sig = Signature.getInstance("SHA256withRSA");
                break;
            case EC:
                sig = Signature.getInstance("SHA256withECDSA");
                break;
            case DSA:
                sig = Signature.getInstance("SHA256withDSA");
                break;
            default:
                throw new NoSuchElementException();
        }
        PrivateKey sk = KeyFactory.getInstance(scheme.getScheme()).generatePrivate(new PKCS8EncodedKeySpec(
                Base64.getDecoder().decode(b64SK)
        ));
        sig.initSign(sk);
        sig.update(message.getBytes());
        sig_mes = Base64.getEncoder().encodeToString(sig.sign());
        return sig_mes;
    }

    /**
     * Verify the integrity of message received.
     *
     * Example:
     * int verify = oracle.verify(message, gpk, signature);
     *
     * @param message String message received
     * @param groupPublicKey Group public key, obtained from GM
     * @param sig SdhSignature received
     * @return 0 for verified, -1 for NOT verified
     */
    public native int verify(String message, GroupPublicKey groupPublicKey, SdhSignature sig);

    public int verify(String message, String signed_mes, GroupPublicKey groupPublicKey, String b64PK,
                      SdhSignature sigPK, Scheme scheme) throws
            NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        int res;
        if (verify(b64PK, groupPublicKey, sigPK) == 0) {
            Signature sig;
            switch (scheme) {
                case RSA:
                case RSA2048:
                    sig = Signature.getInstance("SHA256withRSA");
                    break;
                case EC:
                    sig = Signature.getInstance("SHA256withECDSA");
                    break;
                case DSA:
                    sig = Signature.getInstance("SHA256withDSA");
                    break;
                default:
                    throw new NoSuchElementException();
            }
            PublicKey pk = KeyFactory.getInstance(scheme.getScheme()).generatePublic(new X509EncodedKeySpec(
                    Base64.getDecoder().decode(b64PK)
            ));
            sig.initVerify(pk);
            sig.update(message.getBytes());
            res = sig.verify(Base64.getDecoder().decode(signed_mes)) ? 0 : -1;
        } else {
            res = -1;
        }
        return res;
    }

    private native EcPoint_Fp _open(GroupMasterSecretKey groupMasterSecretKey, SdhSignature sig);

    /**
     * keyGenInit() generates group secret, public, and master keys. Only GM should call this function.
     *
     * Example: 
     * Keys keys = oracle.keyGenInit(12);
     *
     * @param numberOfMembers the number of pre-defined clients = number of group secret keys
     * @return Keys object with group secret, public, and master keys inside
     */
    public native Keys keyGenInit(int numberOfMembers);

    // TODO: Write javadocs
    public MutablePair<String, String> keyPairGen(Scheme scheme) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator;
        KeyPair keyPair;
        keyPairGenerator = KeyPairGenerator.getInstance(String.valueOf(scheme));
        switch (scheme) {
            case EC:
                keyPairGenerator.initialize(256, new SecureRandom());
                break;
            case DSA:
            case RSA:
                keyPairGenerator.initialize(1024, new SecureRandom());
                break;
            case RSA2048:
                keyPairGenerator.initialize(2048, new SecureRandom());
            break;
        }
        keyPair = keyPairGenerator.generateKeyPair();

        return new MutablePair<>(
                Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()),
                Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded())
        );
    }

    public static void printKeys(Keys keys) {
        System.out.println("gpk: " + DigestUtils.sha1Hex(
            SerializationUtils.serialize(keys.getGpk())));

        try {
            int i = 1;
            for (GroupSecretKey gsk : keys.getGsk()) {
                System.out.println("gsk " + i++ + ": " + DigestUtils.sha1Hex(
                        SerializationUtils.serialize(gsk)));
            }
        } catch (Exception e) {

        }
    }

    // public int open(GroupMasterSecretKey groupMasterSecretKey, GroupSecretKey gsks[],
    //                        SdhSignature sig) {
    //     int index = -1;
    //     for (int i = 0; i < gsks.length; i++) {
    //         EcPoint_Fp A = _open(groupMasterSecretKey, sig);
    //         if (Objects.deepEquals(A.getX(), gsks[i].getA().getX())
    //                 && Objects.deepEquals(A.getY(), gsks[i].getA().getY())) {
    //             index = i;
    //             break;
    //         }
    //     }
    //     return index;
    // }
}
