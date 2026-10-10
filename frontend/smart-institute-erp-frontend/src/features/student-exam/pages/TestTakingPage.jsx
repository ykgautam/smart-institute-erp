import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, Box, Paper, RadioGroup, FormControlLabel, Radio, Button, CircularProgress, Chip, LinearProgress } from '@mui/material';
import ErrorState from '@components/tables/ErrorState';
import { studentTestApi } from '@services/api/studentTestApi';
import { showSnackbar } from '@components/feedback/snackbarStore';

// Core exam-taking UI. Answers auto-save on selection (one API call per
// change) rather than requiring an explicit "save" step per question —
// reduces the chance of losing progress if the student navigates away.
//
// NOTE: no visible countdown timer here even when the test has
// timerEnabled — StudentTestResponse doesn't expose durationMinutes,
// and fetching the full TestResponse would require the student to have
// access to an admin-facing endpoint whose authorization isn't
// confirmed. Flagged as a Sprint 13 limitation; the backend enforces
// auto-submission server-side regardless.
function TestTakingPage() {
  const { studentTestId } = useParams();
  const navigate = useNavigate();

  const [studentTest, setStudentTest] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState({}); // { questionId: selectedOptionId }
  const [currentIndex, setCurrentIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [testRes, questionsRes, answersRes] = await Promise.all([
        studentTestApi.getStudentTest(studentTestId),
        studentTestApi.getQuestions(studentTestId),
        studentTestApi.getSavedAnswers(studentTestId),
      ]);
      setStudentTest(testRes);
      setQuestions(questionsRes || []);
      const answerMap = {};
      (answersRes || []).forEach((a) => {
        answerMap[a.questionId] = a.selectedOptionId;
      });
      setAnswers(answerMap);
    } catch (err) {
      setError(err.message || 'Failed to load the test.');
    } finally {
      setLoading(false);
    }
  }, [studentTestId]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    loadData();
  }, [loadData]);

  const handleSelectOption = async (questionId, optionId) => {
    setAnswers((prev) => ({ ...prev, [questionId]: optionId }));
    setSaving(true);
    try {
      await studentTestApi.saveAnswer(studentTestId, { questionId, selectedOptionId: optionId });
    } catch (err) {
      showSnackbar(err.message || 'Failed to save your answer. Please retry.', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      await studentTestApi.submitTest(studentTestId);
      showSnackbar('Test submitted successfully.', 'success');
      navigate(`/exams/result/${studentTestId}`, { replace: true });
    } catch (err) {
      showSnackbar(err.message || 'Failed to submit test.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}><CircularProgress /></Box>;
  if (error) return <Paper variant="outlined"><ErrorState message={error} onRetry={loadData} /></Paper>;
  if (questions.length === 0) return <Typography color="text.secondary">No questions found for this test.</Typography>;

  const currentQuestion = questions[currentIndex];
  const answeredCount = Object.keys(answers).length;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h2">{studentTest?.testTitle}</Typography>
        <Chip label={`${answeredCount} / ${questions.length} answered`} color={answeredCount === questions.length ? 'success' : 'default'} />
      </Box>

      <LinearProgress variant="determinate" value={((currentIndex + 1) / questions.length) * 100} sx={{ mb: 3 }} />

      <Paper variant="outlined" sx={{ p: 3, mb: 3 }}>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
          Question {currentIndex + 1} of {questions.length} — {currentQuestion.marks} marks
        </Typography>
        <Typography variant="body1" sx={{ mb: 2 }}>{currentQuestion.questionText}</Typography>

        <RadioGroup
          value={answers[currentQuestion.id] ?? ''}
          onChange={(e) => handleSelectOption(currentQuestion.id, Number(e.target.value))}
        >
          {currentQuestion.options.map((opt) => (
            <FormControlLabel key={opt.id} value={opt.id} control={<Radio />} label={opt.optionText} disabled={saving} />
          ))}
        </RadioGroup>
      </Paper>

      <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
        <Button disabled={currentIndex === 0} onClick={() => setCurrentIndex((i) => i - 1)}>
          Previous
        </Button>
        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', justifyContent: 'center' }}>
          {questions.map((q, idx) => (
            <Chip
              key={q.id}
              label={idx + 1}
              size="small"
              onClick={() => setCurrentIndex(idx)}
              color={answers[q.id] ? 'success' : idx === currentIndex ? 'primary' : 'default'}
              variant={idx === currentIndex ? 'filled' : 'outlined'}
              sx={{ cursor: 'pointer' }}
            />
          ))}
        </Box>
        {currentIndex < questions.length - 1 ? (
          <Button variant="contained" onClick={() => setCurrentIndex((i) => i + 1)}>
            Next
          </Button>
        ) : (
          <Button variant="contained" color="success" disabled={submitting} onClick={handleSubmit}>
            {submitting ? 'Submitting...' : 'Submit Test'}
          </Button>
        )}
      </Box>
    </Box>
  );
}

export default TestTakingPage;