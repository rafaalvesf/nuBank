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

import static org.junit.jupiter.api.Assertions.*;


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
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(1).getTax());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(2).getTax());
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
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax());
        assertEquals(new BigDecimal("10000.00"), result.get(1).getTax());
    }
    @Test
    @DisplayName("Caso 3: Prejuízo seguido de lucro com compensação")
    void testCaso3_CompensacaoPrejuizo() {
        List<TransacaoDto> transacoes = Arrays.asList(
                new TransacaoDto("buy", new BigDecimal("10.00"), 10000),
                new TransacaoDto("sell", new BigDecimal("5.00"), 5000),
                new TransacaoDto("sell", new BigDecimal("20.00"), 3000)
        );

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(3, result.size());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(1).getTax());
        assertEquals(new BigDecimal("1000.00"), result.get(2).getTax());
    }
    @Test
    @DisplayName("Caso 4: Múltiplas compras e venda sem lucro")
    void testCaso4_SemLucro() {
        List<TransacaoDto> transacoes = Arrays.asList(
                new TransacaoDto("buy", new BigDecimal("10.00"), 10000),
                new TransacaoDto("buy", new BigDecimal("25.00"), 5000),
                new TransacaoDto("sell", new BigDecimal("15.00"), 10000)
        );
        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(3, result.size());
        assertAll(
                () -> assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax()),
                () -> assertEquals(BigDecimal.ZERO.setScale(1), result.get(1).getTax()),
                () -> assertEquals(BigDecimal.ZERO.setScale(1), result.get(2).getTax())
        );
    }

    @Test
    @DisplayName("Teste com operação de compra única")
    void testApenasCompra_NaoGeraImposto() {
        List<TransacaoDto> transacoes = List.of(
                new TransacaoDto("buy", new BigDecimal("100.00"), 100)
        );

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(1, result.size());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax());
    }

    @Test
    @DisplayName("Teste venda com valor exatamente no limite de R$20000")
    void testVendaNoLimite_Isento() {
        List<TransacaoDto> transacoes = Arrays.asList(
                new TransacaoDto("buy", new BigDecimal("100.00"), 200),
                new TransacaoDto("sell", new BigDecimal("100.00"), 200)
        );

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(2, result.size());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(1).getTax());
    }

    @Test
    @DisplayName("Teste venda com valor R$20001 - acima do limite")
    void testVendaAcimaLimite_Tributavel() {
        List<TransacaoDto> transacoes = Arrays.asList(
                new TransacaoDto("buy", new BigDecimal("100.00"), 200),
                new TransacaoDto("sell", new BigDecimal("100.005"), 200)
        );

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(2, result.size());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax());
        assertEquals(new BigDecimal("0.20"), result.get(1).getTax());
    }

    @Test
    @DisplayName("Teste com múltiplos prejuízos acumulados")
    void testMultiplosPrejuizosAcumulados() {
        List<TransacaoDto> transacoes = Arrays.asList(
                new TransacaoDto("buy", new BigDecimal("50.00"), 1000),
                new TransacaoDto("sell", new BigDecimal("40.00"), 500),
                new TransacaoDto("sell", new BigDecimal("45.00"), 500)
        );

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(3, result.size());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(1).getTax());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(2).getTax());
    }

    @Test
    @DisplayName("Teste venda zerando posição e recomprando")
    void testZeraPosicao_ReiniciaMedia() {
        List<TransacaoDto> transacoes = Arrays.asList(
                new TransacaoDto("buy", new BigDecimal("30.00"), 1000),
                new TransacaoDto("sell", new BigDecimal("40.00"), 1000),
                new TransacaoDto("buy", new BigDecimal("50.00"), 500),
                new TransacaoDto("sell", new BigDecimal("60.00"), 500)
        );

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(4, result.size());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax());
        assertEquals(new BigDecimal("2000.00"), result.get(1).getTax());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(2).getTax());
        assertEquals(new BigDecimal("1000.00"), result.get(3).getTax());
    }

    @Test
    @DisplayName("Teste com valores muito altos")
    void testValoresMuitoAltos() {

        List<TransacaoDto> transacoes = Arrays.asList(
                new TransacaoDto("buy", new BigDecimal("1000.00"), 10000),
                new TransacaoDto("sell", new BigDecimal("1200.00"), 5000)
        );

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(2, result.size());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax());
        assertEquals(new BigDecimal("200000.00"), result.get(1).getTax());
    }

    @Test
    @DisplayName("Teste venda parcial com média ponderada complexa")
    void testVendaParcialMediaComplexa() {
        List<TransacaoDto> transacoes = Arrays.asList(
                new TransacaoDto("buy", new BigDecimal("10.00"), 10000),
                new TransacaoDto("buy", new BigDecimal("12.00"), 5000),
                new TransacaoDto("sell", new BigDecimal("15.00"), 8000)
        );

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(3, result.size());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(1).getTax());
        assertEquals(new BigDecimal("6928.00"), result.get(2).getTax());
    }

    @Test
    @DisplayName("Teste lista vazia")
    void testListaVazia() {
        List<TransacaoDto> transacoes = List.of();

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Teste apenas vendas sem compras anteriores")
    void testApenasVendas_SemCompra() {
        List<TransacaoDto> transacoes = List.of(
                new TransacaoDto("sell", new BigDecimal("100.00"), 100)
        );

        List<TaxDto> result = transacaoService.executarTransacao(transacoes);

        assertEquals(1, result.size());
        assertEquals(BigDecimal.ZERO.setScale(1), result.get(0).getTax());
    }
}