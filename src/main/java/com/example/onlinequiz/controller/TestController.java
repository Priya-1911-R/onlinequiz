package com.example.onlinequiz.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.onlinequiz.model.Option;
import com.example.onlinequiz.model.Question;
import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.service.QuizService;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/create-sample-quiz")
    public String createSampleQuiz(@AuthenticationPrincipal User user, Model model) {
        try {
            if (user == null) {
                return "redirect:/login";
            }

            // Create a sample quiz
            Quiz quiz = new Quiz();
            quiz.setTitle("Sample Programming Quiz");
            quiz.setDescription("A sample quiz to test the quiz functionality");
            quiz.setTimeLimit(30);
            quiz.setActive(true);
            quiz.setPublicQuiz(true);
            quiz.setCreatedBy(user);

            // Create questions with Option entities
            List<Question> questions = new ArrayList<>();

            // Question 1
            Question question1 = new Question();
            question1.setText("What does HTML stand for?");
            question1.setQuiz(quiz);
            
            // Create options for question 1
            List<Option> options1 = new ArrayList<>();
            options1.add(createOption("Hyper Text Markup Language", true, question1));
            options1.add(createOption("High Tech Modern Language", false, question1));
            options1.add(createOption("Hyper Transfer Markup Language", false, question1));
            options1.add(createOption("Home Tool Markup Language", false, question1));
            question1.setOptions(options1);
            questions.add(question1);

            // Question 2
            Question question2 = new Question();
            question2.setText("Which tag is used for the largest heading in HTML?");
            question2.setQuiz(quiz);
            
            // Create options for question 2
            List<Option> options2 = new ArrayList<>();
            options2.add(createOption("<h1>", true, question2));
            options2.add(createOption("<heading>", false, question2));
            options2.add(createOption("<head>", false, question2));
            options2.add(createOption("<h6>", false, question2));
            question2.setOptions(options2);
            questions.add(question2);

            // Question 3
            Question question3 = new Question();
            question3.setText("Which language is used for styling web pages?");
            question3.setQuiz(quiz);
            
            // Create options for question 3
            List<Option> options3 = new ArrayList<>();
            options3.add(createOption("CSS", true, question3));
            options3.add(createOption("HTML", false, question3));
            options3.add(createOption("JavaScript", false, question3));
            options3.add(createOption("Python", false, question3));
            question3.setOptions(options3);
            questions.add(question3);

            // Question 4
            Question question4 = new Question();
            question4.setText("What is the correct way to declare a JavaScript variable?");
            question4.setQuiz(quiz);
            
            // Create options for question 4
            List<Option> options4 = new ArrayList<>();
            options4.add(createOption("var variableName;", true, question4));
            options4.add(createOption("variable variableName;", false, question4));
            options4.add(createOption("v variableName;", false, question4));
            options4.add(createOption("let var variableName;", false, question4));
            question4.setOptions(options4);
            questions.add(question4);

            // Question 5
            Question question5 = new Question();
            question5.setText("Which symbol is used for single-line comments in JavaScript?");
            question5.setQuiz(quiz);
            
            // Create options for question 5
            List<Option> options5 = new ArrayList<>();
            options5.add(createOption("//", true, question5));
            options5.add(createOption("<!-- -->", false, question5));
            options5.add(createOption("/* */", false, question5));
            options5.add(createOption("#", false, question5));
            question5.setOptions(options5);
            questions.add(question5);

            quiz.setQuestions(questions);

            // Save the quiz
            Quiz savedQuiz = quizService.createQuiz(quiz);

            model.addAttribute("message", "Sample quiz created successfully with ID: " + savedQuiz.getId());
            model.addAttribute("quiz", savedQuiz);
            
            return "test/success";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to create sample quiz: " + e.getMessage());
            return "test/error";
        }
    }

    @GetMapping("/create-simple-quiz")
    public String createSimpleQuiz(@AuthenticationPrincipal User user, Model model) {
        try {
            if (user == null) {
                return "redirect:/login";
            }

            // Create a simple quiz with minimal questions
            Quiz quiz = new Quiz();
            quiz.setTitle("Basic Math Quiz");
            quiz.setDescription("A simple math quiz for testing");
            quiz.setTimeLimit(15);
            quiz.setActive(true);
            quiz.setPublicQuiz(true);
            quiz.setCreatedBy(user);

            // Create questions with Option entities
            List<Question> questions = new ArrayList<>();

            // Math Question 1
            Question q1 = new Question();
            q1.setText("What is 2 + 2?");
            q1.setQuiz(quiz);
            q1.setOptions(Arrays.asList(
                createOption("3", false, q1),
                createOption("4", true, q1),
                createOption("5", false, q1),
                createOption("6", false, q1)
            ));
            questions.add(q1);

            // Math Question 2
            Question q2 = new Question();
            q2.setText("What is 10 ÷ 2?");
            q2.setQuiz(quiz);
            q2.setOptions(Arrays.asList(
                createOption("2", false, q2),
                createOption("5", true, q2),
                createOption("8", false, q2),
                createOption("10", false, q2)
            ));
            questions.add(q2);

            quiz.setQuestions(questions);

            // Save the quiz
            Quiz savedQuiz = quizService.createQuiz(quiz);

            model.addAttribute("message", "Simple quiz created successfully with ID: " + savedQuiz.getId());
            model.addAttribute("quiz", savedQuiz);
            
            return "test/success";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to create simple quiz: " + e.getMessage());
            return "test/error";
        }
    }

    @GetMapping("/list-quizzes")
    public String listAllQuizzes(Model model) {
        try {
            List<Quiz> quizzes = quizService.getAllQuizzes();
            model.addAttribute("quizzes", quizzes);
            model.addAttribute("count", quizzes.size());
            return "test/quiz-list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to load quizzes: " + e.getMessage());
            return "test/error";
        }
    }

    @GetMapping("/clear-quizzes")
    public String clearAllQuizzes(@AuthenticationPrincipal User user, Model model) {
        try {
            if (user == null) {
                return "redirect:/login";
            }

            // This is a dangerous operation - in real application, add proper authorization
            List<Quiz> userQuizzes = quizService.getQuizzesByUser(user);
            for (Quiz quiz : userQuizzes) {
                quizService.deleteQuiz(quiz.getId());
            }

            model.addAttribute("message", "Deleted " + userQuizzes.size() + " quizzes");
            return "test/success";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to clear quizzes: " + e.getMessage());
            return "test/error";
        }
    }

    // Helper method to create Option entities
    private Option createOption(String text, boolean isCorrect, Question question) {
        Option option = new Option();
        option.setText(text);
        option.setCorrect(isCorrect);
        option.setQuestion(question);
        return option;
    }
}