// Authentication related JavaScript

class AuthManager {
    constructor() {
        this.initAuthForms();
        this.initPasswordToggle();
    }

    initAuthForms() {
        const loginForm = document.getElementById('loginForm');
        const registerForm = document.getElementById('registerForm');

        if (loginForm) {
            loginForm.addEventListener('submit', this.handleLogin.bind(this));
        }

        if (registerForm) {
            registerForm.addEventListener('submit', this.handleRegister.bind(this));
            this.initPasswordStrength();
        }
    }

    initPasswordToggle() {
        const toggleButtons = document.querySelectorAll('.password-toggle');
        toggleButtons.forEach(button => {
            button.addEventListener('click', function() {
                const input = this.previousElementSibling;
                const icon = this.querySelector('i');
                
                if (input.type === 'password') {
                    input.type = 'text';
                    icon.classList.remove('fa-eye');
                    icon.classList.add('fa-eye-slash');
                } else {
                    input.type = 'password';
                    icon.classList.remove('fa-eye-slash');
                    icon.classList.add('fa-eye');
                }
            });
        });
    }

    initPasswordStrength() {
        const passwordInput = document.getElementById('password');
        const strengthMeter = document.getElementById('password-strength');

        if (passwordInput && strengthMeter) {
            passwordInput.addEventListener('input', this.updatePasswordStrength.bind(this));
        }
    }

    updatePasswordStrength(event) {
        const password = event.target.value;
        const strengthMeter = document.getElementById('password-strength');
        const strengthText = document.getElementById('password-strength-text');

        if (!password) {
            strengthMeter.style.width = '0%';
            strengthMeter.className = 'progress-bar';
            strengthText.textContent = '';
            return;
        }

        let strength = 0;
        let feedback = '';

        // Length check
        if (password.length >= 8) strength += 25;
        
        // Lowercase check
        if (/[a-z]/.test(password)) strength += 25;
        
        // Uppercase check
        if (/[A-Z]/.test(password)) strength += 25;
        
        // Number/Special char check
        if (/[0-9!@#$%^&*]/.test(password)) strength += 25;

        strengthMeter.style.width = strength + '%';

        // Update colors and text based on strength
        if (strength < 50) {
            strengthMeter.className = 'progress-bar bg-danger';
            feedback = 'Weak';
        } else if (strength < 75) {
            strengthMeter.className = 'progress-bar bg-warning';
            feedback = 'Medium';
        } else {
            strengthMeter.className = 'progress-bar bg-success';
            feedback = 'Strong';
        }

        strengthText.textContent = feedback;
    }

    async handleLogin(event) {
        event.preventDefault();
        const formData = new FormData(event.target);
        const data = Object.fromEntries(formData);

        try {
            // Simulate API call - replace with actual endpoint
            const response = await quizApp.apiCall('/api/auth/login', {
                method: 'POST',
                body: JSON.stringify(data)
            });

            quizApp.showToast('Login successful!', 'success');
            setTimeout(() => {
                window.location.href = '/dashboard';
            }, 1000);

        } catch (error) {
            console.error('Login failed:', error);
        }
    }

    async handleRegister(event) {
        event.preventDefault();
        const formData = new FormData(event.target);
        const data = Object.fromEntries(formData);

        // Password confirmation check
        if (data.password !== data.confirmPassword) {
            quizApp.showToast('Passwords do not match!', 'danger');
            return;
        }

        try {
            // Simulate API call - replace with actual endpoint
            const response = await quizApp.apiCall('/api/auth/register', {
                method: 'POST',
                body: JSON.stringify(data)
            });

            quizApp.showToast('Registration successful! Please check your email.', 'success');
            setTimeout(() => {
                window.location.href = '/login';
            }, 2000);

        } catch (error) {
            console.error('Registration failed:', error);
        }
    }
}

// Initialize auth manager
document.addEventListener('DOMContentLoaded', function() {
    if (document.querySelector('.auth-container')) {
        window.authManager = new AuthManager();
    }
});