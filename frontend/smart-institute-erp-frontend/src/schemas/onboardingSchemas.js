import { z } from 'zod';

// Mirrors InstituteOnboardingRequest exactly: institute + owner nested
// objects, matching CreateInstituteRequest and OwnerRegistrationRequest.
export const onboardingSchema = z.object({
  institute: z.object({
    name: z.string().min(1, 'Institute name is required.').max(150),
    type: z.enum(['COMPUTER_INSTITUTE', 'SCHOOL', 'COLLEGE', 'TRAINING_CENTER', 'COACHING_CENTER'], {
      errorMap: () => ({ message: 'Institute type is required.' }),
    }),
    email: z.string().email('Invalid email.').max(100).optional().or(z.literal('')),
    mobile: z.string().regex(/^[0-9]{10}$/, 'Must be exactly 10 digits.').optional().or(z.literal('')),
    address: z.string().max(300).optional().or(z.literal('')),
    city: z.string().max(100).optional().or(z.literal('')),
    state: z.string().max(100).optional().or(z.literal('')),
  }),
  owner: z.object({
    firstName: z.string().min(1, 'First name is required.').max(100),
    lastName: z.string().max(100).optional().or(z.literal('')),
    email: z.string().min(1, 'Email is required.').email('Invalid email.'),
    mobile: z.string().regex(/^[0-9]{10}$/, 'Must be exactly 10 digits.').optional().or(z.literal('')),
    password: z.string().min(1, 'Password is required.'),
    gender: z.enum(['MALE', 'FEMALE', 'OTHER']).optional().or(z.literal('')),
  }),
});