import { configureStore } from '@reduxjs/toolkit';
import authReducer from '@features/auth/authSlice';

// Root Redux store — only auth exists today. Feature slices are added
// here as they're built (e.g. notifications unread-count, in later Parts).
export const store = configureStore({
  reducer: {
    auth: authReducer,
  },
});