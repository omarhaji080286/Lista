DELETE FROM `goods` WHERE 1;
DELETE FROM `categories` WHERE 1;
DELETE FROM `co_users` WHERE 1;
DELETE FROM `users` WHERE 1;
DELETE FROM `groups` WHERE 1;
DELETE FROM `shops` WHERE 1;
DELETE FROM `shop_types` WHERE 1;
DELETE FROM `cities` WHERE 1;
DELETE FROM `countries` WHERE 1;

INSERT INTO `users` (`server_user_id`, `email`, `password`, `user_name`, `server_group_id`, `update_available`, `sign_up_type`) VALUES ('1', 'default@lista.com', 'lista', 'default', NULL, '0', 'lista');

INSERT INTO `categories` (`server_category_id`, `category_name`, `category_color`, `category_icon`, `sync_status`, `email`, `device_category_id`, `last_update_user_id`, `crud_status`, `server_user_id`) VALUES
(1, 'FRUITS & LEGUMES', 2131099762, 2131230873, 0, 'default@lista.com', 0, 0, 10, 1),
(2, 'PAIN & VIENNOISERIES', 2131099741, 2131230816, 0, 'default@lista.com', 0, 0, 20, 1),
(3, 'VIANDES & POISSONS', 2131099686, 2131230932, 0, 'default@lista.com', 0, 0, 30, 1),
(4, 'MARCHAND D\'EPICES', 2131099740, 2131230931, 0, 'default@lista.com', 0, 0, 40, 1),
(5, 'MENAGE & HYGIENE', 2131099765, 2131230874, 0, 'default@lista.com', 0, 0, 50, 1),
(6, 'ALIMENTATION GENERALE', 2131099805, 2131230880, 0, 'default@lista.com', 0, 0, 60, 1),
(7, 'FROMAGES & CHARCUTERIE', 2131099781, 2131230819, 0, 'default@lista.com', 0, 0, 70, 1),
(8, 'DIVERS', 2131099790, 2131230923, 0, 'default@lista.com', 0, 0, 80, 1);


INSERT INTO `goods` (`server_good_id`, `good_name`, `quantity_level`, `is_to_buy`, `sync_status`, `device_good_id`, `device_category_id`, `email`, `last_update_user_id`, `crud_status`, `server_category_id`, `good_desc`, `server_originator_id`) VALUES
(1, 'Carottes', 1, 0, 0, 0,0, 'default@lista.com', 0, 10, 1,'',1),
(2, 'Pain', 1, 0, 0, 0,0, 'default@lista.com', 0, 20, 2,'',1),
(3, 'Viande hachÃ©e', 1, 0, 0, 0,0, 'default@lista.com', 0, 30, 3,'',1),
(4, 'Cumin', 1, 0, 0, 0,0, 'default@lista.com', 0, 40, 4,'',1),
(5, 'Dentifrice', 1, 0, 0, 0,0, 'default@lista.com', 0, 50, 5,'',1),
(6, 'Sauce tomate', 1, 0, 0, 0,0, 'default@lista.com', 0, 60, 6,'',1),
(7, 'Kiri', 1, 0, 0, 0,0, 'default@lista.com', 0, 70, 7,'',1),
(8, 'Lampe', 1, 0, 0, 0,0, 'default@lista.com', 0, 80, 8,'',1);


INSERT INTO `shop_types` (`server_shop_type_id`, `shop_type_name`) VALUES (1, 'Alimentation generale');
INSERT INTO `shop_types` (`server_shop_type_id`, `shop_type_name`) VALUES (2, 'Boucher');
INSERT INTO `shop_types` (`server_shop_type_id`, `shop_type_name`) VALUES (3, 'Fruits & Legumes');

INSERT INTO `countries` (`server_country_id`, `country_name`) VALUES (1, 'Morocco');

INSERT INTO `cities` (`server_city_id`, `city_name`, `server_country_id`) VALUES (1, 'Rabat', '1');
INSERT INTO `cities` (`server_city_id`, `city_name`, `server_country_id`) VALUES (2, 'Casablanca', '1');
INSERT INTO `cities` (`server_city_id`, `city_name`, `server_country_id`) VALUES (3, 'Tanger', '1');
INSERT INTO `cities` (`server_city_id`, `city_name`, `server_country_id`) VALUES (4, 'Marrakech', '1');

INSERT INTO `shops` (`server_shop_id`, `shop_name`, `shop_adress`, `shop_email`, `shop_phone`, `longitude`, `latitude`, `server_shop_type_id`, `server_city_id`, `server_country_id`) VALUES ('1', 'chez Ali', '45 Hay el fath', 'chezali@gmail.com', '06 11 11 11 11', '-6.902916', '33.967371', '2', '1', '1');

INSERT INTO `shops` (`server_shop_id`, `shop_name`, `shop_adress`, `shop_email`, `shop_phone`, `longitude`, `latitude`, `server_shop_type_id`, `server_city_id`, `server_country_id`) VALUES ('2', 'chez Karim', '45 Hay Riad', 'chezkarim@gmail.com', '06 33 33 33 33', '-6.879130', '33.960148', '2', '1', '1');

INSERT INTO `shops` (`server_shop_id`, `shop_name`, `shop_adress`, `shop_email`, `shop_phone`, `longitude`, `latitude`, `server_shop_type_id`, `server_city_id`, `server_country_id`) VALUES ('3', 'Epicerie du coin', '103 Hay el Menzeh', 'epicerieducoin@gmail.com', '06 22 22 22 22', '-6.883720', '33.983895', '1', '1', '1');

INSERT INTO `shops` (`server_shop_id`, `shop_name`, `shop_adress`, `shop_email`, `shop_phone`, `longitude`, `latitude`, `server_shop_type_id`, `server_city_id`, `server_country_id`) VALUES ('4', 'Ibrahim khaddar', '103 Hay el Massira', 'ibrahimkhaddar@gmail.com', '06 44 44 44 44', '-6.881696', '33.981511', '3', '1', '1');