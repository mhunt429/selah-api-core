CREATE TABLE transaction
(
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT references app_user (id) ON DELETE SET NULL,
    recurring_transaction_id BIGINT references recurring_transaction(id),
    transaction_date   TIMESTAMPTZ,
    location           TEXT,
    transaction_amount DECIMAL,
    transaction_name   TEXT
)inherits(base_audit_table);

CREATE INDEX transaction_user_id ON transaction (user_id);

/*
  DELETE FROM flyway_schema_history where script = 'V2024.12.07.11.12.13__Create_Transaction_Table.sql';
DROP INDEX transaction_user_id;
DROP TABLE transaction CASCADE;
 */