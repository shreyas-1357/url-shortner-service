/* 6. scripts.js - JavaScript for frontend functionality */
// Common variables and utility functions
const API_BASE_URL = 'http://localhost:8080/api';
const TOKEN_KEY = 'auth_token';
const USERNAME_KEY = 'username';

function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function isLoggedIn() {
    return !!getToken();
}

function saveToken(token, username) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USERNAME_KEY, username);
}

function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USERNAME_KEY);
}

function getUsername() {
    return localStorage.getItem(USERNAME_KEY);
}

function updateNavigation() {
    const loginLink = document.getElementById('loginLink');
    const registerLink = document.getElementById('registerLink');
    const myUrlsLink = document.getElementById('myUrlsLink');
    const logoutLink = document.getElementById('logoutLink');

    if (isLoggedIn()) {
        if (loginLink) loginLink.style.display = 'none';
        if (registerLink) registerLink.style.display = 'none';
        if (myUrlsLink) myUrlsLink.style.display = 'inline-block';
        if (logoutLink) logoutLink.style.display = 'inline-block';

        // Show custom code section on home page if logged in
        const customCodeSection = document.getElementById('customCodeSection');
        if (customCodeSection) {
            customCodeSection.style.display = 'block';
        }
    } else {
        if (loginLink) loginLink.style.display = 'inline-block';
        if (registerLink) registerLink.style.display = 'inline-block';
        if (myUrlsLink) myUrlsLink.style.display = 'none';
        if (logoutLink) logoutLink.style.display = 'none';

        // Hide custom code section if not logged in
        const customCodeSection = document.getElementById('customCodeSection');
        if (customCodeSection) {
            customCodeSection.style.display = 'none';
        }
    }
}

// Initialize page based on login status
document.addEventListener('DOMContentLoaded', function() {
    updateNavigation();

    // Set up logout functionality
    const logoutLink = document.getElementById('logoutLink');
    if (logoutLink) {
        logoutLink.addEventListener('click', function(e) {
            e.preventDefault();
            clearToken();
            window.location.href = 'index.html';
        });
    }

    // URL Shortener Form Handling
    const urlShortenForm = document.getElementById('urlShortenForm');
    if (urlShortenForm) {
        urlShortenForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const originalUrl = document.getElementById('originalUrl').value;
            const customCode = document.getElementById('customCode')?.value || '';

            shortenUrl(originalUrl, customCode);
        });
    }

    // Login Form Handling
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            login(username, password);
        });
    }

    // Registration Form Handling
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const username = document.getElementById('regUsername').value;
            const password = document.getElementById('regPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (password !== confirmPassword) {
                showError('registerError', 'Passwords do not match');
                return;
            }

            register(username, password);
        });
    }

    // Copy button functionality
    const copyButton = document.getElementById('copyButton');
    if (copyButton) {
        copyButton.addEventListener('click', function() {
            const shortenedUrl = document.getElementById('shortenedUrlLink').href;
            navigator.clipboard.writeText(shortenedUrl).then(function() {
                copyButton.textContent = 'Copied!';
                setTimeout(function() {
                    copyButton.textContent = 'Copy';
                }, 2000);
            });
        });
    }

    // Load user's URLs if on My URLs page
    if (window.location.pathname.includes('my-urls.html')) {
        if (!isLoggedIn()) {
            window.location.href = 'login.html';
        } else {
            loadUserUrls();
        }
    }
});

// API Functions
function shortenUrl(originalUrl, customCode) {
    const requestBody = {
        originalUrl: originalUrl,
        customCode: customCode || undefined
    };

    const headers = {
        'Content-Type': 'application/json'
    };

    if (isLoggedIn()) {
        headers['Authorization'] = 'Basic ' + getToken();
    }

    fetch(`${API_BASE_URL}/url/shorten`, {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(requestBody)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.error || 'Error shortening URL');
            });
        }
        return response.json();
    })
    .then(data => {
        document.getElementById('originalUrlDisplay').textContent = data.originalUrl;
        const shortenedUrlLink = document.getElementById('shortenedUrlLink');
        shortenedUrlLink.href = data.shortUrl;
        shortenedUrlLink.textContent = data.shortUrl;
        document.getElementById('resultSection').style.display = 'block';
    })
    .catch(error => {
        alert('Error: ' + error.message);
    });
}

function login(username, password) {
    // Create Basic Authentication token
    const token = btoa(`${username}:${password}`);

    fetch(`${API_BASE_URL}/url/my-urls`, {
        method: 'GET',
        headers: {
            'Authorization': 'Basic ' + token
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Invalid username or password');
        }
        // Store token if authentication was successful
        saveToken(token, username);
        window.location.href = 'index.html';
    })
    .catch(error => {
        showError('loginError', error.message);
    });
}

function register(username, password) {
    fetch(`${API_BASE_URL}/auth/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.error || 'Registration failed');
            });
        }
        return response.json();
    })
    .then(data => {
        // After successful registration, redirect to login page
        alert('Registration successful! Please log in.');
        window.location.href = 'login.html';
    })
    .catch(error => {
        showError('registerError', error.message);
    });
}

function loadUserUrls() {
    const urlsLoading = document.getElementById('urlsLoading');
    const urlsTable = document.getElementById('urlsTable');
    const noUrlsMessage = document.getElementById('noUrlsMessage');

    fetch(`${API_BASE_URL}/url/my-urls`, {
        method: 'GET',
        headers: {
            'Authorization': 'Basic ' + getToken()
        }
    })
    .then(response => {
        if (!response.ok) {
            if (response.status === 401) {
                clearToken();
                window.location.href = 'login.html';
                throw new Error('Session expired. Please login again.');
            }
            throw new Error('Failed to load URLs');
        }
        return response.json();
    })
    .then(data => {
        urlsLoading.style.display = 'none';

        if (!data.urls || data.urls.length === 0) {
            noUrlsMessage.style.display = 'block';
            return;
        }

        const tableBody = document.getElementById('urlsTableBody');
        tableBody.innerHTML = '';

        data.urls.forEach(url => {
            const row = document.createElement('tr');

            // Original URL column with truncation
            const originalUrlCell = document.createElement('td');
            const originalUrlSpan = document.createElement('span');
            originalUrlSpan.className = 'truncate';
            originalUrlSpan.title = url.originalUrl;
            originalUrlSpan.textContent = url.originalUrl;
            originalUrlCell.appendChild(originalUrlSpan);

            // Short URL column with link
            const shortUrlCell = document.createElement('td');
            const shortUrlLink = document.createElement('a');
            shortUrlLink.href = url.shortUrl;
            shortUrlLink.textContent = url.shortUrl;
            shortUrlLink.target = '_blank';
            shortUrlCell.appendChild(shortUrlLink);

            // Created date column
            const createdCell = document.createElement('td');
            createdCell.textContent = new Date(url.createdAt).toLocaleDateString();

            // Click count column
            const clicksCell = document.createElement('td');
            clicksCell.textContent = url.accessCount;

            // Action buttons column
            const actionsCell = document.createElement('td');
            const actionsDiv = document.createElement('div');
            actionsDiv.className = 'url-actions';

            const copyBtn = document.createElement('button');
            copyBtn.className = 'btn secondary';
            copyBtn.textContent = 'Copy';
            copyBtn.addEventListener('click', function() {
                navigator.clipboard.writeText(url.shortUrl).then(function() {
                    copyBtn.textContent = 'Copied!';
                    setTimeout(function() {
                        copyBtn.textContent = 'Copy';
                    }, 2000);
                });
            });

            actionsDiv.appendChild(copyBtn);
            actionsCell.appendChild(actionsDiv);

            // Add all cells to the row
            row.appendChild(originalUrlCell);
            row.appendChild(shortUrlCell);
            row.appendChild(createdCell);
            row.appendChild(clicksCell);
            row.appendChild(actionsCell);

            // Add row to table body
            tableBody.appendChild(row);
        });

        urlsTable.style.display = 'table';
    })
    .catch(error => {
        urlsLoading.style.display = 'none';
        urlsLoading.textContent = 'Error: ' + error.message;
    });
}

function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }
}