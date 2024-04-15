package hn.unah.bases.proyecto_playstore;

import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hn.unah.bases.proyecto_playstore.DTOs.ConexionDTO;

@SpringBootApplication
public class ProyectoPlaystoreApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoPlaystoreApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		try (Scanner input = new Scanner(System.in)) {
			System.out.println("\n\tCREAR PROCESO ETL\n");

			ConexionesDisponibles conexionesDisponibles = new ConexionesDisponibles();
			ArrayList<ConexionDTO> conexiones = new ArrayList<ConexionDTO>();
			conexiones = conexionesDisponibles.obtenerConexionesDisponibles();

			int i = 0;
			System.out.println("Ver conexiones disponibles: ");
			for (ConexionDTO conexionDTO : conexiones) {
				System.out.printf("\t%s. %s\n", i, conexionDTO.getUsername());
				i++;
			}

			System.out.print("\nElija la conexión para la Base de datos OLTP: ");
			int conexionSelec;
			conexionSelec = input.nextInt();

			ConexionDTO conexionOLAP = conexiones.get(conexionSelec);
			System.out.printf("Escriba la contrasenia del usuario %s: ", conexionOLAP.getUsername());
			String contrasenia;
			contrasenia = input.next();

			TablasPorConexion tablasPorConexion = new TablasPorConexion();
			ArrayList<String> tablas = new ArrayList<String>();
			tablas = tablasPorConexion.obtenerTablas(conexionOLAP.getUsername(), contrasenia);

			i = 0;
			System.out.printf("\nVer tablas de la conexión %s: \n", conexionOLAP.getUsername());
			for (String tabla : tablas) {
				System.out.printf("\t%s. %s\n", i, tabla);
				i++;
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}

	}

}
