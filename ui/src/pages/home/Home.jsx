import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Home.css';

const portfolios = [
    {
      id: 1,
      name: 'Portfolio 1',
      description: 'Description 1',
    },
    {
      id: 2,
      name: 'Portfolio 2',
      description: 'Description 2',
    },
    {
      id: 3,
      name: 'Portfolio 3',
      description: 'Description 3',
    },
    {
      id: 4,
      name: 'Portfolio 4',
      description: 'Description 4',
    },
    {
      id: 5,
      name: 'Portfolio 5',
      description: 'Description 5',
    },
    {
      id: 6,
      name: 'Portfolio 6',
      description: 'Description 6',
    },
    {
      id: 7,
      name: 'Portfolio 7',
      description: 'Description 7',
    },
    {
      id: 8,
      name: 'Portfolio 8',
      description: 'Description 8',
    },
  ]

function Home() {
  const navigate = useNavigate();

  const handlePortfolioClick = () => {
    navigate('/dashboard');
  };

  return (
    <div className="home-root">
      <h1>Team 18 Portfolio Manager</h1>
      <div className="button-container">
        <button><h3>Add Portfolio</h3></button>
      </div>
      <div className="portfolio-container">
        {portfolios.map((portfolio) => (
          <div 
            className="portfolio-square" 
            key={portfolio.id} 
            onClick={handlePortfolioClick}
            style={{ cursor: 'pointer' }}
          >
            <h2>{portfolio.name}</h2>
            <p>{portfolio.description}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Home;
