import apiClient from './apiClient';

// Public endpoint — no auth required (matches SecurityConfig's
// permitAll() list). Creates a NEW institute + its first owner user in
// a single call, per InstituteOnboardingRequest.
export const onboardingApi = {
  onboard: (payload) => apiClient.post('/institutes/onboard', payload),
};