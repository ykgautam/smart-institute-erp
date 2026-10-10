import { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem, Grid,
  CircularProgress, Box, FormControlLabel, Checkbox, Typography, List, ListItem, ListItemText,
} from '@mui/material';
import { testSchema } from '@schemas/testSchemas';
import { testApi } from '@services/api/testApi';
import { testQuestionApi } from '@services/api/testQuestionApi';
import { courseApi } from '@services/api/courseApi';
import { topicApi } from '@services/api/topicApi';
import { questionApi } from '@services/api/questionApi';
import { showSnackbar } from '@components/feedback/snackbarStore';

// Handles both create and edit of the test SHELL (settings + initial
// question selection). Fine-grained question reordering/adding after
// creation happens on the dedicated TestQuestionManagePage (Part 4) —
// this dialog only needs to satisfy the backend's required `questions`
// array at create/update time.
function TestFormDialog({ open, mode, initialData, onClose, onSuccess }) {
  const isEdit = mode === 'edit';

  const [courses, setCourses] = useState([]);
  const [allTopics, setAllTopics] = useState([]);
  const [availableQuestions, setAvailableQuestions] = useState([]);
  const [loadingOptions, setLoadingOptions] = useState(true);

  const {
    control, handleSubmit, reset, watch, setValue,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(testSchema),
    defaultValues: {
      title: '', description: '', courseId: '', topicId: '', testType: '',
      passingPercentage: 40, shuffleQuestions: false, shuffleOptions: false,
      timerEnabled: false, durationMinutes: 30, showExplanationAfterSubmission: true,
      maxAttempts: 1, startTime: '', endTime: '', questionIds: [],
    },
  });

  const watchedCourseId = watch('courseId');
  const watchedTopicId = watch('topicId');
  const watchedQuestionIds = watch('questionIds');

  useEffect(() => {
    if (!open) return;
    async function loadBase() {
      setLoadingOptions(true);
      try {
        const [courseRes, topicRes] = await Promise.all([
          courseApi.getCourses({ page: 0, size: 100, sortBy: 'courseName', direction: 'ASC' }),
          topicApi.getAll(),
        ]);
        setCourses(courseRes?.content || []);
        setAllTopics(topicRes || []);
      } catch {
        showSnackbar('Failed to load courses/topics.', 'error');
      } finally {
        setLoadingOptions(false);
      }
    }
    loadBase();
  }, [open]);

  // Load existing test's linked questions when editing, once the test
  // question API is available (Part 4's testQuestionApi).
  useEffect(() => {
    if (open) {
      if (isEdit && initialData) {
        (async () => {
          let currentQuestionIds = [];
          try {
            const linked = await testQuestionApi.getQuestions(initialData.id);
            currentQuestionIds = (linked || []).map((q) => q.questionId);
          } catch {
            // If this fails, edit still works — user just starts with an
            // empty selection and must re-pick questions.
          }
          reset({
            title: initialData.title,
            description: initialData.description || '',
            courseId: initialData.courseId,
            topicId: initialData.topicId,
            testType: initialData.testType,
            passingPercentage: initialData.passingPercentage,
            shuffleQuestions: initialData.shuffleQuestions,
            shuffleOptions: initialData.shuffleOptions,
            timerEnabled: initialData.timerEnabled,
            durationMinutes: initialData.durationMinutes || 0,
            showExplanationAfterSubmission: initialData.showExplanationAfterSubmission,
            maxAttempts: initialData.maxAttempts,
            startTime: initialData.startTime || '',
            endTime: initialData.endTime || '',
            questionIds: currentQuestionIds,
          });
        })();
      } else {
        reset({
          title: '', description: '', courseId: '', topicId: '', testType: '',
          passingPercentage: 40, shuffleQuestions: false, shuffleOptions: false,
          timerEnabled: false, durationMinutes: 30, showExplanationAfterSubmission: true,
          maxAttempts: 1, startTime: '', endTime: '', questionIds: [],
        });
      }
    }
  }, [open, isEdit, initialData, reset]);

  // Reload available questions whenever course+topic selection changes.
  useEffect(() => {
    if (!watchedCourseId || !watchedTopicId) {
      setAvailableQuestions([]);
      return;
    }
    async function loadQuestions() {
      try {
        const response = await questionApi.search({ courseId: watchedCourseId, topicId: watchedTopicId });
        setAvailableQuestions(response || []);
      } catch {
        showSnackbar('Failed to load questions for this topic.', 'error');
      }
    }
    loadQuestions();
  }, [watchedCourseId, watchedTopicId]);

  const toggleQuestion = (questionId) => {
    const current = watchedQuestionIds || [];
    const next = current.includes(questionId) ? current.filter((id) => id !== questionId) : [...current, questionId];
    setValue('questionIds', next, { shouldValidate: true });
  };

  const filteredTopics = allTopics.filter((t) => t.courseId === Number(watchedCourseId));

  const onSubmit = async (data) => {
    const payload = {
      title: data.title,
      description: data.description || undefined,
      courseId: data.courseId,
      topicId: data.topicId,
      testType: data.testType,
      passingPercentage: data.passingPercentage,
      shuffleQuestions: data.shuffleQuestions,
      shuffleOptions: data.shuffleOptions,
      timerEnabled: data.timerEnabled,
      durationMinutes: data.durationMinutes || undefined,
      showExplanationAfterSubmission: data.showExplanationAfterSubmission,
      maxAttempts: data.maxAttempts,
      startTime: data.startTime || undefined,
      endTime: data.endTime || undefined,
      questions: data.questionIds.map((id, idx) => ({ questionId: id, displayOrder: idx + 1 })),
    };

    try {
      if (isEdit) {
        await testApi.update(initialData.id, payload);
        showSnackbar('Test updated successfully.', 'success');
      } else {
        await testApi.create(payload);
        showSnackbar('Test created successfully.', 'success');
      }
      onSuccess();
      onClose();
    } catch (err) {
      showSnackbar(err.message || 'Failed to save test.', 'error');
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>{isEdit ? 'Edit Test' : 'Create Test'}</DialogTitle>
      <DialogContent>
        {loadingOptions ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress size={28} /></Box>
        ) : (
          <Grid container spacing={2} sx={{ mt: 0.5 }}>
            <Grid size={{ xs: 12, sm: 8 }}>
              <Controller name="title" control={control} render={({ field }) => (
                <TextField {...field} fullWidth label="Title" required error={!!errors.title} helperText={errors.title?.message} />
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 4 }}>
              <Controller name="testType" control={control} render={({ field }) => (
                <TextField {...field} select fullWidth label="Test Type" required error={!!errors.testType} helperText={errors.testType?.message}>
                  <MenuItem value="PRACTICE">Practice</MenuItem>
                  <MenuItem value="FINAL">Final</MenuItem>
                </TextField>
              )} />
            </Grid>
            <Grid size={12}>
              <Controller name="description" control={control} render={({ field }) => (
                <TextField {...field} fullWidth label="Description" multiline rows={2} error={!!errors.description} helperText={errors.description?.message} />
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller name="courseId" control={control} render={({ field }) => (
                <TextField {...field} select fullWidth label="Course" required disabled={isEdit} error={!!errors.courseId} helperText={errors.courseId?.message}
                  onChange={(e) => { field.onChange(e); setValue('topicId', ''); setValue('questionIds', []); }}>
                  {courses.map((c) => <MenuItem key={c.id} value={c.id}>{c.courseCode} — {c.courseName}</MenuItem>)}
                </TextField>
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller name="topicId" control={control} render={({ field }) => (
                <TextField {...field} select fullWidth label="Topic" required disabled={!watchedCourseId} error={!!errors.topicId} helperText={errors.topicId?.message}>
                  {filteredTopics.map((t) => <MenuItem key={t.id} value={t.id}>{t.name}</MenuItem>)}
                </TextField>
              )} />
            </Grid>

            <Grid size={{ xs: 12, sm: 4 }}>
              <Controller name="passingPercentage" control={control} render={({ field }) => (
                <TextField {...field} fullWidth type="number" label="Passing %" error={!!errors.passingPercentage} helperText={errors.passingPercentage?.message} />
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 4 }}>
              <Controller name="durationMinutes" control={control} render={({ field }) => (
                <TextField {...field} fullWidth type="number" label="Duration (minutes)" error={!!errors.durationMinutes} helperText={errors.durationMinutes?.message} />
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 4 }}>
              <Controller name="maxAttempts" control={control} render={({ field }) => (
                <TextField {...field} fullWidth type="number" label="Max Attempts" required error={!!errors.maxAttempts} helperText={errors.maxAttempts?.message} />
              )} />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller name="startTime" control={control} render={({ field }) => (
                <TextField {...field} fullWidth type="datetime-local" label="Start Time (optional)" slotProps={{ inputLabel: { shrink: true } }} />
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller name="endTime" control={control} render={({ field }) => (
                <TextField {...field} fullWidth type="datetime-local" label="End Time (optional)" slotProps={{ inputLabel: { shrink: true } }} />
              )} />
            </Grid>

            <Grid size={{ xs: 12, sm: 3 }}>
              <Controller name="shuffleQuestions" control={control} render={({ field }) => (
                <FormControlLabel control={<Checkbox checked={field.value} onChange={(e) => field.onChange(e.target.checked)} />} label="Shuffle Questions" />
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 3 }}>
              <Controller name="shuffleOptions" control={control} render={({ field }) => (
                <FormControlLabel control={<Checkbox checked={field.value} onChange={(e) => field.onChange(e.target.checked)} />} label="Shuffle Options" />
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 3 }}>
              <Controller name="timerEnabled" control={control} render={({ field }) => (
                <FormControlLabel control={<Checkbox checked={field.value} onChange={(e) => field.onChange(e.target.checked)} />} label="Timer Enabled" />
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 3 }}>
              <Controller name="showExplanationAfterSubmission" control={control} render={({ field }) => (
                <FormControlLabel control={<Checkbox checked={field.value} onChange={(e) => field.onChange(e.target.checked)} />} label="Show Explanation" />
              )} />
            </Grid>

            <Grid size={12}>
              <Typography variant="body2" fontWeight={600} sx={{ mb: 1 }}>
                Select Questions ({watchedQuestionIds?.length || 0} selected)
              </Typography>
              {!watchedCourseId || !watchedTopicId ? (
                <Typography variant="body2" color="text.secondary">Select a course and topic to see available questions.</Typography>
              ) : availableQuestions.length === 0 ? (
                <Typography variant="body2" color="text.secondary">No questions found for this topic. Add some in Question Bank first.</Typography>
              ) : (
                <List dense sx={{ maxHeight: 240, overflowY: 'auto', border: '1px solid', borderColor: 'divider', borderRadius: 1 }}>
                  {availableQuestions.map((q) => (
                    <ListItem key={q.id} sx={{ py: 0 }}>
                      <Checkbox checked={watchedQuestionIds?.includes(q.id)} onChange={() => toggleQuestion(q.id)} />
                      <ListItemText primary={q.questionText} secondary={`${q.marks} marks — ${q.difficulty}`} />
                    </ListItem>
                  ))}
                </List>
              )}
              {errors.questionIds && <Typography variant="caption" color="error">{errors.questionIds.message}</Typography>}
            </Grid>
          </Grid>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={isSubmitting}>Cancel</Button>
        <Button onClick={handleSubmit(onSubmit)} variant="contained" disabled={isSubmitting || loadingOptions}>
          {isSubmitting ? 'Saving...' : isEdit ? 'Save Changes' : 'Create Test'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default TestFormDialog;