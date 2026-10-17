// ==========================================
// AUTHENTICATION SYSTEM (Connected to Spring Boot API)
// ==========================================

// Hosted Render Backend URL
window.API_BASE_URL = window.API_BASE_URL || 'https://company-pro-backend.onrender.com';
var API_BASE_URL = window.API_BASE_URL;

// Check if user is already logged in
function checkAuth() {
    const isLoggedIn = sessionStorage.getItem('isLoggedIn');
    const currentPage = window.location.pathname;
    
    if (isLoggedIn === 'true') {
        // If on login page and already logged in, redirect to dashboard
        if (currentPage.includes('login.html')) {
            window.location.href = 'dashboard.html';
        }
    } else {
        // If not logged in and trying to access admin pages
        if (currentPage.includes('dashboard.html') || 
            currentPage.includes('employees.html') || 
            currentPage.includes('departments.html') || 
            currentPage.includes('reports.html') ||
            currentPage.includes('profile.html')) {
            window.location.href = 'login.html';
        }
    }
}

// Login form handler
if (document.getElementById('loginForm')) {
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const rememberMeCheckbox = document.getElementById('rememberMe');
    const loginMessage = document.getElementById('loginMessage');
    const togglePassword = document.getElementById('togglePassword');

    // Toggle password visibility
    if (togglePassword) {
        togglePassword.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            this.classList.toggle('fa-eye');
            this.classList.toggle('fa-eye-slash');
        });
    }

    // Handle form submission
    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const username = usernameInput.value.trim();
        const password = passwordInput.value;
        const rememberMe = rememberMeCheckbox ? rememberMeCheckbox.checked : false;

        // Clear previous messages
        loginMessage.className = 'message';
        loginMessage.textContent = 'Authenticating with Neon DB...';

        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: username,
                    password: password,
                    rememberMe: rememberMe
                })
            });

            const data = await response.json();

            if (response.ok && data.success) {
                // Success
                loginMessage.className = 'message success';
                loginMessage.textContent = 'Login successful! Redirecting...';

                // Store session state in sessionStorage (cleared on tab close)
                sessionStorage.setItem('isLoggedIn', 'true');
                sessionStorage.setItem('adminUser', JSON.stringify({
                    username: data.username,
                    name: data.name,
                    email: data.email,
                    role: data.role
                }));
                sessionStorage.setItem('loginTime', new Date().toISOString());
                sessionStorage.setItem('lastLogin', new Date().toISOString());

                // Redirect after 1 second
                setTimeout(() => {
                    window.location.href = 'dashboard.html';
                }, 1000);
            } else {
                // Failed login
                loginMessage.className = 'message error';
                loginMessage.textContent = data.message || 'Invalid username or password. Please try again.';
                
                // Shake animation
                loginForm.style.animation = 'shake 0.5s';
                setTimeout(() => {
                    loginForm.style.animation = '';
                }, 500);
            }
        } catch (error) {
            console.error('Login request failed:', error);
            loginMessage.className = 'message error';
            loginMessage.textContent = 'Could not connect to the backend server. Please verify Spring Boot is running on port 8080.';
            loginForm.style.animation = 'shake 0.5s';
            setTimeout(() => {
                loginForm.style.animation = '';
            }, 500);
        }
    });
}

// Logout function (available globally)
async function logout() {
    if (confirm('Are you sure you want to logout?')) {
        try {
            await fetch(`${API_BASE_URL}/api/auth/logout`, { method: 'POST' });
        } catch (e) {
            console.warn('Backend logout call completed');
        }
        sessionStorage.clear();
        window.location.href = 'login.html';
    }
}

window.logout = logout;

// Initialize auth check on page load
document.addEventListener('DOMContentLoaded', checkAuth);
