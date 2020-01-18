package com.datacollector.springboot.util;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.IOException;
import java.io.StringReader;
import java.security.*;

public class SSLUtils {
    public static String signMessageEC(String message, String privateKeyPem) throws IOException,
            NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        PEMParser parser = new PEMParser(new StringReader(privateKeyPem));
        Object skObject = parser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(provider);
        PrivateKey sk = converter.getKeyPair((PEMKeyPair) skObject).getPrivate();
        Signature signature =  Signature.getInstance("SHA256withECDSA");
        signature.initSign(sk);
        signature.update(message.getBytes());
        return java.util.Base64.getEncoder().encodeToString(signature.sign());
    }

    public static boolean verifySignatureEC(String message, String signatureB64, String certX509Pem) throws IOException,
            NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Security.addProvider(new BouncyCastleProvider());
        PEMParser parser = new PEMParser(new StringReader(certX509Pem));
        X509CertificateHolder h = (X509CertificateHolder) parser.readObject();
        PublicKey pk = new JcaPEMKeyConverter().getPublicKey(h.getSubjectPublicKeyInfo());
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(pk);
        sig.update(message.getBytes());
        return sig.verify(java.util.Base64.getDecoder().decode(signatureB64));
    }
}
