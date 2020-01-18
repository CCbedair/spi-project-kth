package client.controller;

import client.model.GmResponse;
import client.model.RegistrationRequest;
import client.network.GmInterface;
import client.network.RetrofitClient;
import client.utils.ClientKeys;
import client.utils.SSLUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.time.Instant;

@NoArgsConstructor
public class GmController {
    static Logger logger = Logger.getLogger(GmController.class);

    public static class GmException extends Exception {
        public GmException(String message) {
            super(message);
        }
    }

    public GmResponse registerToGm(String x509) throws Exception {
        Retrofit gmRetrofit = RetrofitClient.getClient(RetrofitClient.Endpoint.GM);
        GmInterface gmInterface = gmRetrofit.create(GmInterface.class);
        RegistrationRequest registrationRequest = new RegistrationRequest(Instant.now().toEpochMilli(),
                x509);

        ObjectMapper objectMapper = new ObjectMapper();
        String regRequestStr = objectMapper.writeValueAsString(registrationRequest);
        String signature = SSLUtils.signMessageEC(regRequestStr,
                ClientKeys.getInstance().getPrivateKeyPem());
        registrationRequest.setMessageSignature(signature);

        long startTime = System.nanoTime();
        Call<GmResponse> k = gmInterface.registerToGm(registrationRequest);
        Response<GmResponse> keysResponse = k.execute();
        logger.info("REGISTER " + (System.nanoTime() - startTime) / 1000000);

        if (keysResponse.code() != 200) {
            throw new GmException("http " + keysResponse.code() + ": " + keysResponse.errorBody().string());
        }

        GmResponse gmResponse = keysResponse.body();
        String responseSignature = gmResponse.getMessageSignature();
        gmResponse.setMessageSignature(null);
        String respStr = objectMapper.writeValueAsString(gmResponse);

        // Verify x509 not needed, since it is already verified in the TLS layer
        boolean verify = SSLUtils.verifySignatureEC(respStr, responseSignature, gmResponse.getX509());
        if (verify) {
            System.out.println("Response signature verified");
        } else {
            System.err.println("Response signature NOT verified");
            throw new GmException("Response signature NOT verified");
        }

        return gmResponse;
    }

    public String getPublicKeys() throws IOException {
        Retrofit gmRetrofit = RetrofitClient.getClient(RetrofitClient.Endpoint.GM);
        GmInterface gmInterface = gmRetrofit.create(GmInterface.class);

        Call<Object> k = gmInterface.getPublicKey();
        Response<Object> keysResponse = k.execute();
        //System.out.println(keysResponse);
        String gmResponse = keysResponse.body().toString();//403 for non available keys
        if (keysResponse.code() != 200) {
            String err = "http " + keysResponse.code() + ": " + keysResponse.errorBody().string();
        }
        return gmResponse;
    }
}
