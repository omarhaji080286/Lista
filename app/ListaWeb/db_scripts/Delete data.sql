DELETE FROM `goods` WHERE 1;
DELETE FROM `categories` WHERE 1;
DELETE FROM `co_users` WHERE 1;
DELETE FROM `users` WHERE 1;
DELETE FROM `groups` WHERE 1;


UPDATE co_users set confirmation_status=0, has_responded=0;
DELETE FROM goods WHERE goods.email = "imanelkahlaoui@gmail.com";
DELETE FROM `categories` WHERE `categories`.`email` = "imanelkahlaoui@gmail.com";
DELETE FROM users WHERE users.email = "imanelkahlaoui@gmail.com";


karimamrani0909@gmail.com
omar.haji@gmail.com