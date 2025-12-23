package com.nubank.nubank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransacaoDto {
    private String operation;
    @JsonProperty("unit-cost")
    private BigDecimal unitCost;
    private Integer quantity;
}
