import React, { useState, useEffect } from 'react';
import { getTransactionsByPortfolioId } from '../../services/portfolioService';
import './dashboard.css';

// Mock data for demonstration
const portfolioSummary = [
  { name: 'AAPL', value: 12000, avgPrice: 100, change: 2.3, shares: 30 },
  { name: 'GOOGL', value: 8000, avgPrice: 800, change: -1.1, shares: 10 },
  { name: 'TSLA', value: 5000, avgPrice: 900, change: 4.7, shares: 5 },
  { name: 'AMZN', value: 3000, avgPrice: 1500, change: 0.5, shares: 2 },
];

const portfolioHistory = [
  { date: '2024-06-01', value: 25000 },
  { date: '2024-06-02', value: 25500 },
  { date: '2024-06-03', value: 26000 },
  { date: '2024-06-04', value: 25800 },
  { date: '2024-06-05', value: 26500 },
];

function Dashboard() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const maxHistoryValue = Math.max(...portfolioHistory.map(h => h.value));

  useEffect(() => {
    const fetchTransactions = async () => {
      setLoading(true);
      setError(null);
      
      try {
        const response = await getTransactionsByPortfolioId(1);
        
        const data = response.data;
        const sortedData = data.sort((a, b) => new Date(b.transactionTime) - new Date(a.transactionTime));
        setTransactions(sortedData);
      } catch (err) {
        console.error('Error fetching transactions:', err);
        setError('Failed to load transactions. Please check if the backend is running.');
      } finally {
        setLoading(false);
      }
    };

    fetchTransactions();
  }, []);

  const formatTransaction = (transaction) => {
    const date = new Date(transaction.transactionTime).toLocaleDateString();
    const type = transaction.sell === true ? 'SELL' : 'BUY';
    const total = (transaction.price * transaction.qty).toFixed(2);
    
    return {
      id: transaction.transactionID,
      date: date,
      ticker: transaction.tickerSymbol,
      type: type,
      shares: transaction.qty,
      price: parseFloat(transaction.price).toFixed(2),
      total: total
    };
  };

  const renderDashboardTab = () => (
    <>
      <div className="dashboard-actions-top">
        <button className="dashboard-action-btn">Buy</button>
        <button className="dashboard-action-btn">Sell</button>
      </div>
      <div className="dashboard-section">
        <h2>Portfolio Value Over Time</h2>
        {/* Simple SVG Line Graph */}
        <svg width="100%" height="120" viewBox="0 0 400 120" className="dashboard-graph">
          <polyline
            fill="none"
            stroke="#002B51"
            strokeWidth="3"
            points={portfolioHistory.map((h, i) => `${i * 100},${120 - (h.value / maxHistoryValue) * 100}`).join(' ')}
          />
          {portfolioHistory.map((h, i) => (
            <circle key={h.date} cx={i * 100} cy={120 - (h.value / maxHistoryValue) * 100} r="4" fill="#2d3748" />
          ))}
        </svg>
        <div className="dashboard-graph-labels">
          {portfolioHistory.map((h, i) => (
            <span key={h.date}>{h.date.slice(5)}</span>
          ))}
        </div>
      </div>
      <div className="dashboard-section">
        <h2>Portfolio Holdings</h2>
        <table className="dashboard-table">
          <thead>
            <tr>
              <th>Stock</th>
              <th>Shares</th>
              <th>Avg Price ($)</th>
              <th>Value ($)</th>
              <th>Change (%)</th>
            </tr>
          </thead>
          <tbody>
            {portfolioSummary.map(stock => (
              <tr key={stock.name}>
                <td>{stock.name}</td>
                <td>{stock.shares}</td>
                <td>{stock.avgPrice}</td>
                <td>{stock.value.toLocaleString()}</td>
                <td style={{ color: stock.change >= 0 ? 'green' : 'red' }}>{stock.change}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );

  const renderTransactionsTab = () => (
    <div className="dashboard-section">
      <h2>Transaction History</h2>
      
      {loading && (
        <div className="loading-message">
          <p>Loading transactions...</p>
        </div>
      )}
      
      {error && (
        <div className="error-message">
          <p>{error}</p>
          <button 
            className="dashboard-action-btn" 
            onClick={() => window.location.reload()}
          >
            Retry
          </button>
        </div>
      )}
      
      {!loading && !error && transactions.length === 0 && (
        <div className="no-data-message">
          <p>No transactions found.</p>
        </div>
      )}
      
      {!loading && !error && transactions.length > 0 && (
        <table className="dashboard-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Ticker</th>
              <th>Type</th>
              <th>Shares</th>
              <th>Price ($)</th>
              <th>Total ($)</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map(transaction => {
              const formattedTransaction = formatTransaction(transaction);
              return (
                <tr key={formattedTransaction.id}>
                  <td>{formattedTransaction.date}</td>
                  <td>{formattedTransaction.ticker}</td>
                  <td>
                    <span className={`transaction-type ${formattedTransaction.type.toLowerCase()}`}>
                      {formattedTransaction.type}
                    </span>
                  </td>
                  <td>{formattedTransaction.shares}</td>
                  <td>{formattedTransaction.price}</td>
                  <td>{formattedTransaction.total}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );

  return (
    <div className="dashboard-root">
      <h1>Portfolio X Dashboard</h1>
      
      {/* Tab Navigation */}
      <div className="dashboard-tabs">
        <button 
          className={`dashboard-tab ${activeTab === 'dashboard' ? 'active' : ''}`}
          onClick={() => setActiveTab('dashboard')}
        >
          Dashboard
        </button>
        <button 
          className={`dashboard-tab ${activeTab === 'transactions' ? 'active' : ''}`}
          onClick={() => setActiveTab('transactions')}
        >
          Transaction History
        </button>
      </div>

      {/* Tab Content */}
      <div className="dashboard-content">
        {activeTab === 'dashboard' && renderDashboardTab()}
        {activeTab === 'transactions' && renderTransactionsTab()}
      </div>
    </div>
  );
}

export default Dashboard;
