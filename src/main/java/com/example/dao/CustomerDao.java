package com.example.dao;

import com.example.entity.Customer;

import java.util.List;
import java.util.Optional;

// controller ---> service --> repo/dao
public interface CustomerDao {
    Optional<Customer> findById(String id);

    Customer save(Customer customer);

    List<Customer> saveAll(List<Customer> customers);
}
