// Central mirror of backend enums (com.smartinstitute.erp.common.enums.*).
// Every feature imports enum VALUES from here instead of hardcoding
// strings like "ACTIVE" directly in components — if the backend enum
// changes, only this file needs updating.
export const ROLE_TYPE = {
  SUPER_ADMIN: 'SUPER_ADMIN',
  INSTITUTE_ADMIN: 'INSTITUTE_ADMIN',
  STAFF: 'STAFF',
  FACULTY: 'FACULTY',
  ACCOUNTANT: 'ACCOUNTANT',
  STUDENT: 'STUDENT',
};

export const GENDER = {
  MALE: 'MALE',
  FEMALE: 'FEMALE',
  OTHER: 'OTHER',
};

export const STUDENT_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  COMPLETED: 'COMPLETED',
  DROPPED: 'DROPPED',
};

export const COURSE_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
};

export const BATCH_STATUS = {
  PLANNED: 'PLANNED',
  ACTIVE: 'ACTIVE',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
};

export const DURATION_TYPE = {
  DAYS: 'DAYS',
  WEEKS: 'WEEKS',
  MONTHS: 'MONTHS',
  YEARS: 'YEARS',
};

export const FEE_STATUS = {
  PENDING: 'PENDING',
  PARTIALLY_PAID: 'PARTIALLY_PAID',
  PAID: 'PAID',
};

export const PAYMENT_MODE = {
  CASH: 'CASH',
  UPI: 'UPI',
  CARD: 'CARD',
  BANK_TRANSFER: 'BANK_TRANSFER',
};

export const ATTENDANCE_STATUS = {
  PRESENT: 'PRESENT',
  ABSENT: 'ABSENT',
  LATE: 'LATE',
  LEAVE: 'LEAVE',
};

export const TEST_STATUS = {
  DRAFT: 'DRAFT',
  PUBLISHED: 'PUBLISHED',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
};

export const TEST_TYPE = {
  PRACTICE: 'PRACTICE',
  FINAL: 'FINAL',
};

export const QUESTION_TYPE = {
  MCQ: 'MCQ',
};

export const QUESTION_DIFFICULTY = {
  EASY: 'EASY',
  MEDIUM: 'MEDIUM',
  HARD: 'HARD',
};

export const STUDENT_TEST_STATUS = {
  IN_PROGRESS: 'IN_PROGRESS',
  SUBMITTED: 'SUBMITTED',
  AUTO_SUBMITTED: 'AUTO_SUBMITTED',
};

export const USER_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  LOCKED: 'LOCKED',
  DELETED: 'DELETED',
};

export const INSTITUTE_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
};

export const INSTITUTE_TYPE = {
  COMPUTER_INSTITUTE: 'COMPUTER_INSTITUTE',
  SCHOOL: 'SCHOOL',
  COLLEGE: 'COLLEGE',
  TRAINING_CENTER: 'TRAINING_CENTER',
  COACHING_CENTER: 'COACHING_CENTER',
};

// --- MUI color mapping helpers ---
export function getStudentStatusColor(status) {
  switch (status) {
    case STUDENT_STATUS.ACTIVE:
      return 'success';
    case STUDENT_STATUS.COMPLETED:
      return 'info';
    case STUDENT_STATUS.DROPPED:
      return 'error';
    case STUDENT_STATUS.INACTIVE:
    default:
      return 'default';
  }
}

export function getFeeStatusColor(status) {
  switch (status) {
    case FEE_STATUS.PAID:
      return 'success';
    case FEE_STATUS.PARTIALLY_PAID:
      return 'warning';
    case FEE_STATUS.PENDING:
    default:
      return 'error';
  }
}

export function getAttendanceStatusColor(status) {
  switch (status) {
    case ATTENDANCE_STATUS.PRESENT:
      return 'success';
    case ATTENDANCE_STATUS.LATE:
      return 'warning';
    case ATTENDANCE_STATUS.LEAVE:
      return 'info';
    case ATTENDANCE_STATUS.ABSENT:
    default:
      return 'error';
  }
}

export function getCourseStatusColor(status) {
  return status === COURSE_STATUS.ACTIVE ? 'success' : 'default';
}