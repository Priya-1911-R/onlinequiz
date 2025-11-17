// Admin panel functionality

class AdminManager {
    constructor() {
        this.initAdminComponents();
        this.initQuizManagement();
        this.initUserManagement();
    }

    initAdminComponents() {
        this.initCharts();
        this.initDataTables();
        this.initQuizForms();
    }

    initCharts() {
        // Initialize performance charts if Chart.js is available
        if (typeof Chart !== 'undefined') {
            this.initPerformanceChart();
            this.initQuizStatsChart();
        }
    }

    initPerformanceChart() {
        const ctx = document.getElementById('performanceChart');
        if (ctx) {
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                    datasets: [{
                        label: 'Average Score',
                        data: [65, 59, 80, 81, 56, 72],
                        borderColor: '#4361ee',
                        backgroundColor: 'rgba(67, 97, 238, 0.1)',
                        tension: 0.4,
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top',
                        },
                        title: {
                            display: true,
                            text: 'Monthly Performance'
                        }
                    }
                }
            });
        }
    }

    initQuizStatsChart() {
        const ctx = document.getElementById('quizStatsChart');
        if (ctx) {
            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: ['Completed', 'In Progress', 'Not Started'],
                    datasets: [{
                        data: [65, 15, 20],
                        backgroundColor: [
                            '#4cc9f0',
                            '#f8961e',
                            '#6c757d'
                        ]
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'bottom',
                        }
                    }
                }
            });
        }
    }

    initDataTables() {
        // Initialize Bootstrap tables with enhanced functionality
        const tables = document.querySelectorAll('.table');
        tables.forEach(table => {
            this.enhanceTable(table);
        });
    }

    enhanceTable(table) {
        // Add search functionality
        const searchInput = document.createElement('input');
        searchInput.type = 'text';
        searchInput.placeholder = 'Search...';
        searchInput.className = 'form-control mb-3';
        searchInput.style.maxWidth = '300px';

        searchInput.addEventListener('input', debounce((e) => {
            const searchTerm = e.target.value.toLowerCase();
            const rows = table.querySelectorAll('tbody tr');
            
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(searchTerm) ? '' : 'none';
            });
        }, 300));

        table.parentNode.insertBefore(searchInput, table);

        // Add sort functionality
        const headers = table.querySelectorAll('thead th[data-sort]');
        headers.forEach(header => {
            header.style.cursor = 'pointer';
            header.addEventListener('click', () => {
                this.sortTable(table, header.cellIndex, header.dataset.sort);
            });
        });
    }

    sortTable(table, columnIndex, sortType) {
        const tbody = table.querySelector('tbody');
        const rows = Array.from(tbody.querySelectorAll('tr'));
        const isNumeric = sortType === 'numeric';
        const isDate = sortType === 'date';

        rows.sort((a, b) => {
            const aVal = a.cells[columnIndex].textContent;
            const bVal = b.cells[columnIndex].textContent;

            if (isNumeric) {
                return parseFloat(aVal) - parseFloat(bVal);
            } else if (isDate) {
                return new Date(aVal) - new Date(bVal);
            } else {
                return aVal.localeCompare(bVal);
            }
        });

        // Reverse if already sorted
        if (tbody.dataset.sortedColumn === columnIndex) {
            rows.reverse();
            tbody.dataset.sortedColumn = '';
        } else {
            tbody.dataset.sortedColumn = columnIndex;
        }

        // Reappend sorted rows
        rows.forEach(row => tbody.appendChild(row));
    }

    initQuizForms() {
        this.initQuestionBuilder();
        this.initQuizValidation();
    }

    initQuestionBuilder() {
        let questionCount = 1;

        // Add question
        document.getElementById('addQuestion')?.addEventListener('click', () => {
            questionCount++;
            const questionsContainer = document.getElementById('questionsContainer');
            const newQuestion = this.createQuestionHTML(questionCount);
            questionsContainer.appendChild(newQuestion);
        });

        // Remove question
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('remove-question')) {
                if (document.querySelectorAll('.question-item').length > 1) {
                    e.target.closest('.question-item').remove();
                } else {
                    quizApp.showToast('At least one question is required', 'warning');
                }
            }
        });

        // Add option
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('add-option')) {
                const questionItem = e.target.closest('.question-item');
                const optionsContainer = questionItem.querySelector('.options-container');
                const optionCount = optionsContainer.querySelectorAll('.option-row').length;
                
                if (optionCount < 6) {
                    const newOption = this.createOptionHTML(optionCount + 1);
                    optionsContainer.appendChild(newOption);
                } else {
                    quizApp.showToast('Maximum 6 options allowed per question', 'warning');
                }
            }
        });

        // Remove option
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('remove-option')) {
                const optionsContainer = e.target.closest('.options-container');
                if (optionsContainer.querySelectorAll('.option-row').length > 2) {
                    e.target.closest('.option-row').remove();
                } else {
                    quizApp.showToast('At least 2 options are required', 'warning');
                }
            }
        });
    }

    createQuestionHTML(number) {
        return `
            <div class="question-item card mb-3">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h6 class="mb-0">Question ${number}</h6>
                    <button type="button" class="btn btn-sm btn-danger remove-question">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <label class="form-label">Question Text</label>
                        <textarea class="form-control" name="questions[${number}].text" required></textarea>
                    </div>
                    <div class="options-container">
                        ${this.createOptionHTML(1)}
                        ${this.createOptionHTML(2)}
                    </div>
                    <button type="button" class="btn btn-info btn-sm add-option mt-2">
                        <i class="fas fa-plus"></i> Add Option
                    </button>
                    <div class="mt-3">
                        <label class="form-label">Correct Answer</label>
                        <select class="form-select" name="questions[${number}].correctOption" required>
                            <option value="">Select correct option</option>
                            <option value="1">Option 1</option>
                            <option value="2">Option 2</option>
                        </select>
                    </div>
                </div>
            </div>
        `;
    }

    createOptionHTML(number) {
        return `
            <div class="option-row mb-2">
                <div class="input-group">
                    <span class="input-group-text">${number}</span>
                    <input type="text" class="form-control" name="questions[0].options[${number}]" placeholder="Option text" required>
                    <button type="button" class="btn btn-outline-danger remove-option">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </div>
        `;
    }

    initQuizValidation() {
        const forms = document.querySelectorAll('.quiz-form');
        forms.forEach(form => {
            form.addEventListener('submit', (e) => {
                if (!this.validateQuizForm(form)) {
                    e.preventDefault();
                    quizApp.showToast('Please fill all required fields correctly', 'danger');
                }
            });
        });
    }

    validateQuizForm(form) {
        const requiredFields = form.querySelectorAll('[required]');
        let isValid = true;

        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                field.classList.add('is-invalid');
                isValid = false;
            } else {
                field.classList.remove('is-invalid');
            }
        });

        return isValid;
    }

    initUserManagement() {
        // User management functionality
        this.initUserActions();
        this.initBulkActions();
    }

    initUserActions() {
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('btn-activate-user')) {
                this.toggleUserStatus(e.target.dataset.userId, true);
            }
            if (e.target.classList.contains('btn-deactivate-user')) {
                this.toggleUserStatus(e.target.dataset.userId, false);
            }
            if (e.target.classList.contains('btn-reset-password')) {
                this.resetUserPassword(e.target.dataset.userId);
            }
        });
    }

    async toggleUserStatus(userId, activate) {
        try {
            const response = await quizApp.apiCall(`/api/admin/users/${userId}/status`, {
                method: 'PUT',
                body: JSON.stringify({ active: activate })
            });

            quizApp.showToast(`User ${activate ? 'activated' : 'deactivated'} successfully`, 'success');
            // Refresh the page or update UI
            setTimeout(() => location.reload(), 1000);
        } catch (error) {
            console.error('Failed to update user status:', error);
        }
    }

    async resetUserPassword(userId) {
        if (confirm('Are you sure you want to reset this user\'s password? They will receive an email with instructions.')) {
            try {
                const response = await quizApp.apiCall(`/api/admin/users/${userId}/reset-password`, {
                    method: 'POST'
                });

                quizApp.showToast('Password reset email sent successfully', 'success');
            } catch (error) {
                console.error('Failed to reset password:', error);
            }
        }
    }

    initBulkActions() {
        const bulkAction = document.getElementById('bulkAction');
        const applyBulkAction = document.getElementById('applyBulkAction');

        if (applyBulkAction) {
            applyBulkAction.addEventListener('click', () => {
                const selectedUsers = Array.from(document.querySelectorAll('.user-checkbox:checked'))
                    .map(checkbox => checkbox.value);

                if (selectedUsers.length === 0) {
                    quizApp.showToast('Please select at least one user', 'warning');
                    return;
                }

                const action = bulkAction.value;
                this.performBulkAction(action, selectedUsers);
            });
        }
    }

    async performBulkAction(action, userIds) {
        try {
            const response = await quizApp.apiCall('/api/admin/users/bulk-action', {
                method: 'POST',
                body: JSON.stringify({ action, userIds })
            });

            quizApp.showToast(`Bulk action completed successfully`, 'success');
            setTimeout(() => location.reload(), 1000);
        } catch (error) {
            console.error('Bulk action failed:', error);
        }
    }
}

// Initialize admin manager
document.addEventListener('DOMContentLoaded', function() {
    if (document.querySelector('.admin-container')) {
        window.adminManager = new AdminManager();
    }
});