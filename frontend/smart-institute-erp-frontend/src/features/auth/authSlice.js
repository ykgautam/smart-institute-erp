import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { authApi } from '@services/api/authApi';

// Restores auth state on load so a page refresh doesn't force re-login
// while the token is still valid — actual validity is confirmed by
// fetchCurrentUser, dispatched from AuthBootstrap on app mount.
const persistedToken = localStorage.getItem('accessToken');

const initialState = {
  user: null, // Populated via GET /auth/me — shape pending backend confirmation
  isAuthenticated: !!persistedToken,
  status: 'idle', // idle | loading | succeeded | failed
  error: null,
};

// Authenticates and persists tokens. Does NOT populate `user` — per the
// backend contract, LoginResponse contains tokens only.
export const loginUser = createAsyncThunk(
  'auth/loginUser',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await authApi.login(credentials);
      localStorage.setItem('accessToken', response.accessToken);
      localStorage.setItem('refreshToken', response.refreshToken);
      return response;
    } catch (error) {
      return rejectWithValue(error.message || 'Login failed.');
    }
  },
);

// Fetches the authenticated user's profile. Called after login, and on
// app bootstrap when a token survives a page refresh.
export const fetchCurrentUser = createAsyncThunk(
  'auth/fetchCurrentUser',
  async (_, { rejectWithValue }) => {
    try {
      return await authApi.getCurrentUser();
    } catch (error) {
      return rejectWithValue(error.message || 'Failed to fetch user profile.');
    }
  },
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    // Backend logout is a stateless no-op (see AuthServiceImpl), so
    // clearing client state IS the logout operation.
    logout: (state) => {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      state.user = null;
      state.isAuthenticated = false;
      state.status = 'idle';
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(loginUser.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state) => {
        state.isAuthenticated = true;
        state.status = 'succeeded';
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
        state.isAuthenticated = false;
      })
      .addCase(fetchCurrentUser.fulfilled, (state, action) => {
        state.user = action.payload;
      })
      .addCase(fetchCurrentUser.rejected, (state) => {
        // A failed /auth/me on bootstrap means the persisted token is
        // stale/invalid — treat the session as logged out.
        state.user = null;
        state.isAuthenticated = false;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
      });
  },
});

export const { logout } = authSlice.actions;
export default authSlice.reducer;