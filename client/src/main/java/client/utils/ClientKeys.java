package client.utils;

import crypto.GroupPublicKey;
import crypto.GroupSecretKey;
import crypto.Oracle;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Setter
@Getter
public class ClientKeys {
    private static volatile ClientKeys instance;
    private static GroupPublicKey groupPublicKey = null;
    private static GroupSecretKey groupSecretKey = null;
    private static MutablePair<String, String> keyPair;
    private static String clientPublicKey;
    private static String clientPrivateKey;
    private static String privateKeyPem = "";
    private static String x509Pem = "";

    private ClientKeys() {}
    public static ClientKeys getInstance() throws Exception {
        if (instance == null) {
            synchronized (Oracle.class) {
                if (instance == null) {
                    instance = new ClientKeys();
                    keyPair = Oracle.getInstance().keyPairGen(Oracle.Scheme.EC);
                    clientPublicKey = keyPair.getKey();
                    clientPrivateKey = keyPair.getValue();
                }
            }
        }
        return instance;
    }

    public String getPrivateKeyPem() throws Exception {
        if (privateKeyPem == "") {
            throw new Exception("set private key from file first!");
        }
        return privateKeyPem;
    }

    public String getX509Pem() throws Exception {
        if (x509Pem == "") {
            throw new Exception("set the x509 file first");
        }
        return x509Pem;
    }

    public void setX509Pem(File x509PemFile) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(x509PemFile);
        byte[] certFileVal = new byte[(int) x509PemFile.length()];
        fileInputStream.read(certFileVal);
        fileInputStream.close();
        this.x509Pem = new String(certFileVal, "UTF-8");
    }

    public void setPrivateKeyPem(File privateKeyFile) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(privateKeyFile);
        byte[] privateKeyVal = new byte[(int) privateKeyFile.length()];
        fileInputStream.read(privateKeyVal);
        fileInputStream.close();
        this.privateKeyPem = new String(privateKeyVal, "UTF-8");
    }

    public MutablePair<String, String> getKeyPair() {
        return keyPair;
    }

    public String getClientPublicKey() {
        return clientPublicKey;
    }

    public String getClientPrivateKey() {
        return clientPrivateKey;
    }

    public GroupPublicKey getGroupPublicKey() throws Exception {
        if (groupPublicKey == null) {
            throw new Exception("Register to GM first");
        }
        return groupPublicKey;
    }

    public GroupSecretKey getGroupSecretKey() throws Exception {
        if (groupSecretKey == null) {
            throw new Exception("Register to GM first");
        }
        return groupSecretKey;
    }

    public void setGroupPublicKey(GroupPublicKey gpk) {
        this.groupPublicKey = gpk;
    }

    public void setGroupSecretKey(GroupSecretKey gsk) {
        this.groupSecretKey = gsk;
    }
}
