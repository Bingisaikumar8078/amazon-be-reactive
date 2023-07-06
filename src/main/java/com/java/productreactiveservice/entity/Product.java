package com.java.productreactiveservice.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Product {


    @Id
    public Integer id;


    @Column
    public UUID productId;
    @Column
    public String name;
    @Column
    public double price;
    @Column
    public Integer rating;
    @Column
    public String imageurl;
    @Column
    public String type;
    @Column
    public String brand;
}