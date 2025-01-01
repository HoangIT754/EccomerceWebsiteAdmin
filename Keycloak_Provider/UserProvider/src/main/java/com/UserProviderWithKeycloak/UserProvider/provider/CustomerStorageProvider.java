package com.UserProviderWithKeycloak.UserProvider.provider;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.UserProviderWithKeycloak.UserProvider.model.Customer;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class CustomerStorageProvider implements UserStorageProvider,
        UserRegistrationProvider,
        UserQueryProvider,
        UserLookupProvider,
        CredentialInputValidator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomerStorageProvider.class);

    private ComponentModel componentModel;
    private KeycloakSession keycloakSession;
    private Connection connection;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public void setModel(ComponentModel componentModel) {
        this.componentModel = componentModel;
    }

    public void setSession(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void close() {
        logger.info("Closing database connection.");
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            logger.error("Error closing database connection", e);
        }
    }

    // 1. Tìm người dùng theo username
    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String username) {
        logger.info("Attempting to find user by username: {}", username);
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.info("User found with username: {}", username);
                Customer customer = mapRowToCustomer(rs);
                return new CustomerAdapter(keycloakSession, realmModel, componentModel, customer);
            }else{
                logger.warn("No user found with username: {}", username);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username", e);
        }
        return null;
    }

    // 2. Tìm người dùng theo email
    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String email) {
        logger.info("Attempting to find user by email: {}", email);
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.info("User found with email: {}", email);
                Customer customer = mapRowToCustomer(rs);
                return new CustomerAdapter(keycloakSession, realmModel, componentModel, customer);
            }else {
                logger.warn("No user found with email: {}", email);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email", e);
        }
        return null;
    }

    // 3. Tìm người dùng theo ID
    @Override
    public UserModel getUserById(RealmModel realmModel, String id) {
        logger.info("Attempting to find user by ID: {}", id);
        UUID persistenceId = UUID.fromString(StorageId.externalId(id));
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, persistenceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.info("User found with ID: {}", id);
                Customer customer = mapRowToCustomer(rs);
                return new CustomerAdapter(keycloakSession, realmModel, componentModel, customer);
            }else{
                logger.warn("No user found with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID", e);
        }
        return null;
    }

    // 4. Thêm người dùng mới
    @Override
    public UserModel addUser(RealmModel realmModel, String username) {
        logger.info("Attempting to add user with username: {}", username);
        String query = "INSERT INTO users (id, username, active) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            UUID newId = UUID.randomUUID();
            stmt.setObject(1, newId);
            stmt.setString(2, username);
            stmt.setInt(3, 1);
            stmt.executeUpdate();
            logger.info("User successfully added with username: {}", username);

            Customer customer = new Customer();
            customer.setId(newId);
            customer.setUserName(username);
            customer.setActive(1);

            return new CustomerAdapter(keycloakSession, realmModel, componentModel, customer);
        } catch (SQLException e) {
            logger.error("Error adding user", e);
        }
        return null;
    }

    public boolean isUserExist(String username) {
        return false ;
    }

    // 5. Xóa người dùng
    @Override
    public boolean removeUser(RealmModel realmModel, UserModel userModel) {
        logger.info("Attempting to remove user with ID: {}", userModel.getId());
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            UUID persistenceId = UUID.fromString(StorageId.externalId(userModel.getId()));
            stmt.setObject(1, persistenceId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User successfully removed with ID: {}", userModel.getId());
                return true;
            } else {
                logger.warn("No user found with ID: {} to remove", userModel.getId());
            }
        } catch (SQLException e) {
            logger.error("Error removing user", e);
        }
        return false;
    }

    // 6. Xác thực mật khẩu
    @Override
    public boolean supportsCredentialType(String credentialType) {
        return "password".equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        logger.info("Validating credentials for user: {}", user.getUsername());
        if (!supportsCredentialType(credentialInput.getType())) {
            logger.warn("Unsupported credential type: {}", credentialInput.getType());
            return false;
        }

        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                logger.info("storedPassword: ", storedPassword);
                boolean isValid = passwordEncoder.matches(credentialInput.getChallengeResponse(), storedPassword);
                logger.info("Credential validation result for user {}: {}", user.getUsername(), isValid);
                return isValid;
            } else {
                logger.warn("No password found for user: {}", user.getUsername());
            }
        } catch (SQLException e) {
            logger.error("Error validating credentials for user", e);
        }
        return false;
    }


    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realmModel, Map<String, String> map, Integer firstResult, Integer maxResults) {
        String searchParam = map.getOrDefault("email", map.getOrDefault("username", ""));
        logger.info("Searching for users with parameter: {}", searchParam);

        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM users WHERE email LIKE ? OR username LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + searchParam + "%");
            stmt.setString(2, "%" + searchParam + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                customers.add(mapRowToCustomer(rs));
            }
            logger.info("Found {} users matching search parameter: {}", customers.size(), searchParam);
        } catch (SQLException e) {
            logger.error("Error searching for users", e);
        }

        return customers.stream().map(customer -> new CustomerAdapter(keycloakSession, realmModel, componentModel, customer));
    }

    private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
        logger.info("Mapping ResultSet to Customer object");
        Customer customer = new Customer();
        customer.setId(rs.getObject("id", UUID.class));
        customer.setUserName(rs.getString("username"));
        customer.setEmail(rs.getString("email"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setPassword(rs.getString("password"));
        customer.setActive(rs.getInt("active"));
        logger.info("Mapped Customer: {}", customer.getUserName());
        return customer;
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realmModel, GroupModel groupModel, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realmModel, String attribute, String value) {
        return Stream.empty();
    }

    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType())) {
            logger.warn("Unsupported credential type: {}", input.getType());
            return false;
        }

        if (input.getChallengeResponse() == null || input.getChallengeResponse().isEmpty()) {
            logger.warn("Invalid password: password is null or empty for user {}", user.getUsername());
            return false;
        }

        try {
            String encodedPassword = passwordEncoder.encode(input.getChallengeResponse());

            boolean updated = updateUserPasswordInDatabase(user.getUsername(), encodedPassword);
            if (updated) {
                logger.info("Password successfully updated for user: {}", user.getUsername());
                return true;
            } else {
                logger.warn("Failed to update password in database for user: {}", user.getUsername());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error while updating password for user: {}", user.getUsername(), e);
            return false;
        }
    }

    private boolean updateUserPasswordInDatabase(String username, String encodedPassword) throws SQLException {
        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, encodedPassword);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}