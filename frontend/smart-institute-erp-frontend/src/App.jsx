import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from '@features/auth/pages/LoginPage';
import AuthBootstrap from '@features/auth/AuthBootstrap';
import ProtectedRoute from '@routes/ProtectedRoute';
import AppLayout from '@components/layout/AppLayout';

import StudentListPage from '@features/students/pages/StudentListPage';
import CourseListPage from '@features/courses/pages/CourseListPage';

// Temporary placeholders — replaced as each feature module is built
// in upcoming Sprints. They prove routing/layout wiring only.
function DashboardPlaceholder() {
  return <div>Dashboard (placeholder)</div>;
}

function App() {
  return (
    <>
      <AuthBootstrap />
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        {/* All authenticated routes share ProtectedRoute (auth guard)
            AND AppLayout (sidebar/topbar shell) via nested layout routes. */}
        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            <Route path="/dashboard" element={<DashboardPlaceholder />} />
            <Route path="/students" element={<StudentListPage />} />
            <Route path="/courses" element={<CourseListPage />} />
          </Route>
        </Route>

        <Route path="/" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </>
  );
}

export default App;