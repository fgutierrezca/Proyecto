package hn.unah.bases.proyecto_playstore;

import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hn.unah.bases.proyecto_playstore.DTOs.CampoDTO;
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

			System.out.printf("\nSelecciona una tabla: \n");
			int tableSelect;
			tableSelect = input.nextInt();
			input.nextLine(); // Consumir el carácter de nueva línea en el búfer

			CamposPorTabla camposPorTabla = new CamposPorTabla();
			ArrayList<CampoDTO> campos = new ArrayList<CampoDTO>();
			campos = camposPorTabla.obtenerCampos(conexionOLAP.getUsername(), contrasenia, tablas.get(tableSelect));

			i = 0;
			System.out.printf("\nLos campos de la tabla %s son : \n",tablas.get(tableSelect));
			for (CampoDTO campo : campos) {
				System.out.printf("\t%s. %-30s  %s\n", i, campo.getColumnName(),campo.getDataType());
				i++;
			}

			System.out.printf("\nSeleccione los campos que desea obtener, separe los valores con una coma : \n");
			String entrada;
			entrada = input.nextLine();
			

			String[] valores = entrada.split(",");

			//Arreglo que permite almacenar los campos que el usuario seleccionó, esto es lo que trabajaremos en el data convert
			ArrayList<CampoDTO> camposSelect = new ArrayList<CampoDTO>();
			
			for (String valor : valores) {
				CampoDTO campoDTO = new CampoDTO();

				campoDTO.setColumnName(campos.get(Integer.parseInt(valor)).getColumnName());
				campoDTO.setDataType(campos.get(Integer.parseInt(valor)).getDataType());
				
				camposSelect.add(campoDTO);
			}

			//AQUI FALTA LA TRANFORMACIÓN;



			//DESTINO
			//Escogemos la conexión y la tabla de destino 
			i = 0;
			System.out.println("Ver conexiones disponibles: ");
			for (ConexionDTO conexionDTO : conexiones) {
				System.out.printf("\t%s. %s\n", i, conexionDTO.getUsername());
				i++;
			}

			System.out.print("\nElija la conexión para la Base de datos OLTP: ");
			int conexionSelecDes;
			conexionSelecDes = input.nextInt();

			ConexionDTO conexionOLAPDes = conexiones.get(conexionSelecDes);
			System.out.printf("Escriba la contrasenia del usuario %s: ", conexionOLAPDes.getUsername());
			String contraseniaDes;
			contraseniaDes = input.next();

			TablasPorConexion tablasPorConexionDes = new TablasPorConexion();
			ArrayList<String> tablasDes = new ArrayList<String>();
			tablasDes = tablasPorConexionDes.obtenerTablas(conexionOLAPDes.getUsername(), contraseniaDes);

			i = 0;
			System.out.printf("\nVer tablas de la conexión %s: \n", conexionOLAP.getUsername());
			for (String tablaDes : tablasDes) {
				System.out.printf("\t%s. %s\n", i, tablaDes);
				i++;
			}

			System.out.printf("\nSelecciona una tabla: \n");
			int tableSelectDes;
			tableSelectDes = input.nextInt();
			input.nextLine(); // Consumir el carácter de nueva línea en el búfer

			CamposPorTabla camposPorTablaDes = new CamposPorTabla();
			ArrayList<CampoDTO> camposDes = new ArrayList<CampoDTO>();
			camposDes = camposPorTablaDes.obtenerCampos(conexionOLAPDes.getUsername(), contraseniaDes, tablasDes.get(tableSelectDes));


			System.out.printf("\nIngrese el valor de los campos de origen en orden para los campos de destino, separe con comas: \n");
			
			i = 0;
			System.out.printf("\nLos campos de la tabla  destino %s son : \n",tablasDes.get(tableSelectDes));
			for (CampoDTO campoDes : camposDes) {
				
				System.out.printf("\t%s. %-30s  %s \n", i, campoDes.getColumnName(),campoDes.getDataType());
				i++;
			}

			i = 0;
			System.out.printf("\nLos campos de la tabla origen %s son : \n",tablas.get(tableSelect));
			for (CampoDTO campoSelect : camposSelect) {
				System.out.printf("\t%s. %-30s  %s  \n", i,  campoSelect.getColumnName(),campoSelect.getDataType());
				i++;
			}

			String orden;
			orden = input.nextLine();
			

			String[] valoresOrden = orden.split(",");
			
			ArrayList<String> camposSelectOrden = new ArrayList<String>();
			
			for (int j = 0; j < valoresOrden.length; j++) {
				int index = Integer.parseInt(valoresOrden[j]);
			
				camposSelectOrden.add(camposSelect.get(index).getColumnName());
			
				/** // Agregar coma si no es el último elemento
				if (j < valoresOrden.length - 1) {
					camposSelectOrden.add(",");
				}*/
			}

			//Tengo el orden ahora necesito ingresarlos en la tabla
			IngresarDatosDestino ingresarDatosDestino = new IngresarDatosDestino();
			int cantInsert =ingresarDatosDestino.ingresarRegistros(conexionOLAP.getUsername(), contrasenia, conexionOLAPDes.getUsername(), contraseniaDes, camposSelectOrden,tablas.get(tableSelect),tablasDes.get(tableSelectDes) );
			System.out.printf("La cantidad de registros ingresados fueron : %d", cantInsert);
		}
		catch (Exception e) {
			System.out.println(e);
		}

	}

}
