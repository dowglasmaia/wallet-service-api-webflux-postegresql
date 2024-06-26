-- Criação do Banco de Dados
CREATE DATABASE walletDB;

-- Cria a extensão uuid-ossp se ainda não existir
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Cria a tabela 'account' se não existir
CREATE TABLE IF NOT EXISTS account (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    number VARCHAR(255),
    user_id VARCHAR(255),
    balance NUMERIC,
    version BIGINT DEFAULT 0
);

-- Cria a tabela 'transaction' se não existir
CREATE TABLE IF NOT EXISTS transaction (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(255),
    amount NUMERIC NOT NULL,
    operation_type VARCHAR(100) NOT NULL,
    date_time TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Cria a tabela 'refund' se não existir
CREATE TABLE IF NOT EXISTS refund (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(255),
    transaction_id UUID NOT NULL,
    amount NUMERIC NOT NULL,
    date_time TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transaction(id),
    version BIGINT DEFAULT 0
);

-- Insere dados de exemplo na tabela account
INSERT INTO account (id, number, user_id, balance)
VALUES (uuid_generate_v4(), '001', 'user123', 0);

INSERT INTO account (id, number, user_id, balance)
VALUES (uuid_generate_v4(), '002', 'user129', 0);
