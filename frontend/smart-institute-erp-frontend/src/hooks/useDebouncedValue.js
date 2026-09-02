import { useState, useEffect } from 'react';

// Generic debounce hook — delays updating the returned value until the
// input has stopped changing for `delayMs`. Used for search fields so
// we don't fire an API call on every keystroke (Section 29).
//
// HOW IT WORKS (for reference since you're new to React hooks):
// Every time `value` changes, this effect sets a timer. If `value`
// changes again before the timer finishes, the cleanup function
// (the `return () => clearTimeout(timer)` part) cancels the old timer
// before starting a new one. So the debounced value only updates once
// typing actually pauses.
export function useDebouncedValue(value, delayMs = 400) {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedValue(value);
    }, delayMs);

    return () => clearTimeout(timer);
  }, [value, delayMs]);

  return debouncedValue;
}