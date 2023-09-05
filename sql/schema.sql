

-- Dumping database structure for gestion_biblio
CREATE DATABASE IF NOT EXISTS `gestion_biblio-v1` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `gestion_biblio-v1`;

-- Dumping structure for table gestion_biblio.authors
CREATE TABLE IF NOT EXISTS `authors` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

-- Dumping data for table gestion_biblio.authors: ~1 rows (approximately)
REPLACE INTO `authors` (`id`, `name`) VALUES
	(1, 'Mounir El Bakkali');

-- Dumping structure for table gestion_biblio.books
CREATE TABLE IF NOT EXISTS `books` (
  `id` int NOT NULL AUTO_INCREMENT,
  `isbn` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `title` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `author_id` int NOT NULL,
  `copies` bigint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `isbn` (`isbn`),
  KEY `FK_books_authors` (`author_id`),
  CONSTRAINT `FK_books_authors` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

-- Dumping data for table gestion_biblio.books: ~2 rows (approximately)
REPLACE INTO `books` (`id`, `isbn`, `title`, `author_id`, `copies`) VALUES
	(1, '24352-56783', 'updated', 1, 3),
	(2, '24352-56784', 'X-T', 1, 2);

-- Dumping structure for table gestion_biblio.borrower
CREATE TABLE IF NOT EXISTS `borrower` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

-- Dumping data for table gestion_biblio.borrower: ~1 rows (approximately)
REPLACE INTO `borrower` (`id`, `nom`) VALUES
	(1, 'mostaph bousil');

-- Dumping structure for table gestion_biblio.borrowing
CREATE TABLE IF NOT EXISTS `borrowing` (
  `id` int NOT NULL AUTO_INCREMENT,
  `borrower_id` int NOT NULL,
  `book_ref` int NOT NULL,
  `date_of_borrowing` date NOT NULL,
  `date_of_returning` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_borrowing_borrower` (`borrower_id`),
  KEY `FK_borrowing_copies` (`book_ref`),
  CONSTRAINT `FK_borrowing_borrower` FOREIGN KEY (`borrower_id`) REFERENCES `borrower` (`id`),
  CONSTRAINT `FK_borrowing_copies` FOREIGN KEY (`book_ref`) REFERENCES `copies` (`ref`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

-- Dumping data for table gestion_biblio.borrowing: ~0 rows (approximately)

-- Dumping structure for table gestion_biblio.copies
CREATE TABLE IF NOT EXISTS `copies` (
  `ref` int NOT NULL AUTO_INCREMENT,
  `isbn` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `status` enum('AVAILABLE','BORROWED') COLLATE utf8_bin NOT NULL DEFAULT 'AVAILABLE',
  PRIMARY KEY (`ref`),
  KEY `FK1s` (`isbn`),
  CONSTRAINT `FK1s` FOREIGN KEY (`isbn`) REFERENCES `books` (`isbn`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

-- Dumping data for table gestion_biblio.copies: ~5 rows (approximately)
REPLACE INTO `copies` (`ref`, `isbn`, `status`) VALUES
	(1, '24352-56784', 'AVAILABLE'),
	(2, '24352-56784', 'AVAILABLE'),
	(3, '24352-56783', 'AVAILABLE'),
	(4, '24352-56783', 'AVAILABLE'),
	(5, '24352-56783', 'AVAILABLE');

-- Dumping structure for procedure gestion_biblio.findAllBooks
DELIMITER //
CREATE PROCEDURE `findAllBooks`()
    READS SQL DATA
BEGIN
SELECT books.*,COUNT(copies.ref) AS copies FROM books
                    left JOIN copies ON copies.isbn = books.isbn
                    GROUP BY books.isbn;
END//
DELIMITER ;

-- Dumping structure for procedure gestion_biblio.findBookByIsbn
DELIMITER //
CREATE PROCEDURE `findBookByIsbn`(
	IN `book_isbn` VARCHAR(50)
)
BEGIN
 SELECT books.*,COUNT(copies.ref) AS copies FROM books
                    left JOIN copies ON copies.isbn = books.isbn
                    WHERE books.isbn = book_isbn
                    GROUP BY books.isbn;
END//
DELIMITER ;

-- Dumping structure for procedure gestion_biblio.generateReport
DELIMITER //
CREATE PROCEDURE `generateReport`()
    READS SQL DATA
BEGIN
 SELECT
                    books.*,
                    COUNT(copies.ref) AS copies,
                    SUM(CASE WHEN copies.`status` = 'AVAILABLE' THEN 1 ELSE 0 END) AS available,
                    SUM(CASE WHEN copies.`status` = 'BORROWED' THEN 1 ELSE 0 END) AS borrowed,
                    SUM(CASE WHEN borrowing.date_of_returning < NOW() THEN 1 ELSE 0 END) AS lost
                FROM
                    books
                LEFT JOIN
                    copies ON copies.isbn = books.isbn
                LEFT JOIN
                    borrowing ON borrowing.book_ref = books.isbn
                GROUP BY
                    books.isbn;
END//
DELIMITER ;

-- Dumping structure for procedure gestion_biblio.saveBook
DELIMITER //
CREATE PROCEDURE `saveBook`(
	IN `book_isbn` VARCHAR(50),
	IN `book_title` VARCHAR(50),
	IN `author_id` INT,
	IN `book_copies` INT
)
BEGIN
	INSERT INTO books (isbn, title, author_id,copies) VALUES (book_isbn, book_title, author_id,book_copies);
END//
DELIMITER ;

-- Dumping structure for procedure gestion_biblio.updateBook
DELIMITER //
CREATE PROCEDURE `updateBook`(
	IN `book_title` VARCHAR(50),
	IN `book_author` INT,
	IN `book_copies` BIGINT,
	IN `book_isbn` VARCHAR(50)
)
    MODIFIES SQL DATA
BEGIN
	UPDATE books SET title = book_title, author_id = book_author , copies = book_copies WHERE isbn = book_isbn;
END//
DELIMITER ;

-- Dumping structure for trigger gestion_biblio.createBookCopies
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='';
DELIMITER //
CREATE TRIGGER `createBookCopies` AFTER INSERT ON `books` FOR EACH ROW BEGIN
	DECLARE numOfCopies INT;
    SET numOfCopies = NEW.copies;
    
    WHILE numOfCopies > 0 DO
        INSERT INTO copies (isbn) VALUES (NEW.isbn);
        SET numOfCopies = numOfCopies - 1;
    END WHILE;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Dumping structure for trigger gestion_biblio.updateBookCopies
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='';
DELIMITER //
CREATE TRIGGER `updateBookCopies` AFTER UPDATE ON `books` FOR EACH ROW BEGIN
	DECLARE numOfCopies  BIGINT ;
    	SET numOfCopies = ABS(NEW.copies - OLD.copies);
		   IF NEW.copies > OLD.copies then 
			 	WHILE numOfCopies > 0 DO 
					INSERT INTO copies (isbn) VALUES (NEW.isbn);
				 	SET numOfCopies = numOfCopies -1 ; 
				END while;
			ELSE 
				DELETE FROM copies WHERE isbn = OLD.isbn AND copies.status='AVAILABLE' LIMIT numOfCopies;
			END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;
