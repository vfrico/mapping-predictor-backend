-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         8.0.11 - MySQL Community Server - GPL
-- SO del servidor:              Linux
-- HeidiSQL Versión:             9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Volcando estructura de base de datos para mappings_annotations
CREATE DATABASE IF NOT EXISTS `mappings_annotations` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
USE `mappings_annotations`;

-- Volcando estructura para tabla mappings_annotations.annotation
CREATE TABLE IF NOT EXISTS `annotation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `langA` varchar(3) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'Language A (use ISO code of two digits when possible)',
  `langB` varchar(3) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'Language B (use ISO code of two digits when possible)',
  `templateA` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `templateB` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `attributeA` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `attributeB` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `propertyA` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `propertyB` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `classA` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `classB` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `propDomainA` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `propDomainB` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `propRangeA` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `propRangeB` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `c1` float DEFAULT NULL,
  `c2` float DEFAULT NULL,
  `c3A` float DEFAULT NULL,
  `c3B` float DEFAULT NULL,
  `m1` int(11) DEFAULT NULL,
  `m2` int(11) DEFAULT NULL,
  `m3` int(11) DEFAULT NULL,
  `m4A` int(11) DEFAULT NULL,
  `m4B` int(11) DEFAULT NULL,
  `m5A` int(11) DEFAULT NULL,
  `m5B` int(11) DEFAULT NULL,
  `tb1` int(11) DEFAULT NULL,
  `tb2` int(11) DEFAULT NULL,
  `tb3` int(11) DEFAULT NULL,
  `tb4` int(11) DEFAULT NULL,
  `tb5` int(11) DEFAULT NULL,
  `tb6` int(11) DEFAULT NULL,
  `tb7` int(11) DEFAULT NULL,
  `tb8` int(11) DEFAULT NULL,
  `tb9` int(11) DEFAULT NULL,
  `tb10` int(11) DEFAULT NULL,
  `tb11` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla mappings_annotations.classification_results
CREATE TABLE IF NOT EXISTS `classification_results` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_annotation` int(11) DEFAULT NULL COMMENT 'FK references annotation id',
  `classified_as` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'References to vote types',
  `probability` decimal(10,4) DEFAULT NULL COMMENT 'The probability that the classifier has estimated',
  `date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_idannotation_classification` (`id_annotation`),
  KEY `fk_votetype_classification` (`classified_as`),
  CONSTRAINT `fk_idannotation_classification` FOREIGN KEY (`id_annotation`) REFERENCES `annotation` (`id`),
  CONSTRAINT `fk_votetype_classification` FOREIGN KEY (`classified_as`) REFERENCES `vote_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla mappings_annotations.lock
CREATE TABLE IF NOT EXISTS `lock` (
  `idlock` int(11) NOT NULL AUTO_INCREMENT,
  `date_start` datetime DEFAULT NULL,
  `date_end` datetime DEFAULT NULL,
  `id_annotation` int(11) NOT NULL,
  `username` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`idlock`),
  KEY `fk_lock_1_idx` (`id_annotation`),
  KEY `fk_username` (`username`),
  CONSTRAINT `fk_annotation` FOREIGN KEY (`id_annotation`) REFERENCES `annotation` (`id`),
  CONSTRAINT `fk_username` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla mappings_annotations.users
CREATE TABLE IF NOT EXISTS `users` (
  `idusers` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `email` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `password_md5` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'Long enough to use any hashing algorithm\n',
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `jwt` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `role` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT 'NO_ROLE',
  PRIMARY KEY (`idusers`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_users_1` (`role`),
  CONSTRAINT `fk_users_1` FOREIGN KEY (`role`) REFERENCES `users_role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla mappings_annotations.users_role
CREATE TABLE IF NOT EXISTS `users_role` (
  `role` varchar(20) COLLATE utf8_bin NOT NULL,
  `code` int(11) DEFAULT NULL,
  PRIMARY KEY (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla mappings_annotations.vote
CREATE TABLE IF NOT EXISTS `vote` (
  `idvote` int(11) NOT NULL AUTO_INCREMENT,
  `annotation_id` int(11) NOT NULL,
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `vote` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `username` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`idvote`),
  KEY `fk_vote_1_idx` (`annotation_id`),
  KEY `fk_vote_type` (`vote`),
  KEY `fk_vote_username` (`username`),
  CONSTRAINT `fk_annotation_id` FOREIGN KEY (`annotation_id`) REFERENCES `annotation` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_vote_type` FOREIGN KEY (`vote`) REFERENCES `vote_type` (`type`),
  CONSTRAINT `fk_vote_username` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla mappings_annotations.vote_type
CREATE TABLE IF NOT EXISTS `vote_type` (
  `type` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- La exportación de datos fue deseleccionada.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
