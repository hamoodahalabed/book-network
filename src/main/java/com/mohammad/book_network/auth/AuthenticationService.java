package com.mohammad.book_network.auth;

import com.mohammad.book_network.email.EmailService;
import com.mohammad.book_network.exceptions.InvalidTokenException;
import com.mohammad.book_network.role.RoleRepository;
import com.mohammad.book_network.security.JwtService;
import com.mohammad.book_network.user.Token;
import com.mohammad.book_network.user.TokenRepository;
import com.mohammad.book_network.user.User;
import com.mohammad.book_network.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mohammad.book_network.email.EmailTemplateName;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void registerUser(RegistrationRequest registrationRequest) {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("User role not found")); // todo better exception handling

        var user = User.builder()
                .firstname(registrationRequest.firstname())
                .lastname(registrationRequest.lastname())
                .email(registrationRequest.email())
                .password(passwordEncoder.encode(registrationRequest.password()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        try {
            sendValidationEmail(user);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();

        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomChar = secureRandom.nextInt(characters.length()); // 0..9
            codeBuilder.append(characters.charAt(randomChar));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        User saveduser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!saveduser.isEnabled()) {
            throw new DisabledException("User account is not enabled");
        }


        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email(), request.password())
        );

        var claims = new HashMap<String,Object>();
        var user = ((User)auth.getPrincipal());
        claims.put("fullName",user.getFullName());
        var jwt = jwtService.generateToken(claims,user);
        return AuthenticationResponse.builder().token(jwt).build();
    }


   // @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token not founded"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new InvalidTokenException("Activation token has expired. A new token has been send to the same email address");
        }

        var user = userRepository.findById(Long.valueOf(savedToken.getUser().getId()))
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
