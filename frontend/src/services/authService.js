
const API_BASE_URL = "http://localhost:8080";

export async function getCsrfToken() {
  const response = await fetch(`${API_BASE_URL}/auth/csrf`, {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error("Failed to fetch CSRF token");
  }

  const data = await response.json();

  return data.token;
}


export async function requestLoginCode(email) {
  const csrfToken = await getCsrfToken();

  const response = await fetch(`${API_BASE_URL}/auth/email-link`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      "X-CSRF-TOKEN": csrfToken,
    },
    body: JSON.stringify({ email }),
  });

  if (!response.ok) {
    throw new Error("Failed to request login code");
  }
}

export async function verifyLoginCode(token) {
  const csrfToken = await getCsrfToken();

  const response = await fetch(`${API_BASE_URL}/auth/verify`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      "X-CSRF-TOKEN": csrfToken,
    },
    body: JSON.stringify({ token }),
  });

  if (!response.ok) {
    throw new Error("Failed to verify login code");
  }

  const user = await response.json();

  return user;
}

export async function getCurrentUser() {
  const response = await fetch(`${API_BASE_URL}/auth/me`, {
    method: "GET",
    credentials: "include",
  });

  if (response.status === 401) {
    return null;
  }

  if (!response.ok) {
    throw new Error("Failed to fetch current user");
  }

  const user = await response.json();

  return user;
}

export async function logout() {
  const csrfToken = await getCsrfToken();

  const response = await fetch(`${API_BASE_URL}/auth/logout`, {
    method: "POST",
    credentials: "include",
    headers: {
      "X-CSRF-TOKEN": csrfToken,
    },
  });

  if (!response.ok) {
    throw new Error("Failed to logout");
  }
}
