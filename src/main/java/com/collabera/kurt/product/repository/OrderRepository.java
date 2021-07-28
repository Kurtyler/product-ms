package com.collabera.kurt.product.repository;

import com.collabera.kurt.product.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findOrderByCustomersCustomerId(Integer customerId);

}
