package ru.polukhin.learningplatform.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.polukhin.learningplatform.entity.RoleType;
import ru.polukhin.learningplatform.entity.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUser() {
        // Given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(RoleType.STUDENT)
                .active(true)
                .build();

        // When
        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindUserByUsername() {
        // Given
        User user = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .role(RoleType.STUDENT)
                .active(true)
                .build();
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByUsername("john_doe");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldFindUsersByRole() {
        // Given
        User student1 = createUser("student1", "s1@example.com", RoleType.STUDENT);
        User student2 = createUser("student2", "s2@example.com", RoleType.STUDENT);
        User instructor = createUser("instructor1", "i1@example.com", RoleType.INSTRUCTOR);
        
        userRepository.saveAll(List.of(student1, student2, instructor));

        // When
        List<User> students = userRepository.findByRole(RoleType.STUDENT);
        List<User> instructors = userRepository.findByRole(RoleType.INSTRUCTOR);

        // Then
        assertThat(students).hasSize(2);
        assertThat(instructors).hasSize(1);
    }

    @Test
    void shouldCheckIfUsernameExists() {
        // Given
        User user = createUser("existinguser", "existing@example.com", RoleType.STUDENT);
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByUsername("existinguser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldUpdateUser() {
        // Given
        User user = createUser("updatetest", "update@example.com", RoleType.STUDENT);
        User saved = userRepository.save(user);

        // When
        saved.setFirstName("Updated");
        saved.setLastName("Name");
        User updated = userRepository.save(saved);

        // Then
        assertThat(updated.getFirstName()).isEqualTo("Updated");
        assertThat(updated.getLastName()).isEqualTo("Name");
    }

    @Test
    void shouldDeleteUser() {
        // Given
        User user = createUser("deletetest", "delete@example.com", RoleType.STUDENT);
        User saved = userRepository.save(user);
        Long userId = saved.getId();

        // When
        userRepository.deleteById(userId);
        Optional<User> found = userRepository.findById(userId);

        // Then
        assertThat(found).isEmpty();
    }

    private User createUser(String username, String email, RoleType role) {
        return User.builder()
                .username(username)
                .email(email)
                .password("password")
                .firstName("First")
                .lastName("Last")
                .role(role)
                .active(true)
                .build();
    }
}
