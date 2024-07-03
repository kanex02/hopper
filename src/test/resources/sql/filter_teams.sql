INSERT INTO location (id, address_line1, address_line2, city, country)
VALUES (1, '', '', 'Dallas', 'US'),
       (2, '', '', 'New York', 'US'),
       (3, '', '', 'Chicago', 'US');


INSERT INTO sport (sport_id, sport_name)
VALUES
    (1, 'football'),
    (2, 'basketball'),
    (3, 'volleyball');

INSERT INTO team (ID, IMAGE, SPORT_ID, TEAM_NAME, LOCATION_ID)
VALUES (1, 'image1.jpg', 1, 'Knicks', 1),
       (2, 'image2.jpg', 1, 'Dodgers', 1),
       (3, 'image3.jpg', 2, 'Bears', 2),
       (4, 'image4.jpg', 2, 'Dynamo', 2),
       (5, 'image5.jpg', 2, 'Bruins', 3);