-- --------------------------------------------------------
-- Host:                         localhost
-- Server version:               10.2.3-MariaDB-log - mariadb.org binary distribution
-- Server OS:                    Win32
-- HeidiSQL Version:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for lockmate
CREATE DATABASE IF NOT EXISTS `lockmate` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `lockmate`;

-- Dumping structure for table lockmate.access_log
CREATE TABLE IF NOT EXISTS `access_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `qr_key_id` int(11) NOT NULL,
  `scan_datetime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `qr_key_id` (`qr_key_id`),
  CONSTRAINT `access_log_ibfk_1` FOREIGN KEY (`qr_key_id`) REFERENCES `qr_keys` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table lockmate.qr_keys
CREATE TABLE IF NOT EXISTS `qr_keys` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `studentName` varchar(100) NOT NULL,
  `matricNo` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
