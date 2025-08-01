import axios from 'axios';
import { API_URL } from '../constants/constants';

export const getPortfolios = () => axios.get(`${API_URL}/portfolios`);
export const createPortfolio = (portfolio) => axios.post(`${API_URL}/portfolios`, portfolio);