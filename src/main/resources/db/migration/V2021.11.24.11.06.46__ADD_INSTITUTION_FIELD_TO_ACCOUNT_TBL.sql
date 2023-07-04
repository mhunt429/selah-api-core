ALTER TABLE user_bank_account ADD COLUMN institution_id BIGSERIAL REFERENCES user_institution(id);
