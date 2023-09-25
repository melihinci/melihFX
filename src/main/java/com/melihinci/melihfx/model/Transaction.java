package com.melihinci.melihfx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
@SequenceGenerator(name = "TRANSACTION_SEQ", initialValue = 1000000)
public class Transaction {
    @Id
    @Column
    @GeneratedValue(generator = "TRANSACTION_SEQ", strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String currencyExchangeCode;

    @Column
    private double amount;

    @Column
    private double targetAmount;

    @Column
    Date processedDate;

    @Column
    Long currencyLogId;

    @JsonIgnore
    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="currencyLogId",insertable = false,updatable = false)
    private CurrencyLog currencyLog;

}
