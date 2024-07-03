INSERT INTO team (ID, TEAM_NAME, JOIN_TOKEN)
VALUES (1, 'test team', 'test');

INSERT INTO tab_user (user_id, date_of_birth, email, first_name, last_name, password, profile_picture, is_password_secure)
VALUES
    (1, '1994-06-04', 'gabbro@nomai.net', 'Gabbro', 'Hornfels', 'password11', 'https://nomai.net/gabbro.jpg', FALSE),
    (2, '1995-07-05', 'john_doe@example.com', 'John', 'Doe', 'password', null, FALSE),
    (3, '1996-08-06', 'jane_doe@example.com', 'Jane', 'Doe', 'password', null, FALSE),
    (4, '1997-09-07', 'sherlock_holmes@example.com', 'Sherlock', 'Holmes', 'password', null, FALSE);
