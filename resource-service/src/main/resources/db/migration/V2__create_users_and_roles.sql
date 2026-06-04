CREATE TABLE app_user (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE app_role (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE app_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_app_user_role_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_app_user_role_role FOREIGN KEY (role_id) REFERENCES app_role(id) ON DELETE CASCADE
);

CREATE INDEX idx_app_user_email ON app_user(email);
CREATE INDEX idx_app_role_name ON app_role(name);

INSERT INTO app_role (name)
VALUES
    ('ADMIN'),
    ('MANAGER'),
    ('VIEWER'),
    ('RESOURCE_EDITOR'),
    ('RESOURCE_READER');

INSERT INTO app_user (email, display_name, enabled)
VALUES
    ('admin@gatto.local', 'Gatto Admin', TRUE),
    ('manager@gatto.local', 'Gatto Manager', TRUE),
    ('viewer@gatto.local', 'Gatto Viewer', TRUE);

INSERT INTO app_user_role (user_id, role_id)
SELECT u.id, r.id
FROM app_user u
JOIN app_role r ON r.name IN ('ADMIN', 'MANAGER', 'RESOURCE_EDITOR', 'RESOURCE_READER', 'VIEWER')
WHERE u.email = 'admin@gatto.local';

INSERT INTO app_user_role (user_id, role_id)
SELECT u.id, r.id
FROM app_user u
JOIN app_role r ON r.name IN ('MANAGER', 'RESOURCE_EDITOR', 'RESOURCE_READER')
WHERE u.email = 'manager@gatto.local';

INSERT INTO app_user_role (user_id, role_id)
SELECT u.id, r.id
FROM app_user u
JOIN app_role r ON r.name IN ('VIEWER', 'RESOURCE_READER')
WHERE u.email = 'viewer@gatto.local';
