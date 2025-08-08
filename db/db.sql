CREATE DATABASE IF NOT EXISTS portfolio_manager;
USE portfolio_manager;

CREATE TABLE IF NOT EXISTS portfolios (
    portfolioid INT AUTO_INCREMENT PRIMARY KEY,
    portfolio_name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    cash DECIMAL(15,2) NOT NULL DEFAULT 1000.0
);

CREATE TABLE IF NOT EXISTS stocks (
    ticker_symbol VARCHAR(10) NOT NULL,
    portfolioid INT NOT NULL,
    qty INT NOT NULL,
    avg_price DECIMAL(15,2) NOT NULL,
    current_price DECIMAL(15,2) NOT NULL,
    change_percent DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (portfolioid, ticker_symbol),
    FOREIGN KEY (portfolioid) REFERENCES portfolios(portfolioid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions (
    transactionid INT AUTO_INCREMENT PRIMARY KEY,
    ticker_symbol VARCHAR(10) NOT NULL,
    portfolioid INT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    qty INT NOT NULL,
    is_sell BOOLEAN NOT NULL,
    transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolioID) REFERENCES portfolios(portfolioID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS stock_data (
    id SERIAL PRIMARY KEY,
    ticker_symbol VARCHAR(10),
    price DECIMAL(10, 4),
    change_percent DECIMAL(10,4)
);