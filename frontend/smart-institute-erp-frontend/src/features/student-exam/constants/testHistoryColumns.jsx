import { Chip, Button } from '@mui/material';

// Column definitions for the Test History table. Kept in its own file
// (rather than inline in the page component) so the page's JSX stays
// focused on layout, and columns can be reused/tweaked independently.
//
// `onAction` is a callback the page passes in — it decides what happens
// when the user clicks "Resume" or "View Result" for a given row. We
// don't hardcode navigation logic here because this file shouldn't need
// to know about React Router.
export function getTestHistoryColumns({ onAction }) {
  return [
    { field: 'testTitle', headerName: 'Test' },
    {
      field: 'status',
      headerName: 'Status',
      render: (row) => {
        // Color-code by status so the table is scannable at a glance:
        // in-progress attempts are "in progress" (orange), completed
        // ones are green if passed, red if failed.
        let color = 'default';
        if (row.status === 'IN_PROGRESS') color = 'warning';
        else if (row.passed) color = 'success';
        else if (row.status !== 'IN_PROGRESS') color = 'error';
        return <Chip label={row.status} size="small" color={color} />;
      },
    },
    {
      field: 'percentage',
      headerName: 'Score',
      // percentage is null/undefined while a test is still IN_PROGRESS
      // (nothing to score yet) — show a placeholder instead of "NaN%".
      render: (row) => (row.percentage != null ? `${Number(row.percentage).toFixed(1)}%` : '—'),
    },
    {
      field: 'obtainedMarks',
      headerName: 'Marks',
      render: (row) => (row.totalMarks != null ? `${row.obtainedMarks ?? 0} / ${row.totalMarks}` : '—'),
    },
    {
      field: 'startedAt',
      headerName: 'Started',
      render: (row) => (row.startedAt ? new Date(row.startedAt).toLocaleString('en-IN') : '—'),
    },
    {
      field: 'submittedAt',
      headerName: 'Submitted',
      render: (row) => (row.submittedAt ? new Date(row.submittedAt).toLocaleString('en-IN') : 'Not submitted'),
    },
    {
      field: 'actions',
      headerName: '',
      render: (row) => (
        <Button size="small" onClick={() => onAction(row)}>
          {row.status === 'IN_PROGRESS' ? 'Resume' : 'View Result'}
        </Button>
      ),
    },
  ];
}