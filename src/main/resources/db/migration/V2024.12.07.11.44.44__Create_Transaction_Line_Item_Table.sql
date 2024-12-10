CREATE TABLE transaction_line_item(
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT references transaction(id) ON DELETE SET NULL,
    transaction_category_id BIGINT references transaction_category(id) ON DELETE SET NULL,
    itemized_amount DECIMAL
) inherits(base_audit_table);

CREATE INDEX transaction_line_item_transaction ON transaction_line_item(transaction_id);

CREATE INDEX transaction_line_item_category ON transaction_line_item(transaction_category_id);

/*
 ROLLBACK
 DELETE FROM flyway_schema_history where script = 'V2024.12.07.11.44.44__Create_Transaction_Line_Item_Table.sql';
DROP INDEX transaction_line_item_transaction;
 DROP INDEX transaction_line_item_category;
DROP TABLE transaction_line_item CASCADE;
 */