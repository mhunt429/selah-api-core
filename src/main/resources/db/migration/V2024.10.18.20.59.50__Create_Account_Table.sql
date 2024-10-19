CREATE TABLE account
(
    original_insert_epoch BIGINT NOT NULL,
    last_update_epoch     BIGINT NOT NULL,
    app_last_changed_by   BIGINT NOT NULL,
    id                    BIGSERIAL PRIMARY KEY,
    date_created          BIGINT,
    account_name          TEXT
)


/*
 ROLLBACK
  DELETE FROM flyway_schema_history where script = 'V2024.10.18.20.59.50__Create_Account_Table.sql'
 DROP TABLE account CASCADE;
 */