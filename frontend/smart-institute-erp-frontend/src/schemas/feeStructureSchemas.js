import { z } from 'zod';

// Mirrors CreateFeeStructureRequest exactly (courseId + amount required,
// description optional). Update schema omits courseId since
// UpdateFeeStructureRequest doesn't include it — the course link is
// immutable after creation, matching Batch's immutable-field pattern.
export const createFeeStructureSchema = z.object({
  courseId: z.coerce.number({ invalid_type_error: 'Course is required.' }).positive('Course is required.'),
  amount: z.coerce.number({ invalid_type_error: 'Amount is required.' }).min(0, 'Amount cannot be negative.'),
  description: z.string().max(300, 'Max 300 characters.').optional().or(z.literal('')),
});

export const updateFeeStructureSchema = z.object({
  amount: z.coerce.number({ invalid_type_error: 'Amount is required.' }).min(0, 'Amount cannot be negative.'),
  description: z.string().max(300, 'Max 300 characters.').optional().or(z.literal('')),
});