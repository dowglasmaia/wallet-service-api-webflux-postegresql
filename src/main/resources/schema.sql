-- Criação de tabelas em PostgreSQL com UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Cria a tabela 'transactions' se não existir
CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(255) NOT NULL,
    balance NUMERIC NOT NULL,
    amount NUMERIC NOT NULL,
    operation_type VARCHAR(100) NOT NULL,
    date_time TIMESTAMP NOT NULL
);

-- Cria a tabela 'refund' se não existir
CREATE TABLE IF NOT EXISTS refund (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(255) NOT NULL,
    transaction_id UUID NOT NULL,
    amount NUMERIC NOT NULL,
    date_time TIMESTAMP NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);
