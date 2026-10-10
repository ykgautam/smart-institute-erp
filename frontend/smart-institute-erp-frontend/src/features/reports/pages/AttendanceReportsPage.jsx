import ReportTabsPage from '../components/ReportTabsPage';
import GenericReportView from '../components/GenericReportView';
import { reportApi } from '@services/api/reportApi';
import { studentAttendanceReportColumns, attendanceReportColumns } from '../constants/reportColumns';

function AttendanceReportsPage() {
  return (
    <ReportTabsPage
      title="Attendance Reports"
      tabs={[
        {
          label: 'By Student',
          content: (
            <GenericReportView
              fetchFn={reportApi.getStudentAttendanceReport}
              columns={studentAttendanceReportColumns}
              filterFields={[
                { type: 'course', key: 'courseId', label: 'Course' },
                { type: 'batch', key: 'batchId', label: 'Batch' },
                { type: 'dateRange', fromKey: 'dateFrom', toKey: 'dateTo', label: 'Date' },
              ]}
              summaryFields={[
                { key: 'totalStudents', label: 'Total Students' },
                { key: 'averageAttendancePercentage', label: 'Avg Attendance', format: 'percent' },
                { key: 'totalPresentClasses', label: 'Total Present' },
                { key: 'totalAbsentClasses', label: 'Total Absent' },
              ]}
              getRowId={(row) => row.studentId}
              defaultSort={{ sortBy: 'studentName', sortDirection: 'ASC' }}
            />
          ),
        },
        {
          label: 'Overall',
          content: (
            <GenericReportView
              fetchFn={reportApi.getAttendanceReport}
              columns={attendanceReportColumns}
              filterFields={[
                { type: 'course', key: 'courseId', label: 'Course' },
                { type: 'batch', key: 'batchId', label: 'Batch' },
                { type: 'dateRange', fromKey: 'attendanceDateFrom', toKey: 'attendanceDateTo', label: 'Date' },
              ]}
              summaryFields={[
                { key: 'totalStudents', label: 'Total Students' },
                { key: 'averageAttendancePercentage', label: 'Avg Attendance', format: 'percent' },
                { key: 'lowAttendanceStudents', label: 'Low Attendance' },
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

export default AttendanceReportsPage;