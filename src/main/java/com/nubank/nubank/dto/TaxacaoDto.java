package com.nubank.nubank.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TaxacaoDto {
    private BigDecimal taxa;

    public TaxacaoDto(BigDecimal taxa){
        this.taxa = taxa;
    }
}
