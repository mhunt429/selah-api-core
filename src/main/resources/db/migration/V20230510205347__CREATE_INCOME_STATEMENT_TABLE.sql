CREATE TABLE income_statement
(
    id                   BIGSERIAL PRIMARY KEY,
    user_id              BIGSERIAL REFERENCES app_user (id) ON DELETE CASCADE,
    statement_start_date TIMESTAMP,
    statement_end_date   TIMESTAMP,
    total_pay            DECIMAL
);

/*
ROLLBACK 
DROP INDEX income_statement_user;
DROP TABLE income_statement;
DELETE FROM changelog WHERE name = 'V20230510205347__CREATE_INCOME_STATEMENT_TABLE.sql';
*/