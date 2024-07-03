INSERT INTO location (id, address_line1, address_line2, city, country)
VALUES (1, '', '', 'Invercargill', 'NZ'),
       (2, '', '', 'New York', 'US'),
       (3, '', '', 'Chicago', 'US'),
       (4, '', '', 'Los Angeles', 'US'),
       (5, '', '', 'Dallas', 'US'),
       (6, '', '', 'Toronto', 'CA'),
       (7, '', '', 'Seattle', 'US'),
       (8, '', '', 'Miami', 'US'),
       (9, '', '', 'Boston', 'US'),
       (10, '', '', 'Minecraft', 'SE'),
       (11, '', '', 'Mango', 'DE'),
       (12, '', '', 'Houston', 'US'),
       (13, '', '', 'San Francisco', 'US'),
       (14, '', '', 'Chicago', 'US'),
       (15, '', '', 'Seattle', 'US'),
       (16, '', '', 'Dallas', 'US');

INSERT INTO sport (sport_id, sport_name)
VALUES
    (1, 'Basketball'),
    (2, 'Basketball'),
    (3, 'Football'),
    (4, 'Soccer'),
    (5, 'Hockey'),
    (6, 'Minecraft'),
    (7, 'Volleyball');

INSERT INTO team (ID, IMAGE, SPORT_ID, TEAM_NAME, LOCATION_ID)
VALUES (1, 'image1.jpg', 1, 'Knicks', 2),
       (2, 'image2.jpg', 2, 'Dodgers', 4),
       (14, 'image14.jpg', 3, 'bears', 3),
       (3, 'image3.jpg', 3, 'Bears', 14),
       (4, 'image4.jpg', 4, 'Dynamo', 12),
       (5, 'image5.jpg', 5, 'Bruins', 9),
       (6, 'image6.jpg', 1, 'Heat', 8),
       (7, 'image7.jpg', 2, 'Giants', 13),
       (8, 'image8.jpg', 3, 'Cowboys', 5),
       (9, 'image9.jpg', 5, 'Maple Leafs', 6),
       (10, 'image10.jpg', 4, 'Sounders', 7),
       (11, 'image11.jpg', 4, 'Sounders', 15),
       (12, 'image12.jpg', 6, 'Minecraft', 10),
       (13, 'image13.jpg', 6, 'Minecraft', 11),
       (15, 'image14.jpg', 7, 'Cowgirls', 16);

INSERT INTO tab_user (user_id, date_of_birth, email, first_name, last_name, password, profile_picture, is_password_secure, location_id)
VALUES
(1, '2000-07-12', 'twin.onkel.other@nomai.net', 'Twin', 'Onkel', 'password22', 'https://nomai.net/twin_onkel.jpg', FALSE, 1);