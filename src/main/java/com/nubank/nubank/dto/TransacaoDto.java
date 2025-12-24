package com.nubank.nubank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoDto {
    private String operation;
    @JsonProperty("unit-cost")
    private BigDecimal unitCost;
    private Integer quantity;
}
