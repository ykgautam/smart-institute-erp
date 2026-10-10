import ReportTabsPage from '../components/ReportTabsPage';
import GenericReportView from '../components/GenericReportView';
import { reportApi } from '@services/api/reportApi';
import { feeCollectionColumns, feeOutstandingColumns, studentFeeCollectionColumns, studentFeeAcademicColumns, FEE_STATUS } from '../constants/reportColumns';

function FeeReportsPage() {
  return (
    <ReportTabsPage
      title="Fee Reports"
      tabs={[
        {
          label: 'Collection',
          content: (
            <GenericReportView
              fetchFn={reportApi.getFeeCollectionReport}
              columns={feeCollectionColumns}
              filterFields={[
                { type: 'course', key: 'courseId', label: 'Course' },
                { type: 'batch', key: 'batchId', label: 'Batch' },
                { type: 'select', key: 'status', label: 'Status', options: Object.values(FEE_STATUS) },
                { type: 'dateRange', fromKey: 'feeDueDateFrom', toKey: 'feeDueDateTo', label: 'Due Date' },
              ]}
              summaryFields={[
                { key: 'totalStudents', label: 'Total Students' },
                { key: 'totalFee', label: 'Total Fee', format: 'currency' },
                { key: 'totalPaid', label: 'Total Paid', format: 'currency' },
                { key: 'totalPending', label: 'Total Pending', format: 'currency' },
              ]}
              getRowId={(row) => row.studentId}
              defaultSort={{ sortBy: 'studentName', sortDirection: 'ASC' }}
            />
          ),
        },
        {
          label: 'Outstanding',
          content: (
            <GenericReportView
              fetchFn={reportApi.getFeeOutstandingReport}
              columns={feeOutstandingColumns}
              filterFields={[
                { type: 'course', key: 'courseId', label: 'Course' },
                { type: 'batch', key: 'batchId', label: 'Batch' },
                { type: 'select', key: 'feeStatus', label: 'Status', options: Object.values(FEE_STATUS) },
                { type: 'dateRange', fromKey: 'dueDateFrom', toKey: 'dueDateTo', label: 'Due Date' },
              ]}
              summaryFields={[
                { key: 'totalStudents', label: 'Total Students' },
                { key: 'studentsWithOutstandingFees', label: 'With Outstanding' },
                { key: 'totalPendingAmount', label: 'Total Pending', format: 'currency' },
              ]}
              getRowId={(row) => row.studentId}
              defaultSort={{ sortBy: 'studentName', sortDirection: 'ASC' }}
            />
          ),
        },
        {
          label: 'By Student',
          content: (
            <GenericReportView
              fetchFn={reportApi.getStudentFeeCollectionReport}
              columns={studentFeeCollectionColumns}
              filterFields={[
                { type: 'course', key: 'courseId', label: 'Course' },
                { type: 'batch', key: 'batchId', label: 'Batch' },
                { type: 'select', key: 'feeStatus', label: 'Status', options: Object.values(FEE_STATUS) },
              ]}
              summaryFields={[
                { key: 'totalStudents', label: 'Total Students' },
                { key: 'studentsWithPayments', label: 'With Payments' },
                { key: 'totalPaidAmount', label: 'Total Paid', format: 'currency' },
              ]}
              getRowId={(row) => row.studentId}
              defaultSort={{ sortBy: 'studentName', sortDirection: 'ASC' }}
            />
          ),
        },
        {
          label: 'Academic + Fee',
          content: (
            <GenericReportView
              fetchFn={reportApi.getStudentFeeAcademicReport}
              columns={studentFeeAcademicColumns}
              filterFields={[
                { type: 'course', key: 'courseId', label: 'Course' },
                { type: 'batch', key: 'batchId', label: 'Batch' },
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

export default FeeReportsPage;