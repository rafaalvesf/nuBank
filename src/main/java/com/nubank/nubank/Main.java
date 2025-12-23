package com.nubank.nubank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nubank.nubank.dto.TaxDto;
import com.nubank.nubank.dto.TransacaoDto;
import com.nubank.nubank.service.TransacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class Main {

	public static void main(String[] args) throws IOException {

		BufferedReader reader = new BufferedReader( new InputStreamReader(System.in));

		ObjectMapper mapper = new ObjectMapper();
		TransacaoService service = new TransacaoService();

		String linha;

		while ((linha = reader.readLine()) != null  && !linha.isBlank()) {
			List<TransacaoDto> transacoes = mapper.readValue(linha, new TypeReference<List<TransacaoDto>>() {});
			List<TaxDto> resultado = service.executarTransacao(transacoes);

			System.out.println(mapper.writeValueAsString(resultado));
		}
	}
}
