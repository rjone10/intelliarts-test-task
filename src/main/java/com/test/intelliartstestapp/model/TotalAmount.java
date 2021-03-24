package com.test.intelliartstestapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Entity
@Table(name = "total")
public class TotalAmount {
    @Id
    @Column
    private Long id;

    @Column(name = "total_amount")
    private String totalAmount;

    public BigDecimal getTotalAmount() {
        return new BigDecimal(totalAmount);
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount.toString();
    }
}