import { z } from 'zod';

// Mirrors UpdateInstituteRequest exactly — all fields optional per the
// backend schema (only maxLength/pattern constraints, no @NotBlank).
export const instituteSchema = z.object({
  name: z.string().max(150, 'Max 150 characters.').optional().or(z.literal('')),
  email: z.string().email('Invalid email.').max(100).optional().or(z.literal('')),
  mobile: z.string().regex(/^[0-9]{10}$/, 'Must be exactly 10 digits.').optional().or(z.literal('')),
  landline: z.string().max(20).optional().or(z.literal('')),
  address: z.string().max(300).optional().or(z.literal('')),
  city: z.string().max(100).optional().or(z.literal('')),
  state: z.string().max(100).optional().or(z.literal('')),
  pincode: z.string().max(10).optional().or(z.literal('')),
  gstNumber: z.string().max(30).optional().or(z.literal('')),
  website: z.string().max(150).optional().or(z.literal('')),
  type: z.enum(['COMPUTER_INSTITUTE', 'SCHOOL', 'COLLEGE', 'TRAINING_CENTER', 'COACHING_CENTER']).optional(),
});