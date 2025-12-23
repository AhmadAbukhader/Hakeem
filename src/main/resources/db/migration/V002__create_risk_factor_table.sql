-- Migration: Create risk_factor table
-- Description: Creates a new risk_factor table in hakeem_schema to store patient risk factors

CREATE TABLE IF NOT EXISTS hakeem_schema.risk_factor (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL,
    factor_name VARCHAR(255) NOT NULL,
    CONSTRAINT fk_risk_factor_patient
        FOREIGN KEY (patient_id)
        REFERENCES hakeem_schema.users(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_risk_factor_patient_id ON hakeem_schema.risk_factor(patient_id);

