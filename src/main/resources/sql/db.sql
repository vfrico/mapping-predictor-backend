-- MySQL dump 10.13  Distrib 8.0.12, for Linux (x86_64)
--
-- Host: localhost    Database: mappings_annotations
-- ------------------------------------------------------
-- Server version	8.0.11

-- /*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
-- /*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
-- /*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8 ;
-- /*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
-- /*!40103 SET TIME_ZONE='+00:00' */;
-- /*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
-- /*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
-- /*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
-- /*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


CREATE DATABASE IF NOT EXISTS `mappings_annotations`;
USE `mappings_annotations`;

--
-- Table structure for table `annotation`
--

DROP TABLE IF EXISTS `annotation`;
-- /*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `annotation` (
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
-- /*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vote_type`
--

DROP TABLE IF EXISTS `vote_type`;
-- /*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `vote_type` (
  `type` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- /*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `classification_results`
--

DROP TABLE IF EXISTS `classification_results`;
-- /*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `classification_results` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_annotation` int(11) NOT NULL COMMENT 'FK references annotation id',
  `classified_as` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'References to vote types',
  `probability` decimal(10,4) DEFAULT NULL COMMENT 'The probability that the classifier has estimated',
  `date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`id_annotation`,`classified_as`),
  KEY `fk_idannotation_classification` (`id_annotation`),
  KEY `fk_votetype_classification` (`classified_as`),
  CONSTRAINT `fk_idannotation_classification` FOREIGN KEY (`id_annotation`) REFERENCES `annotation` (`id`),
  CONSTRAINT `fk_votetype_classification` FOREIGN KEY (`classified_as`) REFERENCES `vote_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- /*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `templates`
--

DROP TABLE IF EXISTS `templates`;
-- /*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `templates` (
  `name` varchar(256) COLLATE utf8_bin NOT NULL,
  `lang` varchar(3) COLLATE utf8_bin NOT NULL,
  `num_instances` int(11) DEFAULT '0' COMMENT 'Numer of usages of the template on the corresponding Wikipedia',
  PRIMARY KEY (`name`,`lang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- /*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users_role`
--

DROP TABLE IF EXISTS `users_role`;
-- /*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `users_role` (
  `role` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `code` int(11) DEFAULT NULL,
  PRIMARY KEY (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- /*!40101 SET character_set_client = @saved_cs_client */;

--

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
-- /*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `users` (
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
-- /*!40101 SET character_set_client = @saved_cs_client */;

-- Table structure for table `vote`
--

DROP TABLE IF EXISTS `vote`;
-- /*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `vote` (
  `idvote` int(11) NOT NULL AUTO_INCREMENT,
  `annotation_id` int(11) NOT NULL,
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `vote` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `username` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`idvote`,`username`,`annotation_id`,`vote`),
  KEY `fk_vote_1_idx` (`annotation_id`),
  KEY `fk_vote_type` (`vote`),
  KEY `fk_vote_username` (`username`),
  CONSTRAINT `fk_annotation_id` FOREIGN KEY (`annotation_id`) REFERENCES `annotation` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_vote_type` FOREIGN KEY (`vote`) REFERENCES `vote_type` (`type`),
  CONSTRAINT `fk_vote_username` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- /*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `lock`
--

DROP TABLE IF EXISTS `lock`;
-- /*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `lock` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- /*!40101 SET character_set_client = @saved_cs_client */;


-- /*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

-- /*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
-- /*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
-- /*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
-- /*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
-- /*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
-- /*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
-- /*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-09-01
