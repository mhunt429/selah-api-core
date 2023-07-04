CREATE TABLE IF NOT EXISTS todo_item(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGSERIAL REFERENCES app_user(id) ON DELETE CASCADE,
    recurring BOOLEAN,
    last_completed TIMESTAMP,
    /*
 OneTime = 0,
        Weekly = 1,
        BiWeekly = 2,
        Monthly = 3,
        Annually = 4,
        Other = 5
    */
    frequency int,
    deadline TIMESTAMP
);

/*
ROLLBACK
DROP INDEX todo_user_index;
DROP TABLE todo_item
*/