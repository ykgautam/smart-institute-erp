import { z } from 'zod';

// Shared field rules — mirrors constraints common to BOTH
// CreateStudentRequest and UpdateStudentRequest exactly.
const mobilePattern = /^[0-9]{10}$/;
const pincodePattern = /^[0-9]{6}$/;

const commonFields = {
  rollNumber: z.string().max(30, 'Roll number must be at most 30 characters.').optional().or(z.literal('')),
  firstName: z.string().max(100, 'First name must be at most 100 characters.'),
  lastName: z.string().max(100, 'Last name must be at most 100 characters.').optional().or(z.literal('')),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER']).optional(),
  dateOfBirth: z
    .string()
    .optional()
    .refine((val) => !val || new Date(val) < new Date(), {
      message: 'Date of birth must be in the past.',
    }),
  mobile: z.string().regex(mobilePattern, 'Mobile number must contain exactly 10 digits.').optional().or(z.literal('')),
  email: z.string().email('Invalid email format.').max(150).optional().or(z.literal('')),
  fatherName: z.string().max(150, 'Father name must be at most 150 characters.'),
  motherName: z.string().max(150).optional().or(z.literal('')),
  guardianMobile: z.string().regex(mobilePattern, 'Guardian mobile must contain exactly 10 digits.').optional().or(z.literal('')),
  address: z.string().max(300).optional().or(z.literal('')),
  city: z.string().max(100).optional().or(z.literal('')),
  state: z.string().max(100).optional().or(z.literal('')),
  pincode: z.string().regex(pincodePattern, 'Pincode must contain exactly 6 digits.').optional().or(z.literal('')),
  admissionDate: z.string().optional(),
};

// Matches CreateStudentRequest — admissionNumber and firstName/fatherName
// are @NotBlank on the backend, so they're required here too.
export const createStudentSchema = z.object({
  admissionNumber: z.string().min(1, 'Admission number is required.').max(30),
  ...commonFields,
  firstName: z.string().min(1, 'First name is required.').max(100),
  fatherName: z.string().min(1, 'Father name is required.').max(150),
});

// Matches UpdateStudentRequest — NOTHING is @NotBlank on the backend here
// (admissionNumber isn't even part of the update DTO — it's immutable
// per Section 9 of the context doc: "Student ID should not be casually
// changed"). All fields are optional on update.
export const updateStudentSchema = z.object({
  ...commonFields,
  firstName: commonFields.firstName.optional().or(z.literal('')),
  fatherName: commonFields.fatherName.optional().or(z.literal('')),
});

// Converts a backend validation-error payload (shape unconfirmed — see
// Part 3 assumption flag) into a flat { fieldName: message } map that
// react-hook-form's setError can consume directly.
export function normalizeFieldErrors(errors) {
  if (!errors) return {};
  if (Array.isArray(errors)) {
    return errors.reduce((acc, e) => {
      if (e.field) acc[e.field] = e.message;
      return acc;
    }, {});
  }
  // Already an object map { fieldName: message }
  return errors;
}