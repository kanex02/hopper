INSERT INTO tab_user (user_id, date_of_birth, email, first_name, last_name, password, profile_picture, is_password_secure)
VALUES
    (100, '1994-06-04', 'gabbro@nomai.net', 'Gabbro', 'Hornfels', 'password11', 'https://nomai.net/gabbro.jpg', TRUE),
    (101, '1991-12-22', 'solanum@nomai.net', 'Solanum', 'Santander', 'password12', 'https://nomai.net/solanum.jpg', TRUE),
    (102, '1992-03-12', 'eskil@nomai.net', 'Eskil', 'Steffensen', 'password13', 'https://nomai.net/eskil.jpg', TRUE),
    (103, '1990-09-01', 'coles@nomai.net', 'Feldspar', 'Outer Wilds Ventures', 'password14', 'https://nomai.net/feldspar.jpg', TRUE),
    (104, '1995-01-23', 'clary@nomai.net', 'Clary', 'Lanier', 'password15', 'https://nomai.net/clary.jpg', TRUE),
    (105, '1995-01-23', 'dog@nomai.net', 'Mary', 'Lanier', 'password15', 'https://nomai.net/mary.jpg', TRUE);

INSERT INTO sport (sport_id, sport_name)
VALUES
    (10, 'football'),
    (11, 'basketball'),
    (12, 'volleyball');

INSERT INTO user_sports (user_id, sport_id)
VALUES
    (100, 10),
    (101, 11),
    (102, 12),
    (103, 10),
    (103, 11),
    (104, 11),
    (104, 12),
    (105, 10),
    (105, 11),
    (105, 12);
