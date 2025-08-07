import axios from 'axios';
import { API_URL } from '../constants/constants';

export const getPortfolios = () => axios.get(`${API_URL}/portfolios`);
export const createPortfolio = (portfolio) => axios.post(`${API_URL}/portfolios`, portfolio);
export const getPortfolioById = (id) => axios.get(`${API_URL}/portfolios/${id}`);
export const updatePortfolio = (id, portfolio) => axios.put(`${API_URL}/portfolios/${id}`, portfolio);
export const deletePortfolio = (id) => axios.delete(`${API_URL}/portfolios/${id}`);
export const depositCash = (id, amount) =>
    axios.put(`${API_URL}/portfolios/${id}/updateCash`, amount, {
      headers: { 'Content-Type': 'application/json' }
    });
  
  export const withdrawCash = (id, amount) =>
    axios.put(`${API_URL}/portfolios/${id}/updateCash`, -amount, {
      headers: { 'Content-Type': 'application/json' }
    });
