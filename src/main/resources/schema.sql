-- Verifica se o banco de dados existe e cria-o se necessário
-- Nota: Isto é apenas um exemplo; criação de banco de dados
-- geralmente não é feita via schema.sql
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'walletdb') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE walletDB');
    END IF;
END
$$ LANGUAGE plpgsql;

-- Conecta-se ao banco de dados 'walletDB'
-- Nota: \connect é um comando psql e não funcionará aqui; a conexão é gerenciada pela aplicação Spring Boot.

-- Cria a extensão UUID se não existir
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