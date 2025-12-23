package com.nubank.nubank.controller;

import io.swagger.annotations.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


public class TransacaoController {

    @PostMapping(value = "/transacao")
    @Tag(name = "1. Evento Transação")
    public ResponseEntity<String> executaTransacao(){
        return transacaoService.executatransacao();
    }
}
