package org.uvigo.esei.example.homespotter.imgbb;

import android.util.Log;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Clase ImageUploader
 * Esta clase permite subir imágenes al servicio ImgBB utilizando su API.
 * Utiliza Retrofit para realizar llamadas HTTP.
 * Dependencias requeridas:
 * - Retrofit
 * - OkHttp
 * - Una interfaz `ImgBBApi` que define el endpoint de la API.
 * Métodos principales:
 * - uploadImage(String imagePath): Subir una imagen al servicio ImgBB.
 */
public class ImageUploader {

    private static final String API_KEY = "6ec76e02770029fefd3a32df22b0e4e4";

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    /**
     * Sube una imagen al servicio ImgBB.
     *
     * @param imagePath Ruta del archivo de la imagen que se desea subir.
     *
     * El método utiliza Retrofit para hacer una solicitud POST al endpoint de ImgBB.
     * En caso de éxito, registra en el log la URL de la imagen subida.
     * En caso de fallo, registra el mensaje de error en el log.
     */
    public void uploadImage(String imagePath, UploadCallback callback) {
        File file = new File(imagePath);

        // Preparar el archivo directamente como MultipartBody.Part
        MultipartBody.Part body = MultipartBody.Part.createFormData(
                "image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file)
        );

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
                    callback.onSuccess(imageUrl);
                } else {
                    callback.onError("Error al cargar imagen: " + response.message());
                    Log.e("ImageUploader.onResponse", "Error al cargar imagen: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ImgBBResponse> call, Throwable t) {
                callback.onError("Error: " + t.getMessage());
                Log.e("ImageUploader.onResponse", "Error: " + t.getMessage());
            }
        });
    }
}

