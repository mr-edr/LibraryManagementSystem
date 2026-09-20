// API Service for Library Management System
const BASE_URL = '';

async function request(endpoint, options = {}) {
  const defaultHeaders = {};
  if (options.body && !(options.body instanceof FormData) && typeof options.body === 'object') {
    defaultHeaders['Content-Type'] = 'application/json';
    options.body = JSON.stringify(options.body);
  }

  const config = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers,
    },
    credentials: 'include', // include session cookie JSESSIONID
  };

  const response = await fetch(`${BASE_URL}${endpoint}`, config);

  if (response.status === 401) {
    // Unauthorized
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || 'Unauthorized or session expired');
  }

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `Request failed with status ${response.status}`);
  }

  // If response has no content
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return response.json();
  }
  return response.text();
}

export const api = {
  // Auth
  async login(username, password) {
    const formData = new URLSearchParams();
    formData.append('username', username);
    formData.append('password', password);

    const response = await fetch('/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: formData.toString(),
      credentials: 'include',
    });

    if (!response.ok) {
      throw new Error('Invalid username or password');
    }
    return this.getMe();
  },

  async logout() {
    return fetch('/logout', {
      method: 'POST',
      credentials: 'include',
    });
  },

  async register(userData) {
    return request('/register', {
      method: 'POST',
      body: userData,
    });
  },

  async getMe() {
    return request('/user/me');
  },

  // Books
  async getBooks() {
    return request('/books');
  },

  async getBookById(id) {
    return request(`/books/${id}`);
  },

  async searchBooks(query, category, availability) {
    const params = new URLSearchParams();
    if (query) params.append('query', query);
    if (category) params.append('category', category);
    if (availability) params.append('availability', availability);
    const queryString = params.toString();
    return request(`/books/search${queryString ? `?${queryString}` : ''}`);
  },

  async addBook(bookData) {
    return request('/librarian/books', {
      method: 'POST',
      body: bookData,
    });
  },

  async updateBook(id, bookData) {
    return request(`/librarian/books/${id}`, {
      method: 'PUT',
      body: bookData,
    });
  },

  async deleteBook(id) {
    return request(`/librarian/books/${id}`, {
      method: 'DELETE',
    });
  },

  // Requests
  async requestBorrow(bookId) {
    return request(`/request/borrow/${bookId}`, {
      method: 'POST',
    });
  },

  async requestExtend(transactionId) {
    return request(`/request/extend/${transactionId}`, {
      method: 'POST',
    });
  },

  async requestReturn(transactionId) {
    return request(`/request/return/${transactionId}`, {
      method: 'POST',
    });
  },

  async getMyRequests() {
    return request('/my-requests');
  },

  async getMyBooks() {
    return request('/my-books');
  },

  // Librarian
  async getLibrarianRequests(status = '') {
    const url = status ? `/librarian/requests?status=${status}` : '/librarian/requests';
    return request(url);
  },

  async approveRequest(requestId) {
    return request(`/librarian/requests/${requestId}/approve`, {
      method: 'PUT',
    });
  },

  async rejectRequest(requestId) {
    return request(`/librarian/requests/${requestId}/reject`, {
      method: 'PUT',
    });
  },

  async getBorrowedSummary() {
    return request('/librarian/borrowed');
  },

  async getOverdueLoans() {
    return request('/librarian/overdue');
  },

  // Admin
  async getAllUsers() {
    return request('/admin/users');
  },

  async updateUserRole(userId, role) {
    return request(`/admin/users/${userId}/role?role=${role}`, {
      method: 'PUT',
    });
  },

  async toggleUserStatus(userId, enabled) {
    return request(`/admin/users/${userId}/status?enabled=${enabled}`, {
      method: 'PUT',
    });
  },
};
