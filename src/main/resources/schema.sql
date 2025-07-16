CREATE TABLE parkinglot(
	id INT NOT NULL AUTO_INCREMENT,
	address1 VARCHAR(255) NOT NULL,
	address2 VARCHAR(255),
	address3 VARCHAR(255),
	name VARCHAR(255),
	capacity int,
	hourlyRate int,
	updateDate DATE
	);