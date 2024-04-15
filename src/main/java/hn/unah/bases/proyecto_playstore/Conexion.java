package hn.unah.bases.proyecto_playstore;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

import oracle.jdbc.pool.OracleDataSource;

@Service
public class Conexion {

    public java.sql.Connection openConnection(String user, String password) throws SQLException {
        OracleDataSource ods = new OracleDataSource();
		ods.setURL("jdbc:oracle:thin:@//localhost:1521/xe"); // jdbc:oracle:thin@//[hostname]:[port]/[DB service name]
		ods.setUser(user);
		ods.setPassword(password);
		Connection conn = ods.getConnection();

        return conn;
    }

    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

}