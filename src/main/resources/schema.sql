CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(255) NOT NULL,
    balance NUMERIC NOT NULL,
    amount NUMERIC NOT NULL,
    operation_type VARCHAR(100) NOT NULL,
    date_time TIMESTAMP NOT NULL
);