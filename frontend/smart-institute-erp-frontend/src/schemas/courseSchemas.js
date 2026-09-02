import { z } from 'zod';

// Mirrors CreateCourseRequest/UpdateCourseRequest exactly — both DTOs
// have IDENTICAL validation constraints on the backend (unlike Student,
// where Update relaxes most fields), so we use ONE schema for both modes.
export const courseSchema = z.object({
  courseCode: z.string().min(1, 'Course code is required.').max(30, 'Max 30 characters.'),
  courseName: z.string().min(1, 'Course name is required.').max(150, 'Max 150 characters.'),
  description: z.string().max(500, 'Max 500 characters.').optional().or(z.literal('')),
  duration: z.coerce
    .number({ invalid_type_error: 'Duration is required.' })
    .positive('Duration must be greater than zero.'),
  durationType: z.enum(['DAYS', 'WEEKS', 'MONTHS', 'YEARS'], {
    errorMap: () => ({ message: 'Duration type is required.' }),
  }),
  // @Digits(integer=10, fraction=2) on backend — precise fraction rounding
  // is a backend/DB concern, not replicated exactly on the frontend.
  fee: z.coerce.number({ invalid_type_error: 'Fee is required.' }).min(0, 'Fee cannot be negative.'),
});