package com.example.dao;

import com.example.connection.ConnectionProvider;
import com.example.connection.SQLConnectionProvider;
import com.example.entity.Customer;
import com.example.exception.SQLRuntimeException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CustomerSQLDaoTest {
    static final String H2_DB_URL = "jdbc:h2:~/test";
    static final String H2_DB_USER = "sa";
    static final String H2_DB_PASSWORD = "";
    static String CUSTOMERS_TABLE_INIT_QUERY;

    static ConnectionProvider connectionProvider;
    static CustomerSQLDao customerDao;

    private CustomerSQLDaoTest() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/customers_initialization.sql"))) {
            StringBuilder sb = new StringBuilder();

            while (reader.ready()) {
                sb.append(reader.readLine());
            }

            CUSTOMERS_TABLE_INIT_QUERY = sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void initializeCustomerSQLDao() {
        connectionProvider = new SQLConnectionProvider(H2_DB_URL, H2_DB_USER, H2_DB_PASSWORD);
        customerDao = new CustomerSQLDao(connectionProvider);
    }

    @BeforeEach
    void initializeCustomersTable() {
        if (CUSTOMERS_TABLE_INIT_QUERY != null) {
            try (Connection connection = connectionProvider.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(CUSTOMERS_TABLE_INIT_QUERY)) {

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        } else {
            throw new RuntimeException("Customers initialization query has not been initialized");
        }
    }

    @Order(1)
    @ParameterizedTest
    @MethodSource("getExistingCustomers")
    @DisplayName("findById() should return an existing Customer object")
    void findByIdShouldReturnExistingCustomer(List<Customer> expectedCustomers) {
        List<Customer> actualCustomers = Arrays.asList(
                customerDao.findById("1").orElse(null),
                customerDao.findById("6").orElse(null),
                customerDao.findById("13").orElse(null)
        );

        IntStream.range(0, actualCustomers.size())
                .forEach(i -> assertEquals(expectedCustomers.get(i), actualCustomers.get(i)));
    }

    @Test
    @DisplayName("findById() should return Optional.empty() if such customer isn't present in DB")
    void findByIdShouldReturnOptionalEmpty() {
        assertEquals(Optional.empty(), customerDao.findById("33"));
    }

    @Test
    @DisplayName("findById() should throw SQLRuntimeException")
    void findByIdShouldThrowSqlRuntimeException() throws SQLException {
        Connection mockedConnection = Mockito.mock(Connection.class);
        ConnectionProvider mockedConnectionProvider = Mockito.mock(ConnectionProvider.class);

        Mockito.when(mockedConnection.prepareStatement("SELECT * FROM customers WHERE id = ?;")).thenThrow(SQLException.class);
        Mockito.when(mockedConnectionProvider.getConnection()).thenReturn(mockedConnection);

        CustomerSQLDao customerSQLDao = new CustomerSQLDao(mockedConnectionProvider);
        assertThrows(SQLRuntimeException.class, () -> customerSQLDao.findById("4"));
    }

    @Test
    @DisplayName("save() should insert passed Customer object to the DB and return it")
    void saveShouldInsertPassedCustomerToDb() {
        Customer expectedCustomer = new Customer(16, "Rich Piana", "male", "richPiana@gmail.com", "6835745687", "543 Tertiary St");
        Customer actualCustomer = customerDao.save(expectedCustomer);
        Customer retrievedCustomer = customerDao.findById("16").orElse(null);

        assertEquals(expectedCustomer, actualCustomer);
        assertEquals(expectedCustomer, retrievedCustomer);
    }

    @Test
    @DisplayName("save() should throw SQLRuntimeException if invalid Customer object was passed")
    void saveShouldThrowSqlException() {
        Customer invalidCustomer = new Customer(99, "John Deere", "invalid sex", "johnDeere@gmail.com", "6835745687", "543 Tertiary St");

        assertThrows(SQLRuntimeException.class, () -> customerDao.save(invalidCustomer));
    }

    @ParameterizedTest
    @MethodSource("getTestCustomers")
    @DisplayName("saveAll() should save all Customers objects passed in a List and return the exact same list")
    void saveAllShouldSaveAllPassedInListCustomers(List<Customer> expectedCustomers) {
        List<Customer> actualCustomers = customerDao.saveAll(expectedCustomers);

        List<Customer> retrievedCustomers = new ArrayList<>();
        for (Customer customer : expectedCustomers) {
            Customer retrievedCustomer = customerDao.findById(String.valueOf(customer.getId())).orElse(null);
            retrievedCustomers.add(retrievedCustomer);
        }

        assertIterableEquals(expectedCustomers, actualCustomers);
        assertIterableEquals(expectedCustomers, retrievedCustomers);
    }

    @Test
    @DisplayName("saveAll() should throw SQLRuntimeException if invalid Customer object was passed")
    void saveAllShouldThrowSqlException() {
        List<Customer> invalidCustomers = List.of(
                new Customer(2, "Walter White", "male", "walterWhite@gmail.com", "3242934842", "Huston TX"),
                new Customer(99, "John Deere", "invalid sex", "johnDeere@gmail.com", "6835745687", "543 Tertiary St")
        );

        assertThrows(SQLRuntimeException.class, () -> customerDao.saveAll(invalidCustomers));
    }

    static Stream<Arguments> getExistingCustomers() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new Customer(1, "John Doe", "male", "john.doe@example.com", "1234567890", "123 Main St"),
                                new Customer(6, "Diana Prince", "female", "diana.prince@example.com", "0001112222", "987 Main St"),
                                new Customer(13, "Kevin Hart", "male", "kevin.hart@example.com", "1112223334", "123 Tertiary St")
                        )
                )
        );
    }

    static Stream<Arguments> getTestCustomers() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new Customer(16, "Test Customer 1", "male", "test1@example.com", "1234567890", "123 Test St"),
                                new Customer(17, "Test Customer 2", "female", "test2@example.com", "0987654321", "456 Test St"),
                                new Customer(18, "Test Customer 3", "male", "test3@example.com", "1112223333", "789 Test St"),
                                new Customer(19, "Test Customer 4", "female", "test4@example.com", "4445556666", "321 Test St"),
                                new Customer(20, "Test Customer 5", "male", "test5@example.com", "7778889999", "654 Test St")
                        )
                )
        );
    }
}