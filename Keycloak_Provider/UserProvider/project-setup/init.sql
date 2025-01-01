CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    active INT NOT NULL
);

-- Thêm dữ liệu mẫu
INSERT INTO users (username, email, first_name, last_name, password, active) VALUES
('user1', 'user1@gmail.com', 'User', 'One', 'hashed_password_1', 1),
('user2', 'user2@gmail.com', 'User', 'Two', 'hashed_password_2', 1),
('user3', 'user3@gmail.com', 'User', 'Three', 'hashed_password_3', 1);
