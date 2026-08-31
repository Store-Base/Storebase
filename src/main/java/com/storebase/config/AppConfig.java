package com.storebase.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppConfig {

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static Connection connection;

    private AppConfig() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                if (URL == null || USER == null || PASSWORD == null) {
                    throw new IllegalStateException(
                            "Variáveis de ambiente DB_URL, DB_USER e DB_PASSWORD precisam estar definidas.");
                }
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados: " + e.getMessage(), e);
        }
    }
}
