CREATE TABLE app_user
(
    id           BIGSERIAL PRIMARY KEY,
    email        TEXT,
    user_name    TEXT,
    password     TEXT,
    first_name   TEXT,
    last_name    TEXT,
    date_created BIGINT,
    UNIQUE (email),
    UNIQUE (user_name)
);


