-- INSERT INTO users (username, password) VALUES
--     ('demo', '$2a$10$demopasswordhash') -- password is 'password'
--     ON DUPLICATE KEY UPDATE username=username;


MERGE INTO users (username, password) 
KEY(username)
VALUES ('demo', '$2a$10$bXcBm0Ln4kimRQJ4aCr0yOTcTfd9AmXkbPWU7LzVZJjuuCfnRMa2W');