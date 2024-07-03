INSERT INTO tab_user (user_id, date_of_birth, email, first_name, last_name, password, profile_picture, is_password_secure)
VALUES
    (100, '1994-06-04', 'gabbro@nomai.net', 'Gabbro', 'Hornfels', 'password11', 'https://nomai.net/gabbro.jpg', TRUE),
    (546567878963, '1994-06-04', 'test1@example.com', 'Gabbro', 'Hornfels', '{bcrypt}$2a$10$aupl3SifiKAbzWlX8BT7rO9FS7S5iypYoUHSxFAwdiDKj8gqLrqk6', 'https://nomai.net/gabbro.jpg', TRUE);

INSERT INTO challenge (id, end_date, title, goal, hops, user_Generated_For_user_id, is_Complete)
VALUES
    (1, '2023-04-20', 'Go outside', 'Touch grass 5 times', 40, 546567878963, FALSE),
    (2, '2023-04-20', 'Go outside', 'Touch grass 6 times', 40, 546567878963, FALSE),
    (3, '2023-04-20', 'Go outside', 'Touch grass 7 times', 40, 546567878963, FALSE),
    (4, '2023-04-20', 'Go outside', 'Touch grass 5 times', 40, 100, FALSE),
    (5, '2023-04-20', 'Go outside', 'Touch grass 6 times', 40, 100, FALSE),
    (6, '2023-04-20', 'Go outside', 'Touch grass 7 times', 40, 100, FALSE);
