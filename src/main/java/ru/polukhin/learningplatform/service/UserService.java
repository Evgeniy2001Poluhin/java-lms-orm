package ru.polukhin.learningplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polukhin.learningplatform.entity.RoleType;
import ru.polukhin.learningplatform.entity.User;
import ru.polukhin.learningplatform.exception.DuplicateResourceException;
import ru.polukhin.learningplatform.exception.ResourceNotFoundException;
import ru.polukhin.learningplatform.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        log.debug("Fetching user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public User getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    public List<User> getUsersByRole(RoleType role) {
        log.debug("Fetching users by role: {}", role);
        return userRepository.findByRole(role);
    }

    public List<User> getActiveUsers() {
        log.debug("Fetching all active users");
        return userRepository.findByActiveTrue();
    }

    @Transactional
    public User createUser(User user) {
        log.info("Creating new user with username: {}", user.getUsername());
        
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("User", "username", user.getUsername());
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }
        
        user.setActive(true);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with id: {}", id);
        
        User user = getUserById(id);
        
        if (!user.getUsername().equals(userDetails.getUsername()) &&
                userRepository.existsByUsername(userDetails.getUsername())) {
            throw new DuplicateResourceException("User", "username", userDetails.getUsername());
        }
        
        if (!user.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new DuplicateResourceException("User", "email", userDetails.getEmail());
        }
        
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setBio(userDetails.getBio());
        user.setProfileImageUrl(userDetails.getProfileImageUrl());
        
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        User user = getUserById(id);
        userRepository.delete(user);
    }

    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user with id: {}", id);
        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }
}
