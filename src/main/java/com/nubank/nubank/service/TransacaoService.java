package com.nubank.nubank.service;

import com.nubank.nubank.dto.TaxacaoDto;
import com.nubank.nubank.dto.TransacaoDto;
import lombok.NoArgsConstructor;
import org.jvnet.hk2.annotations.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@NoArgsConstructor
public class TransacaoService {
    public List<TaxacaoDto> executarTransacao(List<TransacaoDto> transacoes) {

        BigDecimal imposto = BigDecimal.ZERO;

        return List.of(new TaxacaoDto(imposto));
    }
}
