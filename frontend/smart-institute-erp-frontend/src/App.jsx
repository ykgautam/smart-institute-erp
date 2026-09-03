import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from '@features/auth/pages/LoginPage';
import AuthBootstrap from '@features/auth/AuthBootstrap';
import ProtectedRoute from '@routes/ProtectedRoute';
import AppLayout from '@components/layout/AppLayout';

import StudentListPage from '@features/students/pages/StudentListPage';
import CourseListPage from '@features/courses/pages/CourseListPage';
import BatchListPage from '@features/batches/pages/BatchListPage';
import FeeStructureListPage from '@features/fees/pages/FeeStructureListPage';
import StudentFeeListPage from '@features/fees/pages/StudentFeeListPage';
import MarkAttendancePage from '@features/attendance/pages/MarkAttendancePage';
import BatchAttendanceRecordsPage from '@features/attendance/pages/BatchAttendanceRecordsPage';
import StudentAttendanceHistoryPage from '@features/attendance/pages/StudentAttendanceHistoryPage';


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
            <Route path="/batches" element={<BatchListPage />} />

            <Route path="/fees/structures" element={<FeeStructureListPage />} />
            <Route path="/fees/students" element={<StudentFeeListPage />} />

            <Route path="/attendance/mark" element={<MarkAttendancePage />} />
            <Route path="/attendance/records" element={<BatchAttendanceRecordsPage />} />
            <Route path="/attendance/history" element={<StudentAttendanceHistoryPage />} />
          </Route>
        </Route>

        <Route path="/" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </>
  );
}

export default App;