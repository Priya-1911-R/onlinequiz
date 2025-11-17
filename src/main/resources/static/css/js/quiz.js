// Quiz taking functionality

class QuizManager {
    constructor() {
        this.currentQuestion = 0;
        this.answers = {};
        this.timer = null;
        this.timeLeft = 0;
        this.quizStarted = false;

        this.initQuiz();
    }

    initQuiz() {
        this.initQuestionNavigation();
        this.initTimer();
        this.initQuizSubmission();
    }

    initQuestionNavigation() {
        const nextBtn = document.getElementById('nextQuestion');
        const prevBtn = document.getElementById('prevQuestion');

        if (nextBtn) {
            nextBtn.addEventListener('click', () => this.navigateQuestion(1));
        }

        if (prevBtn) {
            prevBtn.addEventListener('click', () => this.navigateQuestion(-1));
        }

        // Keyboard navigation
        document.addEventListener('keydown', (e) => {
            if (e.key === 'ArrowRight') this.navigateQuestion(1);
            if (e.key === 'ArrowLeft') this.navigateQuestion(-1);
        });
    }

    initTimer() {
        const timerElement = document.getElementById('quizTimer');
        if (!timerElement) return;

        const timeLimit = parseInt(timerElement.dataset.timeLimit) * 60; // Convert to seconds
        this.timeLeft = timeLimit;
        this.updateTimerDisplay();

        this.timer = setInterval(() => {
            this.timeLeft--;
            this.updateTimerDisplay();

            if (this.timeLeft <= 0) {
                this.submitQuiz();
            }

            // Warning when 5 minutes left
            if (this.timeLeft === 300) {
                quizApp.showToast('Only 5 minutes remaining!', 'warning');
            }

            // Critical when 1 minute left
            if (this.timeLeft === 60) {
                quizApp.showToast('Only 1 minute remaining! Hurry up!', 'danger');
            }
        }, 1000);
    }

    updateTimerDisplay() {
        const timerElement = document.getElementById('quizTimer');
        const progressElement = document.getElementById('timerProgress');
        
        if (timerElement) {
            timerElement.textContent = formatTime(this.timeLeft);
            
            // Change color when time is running out
            if (this.timeLeft < 300) {
                timerElement.style.color = '#dc3545';
            }
        }

        if (progressElement) {
            const totalTime = parseInt(progressElement.dataset.totalTime) * 60;
            const percentage = (this.timeLeft / totalTime) * 100;
            progressElement.style.width = percentage + '%';
        }
    }

    navigateQuestion(direction) {
        const questions = document.querySelectorAll('.question-card');
        const currentQuestion = document.querySelector('.question-card.active');
        
        if (!currentQuestion) return;

        const currentIndex = Array.from(questions).indexOf(currentQuestion);
        const newIndex = currentIndex + direction;

        if (newIndex >= 0 && newIndex < questions.length) {
            // Hide current question
            currentQuestion.classList.remove('active');
            currentQuestion.style.display = 'none';

            // Show new question
            questions[newIndex].classList.add('active');
            questions[newIndex].style.display = 'block';

            // Update navigation buttons
            this.updateNavigationButtons(newIndex, questions.length);
        }
    }

    updateNavigationButtons(currentIndex, totalQuestions) {
        const prevBtn = document.getElementById('prevQuestion');
        const nextBtn = document.getElementById('nextQuestion');
        const submitBtn = document.getElementById('submitQuiz');

        if (prevBtn) {
            prevBtn.style.display = currentIndex === 0 ? 'none' : 'block';
        }

        if (nextBtn) {
            nextBtn.style.display = currentIndex === totalQuestions - 1 ? 'none' : 'block';
        }

        if (submitBtn) {
            submitBtn.style.display = currentIndex === totalQuestions - 1 ? 'block' : 'none';
        }

        // Update progress
        const progress = ((currentIndex + 1) / totalQuestions) * 100;
        const progressBar = document.getElementById('quizProgress');
        if (progressBar) {
            progressBar.style.width = progress + '%';
            progressBar.textContent = `${currentIndex + 1}/${totalQuestions}`;
        }
    }

    initQuizSubmission() {
        const submitBtn = document.getElementById('submitQuiz');
        if (submitBtn) {
            submitBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.confirmSubmission();
            });
        }
    }

    confirmSubmission() {
        const unanswered = this.getUnansweredQuestions();
        
        if (unanswered.length > 0) {
            this.showUnansweredWarning(unanswered);
        } else {
            this.submitQuiz();
        }
    }

    getUnansweredQuestions() {
        const unanswered = [];
        const questions = document.querySelectorAll('.question-card');
        
        questions.forEach((question, index) => {
            const questionId = question.dataset.questionId;
            if (!this.answers[questionId]) {
                unanswered.push(index + 1);
            }
        });

        return unanswered;
    }

    showUnansweredWarning(unanswered) {
        const modal = new bootstrap.Modal(document.getElementById('unansweredModal'));
        const warningText = document.getElementById('unansweredWarning');
        
        warningText.textContent = `You have ${unanswered.length} unanswered question(s): ${unanswered.join(', ')}`;
        modal.show();

        document.getElementById('submitAnyway').addEventListener('click', () => {
            modal.hide();
            this.submitQuiz();
        });
    }

    submitQuiz() {
        if (this.timer) {
            clearInterval(this.timer);
        }

        // Collect all answers
        this.collectAnswers();

        // Show loading state
        quizApp.showToast('Submitting your quiz...', 'info');

        // Simulate API call - replace with actual submission
        setTimeout(() => {
            window.location.href = '/quiz/results?attempt=' + Date.now();
        }, 2000);
    }

    collectAnswers() {
        const form = document.getElementById('quizForm');
        if (form) {
            const formData = new FormData(form);
            this.answers = Object.fromEntries(formData);
        }
    }

    // For single page quiz with all questions
    initOptionSelection() {
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('option-item')) {
                const optionItem = e.target;
                const questionCard = optionItem.closest('.question-card');
                const questionId = questionCard.dataset.questionId;
                const optionIndex = optionItem.dataset.optionIndex;

                // Remove selected class from all options in this question
                questionCard.querySelectorAll('.option-item').forEach(item => {
                    item.classList.remove('selected');
                });

                // Add selected class to clicked option
                optionItem.classList.add('selected');

                // Update the hidden input
                const input = questionCard.querySelector(`input[name="q_${questionId}"]`);
                if (input) {
                    input.value = optionIndex;
                }

                // Store answer
                this.answers[questionId] = optionIndex;

                // Update progress
                this.updateProgress();
            }
        });
    }

    updateProgress() {
        const totalQuestions = document.querySelectorAll('.question-card').length;
        const answered = Object.keys(this.answers).length;
        const progress = (answered / totalQuestions) * 100;

        const progressBar = document.getElementById('completionProgress');
        if (progressBar) {
            progressBar.style.width = progress + '%';
            progressBar.textContent = `${answered}/${totalQuestions}`;
        }
    }
}

// Initialize quiz manager
document.addEventListener('DOMContentLoaded', function() {
    if (document.querySelector('.quiz-container')) {
        window.quizManager = new QuizManager();
    }
});