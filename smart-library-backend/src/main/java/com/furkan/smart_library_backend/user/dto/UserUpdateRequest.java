package com.furkan.smart_library_backend.user.dto;

import com.furkan.smart_library_backend.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Full name cannot be empty")
        String fullName,

        Role role
) {}