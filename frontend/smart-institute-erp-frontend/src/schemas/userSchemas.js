import { z } from 'zod';

// Mirrors CreateUserRequest exactly — email and password are required
// (@NotBlank on backend), firstName required, rest optional.
export const createUserSchema = z.object({
  firstName: z.string().min(1, 'First name is required.').max(100, 'Max 100 characters.'),
  lastName: z.string().max(100, 'Max 100 characters.').optional().or(z.literal('')),
  email: z.string().min(1, 'Email is required.').email('Invalid email.'),
  mobile: z.string().optional().or(z.literal('')),
  password: z.string().min(1, 'Password is required.'),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER']).optional().or(z.literal('')),
  roleId: z.coerce.number({ invalid_type_error: 'Role is required.' }).positive('Role is required.'),
});

// Mirrors UpdateUserRequest — firstName, gender, lastName, roleId are
// all listed as required in the backend schema (no password/email here,
// those aren't editable through this endpoint).
export const updateUserSchema = z.object({
  firstName: z.string().min(1, 'First name is required.').max(100, 'Max 100 characters.'),
  lastName: z.string().min(1, 'Last name is required.').max(100, 'Max 100 characters.'),
  mobile: z.string().regex(/^[6-9]\d{9}$/, 'Enter a valid 10-digit mobile number.').optional().or(z.literal('')),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER'], { errorMap: () => ({ message: 'Gender is required.' }) }),
  roleId: z.coerce.number({ invalid_type_error: 'Role is required.' }).positive('Role is required.'),
});