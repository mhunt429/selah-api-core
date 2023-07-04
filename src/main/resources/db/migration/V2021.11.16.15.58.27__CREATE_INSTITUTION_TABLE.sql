CREATE TABLE user_institution
(
    id                     BIGSERIAL PRIMARY KEY,
    user_id                BIGSERIAL REFERENCES app_user (id),
    institution_id         TEXT,
    institution_name       TEXT,
    encrypted_access_token TEXT
);

--ROLLBACK;
--DELETE FROM schema_version where script = 'V2021.11.16.15.58.27__CREATE_INSTITUTION_TABLE.sql';
--DROP TABLE user_institution;
