package hn.unah.bases.proyecto_playstore;

import java.sql.Connection;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;
import javax.sql.rowset.RowSetProvider;

import javax.sql.rowset.CachedRowSet;

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

			/* -------------------------------------------------------- */
			/* ORIGEN DE DATOS */
			/* -------------------------------------------------------- */

			// INICIO DEL PROYECTO
			/** SOLICITAR LA SELECCIÓN DE LA CONEXIÓN DE INGRESO */

			System.out.println("\n\t\t  ╭──────────────────╮");
			System.out.println("\t\t  │                  │");
			System.out.println("\t\t  │  Proyecto ETL    │");
			System.out.println("\t\t  │    IS-601        │");
			System.out.println("\t\t  │                  │");
			System.out.println("\t\t  ╰──────────────────╯");
			System.out.println("\t\t        \\");
			System.out.println("\t\t         \\");
			System.out.println("\t\t          \\");
			System.out.println("\t\t            \\    ^__^");
			System.out.println("\t\t             \\   (oo)\\_______");
			System.out.println("\t\t                (__)\\       )\\/\\");
			System.out.println("\t\t                    ||----w |");
			System.out.println("\t\t                    ||     ||");
			System.out.println("\n\t\t    ¡Bienvenido!");

			System.out.println("╔═════════════════════════════════════════════════════════════╗");
			System.out.println("║                                                             ║");
			System.out.println("║                    Escogiendo el origen de datos            ║");
			System.out.println("║                                                             ║");
			System.out.println("╚═════════════════════════════════════════════════════════════╝");
			System.out.println("\n\n");

			// INICIAREMOS UN WHILE MIENTRAS EL USUARIO QUIERA SEGUIR CREANDO EL ETL
			/**
			 * 0. Crear proceso ETL
			 * 1. Terminar creación de procesos
			 */
			boolean crear = true;

			// CREAMOS LA ESTRUCTURA NECESARIA PARA ALMACENAR LAS CONEXIONES
			ConexionesDisponibles conexionesDisponibles = new ConexionesDisponibles();
			ArrayList<ConexionDTO> conexiones = new ArrayList<ConexionDTO>();

			while (crear) {

				/**
				 * MOSTRAMOS AL USUARIO LAS CONEXIONES DISPONIBLES Y LE PEDIMOS SELECCIONAR
				 * LA CONEXIÓN DE ORIGEN DEL ETL
				 */

				// OBTENEMOS LAS CONEXIONES DISPONIBLES

				conexiones = conexionesDisponibles.obtenerConexionesDisponibles();
				System.out.println(
						"\nA continuación se muestran las conexiones disponibles :");

				// RECORREMOS LA ESTRCUTURA Y LA MOSTRAMOS AL USUARIO
				int i = 0;
				for (ConexionDTO conexionDTO : conexiones) {
					System.out.printf("\t%s. %s\n", i, conexionDTO.getUsername());
					i++;
				}

				// USUARIO SELECCIONA LA CONEXION
				// VERIFICAR QUE EL VALOR INGRESADO ESTE DENTRO DEL RANGO PERMITIDO
				int conexionSelec = 0;
				boolean seleccionCorrecta = false;

				while (!seleccionCorrecta) {
					System.out.println("\nFavor seleccione una conexión como origen de datos:");
					conexionSelec = input.nextInt();

					// Verificar si el valor ingresado está dentro del rango permitido
					if (conexionSelec >= 0 && conexionSelec < conexiones.size()) {
						seleccionCorrecta = true; // Si está dentro del rango, establecer seleccionCorrecta a true para
													// salir del bucle
					} else {
						System.out.println(
								"El valor ingresado no es válido. Por favor, seleccione un número dentro del rango permitido.");
					}

					// Consumir el salto de línea pendiente después de nextInt() para evitar
					// problemas con las siguientes entradas
					input.nextLine();
				} // WHILE DE INGRESO DE VALOR NUMERICO

				boolean credencialesCorrectas = false;
				ConexionDTO conexionOrigen = new ConexionDTO();
				String contraseniaOrigen = null;
				Conexion conexion = new Conexion();
				Connection conOrigen = null;

				while (!credencialesCorrectas) {
					// Solicitamos las credenciales de la conexión
					conexionOrigen = conexiones.get(conexionSelec);
					System.out.printf("Escriba la contraseña del usuario %s: ", conexionOrigen.getUsername());
					contraseniaOrigen = input.next();

					// Intentamos establecer la conexión

					try {
						conOrigen = conexion.openConnection(conexionOrigen.getUsername(), contraseniaOrigen);
						credencialesCorrectas = true; // Si la conexión es exitosa, establecemos la bandera a true para
														// salir del bucle

					} catch (SQLException e) {
						System.out.println("No se pudo establecer la conexión. Verifique las credenciales.");
						credencialesCorrectas = false;

					}

				} // WHILE DE INTENTO DE CONEXION

				/**
				 * SI LA CONEXION ES EXITOSA OFRECEMOS AL USUARIO OBTENER CAMPOS MEDIANTE UNA
				 * CONSULTA
				 * O ESCOGER UNA TABLA
				 */
				seleccionCorrecta = false;
				int opcion = 0;

				while (!seleccionCorrecta) {
					System.out.println("╭──────────────────────╮");
					System.out.println("│                      │");
					System.out.println("│         Menú         │");
					System.out.println("│                      │");
					System.out.println("│ 1. Escoger tabla     │");
					System.out.println("│ 2. Ingresar consulta │");
					System.out.println("│                      │");
					System.out.println("│ Ingrese el número    │");
					System.out.println("│ correspondiente a la │");
					System.out.println("│ opción deseada:      │");
					System.out.println("│                      │");
					System.out.println("╰──────────────────────╯");
					opcion = input.nextInt();

					if (opcion == 1 || opcion == 2) {
						seleccionCorrecta = true;
					}

					// Limpiar el salto de línea pendiente después de nextInt()
					input.nextLine();
				}

				// VALOR DE LA TABLA A SELECCIONAR
				int tableSelect = 0;

				// CONSULTA PARA OBTENER INFORAMCIÓN
				String consulta = null;

				// CREAMOS LA ESTRUCTURA PARA OBETENER TODAS LAS TABLAS DE UNA CONEXION
				TablasPorConexion tablasPorConexion = new TablasPorConexion();
				ArrayList<String> tablas = new ArrayList<String>();

				// CREAMOS LA ESTRUCTURA PARA OBTENER LOS CAMPOS DE LA TABLA
				CamposPorTabla camposPorTabla = new CamposPorTabla();
				ResultSet resultado = null;
				CachedRowSet resultadoOrigen = RowSetProvider.newFactory().createCachedRowSet();
				String consultaFrom = null;
				boolean fromTable = false;

				switch (opcion) {
					case 1:
						// Lógica para la opción 1: Escoger tabla
						fromTable = true;
						// ENVIAMOS LA INFORMACIÓN DE LA CONEXION A LA TABLA ENVIAMOS EL
						// ESCHEMA(USUARIO) Y LA CONEXION EXISTOSA
						tablas = tablasPorConexion.obtenerTablas(conexionOrigen.getUsername(), conOrigen);

						// MOSTRAMOS LA LISTA DE TABLAS QUE EL SCHEMA POSEE
						i = 0;
						System.out.printf("\nLas tablas que el  %s posee son: \n", conexionOrigen.getUsername());
						for (String tabla : tablas) {
							System.out.printf("\t%s. %s\n", i, tabla);
							i++;
						}

						// COLOCAMOS LA VARIABLE DE SELECCION CORRECTA EN FALSE
						seleccionCorrecta = false;

						while (!seleccionCorrecta) {
							System.out.printf("\nSelecciona la tabla de origen : \n");
							tableSelect = input.nextInt();
							// Verificar si el valor ingresado está dentro del rango permitido
							if (tableSelect >= 0 && tableSelect < tablas.size()) {
								seleccionCorrecta = true; // Si está dentro del rango, establecer seleccionCorrecta a
															// true para
															// salir del bucle
							} else {
								System.out.println(
										"El valor ingresado no es válido. Por favor, seleccione un número dentro del rango permitido.");
							}
							// Consumir el salto de línea pendiente después de nextInt() para evitar
							// problemas con las siguientes entradas
							input.nextLine();
						} // WHILE DE INGRESO DE VALOR NUMERICO

						// OBTENEMOS LOS CAMPOS
						resultado = camposPorTabla.obtenerCampos2(conOrigen, tablas.get(tableSelect));

						
						resultadoOrigen.populate(resultado);

						break;
					case 2:
						// Lógica para la opción 2: Ingresar consulta
						fromTable = false;
						boolean consultaCorrecta = false;

						while (!consultaCorrecta) {
							System.out.println("\nIngrese la consulta para obtener la información:");
							consulta = input.nextLine();

							try {

								resultado = camposPorTabla.obtenerCamposConsulta2(conOrigen, consulta);
								consultaCorrecta = true;

							} catch (Exception e) {
								System.out.println("Error al ejecutar la consulta: " + e.getMessage());
								consultaCorrecta = false;
							}
						}

						resultadoOrigen.populate(resultado);
						break;

				}// SWITCH DE TABLA O CONSULTA

				// MOSTRAMOS LOS CAMPOS DE LA TABLA O LOS OBTENIDOS CON LA CONSULTA

				// Obtenemos metadatos sobre el resultado de la consulta
				ResultSetMetaData metaData = resultadoOrigen.getMetaData();
				// Obtenemos el número total de columnas en el resultado
				int columnCount = metaData.getColumnCount();
				fromTable = false;

				if (columnCount > 0) {

					System.out.printf("\nLos campos de la consulta proporcionada son : \n");
					for (int l = 1; l <= columnCount; l++) {

						System.out.printf("\t%s. %-30s  %-15s %d\n", l, metaData.getColumnName(l),
								metaData.getColumnTypeName(l), metaData.getColumnDisplaySize(l));

					}

				} // IF COLUMNAS O CAMPOS ENCONTRADAS EN CONSULTA

				// EL USUARIO SELECCIONA LOS CAMPOS QUE DESEA LLEVAR A LA CONVERSION

				seleccionCorrecta = false;
				// ESTE ARREGLO CONTIENE LOS VALORES DE LOS CAMPOS QUE EL USUARIO QUIERE
				// TRANSFORMAR
				String[] valores = null;

				while (!seleccionCorrecta) {

					System.out
							.printf("\nSeleccione los campos que desea obtener, separe los valores con una coma : \n");
					String entrada;
					entrada = input.next();

					// ESTE ARREGLO CONTIENE LOS VALORES DE LOS CAMPOS QUE EL USUARIO QUIERE
					// TRANSFORMAR
					valores = entrada.split(",");

					// VERIFICAMOS QUE LOS VALORES SEAN CORRECTOS SI NO LOS SOLICITAMOS DE NUEVO
					for (String valor : valores) {

						int index = Integer.parseInt(valor);

						if (index > 0 && index < columnCount) {

							seleccionCorrecta = true;
						} else {
							seleccionCorrecta = false;
						}

					}
				} // WHILE DE LISTA DE VALORES DE CAMPOS

				/* -------------------------------------------------------- */
				/* INICIAMOS CON LA CONVERSION DE DATOS */
				/* -------------------------------------------------------- */

				System.out.println("╔═════════════════════════════════════════════════════════════╗");
				System.out.println("║                                                             ║");
				System.out.println("║                    Iniciando la conversión de datos         ║");
				System.out.println("║                                                             ║");
				System.out.println("╚═════════════════════════════════════════════════════════════╝");
				System.out.println("\n\n");

				seleccionCorrecta = false;
				int convertir = 0;

				while (!seleccionCorrecta) {

					System.out.println("╭───────────────────────────╮");
					System.out.println("│                           │");
					System.out.println("│  Opciones disponibles     │");
					System.out.println("│                           │");
					System.out.println("│  1. Transformar           │");
					System.out.println("│  2. Continuar a destino   │");
					System.out.println("│                           │");
					System.out.println("╰───────────────────────────╯");

					convertir = input.nextInt();

					if (convertir == 1 || convertir == 2) {
						seleccionCorrecta = true;
					}
				} // while de convertir o continuar

				

				if (convertir == 1) {
					boolean transformar = true;
					int transformarSelect = 1;
					int indexSelect = -1;
				    String campoEnConver = null;
					opcion=0;

						while (transformar) {
							
							// MOSTRAMOS A LOS USUARIOS LOS CAMPOS SELECCIONADOS
							if (columnCount > 0) {

								System.out.printf("\nCampos para transformación : \n");
								for (String valor : valores) {
									int l = Integer.parseInt(valor);

									System.out.printf("\t%s. %-30s  %-15s %d\n", l, metaData.getColumnName(l),
											metaData.getColumnTypeName(l), metaData.getColumnDisplaySize(l));

								}

							} // IF CAMPOS ESCOGIDOS POR EL USUARIO


							transformarSelect = 1;
							seleccionCorrecta=false;
							
							System.out.printf("\nEscoja el campo que desea transformar  \n");
							indexSelect = input.nextInt();

							
							
							//OBTENEMOS EL TIPO DEL CAMPO SELECCIONADO PARA TRANSFORMAR
							String tipo = metaData.getColumnTypeName(indexSelect);
							campoEnConver =metaData.getColumnName(indexSelect);

							if ("VARCHAR2".equals(tipo)
									|| "CHAR".equals(tipo)) {
								System.out.println("╭───────────────────────────╮");
								System.out.println("│                           │");
								System.out.println("│  Opciones disponibles     │");
								System.out.println("│                           │");
								System.out.println("│  1. Convertir a Mayúscula │");
								System.out.println("│  2. Convertir a Minúscula │");
								System.out.println("│  3. Concatenar con campo  │");
								System.out.println("│  4. Concatenar valor      │");
								System.out.println("╰───────────────────────────╯");

							} else if ("TIMESTAMP".equals(tipo)
									|| "DATE".equals(tipo)) {

								System.out.println("╭──────────────────────╮");
								System.out.println("│                      │");
								System.out.println("│  Opciones disponibles│");
								System.out.println("│                      │");
								System.out.println("│  5. Extraer Año      │");
								System.out.println("│  6. Extraer Mes      │");
								System.out.println("│  7. Extraer Día      │");
								System.out.println("│  8. Extraer Hora     │");
								System.out.println("│                      │");
								System.out.println("╰──────────────────────╯");

							}

							opcion = input.nextInt();

							if (opcion > 0 && opcion <= 8) {
								seleccionCorrecta = true;
							} else {
								seleccionCorrecta = false;
							}

							// Limpiar el salto de línea pendiente después de nextInt()
							input.nextLine();

						

						switch (opcion) {
							case 1:
								// Lógica para la opción 1: Ingresar consulta
								resultadoOrigen.first();
								do {

									String campo = resultadoOrigen.getString(campoEnConver);
									String campoMayus = campo.toUpperCase();
									resultadoOrigen.updateString(campoEnConver, campoMayus);
									
								}while (resultadoOrigen.next());

						
							break;
							case 2:
								// Lógica para la opción 1: Ingresar consulta
								resultadoOrigen.first();
								do {

									String campo = resultadoOrigen.getString(campoEnConver);
									String campoMinus = campo.toLowerCase();
									resultadoOrigen.updateString(campoEnConver, campoMinus);
									
								}while (resultadoOrigen.next());

								

								//camposSelect.get(indexSelect).upperColumn();
							break;
							case 3:
								// Lógica para la opción 1: Ingresar consulta
								seleccionCorrecta = false;
								int campoConcat = -1;
								
								
								// MOSTRAMOS LA LISTA DE CAMPOS CON LOS QUE PUEDE CONCATENAR
								i = 0;
								System.out.printf("\nCampos para concatenar  \n");
								// MOSTRAMOS A LOS USUARIOS LOS CAMPOS SELECCIONADOS
								if (columnCount > 0) {

									for (String valor : valores) {
										int l = Integer.parseInt(valor);

										System.out.printf("\t%s. %-30s  %-15s %d\n", l, metaData.getColumnName(l),
												metaData.getColumnTypeName(l), metaData.getColumnDisplaySize(l));

									}

								} // IF CAMPOS ESCOGIDOS POR EL USUARIO

								while (!seleccionCorrecta) {
									System.out.printf("\nIngrese el valor del campo a concatenar  \n");
									campoConcat = input.nextInt();
							
									boolean encontrado = Arrays.asList(valores).contains(String.valueOf(campoConcat));

									if (encontrado) {
										seleccionCorrecta = true;
									} else {
										seleccionCorrecta = false;
									}
								}

								resultadoOrigen.first();	
								do{
									String campo1 = resultadoOrigen.getString(campoEnConver);
									String campo2 = resultadoOrigen.getString(metaData.getColumnName(campoConcat));
									String campo3=campo1+" "+campo2;
									resultadoOrigen.updateString(campoEnConver, campo3);

								}while(resultadoOrigen.next());
								

								//camposSelect.get(indexSelect).upperColumn();
							break;
							case 4:
								// Lógica para la opción 1: Ingresar consulta
								String valor = null;
								System.out.printf("\nIngrese el valor a concatenar  \n");
								valor = input.nextLine();

								resultadoOrigen.first();
								do {

									String campo = resultadoOrigen.getString(campoEnConver);
									String campo2 = campo+" "+valor;
									resultadoOrigen.updateString(campoEnConver, campo2);
									
								}while (resultadoOrigen.next());

								

								//camposSelect.get(indexSelect).upperColumn();
							break;
							case 5:
								Calendar calendar = Calendar.getInstance();

								resultadoOrigen.first();	
								do {
									// Lógica para la opción 5: Ingresar consulta
									Date campo = resultadoOrigen.getDate(campoEnConver);
									calendar.setTime(campo);
									calendar.clear();

									int anio = calendar.get(Calendar.YEAR);
									calendar.set(Calendar.YEAR, anio);
									
									Date date = calendar.getTime();
									java.sql.Date sqlDate = new java.sql.Date(date.getTime());
									resultadoOrigen.updateDate(campoEnConver,sqlDate);

									
								}while (resultadoOrigen.next());

								System.out.printf("\nEl valor devuelto será number \n");
								
								
							break;	
						
						}//Switch



						System.out.println("╭───────────────────────╮");
						System.out.println("│                       │");
						System.out.println("│  Opciones disponibles │");
						System.out.println("│                       │");
						System.out.println("│  1. Seguir transformando│");
						System.out.println("│  2. Salir             │");
						System.out.println("╰───────────────────────╯");

						seleccionCorrecta = false;

						while (!seleccionCorrecta) {
							transformarSelect = input.nextInt();

							if (transformarSelect == 1) {
								transformar = true;
								seleccionCorrecta = true;

							} else if (transformarSelect == 2) {
								transformar = false;
								seleccionCorrecta = true;
							} else {
								seleccionCorrecta = false;
							}
						
						} // WHILE SELECCION CORRECTA
					} // WHILE TRANSFORMACION

				}  // FINAL DEL ELSE DE CONVERTIR

				System.out.println("╔═════════════════════════════════════════════════════════════╗");
				System.out.println("║                                                             ║");
				System.out.println("║                    Escogiendo el destino de los datos       ║");
				System.out.println("║                                                             ║");
				System.out.println("╚═════════════════════════════════════════════════════════════╝");
				System.out.println("\n\n");

				// conexiones = conexionesDisponibles.obtenerConexionesDisponibles();
				System.out.println(
						"\nA continuación se muestran las conexiones disponibles :");

				// RECORREMOS LA ESTRCUTURA Y LA MOSTRAMOS AL USUARIO
				i = 0;
				for (ConexionDTO conexionDTO : conexiones) {
					System.out.printf("\t%s. %s\n", i, conexionDTO.getUsername());
					i++;
				}

				// USUARIO SELECCIONA LA CONEXION
				// VERIFICAR QUE EL VALOR INGRESADO ESTE DENTRO DEL RANGO PERMITIDO
				int conexionSelec2 = 0;
				seleccionCorrecta = false;

				while (!seleccionCorrecta) {
					System.out.println("\nFavor seleccione una conexión como destino de los datos:");
					conexionSelec2 = input.nextInt();

					// Verificar si el valor ingresado está dentro del rango permitido
					if (conexionSelec2 >= 0 && conexionSelec2 < conexiones.size()) {
						seleccionCorrecta = true; // Si está dentro del rango, establecer seleccionCorrecta a true para
													// salir del bucle
					} else {
						System.out.println(
								"El valor ingresado no es válido. Por favor, seleccione un número dentro del rango permitido.");
					}

					// Consumir el salto de línea pendiente después de nextInt() para evitar
					// problemas con las siguientes entradas
					input.nextLine();
				} // WHILE DE INGRESO DE VALOR NUMERICO

				credencialesCorrectas = false;
				ConexionDTO conexionDestino = conexiones.get(conexionSelec2);
				String contraseniaDestino = null;
				Conexion conexion2 = new Conexion();
				Connection conDestino = null;

				while (!credencialesCorrectas) {
					// Solicitamos las credenciales de la conexión
					conexionOrigen = conexiones.get(conexionSelec2);
					System.out.printf("Escriba la contraseña del usuario %s: ", conexionDestino.getUsername());
					contraseniaDestino = input.next();

					// Intentamos establecer la conexión

					try {
						conDestino = conexion2.openConnection(conexionDestino.getUsername(), contraseniaDestino);
						credencialesCorrectas = true; // Si la conexión es exitosa, establecemos la bandera a true para
														// salir del bucle

					} catch (SQLException e) {
						System.out.println("No se pudo establecer la conexión. Verifique las credenciales.");
						credencialesCorrectas = false;

					}

				} // WHILE DE INTENTO DE CONEXION

				// CREAMOS UNA NUEVA ESTRUCTURA PARA MOSTRAR LAS TABLAS DE LA CONEXIÓN DE
				// DESTINO
				TablasPorConexion tablasPorConexionDes = new TablasPorConexion();
				ArrayList<String> tablasDes = new ArrayList<String>();
				tablasDes = tablasPorConexionDes.obtenerTablas(conexionDestino.getUsername(), conDestino);

				i = 0;
				System.out.printf("\nVer tablas de la conexión %s: \n", conexionDestino.getUsername());
				for (String tablaDes : tablasDes) {
					System.out.printf("\t%s. %s\n", i, tablaDes);
					i++;
				}

				seleccionCorrecta = false;
				int tableSelectDes = -1;

				while (!seleccionCorrecta) {
				System.out.printf("\nSelecciona una tabla como destino: \n");
					tableSelectDes = input.nextInt();

					if (tableSelectDes >= 0 && tableSelectDes < tablasDes.size()) {
						seleccionCorrecta = true;
					} else {
						seleccionCorrecta = false;
					}

					input.nextLine(); // Consumir el carácter de nueva línea en el búfer
				}

				// CREAMOS LA ESTRUCTURA PARA OBTENER LOS CAMPOS DE LA TABLA DE DESTINO
				CamposPorTabla camposPorTablaDes = new CamposPorTabla();
				ResultSet resultadoDestino = null;
				resultadoDestino = camposPorTablaDes.obtenerCampos2(conDestino, tablasDes.get(tableSelectDes));

				// IMPRIMIMOS LOS CAMPOS TANTO DE LA TABLA DE ORIGEN COMO DESTINO PARA
				// ORDENARLOS
				System.out.printf(
						"\nIngrese el valor de los campos de origen en orden para los campos de destino, separe con comas: \n");

				i = 0;
				System.out.printf("\nLos campos de la tabla destino %s son : \n", tablasDes.get(tableSelectDes));
				
				ResultSetMetaData metaDataDes = resultadoDestino.getMetaData();
				// Obtenemos el número total de columnas en el resultado
				int columnCountDes = metaDataDes.getColumnCount();

				if (columnCountDes > 0) {

					for (int l = 1; l <= columnCountDes; l++) {

						System.out.printf("\t%s. %-30s  %-15s %d\n", l, metaDataDes.getColumnName(l),
						metaDataDes.getColumnTypeName(l), metaDataDes.getColumnDisplaySize(l));

					}

				} // IF COLUMNAS O CAMPOS ENCONTRADAS EN TABLA DE DESTINO

				
				System.out.printf("\nLos campos de origen  son : \n");

				for (String valor : valores) {
					int l = Integer.parseInt(valor);

					System.out.printf("\t%s. %-30s  %-15s %d\n", l, metaData.getColumnName(l),
							metaData.getColumnTypeName(l), metaData.getColumnDisplaySize(l));

				}

				String orden;
				orden = input.nextLine();

				String[] valoresOrden = orden.split(",");

				//VERIFICAR EL VALOR DE LOS VALORES INGRESADOS AQUI
				/*for (int j = 0; j < valoresOrden.length; j++) {
					int index = Integer.parseInt(valoresOrden[j]);

					
				}*/

				// Tengo el orden ahora necesito ingresarlos en la tabla
				IngresarDatosDestino ingresarDatosDestino = new IngresarDatosDestino();

				
				
				
					
					int cantInsert = ingresarDatosDestino.ingresarRegistros2( conDestino, resultadoOrigen,
					valoresOrden, tablasDes.get(tableSelectDes));
					System.out.printf("La cantidad de registros ingresados fueron : %d\n", cantInsert);
				
				

				// CERRAR CONEXION DE ORIGEN y DESTINO
				conexion.closeConnection(conOrigen);
				// conexion2.closeConnection(conDestino);

				System.out.println("╭───────────────────╮");
				System.out.println("│                   │");
				System.out.println("│  Menú de opciones │");
				System.out.println("│                   │");
				System.out.println("│  1. Seguir Creando│");
				System.out.println("│  2. Salir         │");
				System.out.println("╰───────────────────╯");

				seleccionCorrecta = false;
				int opcionEtl = 0;
				while (!seleccionCorrecta) {
					opcionEtl = input.nextInt();

					if (opcionEtl == 1) {
						crear = true;
						seleccionCorrecta = true;

					} else if (opcionEtl == 2) {
						crear = false;
						seleccionCorrecta = true;
					} else {
						seleccionCorrecta = false;
					}
				}

			} // WHILE DE CREACION ETL

		}
}

}
