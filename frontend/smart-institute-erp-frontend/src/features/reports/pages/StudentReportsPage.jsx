import ReportTabsPage from '../components/ReportTabsPage';
import GenericReportView from '../components/GenericReportView';
import { reportApi } from '@services/api/reportApi';
import { studentReportColumns, studentPerformanceColumns, studentAcademicReportColumns, STUDENT_STATUS } from '../constants/reportColumns';

function StudentReportsPage() {
  return (
    <ReportTabsPage
      title="Student Reports"
      tabs={[
        {
          label: 'Overview',
          content: (
            <GenericReportView
              fetchFn={reportApi.getStudentReport}
              columns={studentReportColumns}
              filterFields={[
                { type: 'course', key: 'courseId', label: 'Course' },
                { type: 'batch', key: 'batchId', label: 'Batch' },
                { type: 'select', key: 'status', label: 'Status', options: Object.values(STUDENT_STATUS) },
                { type: 'dateRange', fromKey: 'admissionDateFrom', toKey: 'admissionDateTo', label: 'Admission Date' },
              ]}
              summaryFields={[{ key: 'totalStudents', label: 'Total Students' }]}
              getRowId={(row) => row.studentId}
              defaultSort={{ sortBy: 'studentName', sortDirection: 'ASC' }}
            />
          ),
        },
        {
          label: 'Performance',
          content: (
            <GenericReportView
              fetchFn={reportApi.getStudentPerformanceReport}
              columns={studentPerformanceColumns}
              filterFields={[
                { type: 'course', key: 'courseId', label: 'Course' },
                { type: 'batch', key: 'batchId', label: 'Batch' },
                { type: 'dateRange', fromKey: 'submittedFrom', toKey: 'submittedTo', label: 'Submitted' },
              ]}
              getRowId={(row) => row.studentId}
              defaultSort={{ sortBy: 'studentName', sortDirection: 'ASC' }}
            />
          ),
        },
        {
          label: 'Academic',
          content: (
            <GenericReportView
              fetchFn={reportApi.getStudentAcademicReport}
              columns={studentAcademicReportColumns}
              filterFields={[
                { type: 'course', key: 'courseId', label: 'Course' },
                { type: 'batch', key: 'batchId', label: 'Batch' },
              ]}
              summaryFields={[
                { key: 'totalStudents', label: 'Total Students' },
                { key: 'averageAttendancePercentage', label: 'Avg Attendance', format: 'percent' },
                { key: 'averageTestPercentage', label: 'Avg Test %', format: 'percent' },
              ]}
              getRowId={(row) => row.studentId}
              defaultSort={{ sortBy: 'studentName', sortDirection: 'ASC' }}
            />
          ),
        },
      ]}
    />
  );
}

export default StudentReportsPage;