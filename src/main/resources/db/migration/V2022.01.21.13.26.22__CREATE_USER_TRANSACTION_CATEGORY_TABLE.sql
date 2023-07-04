CREATE TABLE user_transaction_category
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGSERIAL references app_user (id),
    category_name VARCHAR(100),
    symbol        TEXT
);