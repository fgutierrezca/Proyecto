package hn.unah.bases.proyecto_playstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import hn.unah.bases.proyecto_playstore.DTOs.ConexionDTO;

public class ConexionesDisponibles {
    
    public ArrayList<ConexionDTO> obtenerConexionesDisponibles(){
        try {
            Conexion conexion = new Conexion();
            Connection conn = conexion.openConnection("system", "oracle");

            PreparedStatement select = conn.prepareStatement("SELECT s.sid, s.serial#, s.username, s.osuser, s.machine, s.program FROM v$session s WHERE s.status = 'INACTIVE'");
			ResultSet rslt = select.executeQuery();

            ArrayList<ConexionDTO> conexionesDisponibles = new ArrayList<ConexionDTO>();

            while (rslt.next()) {
                ConexionDTO conexionDTO = new ConexionDTO();
                conexionDTO.setSid(rslt.getInt("sid"));
                conexionDTO.setSerial(rslt.getInt("serial#"));
                conexionDTO.setUsername(rslt.getString("username"));
                conexionDTO.setOsuser(rslt.getString("osuser"));
                conexionDTO.setMachine(rslt.getString("machine"));
                conexionDTO.setProgram(rslt.getString("program"));

                conexionesDisponibles.add(conexionDTO);
			}

            conexion.closeConnection(conn);

            return conexionesDisponibles;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}
