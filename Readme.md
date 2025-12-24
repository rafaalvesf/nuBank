Desafio Nubank - Ganho de Capital
Programa CLI que calcula impostos sobre operações de compra e venda de ações seguindo as regras específicas de ganho de capital, processando múltiplas simulações independentes via stdin.

Decisões técnicas e arquiteturais
Adotada uma arquitetura simples e escalável inspirada em microserviços, separando responsabilidades claras:

Classe Main: ponto de entrada CLI que gerencia I/O (stdin → JSON → stdout), processando linha por linha independentemente.

Classe TransacaoService: camada de domínio isolada com toda lógica de negócio (média ponderada, prejuízos acumulados, regras de imposto R$20k, alíquota 20%).

DTOs (TransacaoDto, TaxDto): contratos claros para entrada/saída, desacoplados da lógica de negócio.

Benefícios desta separação:

Cada simulação é processada de forma stateless (reset de estado por linha).

Fácil evolução para API REST ou mensageria mantendo a lógica de negócio intacta.

Testes isolados por camada (unitário no service, integração no fluxo completo).

Justificativa das bibliotecas
Lombok: elimina boilerplate nos DTOs (@Getter/@Setter/@NoArgsConstructor/@AllArgsConstructor), focando no domínio.

Jackson: serialização/desserialização JSON eficiente para stdin/stdout e testes.

JUnit 5: testes unitários e integração abrangentes.

Spring Boot (estrutura): organização de projeto padronizada, testes de contexto e evolução futura, sem servidor HTTP ativo.

Sem dependências externas: aplicação standalone, sem banco ou infraestrutura desnecessária.

Compilação e execução
Pré-requisitos
Java 17+

Maven

Build:
mvn clean package

Execução:
java -cp target/classes com.nubank.nubank.Main

Exemplo de uso (input redirection):

bash
echo '[{"operation":"buy","unit-cost":10.00,"quantity":10000},{"operation":"sell","unit-cost":20.00,"quantity":5000}]' | java -cp target/classes com.nubank.nubank.Main
Saída esperada:

json
[{"tax":0.0},{"tax":10000.0}]

Processamento: lê linha por linha do stdin até linha vazia, cada linha = simulação independente.

Execução dos testes:
mvn test

Cobertura de testes
TransacaoServiceTest: teste unitário, testando diversos cenários que enquadra em diferentes regras do negócio.

TransacaoServiceSdinTest: teste de integração end-to-end que:

Lê arquivo input.txt com múltiplos cenários do desafio.

Processa cada linha como simulação independente.

Compara resultado com 35 saídas esperadas pré-definidas (todos os casos do spec).

Casos cobertos: todos os exemplos do desafio (#1-9), incluindo média ponderada, prejuízos acumulados, limite R$20k, recompras após vendas totais.

Implementação das regras de negócio
A lógica em TransacaoService implementa fielmente as especificações:

Média ponderada: ((qtd_atual * media_atual) + (qtd_nova * preco_nova)) / (qtd_atual + qtd_nova)

Prejuízo acumulado: deduz lucros futuros até zerar

Limite isenção: operações ≤ R$20.000 não pagam imposto, mas prejuízos são acumulados

Alíquota: 20% sobre lucro líquido (após dedução prejuízos)

Compras: nunca pagam imposto

Estado stateless: reset por simulação (linha)

Precisão: BigDecimal