package com.collabera.kurt.product.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "customer_id",
            foreignKey = @ForeignKey(name = "fk_customer_id"),
            referencedColumnName = "customerId",
            nullable = false
    )
    private Customer customers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "product_id",
            foreignKey = @ForeignKey(name = "fk_product_id"),
            referencedColumnName = "productId",
            nullable = false
    )
    private Product products;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String status;


}
