CREATE TABLE income_statement_deduction(
     id BIGSERIAL PRIMARY KEY,
     statement_id BIGSERIAL REFERENCES income_statement(id),
     deduction_name VARCHAR(30),
     amount DECIMAL
)

/*
ROLLBACK
DROP TABLE income_statment_deduction
DELETE FROM changelog where name ='V20230510205950__CREATE_INCOME_STATEMENT_DEDUCTION_TABLE.sql'
*/