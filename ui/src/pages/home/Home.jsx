import React from 'react';
import { useNavigate } from 'react-router-dom';
import { set, useForm } from 'react-hook-form';
import { createPortfolio, getPortfolios, updatePortfolio, deletePortfolio } from '../../services/portfolioService';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash, faEdit } from '@fortawesome/free-solid-svg-icons';
import './Home.css';

function Home() {
  const startingCash = 1000;
  const formatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'CAD',
  });

  const [showCreateModal, setShowCreateModal] = React.useState(false);
  const [showEditModal, setShowEditModal] = React.useState(false);
  const [portfolios, setPortfolios] = React.useState([]);
  const [currentPortfolio, setCurrentPortfolio] = React.useState(null);
  const { register, handleSubmit, formState: { errors }, reset } = useForm();
  const navigate = useNavigate();

  React.useEffect(() => {
    const fetchPortfolios = async () => {
      try {
        const response = await getPortfolios();
        setPortfolios(response.data);
      } catch (error) {
        console.error('Error fetching portfolios:', error);
      }
    };
    fetchPortfolios();
  }, []);

  React.useEffect(() => {
    if (currentPortfolio) {
      reset({
        portfolioName: currentPortfolio.portfolioName,
        description: currentPortfolio.description,
      });
    }
  }, [showEditModal]);

  const handlePortfolioClick = () => {
    navigate('/dashboard');
  };

  const handleOpenCreateModal = () => {
    setShowCreateModal(true);
  }

  const handleCloseCreateModal = () => {
    setShowCreateModal(false);
  }

  const handleOpenEditModal = (id) => {
    console.log('Opening edit modal for portfolio ID:', id);
    console.log('Current portfolios:', portfolios);
    setCurrentPortfolio(portfolios.find(p => p.portfolioID === id));
    console.log('Current portfolio:', currentPortfolio);
    setShowEditModal(true);
  }

  const handleCloseEditModal = () => {
    setCurrentPortfolio(null);
    setShowEditModal(false);
  }

  const handleDeletePortfolio = async (id) => {
    const confirmDelete = window.confirm('Are you sure you want to delete this portfolio?');
    if (!confirmDelete) return;
    try {
      await deletePortfolio(id);
      setPortfolios(prev => prev.filter(p => p.portfolioID !== id));
    } catch (error) {
      alert('Failed to delete portfolio');
    }
  };

  const onCreateSubmit = async (data) => {
    const portfolioData = { ...data, cash: startingCash };
    const response = await createPortfolio(portfolioData);
    const newPortfolio = response.data;
    setPortfolios(prev => [...prev, newPortfolio]);
    setShowCreateModal(false);
  }

  const onEditSubmit = async (data) => {
    const updatedPortfolio = { ...currentPortfolio, ...data };
    const response = await updatePortfolio(updatedPortfolio.portfolioID, updatedPortfolio);
    const updatedData = response.data;
    setPortfolios(prev => prev.map(p => p.portfolioID === updatedData.portfolioID ? updatedData : p));
    setCurrentPortfolio(null);
    setShowEditModal(false);
  }

  return (
    <div className="home-root">
      <h1>Team 18 Portfolio Manager</h1>
      <div className="button-container">
        <button onClick={handleOpenCreateModal}><h3>Add Portfolio</h3></button>
      </div>
      <div className="portfolio-container">
        {portfolios.map((portfolio) => (
          <div
            className="portfolio-square"
            key={portfolio.portfolioID}
            onClick={handlePortfolioClick}
            style={{ cursor: 'pointer' }}
          >
            <div className="portfolio-square-content">
              <h2>{portfolio.portfolioName}</h2>
              <p>{formatter.format(portfolio.cash)}</p>
              <div className='portfolio-actions-bar'>
                <button onClick={e => {
                  e.stopPropagation();
                  handleOpenEditModal(portfolio.portfolioID);
                }} className="portfolio-action-btn">
                  <FontAwesomeIcon icon={faEdit} style={{ cursor: 'pointer' }} />
                </button>
                <div className="portfolio-action-divider"></div>
                <button onClick={e => {
                  e.stopPropagation();
                  handleDeletePortfolio(portfolio.portfolioID);
                }} className="portfolio-action-btn">
                  <FontAwesomeIcon icon={faTrash} style={{ cursor: 'pointer' }} />
                </button>
              </div>
            </div>

          </div>
        ))}
      </div>
      {showCreateModal && (
        <div className="modal-backdrop">
          <div className="modal">
            <h2>Add Portfolio</h2>
            <form className="form-control" onSubmit={handleSubmit(onCreateSubmit)}>
              <label htmlFor="name">Choose a name:</label>
              <input {...register("portfolioName", { required: true })} id="name" type="text" placeholder="Portfolio Name" />
              {errors.name && <span className='error-message'>A name is required</span>}
              <label htmlFor="description">Description:</label>
              <textarea {...register("description")} name="description" id="description"></textarea>
              <div className="button-row">
                <button type="submit">Create</button>
                <button type="button" onClick={handleCloseCreateModal}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showEditModal && (
        <div className="modal-backdrop">
          <div className="modal">
            <h2>Edit Portfolio</h2>
            <form className="form-control" onSubmit={handleSubmit(onEditSubmit)}>
              <label htmlFor="name">Rename</label>
              <input {...register("portfolioName", { required: true })} id="name" type="text" />
              {errors.portfolioName && <span className='error-message'>A name is required</span>}
              <label htmlFor="description">Description:</label>
              <textarea {...register("description")} name="description" id="description" />
              <div className="button-row">
                <button type="submit">Save</button>
                <button type="button" onClick={handleCloseEditModal}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
}

export default Home;
