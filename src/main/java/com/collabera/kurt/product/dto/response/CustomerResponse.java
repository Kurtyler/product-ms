package com.collabera.kurt.product.dto.response;

import com.collabera.kurt.product.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private Integer customerId;
    private String name;
    private String email;
    private String gender;

    public CustomerResponse(Customer customer){
        this.setCustomerId(customer.getCustomerId());
        this.setName(customer.getName());
        this.setEmail(customer.getEmail());
        this.setGender(customer.getGender());
    }
}
