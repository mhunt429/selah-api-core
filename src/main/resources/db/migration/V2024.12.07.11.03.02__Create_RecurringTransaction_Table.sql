CREATE TABLE recurring_transaction
(
    id             BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES app_user (id) ON DELETE SET NULL,
    upcoming_date  TIMESTAMPTZ,
    last_paid_date TIMESTAMPTZ,
    location       TEXT,
    frequency      TEXT
) inherits(base_audit_table);

CREATE  INDEX recurring_transaction_user_id ON recurring_transaction(user_id);

/*
ROLLBACK
 DELETE FROM flyway_schema_history where script = 'V2024.12.07.11.03.02__Create_RecurringTransaction_Table.sql';
DROP INDEX recurring_transaction_user_id;
DROP TABLE recurring_transaction CASCADE;
 */