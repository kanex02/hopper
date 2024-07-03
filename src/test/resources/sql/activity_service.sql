INSERT INTO team (ID, TEAM_NAME, JOIN_TOKEN)
VALUES (1, 'test team', 'test'),
       (2, 'a team', 'test'),
       (3, 'Z team', 'test');


INSERT INTO tab_user (user_id, date_of_birth, email, first_name, last_name, password, profile_picture, is_password_secure)
VALUES
    (1, '1994-06-04', 'gabbro@nomai.net', 'Gabbro', 'Hornfels', 'password11', 'https://nomai.net/gabbro.jpg', FALSE),
    (388, '1994-06-04', 'gabbro1@nomai.net', 'Gabbro1', 'Hornfels', 'password11', 'https://nomai.net/gabbro.jpg', FALSE),
     (389, '1994-06-04', 'gabbro2@nomai.net', 'Gabbro2', 'Hornfels', 'password11', 'https://nomai.net/gabbro.jpg', FALSE);

INSERT INTO activity (activity_id, description, end_time, start_time, type, team_id, user_id)
VALUES (1, 'blah', '1234', '2023-05-10T02:01:25.585678484Z', 'GAME', 1, 1),
       (2, 'blahblah', '1234', '2023-05-10T02:01:15.599899367Z', 'GAME', 1, 1),
       (3, 'blahblah', '1234', '2023-05-10T02:01:15.599899367Z', 'GAME', 2, 1),
       (4, 'blahblah', '1234', '2023-05-10T02:01:15.599899367Z', 'GAME', 3, 1);