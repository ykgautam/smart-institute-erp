// Typography scale for the ERP. Uses system font stack to avoid
// an extra font-loading dependency at this stage.
export const typography = {
  fontFamily: [
    '-apple-system',
    'BlinkMacSystemFont',
    '"Segoe UI"',
    'Roboto',
    '"Helvetica Neue"',
    'Arial',
    'sans-serif',
  ].join(','),
  h1: { fontSize: '2rem', fontWeight: 600 },
  h2: { fontSize: '1.5rem', fontWeight: 600 },
  h3: { fontSize: '1.25rem', fontWeight: 600 },
  body1: { fontSize: '0.95rem' },
  body2: { fontSize: '0.85rem' },
  button: { textTransform: 'none' }, // ERP buttons read better in normal case, not ALL CAPS
};