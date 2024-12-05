CREATE TABLE `customer`(id bigint PRIMARY KEY AUTO_INCREMENT,
                        `username` varchar(32) NOT NULL,
                        `email` varchar(128) NOT NULL UNIQUE,
                        `password` varchar(1024) NOT NULL,
                        `citizen_identification_number` varchar(32) NOT NULL UNIQUE,
                        `dob` datetime NOT NULL,
                        `gender` ENUM ('MALE', 'FEMALE', 'OTHERS') NOT NULL,
                        `customer_status` ENUM('NOT_BOOKED', 'BOOKED', 'DRIVER_ASSIGNED', 'ON_THE_WAY') NOT NULL);


CREATE TABLE `admin`(id bigint PRIMARY KEY AUTO_INCREMENT,
                     `username` varchar(32) NOT NULL,
                     `email` varchar(128) NOT NULL UNIQUE,
                     `password` varchar(1024) NOT NULL,
                     `citizen_identification_number` varchar(32) NOT NULL UNIQUE,
                     `dob` datetime NOT NULL,
                     `gender` ENUM ('MALE', 'FEMALE', 'OTHERS') NOT NULL,
                     `company_number` varchar(32) NOT NULL UNIQUE);


CREATE TABLE `driver`(id bigint PRIMARY KEY AUTO_INCREMENT,
                      `username` varchar(32) NOT NULL,
                      `email` varchar(128) NOT NULL UNIQUE,
                      `password` varchar(1024) NOT NULL,
                      `citizen_identification_number` varchar(32) NOT NULL UNIQUE,
                      `dob` datetime NOT NULL,
                      `gender` ENUM ('MALE', 'FEMALE', 'OTHERS') NOT NULL,
                      `driver_license` varchar(128) NOT NULL UNIQUE,
                      `transportation_type_id` int NOT NULL,
                      last_updated timestamp NOT NULL,
                      location POINT,
                      `driver_status` ENUM('NOT_BOOKED', 'ASSIGNED', 'WAIT_FOR_CONFIRMATION', 'ON_THE_WAY', 'OFFLINE'));


CREATE TABLE transportation_type(id int PRIMARY KEY AUTO_INCREMENT,
                                 `type` varchar(128) NOT NULL);


CREATE TABLE review(id bigint PRIMARY KEY AUTO_INCREMENT,
                    ride_transaction_id bigint NOT NULL,
                    rate int CHECK (rate >= 1
                        AND rate <= 10) NOT NULL,
                    `comment` VARCHAR(128));


CREATE TABLE fare(transportation_type_id int NOT NULL PRIMARY KEY,
                  price DOUBLE NOT NULL,
                  rush_hour_rate DOUBLE NOT NULL,
                  normal_hour_rate DOUBLE NOT NULL,
                  holiday_rate DOUBLE NOT NULL,
                  normal_day_rate DOUBLE NOT NULL);


CREATE TABLE `ride_transaction`(id bigint PRIMARY KEY AUTO_INCREMENT,
                                `start_location` POINT NOT NULL,
                                `destination_location` POINT NOT NULL,
                                price DOUBLE NOT NULL,
                                customer_id bigint NOT NULL,
                                driver_id bigint NOT NULL,
                                `start_time` timestamp NOT NULL,
                                `end_time` timestamp NOT NULL,
                                `ride_transaction_status` ENUM('ASSIGNED', 'ON_THE_WAY', 'CANCELLED', 'FINISHED') NOT NULL);


ALTER TABLE `driver` ADD CONSTRAINT transportation_type_driver_pk
    FOREIGN KEY (transportation_type_id) REFERENCES transportation_type(id);


ALTER TABLE `fare` ADD CONSTRAINT transportation_type_fare_pk
    FOREIGN KEY (transportation_type_id) REFERENCES transportation_type(id);


ALTER TABLE `ride_transaction` ADD CONSTRAINT customer_ride_transaction_pk
    FOREIGN KEY (customer_id) REFERENCES customer(id);


ALTER TABLE `ride_transaction` ADD CONSTRAINT driver_ride_transaction_pk
    FOREIGN KEY (driver_id) REFERENCES driver(id);


ALTER TABLE `review` ADD CONSTRAINT ride_transaction_review_pk
    FOREIGN KEY (ride_transaction_id) REFERENCES ride_transaction(id);