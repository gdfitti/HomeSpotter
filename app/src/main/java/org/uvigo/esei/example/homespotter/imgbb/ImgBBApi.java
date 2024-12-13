package org.uvigo.esei.example.homespotter.imgbb;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import okhttp3.MultipartBody;

/**
 * Interfaz ImgBBApi
 *
 * Esta interfaz define el endpoint para interactuar con la API de ImgBB y realizar la carga de imágenes.
 * Utiliza Retrofit para manejar las solicitudes HTTP.
 *
 * Endpoint principal:
 * - POST /1/upload: Permite subir imágenes al servicio ImgBB.
 *
 * Dependencias necesarias:
 * - Retrofit: Manejo de solicitudes HTTP.
 * - OkHttp: Gestión de cuerpos de solicitudes y respuestas.
 *
 * Métodos:
 * - uploadImage(RequestBody apiKey, MultipartBody.Part image): Realiza la solicitud de carga de una imagen.
 */
public interface ImgBBApi {

    /**
     * Método para subir una imagen al servicio ImgBB.
     *
     * @param apiKey Clave de la API para autenticar la solicitud.
     * @param image Imagen que se desea cargar, encapsulada en un objeto MultipartBody.Part.
     * @return Un objeto Call que contiene la respuesta de la API encapsulada en ImgBBResponse.
     *
     * Ejemplo de respuesta esperada:
     * {
     *     "data": {
     *         "display_url": "https://example.com/image.jpg",
     *         ...
     *     },
     *     "success": true,
     *     ...
     * }
     */
    @Multipart
    @POST("1/upload")
    Call<ImgBBResponse> uploadImage(
            @Part("key") okhttp3.RequestBody apiKey,
            @Part MultipartBody.Part image
    );
}