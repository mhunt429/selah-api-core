CREATE TABLE transaction_line_item (
	id BIGSERIAL PRIMARY KEY,
	transaction_id BIGSERIAL REFERENCES user_transaction(id),
	transaction_category_id BIGSERIAL REFERENCES user_transaction_category(id),
	itemized_amount DECIMAL,
	is_app_category BOOLEAN -- Indicates if the user used a default category option
);