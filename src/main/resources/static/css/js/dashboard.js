// Dashboard functionality
document.addEventListener('DOMContentLoaded', function() {
    console.log('Dashboard loaded successfully');
    
    // Initialize Bootstrap tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Chart data for dashboard (you can replace with real data)
    const quizStats = {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [{
            label: 'Quizzes Created',
            data: [12, 19, 3, 5, 2, 3],
            backgroundColor: 'rgba(54, 162, 235, 0.2)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
        }]
    };
    
    // Initialize chart if canvas exists
    const ctx = document.getElementById('quizChart');
    if (ctx) {
        new Chart(ctx, {
            type: 'bar',
            data: quizStats,
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }
    
    // Quick stats update
    updateQuickStats();
});

function updateQuickStats() {
    // You can add AJAX calls here to update stats in real-time
    console.log('Updating dashboard stats...');
}