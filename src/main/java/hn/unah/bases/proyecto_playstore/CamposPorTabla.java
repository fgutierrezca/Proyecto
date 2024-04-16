package hn.unah.bases.proyecto_playstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import hn.unah.bases.proyecto_playstore.DTOs.CampoDTO;

public class CamposPorTabla {
    

    public ArrayList<CampoDTO> obtenerCampos(String userDB, String pass, String table){
        try {
            Conexion conexion = new Conexion();
            Connection conn2 = conexion.openConnection(userDB, pass);

            PreparedStatement select = conn2.prepareStatement("SELECT column_name, data_type FROM all_tab_columns WHERE table_name ='"  + table + "'");
			ResultSet rslt = select.executeQuery();

            ArrayList<CampoDTO> campos = new ArrayList<CampoDTO>();

            

            while (rslt.next()) {
                CampoDTO campoDTO = new CampoDTO();

                campoDTO.setColumnName(rslt.getString("column_name"));
                campoDTO.setDataType(rslt.getString("data_type"));

                campos.add(campoDTO);
			}

            conexion.closeConnection(conn2);

            return campos;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }


}



