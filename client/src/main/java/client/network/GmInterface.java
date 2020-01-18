package client.network;

import client.model.GmResponse;
import client.model.RegistrationRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GmInterface {

    @POST("/register")
    Call<GmResponse> registerToGm(@Body RegistrationRequest registrationRequest);

    @GET("/keys")
    Call<Object> getPublicKey();
}
