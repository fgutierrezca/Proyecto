package hn.unah.bases.proyecto_playstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.rowset.CachedRowSet;

import hn.unah.bases.proyecto_playstore.DTOs.CampoDTO;

public class IngresarDatosDestino {

    public int ingresarRegistros(Connection connOrg, Connection connDes,
            ArrayList<String> camposDestino, ArrayList<String> camposOrigen, String tableOrigen, String tableDestino) {

        int cantInsert = 0;

        try {

            // Construir la parte de la consulta SQL para seleccionar los campos de origen
            StringBuilder camposConsulta = new StringBuilder();
            for (String campo : camposOrigen) {
                camposConsulta.append(campo).append(", ");
            }
            camposConsulta.delete(camposConsulta.length() - 2, camposConsulta.length()); // Eliminar la última coma y el
                                                                                         // espacio
            //PREPARAMOS LOS CAMPOS EN LOS QUE SE INSERTARAN EN DESTINO
            StringBuilder camposInto = new StringBuilder();
            for (String campo : camposDestino) {
                camposInto.append(campo).append(", ");
            }
            camposInto.delete(camposInto.length() - 2, camposInto.length());// Eliminar la última coma y el
            // espacio

            // Preparar la consulta SQL para obtener los registros de origen
            String consulta = "SELECT " + camposConsulta.toString() + " FROM " + tableOrigen;
            PreparedStatement stmtOrg = connOrg.prepareStatement(consulta);

            // Ejecutar la consulta y obtener el ResultSet
            ResultSet rsOrg = stmtOrg.executeQuery();

            // Obtener la metadata del ResultSet para obtener información sobre las columnas
            ResultSetMetaData metaData = rsOrg.getMetaData();
            int cantidadColumnas = metaData.getColumnCount();

            // Preparar la consulta de inserción para la tabla de destino
            StringBuilder consultaInsert = new StringBuilder("INSERT INTO "+ tableDestino +"  ("+camposInto.toString() +") VALUES (");
            for (int i = 1; i <= cantidadColumnas; i++) {
                consultaInsert.append("?,");
            }
            consultaInsert.deleteCharAt(consultaInsert.length() - 1); // Eliminar la última coma
            consultaInsert.append(")");
            PreparedStatement stmtInsert = connDes.prepareStatement(consultaInsert.toString());

            // Iterar sobre los resultados utilizando el cursor (ResultSet)
            while (rsOrg.next()) {
                // Insertar los valores en la tabla de destino
                for (int i = 1; i <= cantidadColumnas; i++) {
                    stmtInsert.setObject(i, rsOrg.getObject(i));

                }

                // Ejecutar la consulta de inserción
                stmtInsert.executeUpdate();
                cantInsert++;
            }

            //DEJAMOS CAER LA TABLA
			PreparedStatement stmtDrop=connOrg.prepareStatement("drop table temp_table");
			stmtDrop.executeUpdate();

          
            // Cerrar la conexión cuando hayas terminado
            connOrg.close();
            connDes.close();

        } catch (SQLException e) {

            e.printStackTrace();
        }


        return cantInsert;
    }

    public int ingresarRegistros2(Connection connDes,
            CachedRowSet registrosConvertidos, String[] orden, String tableDestino) {

        int cantInsert = 0;

        try {
            registrosConvertidos.first();

            StringBuilder consultaInsert = new StringBuilder("INSERT INTO " + tableDestino + " VALUES (");
            for (int i = 1; i <= orden.length; i++) {

                consultaInsert.append("?,");
            }
            consultaInsert.deleteCharAt(consultaInsert.length() - 1); // Eliminar la última coma
            consultaInsert.append(")");
            PreparedStatement stmtInsert = connDes.prepareStatement(consultaInsert.toString());

            // Iterar sobre los resultados utilizando el cursor
            do {
                // Insertar los valores en la tabla de destino
                for (int i = 1; i <= orden.length; i++) {
                    int index = Integer.parseInt(orden[i - 1]);

                    stmtInsert.setObject(i, registrosConvertidos.getObject(index));

                }

                // Ejecutar la consulta de inserción
                stmtInsert.executeUpdate();
                cantInsert++;
            } while (registrosConvertidos.next());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cantInsert;
    }

    public void alterMaxLength(Connection connDes, String table, String campo, String cambio) throws SQLException {
        String consulta = "ALTER TABLE " + table + " MODIFY " + campo + " " + cambio;

        try (PreparedStatement stmt = connDes.prepareStatement(consulta)) {
            stmt.executeUpdate();
        }
    }

    


}
