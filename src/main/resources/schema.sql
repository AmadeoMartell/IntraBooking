CREATE TABLE roles (
    role_id     SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE users (
    user_id      SERIAL PRIMARY KEY,
    role_id      INT NOT NULL REFERENCES roles(role_id),
    username     VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    phone        VARCHAR(20),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE locations (
    location_id SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    address     TEXT,
    description TEXT
);

CREATE TABLE room_types (
    type_id     SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    capacity    INT NOT NULL,
    description TEXT
);

CREATE TABLE rooms (
    room_id     SERIAL PRIMARY KEY,
    location_id INT NOT NULL REFERENCES locations(location_id),
    type_id     INT NOT NULL REFERENCES room_types(type_id),
    name        VARCHAR(100) NOT NULL,
    capacity    INT NOT NULL,
    description TEXT
);

CREATE TABLE statuses (
    status_id   SMALLINT PRIMARY KEY,
    name        VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE bookings (
    booking_id   BIGSERIAL PRIMARY KEY,
    user_id      INT   NOT NULL REFERENCES users(user_id),
    room_id      INT   NOT NULL REFERENCES rooms(room_id),
    status_id    SMALLINT NOT NULL REFERENCES statuses(status_id),
    start_time   TIMESTAMP NOT NULL,
    end_time     TIMESTAMP NOT NULL,
    purpose      VARCHAR(255),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

