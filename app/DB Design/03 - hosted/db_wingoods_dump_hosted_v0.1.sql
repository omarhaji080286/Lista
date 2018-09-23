-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Client :  127.0.0.1
-- Généré le :  Dim 28 Janvier 2018 à 19:17
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
  `category_id` int(11) NOT NULL,
  `category_name` varchar(45) NOT NULL,
  `category_color` int(11) NOT NULL,
  `category_icon` int(11) NOT NULL,
  `sync_status` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Contenu de la table `categories`
--

INSERT INTO `categories` (`category_id`, `category_name`, `category_color`, `category_icon`, `sync_status`, `user_id`) VALUES
(1, 'FRUITS & LEGUMES', 0, 0, 1, 1),
(2, 'PAIN & VIENNOISERIES', 0, 0, 1, 1),
(3, 'VIANDES & POISSONS', 0, 0, 1, 1),
(4, 'MARCHAND D\'EPICES', 0, 0, 1, 1),
(5, 'MENAGE & HYGIENE', 0, 0, 1, 1),
(6, 'ALIMENTATION GENERALE', 0, 0, 1, 1),
(7, 'FROMAGES & CHARCUTERIE', 0, 0, 1, 1),
(8, 'DIVERS', 0, 0, 1, 1);

-- --------------------------------------------------------

--
-- Structure de la table `co_users`
--

CREATE TABLE `co_users` (
  `co_user_id` int(11) NOT NULL,
  `email` varchar(45) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `confirmation_status` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `goods`
--

CREATE TABLE `goods` (
  `good_id` int(11) NOT NULL,
  `good_name` varchar(45) NOT NULL,
  `quantity_level` int(11) NOT NULL DEFAULT '1',
  `is_to_buy` int(11) NOT NULL DEFAULT '1',
  `sync_status` int(11) NOT NULL DEFAULT '0',
  `category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Contenu de la table `goods`
--

INSERT INTO `goods` (`good_id`, `good_name`, `quantity_level`, `is_to_buy`, `sync_status`, `category_id`) VALUES
(1, 'Carottes', 1, 1, 1, 1),
(2, 'Pain', 1, 1, 1, 2),
(3, 'Viande hachée', 1, 1, 1, 3),
(4, 'Cumin', 1, 1, 1, 4),
(5, 'Dentifrice', 1, 1, 1, 5),
(6, 'Sauce tomate', 1, 1, 1, 6),
(7, 'Kiri', 1, 1, 1, 7),
(8, 'Lampe', 1, 1, 1, 8);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `user_name` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Contenu de la table `users`
--

INSERT INTO `users` (`user_id`, `email`, `password`, `user_name`) VALUES
(1, 'omar.haji@gmail.com', 'aaaaaa', 'omar.haji');

--
-- Index pour les tables exportées
--

--
-- Index pour la table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`,`user_id`),
  ADD UNIQUE KEY `category_id_UNIQUE` (`category_id`),
  ADD KEY `fk_categories_users1_idx` (`user_id`);

--
-- Index pour la table `co_users`
--
ALTER TABLE `co_users`
  ADD PRIMARY KEY (`co_user_id`),
  ADD UNIQUE KEY `email_UNIQUE` (`email`),
  ADD KEY `fk_co_users_users1_idx` (`user_id`);

--
-- Index pour la table `goods`
--
ALTER TABLE `goods`
  ADD PRIMARY KEY (`good_id`,`category_id`),
  ADD UNIQUE KEY `good_id_UNIQUE` (`good_id`),
  ADD KEY `fk_goods_categories_idx` (`category_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `idusers_UNIQUE` (`email`),
  ADD UNIQUE KEY `user_id_UNIQUE` (`user_id`);

--
-- AUTO_INCREMENT pour les tables exportées
--

--
-- AUTO_INCREMENT pour la table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT pour la table `co_users`
--
ALTER TABLE `co_users`
  MODIFY `co_user_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `goods`
--
ALTER TABLE `goods`
  MODIFY `good_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `categories`
--
ALTER TABLE `categories`
  ADD CONSTRAINT `fk_categories_users1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `co_users`
--
ALTER TABLE `co_users`
  ADD CONSTRAINT `fk_co_users_users1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `goods`
--
ALTER TABLE `goods`
  ADD CONSTRAINT `fk_goods_categories` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
