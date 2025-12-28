package com.furkan.smart_library_backend.user;

import com.furkan.smart_library_backend.user.dto.UserCreateRequest;
import com.furkan.smart_library_backend.user.dto.UserResponse;
import com.furkan.smart_library_backend.user.dto.UserUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        try {
            return userRepository.findAll().stream()
                    .filter(user -> !user.isDeleted())
                    .map(UserResponse::fromEntity)
                    .toList();
        } catch (Exception e) {
            log.error("Error in UserService.getAllUsers", e);
            throw new RuntimeException("Failed to get all users", e);
        }
    }

    public UserResponse getUserById(UUID id) {
        try {
            User user = userRepository.findByIdAndDeletedFalse(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
            return UserResponse.fromEntity(user);
        } catch (Exception e) {
            log.error("Error in UserService.getUserById id={}", id, e);
            throw new RuntimeException("Failed to get user by id", e);
        }
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        try {
            if (userRepository.findByEmailAndDeletedFalse(request.email()).isPresent()) {
                throw new IllegalArgumentException("Email already in use");
            }

            User user = User.builder()
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .fullName(request.fullName())
                    .role(request.role())
                    .deleted(false)
                    .build();

            User savedUser = userRepository.save(user);
            return UserResponse.fromEntity(savedUser);
        } catch (Exception e) {
            log.error("Error in UserService.createUser request={}", request, e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        try {
            User userToUpdate = userRepository.findByIdAndDeletedFalse(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName() == null) {
                throw new RuntimeException("Authenticated user not found in security context");
            }
            String currentPrincipalName = auth.getName();

            User currentUser = userRepository.findByEmailAndDeletedFalse(currentPrincipalName)
                    .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));

            if (currentUser.getRole() != Role.ADMIN && !currentUser.getId().equals(id)) {
                throw new AccessDeniedException("Başkalarının profilini güncelleyemezsiniz.");
            }

            if (!userToUpdate.getEmail().equals(request.email()) &&
                    userRepository.existsByEmailAndDeletedFalse(request.email())) {
                throw new IllegalArgumentException("Email already in use");
            }

            userToUpdate.setEmail(request.email());
            userToUpdate.setFullName(request.fullName());

            if (request.role() != null && currentUser.getRole() == Role.ADMIN) {
                userToUpdate.setRole(request.role());
            }

            User updatedUser = userRepository.save(userToUpdate);
            return UserResponse.fromEntity(updatedUser);
        } catch (Exception e) {
            log.error("Error in UserService.updateUser id={}", id, e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Transactional
    public void deleteUser(UUID id) {
        try {
            User user = userRepository.findByIdAndDeletedFalse(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            user.setDeleted(true);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error in UserService.deleteUser id={}", id, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}