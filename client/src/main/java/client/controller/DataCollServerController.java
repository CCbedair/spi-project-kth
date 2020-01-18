package client.controller;

import client.model.DcsResponse;
import client.model.GroupSignaturePayload;
import client.model.HybridPayload;
import client.network.DataCollectionServerInterface;
import client.network.RetrofitClient;
import crypto.GroupPublicKey;
import crypto.GroupSecretKey;
import crypto.Oracle;
import crypto.SdhSignature;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.time.Instant;
import java.util.Random;

@NoArgsConstructor
public class DataCollServerController {
    long startTime;

    static Logger logger = Logger.getLogger(DataCollServerController.class);

    public void sendDataGroupScheme(String message, GroupPublicKey gpk, GroupSecretKey gsk) throws Exception {
        // get timestamp
        // sign the message
        // create GroupSignaturePayload instance and put that generated timestamp

        Random rand = new Random();
        int nonce = rand.nextInt();
        Instant instant = Instant.now();
        long timeStampMillis = instant.toEpochMilli();

        Oracle oracle = Oracle.getInstance();

        //We log the signining of the message
        startTime = System.nanoTime();
        SdhSignature clientSignature = oracle.sign(message, gpk, gsk);
        logger.info(String.format("SIGN_GS " + (System.nanoTime() - startTime)/1000000));

        // We create GroupSignaturePayload instance and put that generated timestamp
        GroupSignaturePayload payload = new GroupSignaturePayload(timeStampMillis, nonce, message,
                clientSignature);

        Retrofit dcsRetrofit = RetrofitClient.getClient(RetrofitClient.Endpoint.DATA_COLLECTION_SERVER);
        DataCollectionServerInterface dcsInterface = dcsRetrofit.create(DataCollectionServerInterface.class);

        // We prepare the data to send
        Call<DcsResponse> c = dcsInterface.sendData(payload);
        // We execute the request and receive the response
        Response<DcsResponse> r = c.execute();

        if (r.code() != 200) {
            String err = "http " + r.code() + ": " + r.body().status;
            throw new Exception(err);
        }

        System.out.println(r.body().status);
    }

    /**
     * This function sends a message with the Hybrid Pseudonym Scheme to the receiver.
     * When the receiver returns HTTP status other than 200, it will throw an Exception
     * with the message obtained from that receiver.
     * @param message the message being sent to other parties
     * @param gpk group public key
     * @param gsk group secret key
     * @param keyPair this entity self-generated keys: <public, private>
     * @throws Exception
     */
    public void sendDataHybridScheme(String message, GroupPublicKey gpk, GroupSecretKey gsk,
                                     Pair<String, String> keyPair) throws Exception {
        long startTime = System.nanoTime();
        String msgSignature = Oracle.getInstance().sign(message, keyPair.getValue(), Oracle.Scheme.EC);
        logger.info("SIGN_HY " + (System.nanoTime() - startTime) / 1000000);
        SdhSignature clientPublicKeySignature = Oracle.getInstance().sign(keyPair.getKey(),
                gpk, gsk);

        HybridPayload hybridPayload = new HybridPayload(message, clientPublicKeySignature, msgSignature,
                keyPair.getKey(), Oracle.Scheme.EC);

        Retrofit dcsRetrofit = RetrofitClient.getClient(RetrofitClient.Endpoint.DATA_COLLECTION_SERVER);
        DataCollectionServerInterface dcsInterface = dcsRetrofit.create(DataCollectionServerInterface.class);
        Call<DcsResponse> client = dcsInterface.sendData(hybridPayload);
        Response<DcsResponse> response = client.execute();

        if (response.code() != 200) {
            throw new Exception("http " + response.code() + ": " + response.body().status);
        }
        System.out.println(response.body().status);
    }
}
