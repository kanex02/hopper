-- Generate entities
INSERT INTO `tab_user` (`user_id`, `date_of_birth`, `email`, `first_name`, `is_password_secure`, `last_name`, `password`)
VALUES
    (1, '', 'a', '', 1, '', '');

INSERT INTO `tab_user` (`user_id`, `date_of_birth`, `email`, `first_name`, `is_password_secure`, `last_name`, `password`)
VALUES
    (2, '', 'b', '', 1, '', '');

INSERT INTO `tab_user` (`user_id`, `date_of_birth`, `email`, `first_name`, `is_password_secure`, `last_name`, `password`)
VALUES
    (3, '', 'c', '', 1, '', '');

INSERT INTO `team` (`id`)
VALUES
    (1);

INSERT INTO `club` (`id`, `user_id`)
VALUES
    (1, 3);


-- Map followers
INSERT INTO `follower_mapping` (`followed_id`, `follower_id`)
VALUES
    (2, 1);

INSERT INTO `team_followers` (`team_id`, `user_id`)
VALUES
    (1, 1);

INSERT INTO `club_followers` (`club_id`, `user_id`)
VALUES
    (1, 1);


-- Create posts
INSERT INTO `blog_post` (`id`, `date`, `description`, `title`, `user_id`, `blog_visibility`, `deletable`)
VALUES
    (1, '2023-09-25 04:14:33.757000', 'user post', 'user', 2, 'PUBLIC', 0);

INSERT INTO `blog_post` (`id`, `date`, `description`, `title`, `user_id`, `blog_visibility`, `deletable`, `team_proxy_id`)
VALUES
    (2, '2023-09-25 04:14:33.757000', 'team post', 'team', 3, 'PUBLIC', 0, 1);

INSERT INTO `blog_post` (`id`, `date`, `description`, `title`, `user_id`, `blog_visibility`, `deletable`, `club_proxy_id`)
VALUES
    (3, '2023-09-25 04:14:33.757000', 'club post', 'club', 3, 'PUBLIC', 0, 1);

INSERT INTO `blog_post` (`id`, `date`, `description`, `title`, `user_id`, `blog_visibility`, `deletable`)
VALUES
    (4, '2023-09-25 04:14:33.757000', 'user post', 'user', 3, 'PUBLIC', 0);