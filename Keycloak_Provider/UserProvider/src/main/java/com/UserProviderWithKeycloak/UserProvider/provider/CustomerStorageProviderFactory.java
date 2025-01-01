package com.UserProviderWithKeycloak.UserProvider.provider;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CustomerStorageProviderFactory implements UserStorageProviderFactory<CustomerStorageProvider> {

    private static final Logger logger = LoggerFactory.getLogger(CustomerStorageProviderFactory.class);
    private static final String URL = "jdbc:postgresql://application-db:5432/application";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    private Connection connection;

    @Override
    public CustomerStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        try {
            CustomerStorageProvider customerStorageProvider = new CustomerStorageProvider();
            customerStorageProvider.setModel(componentModel);
            customerStorageProvider.setSession(keycloakSession);
            customerStorageProvider.setConnection(getConnection());
            return customerStorageProvider;
        } catch (Exception e) {
            logger.error("Error creating CustomerStorageProvider", e);
            throw new RuntimeException("Failed to create CustomerStorageProvider", e);
        }
    }

    private synchronized Connection getConnection() {
        if (connection == null || !isConnectionValid()) {
            int attempts = 0;
            SQLException lastException = null;
            while (attempts < 3) {
                try {
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    logger.info("Successfully connected to the PostgreSQL database.");
                    return connection;
                } catch (SQLException e) {
                    attempts++;
                    lastException = e;
                    logger.error("Failed to connect to the database. Attempt: {}", attempts);
                }
            }
            throw new RuntimeException("Unable to connect to the database after 3 attempts", lastException);
        }
        return connection;
    }

    private boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public String getId() {
        return "customer-user-provider";
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.error("Failed to close database connection", e);
        }
    }
}