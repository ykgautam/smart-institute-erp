import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Typography, Box, Paper, Grid, Card, CardContent, Chip, List, ListItem, ListItemText, CircularProgress } from '@mui/material';
import ErrorState from '@components/tables/ErrorState';
import { studentTestApi } from '@services/api/studentTestApi';

function TestResultPage() {
  const { studentTestId } = useParams();
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function load() {
      setLoading(true);
      setError(null);
      try {
        const response = await studentTestApi.getResult(studentTestId);
        setResult(response);
      } catch (err) {
        setError(err.message || 'Failed to load result.');
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [studentTestId]);

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}><CircularProgress /></Box>;
  if (error) return <Paper variant="outlined"><ErrorState message={error} /></Paper>;
  if (!result) return null;

  const kpis = [
    { label: 'Score', value: `${Number(result.percentage).toFixed(1)}%` },
    { label: 'Correct', value: result.correctAnswers },
    { label: 'Wrong', value: result.wrongAnswers },
    { label: 'Unanswered', value: result.unansweredQuestions },
  ];

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
        <Typography variant="h2">{result.testTitle}</Typography>
        <Chip label={result.passed ? 'PASSED' : 'FAILED'} color={result.passed ? 'success' : 'error'} />
      </Box>

      <Grid container spacing={2} sx={{ mb: 4 }}>
        {kpis.map((k) => (
          <Grid key={k.label} size={{ xs: 6, sm: 3 }}>
            <Card variant="outlined">
              <CardContent sx={{ textAlign: 'center' }}>
                <Typography variant="h3">{k.value}</Typography>
                <Typography variant="body2" color="text.secondary">{k.label}</Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
      <Typography variant="h3" sx={{ mb: 1 }}>Question Review</Typography>
      <Paper variant="outlined">
        {!result.questions || result.questions.length === 0 ? (
          <Box sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              Detailed question-by-question review isn't available for this result yet.
            </Typography>
          </Box>
        ) : (
          <List>
            {result.questions.map((q, idx) => (
              <ListItem key={q.questionId} divider>
                <ListItemText
                  primary={`${idx + 1}. ${q.question}`}
                  secondary={
                    <>
                      Your answer: {q.selectedOption || '—'} {q.correct ? '' : `(Correct: ${q.correctOption})`} — {q.marksObtained} marks
                    </>
                  }
                />
                <Chip label={q.correct ? 'Correct' : 'Incorrect'} size="small" color={q.correct ? 'success' : 'error'} />
              </ListItem>
            ))}
          </List>
        )}
      </Paper>
      {/* <Typography variant="h3" sx={{ mb: 1 }}>Question Review</Typography>
      <Paper variant="outlined">
        <List>
          {(result.questions || []).map((q, idx) => (
            <ListItem key={q.questionId} divider>
              <ListItemText
                primary={`${idx + 1}. ${q.question}`}
                secondary={
                  <>
                    Your answer: {q.selectedOption || '—'} {q.correct ? '' : `(Correct: ${q.correctOption})`} — {q.marksObtained} marks
                  </>
                }
              />
              <Chip label={q.correct ? 'Correct' : 'Incorrect'} size="small" color={q.correct ? 'success' : 'error'} />
            </ListItem>
          ))}
        </List>
      </Paper> */}
    </Box>
  );
}

export default TestResultPage;