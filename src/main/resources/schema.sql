DROP TABLE IF EXISTS parkinglot;
DROP TABLE IF EXISTS rates;
DROP TABLE IF EXISTS `range`;

CREATE TABLE parkinglot(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	address1 VARCHAR(255) NOT NULL,
	address2 VARCHAR(255),
	address3 VARCHAR(255),
	name VARCHAR(255),
	capacity int,
	hourlyRate int,
	updateDate DATE
);

CREATE TABLE rates(
	ratesId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	parkinglotId INT,
	rangeId INT,
	amount INT,
	time INT,
	maxRateTimely INT,
	maxRate24h INT,
	maxRateDaily INT
);

CREATE TABLE `range`(
	rangeId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	startTime INT,
	endTime INT,
	monday BOOLEAN,
	tuesday BOOLEAN,
	wednesday BOOLEAN,
	thursday BOOLEAN,
	friday BOOLEAN,
	saturday BOOLEAN,
	sunday BOOLEAN,
	holiday BOOLEAN
);