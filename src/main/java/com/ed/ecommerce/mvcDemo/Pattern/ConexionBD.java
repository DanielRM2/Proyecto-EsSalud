package com.ed.ecommerce.mvcDemo.Pattern;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConexionBD {

    // Variable estática que maneja el pool de conexiones utilizando HikariCP
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();

        // Configuración de conexión a la base de datos CleverCloud (MySQL)
        config.setJdbcUrl("jdbc:mysql://beukuvlrhrmlx8gqe7xo-mysql.services.clever-cloud.com:3306/beukuvlrhrmlx8gqe7xo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima");
        config.setUsername("us5cns1bqofgsggs");
        config.setPassword("sdRA1urjC406Qc95V68g");

        // Parámetros
        config.setMaximumPoolSize(5);       // Máximo de 5 conexiones activas al mismo tiempo
        config.setMinimumIdle(1);           // Mantener mínimo 1 conexión en espera
        config.setIdleTimeout(30000);       // Tiempo máximo (30 seg) que una conexión inactiva permanece antes de cerrarse
        config.setMaxLifetime(600000);      // Tiempo máximo de vida de una conexión (10 minutos), luego se renueva
        config.setConnectionTimeout(30000); // Tiempo máximo de espera (30 seg) para obtener una conexión disponible

        // Inicialización del pool de conexiones
        dataSource = new HikariDataSource(config);
    }

    //Obtiene una conexión del pool
    public static Connection getConexion() throws SQLException {
        return dataSource.getConnection();
    }

    //Cierra el pool de conexiones de manera segura
    public static void cerrarPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
