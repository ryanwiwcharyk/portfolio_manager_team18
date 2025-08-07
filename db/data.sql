USE portfolio_manager;

-- DISABLE SAFE UPDATES TO ALLOW DELETION
SET SQL_SAFE_UPDATES = 0;

-- CLEAR EXISTING DATA
DELETE FROM transactions;
DELETE FROM stocks;
DELETE FROM portfolios;
DROP TABLE stock_data;

-- RE-ENABLE SAFE UPDATES
SET SQL_SAFE_UPDATES = 1;

-- ADD PORTFOLIOS
INSERT INTO portfolios(portfolioID, cash, portfolio_name, description) VALUES
(1, 5000.00, "Ryan's Portfolio", "TFSA"),
(2, 3000.00, "Hannah's Portfolio", "FHSA"),
(3, 1000.00, "Derin's Portfolio", "RRSP");

INSERT INTO stocks (ticker_symbol, portfolioid, qty, avg_price) VALUES
-- Ryan Portfolio
('AAPL', 1, 50, 150.25),
('MSFT', 1, 30, 280.50),
('GOOGL', 1, 25, 120.75),
('AMZN', 1, 40, 85.30),

-- Hannah Portfolio
('JNJ', 2, 100, 165.80),
('PG', 2, 75, 145.20),
('KO', 2, 200, 55.40),
('WMT', 2, 60, 160.90),

-- Derin Portfolio
('NVDA', 3, 20, 450.75),
('TSLA', 3, 15, 220.30),
('META', 3, 35, 180.60),
('NFLX', 3, 45, 95.20),
('AMD', 3, 80, 110.45);

-- TRANSACTIONS
INSERT INTO transactions (ticker_symbol, portfolioid, price, qty, is_sell, transaction_time) VALUES
-- Ryan Portfolio
('AAPL', 1, 145.00, 25, FALSE, '2024-01-15 10:30:00'),
('AAPL', 1, 155.50, 25, FALSE, '2024-02-20 14:15:00'),
('MSFT', 1, 270.00, 15, FALSE, '2024-01-10 09:45:00'),
('MSFT', 1, 285.00, 15, FALSE, '2024-03-05 11:20:00'),
('GOOGL', 1, 115.00, 25, FALSE, '2024-02-01 13:30:00'),
('AMZN', 1, 80.00, 20, FALSE, '2024-01-25 15:45:00'),
('AMZN', 1, 90.60, 20, FALSE, '2024-03-10 10:15:00'),

-- Hannah Portfolio
('JNJ', 2, 160.00, 50, FALSE, '2024-01-05 08:30:00'),
('JNJ', 2, 170.60, 50, FALSE, '2024-02-15 12:45:00'),
('PG', 2, 140.00, 40, FALSE, '2024-01-20 14:20:00'),
('PG', 2, 150.40, 35, FALSE, '2024-03-01 09:30:00'),
('KO', 2, 50.00, 100, FALSE, '2024-02-10 11:15:00'),
('KO', 2, 60.80, 100, FALSE, '2024-03-15 16:00:00'),
('WMT', 2, 155.00, 60, FALSE, '2024-01-30 13:45:00'),

-- Derin Portfolio
('NVDA', 3, 400.00, 10, FALSE, '2024-01-12 10:00:00'),
('NVDA', 3, 500.50, 10, FALSE, '2024-02-25 14:30:00'),
('TSLA', 3, 200.00, 15, FALSE, '2024-01-18 15:20:00'),
('META', 3, 170.00, 20, FALSE, '2024-02-05 11:45:00'),
('META', 3, 190.20, 15, FALSE, '2024-03-08 12:30:00'),
('NFLX', 3, 90.00, 25, FALSE, '2024-01-22 09:15:00'),
('NFLX', 3, 100.40, 20, FALSE, '2024-03-12 14:45:00'),
('AMD', 3, 100.00, 40, FALSE, '2024-02-08 13:20:00'),
('AMD', 3, 120.90, 40, FALSE, '2024-03-18 10:30:00'),

-- Selling stock
('AAPL', 1, 160.00, 10, TRUE, '2024-03-20 10:30:00'),
('MSFT', 1, 290.00, 5, TRUE, '2024-03-22 14:15:00'),
('GOOGL', 1, 125.00, 10, TRUE, '2024-03-25 11:45:00'),
('NVDA', 3, 520.00, 5, TRUE, '2024-03-18 15:30:00'),
('TSLA', 3, 230.00, 5, TRUE, '2024-03-21 09:20:00');

INSERT INTO stock_data (ticker_symbol, price, change_percent)
VALUES ('IBM', 250.6700, -0.5199);

INSERT INTO stock_data (ticker_symbol, price, change_percent)
VALUES ('AAPL', 202.9200, -0.2115);

INSERT INTO stock_data (ticker_symbol, price, change_percent)
VALUES ('MSFT', 527.7500, -1.4730);

INSERT INTO stock_data (ticker_symbol, price, change_percent)
VALUES ('GOOGL', 194.6700, -0.1897);

INSERT INTO stock_data (ticker_symbol, price, change_percent)
VALUES ('NVDA', 178.2600, -0.9667);

INSERT INTO stock_data (ticker_symbol, price, change_percent)
VALUES ('TSLA', 308.7200, -0.1746);

INSERT INTO stock_data (ticker_symbol, price, change_percent)
VALUES ('META', 763.4600, -1.6629);

INSERT INTO stock_data (ticker_symbol, price, change_percent)
VALUES ('NFLX', 1147.8700, -1.9744);

INSERT INTO stock_data (ticker_symbol, price, change_percent)
VALUES ('AMD', 174.3100, -1.3972);

INSERT INTO stock_data (ticker_symbol, price, change_percent)
VALUES ('AMZN', 213.7500, 0.9922);

INSERT INTO stock_data (ticker_symbol, price, change_percent)
VALUES ('INTC', 20.1900, 3.5385);


