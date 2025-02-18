INSERT INTO tab_user (user_id, date_of_birth, email, first_name, last_name, password, profile_picture, is_password_secure)
VALUES
    (100, '1990-01-01', 'user100@example.com', 'User', 'One', 'password100', 'https://example.com/user100.jpg', TRUE),
    (101, '1991-02-02', 'user101@example.com', 'User', 'Two', 'password101', 'https://example.com/user101.jpg', TRUE),
    (102, '1992-03-03', 'user102@example.com', 'User', 'Three', 'password102', 'https://example.com/user102.jpg', TRUE),
    (103, '1993-04-04', 'user103@example.com', 'User', 'Four', 'password103', 'https://example.com/user103.jpg', TRUE),
    (104, '1994-05-05', 'user104@example.com', 'User', 'Five', 'password104', 'https://example.com/user104.jpg', TRUE),
    (105, '1995-06-06', 'user105@example.com', 'User', 'Six', 'password105', 'https://example.com/user105.jpg', TRUE),
    (106, '1996-07-07', 'user106@example.com', 'User', 'Seven', 'password106', 'https://example.com/user106.jpg', TRUE),
    (107, '1997-08-08', 'user107@example.com', 'User', 'Eight', 'password107', 'https://example.com/user107.jpg', TRUE),
    (108, '1998-09-09', 'user108@example.com', 'User', 'Nine', 'password108', 'https://example.com/user108.jpg', TRUE),
    (109, '1999-10-10', 'user109@example.com', 'User', 'Ten', 'password109', 'https://example.com/user109.jpg', TRUE),
    (110, '2000-11-11', 'user110@example.com', 'User', 'Eleven', 'password110', 'https://example.com/user110.jpg', TRUE),
    (111, '2001-12-12', 'user111@example.com', 'User', 'Twelve', 'password111', 'https://example.com/user111.jpg', TRUE),
    (112, '2002-01-13', 'user112@example.com', 'User', 'Thirteen', 'password112', 'https://example.com/user112.jpg', TRUE),
    (113, '2003-02-14', 'user113@example.com', 'User', 'Fourteen', 'password113', 'https://example.com/user113.jpg', TRUE),
    (114, '2004-03-15', 'user114@example.com', 'User', 'Fifteen', 'password114', 'https://example.com/user114.jpg', TRUE),
    (115, '2005-04-16', 'user115@example.com', 'User', 'Sixteen', 'password115', 'https://example.com/user115.jpg', TRUE),
    (116, '2006-05-17', 'user116@example.com', 'User', 'Seventeen', 'password116', 'https://example.com/user116.jpg', TRUE),
    (117, '2007-06-18', 'user117@example.com', 'User', 'Eighteen', 'password117', 'https://example.com/user117.jpg', TRUE),
    (118, '2008-07-19', 'user118@example.com', 'User', 'Nineteen', 'password118', 'https://example.com/user118.jpg', TRUE),
    (119, '2009-08-20', 'user119@example.com', 'User', 'Twenty', 'password119', 'https://example.com/user119.jpg', TRUE),
    (120, '1990-01-01', 'user120@example.com', 'User', 'Twenty', 'password120', 'https://example.com/user120.jpg', TRUE),
    (121, '1991-02-02', 'user121@example.com', 'User', 'Twenty-One', 'password121', 'https://example.com/user121.jpg', TRUE),
    (122, '1992-03-03', 'user122@example.com', 'User', 'Twenty-Two', 'password122', 'https://example.com/user122.jpg', TRUE),
    (123, '1993-04-04', 'user123@example.com', 'User', 'Twenty-Three', 'password123', 'https://example.com/user123.jpg', TRUE),
    (124, '1994-05-05', 'user124@example.com', 'User', 'Twenty-Four', 'password124', 'https://example.com/user124.jpg', TRUE),
    (125, '1995-06-06', 'user125@example.com', 'User', 'Twenty-Five', 'password125', 'https://example.com/user125.jpg', TRUE),
    (126, '1996-07-07', 'user126@example.com', 'User', 'Twenty-Six', 'password126', 'https://example.com/user126.jpg', TRUE),
    (127, '1997-08-08', 'user127@example.com', 'User', 'Twenty-Seven', 'password127', 'https://example.com/user127.jpg', TRUE),
    (128, '1998-09-09', 'user128@example.com', 'User', 'Twenty-Eight', 'password128', 'https://example.com/user128.jpg', TRUE),
    (129, '1999-10-10', 'user129@example.com', 'User', 'Twenty-Nine', 'password129', 'https://example.com/user129.jpg', TRUE),
    (130, '2000-11-11', 'user130@example.com', 'User', 'Thirty', 'password130', 'https://example.com/user130.jpg', TRUE);

INSERT INTO challenge (id, end_date, title, goal, hops, user_Generated_For_user_id, is_Complete, completed_date)
VALUES
    (1, '2023-04-20', 'Go outside', 'Touch grass 5 times', 40, 100, TRUE, CURRENT_TIMESTAMP() - INTERVAL '2' DAY),
    (2, '2023-04-20', 'Go outside', 'Touch grass 6 times', 40, 100, TRUE, CURRENT_TIMESTAMP() - INTERVAL '1' DAY),
    (3, '2023-04-20', 'Go outside', 'Touch grass 7 times', 40, 101, TRUE, CURRENT_TIMESTAMP() - INTERVAL '2' DAY),
    (4, '2023-04-20', 'Read a Book', 'Read 30 pages of a novel', 40, 101, TRUE, CURRENT_TIMESTAMP() - INTERVAL '5' DAY),
    (5, '2023-05-20', 'Run a Mile', 'Run a mile within 10 minutes', 50, 102, FALSE, NULL),
    (6, '2023-04-18', 'Finish Homework', 'Finish all pending assignments', 40, 102, TRUE, CURRENT_TIMESTAMP() - INTERVAL '6' DAY),
    (7, '2023-04-19', 'Do Yoga', 'Perform a 15-minute yoga session', 35, 103, FALSE, NULL),
    (8, '2023-04-15', 'Cook Dinner', 'Cook a dinner meal for the family', 40, 103, TRUE, CURRENT_TIMESTAMP() - INTERVAL '2' DAY),
    (9, '2023-05-25', 'Write Code', 'Solve 5 coding challenges', 45, 104, FALSE, NULL),
    (10, '2023-04-21', 'Plant a Tree', 'Plant a tree in your yard', 45, 104, TRUE, CURRENT_TIMESTAMP() - INTERVAL '4' DAY),
    (11, '2023-04-21', 'Write a Poem', 'Write a 20-line poem', 35, 105, TRUE, CURRENT_TIMESTAMP() - INTERVAL '1' DAY),
    (12, '2023-04-22', 'Learn an Instrument', 'Practice the guitar for 30 minutes', 45, 105, FALSE, NULL),
    (13, '2023-04-25', 'Watch a Movie', 'Watch a movie from your watchlist', 30, 106, TRUE, CURRENT_TIMESTAMP() - INTERVAL '7' DAY),
    (14, '2023-04-27', 'Go Hiking', 'Go for a 3-mile hike', 50, 106, FALSE, NULL),
    (15, '2023-05-10', 'Visit a Museum', 'Visit a museum and learn something new', 35, 107, FALSE, NULL),
    (16, '2023-05-15', 'Draw a Picture', 'Draw a picture and share it with friends', 40, 107, TRUE, CURRENT_TIMESTAMP() - INTERVAL '7' DAY),
    (17, '2023-05-18', 'Build a Lego Set', 'Build a 300-piece Lego set', 45, 108, TRUE, CURRENT_TIMESTAMP() - INTERVAL '2' DAY),
    (18, '2023-06-10', 'Meditate', 'Meditate for 15 minutes', 35, 108, FALSE, NULL),
    (19, '2023-06-15', 'Learn a New Skill', 'Learn the basics of knitting', 40, 109, TRUE, CURRENT_TIMESTAMP() - INTERVAL '3' DAY),
    (20, '2023-06-20', 'Ride a Bike', 'Ride a bike for 5 miles', 50, 109, FALSE, NULL),
    (21, '2023-04-20', 'Go outside', 'Touch grass 5 times', 40, 110, FALSE, NULL),
    (22, '2023-04-20', 'Go outside', 'Touch grass 6 times', 40, 110, TRUE, CURRENT_TIMESTAMP() - INTERVAL '7' DAY),
    (23, '2023-04-20', 'Go outside', 'Touch grass 7 times', 40, 111, FALSE, NULL),
    (24, '2023-04-20', 'Read a Book', 'Read 30 pages of a novel', 35, 111, TRUE, CURRENT_TIMESTAMP() - INTERVAL '6' DAY),
    (25, '2023-05-20', 'Run a Mile', 'Run a mile within 10 minutes', 50, 112, FALSE, NULL),
    (26, '2023-04-18', 'Finish Homework', 'Finish all pending assignments', 40, 112, TRUE, CURRENT_TIMESTAMP() - INTERVAL '1' DAY),
    (27, '2023-04-19', 'Do Yoga', 'Perform a 15-minute yoga session', 35, 113, FALSE, NULL),
    (28, '2023-04-15', 'Cook Dinner', 'Cook a dinner meal for the family', 40, 113, TRUE, CURRENT_TIMESTAMP() - INTERVAL '3' DAY),
    (29, '2023-05-25', 'Write Code', 'Solve 5 coding challenges', 45, 114, FALSE, NULL),
    (30, '2023-04-21', 'Plant a Tree', 'Plant a tree in your yard', 45, 114, TRUE, CURRENT_TIMESTAMP() - INTERVAL '7' DAY),
    (31, '2023-04-20', 'Go outside', 'Touch grass 5 times', 40, 115, FALSE, NULL),
    (32, '2023-04-20', 'Go outside', 'Touch grass 6 times', 40, 115, TRUE, CURRENT_TIMESTAMP() - INTERVAL '2' DAY),
    (33, '2023-04-20', 'Go outside', 'Touch grass 7 times', 40, 116, FALSE, NULL),
    (34, '2023-04-20', 'Read a Book', 'Read 30 pages of a novel', 35, 116, TRUE, CURRENT_TIMESTAMP() - INTERVAL '4' DAY),
    (35, '2023-05-20', 'Run a Mile', 'Run a mile within 10 minutes', 50, 117, FALSE, NULL),
    (36, '2023-04-18', 'Finish Homework', 'Finish all pending assignments', 40, 117, TRUE, CURRENT_TIMESTAMP() - INTERVAL '6' DAY),
    (37, '2023-04-19', 'Do Yoga', 'Perform a 15-minute yoga session', 35, 118, FALSE, NULL),
    (38, '2023-04-15', 'Cook Dinner', 'Cook a dinner meal for the family', 40, 118, TRUE, CURRENT_TIMESTAMP() - INTERVAL '3' DAY),
    (39, '2023-05-25', 'Write Code', 'Solve 5 coding challenges', 45, 119, FALSE, NULL),
    (40, '2023-04-21', 'Plant a Tree', 'Plant a tree in your yard', 45, 119, TRUE, CURRENT_TIMESTAMP() - INTERVAL '1' DAY),
    (41, '2023-04-21', 'Write a Poem', 'Write a 20-line poem', 35, 120, TRUE, CURRENT_TIMESTAMP() - INTERVAL '5' DAY),
    (42, '2023-04-22', 'Learn an Instrument', 'Practice the guitar for 30 minutes', 45, 120, FALSE, NULL),
    (43, '2023-04-20', 'Go outside', 'Touch grass 5 times', 40, 121, FALSE, NULL),
    (44, '2023-04-20', 'Go outside', 'Touch grass 6 times', 40, 121, TRUE, CURRENT_TIMESTAMP() - INTERVAL '3' DAY),
    (45, '2023-04-20', 'Go outside', 'Touch grass 7 times', 40, 122, FALSE, NULL),
    (46, '2023-04-20', 'Read a Book', 'Read 30 pages of a novel', 35, 122, TRUE, CURRENT_TIMESTAMP() - INTERVAL '5' DAY),
    (47, '2023-05-20', 'Run a Mile', 'Run a mile within 10 minutes', 50, 123, FALSE, NULL),
    (48, '2023-04-18', 'Finish Homework', 'Finish all pending assignments', 40, 123, TRUE, CURRENT_TIMESTAMP() - INTERVAL '6' DAY),
    (49, '2023-04-19', 'Do Yoga', 'Perform a 15-minute yoga session', 35, 124, FALSE, NULL),
    (50, '2023-04-15', 'Cook Dinner', 'Cook a dinner meal for the family', 40, 124, TRUE, CURRENT_TIMESTAMP() - INTERVAL '2' DAY),
    (51, '2023-05-25', 'Write Code', 'Solve 5 coding challenges', 45, 125, FALSE, NULL),
    (52, '2023-04-21', 'Plant a Tree', 'Plant a tree in your yard', 45, 125, TRUE, CURRENT_TIMESTAMP() - INTERVAL '4' DAY),
    (53, '2023-04-21', 'Write a Poem', 'Write a 20-line poem', 35, 126, FALSE, NULL),
    (54, '2023-04-22', 'Learn an Instrument', 'Practice the guitar for 30 minutes', 45, 126, FALSE, NULL),
    (55, '2023-04-25', 'Watch a Movie', 'Watch a movie from your watchlist', 30, 127, FALSE, NULL),
    (56, '2023-04-27', 'Go Hiking', 'Go for a 3-mile hike', 50, 127, FALSE, NULL),
    (57, '2023-05-10', 'Visit a Museum', 'Visit a museum and learn something new', 35, 128, FALSE, NULL),
    (58, '2023-05-15', 'Draw a Picture', 'Draw a picture and share it with friends', 40, 128, FALSE, NULL),
    (59, '2023-05-18', 'Build a Lego Set', 'Build a 300-piece Lego set', 45, 129, FALSE, NULL),
    (60, '2023-06-10', 'Meditate', 'Meditate for 15 minutes', 35, 129, FALSE, NULL),
    (61, '2023-06-15', 'Learn a New Skill', 'Learn the basics of knitting', 40, 130, FALSE, NULL),
    (62, '2023-06-20', 'Ride a Bike', 'Ride a bike for 5 miles', 50, 130, FALSE, NULL);

INSERT INTO challenge_completion_status (challenge_id, user_id, is_completed, completed_date)
SELECT id AS challenge_id, user_Generated_For_user_id AS user_id, is_Complete, completed_date
FROM challenge;