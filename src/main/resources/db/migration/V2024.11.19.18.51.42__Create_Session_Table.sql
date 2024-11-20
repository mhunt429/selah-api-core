CREATE TABLE session
(
    id                  UUID PRIMARY KEY         NOT NULL,
    user_id             BIGINT                   NOT NULL,
    expiration          TIMESTAMP WITH TIME ZONE NOT NULL
) INHERITS(base_audit_table);

CREATE INDEX session_user_index ON session (user_id);

/*
 ROLLBACK
  DELETE FROM flyway_schema_history where script = 'V2024.11.19.18.51.42__Create_Session_Table.sql'
 DROP INDEX session_user_index
 DROP TABLE session
 */