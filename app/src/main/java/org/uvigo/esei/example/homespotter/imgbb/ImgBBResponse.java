package org.uvigo.esei.example.homespotter.imgbb;

/**
 * Clase ImgBBResponse
 * Esta clase representa la estructura de la respuesta JSON proporcionada por la API de ImgBB.
 * Sirve como modelo para mapear los datos devueltos por la API.
 * Estructura esperada de la respuesta JSON:
 * {
 *     "data": {
 *         "id": "unique_image_id",
 *         "url": "https://example.com/image.jpg",
 *         "display_url": "https://example.com/display_image.jpg",
 *         "delete_url": "https://example.com/delete_image.jpg"
 *     },
 *     "status": "success"
 * }
 * Atributos principales:
 * - `data`: Objeto que contiene la información detallada de la imagen subida.
 * - `status`: Estado de la operación (por ejemplo, "success").
 */
public class ImgBBResponse {
    public Data data;
    public String status;

    /**
     * Clase anidada Data
     * Contiene los datos específicos de la imagen subida al servicio ImgBB.
     */
    public class Data {
        /**
         * ID único de la imagen en ImgBB.
         */
        public String id;

        /**
         * URL directa de la imagen subida.
         */
        public String url;

        /**
         * URL para mostrar la imagen.
         */
        public String display_url;

        /**
         * URL para eliminar la imagen desde ImgBB.
         */
        public String delete_url;
    }
}

