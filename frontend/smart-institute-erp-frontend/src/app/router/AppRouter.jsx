import { BrowserRouter, Route, Routes } from 'react-router-dom';

import HomePage from '../../pages/HomePage.jsx';
import NotFoundPage from '../../pages/NotFoundPage.jsx';

function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />

        {/* Catch-all route prevents unknown URLs from rendering a blank screen. */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRouter;