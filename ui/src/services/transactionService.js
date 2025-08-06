import axios from 'axios';
import { API_URL } from '../constants/constants';

export const getTransactionsByPortfolioId = (portfolioId) => axios.get(`${API_URL}/transactions/${portfolioId}`);