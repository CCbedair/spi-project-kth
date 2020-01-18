package com.groupmanager.springboot.util;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

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

    /*
    The most appropriate way to verify signature can be read here:
    https://www.openssl.org/docs/man1.1.1/man1/openssl-verify.html
    Look at the "VERIFY OPERATION" section
     */
    public static boolean verifyCertificate(X509Certificate testedCertPem, X509Certificate directIssuerPem)
            throws CertificateException, NoSuchProviderException,
            NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // Checks that the certificate is currently valid:
        // if the current date and time are within the validity period given in the certificate.
        testedCertPem.checkValidity();

        // Verifies that this certificate was signed using the private key that corresponds to the specified public key
        testedCertPem.verify(directIssuerPem.getPublicKey());
        return testedCertPem.getIssuerX500Principal().equals(directIssuerPem.getSubjectX500Principal());
    }

    public static X509Certificate pemStrToX509Cert(String certPem) throws CertificateException {
        String x509B64 = certPem.replaceAll("-----BEGIN CERTIFICATE-----", "")
                .replaceAll("-----END CERTIFICATE-----", "");

        X509Certificate cert = (X509Certificate) CertificateFactory
                .getInstance("X.509").generateCertificate(
                        new ByteArrayInputStream(org.apache.commons.codec.binary.Base64.decodeBase64(x509B64)));
        return cert;
    }
}
