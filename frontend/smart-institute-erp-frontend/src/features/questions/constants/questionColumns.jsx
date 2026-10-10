import { Chip } from '@mui/material';

function difficultyColor(d) {
  if (d === 'EASY') return 'success';
  if (d === 'MEDIUM') return 'warning';
  return 'error';
}

export function getQuestionColumns() {
  return [
    {
      field: 'questionText',
      headerName: 'Question',
      render: (row) => (row.questionText?.length > 80 ? `${row.questionText.slice(0, 80)}...` : row.questionText),
    },
    { field: 'courseName', headerName: 'Course' },
    { field: 'topicName', headerName: 'Topic' },
    { field: 'difficulty', headerName: 'Difficulty', render: (row) => <Chip label={row.difficulty} size="small" color={difficultyColor(row.difficulty)} /> },
    { field: 'marks', headerName: 'Marks' },
    { field: 'options', headerName: 'Options', render: (row) => row.options?.length ?? 0 },
  ];
}