CREATE TABLE app_user
(
    original_insert     TIMESTAMPTZ NOT NULL,
    last_update         TIMESTAMPTZ NOT NULL,
    app_last_changed_by BIGINT      NOT NULL,
    id                  BIGSERIAL PRIMARY KEY,
    account_id          BIGSERIAL references account (id),
    created_date        TIMESTAMPTZ,
    encrypted_email     TEXT,
    username            VARCHAR(20),
    password            TEXT,
    encrypted_name      TEXT,
    encrypted_phone     TEXT,
    last_login          TIMESTAMPTZ,
    last_login_ip       TEXT,
    phone_verified      BOOLEAN,
    email_verified      BOOLEAN,

    UNIQUE (username)

);

CREATE INDEX user_email_index on app_user (encrypted_email);
CREATE INDEX user_phone_index on app_user (encrypted_phone);

/*
 ROLLBACK
 DELETE FROM flyway_schema_history where script = 'V2024.10.18.21.17.19__Create_AppUser_Table.sql';
 DROP INDEX user_email_index;
  DROP INDEX user_phone_index;
 DROP TABLE app_user CASCADE;
 */