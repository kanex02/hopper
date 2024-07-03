INSERT INTO location (id, address_line1, city, country)
VALUES
    (1, '256 Waimairi Road', 'Christchurch', 'NZ'),
    (2, '10 Downing Street', 'London', 'UK');

INSERT INTO tab_user (user_id, date_of_birth, email, first_name, last_name, password, profile_picture, is_password_secure, location_id)
VALUES
    (11, '1994-06-04', 'gabbro@nomai.net', 'Gabbro', 'Hornfels', 'password11', 'https://nomai.net/gabbro.jpg', FALSE, 1),
    (12, '1991-12-22', 'solanum@nomai.net', 'Solanum', 'Santander', 'password12', 'https://nomai.net/solanum.jpg', FALSE, 1),
    (13, '1992-03-12', 'eskil@nomai.net', 'Eskil', 'Steffensen', 'password13', 'https://nomai.net/eskil.jpg', FALSE, 2),
    (14, '1990-09-01', 'coles@nomai.net', 'Feldspar', 'Outer Wilds Ventures', 'password14', 'https://nomai.net/feldspar.jpg', FALSE, 2);