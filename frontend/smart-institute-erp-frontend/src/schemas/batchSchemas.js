import { z } from 'zod';

// Mirrors CreateBatchRequest/UpdateBatchRequest exactly — both DTOs have
// IDENTICAL constraints on the backend (like Course, unlike Student).
// facultyId is the only optional field; everything else is @NotNull/@NotBlank.
export const batchSchema = z
  .object({
    batchCode: z.string().min(1, 'Batch code is required.'),
    batchName: z.string().min(1, 'Batch name is required.'),
    courseId: z.coerce.number({ invalid_type_error: 'Course is required.' }).positive('Course is required.'),
    facultyId: z.coerce.number().optional().or(z.literal('')),
    startDate: z.string().min(1, 'Start date is required.'),
    endDate: z.string().min(1, 'End date is required.'),
    startTime: z.string().min(1, 'Start time is required.'),
    endTime: z.string().min(1, 'End time is required.'),
    capacity: z.coerce
      .number({ invalid_type_error: 'Capacity is required.' })
      .int('Capacity must be a whole number.')
      .min(1, 'Capacity must be at least 1.'),
  })
  // Mirrors BatchServiceImpl.validateDates() as a UX pre-check — backend
  // remains authoritative and will reject invalid dates regardless.
  .refine((data) => new Date(data.endDate) >= new Date(data.startDate), {
    message: 'End date cannot be before start date.',
    path: ['endDate'],
  })
  .refine(
    (data) => {
      if (data.startDate !== data.endDate) return true;
      // Same-day batch: end time must not be before start time.
      return data.endTime >= data.startTime;
    },
    { message: 'End time cannot be before start time on the same day.', path: ['endTime'] },
  );