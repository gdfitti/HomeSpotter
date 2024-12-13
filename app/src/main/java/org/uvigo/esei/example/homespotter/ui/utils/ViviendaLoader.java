package org.uvigo.esei.example.homespotter.ui.utils;

import android.database.Cursor;

import org.uvigo.esei.example.homespotter.database.FavoritosEntity;
import org.uvigo.esei.example.homespotter.database.FotosEntity;
import org.uvigo.esei.example.homespotter.database.ViviendaEntity;
import org.uvigo.esei.example.homespotter.models.Vivienda;

import java.util.ArrayList;
import java.util.List;

public class ViviendaLoader {

    /**
     * Carga una lista de viviendas desde la base de datos.
     *
     * @param viviendaEntity La entidad para acceder a las viviendas.
     * @param fotosEntity La entidad para acceder a las fotos de las viviendas.
     * @param idUsuario El ID del usuario actual.
     * @param filtrarPropias Si es true, filtra solo las viviendas propias; de lo contrario, excluye las propias.
     * @return Una lista de objetos Vivienda con todos los datos, incluidas fotos.
     */
    public static List<Vivienda> cargarViviendas(ViviendaEntity viviendaEntity, FotosEntity fotosEntity, FavoritosEntity favoritosEntity, int idUsuario, boolean filtrarPropias) {
        List<Vivienda> viviendas = new ArrayList<>();
        List<Integer> favList = favoritosEntity.obtenerFavoritosPorUsuario(idUsuario);
        Cursor cursor = null;

        try {
            if(filtrarPropias){
                cursor = viviendaEntity.buscarPorPropietario(idUsuario);
            }else{
                cursor = viviendaEntity.buscar(null, null, null);
            }

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idPropietario = cursor.getInt(cursor.getColumnIndexOrThrow("propietario_id"));
                    // Aplicar filtro según el valor de `filtrarPropias`
                    if ((filtrarPropias && idPropietario == idUsuario) || (!filtrarPropias && idPropietario != idUsuario)) {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_vivienda"));
                        String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                        String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo_vivienda"));
                        double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                        String direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion"));
                        String estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
                        String contacto = cursor.getString(cursor.getColumnIndexOrThrow("contacto"));
                        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                        boolean favorito = false;
                        // Obtener fotos asociadas a la vivienda
                        List<String> fotos = new ArrayList<>();
                        Cursor fotosCursor = null;
                        try {
                            fotosCursor = fotosEntity.obtenerFotosPorVivienda(id);
                            if (fotosCursor != null && fotosCursor.moveToFirst()) {
                                do {
                                    String urlFoto = fotosCursor.getString(fotosCursor.getColumnIndexOrThrow("url_foto"));
                                    fotos.add(urlFoto);
                                } while (fotosCursor.moveToNext());
                            }
                        } finally {
                            if (fotosCursor != null) {
                                fotosCursor.close();
                            }
                        }

                        if(!filtrarPropias){
                            favorito = favList.contains(id);
                        }
                        // Crear objeto Vivienda y agregarlo a la lista
                        viviendas.add(new Vivienda(id, titulo, tipo, precio, direccion, estado, contacto, descripcion, idPropietario, favorito, fotos));
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return viviendas;
    }
}
