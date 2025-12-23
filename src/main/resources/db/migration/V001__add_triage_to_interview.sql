-- Migration: Add triage field to interview table
-- Description: Adds a triage column (VARCHAR) to the interview table in hakeem_schema

ALTER TABLE hakeem_schema.interview
ADD COLUMN triage VARCHAR(255);

