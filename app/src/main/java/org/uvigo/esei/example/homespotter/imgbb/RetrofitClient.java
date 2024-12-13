package org.uvigo.esei.example.homespotter.imgbb;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Clase RetrofitClient
 *
 * Esta clase gestiona la configuración y creación de una instancia de Retrofit
 * para interactuar con la API de ImgBB.
 * Implementa el patrón Singleton para asegurar que solo exista una instancia
 * de Retrofit durante la ejecución.
 *
 * Uso de Retrofit:
 * Retrofit es una librería de cliente HTTP que facilita la comunicación con APIs RESTful.
 *
 * Configuración:
 * - Base URL: `https://api.imgbb.com/`
 * - Conversor: `GsonConverterFactory` para mapear las respuestas JSON en objetos Java.
 *
 * Métodos principales:
 * - `getRetrofitInstance()`: Devuelve la instancia de Retrofit.
 */
public class RetrofitClient {
    private static final String BASE_URL = "https://api.imgbb.com/";
    private static Retrofit retrofit;

    /**
     * Devuelve una instancia única de Retrofit configurada con la base URL
     * de la API de ImgBB y un conversor Gson para manejar respuestas JSON.
     *
     * @return Instancia de Retrofit.
     */
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
