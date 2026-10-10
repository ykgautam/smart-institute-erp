import { Typography, Box } from '@mui/material';
import GenericReportView from '../components/GenericReportView';
import { reportApi } from '@services/api/reportApi';
import { testWiseColumns } from '../constants/reportColumns';

function TestWiseReportPage() {
  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>Test-Wise Performance Report</Typography>
      <GenericReportView
        fetchFn={reportApi.getTestWisePerformanceReport}
        columns={testWiseColumns}
        filterFields={[
          { type: 'course', key: 'courseId', label: 'Course' },
          { type: 'batch', key: 'batchId', label: 'Batch' },
          { type: 'dateRange', fromKey: 'submittedFrom', toKey: 'submittedTo', label: 'Submitted' },
        ]}
        getRowId={(row) => row.testId}
        defaultSort={{ sortBy: 'testName', sortDirection: 'ASC' }}
      />
    </Box>
  );
}

export default TestWiseReportPage;