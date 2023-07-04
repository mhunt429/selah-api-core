CREATE INDEX income_statement_user ON income_statement(user_id)

/*
ROLLBACK
DROP INDEX income_statement_user;
DELETE FROM changelog WHERE name = 'V20230510210428__CREATE_INCOME_STATEMENT_INDEXES.sql';
*/