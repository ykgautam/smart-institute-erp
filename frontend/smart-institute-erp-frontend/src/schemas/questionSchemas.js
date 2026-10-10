import { z } from 'zod';

// Mirrors QuestionOptionRequest exactly.
const optionSchema = z.object({
  optionText: z.string().min(1, 'Option text is required.').max(1000),
  correct: z.boolean(),
  displayOrder: z.number(),
});

// Mirrors CreateQuestionRequest/UpdateQuestionRequest — both identical
// on the backend. options must be 2-6 items (backend @Size constraint);
// we ADD a UX-only rule (at least one option marked correct) since an
// MCQ with no correct answer is meaningless, even though the backend
// DTO doesn't explicitly enforce it.
export const questionSchema = z
  .object({
    courseId: z.coerce.number({ invalid_type_error: 'Course is required.' }).positive('Course is required.'),
    topicId: z.coerce.number({ invalid_type_error: 'Topic is required.' }).positive('Topic is required.'),
    questionText: z.string().min(1, 'Question text is required.').max(2000),
    questionType: z.literal('MCQ'),
    difficulty: z.enum(['EASY', 'MEDIUM', 'HARD'], { errorMap: () => ({ message: 'Difficulty is required.' }) }),
    explanation: z.string().max(3000).optional().or(z.literal('')),
    marks: z.coerce.number({ invalid_type_error: 'Marks is required.' }).positive('Marks must be greater than zero.'),
    options: z.array(optionSchema).min(2, 'At least 2 options are required.').max(6, 'At most 6 options allowed.'),
  })
  .refine((data) => data.options.some((o) => o.correct), {
    message: 'At least one option must be marked correct.',
    path: ['options'],
  });