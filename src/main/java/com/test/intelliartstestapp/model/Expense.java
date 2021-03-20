package com.test.intelliartstestapp.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency", columnDefinition = "enum('UAH','USD','EUR','PLN')")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "product")
    private String product;

}
