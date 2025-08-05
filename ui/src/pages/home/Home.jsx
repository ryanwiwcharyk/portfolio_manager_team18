import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { createPortfolio, getPortfolios, updatePortfolio, deletePortfolio } from '../../services/portfolioService';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash, faEdit } from '@fortawesome/free-solid-svg-icons';
import { formatter } from '../../constants/constants';
import { ToastContainer, toast } from 'react-toastify';
import './Home.css';

function Home() {
  const startingCash = 1000;
  const [showCreateModal, setShowCreateModal] = React.useState(false);
  const [showEditModal, setShowEditModal] = React.useState(false);
  const [portfolios, setPortfolios] = React.useState([]);
  const [currentPortfolio, setCurrentPortfolio] = React.useState(null);
  const { register, handleSubmit, formState: { errors }, reset } = useForm();
  const navigate = useNavigate();
  const notify = (message) => toast.error(message, { position: "top-right", autoClose: 5000 });
  
  React.useEffect(() => {
    const fetchPortfolios = async () => {
      try {
        const response = await getPortfolios();
        setPortfolios(response.data);
      } catch (error) {
        notify('Failed to fetch portfolios: ' + error.message);
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
  }, [showEditModal, currentPortfolio, reset]);

  const handlePortfolioClick = (id) => {
    navigate(`/dashboard/${id}`);
  };

  const handleOpenCreateModal = () => {
    setShowCreateModal(true);
  }

  const handleCloseCreateModal = () => {
    setShowCreateModal(false);
  }

  const handleOpenEditModal = (id) => {
    setCurrentPortfolio(portfolios.find(p => p.portfolioID === id));
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
      notify('Failed to delete portfolio: ' + error.message);
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
      <h1>Hi, <span>We're Team 18,</span></h1>
      <h2>Welcome to our Portfolio Manager</h2>
      <p>Our portfolio manager web application designed to help users manage their stock investments efficiently. It allows users to create and organize multiple portfolios, track cash balances, and maintain detailed descriptions for each portfolio. With an intuitive interface and simple controls, users can easily add, edit, and delete portfolios and stocks, making it ideal for individual investors or students learning about portfolio management.
      </p>    
      <div className="button-wrapper">
        <button className="button-container-portfolio" onClick={handleOpenCreateModal}>Add Portfolio</button>
        <button className="button-container-portfolio" onClick={handleOpenCreateModal}>Search Portfolio</button>
      </div>

      


      <div className="portfolio-container">
        {portfolios.map((portfolio) => (
          <div
            className="portfolio-square"
            key={portfolio.portfolioID}
            onClick={() => handlePortfolioClick(portfolio.portfolioID)}
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
      <ToastContainer />
    </div>
  );
}

export default Home;