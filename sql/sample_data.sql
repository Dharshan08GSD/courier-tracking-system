-- ============================================================
-- Courier and Parcel Tracking Management System — Sample Data
-- ============================================================
-- Charge formula:
--   delivery_charge = weight * base_rate * distance_multiplier
--   Base rates   — Standard: 50, Express: 80, Overnight: 120
--   Multipliers  — Local: 1.0, Regional: 1.5, National: 2.0, International: 3.5
-- ============================================================

PRAGMA foreign_keys = ON;

-- ============================================================
-- 1. customers (8 records)
-- ============================================================
INSERT INTO customers (name, contact_number, address) VALUES
('Aarav Sharma',    '9876543210', '12, MG Road, Bengaluru, Karnataka 560001'),
('Priya Verma',     '9823456789', '45, Connaught Place, New Delhi 110001'),
('Rohit Mehta',     '9812345678', '78, FC Road, Pune, Maharashtra 411004'),
('Sneha Iyer',      '9845671234', '23, Anna Nagar, Chennai, Tamil Nadu 600040'),
('Vikram Singh',    '9801234567', '56, Civil Lines, Jaipur, Rajasthan 302006'),
('Anjali Nair',     '9867890123', '9, Marine Drive, Kochi, Kerala 682031'),
('Deepak Gupta',    '9834567890', '101, Hazratganj, Lucknow, Uttar Pradesh 226001'),
('Kavitha Reddy',   '9856781234', '34, Banjara Hills, Hyderabad, Telangana 500034');

-- ============================================================
-- 2. delivery_personnel (6 records)
-- ============================================================
INSERT INTO delivery_personnel (name, contact_number, assigned_area, status) VALUES
('Raju Patil',      '9900112233', 'Bengaluru South',  'Available'),
('Suresh Kumar',    '9900223344', 'New Delhi Central', 'On Delivery'),
('Manoj Tiwari',    '9900334455', 'Pune West',         'Available'),
('Anil Yadav',      '9900445566', 'Chennai Central',   'Off Duty'),
('Farhan Sheikh',   '9900556677', 'Hyderabad East',    'Available'),
('Kiran Das',       '9900667788', 'Kochi South',       'On Delivery');

-- ============================================================
-- 3. parcels (10 records)
-- ============================================================
-- Charge calculations shown as comments:
--
-- P1: 0.5 * 50  * 1.0 =   25.00  (Standard, Local)
-- P2: 2.0 * 80  * 1.5 =  240.00  (Express, Regional)
-- P3: 5.0 * 50  * 2.0 =  500.00  (Standard, National)
-- P4: 1.0 * 120 * 1.0 =  120.00  (Overnight, Local)
-- P5: 3.5 * 80  * 3.5 =  980.00  (Express, International)
-- P6: 0.3 * 120 * 1.5 =   54.00  (Overnight, Regional)
-- P7: 8.0 * 50  * 2.0 =  800.00  (Standard, National)
-- P8: 1.5 * 80  * 1.0 =  120.00  (Express, Local)
-- P9: 4.0 * 120 * 3.5 = 1680.00  (Overnight, International)
-- P10: 2.5 * 50  * 1.5 =  187.50  (Standard, Regional)

INSERT INTO parcels (tracking_number, sender_id, receiver_id, parcel_type, weight, booking_date, destination, distance_category, service_type, delivery_charge, status) VALUES
('TRK1001', 1, 4, 'Document',        0.5, '2024-11-10', 'Chennai, Tamil Nadu',           'Local',         'Standard',  25.00,   'Delivered'),
('TRK1002', 2, 5, 'Small Package',   2.0, '2024-12-01', 'Jaipur, Rajasthan',             'Regional',      'Express',   240.00,  'Delivered'),
('TRK1003', 3, 7, 'Large Package',   5.0, '2025-01-05', 'Lucknow, Uttar Pradesh',        'National',      'Standard',  500.00,  'In Transit'),
('TRK1004', 4, 1, 'Document',        1.0, '2025-01-15', 'Bengaluru, Karnataka',           'Local',         'Overnight', 120.00,  'Out for Delivery'),
('TRK1005', 5, 6, 'Fragile',         3.5, '2025-02-10', 'London, United Kingdom',         'International', 'Express',   980.00,  'Booked'),
('TRK1006', 6, 2, 'Document',        0.3, '2025-02-20', 'New Delhi',                      'Regional',      'Overnight', 54.00,   'In Transit'),
('TRK1007', 7, 8, 'Large Package',   8.0, '2025-03-01', 'Hyderabad, Telangana',           'National',      'Standard',  800.00,  'Returned'),
('TRK1008', 8, 3, 'Small Package',   1.5, '2025-03-12', 'Pune, Maharashtra',              'Local',         'Express',   120.00,  'Out for Delivery'),
('TRK1009', 1, 6, 'Medium Package',  4.0, '2025-04-01', 'Dubai, UAE',                     'International', 'Overnight', 1680.00, 'Booked'),
('TRK1010', 3, 5, 'Medium Package',  2.5, '2025-04-10', 'Jaipur, Rajasthan',              'Regional',      'Standard',  187.50,  'In Transit');

-- ============================================================
-- 4. shipment_tracking (15 records)
--    Shows progressive history for several parcels
-- ============================================================
-- Parcel 1 (Delivered — full lifecycle)
INSERT INTO shipment_tracking (parcel_id, status, location, update_time) VALUES
(1, 'Booked',           'Bengaluru Hub',   '2024-11-10 09:00:00'),
(1, 'In Transit',       'Bengaluru Hub',   '2024-11-11 06:30:00'),
(1, 'Out for Delivery', 'Chennai Hub',     '2024-11-12 08:00:00'),
(1, 'Delivered',        'Chennai, Anna Nagar', '2024-11-12 14:30:00');

-- Parcel 2 (Delivered)
INSERT INTO shipment_tracking (parcel_id, status, location, update_time) VALUES
(2, 'Booked',           'New Delhi Hub',   '2024-12-01 10:00:00'),
(2, 'In Transit',       'Gurgaon Sorting', '2024-12-02 05:00:00'),
(2, 'Delivered',        'Jaipur, Civil Lines', '2024-12-03 11:00:00');

-- Parcel 3 (In Transit)
INSERT INTO shipment_tracking (parcel_id, status, location, update_time) VALUES
(3, 'Booked',           'Pune Hub',        '2025-01-05 12:00:00'),
(3, 'In Transit',       'Nagpur Transit',  '2025-01-07 03:00:00');

-- Parcel 4 (Out for Delivery)
INSERT INTO shipment_tracking (parcel_id, status, location, update_time) VALUES
(4, 'Booked',           'Chennai Hub',     '2025-01-15 08:00:00'),
(4, 'In Transit',       'Bengaluru Sorting', '2025-01-16 04:00:00'),
(4, 'Out for Delivery', 'Bengaluru South', '2025-01-16 09:30:00');

-- Parcel 7 (Returned)
INSERT INTO shipment_tracking (parcel_id, status, location, update_time) VALUES
(7, 'Booked',           'Lucknow Hub',     '2025-03-01 10:00:00'),
(7, 'In Transit',       'Nagpur Transit',  '2025-03-03 06:00:00'),
(7, 'Returned',         'Lucknow Hub',     '2025-03-06 15:00:00');

-- ============================================================
-- 5. delivery_assignments (8 records)
-- ============================================================
INSERT INTO delivery_assignments (parcel_id, personnel_id, assigned_date, status) VALUES
(1, 1, '2024-11-12', 'Completed'),
(2, 2, '2024-12-03', 'Completed'),
(3, 3, '2025-01-08', 'In Progress'),
(4, 1, '2025-01-16', 'In Progress'),
(7, 5, '2025-03-04', 'Failed'),
(8, 3, '2025-03-14', 'Assigned'),
(6, 6, '2025-02-22', 'In Progress'),
(10, 5, '2025-04-12', 'Assigned');

-- ============================================================
-- 6. delivery_attempts (6 records — mix of Successful and Failed)
-- ============================================================
INSERT INTO delivery_attempts (parcel_id, personnel_id, attempt_date, delivery_status, failure_reason) VALUES
(1, 1, '2024-11-12', 'Successful', NULL),
(2, 2, '2024-12-03', 'Successful', NULL),
(7, 5, '2025-03-04', 'Failed',     'Recipient not available at address'),
(7, 5, '2025-03-05', 'Failed',     'Incorrect address — building not found'),
(4, 1, '2025-01-16', 'Failed',     'Customer refused delivery — damaged packaging'),
(8, 3, '2025-03-14', 'Successful', NULL);
