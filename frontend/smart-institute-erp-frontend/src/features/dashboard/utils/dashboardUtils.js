const MONTH_LABELS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

// Converts backend's { year, month } (month as 1-12 integer) into a
// short chart-axis label like "Jan 2026". Centralized here so every
// trend chart formats months identically.
export function formatMonthLabel(year, month) {
  const label = MONTH_LABELS[month - 1] || month;
  return `${label} ${year}`;
}

// Formats currency consistently across dashboard cards/charts —
// mirrors the ₹ formatting pattern already used in Fee module tables.
export function formatCurrency(value) {
  return `₹${Number(value || 0).toLocaleString('en-IN')}`;
}