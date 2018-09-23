-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Client :  127.0.0.1
-- Généré le :  Mer 07 Février 2018 à 17:47
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
  `server_category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `shared_categories`
--

CREATE TABLE `shared_categories` (
  `server_shared_category_id` int(11) NOT NULL,
  `server_category_id` int(11) NOT NULL,
  `server_user_id` int(11) NOT NULL,
  `server_co_user_id` int(11) NOT NULL,
  `device_shared_category_id` int(11) DEFAULT NULL,
  `device_category_id` int(11) DEFAULT NULL,
  `device_user_id` int(11) DEFAULT NULL,
  `device_co_user_id` int(11) DEFAULT NULL,
  `confirmation_status` int(11) NOT NULL,
  `sync_status` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `server_user_id` int(11) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `user_name` varchar(45) DEFAULT NULL
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
-- Index pour la table `shared_categories`
--
ALTER TABLE `shared_categories`
  ADD PRIMARY KEY (`server_shared_category_id`,`server_category_id`,`server_user_id`,`server_co_user_id`),
  ADD KEY `fk_shared_categories_categories1_idx` (`server_category_id`,`server_user_id`),
  ADD KEY `fk_shared_categories_co_users1_idx` (`server_co_user_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`server_user_id`),
  ADD UNIQUE KEY `idusers_UNIQUE` (`email`),
  ADD UNIQUE KEY `user_id_UNIQUE` (`server_user_id`);

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
-- AUTO_INCREMENT pour la table `shared_categories`
--
ALTER TABLE `shared_categories`
  MODIFY `server_shared_category_id` int(11) NOT NULL AUTO_INCREMENT;
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
-- Contraintes pour la table `shared_categories`
--
ALTER TABLE `shared_categories`
  ADD CONSTRAINT `fk_shared_categories_categories1` FOREIGN KEY (`server_category_id`,`server_user_id`) REFERENCES `categories` (`server_category_id`, `server_user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_shared_categories_co_users1` FOREIGN KEY (`server_co_user_id`) REFERENCES `co_users` (`server_co_user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
