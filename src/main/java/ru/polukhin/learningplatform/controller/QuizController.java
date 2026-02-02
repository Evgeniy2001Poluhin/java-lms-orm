package ru.polukhin.learningplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.polukhin.learningplatform.entity.*;
import ru.polukhin.learningplatform.service.QuizService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quizzes", description = "Quiz and testing management APIs")
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    @Operation(summary = "Get all quizzes")
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    @GetMapping("/{id}/with-questions")
    @Operation(summary = "Get quiz with questions and answers")
    public ResponseEntity<Quiz> getQuizWithQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizWithQuestions(id));
    }

    @PostMapping
    @Operation(summary = "Create a new quiz")
    public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody Quiz quiz) {
        Quiz created = quizService.createQuiz(quiz);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update quiz")
    public ResponseEntity<Quiz> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody Quiz quiz) {
        return ResponseEntity.ok(quizService.updateQuiz(id, quiz));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quiz")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/questions")
    @Operation(summary = "Add question to quiz")
    public ResponseEntity<Question> addQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody Question question) {
        Question created = quizService.addQuestionToQuiz(quizId, question);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/questions/{questionId}/answers")
    @Operation(summary = "Add answer option to question")
    public ResponseEntity<AnswerOption> addAnswerOption(
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerOption answerOption) {
        AnswerOption created = quizService.addAnswerOptionToQuestion(questionId, answerOption);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{quizId}/take")
    @Operation(summary = "Take quiz (submit answers)")
    public ResponseEntity<QuizSubmission> takeQuiz(
            @PathVariable Long quizId,
            @RequestParam Long studentId,
            @RequestBody Map<Long, List<Long>> answers,
            @RequestParam(required = false) Integer timeTaken) {
        QuizSubmission submission = quizService.takeQuiz(quizId, studentId, answers, timeTaken);
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    @GetMapping("/submissions/student/{studentId}")
    @Operation(summary = "Get quiz submissions by student")
    public ResponseEntity<List<QuizSubmission>> getSubmissionsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(quizService.getSubmissionsByStudent(studentId));
    }

    @GetMapping("/{quizId}/submissions")
    @Operation(summary = "Get quiz submissions for quiz")
    public ResponseEntity<List<QuizSubmission>> getSubmissionsByQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getSubmissionsByQuiz(quizId));
    }
}
