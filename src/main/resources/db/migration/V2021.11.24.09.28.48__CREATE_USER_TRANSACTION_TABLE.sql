CREATE TABLE IF NOT EXISTS user_transaction(
	id BIGSERIAL PRIMARY KEY,
	account_id BIGSERIAL REFERENCES user_bank_account(id),
	user_id BIGSERIAL  REFERENCES app_user(id),
	transaction_amount DECIMAL,
	transaction_date TIMESTAMP WITH TIME ZONE,
	merchant_name TEXT,
	transaction_name TEXT,
	pending boolean,
	payment_method TEXT,
	external_transaction_id TEXT
);

/*
 ROLLBACK
 DELETE FROM flyway_schema_history WHERE script = 'V2021.11.24.09.28.48__CREATE_USER_TRANSACTION_TABLE.sql';
 DROP TABLE user_transaction
 */