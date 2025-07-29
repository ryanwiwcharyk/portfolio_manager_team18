CREATE DATABASE IF NOT EXISTS portfolio_manager;
USE portfolio_manager;

CREATE TABLE IF NOT EXISTS portfolios (
    portfolioID INT AUTO_INCREMENT PRIMARY KEY,
    portfolioName VARCHAR(255) NOT NULL,
    cash DECIMAL(15,2) NOT NULL DEFAULT 1000.0
);

CREATE TABLE IF NOT EXISTS stocks (
    tickerSymbol VARCHAR(10) NOT NULL,
    portfolioID INT NOT NULL,
    qty INT NOT NULL,
    avgPrice DECIMAL(15,2) NOT NULL,
    PRIMARY KEY (portfolioID, tickerSymbol),
    FOREIGN KEY (portfolioID) REFERENCES portfolios(portfolioID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions (
    transactionID INT AUTO_INCREMENT PRIMARY KEY,
    tickerSymbol VARCHAR(10) NOT NULL,
    portfolioID INT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    qty INT NOT NULL,
    isSell BOOLEAN NOT NULL,
    transactionTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolioID) REFERENCES portfolios(portfolioID) ON DELETE CASCADE
);