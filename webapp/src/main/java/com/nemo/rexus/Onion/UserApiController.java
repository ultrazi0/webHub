package com.nemo.rexus.Onion;

import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Decibel.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserApiController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/csrf")
    public ResponseEntity<CsrfToken> getCsrfToken(CsrfToken token) {
        return ResponseEntity.ok().body(token);
    }

    @GetMapping("/user")
    public UserEntity getUser(@AuthenticationPrincipal UserEntity user) {
        return user;
    }

    @GetMapping("/user/{userId}")
    public UserEntity getUserById(@PathVariable int userId) {
        return userRepository.findUserById(userId);
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> registerNewUser(@ModelAttribute LoginRequest registerRequest,
                                                      HttpServletRequest request) throws ServletException {
        UserEntity user = userRepository.addNewUser(registerRequest.username(),
                passwordEncoder.encode(registerRequest.password()));

        request.login(user.getUsername(), registerRequest.password());
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/user")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserEntity user,
                                           HttpServletRequest request) throws ServletException {
        userRepository.deleteUser(user.getId());

        request.logout();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/user")
    public ResponseEntity<UserEntity> updateUser(@AuthenticationPrincipal UserEntity user, @ModelAttribute EditRequest editRequest) {
        String newUsername = editRequest.username();
        String oldPassword = editRequest.oldPassword();
        String newPassword = editRequest.newPassword();

        UserEntity newUser;

        if (newPassword != null && newPassword.isBlank()) {
            newPassword = null;
        }

        if (newUsername == null && newPassword == null ||
                oldPassword == null || oldPassword.equals(newPassword)) {
            return ResponseEntity.badRequest().build();
        }

        if (newUsername == null) {
            newUsername = user.getUsername();
        }

        if (newPassword == null) {
            newUser = userRepository.updateUserUsername(user.getId(), newUsername, oldPassword);
        } else {
            newUser = userRepository.updateUser(user.getId(), newUsername, oldPassword, newPassword);
        }

        user.updateUser(newUser);

        return ResponseEntity.ok(user);
    }

    public record LoginRequest(String username, String password) {}

    public record EditRequest(String username, String oldPassword, String newPassword) {}
}
