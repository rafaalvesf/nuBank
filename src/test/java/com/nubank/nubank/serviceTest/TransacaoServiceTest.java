package com.nubank.nubank.serviceTest;

import com.nubank.nubank.dto.TaxDto;
import com.nubank.nubank.dto.TransacaoDto;
import com.nubank.nubank.service.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


class TransacaoServiceTest {

    private TransacaoService transacaoService;

    @BeforeEach
    void setUp() {
        transacaoService = new TransacaoService();
    }

    @Test
    @DisplayName("Caso 1: Compra e vendas sem imposto (valor < 20000)")
    void testCaso1_VendasSemImposto() {
        List<TransacaoDto> transacoes = Arrays.asList(
                new TransacaoDto("buy", new BigDecimal("10.00"), 100),
                new TransacaoDto("sell", new BigDecimal("15.00"), 50),
                new TransacaoDto("sell", new BigDecimal("15.00"), 50)
        );

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(3, result.size());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(0).getTax());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(1).getTax());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(2).getTax());
    }

    @Test
    @DisplayName("Caso 2: Lucro com imposto")
    void testCaso2_LucroComImposto() {
        List<TransacaoDto> transacoes = Arrays.asList(
                new TransacaoDto("buy", new BigDecimal("10.00"), 10000),
                new TransacaoDto("sell", new BigDecimal("20.00"), 5000)
        );

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(2, result.size());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(0).getTax());
        assertEquals(new BigDecimal("10000.00"), result.get(1).getTax());
    }
}