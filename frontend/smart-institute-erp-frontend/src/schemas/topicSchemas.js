import { z } from 'zod';

// Mirrors CreateTopicRequest/UpdateTopicRequest — Update adds `active`
// (boolean, required on backend); Create doesn't have it.
export const createTopicSchema = z.object({
  courseId: z.coerce.number({ invalid_type_error: 'Course is required.' }).positive('Course is required.'),
  name: z.string().min(1, 'Name is required.').max(150, 'Max 150 characters.'),
  description: z.string().max(500, 'Max 500 characters.').optional().or(z.literal('')),
  displayOrder: z.coerce.number().int().min(1, 'Must be at least 1.').max(9999),
});

export const updateTopicSchema = createTopicSchema.extend({
  active: z.boolean(),
});