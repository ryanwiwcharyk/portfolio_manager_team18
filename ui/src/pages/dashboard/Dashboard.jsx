import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { getStocks, purchaseStock } from '../../services/stockService';
import { getPortfolioById } from '../../services/portfolioService';
import { formatter } from '../../constants/constants';
import { ToastContainer, toast } from 'react-toastify';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash, faPenToSquare } from '@fortawesome/free-solid-svg-icons';
import { getTransactionsByPortfolioId } from '../../services/portfolioService';
import './dashboard.css';

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
  const { register, handleSubmit, formState: { errors } } = useForm();
  const { id } = useParams();
  const [portfolio, setPortfolio] = useState(null);
  const [portfolioStocks, setPortfolioStocks] = useState([]);
  const [showBuyModal, setShowBuyModal] = useState(false);
  const [showSellModal, setShowSellModal] = useState(false);
  const [selectedStock, setSelectedStock] = useState(null);
  const notify = (message) => toast.error(message, { position: "top-right", autoClose: 5000 });

  useEffect(() => {
    const fetchPortfolio = async () => {
      try {
        const response = await getPortfolioById(id);
        setPortfolio(response.data);
      } catch (error) {
        notify('Failed to fetch portfolio data: ' + error.message);
      }
    };
    fetchPortfolio();
  }, [id]);

  useEffect(() => {
    const fetchStocks = async () => {
      try {
        const response = await getStocks(id);
        setPortfolioStocks(response.data);
      }
      catch (error) {
        notify('Failed to fetch portfolio stocks: ' + error.message);
      }
    };
    fetchStocks();
  }, [id]);

  useEffect(() => {
    const fetchTransactions = async () => {
      setLoading(true);
      setError(null);
      
      try {
        const response = await getTransactionsByPortfolioId(id);
        
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

  const onPurchaseSubmit = async (data) => {
    try {
      const purchaseData = {
        portfolioID: portfolio.portfolioID,
        tickerSymbol: data.tickerSymbol,
        qty: data.qty
      }
      const response = await purchaseStock(purchaseData);
      const newStock = response.data;
      portfolio.cash = response.data.cash;

      setPortfolioStocks(prevStocks => [...prevStocks, newStock]);
    }
    catch (error) {
      notify('Failed to purchase stock: ' + error.message);
    }
    finally {
      setShowBuyModal(false);
    }
  }

  const onSellSubmit = async (data) => {
    try {
      // Replace with your sellStock service call
      // Example: await sellStock({ portfolioID: portfolio.portfolioID, tickerSymbol: selectedStock.tickerSymbol, qty: data.qty });
      notify(`Sold ${data.qty} shares of ${selectedStock.tickerSymbol}`);
      // Optionally update portfolioStocks here
    } catch (error) {
      notify('Failed to sell stock: ' + error.message);
    } finally {
      handleCloseSellModal();
    }
  };

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

  const handleOpenBuyModal = () => {
    setShowBuyModal(true);
  }

  const handleCloseBuyModal = () => {
    setShowBuyModal(false);
  }

  const handleOpenSellModal = (stock) => {
    setSelectedStock(stock);
    setShowSellModal(true);
  };

  const handleCloseSellModal = () => {
    setShowSellModal(false);
    setSelectedStock(null);
  };


  if (!portfolio) {
    return <div>Loading...</div>;
  }

  const renderDashboardTab = () => (
    <>
      <div className="dashboard-actions-top">
        <button onClick={handleOpenBuyModal} className="dashboard-action-btn">Buy</button>
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
        <div className="dashboard-section-header">
          <div className="dashboard-section-title-group">
            <h2>Portfolio Holdings</h2>
            <p className="dashboard-section-subtext">As of previous market close</p>
          </div>
          <h2>Portfolio Cash: {formatter.format(portfolio.cash)}</h2>
        </div>
        <table className="dashboard-table">
          <thead>
            <tr>
              <th>Stock</th>
              <th>Shares</th>
              <th>Avg Price ($)</th>
              <th>Value ($)</th>
              <th>Change (%)</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {portfolioStocks.map(stock => (
              <tr key={stock.tickerSymbol}>
                <td>{stock.tickerSymbol}</td>
                <td>{stock.qty}</td>
                <td>{stock.avgPrice}</td>
                <td>{stock.currentPrice}</td>
                <td style={{ color: stock.changePercent >= 0 ? 'green' : 'red' }}>{stock.changePercent}</td>
                 <td>
                  <button
                    onClick={() => handleOpenSellModal(stock)}
                    className="dashboard-action-btn"
                    title="Sell"
                  >
                    <FontAwesomeIcon icon={faTrash} />
                  </button>
                  <button
                    className="dashboard-action-btn"
                    title="Edit"
                  >
                    <FontAwesomeIcon icon={faPenToSquare} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {showBuyModal && (
        <div className="modal-backdrop">
          <div className="modal">
            <h2>Purchase Stock</h2>
            <form className="form-control" onSubmit={handleSubmit(onPurchaseSubmit)}>
              <label htmlFor="name">Ticker Symbol: </label>
              <input {...register("tickerSymbol",
                {
                  required: true,
                  maxLength: 4,
                  pattern: {
                    value: /^[A-Za-z]{1,4}$/,
                    message: "Ticker must be 1-4 letters"
                  }
                })} id="ticker-symbol" type="text" placeholder="ex: AAPL, MSFT..." maxLength={4} />
              {errors.tickerSymbol && <span className='error-message'>{errors.tickerSymbol.message || "A ticker symbol is required"}</span>}
              <label htmlFor="quantity">Quantity:</label>
              <input {...register("qty", { required: true })} id="quantity" type="number"></input>
              {errors.qty && <span className='error-message'>{errors.qty.message || "A quantity is required"}</span>}
              <div className="button-row">
                <button type="submit">Purchase</button>
                <button type="button" onClick={handleCloseBuyModal}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}
       {showSellModal && selectedStock && (
        <div className="modal-backdrop">
          <div className="modal">
            <h2>
              Sell Stock <FontAwesomeIcon icon={faTrash} />
            </h2>
            <form className="form-control" onSubmit={handleSubmit(onSellSubmit)}>
              <label htmlFor="sell-ticker">Ticker Symbol:</label>
              <input
                id="sell-ticker"
                type="text"
                value={selectedStock.tickerSymbol}
                disabled
                style={{ background: "#f3f4f6" }}
              />
              <label htmlFor="sell-qty">Quantity to Sell:</label>
              <input
                {...register("qty", {
                  required: true,
                  min: 1,
                  max: selectedStock.qty,
                  valueAsNumber: true,
                })}
                id="sell-qty"
                type="number"
                min={1}
                max={selectedStock.qty}
                placeholder={`Max: ${selectedStock.qty}`}
              />
              {errors.qty && (
                <span className="error-message">
                  {errors.qty.message || `Enter a value between 1 and ${selectedStock.qty}`}
                </span>
              )}
              <div className="button-row">
                <button type="submit">Sell</button>
                <button type="button" onClick={handleCloseSellModal}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}
      <ToastContainer />
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
      <h1>{portfolio.portfolioName}</h1>
      <h3>{portfolio.description}</h3>
      
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
