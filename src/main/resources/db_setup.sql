-- 1. Create the Database
CREATE DATABASE IF NOT EXISTS train_system;
USE train_system;

-- 2. Drop existing tables in reverse order of dependencies
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS route_stations;
DROP TABLE IF EXISTS routes;
DROP TABLE IF EXISTS trains;
DROP TABLE IF EXISTS stations;

-- 3. Create Stations Table
CREATE TABLE stations (
                          station_id INT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL
);

-- 4. Create Routes Table
CREATE TABLE routes (
                        route_id INT PRIMARY KEY AUTO_INCREMENT,
                        route_name VARCHAR(255) NOT NULL
);

-- 5. Create Trains Table
CREATE TABLE trains (
                        train_id VARCHAR(50) PRIMARY KEY,
                        route_id INT,
                        total_capacity INT NOT NULL,
                        current_delay_minutes INT DEFAULT 0,
                        FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE SET NULL
);

-- 6. Create Route-Station Mapping (The Pathfinding Table)
CREATE TABLE route_stations (
                                route_station_id INT PRIMARY KEY AUTO_INCREMENT,
                                route_id INT,
                                station_id INT,
                                stop_order INT NOT NULL,
                                FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE CASCADE,
                                FOREIGN KEY (station_id) REFERENCES stations(station_id) ON DELETE CASCADE
);

-- 7. Create Schedules Table (The Timing Table)
CREATE TABLE schedules (
                           schedule_id INT PRIMARY KEY AUTO_INCREMENT,
                           train_id VARCHAR(50),
                           route_id INT,
                           departure_time TIME NOT NULL,
                           arrival_time TIME NOT NULL,
                           FOREIGN KEY (train_id) REFERENCES trains(train_id) ON DELETE CASCADE,
                           FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE CASCADE
);

-- 8. Create Bookings Table (The Transaction Table)
CREATE TABLE bookings (
                          booking_id INT PRIMARY KEY AUTO_INCREMENT,
                          customer_email VARCHAR(255) NOT NULL,
                          start_station_id INT,
                          end_station_id INT,
                          num_seats INT DEFAULT 1,
                          train_id VARCHAR(50),
                          FOREIGN KEY (train_id) REFERENCES trains(train_id) ON DELETE CASCADE
);

-- ==========================================
-- SEED DATA (Sample Records for Demo)
-- ==========================================

-- Insert Stations
INSERT INTO stations (name) VALUES ('Central Hub'), ('North Valley'), ('South Port'), ('East Gate'), ('West End'), ('Green Park'), ('Airport Terminal');

-- Insert Routes
INSERT INTO routes (route_name) VALUES ('The Mainline'), ('The Circle Line'), ('Airport Express');

-- Map Stations to Routes (Route 1: 2->1->3 | Route 2: 1->5->6)
INSERT INTO route_stations (route_id, station_id, stop_order) VALUES
                                                                  (1, 2, 1), (1, 1, 2), (1, 3, 3), -- Mainline
                                                                  (2, 1, 1), (2, 5, 2), (2, 6, 3); -- Circle Line (Hubs at Station 1)

-- Insert Trains
INSERT INTO trains (train_id, route_id, total_capacity) VALUES
                                                            ('TR-MAIN-01', 1, 50),
                                                            ('TR-CIRC-02', 2, 100),
                                                            ('TR-AIR-99', 3, 80);

-- Insert Schedules
INSERT INTO schedules (train_id, route_id, departure_time, arrival_time) VALUES
                                                                             ('TR-MAIN-01', 1, '08:00:00', '09:30:00'),
                                                                             ('TR-CIRC-02', 2, '10:00:00', '11:30:00'),
                                                                             ('TR-AIR-99', 3, '07:00:00', '07:30:00');