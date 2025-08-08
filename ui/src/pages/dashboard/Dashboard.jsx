import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { set, useForm } from 'react-hook-form';
import { getStocks, purchaseStock, sellStock } from '../../services/stockService';
import { getTransactionsByPortfolioId } from '../../services/transactionService';
import { getPortfolioById, depositCash, withdrawCash } from '../../services/portfolioService';
import { formatter } from '../../constants/constants';
import { ToastContainer, toast } from 'react-toastify';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash, faPenToSquare } from '@fortawesome/free-solid-svg-icons';
import { formatISOToDateTime } from '../../formatters/dateFormmater';
import './dashboard.css';

// Add spinner animation styles
const spinnerStyle = `
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`;

// Inject styles into head
if (typeof document !== 'undefined') {
  const styleElement = document.createElement('style');
  styleElement.textContent = spinnerStyle;
  document.head.appendChild(styleElement);
}

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
  const [portfolioError, setPortfolioError] = useState(null);
  const [portfolioLoading, setPortfolioLoading] = useState(true);
  const maxHistoryValue = Math.max(...portfolioHistory.map(h => h.value));
  const { register, handleSubmit, watch, formState: { errors }, reset } = useForm();
  const { id } = useParams();
  const [portfolio, setPortfolio] = useState(null);
  const [portfolioStocks, setPortfolioStocks] = useState([]);
  const [showBuyModal, setShowBuyModal] = useState(false);
  const [showSellModal, setShowSellModal] = useState(false);
  const [showDepositModal, setShowDepositModal] = useState(false);
  const [showWithdrawModal, setShowWithdrawModal] = useState(false);
  const [selectedStock, setSelectedStock] = useState(null);
  const [realizedP, setRealizedP] = useState();
  const [unrealizedP, setUnrealizedP] = useState();
  const notify = (message) => toast.error(message, { position: "top-right", autoClose: 5000 });

  useEffect(() => {
    const fetchPortfolio = async () => {
      setPortfolioLoading(true);
      setPortfolioError(null);
      try {
        const response = await getPortfolioById(id);
        setPortfolio(response.data);
      } catch (error) {
        if (error.response?.status === 404) {
          setPortfolioError('Portfolio not found. The portfolio you\'re looking for doesn\'t exist or may have been deleted.');
        } else if (error.response?.status === 403) {
          setPortfolioError('Access denied. You don\'t have permission to view this portfolio.');
        } else {
          setPortfolioError('Failed to load portfolio. Please check your connection and try again.');
        }
      } finally {
        setPortfolioLoading(false);
      }
    };
    fetchPortfolio();
  }, [id]);

  useEffect(() => {
    const fetchStocks = async () => {
      try {
        const response = await getStocks(id);
        setPortfolioStocks(response.data || []);
      }
      catch (error) {
        notify('Failed to fetch portfolio stocks: ' + error.response.data.errorMessage);
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
        notify('Failed to fetch portfolio transactions: ' + err.response.data.errorMessage);
      } finally {
        setLoading(false);
      }
    };

    fetchTransactions();
  }, [id, activeTab]);

  useEffect(() => {
    const updatedUnrealized = calculateUnrealizedPnL(portfolioStocks);
    const updatedRealized = calculateRealizedPnL(transactions);
    setUnrealizedP(updatedUnrealized);
    setRealizedP(updatedRealized);
  }, [portfolioStocks, transactions]);


  const refreshStocks = async () => {
    try {
      const response = await getStocks(id);
      setPortfolioStocks(response.data || []);
    } catch (error) {
      console.error('Failed to refresh stocks:', error);
    }
  };

  const onPurchaseSubmit = async (data) => {
    try {
      const purchaseData = {
        portfolioID: portfolio.portfolioID,
        tickerSymbol: data.tickerSymbol,
        qty: data.qty
      }
      const response = await purchaseStock(purchaseData);
      portfolio.cash = response.data.updatedCash;

      // Refresh stocks from server to ensure we have the latest data
      await refreshStocks();
    }
    catch (error) {
      notify('Failed to purchase stock: ' + error.response.data.errorMessage);
    }
    finally {
      handleCloseBuyModal();
    }
  }

  const calculateUnrealizedPnL = (stocks) => {
    if (!stocks || stocks.length === 0) return 0;

    return stocks.reduce((sum, stock) => {
      const marketValue = stock.currentPrice * stock.qty;
      const costBasis = stock.avgPrice * stock.qty;
      return sum + (marketValue - costBasis);
    }, 0);
  };

  const calculateRealizedPnL = (transactions) => {
    if (!transactions || transactions.length === 0) return 0;

    let realizedPnL = 0;
    const costBasis = {};

    const sorted = [...transactions].sort((a, b) => new Date(a.transactionTime) - new Date(b.transactionTime));

    for (const tx of sorted) {
      const ticker = tx.tickerSymbol;
      const qty = tx.qty;
      const price = tx.price;

      if (!costBasis[ticker]) {
        costBasis[ticker] = { totalQty: 0, totalCost: 0 };
      }

      if (!tx.sell) {
        costBasis[ticker].totalQty += qty;
        costBasis[ticker].totalCost += price * qty;
      } else {
        const { totalQty, totalCost } = costBasis[ticker];
        if (totalQty === 0) continue;

        const avgCost = totalCost / totalQty;
        realizedPnL += (price - avgCost) * qty;

        costBasis[ticker].totalQty -= qty;
        costBasis[ticker].totalCost -= avgCost * qty;
      }
    }

    return realizedPnL;
  };


  const onSellSubmit = async (data) => {
    try {
      const requestData = {
        portfolioID: portfolio.portfolioID,
        tickerSymbol: selectedStock.tickerSymbol,
        qty: data.qty
      };

      const response = await sellStock(requestData);
      portfolio.cash = portfolio.cash + response.data.updatedCash;

      const stocksResponse = await getStocks(portfolio.portfolioID);
      setPortfolioStocks(stocksResponse.data || []);

      const txResponse = await getTransactionsByPortfolioId(portfolio.portfolioID);
      const sortedTxs = txResponse.data.sort((a, b) => new Date(b.transactionTime) - new Date(a.transactionTime));
      setTransactions(sortedTxs);
    } catch (error) {
      notify('Failed to sell stock: ' + error.response.data.errorMessage);
    } finally {
      handleCloseSellModal();
    }
  };


  const onDepositSubmit = async (data) => {
    try {
      const response = await depositCash(id, data.amount);
      setPortfolio(response.data);
    } catch (error) {
      notify('Failed to deposit: ' + error.message);
    } finally {
      handleCloseDepositModal();
    }
  };

  const onWithdrawSubmit = async (data) => {
    try {
      const response = await withdrawCash(id, data.amount);
      setPortfolio(response.data);
    } catch (error) {
      notify('Failed to withdraw: ' + error.message);
    } finally {
      handleCloseWithdrawModal();
    }
  };

  const formatTransaction = (transaction) => {
    const date = formatISOToDateTime(transaction.transactionTime);
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
    reset(); // Reset form when closing
  }

  const handleOpenSellModal = (stock) => {
    setSelectedStock(stock);
    reset(); // Reset form when opening
    setShowSellModal(true);
  };

  const handleCloseSellModal = () => {
    setShowSellModal(false);
    setSelectedStock(null);
    reset(); // Reset form when closing
  };


  // Handle portfolio loading and error states
  if (portfolioLoading) {
    return (
      <div className="dashboard-root">
        <div className="loading-container" style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '50vh',
          flexDirection: 'column'
        }}>
          <div className="loading-spinner" style={{
            border: '4px solid #f3f4f6',
            borderTop: '4px solid #002B51',
            borderRadius: '50%',
            width: '40px',
            height: '40px',
            animation: 'spin 1s linear infinite',
            marginBottom: '20px'
          }}></div>
          <p>Loading portfolio...</p>
        </div>
      </div>
    );
  }

  if (portfolioError) {
    return (
      <div className="dashboard-root">
        <div className="error-container" style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '50vh',
          flexDirection: 'column',
          textAlign: 'center',
          padding: '20px'
        }}>
          <div style={{
            fontSize: '64px',
            marginBottom: '20px',
            color: '#e53e3e'
          }}>⚠️</div>
          <h2 style={{ color: '#e53e3e', marginBottom: '10px' }}>Portfolio Not Found</h2>
          <p style={{
            color: '#666',
            marginBottom: '20px',
            maxWidth: '400px',
            lineHeight: '1.5'
          }}>
            {portfolioError}
          </p>
          <div style={{ display: 'flex', gap: '10px' }}>
            <button
              className="dashboard-action-btn"
              onClick={() => window.history.back()}
            >
              Go Back
            </button>
            <button
              className="dashboard-action-btn"
              onClick={() => window.location.href = '/'}
            >
              Go to Home
            </button>
          </div>
        </div>
      </div>
    );
  }
  const handleOpenDepositModal = () => {
    setShowDepositModal(true);
  };

  const handleCloseDepositModal = () => {
    setShowDepositModal(false);
  };

  const handleOpenWithdrawModal = () => {
    setShowWithdrawModal(true);
  };

  const handleCloseWithdrawModal = () => {
    setShowWithdrawModal(false);
  };

  // Calculate portfolio allocation data for pie chart
  const getPortfolioAllocation = () => {
    if (!portfolio || !portfolioStocks) return [];

    const allocation = [];

    // Add cash
    if (portfolio.cash > 0) {
      allocation.push({
        name: 'Cash',
        value: portfolio.cash,
        color: '#10B981', // Green for cash
        percentage: 0
      });
    }

    // Add stocks
    portfolioStocks.forEach(stock => {
      const stockValue = stock.qty * stock.currentPrice;
      if (stockValue > 0) {
        allocation.push({
          name: stock.tickerSymbol,
          value: stockValue,
          color: getStockColor(stock.tickerSymbol),
          percentage: 0
        });
      }
    });

    // Calculate percentages
    const totalValue = allocation.reduce((sum, item) => sum + item.value, 0);
    allocation.forEach(item => {
      item.percentage = totalValue > 0 ? (item.value / totalValue) * 100 : 0;
    });

    return allocation.sort((a, b) => b.value - a.value); // Sort by value descending
  };

  // Generate colors for stocks
  const getStockColor = (ticker) => {
    const colors = [
      '#3B82F6', '#EF4444', '#F59E0B', '#8B5CF6', '#EC4899',
      '#06B6D4', '#84CC16', '#F97316', '#6366F1', '#14B8A6'
    ];
    const index = ticker.charCodeAt(0) % colors.length;
    return colors[index];
  };

  // Render pie chart
  const renderPieChart = () => {
    const allocation = getPortfolioAllocation();
    if (allocation.length === 0) {
      return <div className="no-data-message">No portfolio data available</div>;
    }

    const size = 200;
    const radius = size / 2;
    const centerX = size / 2;
    const centerY = size / 2;

    let currentAngle = 0;
    const paths = [];

    allocation.forEach((item, index) => {
      const angle = (item.percentage / 100) * 2 * Math.PI;
      const endAngle = currentAngle + angle;

      const x1 = centerX + radius * Math.cos(currentAngle);
      const y1 = centerY + radius * Math.sin(currentAngle);
      const x2 = centerX + radius * Math.cos(endAngle);
      const y2 = centerY + radius * Math.sin(endAngle);

      const largeArcFlag = angle > Math.PI ? 1 : 0;

      const pathData = [
        `M ${centerX} ${centerY}`,
        `L ${x1} ${y1}`,
        `A ${radius} ${radius} 0 ${largeArcFlag} 1 ${x2} ${y2}`,
        'Z'
      ].join(' ');

      paths.push(
        <path
          key={index}
          d={pathData}
          fill={item.color}
          stroke="#fff"
          strokeWidth="2"
        />
      );

      currentAngle = endAngle;
    });

    return (
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} className="pie-chart">
        {paths}
      </svg>
    );
  };

  const getInvestedAmount = () => {
    return transactions
      .filter(tx => !tx.sell) // Only BUY transactions
      .reduce((sum, tx) => sum + (tx.price * tx.qty), 0);
  };


  // Render pie chart legend
  const renderPieChartLegend = () => {
    const allocation = getPortfolioAllocation();

    return (
      <div className="legend-items">
        {allocation.map((item, index) => (
          <div key={index} className="legend-item">
            <div className="legend-header">
              <div className="legend-color" style={{ backgroundColor: item.color }}></div>
              <span className="legend-name">{item.name}</span>
            </div>
            <div className="legend-text">
              <span className="legend-value">{formatter.format(item.value)}</span>
              <span className="legend-percentage">({item.percentage.toFixed(1)}%)</span>
            </div>
          </div>
        ))}
      </div>
    );
  };

  if (!portfolio) {
    return (
      <div className="dashboard-root">
        <div style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '50vh'
        }}>
          <p>No portfolio data available.</p>
        </div>
      </div>
    );
  }

  const getTotalPortfolioValue = () => {
    if (!portfolio || !portfolioStocks) return 0;
    const stockValue = portfolioStocks.reduce((total, stock) => total + (stock.qty * stock.currentPrice), 0);
    return portfolio.cash + stockValue - 1000;
  };





  const renderDashboardTab = () => (
    <>
      <div className="dashboard-actions-top">
        <button onClick={handleOpenBuyModal} className="dashboard-action-btn">Buy</button>
        <button onClick={handleOpenDepositModal} className="dashboard-action-btn">Deposit</button>
        <button onClick={handleOpenWithdrawModal} className="dashboard-action-btn">Withdraw</button>
      </div>
      <div className="dashboard-section">
        <h2>Portfolio Allocation</h2>
        <div className="pie-chart-container">
          {renderPieChart()}
          <div className="portfolio-summary">
            <h2>
              Portfolio Value: {formatter.format(getTotalPortfolioValue())}
            </h2>
            <h2 style={{ color: unrealizedP >= 0 ? 'limegreen' : 'tomato' }}>
              Unrealized {unrealizedP >= 0 ? 'Profit' : 'Loss'}: {formatter.format(Math.abs(unrealizedP))}
            </h2>
            <h2 style={{ color: realizedP >= 0 ? 'limegreen' : 'tomato' }}>
              Realized {realizedP >= 0 ? 'Profit' : 'Loss'}: {formatter.format(Math.abs(realizedP))}
            </h2>
            <h2 style={{ color: (unrealizedP + realizedP) >= 0 ? 'limegreen' : 'tomato' }}>
              Total {realizedP >= 0 ? 'Profit' : 'Loss'}: {formatter.format(Math.abs(unrealizedP + realizedP))}
            </h2>
            
          </div>
        </div>
        <div className="pie-chart-legend">
          {renderPieChartLegend()}
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
              <th>Current Price ($)</th>
              <th>Daily Change (%)</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {portfolioStocks && portfolioStocks.length > 0 ? (
              portfolioStocks.map((stock, index) => (
                <tr key={`${stock.tickerSymbol}-${stock.qty}-${stock.avgPrice}-${index}`}>
                  <td>{stock.tickerSymbol.toUpperCase()}</td>
                  <td>{stock.qty}</td>
                  <td>{parseFloat(stock.avgPrice).toFixed(2)}</td>
                  <td>{stock.currentPrice}</td>
                  <td style={{ color: stock.changePercent >= 0 ? 'green' : 'red' }}>{stock.changePercent}</td>
                  <td>
                    <div className="dashboard-action-btn-group">
                      <button
                        onClick={() => handleOpenSellModal(stock)}
                        className="dashboard-action-btn"
                        title="Sell"
                      >
                        <FontAwesomeIcon icon={faTrash} />
                      </button>
                    </div>
                  </td>

                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', padding: '20px', color: '#666' }}>
                  No stocks in your portfolio. Click the "Buy" button to add some stocks!
                </td>
              </tr>
            )}
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
                  maxLength: 6,
                  pattern: {
                    value: /^[A-Za-z]{1,6}$/,
                    message: "Ticker must be 1-6 letters"
                  }
                })}
                id="ticker-symbol"
                type="text"
                placeholder="ex: AAPL, MSFT..."
                maxLength={6}
                style={{ textTransform: 'uppercase' }}
                onInput={(e) => e.target.value = e.target.value.toUpperCase()}
              />
              {errors.tickerSymbol && <span className='error-message'>{errors.tickerSymbol.message || "A ticker symbol is required"}</span>}
              <label htmlFor="quantity">Quantity:</label>
              <input {...register("qty", { required: true, valueAsNumber: true })} id="quantity" type="number"></input>
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
      {showDepositModal && (
        <div className="modal-backdrop">
          <div className="modal">
            <h2>Deposit Cash</h2>
            <form className="form-control" onSubmit={handleSubmit(onDepositSubmit)}>
              <label htmlFor="deposit-amount">Amount ($):</label>
              <input
                {...register("amount", {
                  required: true,
                  min: 0.01,
                  valueAsNumber: true,
                })}
                id="deposit-amount"
                type="number"
                step="0.01"
                min="0.01"
                placeholder="Enter amount to deposit"
              />
              {errors.amount && (
                <span className="error-message">
                  {errors.amount.message || "Please enter a valid amount"}
                </span>
              )}
              <div className="button-row">
                <button type="submit">Deposit</button>
                <button type="button" onClick={handleCloseDepositModal}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}
      {showWithdrawModal && (
        <div className="modal-backdrop">
          <div className="modal">
            <h2>Withdraw Cash</h2>
            <form className="form-control" onSubmit={handleSubmit(onWithdrawSubmit)}>
              <label htmlFor="withdraw-amount">Amount ($):</label>
              <input
                {...register("amount", {
                  required: true,
                  min: 0.01,
                  max: portfolio.cash,
                  valueAsNumber: true,
                })}
                id="withdraw-amount"
                type="number"
                step="0.01"
                min="0.01"
                max={portfolio.cash}
                placeholder={`Max: ${formatter.format(portfolio.cash)}`}
              />
              {errors.amount && (
                <span className="error-message">
                  {errors.amount.message || `Enter a value between 0.01 and ${formatter.format(portfolio.cash)}`}
                </span>
              )}
              <div className="button-row">
                <button type="submit">Withdraw</button>
                <button type="button" onClick={handleCloseWithdrawModal}>Cancel</button>
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
      <div className="dashboard-header">
        <button
          className="dashboard-back-btn"
          onClick={() => window.location.href = '/'}
          style={{
            background: 'none',
            border: '1px solid #ccc',
            padding: '8px 16px',
            borderRadius: '4px',
            cursor: 'pointer',
            marginBottom: '10px',
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            color: '#35db77',
            fontSize: '15px'
          }}
        >
          ← Go Back
        </button>
        <h1>{portfolio.portfolioName}</h1>
        <h3>{portfolio.description}</h3>
      </div>

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
