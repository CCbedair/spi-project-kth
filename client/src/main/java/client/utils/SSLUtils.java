package client.utils;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
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
        return Base64.getEncoder().encodeToString(signature.sign());
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
        return sig.verify(Base64.getDecoder().decode(signatureB64));
    }

    public static boolean verifyCertificate(X509Certificate testedCertPem, X509Certificate directIssuerPem)
            throws CertificateException, NoSuchProviderException,
            NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        testedCertPem.checkValidity();
        testedCertPem.verify(directIssuerPem.getPublicKey());
        return testedCertPem.getIssuerX500Principal().equals(directIssuerPem.getSubjectX500Principal());
    }

    public static X509Certificate pemStrToX509Cert(String certPem) throws CertificateException {
        String x509B64 = certPem.replaceAll("-----BEGIN CERTIFICATE-----", "")
                .replaceAll("-----END CERTIFICATE-----", "");

        X509Certificate cert = (X509Certificate) CertificateFactory
                .getInstance("X.509").generateCertificate(
                        new ByteArrayInputStream(Base64.getDecoder().decode(x509B64)));
        return cert;
    }
}
