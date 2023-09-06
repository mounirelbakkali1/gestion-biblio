
CREATE DATABASE IF NOT EXISTS `gestion_biblio` ;
USE `gestion_biblio`;

CREATE TABLE IF NOT EXISTS `authors` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

REPLACE INTO `authors` (`id`, `name`) VALUES
	(1, 'Mounir El Bakkali'),
	(2, 'lapha linux'),
	(3, 'ajax');

CREATE TABLE IF NOT EXISTS `books` (
  `id` int NOT NULL AUTO_INCREMENT,
  `isbn` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `title` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `author_id` int NOT NULL,
  `copies` bigint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique isbn` (`isbn`) USING BTREE,
  UNIQUE KEY `unique title` (`title`) USING BTREE,
  KEY `FK_books_authors` (`author_id`),
  CONSTRAINT `FK_books_authors` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

REPLACE INTO `books` (`id`, `isbn`, `title`, `author_id`, `copies`) VALUES
	(1, '24352-56783', 'updated', 1, 3),
	(2, '24352-56784', 'X-T', 1, 2);

CREATE TABLE IF NOT EXISTS `borrower` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

REPLACE INTO `borrower` (`id`, `nom`) VALUES
	(1, 'mostaph bousil');

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
  CONSTRAINT `FK_borrowing_copies` FOREIGN KEY (`book_ref`) REFERENCES `copies` (`ref`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;


CREATE TABLE IF NOT EXISTS `copies` (
  `ref` int NOT NULL AUTO_INCREMENT,
  `isbn` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `status` enum('AVAILABLE','BORROWED') COLLATE utf8_bin NOT NULL DEFAULT 'AVAILABLE',
  PRIMARY KEY (`ref`),
  KEY `FK1s` (`isbn`),
  CONSTRAINT `FK1s` FOREIGN KEY (`isbn`) REFERENCES `books` (`isbn`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

REPLACE INTO `copies` (`ref`, `isbn`, `status`) VALUES
	(1, '24352-56784', 'AVAILABLE'),
	(2, '24352-56784', 'AVAILABLE'),
	(3, '24352-56783', 'AVAILABLE'),
	(4, '24352-56783', 'AVAILABLE'),
	(5, '24352-56783', 'AVAILABLE');


DELIMITER //
CREATE PROCEDURE `deleteBook`(
	IN `book_isbn` VARCHAR(50)
)
BEGIN
	DELETE FROM books WHERE isbn = book_isbn;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `findAllBooks`()
    READS SQL DATA
BEGIN
SELECT books.*,COUNT(copies.ref) AS copies , authors.name AS author_name FROM books
                    left JOIN copies ON copies.isbn = books.isbn
                    LEFT JOIN authors ON authors.id = books.author_id
                    GROUP BY books.isbn;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `findBookByIsbn`(
	IN `book_isbn` VARCHAR(50)
)
BEGIN
 SELECT books.*,COUNT(copies.ref) AS copies , authors.name AS author_name FROM books
                    left JOIN copies ON copies.isbn = books.isbn
                    LEFT JOIN authors ON books.author_id = authors.id
                    WHERE books.isbn = book_isbn
                    GROUP BY books.isbn;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `findBooksByTitleOrAuthor`(
	IN `criteria` VARCHAR(50)
)
BEGIN
	SELECT books.* , authors.name AS author_name FROM books
                    inner JOIN authors ON authors.id = books.author_id
                        WHERE LOWER(authors.name) LIKE CONCAT('%', criteria, '%') OR LOWER(authors.name) LIKE CONCAT('%', criteria, '%');

                     
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `generateReport`()
    READS SQL DATA
BEGIN
 SELECT
                    books.*,
                    authors.name AS author_name,
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
               LEFT JOIN authors ON authors.id = books.author_id
                GROUP BY
                    books.isbn;
END//
DELIMITER ;

DELIMITER //
CREATE FUNCTION `isUniqueIsbn`(
	`book_isbn` VARCHAR(50)
) RETURNS int
BEGIN	
	DECLARE rst INT; 
	SET rst = 0 ;
	if NOT EXISTS (SELECT 1 FROM books WHERE isbn = book_isbn) then 
		SET rst =1 ;
	END if;
	RETURN rst;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `saveBook`(
	IN `book_isbn` VARCHAR(50),
	IN `book_title` VARCHAR(50),
	IN `author` VARCHAR(50),
	IN `book_copies` INT
)
BEGIN
		DECLARE author_id_exists INT;
	  SELECT id INTO author_id_exists FROM authors WHERE authors.name = author;
	  IF author_id_exists IS NULL THEN
	    INSERT INTO authors (`name`) VALUES (author);
	    SET author_id_exists = LAST_INSERT_ID();
	  END IF;
		INSERT INTO books (isbn, title, author_id,copies) VALUES (book_isbn, book_title, author_id_exists,book_copies);
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `updateBook`(
	IN `book_title` VARCHAR(50),
	IN `book_author` VARCHAR(50),
	IN `book_copies` BIGINT,
	IN `book_isbn` VARCHAR(50)
)
    MODIFIES SQL DATA
BEGIN
	DECLARE author_id_exists INT;
	  SELECT id INTO author_id_exists FROM authors WHERE authors.name = book_author;
	  IF author_id_exists IS NULL THEN
	    INSERT INTO authors (`name`) VALUES (book_author);
	    SET author_id_exists = LAST_INSERT_ID();
	  END IF;
	UPDATE books SET title = book_title, author_id = author_id_exists , copies = book_copies WHERE isbn = book_isbn;
END//
DELIMITER ;

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
