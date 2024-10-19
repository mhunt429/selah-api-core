CREATE TABLE app_user
(
    original_insert_epoch BIGINT    NOT NULL,
    last_update_epoch     BIGINT    NOT NULL,
    app_last_changed_by   BIGSERIAL NOT NULL,
    id                    BIGSERIAL PRIMARY KEY,
    account_id            BIGSERIAL references account (id),
    created_epoch         BIGINT,
    encrypted_email       BYTEA,
    username              VARCHAR(20),
    password              TEXT,
    encrypted_name        BYTEA,
    encrypted_phone       BYTEA,
    last_login_epoch      BIGINT,
    last_login_ip         TEXT,
    phone_verified        BOOLEAN,
    email_verified        BOOLEAN,

    UNIQUE (username)

);

CREATE INDEX user_email_index on app_user (encrypted_email);
CREATE INDEX user_phone_idx on app_user (encrypted_phone);

/*
 ROLLBACK
 DELETE FROM flyway_schema_history where script = 'V2024.10.18.21.17.19__Create_AppUser_Table.sql';
 DROP INDEX user_email_index;
  DROP INDEX user_phone_index;
 DROP TABLE app_user CASCADE;
 */