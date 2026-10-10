import { z } from 'zod';

// Mirrors CreateTestRequest/UpdateTestRequest — identical shape on
// backend. `questions` requires at least 1 item (backend @NotEmpty).
export const testSchema = z.object({
  title: z.string().min(1, 'Title is required.').max(150),
  description: z.string().max(500).optional().or(z.literal('')),
  courseId: z.coerce.number({ invalid_type_error: 'Course is required.' }).positive('Course is required.'),
  topicId: z.coerce.number({ invalid_type_error: 'Topic is required.' }).positive('Topic is required.'),
  testType: z.enum(['PRACTICE', 'FINAL'], { errorMap: () => ({ message: 'Test type is required.' }) }),
  passingPercentage: z.coerce.number().min(0).max(100),
  shuffleQuestions: z.boolean(),
  shuffleOptions: z.boolean(),
  timerEnabled: z.boolean(),
  durationMinutes: z.coerce.number().int().min(0).optional(),
  showExplanationAfterSubmission: z.boolean(),
  maxAttempts: z.coerce.number({ invalid_type_error: 'Max attempts is required.' }).int().min(1),
  startTime: z.string().optional().or(z.literal('')),
  endTime: z.string().optional().or(z.literal('')),
  questionIds: z.array(z.number()).min(1, 'Select at least one question.'),
});