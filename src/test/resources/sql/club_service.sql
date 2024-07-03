INSERT INTO location (id, address_line1, address_line2, city, country)
VALUES (1, '', '', 'Dallas', 'US');

INSERT INTO sport (sport_id, sport_name)
VALUES
    (1, 'Ball');

INSERT INTO team (ID, TEAM_NAME, JOIN_TOKEN, sport_id)
VALUES (1, 'test team', 'test', 1),
       (2, 'a team', 'test', 1);

INSERT INTO tab_user (user_id, date_of_birth, email, first_name, last_name, password, profile_picture, is_password_secure)
VALUES
    (1, '1994-06-04', 'gabbro@nomai.net', 'Gabbro', 'Hornfels', 'password11', 'https://nomai.net/gabbro.jpg', FALSE);

INSERT INTO club (id, club_name, club_description, club_image, sport_id, location_id, user_id)
VALUES (388, 'Club One', 'This is Club One', 'image1.jpg', 1, 1, 1);

INSERT INTO team_managers (team_id, user_id)
VALUES
    (1, 1),
    (2, 1);