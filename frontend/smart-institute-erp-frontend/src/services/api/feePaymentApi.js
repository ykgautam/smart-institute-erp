import apiClient from './apiClient';

// Matches fee-payment-controller exactly (per openapi.json).
const FEE_PAYMENT_ENDPOINTS = {
  BASE: '/fee-payments',
  HISTORY: (studentFeeId) => `/fee-payments/history/${studentFeeId}`,
};

export const feePaymentApi = {
  // payload: { studentFeeId, amount, paymentMode, transactionReference?, remarks? }
  // — matches CollectFeeRequest exactly.
  collect: (payload) => apiClient.post(FEE_PAYMENT_ENDPOINTS.BASE, payload),
  getHistory: (studentFeeId) => apiClient.get(FEE_PAYMENT_ENDPOINTS.HISTORY(studentFeeId)),
};