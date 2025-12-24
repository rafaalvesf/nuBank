package com.nubank.nubank.service;


import com.nubank.nubank.dto.TaxDto;
import com.nubank.nubank.dto.TransacaoDto;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class TransacaoService {
    public List<TaxDto> executarTransacao(List<TransacaoDto> transacoes) {

        List<TaxDto> tax = new ArrayList<>();
        BigDecimal mediaPonderada = BigDecimal.ZERO;
        int quantidadeDeAcoesAtual = 0;
        BigDecimal prejuizoAcumulado = BigDecimal.ZERO;
        final BigDecimal VALOR_LIVRE_IMPOSTO = BigDecimal.valueOf(20000.00);
        final BigDecimal TAXA_IMPOSTO = new BigDecimal("0.20");

        for(TransacaoDto transacao : transacoes){
            TaxDto taxDto = new TaxDto();

            if ("buy".equals(transacao.getOperation())) {

                BigDecimal custoAtual = mediaPonderada.multiply(BigDecimal.valueOf(quantidadeDeAcoesAtual));
                BigDecimal custoNovaCompra = transacao.getUnitCost().multiply(BigDecimal.valueOf(transacao.getQuantity()));
                int novaQuantidade = quantidadeDeAcoesAtual + transacao.getQuantity();

                mediaPonderada = custoAtual.add(custoNovaCompra).divide(BigDecimal.valueOf(novaQuantidade),2,RoundingMode.HALF_UP);

                quantidadeDeAcoesAtual = novaQuantidade;

                taxDto.setTax(BigDecimal.ZERO.setScale(1));
            }
            else{

                BigDecimal lucroTributavel = (transacao.getUnitCost().subtract(mediaPonderada)).multiply(BigDecimal.valueOf(transacao.getQuantity()));
                BigDecimal valorDaOperacao = transacao.getUnitCost().multiply(BigDecimal.valueOf(transacao.getQuantity()));
                quantidadeDeAcoesAtual = quantidadeDeAcoesAtual - transacao.getQuantity();

                if (valorDaOperacao.compareTo(VALOR_LIVRE_IMPOSTO) > 0) {

                    if (lucroTributavel.compareTo(BigDecimal.ZERO) > 0 &&
                            prejuizoAcumulado.compareTo(BigDecimal.ZERO) > 0) {

                        if (lucroTributavel.compareTo(prejuizoAcumulado) >= 0) {
                            lucroTributavel = lucroTributavel.subtract(prejuizoAcumulado);
                            prejuizoAcumulado = BigDecimal.ZERO;
                        } else {
                            prejuizoAcumulado = prejuizoAcumulado.subtract(lucroTributavel);
                            lucroTributavel = BigDecimal.ZERO;
                        }
                    }

                    if (lucroTributavel.compareTo(BigDecimal.ZERO) > 0) {
                        taxDto.setTax(lucroTributavel.multiply(TAXA_IMPOSTO)
                                .setScale(2, RoundingMode.HALF_UP));
                    } else {
                        if (lucroTributavel.compareTo(BigDecimal.ZERO) < 0) {
                            prejuizoAcumulado = prejuizoAcumulado.add(lucroTributavel.abs());
                        }
                        taxDto.setTax(BigDecimal.ZERO.setScale(1));
                    }

                }
                else {
                    if (lucroTributavel.compareTo(BigDecimal.ZERO) < 0) {
                        prejuizoAcumulado = prejuizoAcumulado.add(lucroTributavel.abs());
                    }
                    taxDto.setTax(BigDecimal.ZERO.setScale(1));
                }
            }

            tax.add(taxDto);
        }
        return tax;
    }
}
