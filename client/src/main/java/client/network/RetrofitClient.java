package client.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.HostnameVerifier;

public class RetrofitClient {
    private static Retrofit gmRetrofit = null;
    private static Retrofit dataCollServerRetrofit = null;
    // We get the GM Server from the environment variable
    private static final String gmBaseUrl = System.getenv("GM_URL");
    // We get the Data Collection Server from the environment variable
    private static final String dataCollServerBaseUrl = System.getenv("DC_URL");

    public enum Endpoint {
        GM,
        DATA_COLLECTION_SERVER
    }

    public static Retrofit getClient(Endpoint endpoint) {
        switch (endpoint) {
            case GM:
                if (gmRetrofit == null) {
                    // why return true?
                    // the CN should be in the subjectaltname,
                    // in the CSR, it is :Requested Extensions.X509v3 Subject Alternative Name
                    // BUT, bug: https://www.openssl.org/docs/man1.1.1/man1/x509.html#BUGS
                    HostnameVerifier hostnameVerifier = (hostname, session) -> {
                        return true;
                    };

                    OkHttpClient client = new OkHttpClient
                            .Builder()
                            .hostnameVerifier(hostnameVerifier).build();

                    gmRetrofit = new Retrofit
                            .Builder()
                            .baseUrl(gmBaseUrl)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }

                return gmRetrofit;
            case DATA_COLLECTION_SERVER:
                if (dataCollServerRetrofit == null) {
                    HostnameVerifier hostnameVerifier = (hostname, session) -> {
                        return true;
                    };

                    OkHttpClient client = new OkHttpClient
                            .Builder()
                            .hostnameVerifier(hostnameVerifier).build();

                    dataCollServerRetrofit = new Retrofit
                            .Builder()
                            .baseUrl(dataCollServerBaseUrl)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }

                return dataCollServerRetrofit;
            default:
                return null;
        }
    }
}
