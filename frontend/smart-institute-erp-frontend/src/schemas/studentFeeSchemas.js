import { z } from 'zod';

// Mirrors AssignStudentFeeRequest exactly — studentId and feeStructureId
// are required (@NotNull on backend), discount is optional.
export const assignFeeSchema = z.object({
  studentId: z.coerce.number({ invalid_type_error: 'Student is required.' }).positive('Student is required.'),
  feeStructureId: z.coerce
    .number({ invalid_type_error: 'Fee structure is required.' })
    .positive('Fee structure is required.'),
  discount: z.coerce.number().min(0, 'Discount cannot be negative.').optional().or(z.literal('')),
});