CREATE TABLE account
(
    original_insert     TIMESTAMPTZ NOT NULL,
    last_update         TIMESTAMPTZ NOT NULL,
    app_last_changed_by BIGINT      NOT NULL,
    id                  BIGSERIAL PRIMARY KEY,
    date_created        TIMESTAMPTZ,
    account_name        TEXT
)


/*
 ROLLBACK
  DELETE FROM flyway_schema_history where script = 'V2024.10.18.20.59.50__Create_Account_Table.sql'
 DROP TABLE account CASCADE;
 */