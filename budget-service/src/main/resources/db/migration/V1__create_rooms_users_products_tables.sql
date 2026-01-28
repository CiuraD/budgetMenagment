-- Flyway migration: create rooms, room_users, and room_products tables

CREATE TABLE rooms (
    room_id BIGSERIAL PRIMARY KEY,
    room_name VARCHAR(255) NOT NULL
);

CREATE TABLE room_users (
    user_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (user_id, room_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE
);

CREATE TABLE room_products (
    product_id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price NUMERIC(12,2) NOT NULL DEFAULT 0,
    is_paid BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE
);

