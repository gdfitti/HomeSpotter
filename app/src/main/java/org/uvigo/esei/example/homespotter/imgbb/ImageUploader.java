package org.uvigo.esei.example.homespotter.imgbb;

import android.util.Log;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUploader {

    private static final String API_KEY = "6ec76e02770029fefd3a32df22b0e4e4";

    public void uploadImage(String imagePath) {
        // Preparar el archivo
        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // Clave de API
        RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), API_KEY);

        // Llamar al endpoint
        ImgBBApi apiService = RetrofitClient.getRetrofitInstance().create(ImgBBApi.class);
        Call<ImgBBResponse> call = apiService.uploadImage(apiKey, body);

        call.enqueue(new Callback<ImgBBResponse>() {
            @Override
            public void onResponse(Call<ImgBBResponse> call, Response<ImgBBResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().data.display_url;
                    Log.d("ImgBB", "Imagen cargada: " + imageUrl);
                } else {
                    Log.e("ImgBB", "Error al cargar imagen: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ImgBBResponse> call, Throwable t) {
                Log.e("ImgBB", "Error: " + t.getMessage());
            }
        });
    }
}

