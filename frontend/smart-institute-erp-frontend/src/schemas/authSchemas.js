import { z } from 'zod';

// Mirrors LoginRequest's Bean Validation constraints exactly
// (@NotBlank + @Email on email, @NotBlank on password) so the frontend
// rejects obviously-invalid input before hitting the network.
export const loginSchema = z.object({
  email: z.string().min(1, 'Email is required').email('Invalid email'),
  password: z.string().min(1, 'Password is required'),
});

// Mirrors ChangePasswordRequest constraints — used in a later Part
// when the change-password screen is built, defined now for consistency.
export const changePasswordSchema = z
  .object({
    currentPassword: z.string().min(1, 'Current password is required.'),
    newPassword: z
      .string()
      .min(8, 'Password must be between 8 and 50 characters.')
      .max(50, 'Password must be between 8 and 50 characters.'),
    confirmPassword: z.string().min(1, 'Confirm password is required.'),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Passwords do not match.',
    path: ['confirmPassword'],
  });