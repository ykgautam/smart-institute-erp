// Safely extracts pagination metadata from a backend page response,
// defaulting each field so a table never crashes on an unexpected/partial
// shape (e.g. during early integration before an endpoint is finalized).
export function extractPageMeta(pageResponse) {
  if (!pageResponse) {
    return {
      content: [],
      totalElements: 0,
      totalPages: 0,
      empty: true,
    };
  }

  return {
    content: pageResponse.content ?? [],
    totalElements: pageResponse.totalElements ?? 0,
    totalPages: pageResponse.totalPages ?? 0,
    empty: pageResponse.empty ?? (pageResponse.content?.length ?? 0) === 0,
  };
}