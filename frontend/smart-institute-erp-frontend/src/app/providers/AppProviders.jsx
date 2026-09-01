import { ThemeProvider, CssBaseline } from '@mui/material';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { theme } from '@theme/index';
import { store } from '@store/index';

// All global providers in one place. Order matters: Redux and Router
// must wrap everything else since AuthBootstrap (rendered inside App)
// needs both useDispatch and useNavigate.
function AppProviders({ children }) {
  return (
    <Provider store={store}>
      <BrowserRouter >
        <ThemeProvider theme={theme}>
          <CssBaseline />
          {children}
        </ThemeProvider>
      </BrowserRouter>
    </Provider>
  );
}

export default AppProviders;