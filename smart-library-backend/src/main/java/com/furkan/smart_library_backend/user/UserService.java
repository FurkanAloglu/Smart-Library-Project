package com.furkan.smart_library_backend.user;

import com.furkan.smart_library_backend.user.dto.UserCreateRequest;
import com.furkan.smart_library_backend.user.dto.UserResponse;
import com.furkan.smart_library_backend.user.dto.UserUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isDeleted())
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
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
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        // 1. Kullanıcıyı bul
        User userToUpdate = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 2. İşlemi yapan kim? (Security Context'ten al)
        String currentPrincipalName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmailAndDeletedFalse(currentPrincipalName)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));

        // 3. GÜVENLİK KONTROLÜ: Başkasını güncellemeye çalışıyor mu?
        // Eğer işlem yapan ADMIN değilse VE güncellemeye çalıştığı ID kendi ID'si değilse -> HATA
        if (currentUser.getRole() != Role.ADMIN && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Başkalarının profilini güncelleyemezsiniz.");
        }

        // Email çakışma kontrolü
        if (!userToUpdate.getEmail().equals(request.email()) &&
                userRepository.existsByEmailAndDeletedFalse(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Bilgileri güncelle
        userToUpdate.setEmail(request.email());
        userToUpdate.setFullName(request.fullName());

        // 4. ROL GÜNCELLEME GÜVENLİĞİ:
        // Sadece ADMIN rol değiştirebilir. Normal kullanıcı kendi rolünü değiştiremez.
        if (request.role() != null && currentUser.getRole() == Role.ADMIN) {
            userToUpdate.setRole(request.role());
        }
        // Eğer kullanıcı ADMIN değilse ve role göndermişse, bu istek sessizce yoksayılır (veya hata fırlatılabilir).
        // Şu anki haliyle role değişikliği uygulanmaz.

        User updatedUser = userRepository.save(userToUpdate);
        return UserResponse.fromEntity(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setDeleted(true);
        userRepository.save(user);
    }
}