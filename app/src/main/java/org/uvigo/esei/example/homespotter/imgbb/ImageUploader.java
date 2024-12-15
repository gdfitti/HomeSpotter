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
        void onSuccess(String imageUrl, String deleteUrl);
        void onError(String error);
    }

    public interface DeleteCallback {
        void onComplete(boolean success);
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
                    Log.d("ImageUploader.uploadImage", "Imagen subida correctamente.");
                    String imageUrl = response.body().data.display_url;
                    String deleteUrl = response.body().data.delete_url; // Extraer delete_url
                    callback.onSuccess(imageUrl, deleteUrl);
                } else {
                    callback.onError("Error al cargar imagen: " + response.message());
                    Log.e("ImageUploader.onResponse", "Error al cargar imagen: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ImgBBResponse> call, Throwable t) {
                Log.d("ImageUploader.deleteImage", "Entrando a onFailure...");
                Log.e("ImageUploader.deleteImage", "Error al realizar la solicitud de carga: " + t.getMessage());
                callback.onError("Error: " + t.getMessage());
            }
        });
    }

    /**
     * Elimina una imagen del servicio ImgBB.
     *
     * @param deleteUrl URL de la imagen que se desea eliminar.
     * @param callback Callback para indicar si la eliminación fue exitosa o no.
     */
    public void deleteImage(String deleteUrl, DeleteCallback callback) {
        String deleteHash = extractDeleteHash(deleteUrl);
        if (deleteHash == null) {
            Log.e("ImageUploader.deleteImage", "El deleteHash no se pudo extraer de la URL: " + deleteUrl);
            callback.onComplete(false);
            return;
        }

        ImgBBApi apiService = RetrofitClient.getRetrofitInstance().create(ImgBBApi.class);
        Call<Void> call = apiService.deleteImage("h9vmr9k/ae63587128763104d6cb1679e3962da7", API_KEY);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ImageUploader.deleteImage", "Imagen eliminada correctamente. DeleteHash: " + deleteHash);
                    callback.onComplete(true);
                } else {
                    Log.e("ImageUploader.deleteImage", "Error al eliminar imagen. Código: " + response.code() + ", Mensaje: " + response.message());
                    Log.e("ImageUploader.deleteImage", "Cuerpo de la respuesta: " + response.errorBody());
                    callback.onComplete(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ImageUploader.deleteImage", "Error al realizar la solicitud de eliminación: " + t.getMessage());
                callback.onComplete(false);
            }
        });
    }

    public String extractDeleteHash(String deleteUrl) {
        if (deleteUrl != null && deleteUrl.contains("/")) {
            return deleteUrl.substring(deleteUrl.lastIndexOf("/") + 1);
        }
        return null;
    }

}

