package com.nubank.nubank.service;


import com.nubank.nubank.dto.TaxDto;
import com.nubank.nubank.dto.TransacaoDto;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class TransacaoService {
    public List<TaxDto> executarTransacao(List<TransacaoDto> transacoes) {

        List<TaxDto> tax = new ArrayList<>();
        BigDecimal mediaPonderada;

        for(TransacaoDto transacao : transacoes){

            transacao.getOperation();

            TaxDto taxDto = new TaxDto(BigDecimal.ZERO);
            tax.add(taxDto);
        }

        return tax;
    }
}
