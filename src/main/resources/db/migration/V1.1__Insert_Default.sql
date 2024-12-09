-- Insert into TransportationType table
USE car_booking;
INSERT INTO transportation_type (id, type)
VALUES (1, 'CAR');

INSERT INTO transportation_type (id, type)
VALUES (2, 'MOTORBIKE');

-- Insert into Fare table
INSERT INTO fare (transportation_type_id, holiday_rate, price, normal_hour_rate, rush_hour_rate, normal_day_rate)
VALUES ( 1, 1.5, 20000.0, 1.0, 1.2, 1.0);

INSERT INTO fare (transportation_type_id, holiday_rate, price, normal_hour_rate, rush_hour_rate, normal_day_rate)
VALUES ( 2, 1.5, 5000.0, 1.0, 1.2, 1.0);
