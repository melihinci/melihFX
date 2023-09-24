package com.melihinci.melihfx.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency implements Serializable {

    public Currency(String source,String target){
        this.source=source;
        this.target=target;
    }

    @Id
    @Column
    private String exchangeCode;
    @Column
    private String source;
    @Column
    private String target;
    @Column
    private LocalDateTime timestamp;
    @Column
    private double exchangeRate;

}

