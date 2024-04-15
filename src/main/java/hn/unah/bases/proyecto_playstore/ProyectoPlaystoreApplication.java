package hn.unah.bases.proyecto_playstore;

import java.util.ArrayList;

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
		System.out.println("\n\tCREAR PROCESO ETL\n");

		ConexionesDisponibles conexionesDisponibles = new ConexionesDisponibles();
		ArrayList<ConexionDTO> conexiones = new ArrayList<ConexionDTO>();

		conexiones = conexionesDisponibles.obtenerConexionesDisponibles();

		System.out.println("Ver conexiones disponibles: ");
		for (ConexionDTO conexionDTO : conexiones) {
			System.out.println("\t" + conexionDTO.getUsername());
		}
	}

}
