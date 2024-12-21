-- CREATE TABLE IF NOT EXISTS users (
--                                      id IDENTITY PRIMARY KEY,
--                                      username VARCHAR(50) NOT NULL UNIQUE,
--     password VARCHAR(100) NOT NULL
--     );
--
-- CREATE TABLE IF NOT EXISTS diary_entries (
--                                              id IDENTITY PRIMARY KEY,
--                                              user_id BIGINT NOT NULL,
--                                              title VARCHAR(100) NOT NULL,
--     content TEXT,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (user_id) REFERENCES users(id)
--     );

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS diary_entries (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             user_id BIGINT NOT NULL,
                                             title VARCHAR(100) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
    );