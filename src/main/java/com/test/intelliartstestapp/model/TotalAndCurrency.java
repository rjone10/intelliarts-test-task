package com.test.intelliartstestapp.model;

import java.math.BigDecimal;

public class TotalAndCurrency {
    private BigDecimal total;
    private Currency currency;

    public TotalAndCurrency(BigDecimal total, Currency currency) {
        this.total = total;
        this.currency = currency;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
