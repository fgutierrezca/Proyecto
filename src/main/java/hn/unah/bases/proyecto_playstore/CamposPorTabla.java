package hn.unah.bases.proyecto_playstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Pattern;

import ch.qos.logback.core.boolex.Matcher;
import hn.unah.bases.proyecto_playstore.DTOs.CampoDTO;

public class CamposPorTabla {

    public ArrayList<CampoDTO> obtenerCampos(Connection conn2, String table) {
        ArrayList<CampoDTO> campos = new ArrayList<>();
    
        try {
            PreparedStatement select = conn2.prepareStatement(
                    "SELECT column_name, data_type, data_length FROM all_tab_columns WHERE table_name = ?");
            select.setString(1, table);
            ResultSet rslt = select.executeQuery();
    
            while (rslt.next()) {
                CampoDTO campoDTO = new CampoDTO();
                campoDTO.setColumnName(rslt.getString("column_name"));
                campoDTO.setColumnNameConvert(rslt.getString("column_name"));
                campoDTO.setAlias(rslt.getString("column_name"));
                campoDTO.setDataType(rslt.getString("data_type"));
                
                // Verificamos si el tipo de dato es VARCHAR2
                if ("VARCHAR2".equals(rslt.getString("data_type"))) {
                    // Si es VARCHAR2, obtenemos la longitud máxima
                    int maxLength = rslt.getInt("data_length");
                    // Establecemos la longitud máxima en el objeto CampoDTO
                    campoDTO.setMaxLength(maxLength);
                    campoDTO.setMaxLeghtConvert(maxLength);
                }
    
                campos.add(campoDTO);
            }
            return campos;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public ResultSet obtenerCampos2(Connection conn2, String table) {
        
        try {
            PreparedStatement select = conn2.prepareStatement( "SELECT * FROM " + table );
            ResultSet rslt = select.executeQuery();
    
        
            return rslt;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public ArrayList<CampoDTO> obtenerCamposConsulta(Connection conn2, String consulta) throws SQLException {
        // Lista para almacenar los objetos CampoDTO
        ArrayList<CampoDTO> campos = new ArrayList<>();
    
        try (
            // Preparamos la consulta SQL
            PreparedStatement select = conn2.prepareStatement(consulta);
            // Ejecutamos la consulta y obtenemos el conjunto de resultados
            ResultSet rslt = select.executeQuery()
        ) {
            // Obtenemos metadatos sobre el resultado de la consulta
            ResultSetMetaData metaData = rslt.getMetaData();
            // Obtenemos el número total de columnas en el resultado
            int columnCount = metaData.getColumnCount();
    
            // Verificamos si la consulta devuelve algún resultado
            if (columnCount > 0) {
                // Iteramos sobre cada columna en el resultado
                for (int i = 1; i <= columnCount; i++) {
                    // Creamos un nuevo objeto CampoDTO para almacenar información sobre la columna
                    CampoDTO campoDTO = new CampoDTO();
                    // Establecemos el nombre de la columna en el objeto CampoDTO
                    campoDTO.setColumnName(metaData.getColumnName(i));
                    // Establecemos el tipo de dato de la columna en el objeto CampoDTO
                    campoDTO.setDataType(metaData.getColumnTypeName(i));
                    campoDTO.setColumnNameConvert(metaData.getColumnName(i));
                    campoDTO.setAlias(metaData.getColumnName(i));
                    
                    // Verificamos si el tipo de dato es VARCHAR
                    if ("VARCHAR2".equals(metaData.getColumnTypeName(i))) {
                        // Si es VARCHAR, obtenemos la longitud máxima
                        int maxLength = metaData.getColumnDisplaySize(i);
                        // Establecemos la longitud máxima en el objeto CampoDTO
                        campoDTO.setMaxLength(maxLength);
                        campoDTO.setMaxLeghtConvert(maxLength);
                    }
                    
                    // Agregamos el objeto CampoDTO a la lista de campos
                    campos.add(campoDTO);
                }
            }
        }
    
        // Retornamos la lista de campos
        return campos;
    }

    public ArrayList<CampoDTO> obtenerCamposConsulta2(Connection conn2, String consulta) throws SQLException {
        PreparedStatement select = conn2.prepareStatement(consulta);
        ResultSet rslt = select.executeQuery();
        ArrayList<CampoDTO> campos = new ArrayList<>();
        
        // Crear la tabla temporal en la base de datos
        Statement createTempTable = conn2.createStatement();
        ResultSetMetaData metaData = rslt.getMetaData();
        int columnCount = metaData.getColumnCount();
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE temp_table (");
        
        // Construir la parte de la sentencia SQL para definir las columnas de la tabla temporal
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            String columnType = metaData.getColumnTypeName(i);
            int columnSize = metaData.getPrecision(i); // Obtener precisión de la columna
            createTableQuery.append(columnName).append(" ").append(columnType);
            if (columnType.equals("NUMBER")) { // Manejar columnas numéricas
                createTableQuery.append("(").append(columnSize).append(")");
            } else if (columnSize > 0) {
                createTableQuery.append("(").append(columnSize);
                int decimalDigits = metaData.getScale(i); // Obtener la escala de la columna numérica
                if (decimalDigits > 0) {
                    createTableQuery.append(",").append(decimalDigits); // Agregar la escala si es un número decimal
                }
                createTableQuery.append(")");
            }
            if (i < columnCount) {
                createTableQuery.append(", ");
            }
        }
        createTableQuery.append(")");
        createTempTable.executeUpdate(createTableQuery.toString());
        
        // Insertar los registros en la tabla temporal
        while (rslt.next()) {
            StringBuilder insertQuery = new StringBuilder("INSERT INTO temp_table VALUES (");
            for (int i = 1; i <= columnCount; i++) {
                // Dependiendo del tipo de dato de la columna, puedes manejarlo adecuadamente aquí
                // En este ejemplo, se asume que todas las columnas son de tipo String
                String columnValue = rslt.getString(i);
                insertQuery.append("'").append(columnValue).append("'");
                if (i < columnCount) {
                    insertQuery.append(", ");
                }
            }
            insertQuery.append(")");
            Statement insertStatement = conn2.createStatement();
            insertStatement.executeUpdate(insertQuery.toString());
        }


         // Verificamos si la consulta devuelve algún resultado
         if (columnCount > 0) {
            // Iteramos sobre cada columna en el resultado
            for (int i = 1; i <= columnCount; i++) {
                // Creamos un nuevo objeto CampoDTO para almacenar información sobre la columna
                CampoDTO campoDTO = new CampoDTO();
                // Establecemos el nombre de la columna en el objeto CampoDTO
                campoDTO.setColumnName(metaData.getColumnName(i));
                // Establecemos el tipo de dato de la columna en el objeto CampoDTO
                campoDTO.setDataType(metaData.getColumnTypeName(i));
                campoDTO.setColumnNameConvert(metaData.getColumnName(i));
                campoDTO.setAlias(metaData.getColumnName(i));
                
                // Verificamos si el tipo de dato es VARCHAR
                if ("VARCHAR2".equals(metaData.getColumnTypeName(i))) {
                    // Si es VARCHAR, obtenemos la longitud máxima
                    int maxLength = metaData.getColumnDisplaySize(i);
                    // Establecemos la longitud máxima en el objeto CampoDTO
                    campoDTO.setMaxLength(maxLength);
                    campoDTO.setMaxLeghtConvert(maxLength);
                }
                
                // Agregamos el objeto CampoDTO a la lista de campos
                campos.add(campoDTO);
            }
        }
        
        // Ahora puedes realizar las transformaciones utilizando los datos almacenados en la tabla temporal
        
        return campos;
    }



    public static String devolverFrom(String consulta) {
        // Patrón de búsqueda para encontrar la última ocurrencia de FROM seguida de cualquier cantidad de caracteres que no sean FROM
        Pattern patron = Pattern.compile("(?i)(?s)(?<=FROM(?!.*FROM)).*$");
        java.util.regex.Matcher matcher = patron.matcher(consulta);
    
        // Variable para almacenar la parte de la consulta después de la última ocurrencia de FROM
        String parteDespuesDeUltimoFrom = "";
    
        // Buscar la última coincidencia de FROM y extraer la parte después de él
        if (matcher.find()) {
            parteDespuesDeUltimoFrom = matcher.group().trim();
        }
    
        return parteDespuesDeUltimoFrom;
    }
}
