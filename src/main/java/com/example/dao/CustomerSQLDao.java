package com.example.dao;

import com.example.connection.ConnectionProvider;
import com.example.entity.Customer;
import com.example.exception.SQLRuntimeException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;
import java.util.Optional;

public class CustomerSQLDao implements CustomerDao {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM customers WHERE id = ?;";
    private static final String SAVE_CUSTOMER = "INSERT INTO customers (id, name, sex, email, phone_number, address) VALUES(?, ?, ?, ?, ?, ?);";
    private final ConnectionProvider connectionProvider;

    public CustomerSQLDao(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<Customer> findById(String id) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_QUERY)) {

            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Customer customer = Customer.builder()
                        .id(resultSet.getInt("id"))
                        .name(resultSet.getString("name"))
                        .sex(resultSet.getString("sex"))
                        .email(resultSet.getString("email"))
                        .phoneNumber(resultSet.getString("phone_number"))
                        .address(resultSet.getString("address"))
                        .build();

                return Optional.ofNullable(customer);
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @Override
    public Customer save(Customer customer) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_CUSTOMER)) {

            setCustomerStatementParams(customer, preparedStatement);

            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();

            try {
                int fieldsAffected = preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback(savepoint);
                throw new SQLRuntimeException(e);
            }

            connection.setAutoCommit(true);
            return customer;

        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @Override
    public List<Customer> saveAll(List<Customer> customers) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_CUSTOMER)) {
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();

            for (Customer customer : customers) {
                setCustomerStatementParams(customer, preparedStatement);
                preparedStatement.addBatch();
            }

            try {
                preparedStatement.executeBatch();
            } catch (SQLException e) {
                connection.rollback(savepoint);
                throw new SQLRuntimeException(e);
            }

            connection.setAutoCommit(true);
            return customers;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private static void setCustomerStatementParams(Customer customer, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, String.valueOf(customer.getId()));
        preparedStatement.setString(2, customer.getName());
        preparedStatement.setString(3, customer.getSex());
        preparedStatement.setString(4, customer.getEmail());
        preparedStatement.setString(5, customer.getPhoneNumber());
        preparedStatement.setString(6, customer.getAddress());
    }
}
