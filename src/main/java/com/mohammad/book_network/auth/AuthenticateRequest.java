package com.mohammad.book_network.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticateRequest (
        @Email(message = "Please enter valid email")
        String email,
        @NotBlank(message = "Password cant be null")
        @Size(min = 8,message = "Password should be 8 characters long min")
        String password
) {
}
