import { useEffect, useState } from 'react';
import { useForm, Controller, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem, Grid,
  CircularProgress, Box, Checkbox, IconButton, Typography, FormHelperText,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import { questionSchema } from '@schemas/questionSchemas';
import { questionApi } from '@services/api/questionApi';
import { courseApi } from '@services/api/courseApi';
import { topicApi } from '@services/api/topicApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { normalizeFieldErrors } from '@schemas/studentSchemas';
import { QUESTION_DIFFICULTY } from '@constants/enums';

function QuestionFormDialog({ open, mode, initialData, onClose, onSuccess }) {
  const isEdit = mode === 'edit';

  const [courses, setCourses] = useState([]);
  const [allTopics, setAllTopics] = useState([]);
  const [loadingOptions, setLoadingOptions] = useState(true);

  const {
    control, handleSubmit, reset, watch, setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(questionSchema),
    defaultValues: {
      courseId: '', topicId: '', questionText: '', questionType: 'MCQ', difficulty: '',
      explanation: '', marks: 1,
      options: [
        { optionText: '', correct: false, displayOrder: 1 },
        { optionText: '', correct: false, displayOrder: 2 },
      ],
    },
  });

  const { fields, append, remove } = useFieldArray({ control, name: 'options' });
  const watchedCourseId = watch('courseId');

  useEffect(() => {
    if (!open) return;
    async function loadOptions() {
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
    loadOptions();
  }, [open]);

  useEffect(() => {
    if (open) {
      reset(
        isEdit && initialData
          ? {
              courseId: initialData.courseId,
              topicId: initialData.topicId,
              questionText: initialData.questionText,
              questionType: 'MCQ',
              difficulty: initialData.difficulty,
              explanation: initialData.explanation || '',
              marks: initialData.marks,
              options: initialData.options.map((o) => ({ optionText: o.optionText, correct: o.correct, displayOrder: o.displayOrder })),
            }
          : {
              courseId: '', topicId: '', questionText: '', questionType: 'MCQ', difficulty: '',
              explanation: '', marks: 1,
              options: [
                { optionText: '', correct: false, displayOrder: 1 },
                { optionText: '', correct: false, displayOrder: 2 },
              ],
            },
      );
    }
  }, [open, isEdit, initialData, reset]);

  // Topics filtered to the currently-selected course only.
  const filteredTopics = allTopics.filter((t) => t.courseId === Number(watchedCourseId));

  const onSubmit = async (data) => {
    // Reassign displayOrder to match current field order (in case rows
    // were added/removed, indices must stay contiguous).
    const payload = { ...data, options: data.options.map((o, idx) => ({ ...o, displayOrder: idx + 1 })) };

    try {
      if (isEdit) {
        await questionApi.update(initialData.id, payload);
        showSnackbar('Question updated successfully.', 'success');
      } else {
        await questionApi.create(payload);
        showSnackbar('Question created successfully.', 'success');
      }
      onSuccess();
      onClose();
    } catch (err) {
      const fieldErrors = normalizeFieldErrors(err.errors);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => setError(field, { type: 'server', message }));
      } else {
        showSnackbar(err.message || 'Failed to save question.', 'error');
      }
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>{isEdit ? 'Edit Question' : 'Add Question'}</DialogTitle>
      <DialogContent>
        {loadingOptions ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress size={28} /></Box>
        ) : (
          <Grid container spacing={2} sx={{ mt: 0.5 }}>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller name="courseId" control={control} render={({ field }) => (
                <TextField {...field} select fullWidth label="Course" required error={!!errors.courseId} helperText={errors.courseId?.message}>
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
            <Grid size={12}>
              <Controller name="questionText" control={control} render={({ field }) => (
                <TextField {...field} fullWidth label="Question Text" required multiline rows={2} error={!!errors.questionText} helperText={errors.questionText?.message} />
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller name="difficulty" control={control} render={({ field }) => (
                <TextField {...field} select fullWidth label="Difficulty" required error={!!errors.difficulty} helperText={errors.difficulty?.message}>
                  <MenuItem value={QUESTION_DIFFICULTY.EASY}>Easy</MenuItem>
                  <MenuItem value={QUESTION_DIFFICULTY.MEDIUM}>Medium</MenuItem>
                  <MenuItem value={QUESTION_DIFFICULTY.HARD}>Hard</MenuItem>
                </TextField>
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller name="marks" control={control} render={({ field }) => (
                <TextField {...field} fullWidth type="number" label="Marks" required error={!!errors.marks} helperText={errors.marks?.message} />
              )} />
            </Grid>
            <Grid size={12}>
              <Controller name="explanation" control={control} render={({ field }) => (
                <TextField {...field} fullWidth label="Explanation (optional)" multiline rows={2} error={!!errors.explanation} helperText={errors.explanation?.message} />
              )} />
            </Grid>

            <Grid size={12}>
              <Typography variant="body2" fontWeight={600} sx={{ mb: 1 }}>Options (mark the correct one)</Typography>
              {fields.map((field, index) => (
                <Box key={field.id} sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                  <Controller
                    name={`options.${index}.correct`}
                    control={control}
                    render={({ field: cf }) => <Checkbox checked={cf.value} onChange={(e) => cf.onChange(e.target.checked)} />}
                  />
                  <Controller
                    name={`options.${index}.optionText`}
                    control={control}
                    render={({ field: tf }) => (
                      <TextField {...tf} fullWidth size="small" placeholder={`Option ${index + 1}`} error={!!errors.options?.[index]?.optionText} />
                    )}
                  />
                  <IconButton size="small" disabled={fields.length <= 2} onClick={() => remove(index)}>
                    <DeleteOutlinedIcon fontSize="small" />
                  </IconButton>
                </Box>
              ))}
              {errors.options && <FormHelperText error>{errors.options.message || errors.options.root?.message}</FormHelperText>}
              <Button
                size="small"
                startIcon={<AddIcon />}
                disabled={fields.length >= 6}
                onClick={() => append({ optionText: '', correct: false, displayOrder: fields.length + 1 })}
              >
                Add Option
              </Button>
            </Grid>
          </Grid>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={isSubmitting}>Cancel</Button>
        <Button onClick={handleSubmit(onSubmit)} variant="contained" disabled={isSubmitting || loadingOptions}>
          {isSubmitting ? 'Saving...' : isEdit ? 'Save Changes' : 'Create Question'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default QuestionFormDialog;