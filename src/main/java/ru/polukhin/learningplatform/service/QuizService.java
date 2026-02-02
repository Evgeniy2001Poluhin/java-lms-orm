package ru.polukhin.learningplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polukhin.learningplatform.entity.*;
import ru.polukhin.learningplatform.exception.BusinessException;
import ru.polukhin.learningplatform.exception.DuplicateResourceException;
import ru.polukhin.learningplatform.exception.ResourceNotFoundException;
import ru.polukhin.learningplatform.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;

    public List<Quiz> getAllQuizzes() {
        log.debug("Fetching all quizzes");
        return quizRepository.findAll();
    }

    public Quiz getQuizById(Long id) {
        log.debug("Fetching quiz by id: {}", id);
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
    }

    @Transactional
    public Quiz getQuizWithQuestions(Long id) {
        log.debug("Fetching quiz with questions by id: {}", id);
        Quiz quiz = quizRepository.findByIdWithFullStructure(id);
        if (quiz == null) {
            throw new ResourceNotFoundException("Quiz", id);
        }
        return quiz;
    }

    @Transactional
    public Quiz createQuiz(Quiz quiz) {
        log.info("Creating new quiz: {}", quiz.getTitle());
        return quizRepository.save(quiz);
    }

    @Transactional
    public Quiz updateQuiz(Long id, Quiz quizDetails) {
        log.info("Updating quiz with id: {}", id);
        
        Quiz quiz = getQuizById(id);
        quiz.setTitle(quizDetails.getTitle());
        quiz.setDescription(quizDetails.getDescription());
        quiz.setPassingScore(quizDetails.getPassingScore());
        quiz.setTimeLimitMinutes(quizDetails.getTimeLimitMinutes());
        
        return quizRepository.save(quiz);
    }

    @Transactional
    public void deleteQuiz(Long id) {
        log.info("Deleting quiz with id: {}", id);
        Quiz quiz = getQuizById(id);
        quizRepository.delete(quiz);
    }

    @Transactional
    public Question addQuestionToQuiz(Long quizId, Question question) {
        log.info("Adding question to quiz id: {}", quizId);
        
        Quiz quiz = getQuizById(quizId);
        question.setQuiz(quiz);
        
        if (question.getOrderIndex() == null) {
            question.setOrderIndex(quiz.getQuestions().size());
        }
        
        return questionRepository.save(question);
    }

    @Transactional
    public AnswerOption addAnswerOptionToQuestion(Long questionId, AnswerOption answerOption) {
        log.info("Adding answer option to question id: {}", questionId);
        
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));
        
        answerOption.setQuestion(question);
        
        if (answerOption.getOrderIndex() == null) {
            answerOption.setOrderIndex(question.getAnswerOptions().size());
        }
        
        return answerOptionRepository.save(answerOption);
    }

    @Transactional
    public QuizSubmission takeQuiz(Long quizId, Long studentId, Map<Long, List<Long>> answers, Integer timeTaken) {
        log.info("Student {} taking quiz {}", studentId, quizId);
        
        Quiz quiz = getQuizWithQuestions(quizId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", studentId));
        
        if (student.getRole() != RoleType.STUDENT) {
            throw new BusinessException("User is not a student");
        }
        
        if (quizSubmissionRepository.existsByStudentAndQuiz(student, quiz)) {
            throw new DuplicateResourceException("Quiz already taken by this student");
        }
        
        // Calculate score
        int totalScore = 0;
        int maxScore = 0;
        
        for (Question question : quiz.getQuestions()) {
            maxScore += question.getPoints();
            
            List<Long> studentAnswers = answers.get(question.getId());
            if (studentAnswers == null) continue;
            
            List<AnswerOption> correctAnswers = answerOptionRepository
                    .findCorrectAnswersByQuestionId(question.getId());
            
            boolean isCorrect = checkAnswer(question, studentAnswers, correctAnswers);
            if (isCorrect) {
                totalScore += question.getPoints();
            }
        }
        
        double percentageScore = (totalScore * 100.0) / maxScore;
        boolean passed = percentageScore >= quiz.getPassingScore();
        
        QuizSubmission submission = QuizSubmission.builder()
                .student(student)
                .quiz(quiz)
                .score(totalScore)
                .maxScore(maxScore)
                .percentageScore(percentageScore)
                .passed(passed)
                .submittedAt(LocalDateTime.now())
                .timeTakenMinutes(timeTaken)
                .build();
        
        return quizSubmissionRepository.save(submission);
    }

    private boolean checkAnswer(Question question, List<Long> studentAnswers, List<AnswerOption> correctAnswers) {
        if (question.getType() == QuestionType.SINGLE_CHOICE) {
            if (studentAnswers.size() != 1) return false;
            Long studentAnswerId = studentAnswers.get(0);
            return correctAnswers.stream().anyMatch(a -> a.getId().equals(studentAnswerId));
        } else if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
            if (studentAnswers.size() != correctAnswers.size()) return false;
            return studentAnswers.containsAll(correctAnswers.stream().map(AnswerOption::getId).toList());
        } else if (question.getType() == QuestionType.TRUE_FALSE) {
            if (studentAnswers.size() != 1) return false;
            Long studentAnswerId = studentAnswers.get(0);
            return correctAnswers.stream().anyMatch(a -> a.getId().equals(studentAnswerId));
        }
        return false;
    }

    public List<QuizSubmission> getSubmissionsByStudent(Long studentId) {
        log.debug("Fetching quiz submissions for student id: {}", studentId);
        return quizSubmissionRepository.findByStudentId(studentId);
    }

    public List<QuizSubmission> getSubmissionsByQuiz(Long quizId) {
        log.debug("Fetching quiz submissions for quiz id: {}", quizId);
        return quizSubmissionRepository.findByQuizId(quizId);
    }
}
