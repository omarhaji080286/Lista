-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Client :  127.0.0.1
-- Généré le :  Ven 07 Septembre 2018 à 15:12
-- Version du serveur :  5.7.14
-- Version de PHP :  5.6.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `db_wingoods`
--

-- --------------------------------------------------------

--
-- Structure de la table `categories`
--

CREATE TABLE `categories` (
  `server_category_id` int(11) NOT NULL,
  `category_name` varchar(45) NOT NULL,
  `category_color` int(11) NOT NULL,
  `category_icon` int(11) NOT NULL,
  `sync_status` int(11) NOT NULL DEFAULT '0',
  `email` varchar(45) NOT NULL,
  `device_category_id` int(11) NOT NULL,
  `last_update_user_id` int(11) NOT NULL,
  `crud_status` int(11) NOT NULL DEFAULT '0',
  `server_user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `co_users`
--

CREATE TABLE `co_users` (
  `server_co_user_id` int(11) NOT NULL,
  `device_co_user_id` int(11) DEFAULT NULL,
  `co_user_email` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `confirmation_status` int(11) NOT NULL,
  `has_responded` int(11) NOT NULL,
  `sync_status` int(11) NOT NULL,
  `server_user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `goods`
--

CREATE TABLE `goods` (
  `server_good_id` int(11) NOT NULL,
  `good_name` varchar(45) NOT NULL,
  `quantity_level` int(11) NOT NULL DEFAULT '1',
  `is_to_buy` int(11) NOT NULL DEFAULT '1',
  `sync_status` int(11) NOT NULL DEFAULT '0',
  `device_good_id` int(11) NOT NULL,
  `device_category_id` int(11) NOT NULL,
  `email` varchar(45) NOT NULL,
  `last_update_user_id` int(11) NOT NULL,
  `crud_status` int(11) NOT NULL DEFAULT '0',
  `server_category_id` int(11) NOT NULL,
  `good_desc` varchar(45) DEFAULT NULL,
  `server_originator_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `groups`
--

CREATE TABLE `groups` (
  `server_group_id` int(11) NOT NULL,
  `group_name` varchar(45) NOT NULL,
  `owner_email` varchar(45) NOT NULL,
  `server_owner_id` varchar(45) NOT NULL,
  `sync_status` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `ordered_goods`
--

CREATE TABLE `ordered_goods` (
  `server_ordered_good_id` int(11) NOT NULL,
  `server_good_id` int(11) NOT NULL,
  `server_category_id` int(11) NOT NULL,
  `server_order_id` int(11) NOT NULL,
  `server_user_id` int(11) NOT NULL,
  `server_shop_id` int(11) NOT NULL,
  `server_shop_type_id` int(11) NOT NULL,
  `good_name` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `orders`
--

CREATE TABLE `orders` (
  `server_order_id` int(11) NOT NULL,
  `server_user_id` int(11) NOT NULL,
  `server_shop_id` int(11) NOT NULL,
  `server_shop_type_id` int(11) NOT NULL,
  `order_status` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `shopkeepers`
--

CREATE TABLE `shopkeepers` (
  `server_shopkeeper_id` int(11) NOT NULL,
  `shopkeeper_email` varchar(45) NOT NULL,
  `shopkeeper_password` varchar(45) NOT NULL,
  `shopkeeper_name` varchar(45) NOT NULL,
  `shopkeeper_phone` varchar(45) DEFAULT NULL,
  `server_shop_id` int(11) NOT NULL,
  `server_shop_type_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `shops`
--

CREATE TABLE `shops` (
  `server_shop_id` int(11) NOT NULL,
  `shop_name` varchar(45) DEFAULT NULL,
  `shop_adress` varchar(45) DEFAULT NULL,
  `store_email` varchar(45) DEFAULT NULL,
  `shop_phone` varchar(45) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `server_shop_type_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `shop_types`
--

CREATE TABLE `shop_types` (
  `server_shop_type_id` int(11) NOT NULL,
  `shop_type_name` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `server_user_id` int(11) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `user_name` varchar(45) DEFAULT NULL,
  `server_group_id` int(11) DEFAULT NULL,
  `update_available` int(11) NOT NULL DEFAULT '0',
  `sign_up_type` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Index pour les tables exportées
--

--
-- Index pour la table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`server_category_id`,`server_user_id`),
  ADD UNIQUE KEY `category_id_UNIQUE` (`server_category_id`),
  ADD KEY `fk_categories_users1_idx` (`server_user_id`);

--
-- Index pour la table `co_users`
--
ALTER TABLE `co_users`
  ADD PRIMARY KEY (`server_co_user_id`,`server_user_id`),
  ADD KEY `fk_co_users_users1_idx` (`server_user_id`);

--
-- Index pour la table `goods`
--
ALTER TABLE `goods`
  ADD PRIMARY KEY (`server_good_id`,`server_category_id`),
  ADD UNIQUE KEY `good_id_UNIQUE` (`server_good_id`),
  ADD KEY `fk_goods_categories_idx` (`server_category_id`);

--
-- Index pour la table `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`server_group_id`);

--
-- Index pour la table `ordered_goods`
--
ALTER TABLE `ordered_goods`
  ADD PRIMARY KEY (`server_ordered_good_id`,`server_good_id`,`server_category_id`,`server_order_id`,`server_user_id`,`server_shop_id`,`server_shop_type_id`),
  ADD KEY `fk_ordered_goods_goods1_idx` (`server_good_id`,`server_category_id`),
  ADD KEY `fk_ordered_goods_orders1_idx` (`server_order_id`,`server_user_id`,`server_shop_id`,`server_shop_type_id`);

--
-- Index pour la table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`server_order_id`,`server_user_id`,`server_shop_id`,`server_shop_type_id`),
  ADD KEY `fk_orders_users1_idx` (`server_user_id`),
  ADD KEY `fk_orders_shops1_idx` (`server_shop_id`,`server_shop_type_id`);

--
-- Index pour la table `shopkeepers`
--
ALTER TABLE `shopkeepers`
  ADD PRIMARY KEY (`server_shopkeeper_id`,`server_shop_id`,`server_shop_type_id`),
  ADD KEY `fk_shopkeepers_shops1_idx` (`server_shop_id`,`server_shop_type_id`);

--
-- Index pour la table `shops`
--
ALTER TABLE `shops`
  ADD PRIMARY KEY (`server_shop_id`,`server_shop_type_id`),
  ADD KEY `fk_stores_store_types1_idx` (`server_shop_type_id`);

--
-- Index pour la table `shop_types`
--
ALTER TABLE `shop_types`
  ADD PRIMARY KEY (`server_shop_type_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`server_user_id`),
  ADD UNIQUE KEY `user_id_UNIQUE` (`server_user_id`),
  ADD KEY `fk_users_groups1_idx` (`server_group_id`);

--
-- AUTO_INCREMENT pour les tables exportées
--

--
-- AUTO_INCREMENT pour la table `categories`
--
ALTER TABLE `categories`
  MODIFY `server_category_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `co_users`
--
ALTER TABLE `co_users`
  MODIFY `server_co_user_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `goods`
--
ALTER TABLE `goods`
  MODIFY `server_good_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `groups`
--
ALTER TABLE `groups`
  MODIFY `server_group_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `orders`
--
ALTER TABLE `orders`
  MODIFY `server_order_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `shopkeepers`
--
ALTER TABLE `shopkeepers`
  MODIFY `server_shopkeeper_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `shops`
--
ALTER TABLE `shops`
  MODIFY `server_shop_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `shop_types`
--
ALTER TABLE `shop_types`
  MODIFY `server_shop_type_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `server_user_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `categories`
--
ALTER TABLE `categories`
  ADD CONSTRAINT `fk_categories_users1` FOREIGN KEY (`server_user_id`) REFERENCES `users` (`server_user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `co_users`
--
ALTER TABLE `co_users`
  ADD CONSTRAINT `fk_co_users_users1` FOREIGN KEY (`server_user_id`) REFERENCES `users` (`server_user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `goods`
--
ALTER TABLE `goods`
  ADD CONSTRAINT `fk_goods_categories` FOREIGN KEY (`server_category_id`) REFERENCES `categories` (`server_category_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `ordered_goods`
--
ALTER TABLE `ordered_goods`
  ADD CONSTRAINT `fk_ordered_goods_goods1` FOREIGN KEY (`server_good_id`,`server_category_id`) REFERENCES `goods` (`server_good_id`, `server_category_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_ordered_goods_orders1` FOREIGN KEY (`server_order_id`,`server_user_id`,`server_shop_id`,`server_shop_type_id`) REFERENCES `orders` (`server_order_id`, `server_user_id`, `server_shop_id`, `server_shop_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `fk_orders_shops1` FOREIGN KEY (`server_shop_id`,`server_shop_type_id`) REFERENCES `shops` (`server_shop_id`, `server_shop_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_orders_users1` FOREIGN KEY (`server_user_id`) REFERENCES `users` (`server_user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `shopkeepers`
--
ALTER TABLE `shopkeepers`
  ADD CONSTRAINT `fk_shopkeepers_shops1` FOREIGN KEY (`server_shop_id`,`server_shop_type_id`) REFERENCES `shops` (`server_shop_id`, `server_shop_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `shops`
--
ALTER TABLE `shops`
  ADD CONSTRAINT `fk_stores_store_types1` FOREIGN KEY (`server_shop_type_id`) REFERENCES `shop_types` (`server_shop_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `fk_users_groups1` FOREIGN KEY (`server_group_id`) REFERENCES `groups` (`server_group_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
