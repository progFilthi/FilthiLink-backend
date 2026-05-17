ALTER TABLE users
    ADD auth_provider VARCHAR(255);

ALTER TABLE users
    ADD google_id VARCHAR(255);

ALTER TABLE users
    ALTER COLUMN auth_provider SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN google_id SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT uc_users_googleid UNIQUE (google_id);

ALTER TABLE users
    ALTER COLUMN password DROP NOT NULL;