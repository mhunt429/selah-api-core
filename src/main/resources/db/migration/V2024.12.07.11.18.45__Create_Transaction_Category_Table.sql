CREATE TABLE transaction_category
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT references app_user(id)  ON DELETE SET NULL,
    category_name TEXT
) inherits(base_audit_table);

CREATE INDEX transaction_category_user_id ON transaction_category(user_id);

/*
  DELETE FROM flyway_schema_history where script = 'V2024.12.07.11.18.45__Create_Transaction_Category_Table.sql';
DROP INDEX transaction_category_user_id;
DROP TABLE transaction_category CASCADE;
 */