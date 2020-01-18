package x509bootstrapper;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class Bootstrapper {

    public static void main(String[] args) {
        try {
            trustAll();
            String myX509 = SingletonNssPkiRequester.getInstance().getX509Pem();
            String intermediateCert = SingletonNssPkiRequester.getInstance().getIntermediateCertPem();
            String caCert = SingletonNssPkiRequester.getInstance().getCaCertPem();
            System.out.println(myX509);
            System.out.println(intermediateCert);
            System.out.println(caCert);

            PrintWriter printWriter = new PrintWriter("myx509.pem");
            printWriter.print(myX509);
            printWriter.close();

            printWriter = new PrintWriter("intermediate.pem");
            printWriter.print(intermediateCert);
            printWriter.close();

            printWriter = new PrintWriter("root.pem");
            printWriter.print(caCert);
            printWriter.close();

            printWriter = new PrintWriter("all.pem");
            printWriter.print(myX509 + "\n" + intermediateCert + "\n" + caCert + "\n");
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Needed so that we can contact NSS-PKI
     */
    private static void trustAll() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
}
