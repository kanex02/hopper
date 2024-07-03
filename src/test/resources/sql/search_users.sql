INSERT INTO location (id, address_line1, city, country)
VALUES
    (1, '256 Waimairi Road', 'Christchurch', 'NZ');

INSERT INTO tab_user (user_id, date_of_birth, email, first_name, last_name, password, profile_picture, is_password_secure, location_id)
VALUES
    (11, '1994-06-04', 'gabbro@nomai.net', 'Gabbro', 'Hornfels', 'password11', 'https://nomai.net/gabbro.jpg', FALSE, 1),
    (12, '1991-12-22', 'solanum@nomai.net', 'Solanum', 'Santander', 'password12', 'https://nomai.net/solanum.jpg', FALSE, 1),
    (13, '1992-03-12', 'eskil@nomai.net', 'Eskil', 'Steffensen', 'password13', 'https://nomai.net/eskil.jpg', FALSE, 1),
    (14, '1990-09-01', 'coles@nomai.net', 'Feldspar', 'Outer Wilds Ventures', 'password14', 'https://nomai.net/feldspar.jpg', FALSE, 1),
    (15, '1995-01-23', 'clary@nomai.net', 'Clary', 'Lanier', 'password15', 'https://nomai.net/clary.jpg', FALSE, 1),
    (16, '1993-05-10', 'lantern@nomai.net', 'Lantern', 'Feldspar', 'password16', 'https://nomai.net/lantern.jpg', FALSE, 1),
    (17, '1989-11-12', 'slate@nomai.net', 'Slate', 'Onkel', 'password17', 'https://nomai.net/slate.jpg', FALSE, 1),
    (18, '1994-08-08', 'gossan@nomai.net', 'Gossan', 'Chert', 'password18', 'https://nomai.net/gossan.jpg', FALSE, 1),
    (19, '1991-04-15', 'faye@nomai.net', 'Faye', 'Feldspar', 'password19', 'https://nomai.net/faye.jpg', FALSE, 1),
    (20, '1990-12-03', 'hal@nomai.net', 'Hal', 'Marlson', 'password20', 'https://nomai.net/hal.jpg', FALSE, 1),
    (21, '1989-07-26', 'hal.brittlesonother@nomai.net', 'Hal', 'Brittleson', 'password21', 'https://nomai.net/hal_brittleson.jpg', FALSE, 1),
    (22, '2000-07-12', 'twin.onkel@nomai.net', 'Twin', 'Onkel', 'password22', 'https://nomai.net/twin_onkel.jpg', FALSE, 1),
    (23, '1984-01-01', 'dark.bramble@planets.com', 'Dark', 'Bramble', 'password23', 'https://nomai.net/dark_bramble.svg', FALSE, 1),
    (24, '2000-07-12', 'twin.onkel.other@nomai.net', 'Twin', 'Onkel', 'password22', 'https://nomai.net/twin_onkel.jpg', FALSE, 1);



