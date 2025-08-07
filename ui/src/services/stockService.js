import axios from 'axios';
import { API_URL } from '../constants/constants';

export const getStocks = (portfolioId) => axios.get(`${API_URL}/stocks/${portfolioId}`);
export const purchaseStock = (stock) => axios.post(`${API_URL}/stocks/purchase`, stock);
export const sellStock = (data) => axios.post(`${API_URL}/stocks/sell`, data);