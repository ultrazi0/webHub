package com.nemo.webHub.Onion;

import com.nemo.webHub.Decibel.UserEntity;
import com.nemo.webHub.Decibel.UserRepository;
import com.nemo.webHub.Sect.UserRepositoryUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserApiController {

    @Autowired
    private UserRepository userRepository;

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
                "{noop}" + registerRequest.password());  // TODO: fix password later

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
        System.out.println(editRequest);
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
            newUser = userRepository.updateUserPassword(user.getId(), oldPassword, newPassword);
        } else if (newPassword == null) {
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
