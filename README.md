
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
- **[Repositório API Gateway](https://github.com/dowglasmaia/traefik-gateway-config)**: Traefik Gateway
- **[Repositório API Audit](https://github.com/dowglasmaia/audit-transaction-service-sboot-mongodb)**: Audit Transaction Service API

 <img src="./img/swagger.jpg" alt="Flow to obtain the address" width="1280" height="620">

---

## 🚀 Execução e Configuração

### Pré-requisitos
- **Docker**: [Instalar Docker](https://docs.docker.com/get-docker/)

**Navegue até o diretório da aplicação:**
   Abra o terminal e vá até o diretório onde o `docker-compose.yml` da sua aplicação está localizado.

 **Execute o Docker Compose:**
   Use o comando abaixo para iniciar os containers definidos no `docker-compose.yml` da aplicação.

   ```bash
   docker-compose up
   ```

   Isso iniciará todos os serviços definidos, incluindo a aplicação e o banco de dados.

#### Executar o Gateway

**Navegue até o diretório do gateway:**
   Abra um novo terminal e vá até o diretório onde o `docker-compose.yml` do gateway está localizado.

**Execute o Docker Compose:**
   Use o comando abaixo para iniciar os containers definidos no `docker-compose.yml` do gateway.

   ```bash
   docker-compose up
   ```

   Isso iniciará o gateway e quaisquer serviços auxiliares necessários para o gateway funcionar.

**Execute para criar o Banco de Dados:**

- Conexão com Base Dados
```bash
    url: jdbc:postgresql://localhost:5432/walletDB
    username: maia
    password: maiapw
   ```

   ```roomsql
-- Script SQL para walletDB
-- Criação do Banco de Dados
CREATE DATABASE walletDB;

-- Seleciona o Banco de Dados
USE walletDB;

   -- Criação de tabelas em PostgreSQL com UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Cria a tabela 'account' se não existir
CREATE TABLE IF NOT EXISTS account (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    number VARCHAR(255),
    user_id VARCHAR(255),
    balance NUMERIC
);

-- Cria a tabela 'transaction' se não existir
CREATE TABLE IF NOT EXISTS transaction (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(255),
    amount NUMERIC NOT NULL,
    operation_type VARCHAR(100) NOT NULL,
    date_time TIMESTAMP
);

-- Cria a tabela 'refund' se não existir
CREATE TABLE IF NOT EXISTS refund (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(255),
    transaction_id UUID NOT NULL,
    amount NUMERIC NOT NULL,
    date_time TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transaction(id)
);


INSERT INTO public.account
(id, "number", user_id, balance)
VALUES(uuid_generate_v4(), '001', 'user123', 0);

INSERT INTO public.account
(id, "number", user_id, balance)
VALUES(uuid_generate_v4(), '002', 'user129', 0);
   ```

---


## 🔍 Observabilidade

### OpenTelemetry e Jaeger

- **OpenTelemetry**: Instrumentação automática para coleta de métricas.
- **Jaeger**: Sistema de rastreamento distribuído para monitoramento de transações e desempenho.

---

## 🌐 Referências

- [Opentelemetry Docs](https://opentelemetry.io/docs)
- [Jaeger Docs](https://www.jaegertracing.io/docs)
- [Spring Webflux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Dockerfile Reference](https://docs.docker.com/reference/dockerfile)
- [Kafka Docs](https://kafka.apache.org/documentation/)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
