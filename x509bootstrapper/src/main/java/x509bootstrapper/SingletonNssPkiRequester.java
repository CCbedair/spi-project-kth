package x509bootstrapper;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import x509bootstrapper.models.Interfaces;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class SingletonNssPkiRequester {
    private static Random rand;
    private static XmlRpcClient ltcaClient;
    private static SingletonNssPkiRequester instance;
    private static String x509Pem = "";
    private static String caCertPem = "";
    private static String intermediateCertPem = "";
    private static String csr;

    private SingletonNssPkiRequester() {
        rand = new Random();
        ltcaClient = new XmlRpcClient();
    }

    public static SingletonNssPkiRequester getInstance() throws IOException {
        if (instance == null) {
            instance = new SingletonNssPkiRequester();
            XmlRpcClientConfigImpl xmlConfig = new XmlRpcClientConfigImpl();
            xmlConfig.setServerURL(new URL("https://ltca.vpki-nss-kth.se/cgi-bin/ltca"));
            ltcaClient.setConfig(xmlConfig);
        }

        return instance;
    }

    public String getX509Pem() throws Exception {
        if (x509Pem == "") {
            requestCert();
        }
        return x509Pem;
    }

    public String getCaCertPem() throws Exception {
        if (x509Pem == "") {
            requestCert();
        }
        return caCertPem;
    }

    public String getIntermediateCertPem() throws Exception {
        if (intermediateCertPem == "") {
            requestCert();
        }
        return intermediateCertPem;
    }

    private static String getVoucher() throws XmlRpcException, InvalidProtocolBufferException {
        Instant instant = Instant.now();
        long timeStampMillis = instant.toEpochMilli();

        Interfaces.msgVoucherReq_V2LTCA.Builder voucherReq = Interfaces.msgVoucherReq_V2LTCA.newBuilder();

        voucherReq.setIReqType(120);
        voucherReq.setStrUserName("");
        voucherReq.setStrPwd("");
        voucherReq.setStrEmailAddress("base64_lemper@kth.se");
        voucherReq.setStrCaptcha("captcha");
        voucherReq.setINonce(rand.nextInt());
        voucherReq.setTTimeStamp(timeStampMillis);

        String encodedReq = Base64.getEncoder().encodeToString(voucherReq.build().toByteArray());

        List<Object> register_params = new ArrayList<>();
        register_params.add(120);
        register_params.add(encodedReq);

        String response = (String) ltcaClient.execute("ltca.operate", register_params);

        Interfaces.msgVoucherRes_LTCA2V resVoucher = Interfaces.msgVoucherRes_LTCA2V.parseFrom(
                Base64.getDecoder().decode(response));
        return resVoucher.getStrVoucher();
    }

    private static void requestCert() throws Exception {
        Instant instant = Instant.now();
        long timeStampMillis = instant.toEpochMilli();
        Interfaces.msgX509CertReq_V2LTCA.Builder certReq = Interfaces.msgX509CertReq_V2LTCA.newBuilder();

        certReq.setIReqType(122);
        certReq.setINonce(rand.nextInt());
        certReq.setILTCAIdRange(1002);
        certReq.setTTimeStamp(timeStampMillis);
        certReq.setStrProofOfPossessionVoucher(getVoucher());
        certReq.setStrX509CertReq(getCSR(new File("cert_request.csr")));
        certReq.setStrDNSExtension("");

        String encodedReq = Base64.getEncoder().encodeToString(certReq.build().toByteArray());

        List<Object> register_params = new ArrayList<>();
        register_params.add(122);
        register_params.add(encodedReq);

        String response = (String) ltcaClient.execute("ltca.operate", register_params);

        Interfaces.msgX509CertRes_LTCA2V resCert = Interfaces.msgX509CertRes_LTCA2V.parseFrom(
                Base64.getDecoder().decode(response));

        caCertPem = resCert.getStSigner().getStrCertificatesChain();
        intermediateCertPem = resCert.getStSigner().getStrCertificate();
        x509Pem = resCert.getStrX509Cert();
    }

    private static String getCSR(File csrFile) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(csrFile);
        byte[] csrFileVal = new byte[(int) csrFile.length()];
        fileInputStream.read(csrFileVal);
        fileInputStream.close();
        return new String(csrFileVal, "UTF-8");
    }
}
