import { z } from 'zod';

// Mirrors CollectFeeRequest exactly — studentFeeId, amount, and
// paymentMode are required; transactionReference/remarks are optional.
export const collectPaymentSchema = z.object({
  amount: z.coerce
    .number({ invalid_type_error: 'Amount is required.' })
    .positive('Amount must be greater than zero.'),
  paymentMode: z.enum(['CASH', 'UPI', 'CARD', 'BANK_TRANSFER'], {
    errorMap: () => ({ message: 'Payment mode is required.' }),
  }),
  transactionReference: z.string().max(100, 'Max 100 characters.').optional().or(z.literal('')),
  remarks: z.string().max(300, 'Max 300 characters.').optional().or(z.literal('')),
});