CREATE TABLE IF NOT EXISTS recurring_transaction(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGSERIAL REFERENCES app_user(id) ON DELETE CASCADE,
    frequency TEXT,
    notification_preference TEXT,
    upcoming_date TIMESTAMP,
    last_paid_date TIMESTAMP,
    location_name TEXT,
    send_reminder_notication boolean,
    category_id BIGSERIAL REFERENCES user_transaction_category(id) ON DELETE CASCADE
);