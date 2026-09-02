// Minimal pub/sub store for global toast notifications. Deliberately NOT
// Redux — a toast is fire-and-forget UI feedback that nothing else in
// the app needs to read or persist.
let listener = null;

export function subscribeSnackbar(callback) {
  listener = callback;
  return () => {
    listener = null;
  };
}

// severity: 'success' | 'error' | 'warning' | 'info'
export function showSnackbar(message, severity = 'info') {
  if (listener) {
    listener({ message, severity, key: Date.now() });
  }
}