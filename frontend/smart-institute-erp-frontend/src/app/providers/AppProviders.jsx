import { ThemeProvider, CssBaseline } from '@mui/material';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { theme } from '@theme/index';
import { store } from '@store/index';
import SnackbarProvider from '../../components/feedback/SnackbarProvider';

// All global providers in one place. Order matters: Redux and Router
// must wrap everything else since AuthBootstrap (rendered inside App)
// needs both useDispatch and useNavigate.
function AppProviders({ children }) {
  return (
    <Provider store={store}>
      <BrowserRouter >
        <ThemeProvider theme={theme}>
          <CssBaseline />
           {/* Mounted once, app-wide — see snackbarStore.js for how
              features trigger toasts without prop-drilling. */}
          <SnackbarProvider />
          {children}
        </ThemeProvider>
      </BrowserRouter>
    </Provider>
  );
}

export default AppProviders;