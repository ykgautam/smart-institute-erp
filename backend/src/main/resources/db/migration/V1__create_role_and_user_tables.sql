CREATE TABLE roles
(
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(50) NOT NULL UNIQUE,

    description VARCHAR(255),

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    created_by VARCHAR(100),

    updated_by VARCHAR(100)
);

CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,

    first_name VARCHAR(100) NOT NULL,

    last_name VARCHAR(100),

    email VARCHAR(150) NOT NULL UNIQUE,

    mobile VARCHAR(20) UNIQUE,

    password VARCHAR(255) NOT NULL,

    status VARCHAR(30) NOT NULL,

    gender VARCHAR(20),

    role_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    created_by VARCHAR(100),

    updated_by VARCHAR(100),

    CONSTRAINT fk_user_role
        FOREIGN KEY(role_id)
            REFERENCES roles(id)
);