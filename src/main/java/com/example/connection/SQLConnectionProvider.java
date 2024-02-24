package com.example.connection;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@AllArgsConstructor
public class SQLConnectionProvider implements ConnectionProvider {
    private final String url;
    private final String userName;
    private final String password;

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, userName, password);
    }
}
