import axios from 'axios';
import { API_URL } from '../constants/constants';

export const getStocks = (portfolioId) => axios.get(`${API_URL}/stocks/${portfolioId}`);