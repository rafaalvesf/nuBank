package com.nubank.nubank.integracaoTest;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nubank.nubank.dto.TaxDto;
import com.nubank.nubank.dto.TransacaoDto;
import com.nubank.nubank.service.TransacaoService;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransacaoServiceSdinTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final TransacaoService service = new TransacaoService();

    @Test
    void deveProcessarTodosOsCenariosDoArquivo() throws Exception {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("input.txt")
                )
        );
        String linha;
        int numeroDoCenario = 1;

        while ((linha = reader.readLine()) != null) {

            List<TransacaoDto> transacoes = mapper.readValue(
                    linha,
                    new TypeReference<List<TransacaoDto>>() {}
            );

            List<TaxDto> resultado = service.executarTransacao(transacoes);

            String saidaEsperadaJson = SAIDAS_ESPERADAS[numeroDoCenario - 1];

            List<TaxDto> esperado = mapper.readValue(
                    saidaEsperadaJson,
                    new TypeReference<List<TaxDto>>() {}
            );

            assertEquals(
                    esperado.size(),
                    resultado.size(),
                    "Erro no cenário " + numeroDoCenario
            );

            for (int i = 0; i < esperado.size(); i++) {
                BigDecimal esperadoTax = esperado.get(i).getTax().setScale(2);
                BigDecimal resultadoTax = resultado.get(i).getTax().setScale(2);

                assertEquals(
                        esperadoTax,
                        resultadoTax,
                        "Erro no cenário " + numeroDoCenario + ", operação " + (i + 1)
                );
            }

            numeroDoCenario++;
        }
    }

    private static final String[] SAIDAS_ESPERADAS = {
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":4863.10},{\"tax\":37262.00}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":9500.00},{\"tax\":0.0}]",
            "[{\"tax\":0.0},{\"tax\":0.0}]",
            "[{\"tax\":0.0},{\"tax\":0.0}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0}]",
            "[{\"tax\":0.0},{\"tax\":10000.00},{\"tax\":0.0}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":1000.00}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":10000.00}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":3000.00}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":3000.00},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":3700.00},{\"tax\":0.0}]",
            "[{\"tax\":0.0},{\"tax\":80000.00},{\"tax\":0.0},{\"tax\":60000.00}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":1000.00},{\"tax\":2400.00}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":1307.20}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0}]",
            "[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0}]"
    };
}
