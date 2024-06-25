
# 🚀 Wallet API

Uma aplicação de carteira digital (Wallet) que permite gerenciar transações, obter extratos, consultar saldos, realizar compras e estornos. Desenvolvida utilizando um modelo reativo não-bloqueante com Java 17 e Spring WebFlux, seguindo os princípios de Clean Code e SOLID, e implementando os padrões de design Bridge e Strategy.

---

## 📜 Sumário

- [⚡ Tecnologias](#-tecnologias)
- [📦 Estrutura do Projeto](#-estrutura-do-projeto)
- [📑 Funcionalidades](#-funcionalidades)
- [🔌 Padrões de Design](#-padrões-de-design)
- [🛠️ Desenvolvimento](#️-desenvolvimento)
- [📚 Documentação](#-documentação)
- [🔍 Observabilidade](#-observabilidade)
- [🌐 Referências](#-referências)

---

## ⚡ Tecnologias

Essas são algumas das tecnologias e ferramentas utilizadas no projeto:

![Java 17](https://img.shields.io/badge/-Java%2017-007396?style=flat-square&logo=java&logoColor=white)
![Spring WebFlux](https://img.shields.io/badge/-Spring%20WebFlux-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Kafka](https://img.shields.io/badge/-Kafka-231F20?style=flat-square&logo=apache-kafka&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/-PostgreSQL-336791?style=flat-square&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/-Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)
![Jaeger](https://img.shields.io/badge/-Jaeger-00B7FF?style=flat-square&logo=jaeger&logoColor=white)
![OpenTelemetry](https://img.shields.io/badge/-OpenTelemetry-CF6300?style=flat-square&logo=opentelemetry&logoColor=white)

---

## 📦 Estrutura do Projeto

```plaintext
src
├── main
│   ├── java
│   │   └── com.dowglasmaia.wallet
│   │       ├── config
│   │       ├── controller
│   │       ├── entity
│   │       ├── enums
│   │       ├── exceptions
│   │       ├── messagemodel
│   │       ├── repository
│   │       ├── service
│   │       │   ├── impl
│   │       │   ├── mapper
│   │       ├── strategy
│   │       └── WalletApplication.java
│   └── resources
│       ├── application.yml
│       └── ...
└── test
    └── java
        └── com.dowglasmaia.wallet
            └── ...
```

---
## 🚀 Desenho de Solução
<img src="./img/API-Gateway.jpg" alt="Flow to obtain the address" width="1280" height="620">


---

## 📑 Funcionalidades

- **Extrato de Transações**: Obtenha um extrato completo de todas as transações.
- **Consulta de Saldo**: Consulte o saldo atual da conta.
- **Retirada de Valor**: Realize retiradas de valor da conta.
- **Realização de Compras**: Efetue compras, que resultam em retiradas da conta.
- **Estorno de Compras**: Cancele compras realizadas e estorne o valor para a conta.
- **Envio de Mensagem Assíncrona**: Após operações que resultam em persistência de dados, a API envia uma mensagem assíncrona para uma API de auditoria.

---

## 🔌 Padrões de Design

### Bridge

O padrão **Bridge** é utilizado para separar a abstração da implementação, permitindo que ambas possam variar independentemente. Neste projeto, ele é aplicado para gerenciar diferentes tipos de transações e operações de ajuste de crédito.

- **Abstração**: A interface `TransactionService`.
- **Implementação**: As classes `CreditAdjustmentTransactionServiceImpl`, `CreateTransactionServiceImpl`, etc.

### Strategy

O padrão **Strategy** define uma família de algoritmos, encapsula cada um deles e os torna intercambiáveis. Este projeto usa este padrão para executar diferentes estratégias de cálculo de saldo, conforme o tipo de operação da transação.

- **Contexto**: `TransactionContext`, que usa uma estratégia para calcular o novo saldo.
- **Estratégias**: Diferentes estratégias de cálculo para operações como depósito, retirada e estorno.

---

## 🛠️ Desenvolvimento

### Princípios Seguidos

- **Clean Code**: Código limpo e fácil de entender.
- **SOLID**: Seguindo os princípios de design orientado a objetos, com ênfase em responsabilidade única.
- **Arquitetura Reativa**: Implementação não-bloqueante com Spring WebFlux.

### Testes

- **JUnit**: Utilizado para testes unitários.
- **Mockito**: Utilizado para criação de mocks nos testes.

---

## 📚 Documentação

- **[Swagger](src/main/resources/openapi/MS-WALLET.yaml)**: Para ver os endpoints e suas descrições.
- **OpenAPI**: Definição completa da API.
- **[Collection PostMan - API](src/main/resources/collection/Wallet%20-%20Transaction%20-%20API.postman_collection.json)**: API Postman Collection
- **[Collection PostMan - GATEWAY](src/main/resources/collection/API-GATEWAY-DOWGLAS-MAIA.postman_collection.json)**: GATEWAY Postman Collection

 <img src="./img/swagger.jpg" alt="Flow to obtain the address" width="1280" height="620">


---

## 🔍 Observabilidade

### OpenTelemetry e Jaeger

- **OpenTelemetry**: Instrumentação automática para coleta de métricas.
- **Jaeger**: Sistema de rastreamento distribuído para monitoramento de transações e desempenho.

Configure o `application.yml` para adicionar rastreamento:

---

## 🌐 Referências

- [Opentelemetry Docs](https://opentelemetry.io/docs)
- [Jaeger Docs](https://www.jaegertracing.io/docs)
- [Spring Webflux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Dockerfile Reference](https://docs.docker.com/reference/dockerfile)
- [Kafka Docs](https://kafka.apache.org/documentation/)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)

---
