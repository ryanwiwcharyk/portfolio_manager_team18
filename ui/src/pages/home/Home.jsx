import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { createPortfolio, getPortfolios } from '../../services/portfolioService';
import './Home.css';

function Home() {
  const startingCash = 1000;
  const [showModal, setShowModal] = React.useState(false);
  const [portfolios, setPortfolios] = React.useState([]);
  const { register, handleSubmit, formState: { errors } } = useForm();
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

  const handlePortfolioClick = () => {
    navigate('/dashboard');
  };

  const handleOpenModal = () => {
    setShowModal(true);
  }

  const handleCloseModal = () => {
    setShowModal(false);
  }

  const onSubmit = async (data) => {
    const portfolioData = {...data, cash: startingCash };
    const response = await createPortfolio(portfolioData);
    const newPortfolio = response.data;
    setPortfolios(prev => [...prev, newPortfolio]);
    setShowModal(false);
  }

  return (
    <div className="home-root">
      <h1>Team 18 Portfolio Manager</h1>
      <div className="button-container">
        <button onClick={handleOpenModal}><h3>Add Portfolio</h3></button>
      </div>
      <div className="portfolio-container">
        {portfolios.map((portfolio) => (
          <div 
            className="portfolio-square" 
            key={portfolio.id} 
            onClick={handlePortfolioClick}
            style={{ cursor: 'pointer' }}
          >
            <h2>{portfolio.portfolioName}</h2>
            <p>{portfolio.description}</p>
          </div>
        ))}
      </div>
      {showModal && (
        <div className="modal-backdrop">
          <div className="modal">
            <h2>Add Portfolio</h2>
            <form className="form-control" onSubmit={handleSubmit(onSubmit)}>
              <label htmlFor="name">Choose a name:</label>
              <input {...register("portfolioName", {required: true })} id="name" type="text" placeholder="Portfolio Name" />
              {errors.name && <span className='error-message'>A name is required</span>}
              <label htmlFor="description">Description:</label>
              <textarea {...register("description")} name="description" id="description"></textarea>
              <div className="button-row">
              <button type="submit">Create</button>
              <button type="button" onClick={handleCloseModal}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Home;
