package hn.unah.bases.proyecto_playstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class IngresarDatosDestino {

    public int ingresarRegistros(String userDBOOrg, String passOrg, String userDBODes, String passDes,
            ArrayList<String> camposOrigen, String tableOrigen, String tableDestino) {

        int cantInsert = 0;

        try {
            // Establecer la conexión con la base de datos de origen
            Conexion conexionOrg = new Conexion();
            Connection connOrg = conexionOrg.openConnection(userDBOOrg, passOrg);

            // Establecer la conexión con la base de datos de destino
            Conexion conexionDes = new Conexion();
            Connection connDes = conexionDes.openConnection(userDBODes, passDes);

            // Construir la parte de la consulta SQL para seleccionar los campos de origen
            StringBuilder camposConsulta = new StringBuilder();
            for (String campo : camposOrigen) {
                camposConsulta.append(campo).append(", ");
            }
            camposConsulta.delete(camposConsulta.length() - 2, camposConsulta.length()); // Eliminar la última coma y el espacio

            // Preparar la consulta SQL para obtener los registros de origen
            String consulta = "SELECT " + camposConsulta.toString() + " FROM " + tableOrigen;
            PreparedStatement stmtOrg = connOrg.prepareStatement(consulta);

            // Ejecutar la consulta y obtener el ResultSet
            ResultSet rsOrg = stmtOrg.executeQuery();

            // Obtener la metadata del ResultSet para obtener información sobre las columnas
            ResultSetMetaData metaData = rsOrg.getMetaData();
            int cantidadColumnas = metaData.getColumnCount();

            // Preparar la consulta de inserción para la tabla de destino
            StringBuilder consultaInsert = new StringBuilder("INSERT INTO " + tableDestino + " VALUES (");
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

            // Cerrar la conexión cuando hayas terminado
            connOrg.close();
            connDes.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cantInsert;
    }
}
