CREATE TABLE account
(
    id                  BIGSERIAL PRIMARY KEY,
    date_created        TIMESTAMPTZ,
    account_name        TEXT
) INHERITS(base_audit_table)


/*
 ROLLBACK
  DELETE FROM flyway_schema_history where script = 'V2024.10.18.20.59.50__Create_Account_Table.sql'
 DROP TABLE account CASCADE;
 */