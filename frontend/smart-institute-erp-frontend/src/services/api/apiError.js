/**
 * Converts Axios errors into a predictable application-level error shape.
 *
 * Keeping this normalization in one place prevents individual features
 * from having to understand Axios-specific error structures.
 */
export function normalizeApiError(error) {
  if (!error) {
    return {
      status: null,
      message: 'An unexpected error occurred.',
      data: null,
      isNetworkError: false,
      isTimeout: false,
    };
  }

  if (error.code === 'ECONNABORTED') {
    return {
      status: null,
      message: 'The request timed out. Please try again.',
      data: null,
      isNetworkError: false,
      isTimeout: true,
    };
  }

  if (!error.response) {
    return {
      status: null,
      message:
        'Unable to connect to the server. Please check your connection and try again.',
      data: null,
      isNetworkError: true,
      isTimeout: false,
    };
  }

  const { status, data } = error.response;

  return {
    status,
    message: extractApiErrorMessage(data, status),
    data,
    isNetworkError: false,
    isTimeout: false,
  };
}

/**
 * Extracts the most useful user-facing message from the backend response.
 *
 * The backend may evolve its error payload over time, so the API layer
 * supports the known message patterns without exposing raw server details.
 */
function extractApiErrorMessage(data, status) {
  if (typeof data === 'string' && data.trim()) {
    return data;
  }

  if (data?.message) {
    return data.message;
  }

  switch (status) {
    case 400:
      return 'The request is invalid.';

    case 401:
      return 'You are not authenticated.';

    case 403:
      return 'You do not have permission to perform this action.';

    case 404:
      return 'The requested resource was not found.';

    case 409:
      return 'The request conflicts with existing data.';

    case 422:
      return 'The submitted data is invalid.';

    case 500:
      return 'A server error occurred. Please try again later.';

    default:
      return 'An unexpected error occurred.';
  }
}