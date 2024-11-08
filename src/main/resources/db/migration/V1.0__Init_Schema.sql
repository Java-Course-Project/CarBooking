CREATE TABLE `user` (
    id VARCHAR(36) PRIMARY KEY,
    credential VARCHAR(258) NOT NULL UNIQUE,
    name VARCHAR(258) NOT NULL UNIQUE,
    dob DATETIME,
    gender VARCHAR(36),
    role VARCHAR(36),
    `password` VARCHAR(255) NOT NULL
);

CREATE TABLE vehicle (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    type VARCHAR(36) NOT NULL,
    status VARCHAR(36) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES `user`(id)
);

CREATE TABLE location (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) UNIQUE NOT NULL,
    point POINT,
    FOREIGN KEY (user_id) REFERENCES `user`(id)
);

CREATE TABLE price_unit (
    id VARCHAR(36) PRIMARY KEY,
    vehicle_type VARCHAR(36) NOT NULL UNIQUE,
    price DOUBLE NOT NULL
);

CREATE TABLE `transaction` (
    id VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL,
    driver_id VARCHAR(36) NOT NULL,
    start_point POINT NOT NULL,
    end_point POINT NOT NULL,
    fare DOUBLE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES `user`(id),
    FOREIGN KEY (driver_id) REFERENCES `user`(id)
);

CREATE TABLE review (
    id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL,
    rate INT NOT NULL CHECK (rate BETWEEN 1 AND 5),
    reason VARCHAR(258),
    FOREIGN KEY (transaction_id) REFERENCES `transaction`(id)
);

CREATE TABLE discount (
    id VARCHAR(36) PRIMARY KEY,
    fare DOUBLE NOT NULL,
    transaction_id VARCHAR(36),
    is_used BOOLEAN NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES `transaction`(id)
);
