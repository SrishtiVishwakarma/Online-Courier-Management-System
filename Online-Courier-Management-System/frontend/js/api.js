// API utilities and authentication
// For Vercel production, this should point to your backend URL
// const API_BASE = window.ENV?.API_BASE || "https://online-courier-management-system-backend.onrender.com/api";

//To run local
const API_BASE = window.ENV?.API_BASE || "http://localhost:8080/api";

// Auth helpers
function getToken() {
  return localStorage.getItem("ocms_token");
}

function getRole() {
  return localStorage.getItem("ocms_role");
}

function getUser() {
  try {
    return JSON.parse(localStorage.getItem("ocms_user") || "null");
  } catch {
    return null;
  }
}

function setAuth(authData) {
  localStorage.setItem("ocms_token", authData.token);
  localStorage.setItem("ocms_role", authData.role);
  localStorage.setItem("ocms_user", JSON.stringify(authData.user));
}

function clearAuth() {
  localStorage.removeItem("ocms_token");
  localStorage.removeItem("ocms_role");
  localStorage.removeItem("ocms_user");
}

function isAuthenticated() {
  return !!getToken();
}

// API fetch wrapper
async function api(path, options = {}) {
  const headers = options.headers || {};

  // Add auth token if available (but not for login requests)
  //here bearer is used to send the token to the server why bearer is used cuase it's a standard way to send the token to the server
  const token = getToken();
  if (token && !path.includes("/auth/login")) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  // Add content-type for JSON requests
  if (
    options.body &&
    typeof options.body === "object" &&
    !(options.body instanceof FormData)
  ) {
    headers["Content-Type"] = "application/json";
    options.body = JSON.stringify(options.body);
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  // Handle 401 Unauthorized - redirect to login
  if (response.status === 401) {
    clearAuth();
    window.location.href = "login.html";
    throw new Error("Unauthorized");
  }

  // Handle 403 Forbidden
  if (response.status === 403) {
    throw new Error("Access denied");
  }

  // Handle empty responses
  if (
    response.status === 204 ||
    response.headers.get("content-length") === "0"
  ) {
    return null;
  }

  // For PDF responses
  const contentType = response.headers.get("content-type");
  if (contentType && contentType.includes("application/pdf")) {
    return response;
  }

  // Parse JSON response
  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || data.error || "Request failed");
  }

  return data;
}

// Download receipt with authentication
async function downloadReceipt(courierId) {
  try {
    const token = getToken();
    if (!token) {
      throw new Error("Authentication required");
    }

    const response = await fetch(`${API_BASE}/couriers/${courierId}/receipt`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      if (response.status === 401) {
        clearAuth();
        window.location.href = "login.html";
        throw new Error("Unauthorized");
      }
      throw new Error("Failed to download receipt");
    }

    // Get the blob and download it
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `receipt-${courierId}.pdf`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  } catch (error) {
    alert("Error downloading receipt: " + error.message);
    throw error;
  }
}

// Export for use in other files
window.ocmsAPI = {
  api,
  getToken,
  getRole,
  getUser,
  setAuth,
  clearAuth,
  isAuthenticated,
  downloadReceipt,
  API_BASE,
};
