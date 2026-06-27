CREATE DATABASE  IF NOT EXISTS `carls` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `carls`;
-- MySQL dump 10.13  Distrib 8.0.43, for macos15 (arm64)
--
-- Host: localhost    Database: carls
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */

-- 
-- Table structure for table `Person`
-- 
DROP TABLE IF EXISTS `Person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Person` (
  `personID` varchar(36) NOT NULL,
  `first_name` varchar(45) NOT NULL,
  `middle_initial` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) NOT NULL,
  `suffix` varchar(45) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  PRIMARY KEY (`personID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- 
-- Table structure for table `Employee`
-- 
DROP TABLE IF EXISTS `Employee`;
-- /*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Employee` (
  `personID` varchar(36) NOT NULL,
  `password` varchar(97) NOT NULL,
  PRIMARY KEY (`personID`),
  CONSTRAINT `employee_person_fk` FOREIGN KEY (`personID`) REFERENCES `Person` (`personID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- 
-- Table structure for table `Customer`
-- 
DROP TABLE IF EXISTS `Customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Customer` (
  `personID` varchar(36) NOT NULL,
  `is_active` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`personID`),
  CONSTRAINT `customer_person_fk` FOREIGN KEY (`personID`) REFERENCES `Person` (`personID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- 
-- Table structure for table `Vehicles`
-- 
DROP TABLE IF EXISTS `Vehicles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Vehicles` (
  `vehicleID` int NOT NULL AUTO_INCREMENT,
  `brand` varchar(45) NOT NULL,
  `model` varchar(45) NOT NULL,
  `type` varchar(45) NOT NULL,
  `plate_number` varchar(7) NOT NULL,
  `daily_rate` decimal(10,2) NOT NULL,
  `status` enum('Available','Maintenance','Cleaning','Out of Service','Rented','Reserved') NOT NULL DEFAULT 'Available',
  PRIMARY KEY (`vehicleID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- 
-- Table structure for table `Rentals`
-- 
DROP TABLE IF EXISTS `Rentals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Rentals` (
  `rentalID` int NOT NULL AUTO_INCREMENT,
  `personID` varchar(36) DEFAULT NULL,
  `vehicleID` int NOT NULL,
  `rental_date` date NOT NULL,
  `planned_return_date` date NOT NULL,
  `return_date` date DEFAULT NULL,
  `total_cost` decimal(10,2) NOT NULL,
  PRIMARY KEY (`rentalID`),
  KEY `rental-customer_idx` (`personID`),
  KEY `rental-vehicle_idx` (`vehicleID`),
  CONSTRAINT `rental-customer` FOREIGN KEY (`personID`) REFERENCES `Customer` (`personID`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `rental-vehicle` FOREIGN KEY (`vehicleID`) REFERENCES `Vehicles` (`vehicleID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;