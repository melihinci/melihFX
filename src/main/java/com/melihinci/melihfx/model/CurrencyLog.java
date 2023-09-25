package com.melihinci.melihfx.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
@SequenceGenerator(name = "CURRENCY_LOG_SEQ", initialValue = 100000)
public class CurrencyLog implements Serializable {
    @Id
    @GeneratedValue(generator = "CURRENCY_LOG_SEQ", strategy = GenerationType.AUTO)
    @Column
    private Long id;
    @Column
    private String currencyId;
    @Column
    private String source;
    @Column
    private String target;
    @Column
    private Date processedDate;
    @Column
    private double exchangeRate;

    public CurrencyLog(Currency currency) {
        this.currencyId = currency.getExchangeCode();
        this.exchangeRate = currency.getExchangeRate();
        this.source = currency.getSource();
        this.target = currency.getTarget();
        this.processedDate = new Date();
    }
}
