import ReportTabsPage from '../components/ReportTabsPage';
import GenericReportView from '../components/GenericReportView';
import { reportApi } from '@services/api/reportApi';
import { coursePerformanceColumns, batchPerformanceColumns } from '../constants/reportColumns';

function CourseBatchPerformancePage() {
  return (
    <ReportTabsPage
      title="Course & Batch Performance"
      tabs={[
        {
          label: 'Course Performance',
          content: (
            <GenericReportView
              fetchFn={reportApi.getCoursePerformanceReport}
              columns={coursePerformanceColumns}
              filterFields={[
                { type: 'course', key: 'courseId', label: 'Course' },
                { type: 'dateRange', fromKey: 'submittedFrom', toKey: 'submittedTo', label: 'Submitted' },
              ]}
              getRowId={(row) => row.courseId}
              defaultSort={{ sortBy: 'courseName', sortDirection: 'ASC' }}
            />
          ),
        },
        {
          label: 'Batch Performance',
          content: (
            <GenericReportView
              fetchFn={reportApi.getBatchPerformanceReport}
              columns={batchPerformanceColumns}
              filterFields={[
                { type: 'course', key: 'courseId', label: 'Course' },
                { type: 'batch', key: 'batchId', label: 'Batch' },
                { type: 'dateRange', fromKey: 'submittedFrom', toKey: 'submittedTo', label: 'Submitted' },
              ]}
              getRowId={(row) => row.batchId}
              defaultSort={{ sortBy: 'batchName', sortDirection: 'ASC' }}
            />
          ),
        },
      ]}
    />
  );
}

export default CourseBatchPerformancePage;