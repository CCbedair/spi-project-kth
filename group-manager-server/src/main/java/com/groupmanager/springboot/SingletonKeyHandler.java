package com.groupmanager.springboot;

import com.groupmanager.springboot.util.SSLUtils;
import crypto.GroupSecretKey;
import crypto.Keys;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class SingletonKeyHandler {
    private static volatile SingletonKeyHandler instance;

    @Setter
    @Getter
    private Keys keys;
    private int currentGSKIndex;
    private String x509 = "";
    private String privateKeyPem = "";
    private String csrPem = "";
    private String intermediateCert = "";
    private X509Certificate intermediateX509 = null;

    private SingletonKeyHandler() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SingletonKeyHandler getInstance() {
        if (instance == null) {
            synchronized (SingletonKeyHandler.class) {
                if (instance == null) {
                    instance = new SingletonKeyHandler();
                }
            }
        }

        return instance;
    }

    public String getIntermediateCert() throws Exception {
        if (intermediateCert == "") {
            throw new Exception("set the intermediate cert first");
        }
        return intermediateCert;
    }

    public X509Certificate getIntermediateX509() throws Exception {
        if (intermediateX509 == null) {
            intermediateX509 = SSLUtils.pemStrToX509Cert(getIntermediateCert());
        }
        return intermediateX509;
    }

    public void setIntermediateCert(File intermediateCertFile) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(intermediateCertFile);
        byte[] intermediateVal = new byte[(int) intermediateCertFile.length()];
        fileInputStream.read(intermediateVal);
        this.intermediateCert = new String(intermediateVal, "UTF-8");
        fileInputStream.close();
    }


    public String getX509() throws Exception {
        if (x509 == "") {
            throw new Exception("set the X509 first");
        }
        return x509;
    }

    public void setX509(File x509PemFile) throws IOException {
        FileInputStream fileInputStream;
        fileInputStream = new FileInputStream(x509PemFile);
        byte[] x509Val = new byte[(int) x509PemFile.length()];
        fileInputStream.read(x509Val);
        this.x509 = new String(x509Val, "UTF-8");
        fileInputStream.close();
    }

    public String getPrivateKeyPem() throws Exception {
        if (privateKeyPem == "") {
            throw new Exception("set the private key file first");
        }
        return privateKeyPem;
    }

    public String getCsrPem() throws Exception {
        if (csrPem == "") {
            throw new Exception("set the csr file first");
        }
        return csrPem;
    }

    public void setPrivateKeyPem(File privateKeyPemFile) throws IOException {
        FileInputStream fileInputStream;
        fileInputStream = new FileInputStream(privateKeyPemFile);
        byte[] skVal = new byte[(int) privateKeyPemFile.length()];
        fileInputStream.read(skVal);
        privateKeyPem = new String(skVal, "UTF-8");
        fileInputStream.close();
    }

    public void setCsrPem(File csrPemFile) throws IOException {
        FileInputStream fileInputStream;
        fileInputStream = new FileInputStream(csrPemFile);
        byte[] csrVal = new byte[(int) csrPemFile.length()];
        fileInputStream.read(csrVal);
        csrPem = new String(csrVal, "UTF-8");
        fileInputStream.close();
    }

    public GroupSecretKey getNextGsk() {
        // TODO: Update index in database not file
        File file = new File("conf/last_index.txt");
        file.getParentFile().mkdirs();
        FileWriter writer = null;
        currentGSKIndex += 1;
        try {
            writer = new FileWriter(file);
            writer.write(currentGSKIndex);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keys.getGsk()[currentGSKIndex];
    }

    public PublicKey getPublicKey() throws IOException {
        PEMParser parser = new PEMParser(new StringReader(this.x509));
        X509CertificateHolder h = (X509CertificateHolder) parser.readObject();
        return new JcaPEMKeyConverter().getPublicKey(h.getSubjectPublicKeyInfo());
    }

    public String getB64SecretKey() throws IOException {
        PEMParser parser = new PEMParser(new StringReader(privateKeyPem));
        PrivateKey sk = new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) parser.readObject());
        return Base64.getEncoder().encodeToString(sk.getEncoded());
    }
}
