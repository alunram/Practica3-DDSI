package com.bandadelpatio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
//import java.sql.Savepoint;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.ArrayList;

import java.util.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class App {
    public static void main(String[] args) {
        Connection conexion;
        Scanner entrada = new Scanner(System.in);
        entrada.useDelimiter("\n");
        Statement sentencia;
        ResultSet rs;
        int opcion;
        boolean salir = false;

        try {
            // Se carga el driver JDBC
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            // Nombre del servidor
            String nombre_servidor = "oracle0.ugr.es";
            // Numero del puerto
            String numero_puerto = "1521";
            // SID
            String sid = "practbd.oracle0.ugr.es";
            // URL "jdbc:oracle:thin:@nombreServidor:numeroPuerto:SID"
            String url = "jdbc:oracle:thin:@" + nombre_servidor + ":" + numero_puerto + "/" + sid;

            // Nombre usuario y password
            String usuario = "x7147851";
            String password = "x7147851";

            // Obtiene la conexion
            conexion = DriverManager.getConnection(url, usuario, password);
            conexion.setAutoCommit(false);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

        while (!salir) {
            try {
                System.out.println("\n---------------------------------------------------");
                System.out.println("SISTEMA DE INFORMACIÓN DEL COLEGIO BANDA DEL PATIO");
                System.out.println("---------------------------------------------------");
                System.out.println("Subsistemas:");
                System.out.println("1. Asignaturas");
                System.out.println("2. Espacios físicos");
                System.out.println("3. Alumnos");
                System.out.println("4. Personal docente");
                System.out.println("5. Actividades extraescolares");
                System.out.println("6. Materiales");
                System.out.println("---------------------------------------------------\n");
                System.out.print("¿A qué subsistema quiere acceder? (0 para terminar): ");

                while (true) {
                    try {
                        opcion = entrada.nextInt();
                        break;
                    } catch (Exception e) {
                        System.out.println("Error: Introduzca un número");
                        entrada.nextLine();
                    }
                }

                switch (opcion) {
                    //////////////////////////////////////////////////////////
                    // SUBSISTEMA ASIGNATURAS - GERMÁN
                    //////////////////////////////////////////////////////////
                    case 1:
                        System.out.println("\n---------------------------------------------------");
                        System.out.println("SUBSISTEMA DE ASIGNATURAS");
                        System.out.println("---------------------------------------------------");
                        System.out.println("Funcionalidades: ");
                        System.out.println("1. Añadir asignatura");
                        System.out.println("2. Eliminar asignatura");
                        System.out.println("3. Consultar información sobre la asignatura");
                        System.out.println("4. Consultar alumnos en la asignatura");
                        System.out.println("5. Asignar profesor a una asignatura");
                        System.out.println("---------------------------------------------------\n");
                        System.out.print("¿Qué funcionalidad desea?: ");

                        while (true) {
                            try {
                                opcion = entrada.nextInt();
                                break;
                            } catch (Exception e) {
                                System.out.println("Error: Introduzca un número");
                                entrada.nextLine();
                            }
                        }

                        switch (opcion) {
                            // Añadir asignatura
                            case 1:
                                System.out.println("Introduzca el nombre de la asignatura: ");
                                String nombre = entrada.next();
                                System.out.println("Introduzca el curso de la asignatura: ");
                                int curso;
                                while (true) {
                                    try {
                                        curso = entrada.nextInt();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("Error: Introduzca un número");
                                        entrada.nextLine();
                                    }
                                }

                                System.out.println("Introduzca el departamento de la asignatura: ");
                                String departamento = entrada.next();

                                String sql1 = "SELECT COUNT(*) FROM NombreDepar WHERE Departamento = ? AND Nombre_Asig = ?";

                                try (PreparedStatement pstmt = conexion.prepareStatement(sql1)) {
                                    pstmt.setString(1, departamento);
                                    pstmt.setString(2, nombre);
                                    ResultSet rs1 = pstmt.executeQuery();

                                    if (rs1.next() && rs1.getInt(1) > 0) {
                                        System.out.println("La asignatura ya estaba enlazada con ese departamento");
                                    } else {
                                        // insertar departamento en la tabla NombreDepar
                                        String sql = "INSERT INTO NombreDepar VALUES (?, ?)";
                                        try (PreparedStatement pstmt1 = conexion.prepareStatement(sql)) {
                                            pstmt1.setString(2, departamento);
                                            pstmt1.setString(1, nombre);
                                            pstmt1.executeUpdate();
                                        } catch (SQLException e) {
                                            // maneja el error
                                            System.out.println("Se ha producido un error: " + e.getMessage());
                                            System.out.println("Código de error: " + e.getErrorCode());
                                        }
                                    }
                                } catch (SQLException e) {
                                    // maneja el error
                                    System.out.println("Se ha producido un error: " + e.getMessage());
                                    System.out.println("Código de error: " + e.getErrorCode());
                                }

                                sql1 = "INSERT INTO Asignatura VALUES (?, ?)";
                                try (PreparedStatement pstmt = conexion.prepareStatement(sql1)) {
                                    pstmt.setInt(1, curso);
                                    pstmt.setString(2, nombre);
                                    pstmt.executeUpdate();
                                } catch (SQLException e) {
                                    // maneja el error
                                    System.out.println("Se ha producido un error: " + e.getMessage());
                                    System.out.println("Código de error: " + e.getErrorCode());
                                }

                                conexion.commit();

                                break;
                            case 2:

                                System.out.println("Introduzca el nombre de la asignatura a eliminar: ");
                                String nombreborrar = entrada.next();
                                System.out.println("Introduzca el curso de la asignatura a eliminar: ");
                                int cursoborrar;
                                while (true) {
                                    try {
                                        cursoborrar = entrada.nextInt();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("Error: Introduzca un número");
                                        entrada.nextLine();
                                    }
                                }

                                String sql2 = "DELETE FROM Asignatura WHERE Curso_Asig = ? AND Nombre_Asig = ?";

                                try (PreparedStatement pstmt = conexion.prepareStatement(sql2)) {
                                    pstmt.setInt(1, cursoborrar);
                                    pstmt.setString(2, nombreborrar);
                                    pstmt.executeUpdate();
                                } catch (SQLException e) {
                                    // maneja el error
                                    System.out.println("Se ha producido un error: " + e.getMessage());
                                    System.out.println("Código de error: " + e.getErrorCode());
                                }

                                String sqlAsignatura1 = "SELECT COUNT(*) FROM Asignatura WHERE Nombre_Asig = ?";
                                boolean borrarDepartamento = false;

                                try (PreparedStatement pstmt = conexion.prepareStatement(sqlAsignatura1)) {
                                    pstmt.setString(1, nombreborrar);
                                    ResultSet rs1 = pstmt.executeQuery();

                                    if (rs1.next() && rs1.getInt(1) == 0) {
                                        borrarDepartamento = true;
                                        System.out.println(
                                                "Se han borrado todas las asignaturas con ese nombre por lo que se eliminará del departamento");
                                    }
                                } catch (SQLException e) {
                                    // maneja el error
                                    System.out.println("Se ha producido un error: " + e.getMessage());
                                    System.out.println("Código de error: " + e.getErrorCode());
                                }

                                if (borrarDepartamento) {
                                    sql2 = "DELETE FROM NombreDepar WHERE Nombre_Asig = ?";

                                    try (PreparedStatement pstmt = conexion.prepareStatement(sql2)) {
                                        pstmt.setString(1, nombreborrar);
                                        pstmt.executeUpdate();
                                    } catch (SQLException e) {
                                        // maneja el error
                                        System.out.println("Se ha producido un error: " + e.getMessage());
                                        System.out.println("Código de error: " + e.getErrorCode());
                                    }
                                }

                                conexion.commit();

                                break;
                            // Consultar información sobre la asignatura
                            case 3:

                                // Comprobar que existe la asignatura
                                System.out.println("Introduzca el nombre de la asignatura: ");
                                String nombreconsulta = entrada.next();
                                System.out.println("Introduzca el curso de la asignatura: ");
                                int cursoconsulta;
                                while (true) {
                                    try {
                                        cursoconsulta = entrada.nextInt();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("Error: Introduzca un número");
                                        entrada.nextLine();
                                    }
                                }

                                boolean existe = true;
                                String sql3 = "SELECT * FROM Asignatura WHERE Curso_Asig = ? AND Nombre_Asig = ?";
                                try (PreparedStatement pstmt = conexion.prepareStatement(sql3)) {
                                    pstmt.setInt(1, cursoconsulta);
                                    pstmt.setString(2, nombreconsulta);
                                    ResultSet rs2 = pstmt.executeQuery();
                                    if (rs2.next()) {
                                        System.out.println("Nombre: " + rs2.getString("Nombre_Asig"));
                                        System.out.println("Curso: " + rs2.getInt("Curso_Asig"));
                                    } else {

                                        System.out.println("No existe la asignatura");
                                        existe = false;
                                    }
                                } catch (SQLException e) {
                                    // maneja el error
                                    System.out.println("Se ha producido un error: " + e.getMessage());
                                    System.out.println("Código de error: " + e.getErrorCode());
                                }

                                if (!existe)
                                    break;

                                sql3 = "SELECT * FROM NombreDepar WHERE Nombre_Asig = ?";
                                try (PreparedStatement pstmt = conexion.prepareStatement(sql3)) {
                                    pstmt.setString(1, nombreconsulta);
                                    ResultSet rs2 = pstmt.executeQuery();
                                    if (rs2.next()) {
                                        System.out.println("Departamento: " + rs2.getString("Departamento"));
                                    }
                                } catch (SQLException e) {
                                    // maneja el error
                                    System.out.println("Se ha producido un error: " + e.getMessage());
                                    System.out.println("Código de error: " + e.getErrorCode());
                                }

                                sql3 = "SELECT * FROM Ensenar WHERE Nombre_Asig = ? AND Curso_Asig = ?";

                                String dniper = "";
                                boolean hayprof = true;
                                try (PreparedStatement pstmt = conexion.prepareStatement(sql3)) {
                                    pstmt.setString(1, nombreconsulta);
                                    pstmt.setInt(2, cursoconsulta);
                                    ResultSet rs2 = pstmt.executeQuery();
                                    if (rs2.next()) {
                                        dniper = rs2.getString("DNI_Per");
                                        System.out.println("DNI Personal Docente: " + dniper);
                                    } else {
                                        System.out.println("No hay profesor asignado");
                                        hayprof = false;
                                    }
                                } catch (SQLException e) {
                                    // maneja el error
                                    System.out.println("Se ha producido un error: " + e.getMessage());
                                    System.out.println("Código de error: " + e.getErrorCode());
                                }

                                sql3 = "SELECT * FROM PersonalDocente WHERE DNI_Per = ?";
                                if (hayprof) {
                                    try (PreparedStatement pstmt = conexion.prepareStatement(sql3)) {
                                        pstmt.setString(1, dniper);
                                        ResultSet rs2 = pstmt.executeQuery();
                                        if (rs2.next()) {
                                            System.out.println("Nombre: " + rs2.getString("Nombre"));
                                            System.out.println("Apellidos: " + rs2.getString("Apellidos"));
                                            System.out.println(" ");
                                        }
                                    } catch (SQLException e) {
                                        // maneja el error
                                        System.out.println("Se ha producido un error: " + e.getMessage());
                                        System.out.println("Código de error: " + e.getErrorCode());
                                    }
                                }

                                break;

                            // Consultar Alumnos en la Asignatura
                            case 4:

                                System.out.println("Introduzca el nombre de la asignatura: ");
                                String nombreconsulta2 = entrada.next();
                                System.out.println("Introduzca el curso de la asignatura: ");
                                int cursoconsulta2;
                                while (true) {
                                    try {
                                        cursoconsulta2 = entrada.nextInt();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("Error: Introduzca un número");
                                        entrada.nextLine();
                                    }
                                }

                                String sql4 = "SELECT COUNT(*) FROM Asignatura WHERE Curso_Asig = ? AND Nombre_Asig = ?";
                                boolean error2 = false;
                                try (PreparedStatement pstmt = conexion.prepareStatement(sql4)) {
                                    pstmt.setInt(1, cursoconsulta2);
                                    pstmt.setString(2, nombreconsulta2);
                                    ResultSet rs5 = pstmt.executeQuery();
                                    if (!rs5.next() || rs5.getInt(1) == 0) {
                                        error2 = true;
                                        throw new SQLException("La asignatura no existe");
                                    }
                                } catch (SQLException e) {
                                    // maneja el error
                                    System.out.println("Se ha producido un error: " + e.getMessage());
                                    System.out.println("Código de error: " + e.getErrorCode());
                                }

                                if (error2)
                                    break;

                                sql4 = "SELECT Alumnos.DNI_alum, Alumnos.Nombre, Alumnos.Apellidos " +
                                        "FROM Alumnos JOIN Pertenece ON Alumnos.DNI_alum = Pertenece.DNI_alum " +
                                        "JOIN Asignatura ON Pertenece.Curso_Asig = Asignatura.Curso_Asig AND Pertenece.Nombre_Asig = Asignatura.Nombre_Asig "
                                        +
                                        "WHERE Asignatura.Curso_Asig = ? AND Asignatura.Nombre_Asig = ?";

                                System.out.println("Alumnos en la asignatura " + nombreconsulta2 + " del curso "
                                        + cursoconsulta2 + " :");

                                try (PreparedStatement pstmt = conexion.prepareStatement(sql4)) {
                                    pstmt.setInt(1, cursoconsulta2);
                                    pstmt.setString(2, nombreconsulta2);
                                    try (ResultSet rs4 = pstmt.executeQuery()) {
                                        while (rs4.next()) {
                                            String dnialum = rs4.getString("DNI_alum");
                                            String nombrealum = rs4.getString("Nombre");
                                            String apellidosalum = rs4.getString("Apellidos");
                                            System.out.println(
                                                    "DNI: " + dnialum + ", Nombre: " + nombrealum + ", Apellidos: "
                                                            + apellidosalum);
                                        }
                                    }
                                } catch (SQLException e) {
                                    // maneja el error
                                    System.out.println("Se ha producido un error: " + e.getMessage());
                                    System.out.println("Código de error: " + e.getErrorCode());
                                }
                                break;

                            // Asignar Profesor a la Asignatura
                            case 5:
                                boolean error = false;
                                System.out.println("Introduzca el nombre de la asignatura: ");
                                String nombreconsulta3 = entrada.next();
                                System.out.println("Introduzca el curso de la asignatura: ");
                                int cursoconsulta3;
                                while (true) {
                                    try {
                                        cursoconsulta3 = entrada.nextInt();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("Error: Introduzca un número");
                                        entrada.nextLine();
                                    }
                                }
                                System.out.println("Introduzca el DNI del Personal Docente: ");
                                String dnipersonal = entrada.next();

                                String sqlAsignatura = "SELECT COUNT(*) FROM Asignatura WHERE Curso_Asig = ? AND Nombre_Asig = ?";
                                String sqlDocente = "SELECT COUNT(*) FROM PersonalDocente WHERE DNI_Per = ?";

                                try (PreparedStatement pstmt = conexion.prepareStatement(sqlAsignatura)) {
                                    pstmt.setInt(1, cursoconsulta3);
                                    pstmt.setString(2, nombreconsulta3);
                                    ResultSet rs5 = pstmt.executeQuery();
                                    if (!rs5.next() || rs5.getInt(1) == 0) {
                                        error = true;
                                        throw new SQLException("La asignatura no existe");
                                    }
                                } catch (SQLException e) {
                                    // maneja el error
                                    System.out.println("Se ha producido un error: " + e.getMessage());
                                    System.out.println("Código de error: " + e.getErrorCode());
                                }

                                try (PreparedStatement pstmt = conexion.prepareStatement(sqlDocente)) {
                                    pstmt.setString(1, dnipersonal);
                                    ResultSet rs5 = pstmt.executeQuery();
                                    if (!rs5.next() || rs5.getInt(1) == 0) {
                                        error = true;
                                        throw new SQLException("El Personal Docente no existe");
                                    }
                                } catch (SQLException e) {
                                    // maneja el error
                                    System.out.println("Se ha producido un error: " + e.getMessage());
                                    System.out.println("Código de error: " + e.getErrorCode());
                                }

                                if (!error) {
                                    String sql5 = "INSERT INTO Ensenar (Curso_Asig, Nombre_Asig, DNI_Per) VALUES (?, ?, ?)";

                                    try (PreparedStatement pstmt = conexion.prepareStatement(sql5)) {
                                        pstmt.setInt(1, cursoconsulta3);
                                        pstmt.setString(2, nombreconsulta3);
                                        pstmt.setString(3, dnipersonal);
                                        pstmt.executeUpdate();

                                        System.out.println("Se ha asignado el Personal Docente a la Asignatura");
                                    } catch (SQLException e) {
                                        // maneja el error
                                        System.out.println("Se ha producido un error: " + e.getMessage());
                                        System.out.println("Código de error: " + e.getErrorCode());
                                    }

                                    conexion.commit();
                                }

                                break;

                        }
                        ;

                        break;

                    //////////////////////////////////////////////////////////
                    // SUBSISTEMA ESPACIOS FÍSICOS - NEREA
                    //////////////////////////////////////////////////////////
                    case 2:
                        System.out.println("\n---------------------------------------------------");
                        System.out.println("SUBSISTEMA DE ESPACIOS FÍSICOS");
                        System.out.println("---------------------------------------------------");
                        System.out.println("Funcionalidades: ");
                        System.out.println("1. Añadir espacio físico");
                        System.out.println("2. Reservar aula para asignatura");
                        System.out.println("3. Eliminar reserva de aula");
                        System.out.println("4. Consultar aulas disponibles");
                        System.out.println("5. Consultar asignaciones de aulas");
                        System.out.println("6. Reservar aula para actividad extraescolar");
                        System.out.println("---------------------------------------------------\n");
                        System.out.print("¿Qué funcionalidad desea?: ");

                        while (true) {
                            try {
                                opcion = entrada.nextInt();
                                break;
                            } catch (Exception e) {
                                System.out.println("Error: Introduzca un número");
                                entrada.nextLine();
                            }
                        }

                        section02_processRequest(conexion, entrada, opcion);

                        break;

                    //////////////////////////////////////////////////////////
                    // SUBSISTEMA ALUMNOS - ÁLVARO
                    //////////////////////////////////////////////////////////
                    case 3:
                        System.out.println("\n---------------------------------------------------");
                        System.out.println("SUBSISTEMA DE ALUMNOS");
                        System.out.println("---------------------------------------------------");
                        System.out.println("Funcionalidades: ");
                        System.out.println("1. Añadir alumno");
                        System.out.println("2. Modificar alumno");
                        System.out.println("3. Eliminar alumno");
                        System.out.println("4. Consultar alumno");
                        System.out.println("5. Mostrar el listado de alumnos por curso");
                        System.out.println("---------------------------------------------------\n");
                        System.out.print("¿Qué funcionalidad desea?: ");

                        while (true) {
                            try {
                                opcion = entrada.nextInt();
                                break;
                            } catch (Exception e) {
                                System.out.println("Error: Introduzca un número");
                                entrada.nextLine();
                            }
                        }

                        switch (opcion) {
                            case 1:
                                // añadir alumnos. aqui funciona el disparador
                                // se piden: DNI, Nombre, Apellidos, Telefono_contacto, fecha_nac, curso, datos_tutor, domicilio
                                System.out.print("Ha elegido la opción de añadir un nuevo alumno: ");

                                sentencia = conexion.createStatement();

                                // Pedir DNI_Alum:
                                String dnialumno = "";
                                boolean correcto = false;
                                entrada.useDelimiter("\n");

                                while (!correcto) {
                                    System.out.print("Introducir el DNI del nuevo alumno (9 caracteres): ");
                                    dnialumno = entrada.next();

                                    // comprobamos que no hay un alumno con ese dni (y de paso veo que tiene 9 caracteres
                                    rs = sentencia.executeQuery("SELECT COUNT(*) FROM Alumnos WHERE DNI_alum = '" + dnialumno + "'");
                                    rs.next();
                                    int numalumnosdni = rs.getInt(1);

                                    if (dnialumno.length() != 9) {
                                        System.out.print("Formato incorrecto. \n");
                                    } else if (numalumnosdni == 0) {
                                        correcto = true;
                                    } else
                                        System.out.print("DNI no valido: Ya existe un alumno con ese DNI. \n");
                                }

                                // Pedir Nombre (solo una palabra):
                                String nombre_alumno = "";
                                correcto = false;

                                while (!correcto) {
                                    System.out.print("Introducir el nombre del nuevo alumno (maximo 20 caracteres): ");
                                    nombre_alumno = entrada.next();

                                    // comprobamos que tiene menos de 21 caracteres:
                                    if (nombre_alumno.length() < 21) {
                                        correcto = true;
                                    } else
                                        System.out.print("Formato incorrecto. \n");
                                }

                                // Pedir Apellidos:
                                String apellidos_alumno = "";
                                correcto = false;

                                String primer_apellido_alumno = "";
                                while (!correcto) {
                                    System.out.print("Introducir el primer apellido del nuevo alumno (maximo 20 caracteres): ");
                                    primer_apellido_alumno = entrada.next();

                                    // comprobamos que tiene menos de 21 caracteres:
                                    if (primer_apellido_alumno.length() < 21) {
                                        correcto = true;
                                    } else
                                        System.out.print("Formato incorrecto. \n");
                                }

                                correcto = false;

                                String segundo_apellido_alumno = "";
                                while (!correcto) {
                                    System.out.print("Introducir el segundo apellido del nuevo alumno (maximo 20 caracteres): ");
                                    segundo_apellido_alumno = entrada.next();

                                    // comprobamos que tiene menos de 21 caracteres:
                                    if (segundo_apellido_alumno.length() < 21) {
                                        correcto = true;
                                    } else
                                        System.out.print("Formato incorrecto. \n");
                                }

                                apellidos_alumno = primer_apellido_alumno + " " + segundo_apellido_alumno;

                                // Pedir Telefono_contacto:
                                String telefono_alumno = "";
                                correcto = false;

                                while (!correcto) {
                                    System.out.print("Introducir el telefono de contacto del nuevo alumno (12 caracteres): ");
                                    telefono_alumno = entrada.next();

                                    // comprobamos que tiene 12 caracteres:
                                    if (telefono_alumno.length() == 12) {
                                        correcto = true;
                                    } else
                                        System.out.print("Formato del telefono incorrecto. \n");
                                }

                                // Pedir Fecha_nac:
                                String fecha_nac_alumno = "";
                                // compruebo que la fecha este bien del mismo modo que en el seminario1
                                boolean fecha_incorrecta = true;
                                while (fecha_incorrecta) {
                                    fecha_incorrecta = false;
                                    System.out.print("Introduce la fecha de nacimiento del alumno (formato yyyy-MM-dd): ");
                                    fecha_nac_alumno = entrada.next();
                                    System.out.println("fecha: " + fecha_nac_alumno);

                                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    sdf.setLenient(false); // lenient = tolerante (lo ponemos a false para que dé error
                                    // si el formato no es correcto o si no es válida la fecha)
                                    try {
                                        sdf.parse(fecha_nac_alumno);
                                    } catch (Exception e) {
                                        System.out.println("Error: Formato de fecha incorrecto o fecha inválida. ");
                                        fecha_incorrecta = true;
                                    }
                                }

                                // Pedir Curso_alum:
                                int curso_alumno = 0;
                                correcto = false;

                                /*el disparador es:
                                CREATE OR REPLACE TRIGGER cursoalumnos BEFORE INSERT ON Alumnos FOR EACH ROW DECLARE sumacurso INTEGER; BEGIN SELECT count(*) INTO sumacurso FROM Alumnos WHERE Curso_alum = :new.Curso_alum; IF (sumacurso > 29) THEN raise_application_error(-20600, :new.Curso_alum || ' no puede haber mas de 30 alumnos en un curso'); END IF; END;
                                */
                                while (!correcto) {
                                    System.out.print("Introducir el curso del nuevo alumno (entre 1 y 6): ");
                                    curso_alumno = entrada.nextInt();

                                    // comprobamos que esta entre 1 y 6:
                                    if ((curso_alumno < 7) && (curso_alumno > 0)) {
                                        correcto = true;
                                    } else
                                        System.out.print("Curso incorrecto. \n");
                                }

                                // Pedir Nombre_apellidos_tutor:
                                String nombre_apellidos_tutor_alumno = "";
                                String nombre_tutor_alumno = "";
                                String primer_apellido_tutor_alumno = "";
                                String segundo_apellido_tutor_alumno = "";
                                correcto = false;

                                while (!correcto) {
                                    System.out.print("Introducir el nombre  del tutor del nuevo alumno (maximo 20 caracteres): ");
                                    nombre_tutor_alumno = entrada.next();

                                    // comprobamos que tiene menos de 21 caracteres:
                                    if (nombre_tutor_alumno.length() < 21) {
                                        correcto = true;
                                    } else
                                        System.out.print("Formato incorrecto. \n");
                                }

                                correcto = false;
                                while (!correcto) {
                                    System.out.print("Introducir el primer apellido del tutor del nuevo alumno (maximo 20 caracteres): ");
                                    primer_apellido_tutor_alumno = entrada.next();

                                    // comprobamos que tiene menos de 21 caracteres:
                                    if (primer_apellido_tutor_alumno.length() < 21) {
                                        correcto = true;
                                    } else
                                        System.out.print("Formato incorrecto \n");
                                }

                                correcto = false;
                                while (!correcto) {
                                    System.out.print("Introducir el segundo apellido del tutor del nuevo alumno (maximo 20 caracteres): ");
                                    segundo_apellido_tutor_alumno = entrada.next();

                                    // comprobamos que tiene menos de 21 caracteres:
                                    if (segundo_apellido_tutor_alumno.length() < 21) {
                                        correcto = true;
                                    } else
                                        System.out.print("Formato incorrecto \n");
                                }

                                nombre_apellidos_tutor_alumno = nombre_tutor_alumno + " " + primer_apellido_tutor_alumno + " " + segundo_apellido_tutor_alumno;

                                // Pedir Domicilio:
                                String domicilio_alumno = "";
                                correcto = false;

                                while (!correcto) {
                                    System.out.print("Introducir el domicilio del nuevo alumno (maximo 40 caracteres): ");
                                    domicilio_alumno = entrada.next();

                                    // comprobamos que tiene menos de 41 caracteres:
                                    if (domicilio_alumno.length() < 41) {
                                        correcto = true;
                                    } else
                                        System.out.print("Formato incorrecto \n");
                                }

                                // se añade el alumno:
                                try {
                                    sentencia.executeQuery("INSERT INTO Alumnos VALUES('" + dnialumno + "', '" + nombre_alumno + "', '" + apellidos_alumno + "', '" + telefono_alumno + "', to_date('" + fecha_nac_alumno + "','yyyy-mm-dd')" + ", '" + curso_alumno + "', '" + nombre_apellidos_tutor_alumno + "', '" + domicilio_alumno + "')");
                                    sentencia.executeQuery("COMMIT");
                                    System.out.println("Alumno añadido correctamente");
                                    break;
                                } catch (SQLException e) {
                                    System.out.println("SE HA PRODUCIDO UN ERROR: " + e.getMessage());
                                    System.out.println("CODIGO DE ERROR: " + e.getErrorCode());
                                }

                                entrada.reset();

                                break;

                            case 2:
                                // modificar alumnos. 
                                // primero pide el dni
                                // despues pide el atributo que quieres cambiar
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Ha elegido la opción de modificar un alumno.");
                                System.out.println("1. Modificar nombre");
                                System.out.println("2. Modificar apellido");
                                System.out.println("3. Modificar telefono de contacto");
                                System.out.println("4. Modificar fecha de nacimiento");
                                System.out.println("5. Modificar curso");
                                System.out.println("6. Modificar nombre y apellidos del tutor");
                                System.out.println("7. Modificar domicilio");
                                System.out.println("---------------------------------------------------\n");
                                System.out.print("¿Qué funcionalidad desea?: ");

                                sentencia = conexion.createStatement();

                                while (true) {
                                    try {
                                        opcion = entrada.nextInt();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("Error: Introduzca un número");
                                        entrada.nextLine();
                                    }
                                }

                                // pedimos el dni del alumno que queremos modificar
                                String dnialumnomod = "";
                                boolean correcto2 = false;
                                entrada.useDelimiter("\n");

                                while (!correcto2) {
                                    System.out.print(
                                            "Introducir el DNI del alumno que se desea modificar(9 caracteres): ");
                                    dnialumnomod = entrada.next();

                                    // comprobamos que hay un alumno con ese dni (y de paso veo que tiene 9
                                    // caracteres):
                                    rs = sentencia.executeQuery("SELECT COUNT(*) FROM Alumnos WHERE DNI_alum = '" + dnialumnomod + "'");
                                    rs.next();
                                    int numalumnosdni = rs.getInt(1);

                                    if ((dnialumnomod.length() == 9) && (numalumnosdni == 1)) {
                                        correcto2 = true;
                                    } else
                                        System.out.println("DNI no valido.");
                                }

                                switch (opcion) {
                                    case 1:
                                        // opcion nombre. solo puede estar formado por una palabra.
                                        String nuevo_nombre_alumno = "";
                                        correcto2 = false;

                                        while (!correcto2) {
                                            System.out.print("Introducir el nuevo nombre del alumno (maximo 20 caracteres): ");
                                            nuevo_nombre_alumno = entrada.next();

                                            // comprobamos que tiene menos de 21 caracteres:
                                            if (nuevo_nombre_alumno.length() < 21) {
                                                correcto2 = true;
                                            } else
                                                System.out.print("Formato incorrecto \n");
                                        }

                                        // modificamos el nombre:
                                        sentencia.executeQuery("UPDATE Alumnos SET Nombre = '" + nuevo_nombre_alumno + "' WHERE DNI_alum = '" + dnialumnomod + "'");

                                        System.out.println("Nombre actualizado.");

                                        break;

                                    case 2:
                                        // opcion apellido
                                        String nuevos_apellidos_alumno = "";
                                        correcto2 = false;
                                        String nuevo_primer_apellido_alumno = "";

                                        while (!correcto2) {
                                            System.out.print("Introducir el nuevo primer apellido del alumno (maximo 20 caracteres): ");
                                            nuevo_primer_apellido_alumno = entrada.next();

                                            // comprobamos que tiene menos de 21 caracteres:
                                            if (nuevo_primer_apellido_alumno.length() < 21) {
                                                correcto2 = true;
                                            } else
                                                System.out.print("Formato incorrecto \n");
                                        }

                                        correcto2 = false;
                                        String nuevo_segundo_apellido_alumno = "";
                                        while (!correcto2) {
                                            System.out.print("Introducir el nuevo segundo apellido del alumno (maximo 20 caracteres): ");
                                            nuevo_segundo_apellido_alumno = entrada.next();

                                            // comprobamos que tiene menos de 21 caracteres:
                                            if (nuevo_segundo_apellido_alumno.length() < 21) {
                                                correcto2 = true;
                                            } else
                                                System.out.print("Formato incorrecto \n");
                                        }

                                        nuevos_apellidos_alumno = nuevo_primer_apellido_alumno + " " + nuevo_segundo_apellido_alumno;

                                        // modificamos el apellido:
                                        sentencia.executeQuery("UPDATE Alumnos SET Apellidos = '" + nuevos_apellidos_alumno + "' WHERE DNI_alum = '" + dnialumnomod + "'");

                                        System.out.println("Apellidos actualizados.");

                                        break;

                                    case 3:
                                        // opcion telefono de contacto
                                        String nuevo_telefono_alumno = "";
                                        correcto2 = false;

                                        while (!correcto2) {
                                            System.out.print("Introducir el telefono de contacto del nuevo alumno (12 caracteres): ");
                                            nuevo_telefono_alumno = entrada.next();

                                            // comprobamos que tiene 12 caracteres:
                                            if (nuevo_telefono_alumno.length() == 12) {
                                                correcto2 = true;
                                            } else
                                                System.out.print("Formato del telefono incorrecto. \n");
                                        }

                                        // modificamos el telefono:
                                        sentencia.executeQuery("UPDATE Alumnos SET Telefono_contacto = '" + nuevo_telefono_alumno + "' WHERE DNI_alum = '" + dnialumnomod + "'");

                                        System.out.println("Telefono actualizado.");

                                        break;

                                    case 4:
                                        // opcion fecha de nacimiento
                                        String nueva_fecha_nac_alumno = "";
                                        // compruebo que la fecha este bien del mismo modo que en el seminario1
                                        boolean fecha_incorrecta2 = true;
                                        while (fecha_incorrecta2) {
                                            fecha_incorrecta2 = false;
                                            System.out.print("Introduce la fecha (formato yyyy-MM-dd): ");
                                            nueva_fecha_nac_alumno = entrada.next();
                                            System.out.println("fecha: " + nueva_fecha_nac_alumno);

                                            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                            sdf.setLenient(false); // lenient = tolerante (lo ponemos a false para que
                                            // dé error si el formato no es correcto o si no es
                                            // válida la fecha)
                                            try {
                                                sdf.parse(nueva_fecha_nac_alumno);
                                            } catch (Exception e) {
                                                System.out
                                                        .println("Error: Formato de fecha incorrecto o fecha inválida.");
                                                fecha_incorrecta2 = true;
                                            }
                                        }

                                        // modificamos la fecha de nacimiento:
                                        sentencia.executeQuery("UPDATE Alumnos SET Fecha_nac = " + "to_date('" + nueva_fecha_nac_alumno + "','yyyy-mm-dd')" + " WHERE DNI_alum = '" + dnialumnomod + "'");

                                        System.out.println("Fecha de nacimiento actualizada.");

                                        break;
                                    //"to_date('" + nueva_fecha_nac_alumno + "','yyyy-mm-dd')"

                                    case 5:
                                        // opcion curso
                                        int nuevo_curso_alumno = 0;
                                        correcto2 = false;

                                        while (!correcto2) {
                                            System.out.print("Introducir el curso nuevo del alumno (entre 1 y 6): ");
                                            nuevo_curso_alumno = entrada.nextInt();

                                            // comprobamos que esta entre 1 y 6:
                                            if ((nuevo_curso_alumno < 7) && (nuevo_curso_alumno > 0)) {
                                                correcto2 = true;
                                            } else
                                                System.out.print("Curso incorrecto. \n");
                                        }

                                        // modificamos el curso:
                                        sentencia.executeQuery("UPDATE Alumnos SET Curso_alum = '" + nuevo_curso_alumno + "' WHERE DNI_alum = '" + dnialumnomod + "'");

                                        System.out.println("Curso actualizado.");

                                        break;

                                    case 6:
                                        // opcion datos tutor
                                        String nuevos_nombre_apellidos_tutor_alumno = "";
                                        String nuevo_nombre_tutor_alumno = "";
                                        String nuevo_primer_apellido_tutor_alumno = "";
                                        String nuevo_segundo_apellido_tutor_alumno = "";
                                        correcto2 = false;

                                        while (!correcto2) {
                                            System.out.print("Introducir el nuevo nombre del tutor del alumno (maximo 20 caracteres): ");
                                            nuevo_nombre_tutor_alumno = entrada.next();

                                            // comprobamos que tiene menos de 21 caracteres:
                                            if (nuevo_nombre_tutor_alumno.length() < 21) {
                                                correcto2 = true;
                                            } else
                                                System.out.print("Formato incorrecto. \n");
                                        }

                                        correcto2 = false;
                                        while (!correcto2) {
                                            System.out.print("Introducir el nuevo primer apellido del tutor del alumno (maximo 20 caracteres): ");
                                            nuevo_primer_apellido_tutor_alumno = entrada.next();

                                            // comprobamos que tiene menos de 21 caracteres:
                                            if (nuevo_primer_apellido_tutor_alumno.length() < 21) {
                                                correcto2 = true;
                                            } else
                                                System.out.print("Formato incorrecto. \n");
                                        }

                                        correcto2 = false;
                                        while (!correcto2) {
                                            System.out.print("Introducir el nuevo segundo apellido del tutor del alumno (maximo 20 caracteres): ");
                                            nuevo_segundo_apellido_tutor_alumno = entrada.next();

                                            // comprobamos que tiene menos de 21 caracteres:
                                            if (nuevo_segundo_apellido_tutor_alumno.length() < 21) {
                                                correcto2 = true;
                                            } else
                                                System.out.print("Formato incorrecto. \n");
                                        }

                                        nuevos_nombre_apellidos_tutor_alumno = nuevo_nombre_tutor_alumno + " " + nuevo_primer_apellido_tutor_alumno + " " + nuevo_segundo_apellido_tutor_alumno;

                                        // modificamos los datos del tutor:
                                        sentencia.executeQuery("UPDATE Alumnos SET Nombre_apellidos_tutor = '" + nuevos_nombre_apellidos_tutor_alumno + "' WHERE DNI_alum = '" + dnialumnomod + "'");

                                        System.out.println("Datos del tutor actualizados.");

                                        break;

                                    case 7:
                                        // opcion domicilio
                                        String nuevo_domicilio_alumno = "";
                                        correcto2 = false;

                                        while (!correcto2) {
                                            System.out.print("Introducir el nuevo domicilio del alumno (maximo 40 caracteres): ");
                                            nuevo_domicilio_alumno = entrada.next();

                                            // comprobamos que tiene menos de 41 caracteres:
                                            if (nuevo_domicilio_alumno.length() < 41) {
                                                correcto2 = true;
                                            } else
                                                System.out.print("Formato incorrecto. \n");
                                        }

                                        // modificamos el domicilio:
                                        sentencia.executeQuery("UPDATE Alumnos SET Domicilio = '" + nuevo_domicilio_alumno + "' WHERE DNI_alum = '" + dnialumnomod + "'");

                                        System.out.println("Domicilio actualizado.");

                                        break;

                                    default:
                                        System.out.println("Opción no válida.");
                                        break;
                                }
                                ;

                                //commit para finalizar:
                                sentencia.executeQuery("COMMIT");
                                entrada.reset();

                                break;

                            case 3:
                                // eliminar alumnos
                                // pedir dni y borrar a continuacion (en cascada)
                                sentencia = conexion.createStatement();

                                // pedimos el dni del alumno que queremos eliminar
                                String dnialumnoelim = "";
                                boolean correcto3 = false;
                                entrada.useDelimiter("\n");

                                while (!correcto3) {
                                    System.out.print("Introducir el DNI del alumno que se desea eliminar(9 caracteres): ");
                                    dnialumnoelim = entrada.next();

                                    // comprobamos que hay un alumno con ese dni (y de paso veo que tiene 9 caracteres):
                                    rs = sentencia.executeQuery("SELECT COUNT(*) FROM Alumnos WHERE DNI_alum = '" + dnialumnoelim + "'");
                                    rs.next();
                                    int numalumnosdni = rs.getInt(1);

                                    if ((dnialumnoelim.length() == 9) && (numalumnosdni == 1)) {
                                        correcto3 = true;
                                    } else
                                        System.out.println("DNI no valido.");
                                }

                                // eliminamos el alumno:
                                sentencia.executeQuery("DELETE FROM Alumnos WHERE DNI_alum = '" + dnialumnoelim + "'");

                                sentencia.executeQuery("COMMIT");

                                System.out.println("Alumno borrado.");

                                entrada.reset();

                                break;

                            case 4:
                                // consultar alumno
                                // pedir dni y despues se muestran sus datos
                                sentencia = conexion.createStatement();

                                // pedimos el dni del alumno que queremos eliminar
                                String dnialumnoconsult = "";
                                boolean correcto4 = false;
                                entrada.useDelimiter("\n");

                                while (!correcto4) {
                                    System.out.print("Introducir el DNI del alumno que se desea consultar(9 caracteres): ");
                                    dnialumnoconsult = entrada.next();

                                    // comprobamos que hay un alumno con ese dni (y de paso veo que tiene 9 caracteres, aunque esto en realidad no haria falta):
                                    rs = sentencia.executeQuery("SELECT COUNT(*) FROM Alumnos WHERE DNI_alum = '" + dnialumnoconsult + "'");
                                    rs.next();
                                    int numalumnosdni = rs.getInt(1);

                                    if ((dnialumnoconsult.length() == 9) && (numalumnosdni == 1)) {
                                        correcto4 = true;
                                    } else
                                        System.out.println("DNI no valido.");
                                }

                                // lo buscamos y mostramos sus atributos como hicimos con las tablas del seminario1
                                ResultSet rsalumno;
                                rsalumno = sentencia.executeQuery("SELECT * FROM Alumnos WHERE DNI_alum = '" + dnialumnoconsult + "'");

                                ResultSetMetaData rsmd = rsalumno.getMetaData();
                                int numero_columnas = rsmd.getColumnCount();

                                // Imprimimos las columnas (deberian ser 8)
                                for (int i = 1; i <= numero_columnas; i++) {
                                    if (i > 1)
                                        System.out.print(", ");
                                    System.out.print(rsmd.getColumnName(i).toUpperCase());
                                }
                                System.out.println();

                                // Vamos iterando por las filas e imprimiéndolas (deberia haber solo una)
                                while (rsalumno.next()) {
                                    for (int i = 1; i <= numero_columnas; i++) {
                                        if (i > 1)
                                            System.out.print(", ");
                                        System.out.print(rsalumno.getString(i));
                                    }
                                    System.out.println();
                                }
                                rsalumno.close();

                                entrada.reset();

                                break;

                            case 5:
                                // mostrar listado de alumnos por curso
                                // 1.pedir un curso
                                // 2.mostrar los datos (nombre, apellidos y dni) de todos los alumnos con curso=cursointroducido

                                sentencia = conexion.createStatement();

                                int curso_list_alumno = 0;
                                boolean correcto5 = false;
                                entrada.useDelimiter("\n");

                                while (!correcto5) {
                                    System.out.print("Introducir el curso (entre 1 y 6) del que quieres ver los alumnos: ");
                                    curso_list_alumno = entrada.nextInt();

                                    // comprobamos que esta entre 1 y 6:
                                    if ((curso_list_alumno < 7) && (curso_list_alumno > 0)) {
                                        correcto5 = true;
                                    } else
                                        System.out.print("Curso incorrecto. \n");
                                }

                                // mostraremos el nombre, los apellidos y el DNI:
                                // lo buscamos y mostramos sus atributos como hicimos con las tablas del seminario1
                                ResultSet rsalumno2;
                                rsalumno2 = sentencia.executeQuery("SELECT DNI_alum, Nombre, Apellidos FROM Alumnos WHERE Curso_alum = " + curso_list_alumno + "");

                                ResultSetMetaData rsmd2 = rsalumno2.getMetaData();
                                int numero_columnasselect = rsmd2.getColumnCount();

                                // Imprimimos las columnas (deberian ser 3)
                                for (int i = 1; i <= numero_columnasselect; i++) {
                                    if (i > 1)
                                        System.out.print(", ");
                                    System.out.print(rsmd2.getColumnName(i).toUpperCase());
                                }
                                System.out.println();

                                // Vamos iterando por las filas e imprimiéndolas
                                while (rsalumno2.next()) {
                                    for (int i = 1; i <= numero_columnasselect; i++) {
                                        if (i > 1)
                                            System.out.print(", ");
                                        System.out.print(rsalumno2.getString(i));
                                    }
                                    System.out.println();
                                }
                                rsalumno2.close();

                                entrada.reset();

                                break;

                            default:
                                System.out.println("Opción no válida");

                                break;
                        }
                        ;

                        break;

                    //////////////////////////////////////////////////////////
                    // SUBSISTEMA PERSONAL DOCENTE - PEDRO
                    //////////////////////////////////////////////////////////
                    case 4:
                        System.out.println("\n---------------------------------------------------");
                        System.out.println("SUBSISTEMA DE PERSONAL DOCENTE");
                        System.out.println("---------------------------------------------------");
                        System.out.println("Funcionalidades: ");
                        System.out.println("1. Dar de alta personal docente");
                        System.out.println("2. Dar de baja personal docente");
                        System.out.println("3. Solicitar tutoría");
                        System.out.println("4. Consulta por filtro");
                        System.out.println("5. Registrar asistencia");
                        System.out.println("---------------------------------------------------\n");
                        System.out.print("¿Qué funcionalidad desea?: ");

                        while (true) {
                            try {
                                opcion = entrada.nextInt();
                                break;
                            } catch (Exception e) {
                                System.out.println("Error: Introduzca un número");
                                entrada.nextLine();
                            }
                        }

                        switch (opcion) {
                            // Dar de alta personal docente
                            case 1:
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Dar de alta personal docente");
                                System.out.println("---------------------------------------------------");

                                // Pedimos los datos del personal docente
                                System.out.println("Introduzca los datos de entrada del nuevo personal docente");
                                System.out.print("\t- DNI (9 caracteres máximo): ");
                                String dni = entrada.next();
                                System.out.print("\t- Nombre (20 caracteres máximo): ");
                                String nombre = entrada.next();
                                System.out.print("\t- Apellidos (40 caracteres máximo): ");
                                entrada.nextLine();
                                String apellidos = entrada.nextLine();
                                System.out.print("\t- Teléfono (12 caracteres máximo): ");
                                String telefono = entrada.next();
                                System.out.print("\t- Domicilio (40 caracteres máximo): ");
                                entrada.nextLine();
                                String domicilio = entrada.nextLine();
                                System.out.print("\t- Departamento (20 caracteres máximo): ");
                                String departamento = entrada.next();
                                System.out.print("\t- Turno (manana/tarde/manana y tarde): ");
                                entrada.nextLine();
                                String turno = entrada.nextLine();

                                // Añadimos el personal docente
                                String values1 = "('" + dni + "', '" + nombre + "', '" + apellidos + "', '" + telefono +
                                        "', '" + domicilio + "', '" + departamento + "', '" + turno + "')";

                                System.out.println("Valores a insertar: " + values1);

                                try {
                                    sentencia = conexion.createStatement();
                                    sentencia.executeQuery("INSERT INTO PersonalDocente (DNI_Per, Nombre, Apellidos, Telefono, Domicilio, Departamento, Turno) VALUES " + values1);
                                    sentencia.executeQuery("COMMIT");
                                    System.out.println("Personal docente añadido correctamente");
                                } catch (SQLException sql_exception) {
                                    System.out.println("Error: No se ha podido añadir el personal docente");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                }

                                break;

                            // Dar de baja personal docente
                            case 2:
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Dar de baja personal docente");
                                System.out.println("---------------------------------------------------");

                                // Pedimos el DNI del personal docente
                                System.out.print("Introduzca el DNI del personal docente a dar de baja: ");
                                String dni_baja = entrada.next();

                                try {
                                    sentencia = conexion.createStatement();
                                    sentencia.executeQuery("DELETE FROM PersonalDocente WHERE DNI_Per='" + dni_baja + "'");
                                    sentencia.executeQuery("COMMIT");
                                    System.out.println("Personal docente borrado correctamente");
                                } catch (SQLException sql_exception) {
                                    System.out.println("Error: No se ha podido dar de baja el personal docente");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                }

                                break;

                            // Solicitar tutoría
                            case 3:
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Solictar tutoría");
                                System.out.println("---------------------------------------------------");

                                // Pedimos el DNI del alumno solicitante de la tutoría
                                System.out.print("Introduzca el DNI del alumno solicitante de la tutoría: ");
                                String dni_alumno = entrada.next();

                                // Pedimos el DNI del personal docente con el que quiere la tutoría
                                System.out.print("Introduzca el DNI del personal docente con el que quiera tener la tutoría: ");
                                String dni_pd = entrada.next();

                                // Pedimos la fecha de la tutoría
                                String fecha_tutoria = "";
                                boolean fecha_incorrecta = true;

                                while (fecha_incorrecta) {
                                    fecha_incorrecta = false;
                                    System.out.print("Introduzca la fecha de la tutoría (yyyy-MM-dd): ");
                                    fecha_tutoria = entrada.next();

                                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    sdf.setLenient(false);

                                    try {
                                        sdf.parse(fecha_tutoria);
                                    } catch (Exception e) {
                                        System.out.println("Error: El formato de fecha introducido no es correcto");
                                        fecha_incorrecta = true;
                                    }

                                    // Comprobamos que además de estar en un formato correcto, es posterior a la fecha actual
                                    if (!fecha_incorrecta) {
                                        Date fecha_tutoria_date = null;
                                        Date fecha_actual = new Date(); // Esto nos da el día actual
                                        try {
                                            fecha_tutoria_date = sdf.parse(fecha_tutoria);
                                        } catch (Exception e) {
                                            System.out.println("Error: No se ha podido convertir la fecha de tutoría a Date");
                                        }

                                        if (fecha_tutoria_date.before(fecha_actual)) {
                                            System.out.println("Error: La fecha de tutoría debe ser posterior a la fecha actual");
                                            fecha_incorrecta = true;
                                        }
                                    }

                                }

                                String values3 = "('" + dni_alumno + "', to_date('" + fecha_tutoria + "','yyyy-mm-dd'), '" + dni_pd + "')";

                                try {
                                    sentencia = conexion.createStatement();
                                    sentencia.executeQuery("INSERT INTO Tutoria VALUES " + values3);
                                    sentencia.executeQuery("COMMIT");
                                    System.out.println("Tutoría añadida correctamente");
                                } catch (SQLException sql_exception) {
                                    System.out.println("Error: No se ha podido añadir la tutoría");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                }

                                break;

                            // Consulta por filtro
                            case 4:
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Consulta por filtro");
                                System.out.println("---------------------------------------------------");

                                //Crear una lista de string con los filtros
                                ArrayList<String> filtros = new ArrayList<String>();
                                ArrayList<String> valorFiltros = new ArrayList<String>();
                                int MAX_FILTROS = 5;
                                boolean pedir_filtros = true;

                                // Preguntamos al usuario que filtros quiere usar para la búsqueda
                                while (pedir_filtros) {
                                    System.out.print("Introduzca el filtro por el que quiere consultar (DNI, Nombre, Apellidos, Departamento, Turno): ");
                                    String filtro = entrada.next();

                                    if (filtro.equals("DNI") || filtro.equals("Nombre") || filtro.equals("Apellidos") || filtro.equals("Departamento") || filtro.equals("Turno")) {
                                        filtros.add(filtro);
                                        System.out.println("Filtro añadido correctamente");
                                    } else {
                                        System.out.println("Error: El filtro introducido no es correcto");
                                    }

                                    if (filtros.size() == MAX_FILTROS) {
                                        pedir_filtros = false;
                                    } else {
                                        System.out.print("¿Desea añadir otro filtro? (S/N): ");
                                        String respuesta = entrada.next();

                                        if (respuesta.equals("N")) {
                                            pedir_filtros = false;
                                        }
                                    }
                                }

                                // Pedimos el valor de los filtros escogidos
                                for (int i = 0; i < filtros.size(); i++) {
                                    System.out.print("Introduzca el valor del filtro " + filtros.get(i) + ": ");
                                    String valorFiltro = "";
                                    switch (filtros.get(i)) {
                                        case "DNI":
                                            System.out.print("(Formato: 00000000A): ");
                                            valorFiltro = entrada.next();
                                            break;
                                        case "Nombre":
                                            System.out.print("(Formato: Nombre): ");
                                            valorFiltro = entrada.next();
                                            break;
                                        case "Apellidos":
                                            System.out.print("(Formato: Apellido1 Apellido2): ");
                                            entrada.nextLine();
                                            valorFiltro = entrada.nextLine();
                                            break;
                                        case "Departamento":
                                            System.out.print("(Formato: Departamento): ");
                                            valorFiltro = entrada.next();
                                            break;
                                        case "Turno":
                                            System.out.print("(Formato: Mañana/Tarde): ");
                                            entrada.nextLine();
                                            valorFiltro = entrada.nextLine();
                                            break;
                                    }
                                    valorFiltros.add(valorFiltro);
                                }

                                // *********** Añadir alguna restricción por si se ponen los valores de los filtros mal en los bucles anteriores? ***********

                                // Construimos la consulta
                                String consulta = "SELECT * FROM PersonalDocente WHERE ";
                                for (int i = 0; i < filtros.size(); i++) {
                                    switch (filtros.get(i)) {
                                        case "DNI":
                                            consulta += "DNI_Per = '" + valorFiltros.get(i) + "'";
                                            break;
                                        case "Nombre":
                                            consulta += "Nombre = '" + valorFiltros.get(i) + "'";
                                            break;
                                        case "Apellidos":
                                            consulta += "Apellidos = '" + valorFiltros.get(i) + "'";
                                            break;
                                        case "Departamento":
                                            consulta += "Departamento = '" + valorFiltros.get(i) + "'";
                                            break;
                                        case "Turno":
                                            consulta += "Turno = '" + valorFiltros.get(i) + "'";
                                            break;
                                    }
                                    if (i != filtros.size() - 1) {
                                        consulta += " AND ";
                                    }
                                }

                                // Realizamos y mostramos el resultado de la consulta
                                try {
                                    sentencia = conexion.createStatement();
                                    ResultSet resultado = sentencia.executeQuery(consulta);

                                    System.out.println("Resultado de la consulta:");
                                    ResultSetMetaData rsmd = resultado.getMetaData();
                                    int numero_columnas = rsmd.getColumnCount();

                                    // Imprimimos las columnas
                                    for (int i = 1; i <= numero_columnas; i++) {
                                        if (i > 1) System.out.print(", ");
                                        System.out.print(rsmd.getColumnName(i).toUpperCase());
                                    }
                                    System.out.println();

                                    // Vamos iterando por las filas e imprimiéndolas
                                    while (resultado.next()) {
                                        for (int i = 1; i <= numero_columnas; i++) {
                                            if (i > 1) System.out.print(", ");
                                            System.out.print(resultado.getString(i));
                                        }
                                        System.out.println();
                                    }
                                    System.out.println();
                                    resultado.close();


                                } catch (SQLException sql_exception) {
                                    System.out.println("Error: No se ha podido realizar la consulta");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                }

                                break;

                            // Registrar asistencia
                            case 5:
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Registrar asistencia");
                                System.out.println("---------------------------------------------------");

                                // Solicitar el DNI al docente
                                System.out.print("Introduzca su DNI: ");
                                String dni_docente = entrada.next();

                                // Preguntamos al usuario por el día actual
                                String fecha_actual = "";
                                boolean formato_incorrecto = true;

                                while (formato_incorrecto) {
                                    fecha_incorrecta = false;
                                    System.out.print("Introduzca la fecha de hoy (yyyy-mm-dd): ");
                                    fecha_actual = entrada.next();

                                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    sdf.setLenient(false);

                                    try {
                                        sdf.parse(fecha_actual);
                                    } catch (Exception e) {
                                        System.out.println("Error: El formato de fecha introducido no es correcto");
                                        fecha_incorrecta = true;
                                    }
                                }

                                // *********** En general, en todos los casos de uso, comprobar más la veracidad de los datos y acciones???? ***********

                                /* Disparador algo así?????
                                CREATE OR REPLACE TRIGGER registrar_asistencia
                                BEFORE 
                                UPDATE OF Fecha_Hora ON PersonalDocente
                                FOR EACH ROW
                                BEGIN
                                    IF :NEW.ID = :OLD.ID THEN
                                    IF :NEW.Fecha_Hora != TRUNC(SYSDATE) THEN
                                        RAISE_APPLICATION_ERROR(-20000, 'La fecha debe ser la fecha actual');
                                    END IF;
                                    END IF;
                                END;
                                */

                                // Actualizamos la fecha y hora de la última asistencia del docente
                                try {
                                    sentencia = conexion.createStatement();
                                    sentencia.executeUpdate("UPDATE PersonalDocente SET Fecha_Hora = '" + fecha_actual + "' WHERE DNI_Per = '" + dni_docente + "'");
                                    System.out.println("Asistencia registrada correctamente");
                                } catch (SQLException sql_exception) {
                                    System.out.println("Error: No se ha podido registrar la asistencia");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                }

                                break;
                        }

                        break;

                    //////////////////////////////////////////////////////////
                    // SUBSISTEMA ACTIVIDADES EXTRAESCOLARES - ISABEL
                    //////////////////////////////////////////////////////////
                    case 5:
                        System.out.println("\n---------------------------------------------------");
                        System.out.println("SUBSISTEMA DE ACTIVIDADES EXTRAESCOLARES");
                        System.out.println("---------------------------------------------------");
                        System.out.println("Funcionalidades: ");
                        System.out.println("1. Darse de alta en una actividad extraescolar");
                        System.out.println("2. Darse de baja en una actividad extraescolar");
                        System.out.println("3. Consultar información sobre una actividad extraescolar");
                        System.out.println("4. Añadir una actividad extraescolar");
                        System.out.println("5. Eliminar una actividad extraescolar");
                        System.out.println("---------------------------------------------------\n");
                        System.out.print("¿Qué funcionalidad desea?: ");

                        while (true) {
                            try {
                                opcion = entrada.nextInt();
                                break;
                            } catch (Exception e) {
                                System.out.println("Error: Introduzca un número");
                                entrada.nextLine();
                            }
                        }

                        switch (opcion) {
                            case 1:

                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Ha elegido la funcionalidad 1: Darse de alta en una actividad extraescolar");
                                System.out.println("---------------------------------------------------");

                                //Primero habra que pedir el DNI del alumno y el codigo de la actividad
                                //vemos que son correctos y añadimos el alumno a la actividad. IMPORTANTE: HAY QUE MIRAR QUE NO ESTABA YA AÑADIDO


                                sentencia = conexion.createStatement();

                                //Pedir DNI_Alum:
                                String dnialumno = "";
                                boolean correcto = false;

                                while (!correcto) {
                                    System.out.print("\nIntroducir el DNI del alumno (9 caracteres): ");
                                    dnialumno = entrada.next();

                                    //comprobamos que hay un alumno con ese dni 
                                    rs = sentencia.executeQuery("SELECT COUNT(*) FROM Alumnos WHERE DNI_alum = '" + dnialumno + "'");
                                    rs.next();
                                    int numalumnosdni = rs.getInt(1);

                                    if (numalumnosdni > 0) {
                                        correcto = true;
                                    } else
                                        System.out.print("DNI no valido: No existe un alumno con ese DNI \n");
                                }

                                //Pedir Codigo de la actividad extraescolar:
                                String cod = "";
                                boolean correcto2 = false;

                                while (!correcto2) {
                                    System.out.print("Introducir el codigo de la actividad extraescolar (maximo 5 caracteres): ");
                                    cod = entrada.next();

                                    //comprobamos que hay un actividad extraescolar con ese codigo 
                                    rs = sentencia.executeQuery("SELECT COUNT(*) FROM ActividadExtraescolar WHERE Codigo_act = '" + cod + "'");
                                    rs.next();
                                    int numact = rs.getInt(1);

                                    if (numact > 0) {
                                        correcto2 = true;
                                    } else
                                        System.out.print("Codigo no valido: No existe una actividad extraescolar con ese codigo \n");
                                }

                                //Falta comprobar que no estaba ya inscrito, si no daria error
                                rs = sentencia.executeQuery("SELECT COUNT(*) FROM Inscrito WHERE DNI_alum = '" + dnialumno + "' AND Codigo_act = '" + cod + "'");
                                rs.next();
                                int comprobacion = rs.getInt(1);


                                if (comprobacion == 0) {
                                    try {
                                        sentencia.executeQuery("INSERT INTO Inscrito VALUES('" + cod + "', '" + dnialumno + "')");
                                        sentencia.executeQuery("COMMIT");
                                        sentencia.close();
                                        System.out.println("Alumno añadido correctamente a la actividad.");
                                    } catch (SQLException e) {
                                        System.out.println("Error: El alumno no se ha podido inscribir correctamente.");
                                    }
                                } else System.out.println("El alumno ya estaba inscrito en a la actividad.");

                                break;

                            case 2:
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Ha elegido la funcionalidad 2: Darse de baja en una actividad extraescolar");
                                System.out.println("---------------------------------------------------");

                                //Primero habra que pedir el codigo de la actividad y el DNI del alumno
                                //vemos que existe la actividad y que hay un alumno en ella con el dni introducido
                                //sera una sentencia tal que: DELETE FROM Inscrito WHERE DNI_alum = dnialumnoelim  AND Cod_Act = codigoborrado

                                sentencia = conexion.createStatement();

                                //Pedir Codigo de la actividad extraescolar:
                                String codact = "";
                                boolean correcto3 = false;

                                while (!correcto3) {
                                    System.out.print("\nIntroducir el codigo de la actividad extraescolar (maximo 5 caracteres): ");
                                    codact = entrada.next();

                                    //comprobamos que hay un actividad extraescolar con ese codigo y que tiene a algun alumno inscrito
                                    rs = sentencia.executeQuery("SELECT COUNT(*) FROM Inscrito WHERE Codigo_act = '" + codact + "'");
                                    rs.next();
                                    int numact = rs.getInt(1);

                                    if (numact > 0) {
                                        correcto3 = true;
                                    } else
                                        System.out.print("\nCodigo no valido: No existe una actividad extraescolar con ese codigo o no tiene ningun alumno inscrito\n");
                                }

                                //Pedir DNI del alumno a borrar:
                                String dnialumnoelim = "";
                                boolean correcto4 = false;

                                while (!correcto4) {
                                    System.out.print("\nIntroducir el DNI del alumno que se desea eliminar de la actividad(9 caracteres): ");
                                    dnialumnoelim = entrada.next();

                                    //comprobamos que hay un alumno inscrito en la actividad con ese dni 
                                    rs = sentencia.executeQuery("SELECT COUNT(*) FROM Inscrito WHERE DNI_alum = '" + dnialumnoelim + "' AND Codigo_act = '" + codact + "'");
                                    rs.next();
                                    int numalumnosdni = rs.getInt(1);

                                    if (numalumnosdni > 0) {
                                        correcto4 = true;
                                    } else
                                        System.out.println("\nNo hay ningun alumno inscrito con ese DNI.");
                                }

                                try {
                                    sentencia.executeQuery("DELETE FROM Inscrito WHERE DNI_alum = '" + dnialumnoelim + "' AND Codigo_act = '" + codact + "'");
                                    sentencia.executeQuery("COMMIT");
                                    sentencia.close();
                                    System.out.println("\nAlumno borrado correctamente de la actividad.");
                                } catch (SQLException e) {
                                    System.out.println("\nError al borrar el alumno de la actividad.");
                                }

                                break;

                            case 3:
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Ha elegido la funcionalidad 3: Consultar información sobre una actividad extraescolar");
                                System.out.println("---------------------------------------------------");
                                //Primero se pide el codigo de la actividad y se comprueba que existe
                                //luego se muestran por pantalla los datos de la actividad

                                sentencia = conexion.createStatement();

                                //Pedir Codigo de la actividad extraescolar:
                                String codigoact = "";
                                boolean correcto5 = false;

                                while (!correcto5) {
                                    System.out.print("\nIntroducir el código de la actividad extraescolar (maximo 5 caracteres): ");
                                    codigoact = entrada.next();

                                    //comprobamos que hay un actividad extraescolar con ese codigo 
                                    rs = sentencia.executeQuery("SELECT COUNT(*) FROM ActividadExtraescolar WHERE Codigo_act = '" + codigoact + "'");
                                    rs.next();
                                    int numact = rs.getInt(1);

                                    if (numact > 0) { //es 0 o 1
                                        correcto5 = true;
                                    } else
                                        System.out.print("\nCódigo no válido: No existe una actividad extraescolar con ese código \n");
                                }

                                //Se mostrará el codigo, nombre, capacidad y descripcion de la actividad:
                                // buscamos y mostramos los atributos como hicimos con las tablas del seminario1
                                ResultSet rsact;
                                rsact = sentencia.executeQuery(
                                        "SELECT Codigo_act, Nombre, Capacidad, Descripcion FROM ActividadExtraescolar WHERE Codigo_act = '" + codigoact + "'");

                                ResultSetMetaData rsmd2 = rsact.getMetaData();
                                int numero_columnasselect = rsmd2.getColumnCount();

                                // Imprimimos las columnas (deberian ser 4)
                                for (int i = 1; i <= numero_columnasselect; i++) {
                                    if (i > 1)
                                        System.out.print(", ");
                                    System.out.print(rsmd2.getColumnName(i).toUpperCase());
                                }


                                System.out.println();

                                // Vamos iterando por las filas e imprimiéndolas
                                while (rsact.next()) {
                                    for (int i = 1; i <= numero_columnasselect; i++) {
                                        if (i > 1)
                                            System.out.print(", ");
                                        System.out.print(rsact.getString(i));
                                    }
                                    System.out.println();
                                }
                                rsact.close();
                                sentencia.close();
                                break;

                            case 4:
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Ha elegido la funcionalidad 4: Añadir una actividad extraescolar");
                                System.out.println("---------------------------------------------------");

                                System.out.println("\nA continuación se le pedirán introducir los siguientes datos de la actividad extraescolar: ");

                                sentencia = conexion.createStatement();

                                // Pedir Nombre:
                                String nombre_act = "";
                                boolean correcto7 = false;
                                entrada.useDelimiter("\n");
                                while (!correcto7) {
                                    System.out.print("\nIntroducir el nombre de la actividad extraescolar a insertar (maximo 20 caracteres): ");
                                    nombre_act = entrada.next();
                                    // comprobamos que tiene menos de 21 caracteres:
                                    if (nombre_act.length() < 21) {
                                        correcto7 = true;
                                    } else
                                        System.out.print("\nFormato incorrecto \n");
                                }
                                entrada.reset();

                                //Pedir Código:

                                String codigoact3 = "";
                                boolean correcto8 = false;

                                while (!correcto8) {
                                    System.out.print("\nIntroducir el código de la actividad extraescolar (máximo 5 caracteres): ");
                                    codigoact3 = entrada.next();

                                    //comprobamos que no hay un actividad extraescolar con ese codigo
                                    rs = sentencia.executeQuery("SELECT COUNT(*) FROM ActividadExtraescolar WHERE Codigo_act = '" + codigoact3 + "'");
                                    rs.next();
                                    int numact = rs.getInt(1);
                                    if (codigoact3.length() > 5)
                                        System.out.print("\nFormato incorrecto \n");
                                    else if (numact == 0) {
                                        correcto8 = true;
                                    } else
                                        System.out.print("\nCodigo no valido: Ya existe una actividad extraescolar con ese código \n");
                                }

                                // Pedir Capacidad:
                                int capacidad = 0;
                                boolean correcto10 = false;

                                while (!correcto10) {
                                    System.out.print("\nIntroducir la capacidad de la actividad: ");
                                    capacidad = entrada.nextInt();

                                    // comprobamos que es positiva
                                    if (capacidad > 0) {
                                        correcto10 = true;
                                    } else
                                        System.out.print("\nCapacidad no válida, debe ser un entero positivo");

                                }

                                // Pedir Descripción:

                                boolean correcto9 = false;
                                String descripcion = "";
                                entrada.useDelimiter("\n");
                                while (!correcto9) {
                                    System.out.print("\nIntroducir descripción de la actividad extraescolar a insertar (maximo 20 caracteres): ");
                                    descripcion = entrada.next();
                                    // comprobamos que tiene menos de 21 caracteres:
                                    if (descripcion.length() < 21) {
                                        correcto9 = true;
                                    } else {
                                        System.out.print("\nFormato incorrecto \n");
                                    }
                                }
                                entrada.reset();

                                try {
                                    sentencia.executeQuery("INSERT INTO ActividadExtraescolar VALUES('" + codigoact3 + "', '" + nombre_act + "', '" + capacidad + "', '" + descripcion + "')");
                                    sentencia.executeQuery("COMMIT");
                                    sentencia.close();
                                    System.out.println("\nAlumno añadido correctamente");
                                    System.out.println("\nActividad extraescolar añadida correctamente ");

                                } catch (SQLException sql_exception) {
                                    System.out.println("\nError al añadir la actividad extraescolar");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                }


                                break;

                            case 5:
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Ha elegido la funcionalidad 5: Eliminar una actividad extraescolar");
                                System.out.println("---------------------------------------------------");
                                //Se tiene en cuenta RS2,RS3 

                                //Pedir Codigo de la actividad extraescolar:
                                String codigoact2 = "";
                                boolean correcto6 = false;
                                sentencia = conexion.createStatement();   //Dentro mejor no por si hay error al establecer conexión o si?

                                while (!correcto6) {
                                    System.out.print("\nIntroducir el código de la actividad extraescolar (maximo 5 caracteres): ");
                                    codigoact2 = entrada.next();
                                    //comprobamos que hay un actividad extraescolar con ese codigo 
                                    rs = sentencia.executeQuery("SELECT COUNT(*) FROM ActividadExtraescolar WHERE Codigo_act = '" + codigoact2 + "'");
                                    rs.next();
                                    int numact = rs.getInt(1);
                                    if (numact > 0) { //es 0 o 1
                                        correcto6 = true;
                                    } else
                                        System.out.print("\nCodigo no valido: No existe una actividad extraescolar con ese código \n");
                                }

                                try {
                                    sentencia.executeQuery("DELETE FROM ActividadExtraescolar WHERE Codigo_act = '" + codigoact2 + "'");
                                    sentencia.executeQuery("COMMIT");
                                    sentencia.close();
                                    System.out.println("\nActividad extraescolar borrada correctamente");
                                } catch (SQLException sql_exception) {
                                    System.out.println("\nError en la eliminación de la actividad extraescolar ");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                }


                                break;

                            default:
                                System.out.println("\nOpción no válida");
                                break;
                        }
                        ;

                        break;

                    //////////////////////////////////////////////////////////
                    // SUBSISTEMA MATERIALES - FEDERICO
                    //////////////////////////////////////////////////////////
                    case 6:
                        // Lectura de la funcionalidad a realizar
                        System.out.println("\n---------------------------------------------------");
                        System.out.println("SUBSISTEMA DE MATERIALES");
                        System.out.println("---------------------------------------------------");
                        System.out.println("Funcionalidades: ");
                        System.out.println("1. Añadir un nuevo material");
                        System.out.println("2. Añadir cantidad de un determinado material");
                        System.out.println("3. Eliminar un tipo de material");
                        System.out.println("4. Consultar información sobre todos los materiales y su disponibilidad");
                        System.out.println("5. Realizar un nuevo préstamo de un material a un profesor");
                        System.out.println("6. Realizar devolución del préstamo de un profesor");
                        System.out.println("7. Consultar información sobre los préstamos");
                        System.out.println("---------------------------------------------------\n");
                        System.out.print("¿Qué funcionalidad desea?: ");

                        while (true) {
                            try {
                                opcion = entrada.nextInt();
                                break;
                            } catch (Exception e) {
                                System.out.println("Error: Introduzca un número");
                                entrada.nextLine();
                            }
                        }

                        // Procesamiento de las funcionalidades
                        switch (opcion) {
                            // Funcionalidad 1: Añadir un nuevo material
                            case 1: {
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Funcionalidad 1: Añadiendo un nuevo material");
                                System.out.println("---------------------------------------------------");

                                // Pedimos los datos del nuevo material
                                entrada.useDelimiter("\n");
                                System.out.println("Introduzca los datos de entrada del nuevo material");
                                System.out.print("\t- Nombre (15 caracteres máximo): ");
                                String nombre_material = entrada.next();
                                System.out.print("\t- Código (5 caracteres máximo): ");
                                String codigo_material = entrada.next();
                                System.out.print("\t- Clase del material (entre 1 y 5): ");
                                String clase_material = entrada.next();
                                System.out.print("\t- Descripción del material (30 caracteres máximo): ");
                                String descripcion_material = entrada.next();
                                entrada.reset();

                                // Añadimos el nuevo material
                                String values = "('" + codigo_material + "', '" + nombre_material + "', " + clase_material +
                                        ", '" + descripcion_material + "', 0)";
                                sentencia = conexion.createStatement();
                                try {
                                    sentencia.executeQuery("INSERT INTO Material VALUES " + values);
                                } catch (SQLException sql_exception) {
                                    System.out.println("Error en la inserción del material. Información: ");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                    continue;
                                }
                                sentencia.executeQuery("COMMIT");
                                sentencia.close();
                                System.out.println("Inserción del material realizada correctamente");

                                break;
                            }

                            // Funcionalidad 2: Añadir cantidad de un determinado material
                            case 2: {
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Funcionalidad 2: Añadiendo cantidad a un nuevo material");
                                System.out.println("---------------------------------------------------");

                                // Pedimos los datos del nuevo material
                                entrada.useDelimiter("\n");
                                System.out.println("Introduzca los datos:");
                                System.out.print("\t- Código del material: ");
                                String codigo_material = entrada.next();
                                int cantidad_mas;
                                do {
                                    System.out.print("\t- Cantidad a añadir: ");
                                    cantidad_mas = entrada.nextInt();
                                    if (cantidad_mas <= 0)
                                        System.out.println("Ha introducido una cantidad incorrecta");
                                } while (cantidad_mas <= 0);
                                entrada.reset();


                                sentencia = conexion.createStatement();
                                try {
                                    sentencia.executeQuery("UPDATE Material SET cantidad = cantidad + " + cantidad_mas + " WHERE Codigo_Mat = '" + codigo_material + "'");
                                } catch (SQLException sql_exception) {
                                    System.out.println("Error en la modificación del material. Información: ");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                    continue;
                                }
                                sentencia.executeQuery("COMMIT");
                                sentencia.close();
                                System.out.println("Adición de material realizada correctamente");

                                break;
                            }

                            // Funcionalidad 3: Eliminar un tipo de material
                            case 3: {
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Funcionalidad 3: Eliminar un tipo de material");
                                System.out.println("---------------------------------------------------");

                                // Pedimos los datos del material a eliminar
                                System.out.println("Introduzca los datos:");
                                System.out.print("\t- Código del material: ");
                                String codigo_material = entrada.next();

                                sentencia = conexion.createStatement();
                                try {
                                    sentencia.executeQuery("DELETE FROM Material WHERE codigo_mat = '" + codigo_material + "'");
                                } catch (SQLException sql_exception) {
                                    System.out.println("Error en la inserción del préstamo. Información: ");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                    continue;
                                }
                                sentencia.executeQuery("COMMIT");
                                sentencia.close();
                                System.out.println("Eliminación del material realizada correctamente");

                                break;
                            }

                            // Funcionalidad 4: Consultar información sobre todos los materiales y su disponibilidad
                            case 4: {
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Funcionalidad 4: Consultar información sobre todos los materiales y su disponibilidad");
                                System.out.println("---------------------------------------------------");

                                sentencia = conexion.createStatement();
                                rs = sentencia.executeQuery("SELECT * FROM Material");

                                while (rs.next()) {
                                    // Obtenemos la cantidad prestada
                                    String codigo_material = rs.getString(1);
                                    int cantidad_total = rs.getInt(5);
                                    Statement sentencia2 = conexion.createStatement();
                                    ResultSet rs2 = sentencia2.executeQuery("SELECT SUM(cantidad_prestada) FROM Prestamo where codigo_mat = '" + codigo_material + "'");
                                    rs2.next();

                                    // Imprimimos la información
                                    System.out.println("\n|||| Material " + codigo_material + " ||||");
                                    System.out.println("\t- Nombre: " + rs.getString(2));
                                    System.out.println("\t- Descripción: " + rs.getString(4));
                                    System.out.println("\t- Cantidad total: " + cantidad_total);
                                    System.out.println("\t- Cantidad disponible: " + (cantidad_total - rs2.getInt(1)));
                                }

                                sentencia.close();

                                break;
                            }

                            // Funcionalidad 5: Realizar un nuevo préstamo de un material a un profesor
                            case 5: {
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Funcionalidad 5: Realizar un nuevo préstamo de un material a un profesor");
                                System.out.println("---------------------------------------------------");

                                // Pedimos los datos del nuevo préstamo
                                entrada.useDelimiter("\n");
                                System.out.println("Introduzca los datos de entrada del nuevo préstamo::");
                                System.out.print("\t- DNI del profesor (15 caracteres máximo): ");
                                String dni_profesor = entrada.next();
                                System.out.print("\t- Código del material (5 caracteres máximo): ");
                                String codigo_material = entrada.next();
                                int cantidad_a_prestar;
                                do {
                                    System.out.print("\t- Cantidad a prestar: ");
                                    cantidad_a_prestar = entrada.nextInt();
                                    if (cantidad_a_prestar <= 0)
                                        System.out.println("Ha introducido una cantidad incorrecta");
                                } while (cantidad_a_prestar <= 0);
                                entrada.reset();


                                // Añadimos el nuevo préstamo
                                String values = "('" + dni_profesor + "', '" + codigo_material + "', " + cantidad_a_prestar + "')";
                                sentencia = conexion.createStatement();
                                try {
                                    sentencia.executeQuery("INSERT INTO Prestamo VALUES " + values);
                                } catch (SQLException sql_exception) {
                                    System.out.println("Error en la inserción del préstamo. Información: ");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                    continue;
                                }
                                sentencia.executeQuery("COMMIT");
                                sentencia.close();
                                System.out.println("Inserción del préstamo realizada correctamente");

                                break;
                            }

                            // Funcionalidad 6: Realizar devolución del préstamo de un profesor
                            case 6: {
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Funcionalidad 6: Realizar devolución del préstamo de un profesor");
                                System.out.println("---------------------------------------------------");

                                // Pedimos los datos del préstamos a devolver
                                entrada.useDelimiter("\n");
                                System.out.println("Introduzca los datos de entrada del préstamo a devolver:");
                                System.out.print("\t- DNI del profesor (15 caracteres máximo): ");
                                String dni_profesor = entrada.next();
                                System.out.print("\t- Código del material (5 caracteres máximo): ");
                                String codigo_material = entrada.next();
                                System.out.print("\t- Cantidad devuelta: ");
                                int cantidad_devuelta = entrada.nextInt();
                                entrada.reset();

                                // Verificamos que la cantidad devuelta es la prestada
                                sentencia = conexion.createStatement();
                                try {
                                    rs = sentencia.executeQuery("SELECT cantidad_prestada FROM Prestamo WHERE DNI_per = '" + dni_profesor + "' AND " +
                                            "codigo_mat = '" + codigo_material + "'");
                                } catch (SQLException sql_exception) {
                                    System.out.println("Error en los datos del préstamo. Información: ");
                                    System.out.println("\t- Código: " + sql_exception.getErrorCode());
                                    System.out.println("\t- Descripción: " + sql_exception.getMessage());
                                    continue;
                                }

                                rs.next();
                                int cantidad_prestada = rs.getInt(1);
                                if (cantidad_devuelta != cantidad_prestada) {
                                    System.out.println("La cantidad devuelta no es la que se prestó. Error.");
                                    continue;
                                }

                                sentencia.executeQuery("DELETE FROM Prestamo WHERE DNI_per = '" + dni_profesor + "' AND " +
                                        "codigo_mat = '" + codigo_material + "'");

                                break;
                            }

                            // Funcionalidad 7: Consultar información sobre los préstamos
                            case 7: {
                                System.out.println("\n---------------------------------------------------");
                                System.out.println("Funcionalidad 7: Consultar información sobre los préstamos");
                                System.out.println("---------------------------------------------------");

                                int i = 1;
                                sentencia = conexion.createStatement();
                                rs = sentencia.executeQuery("select M.nombre, PD.nombre, PD.apellidos, P.cantidad_prestada" +
                                        " from Prestamo P, Material M, PersonalDocente PD" +
                                        " where P.codigo_mat = M.codigo_mat and P.dni_per = PD.dni_per");

                                while (rs.next()) {
                                    // Imprimimos la información
                                    System.out.println("\n|||| Préstamo " + (i++) + " ||||");
                                    System.out.println("\t- Nombre del material: " + rs.getString(1));
                                    System.out.println("\t- Nombre del profesor: " + rs.getString(2) + " " + rs.getString(3));
                                    System.out.println("\t- Cantidad prestada: " + rs.getString(4));
                                }

                                break;
                            }
                        }

                        break;

                    //////////////////////////////////////////////////////////
                    // CERRAR CONEXIÓN CON LA BASE DE DATOS Y SALIR DEL SI
                    //////////////////////////////////////////////////////////
                    case 0:
                        sentencia = conexion.createStatement();
                        sentencia.close();
                        conexion.close();
                        salir = true;

                        break;

                    default:
                        System.out.println("Opción no válida");

                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        entrada.close();
    }
    
    // NEREA ALBERDI
    private static final String[] SECTION02_CODE01_TIPOSADMITIDOS = {
        "aula", "despacho", "comedor", "patio", "aseo", "otro"
    };

    private static boolean section02_code01_checker(String ident, String ubic, String cadAforo, String tipoEspacio) {
        boolean result = true;

        if(ident.length() > 10 || ident.length() == 0) {
            System.out.println("Identificador incorrecto (longitud) - no puede ser vacio y no se pueden exceder los 10 caracteres");
            result = false;
        }

        if(ubic.length() > 40) {
            System.out.println("La ubicacion no puede exceder los 40 caracteres");
            result = false;
        }

        int aforo;
        try {
            aforo = Integer.parseInt(cadAforo);

            if(aforo < 0) {
                System.out.println("El aforo no puede ser negativo");
                result = false;
            }
        } catch(Exception exception) {
            System.out.println("El aforo tiene que ser un numero");
            result = false;
        }

        boolean found = false;
        for(String s : SECTION02_CODE01_TIPOSADMITIDOS)
            if(tipoEspacio.equals(s)) {
                found = true;
                break;
            }

        if(!found) {
            System.out.println("Tipo no reconocido");
            result = false;
        }

        return result;
    }
    
    private static void section02_processRequest(Connection conexion, Scanner entrada, int code) throws Exception { // Cambio SQLException por Exception
        Statement sentencia = conexion.createStatement();
        String query1 = null;
        String query2 = null;
        Date currentDate = new Date();
        final LocalTime horaAbrir = LocalTime.parse("07:00");
        final LocalTime horaCerrar = LocalTime.parse("20:00");
        switch(code) {
            case 1:
            // agregar espacio fisico
                // example query: INSERT INTO ESPACIOFISICO VALUES('esp11', 'planta 4', 20, 'despacho');
                System.out.println("Inserte identificador del espacio");
                String identificador = entrada.nextLine();
                System.out.println("Inserte ubicacion");
                String ubicacion = entrada.nextLine();
                System.out.println("Inserte aforo");
                String cadAforo = entrada.next();
                System.out.println("Inserte tipo de espacio (aula, despacho, comedor, patio, aseo, otro)");
                String tipoEspacio = entrada.next();

                boolean check = section02_code01_checker(identificador, ubicacion, cadAforo, tipoEspacio);
                if(check) {
                    query1 = String.format("INSERT INTO ESPACIOFISICO VALUES('%s', '%s', %d, '%s')", 
                        identificador, ubicacion, Integer.parseInt(cadAforo), tipoEspacio);
                    
                    try {
                        sentencia.executeQuery(query1);

                        conexion.commit();
                        System.out.println("Se ha agregado el espacio fisico de manera satisfactoria");
                    } catch(java.sql.SQLIntegrityConstraintViolationException exceptionSqlException) {
                        // only one integrity constraint can be violated - primary key of identification
                        System.out.println("No se ha podido realizar la operacion debido a que el identificador es unico");
                    }

                } else
                    System.out.println("No se ha podido realizar la operacion debido a un error de formato");

                break;
            case 2:
            // crear reserva asignatura

                // PEDIR FECHA_HORA Y COMPROBAR
                System.out.println("Inserte la fecha y hora de la reserva a crear (yyyy-MM-dd HH:mm)");
                String fechaRes2 = entrada.next();
                String horaRes2 = entrada.next();

                Date dateRes2 = null; 
                try{
                    dateRes2 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fechaRes2 + " " + horaRes2); 
                } catch(Exception e) {
                    // FORMATO INCORRECTO
                    System.out.println("El formato de la fecha no es correto");
                    break;
                }

                if (currentDate.after(dateRes2) ){
                    System.out.println("La reserva debe ser posterior a la fecha actual");
                    break;
                } 

                // Comprobamos que no sea entre semana
                LocalDate dayRes = LocalDate.parse(fechaRes2);  
                DayOfWeek diaRes = dayRes.getDayOfWeek();
                if ( diaRes.getValue() > 5){
                    System.out.println("El fin de semana el centro está cerrado.");
                    System.out.println("No se puede realizar la reserva.");
                    break;
                }

                // Comprobamos que el centro esté abierto en el horario indicado
                LocalTime timeRes = LocalTime.parse(horaRes2);
                if ( timeRes.isBefore(horaAbrir) || timeRes.isAfter(horaCerrar)){
                    System.out.println("El centro está cerrado a esa hora.");
                    System.out.println("El horario de apertura es de 07:00 a 20:00.");
                    System.out.println("No se puede realizar la consulta.");
                    break;
                }

                // Insertar duración 
                long minutMax2 = ChronoUnit.MINUTES.between(timeRes, horaCerrar);

                System.out.println("Inserte la duracion de la reserva en minutos");
                System.out.println("La duración debe superar la media hora y no puede exceder la hora de cierre del centro " + minutMax2);
                String duracionResString = entrada.next();

                int duracionRes;
                try {
                    duracionRes = Integer.parseInt(duracionResString);

                    if ( duracionRes < 30 || duracionRes > minutMax2){
                        System.out.println("La duracion introducida no es correcta.");
                        System.out.println("No se puede realizar la consulta.");
                        break;
                    }
                } catch(Exception exception) {
                    System.out.println("La duracion tiene que ser un numero");
                    break;
                }               

                //MOSTRAR IDENTIFICADOR DE AULAS DISPONIBLES
                System.out.println("Se muestran a continuación los identificadores de las aulas disponibles en dicho instante. ");

                query1 = "SELECT identesp FROM espaciofisico WHERE tipo='aula' AND NOT EXISTS (SELECT * from reservaasig WHERE identesp=espaciofisico.identesp AND ((TO_TIMESTAMP('" + 
                fechaRes2 + " " + horaRes2 + "', 'YYYY-MM-DD HH24:MI') + INTERVAL '" + duracionRes + "' minute) > fecha_hora) AND ( (fecha_hora + NUMTODSINTERVAL(duracion, 'minute')) > (TO_TIMESTAMP('" + 
                fechaRes2 + " " + horaRes2 + "', 'YYYY-MM-DD HH24:MI')))) AND NOT EXISTS (SELECT * from reservaact WHERE identesp=espaciofisico.identesp AND ((TO_TIMESTAMP('" + 
                fechaRes2 + " " + horaRes2 + "', 'YYYY-MM-DD HH24:MI') + INTERVAL '" + duracionRes + "' minute) > fecha_hora) AND ( (fecha_hora + NUMTODSINTERVAL(duracion, 'minute')) > (TO_TIMESTAMP('" + 
                fechaRes2 + " " + horaRes2 + "', 'YYYY-MM-DD HH24:MI'))))";

                ResultSet rs4 = sentencia.executeQuery(query1);
                ArrayList<String> espacios = new ArrayList<String>();

                // Vamos iterando por las filas e imprimiéndolas
                while (rs4.next()){
                        System.out.println(rs4.getString(1));
                        espacios.add(rs4.getString(1));
                }
                rs4.close();

                // INSERTAR IDENTESP A RESERVAR
                System.out.println("Seleccione el espacio a reservar entre los disponibles. ");
                String espacioRes = entrada.next();

                if( !espacios.contains(espacioRes)){
                    System.out.println("No es un identificador válido. ");
                    break;
                }

                // INSERTAR IDENTIFICADOR DE ASIGNATURA
                System.out.println("Introduzca el nombre de la asignatura para la que reserva el aula. ");
                String nombreAsig = entrada.next();
                System.out.println("Introduzca el curso de la asignatura para la que reserva el aula. ");
                String cursoAsig = entrada.next();

                int curso;
                try {
                    curso = Integer.parseInt(cursoAsig);

                    if(curso < 0 || curso > 7) {
                        System.out.println("El curso es un entero entre 1 y 6, ambos incluidos. ");
                        break;
                    }
                } catch(Exception exception) {
                    System.out.println("El curso tiene que ser un numero");
                    break;
                }

                query1 = String.format("SELECT * FROM Asignatura WHERE curso_asig=%d AND nombre_asig='%s' ", 
                    curso, nombreAsig);
                ResultSet rs5 = sentencia.executeQuery(query1);
                
                if(!rs5.next()){
                    System.out.println("Ha ocurrido un error fatal. ");
                    System.out.println("Compruebe que los datos concuerdan con los de una asignatura");
                    break;
                }

                rs5.close();

                query1 = String.format("INSERT INTO ReservaAsig VALUES('%s', TO_TIMESTAMP('" + fechaRes2 + " " + horaRes2  +"', 'YYYY-MM-DD HH24:MI'), %d, '%s', %d)", 
                    espacioRes, curso, nombreAsig, duracionRes);
                    
                try {
                    sentencia.executeQuery(query1);

                    conexion.commit();
                    System.out.println("Se ha agregado la reserva de manera satisfactoria");
                } catch(Exception e) {
                    // ERROR NO IDENTIFICADO
                    //e.printStackTrace();
                    System.out.println("No se ha podido realizar la operacion. ");
                }

                break;
            case 3:
            // cancelar reserva
                int valoresModif = 0;
                System.out.println("Inserte la fecha y hora de la reserva (yyyy-MM-dd HH:mm)");
                String fechaRes = entrada.nextLine();
                Date dateRes = null; 
                try{
                    dateRes = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fechaRes);  
                } catch(Exception e) {
                    // FORMATO INCORRECTO
                    System.out.println("El formato de la fecha no es correto");
                    break;
                }

                if (currentDate.after(dateRes) ){
                    System.out.println("La reserva debe ser posterior a la fecha actual");
                    break;
                } 

                System.out.println("Inserte el identificador del espacio reservado");
                String identRes = entrada.nextLine();
                if(identRes.length() > 10 || identRes.length() == 0) {
                    System.out.println("Identificador incorrecto (longitud) - no puede ser vacio y no se pueden exceder los 10 caracteres");
                    break;
                }

                query1 = String.format("DELETE FROM ReservaAsig WHERE fecha_hora=TO_TIMESTAMP('%s', 'YYYY-MM-DD HH24:MI') AND identesp='%s'", 
                fechaRes, identRes);

                query2 = String.format("DELETE FROM ReservaAct WHERE fecha_hora=TO_TIMESTAMP('%s', 'YYYY-MM-DD HH24:MI') AND identesp='%s'", 
                fechaRes, identRes);

                try {
                    valoresModif += sentencia.executeUpdate(query1);
                    valoresModif += sentencia.executeUpdate(query2);

                    conexion.commit();
                    if( valoresModif == 0){
                        System.out.println("La reserva no existe");
                    }else { // 1
                        System.out.println("La reserva se ha eliminado satisfactoriamente");
                    } 
                    
                } catch(Exception e) {
                    conexion.rollback();  // Por si se realiza el primer query y no el segundo
                    System.out.println("No se ha podido realizar la operacion");
                }

            break;
            case 4:
            // consultar aulas disponibles
                System.out.println("Inserte la fecha y hora que quiere consultar (yyyy-MM-dd HH:mm)");
                String fechaDisp = entrada.next();
                String horaDisp = entrada.next();

                Date dateDisp = null; 
                try{
                    dateDisp = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fechaDisp + " " + horaDisp); 
                } catch(Exception e) {
                    // FORMATO INCORRECTO
                    System.out.println("El formato de la fecha no es correto");
                    break;
                }

                // Comprobamos que no sea entre semana
                LocalDate dayDisp = LocalDate.parse(fechaDisp);  
                DayOfWeek dia = dayDisp.getDayOfWeek();
                if ( dia.getValue() > 5){
                    System.out.println("El fin de semana el centro está cerrado.");
                    System.out.println("No se puede realizar la consulta.");
                    break;
                }

                // Comprobamos que el centro esté abierto en el horario indicado
                LocalTime timeDisp = LocalTime.parse(horaDisp);
                if ( timeDisp.isBefore(horaAbrir) || timeDisp.isAfter(horaCerrar)){
                    System.out.println("El centro está cerrado a esa hora.");
                    System.out.println("El horario de apertura es de 07:00 a 20:00.");
                    System.out.println("No se puede realizar la consulta.");
                    break;
                }

                // Insertar duración 

                long minutMax = ChronoUnit.MINUTES.between(timeDisp, horaCerrar);

                System.out.println("Inserte la duracion del periodo de tiempo a consular en minutos");
                System.out.println("La duración debe superar la media hora y no puede exceder la hora de cierre del centro " + minutMax);
                String duracionDispString = entrada.next();

                int duracionDisp;
                try {
                    duracionDisp = Integer.parseInt(duracionDispString);

                    if ( duracionDisp < 30 || duracionDisp > minutMax){
                        System.out.println("La duracion introducida no es correcta.");
                        System.out.println("No se puede realizar la consulta.");
                        break;
                    }
                } catch(Exception exception) {
                    System.out.println("La duracion tiene que ser un numero");
                    break;
                }        

                // CONSULTA
                
                query1 = "SELECT * FROM espaciofisico WHERE tipo='aula' AND NOT EXISTS (SELECT * from reservaasig WHERE identesp=espaciofisico.identesp AND ((TO_TIMESTAMP('" + 
                fechaDisp + " " + horaDisp + "', 'YYYY-MM-DD HH24:MI') + INTERVAL '" + duracionDisp + "' minute) > fecha_hora) AND ( (fecha_hora + NUMTODSINTERVAL(duracion, 'minute')) > (TO_TIMESTAMP('" + 
                fechaDisp + " " + horaDisp + "', 'YYYY-MM-DD HH24:MI')))) AND NOT EXISTS (SELECT * from reservaact WHERE identesp=espaciofisico.identesp AND ((TO_TIMESTAMP('" + 
                fechaDisp + " " + horaDisp + "', 'YYYY-MM-DD HH24:MI') + INTERVAL '" + duracionDisp + "' minute) > fecha_hora) AND ( (fecha_hora + NUMTODSINTERVAL(duracion, 'minute')) > (TO_TIMESTAMP('" + 
                fechaDisp + " " + horaDisp + "', 'YYYY-MM-DD HH24:MI'))))";

                ResultSet rs3 = sentencia.executeQuery(query1);
                ResultSetMetaData rsmd3 = rs3.getMetaData();
                int numero_columnas3 = rsmd3.getColumnCount();
                System.out.println("AULAS DISPONIBLES EN EL PERIODO SELECCIONADO: ");

                // Imprimimos las columnas
                for (int i = 1; i <= numero_columnas3; i++) {
                    if (i > 1) System.out.print(", ");
                    System.out.print(rsmd3.getColumnName(i).toUpperCase());
                }
                System.out.println();

                // Vamos iterando por las filas e imprimiéndolas
                while (rs3.next()){
                    for (int i = 1; i <= numero_columnas3; i++){
                        if (i > 1) System.out.print(", ");
                        System.out.print(rs3.getString(i));
                    }
                    System.out.println();
                }
                System.out.println();
                rs3.close();

            break;
            case 5:
            // consultar asignaciones a un aula
                System.out.println("Inserte identificador del espacio");
                String identAsignacion = entrada.nextLine();
                if(identAsignacion.length() > 10 || identAsignacion.length() == 0) {
                    System.out.println("Identificador incorrecto (longitud) - no puede ser vacio y no se pueden exceder los 10 caracteres");
                    break;
                }

                query1 = String.format("SELECT tipo FROM espaciofisico WHERE identesp='%s'", 
                        identAsignacion);
                ResultSet rs = sentencia.executeQuery(query1);
                String tipoAsig = null;
                
                if(rs.next())
                    tipoAsig = rs.getString(1);
                else{
                    System.out.println("Ha ocurrido un error fatal. ");
                    System.out.println("Compruebe que el identificador pertenece a un aula");
                    break;
                }

                rs.close();

                if( !tipoAsig.equals("aula")){
                    System.out.println("El espacio introducido ha de ser de tipo 'aula'.");
                    System.out.println("El resto de espacios no se pueden reservar.");
                    break;
                }

                System.out.println("Asignaciones al aula para asignaturas: ");
                query1 = String.format("SELECT * FROM ReservaAsig WHERE identEsp='%s'", identAsignacion);
                ResultSet rs1 = sentencia.executeQuery(query1);
                ResultSetMetaData rsmd1 = rs1.getMetaData();
                int numero_columnas = rsmd1.getColumnCount();

                // Imprimimos las columnas
                for (int i = 1; i <= numero_columnas; i++) {
                    if (i > 1) System.out.print(", ");
                    System.out.print(rsmd1.getColumnName(i).toUpperCase());
                }
                System.out.println();

                // Vamos iterando por las filas e imprimiéndolas
                while (rs1.next()){
                    for (int i = 1; i <= numero_columnas; i++){
                        if (i > 1) System.out.print(", ");
                        System.out.print(rs1.getString(i));
                    }
                    System.out.println();
                }
                System.out.println();
                rs1.close();

                System.out.println("Asignaciones al aula para actividades extraescolares: ");
                query1 = String.format("SELECT * FROM ReservaAct WHERE identEsp='%s'", identAsignacion);
                ResultSet rs2 = sentencia.executeQuery(query1);
                ResultSetMetaData rsmd2 = rs2.getMetaData();
                numero_columnas = rsmd2.getColumnCount();

                // Imprimimos las columnas
                for (int i = 1; i <= numero_columnas; i++) {
                    if (i > 1) System.out.print(", ");
                    System.out.print(rsmd2.getColumnName(i).toUpperCase());
                }
                System.out.println();

                // Vamos iterando por las filas e imprimiéndolas
                while (rs2.next()){
                    for (int i = 1; i <= numero_columnas; i++){
                        if (i > 1) System.out.print(", ");
                        System.out.print(rs2.getString(i));
                    }
                    System.out.println();
                }
                System.out.println();
                rs2.close();

                break;
            case 6:
            // crear reserva para actividad extraescolaar
                // PEDIR FECHA_HORA Y COMPROBAR
                System.out.println("Inserte la fecha y hora de la reserva a crear (yyyy-MM-dd HH:mm)");
                String fechaRes6 = entrada.next();
                String horaRes6 = entrada.next();

                Date dateRes6 = null; 
                try{
                    dateRes6 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fechaRes6 + " " + horaRes6); 
                } catch(Exception e) {
                    // FORMATO INCORRECTO
                    System.out.println("El formato de la fecha no es correto");
                    break;
                }

                if (currentDate.after(dateRes6) ){
                    System.out.println("La reserva debe ser posterior a la fecha actual");
                    break;
                } 

                // Comprobamos que no sea entre semana
                LocalDate dayRes6 = LocalDate.parse(fechaRes6);  
                DayOfWeek diaRes6 = dayRes6.getDayOfWeek();
                if ( diaRes6.getValue() > 5){
                    System.out.println("El fin de semana el centro está cerrado.");
                    System.out.println("No se puede realizar la reserva.");
                    break;
                }

                // Comprobamos que el centro esté abierto en el horario indicado
                LocalTime timeRes6 = LocalTime.parse(horaRes6);
                if ( timeRes6.isBefore(horaAbrir) || timeRes6.isAfter(horaCerrar)){
                    System.out.println("El centro está cerrado a esa hora.");
                    System.out.println("El horario de apertura es de 07:00 a 20:00.");
                    System.out.println("No se puede realizar la consulta.");
                    break;
                }

                // Insertar duración 
                long minutMax6 = ChronoUnit.MINUTES.between(timeRes6, horaCerrar);

                System.out.println("Inserte la duracion de la reserva en minutos");
                System.out.println("La duración debe superar la media hora y no puede exceder la hora de cierre del centro " + minutMax6);
                String duracionResString6 = entrada.next();

                int duracionRes6;
                try {
                    duracionRes6 = Integer.parseInt(duracionResString6);

                    if ( duracionRes6 < 30 || duracionRes6 > minutMax6){
                        System.out.println("La duracion introducida no es correcta.");
                        System.out.println("No se puede realizar la consulta.");
                        break;
                    }
                } catch(Exception exception) {
                    System.out.println("La duracion tiene que ser un numero");
                    break;
                }               

                //MOSTRAR IDENTIFICADOR DE AULAS DISPONIBLES
                System.out.println("Se muestran a continuación los identificadores de las aulas disponibles en dicho instante. ");

                query1 = "SELECT identesp FROM espaciofisico WHERE tipo='aula' AND NOT EXISTS (SELECT * from reservaasig WHERE identesp=espaciofisico.identesp AND ((TO_TIMESTAMP('" + 
                fechaRes6 + " " + horaRes6 + "', 'YYYY-MM-DD HH24:MI') + INTERVAL '" + duracionRes6 + "' minute) > fecha_hora) AND ( (fecha_hora + NUMTODSINTERVAL(duracion, 'minute')) > (TO_TIMESTAMP('" + 
                fechaRes6 + " " + horaRes6 + "', 'YYYY-MM-DD HH24:MI')))) AND NOT EXISTS (SELECT * from reservaact WHERE identesp=espaciofisico.identesp AND ((TO_TIMESTAMP('" + 
                fechaRes6 + " " + horaRes6 + "', 'YYYY-MM-DD HH24:MI') + INTERVAL '" + duracionRes6 + "' minute) > fecha_hora) AND ( (fecha_hora + NUMTODSINTERVAL(duracion, 'minute')) > (TO_TIMESTAMP('" + 
                fechaRes6 + " " + horaRes6 + "', 'YYYY-MM-DD HH24:MI'))))";

                ResultSet rs6 = sentencia.executeQuery(query1);
                ArrayList<String> espacios6 = new ArrayList<String>();

                // Vamos iterando por las filas e imprimiéndolas
                while (rs6.next()){
                        System.out.println(rs6.getString(1));
                        espacios6.add(rs6.getString(1));
                }
                rs6.close();

                // INSERTAR IDENTESP A RESERVAR
                System.out.println("Seleccione el espacio a reservar entre los disponibles. ");
                String espacioRes6 = entrada.next();

                if( !espacios6.contains(espacioRes6)){
                    System.out.println("No es un identificador válido. ");
                    break;
                }

                // IDENTIFICADOR DE LA ACTIVIDAD EXTRAESCOLAR
                System.out.println("Introduzca el identificador de la actividad para la que reserva el aula. ");
                String identAct = entrada.next();

                query1 = String.format("SELECT * FROM ActividadExtraescolar WHERE Codigo_act='%s' ", 
                    identAct);
                ResultSet rs7 = sentencia.executeQuery(query1);
                
                if(!rs7.next()){
                    System.out.println("Ha ocurrido un error fatal. ");
                    System.out.println("Compruebe que el identificador concuerda con una actividad");
                    break;
                }

                rs7.close();

                query1 = String.format("INSERT INTO ReservaAct VALUES('%s', TO_TIMESTAMP('" + fechaRes6 + " " + horaRes6  +"', 'YYYY-MM-DD HH24:MI'), '%s', %d)", 
                    espacioRes6, identAct, duracionRes6);
                    
                try {
                    sentencia.executeQuery(query1);

                    conexion.commit();
                    System.out.println("Se ha agregado la reserva de manera satisfactoria");
                } catch(Exception e) {
                    // ERROR NO IDENTIFICADO
                    e.printStackTrace();
                    //System.out.println("No se ha podido realizar la operacion. ");
                }


            break;
            default:
                 System.out.println("Codigo no reconocido");
        };
    }
}