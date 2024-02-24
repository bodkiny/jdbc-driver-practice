package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
public class Customer {

    @ToString.Include(rank = 1)
    private Integer id;

    private String name;

    private String sex;

    @ToString.Include
    private String email;

    private String phoneNumber;

    private String address;
}

