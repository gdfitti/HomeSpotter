package org.uvigo.esei.example.homespotter.imgbb;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import okhttp3.MultipartBody;

public interface ImgBBApi {
    @Multipart
    @POST("1/upload")
    Call<ImgBBResponse> uploadImage(
            @Part("key") okhttp3.RequestBody apiKey,
            @Part MultipartBody.Part image
    );
}
