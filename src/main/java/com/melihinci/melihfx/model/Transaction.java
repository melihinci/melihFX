package com.melihinci.melihfx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
public class Transaction {
    @Id
    @Column
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
