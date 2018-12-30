package ru.mai.db.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {                             //синглтон ленивый
    private static DatabaseConnection instance = null;

    private Connection connection = null;

    private String url = "jdbc:mysql://localhost:3306/test"+    //где test - название database
            "?useLegacyDatetimeCode=false"+
            "&serverTimezone=UTC";
    private String username = "root";
    private String password = "";

    private DatabaseConnection() throws SQLException {      //закрытый конструктор
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException ex) {
            System.out.println("Создание подключения провалено: " + ex.getMessage());
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else if (instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
