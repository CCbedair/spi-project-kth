package client.network;

import client.model.DcsResponse;
import client.model.HybridPayload;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import client.model.GroupSignaturePayload;

public interface DataCollectionServerInterface {
    @POST("/locations/")
    Call<DcsResponse> sendData(@Body GroupSignaturePayload groupSignaturePayload);

    @POST("/locations/hybrid")
    Call<DcsResponse> sendData(@Body HybridPayload hybridPayload);
}
