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
import DashboardRouter from '@features/dashboard/pages/DashboardRouter';
import UserListPage from '@features/users/pages/UserListPage';
import InstituteDetailPage from '@features/institute/pages/InstituteDetailPage';
import NotificationListPage from '@features/notifications/pages/NotificationListPage';
import AuditLogListPage from '@features/audit/pages/AuditLogListPage';
import TopicListPage from '@features/topics/pages/TopicListPage';
import QuestionListPage from '@features/questions/pages/QuestionListPage';
import TestListPage from '@features/tests/pages/TestListPage';
import TestQuestionManagePage from '@features/tests/pages/TestQuestionManagePage';
import TestPerformanceReportPage from '@features/tests/pages/TestPerformanceReportPage';
import MyTestsPage from '@features/student-exam/pages/MyTestsPage';
import TestTakingPage from '@features/student-exam/pages/TestTakingPage';
import TestResultPage from '@features/student-exam/pages/TestResultPage';
import StudentReportsPage from '@features/reports/pages/StudentReportsPage';
import AttendanceReportsPage from '@features/reports/pages/AttendanceReportsPage';
import FeeReportsPage from '@features/reports/pages/FeeReportsPage';
import CourseBatchPerformancePage from '@features/reports/pages/CourseBatchPerformancePage';
import TestWiseReportPage from '@features/reports/pages/TestWiseReportPage';
import ChangePasswordPage from '@features/auth/pages/ChangePasswordPage';
import ErrorBoundary from '@components/common/ErrorBoundary';
import NotFoundPage from '@components/common/NotFoundPage';
import OnboardingPage from '@features/auth/pages/OnboardingPage';
import MyProfilePage from '@features/auth/pages/MyProfilePage';
import StudentPerformancePage from '@features/student-exam/pages/StudentPerformancePage';

function App() {
  return (
    <>
    <ErrorBoundary>
      <AuthBootstrap />
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/onboard" element={<OnboardingPage />} />

        {/* All authenticated routes share ProtectedRoute (auth guard)
            AND AppLayout (sidebar/topbar shell) via nested layout routes. */}
        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            {/* <Route path="/dashboard" element={<DashboardPlaceholder />} /> */}
            <Route path="/students" element={<StudentListPage />} />
            <Route path="/courses" element={<CourseListPage />} />
            <Route path="/batches" element={<BatchListPage />} />

            <Route path="/fees/structures" element={<FeeStructureListPage />} />
            <Route path="/fees/students" element={<StudentFeeListPage />} />

            <Route path="/attendance/mark" element={<MarkAttendancePage />} />
            <Route path="/attendance/records" element={<BatchAttendanceRecordsPage />} />
            <Route path="/attendance/history" element={<StudentAttendanceHistoryPage />} />

            <Route path="/dashboard" element={<DashboardRouter />} />
            <Route path="/users" element={<UserListPage />} />
            <Route path="/institute" element={<InstituteDetailPage />} />
            <Route path="/notifications" element={<NotificationListPage />} />
            <Route path="/audit-logs" element={<AuditLogListPage />} />

            <Route path="/exams/topics" element={<TopicListPage />} />
            <Route path="/exams/questions" element={<QuestionListPage />} />
            <Route path="/exams/tests" element={<TestListPage />} />
            <Route path="/exams/tests/:testId/questions" element={<TestQuestionManagePage />} />
            <Route path="/exams/reports" element={<TestPerformanceReportPage />} />
            <Route path="/exams/my-tests" element={<MyTestsPage />} />
            <Route path="/exams/take/:studentTestId" element={<TestTakingPage />} />
            <Route path="/exams/result/:studentTestId" element={<TestResultPage />} />
            <Route path="/exams/my-performance" element={<StudentPerformancePage />} />

            <Route path="/reports/students" element={<StudentReportsPage />} />
            <Route path="/reports/attendance" element={<AttendanceReportsPage />} />
            <Route path="/reports/fees" element={<FeeReportsPage />} />
            <Route path="/reports/performance" element={<CourseBatchPerformancePage />} />
            <Route path="/reports/tests" element={<TestWiseReportPage />} />

            <Route path="/change-password" element={<ChangePasswordPage />} />
            <Route path="/my-profile" element={<MyProfilePage />} />
            
          </Route>
        </Route>

        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </ErrorBoundary>
    </>
  );
}

export default App;