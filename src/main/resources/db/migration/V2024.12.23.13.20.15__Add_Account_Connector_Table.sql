CREATE TABLE account_connector
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT references app_user (id) ON DELETE SET NULL,
    institution_id TEXT,
    institution_name TEXT,
    date_connected   TIMESTAMPTZ,
    encrypted_access_token TEXT,
    transaction_sync_cursor TEXT
)INHERITS(base_audit_table);

CREATE INDEX account_connector_userId ON account_connector(user_id);

/*
ROLLBACK;
DELETE FROM flyway_schema_history where script = 'V2024.12.23.13.20.15__Add_Account_Connector_Table.sql';
DROP INDEX account_connector_userId
DROP TABLE account_connector CASCADE
 */