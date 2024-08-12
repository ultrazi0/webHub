package com.nemo.webHub.Onion;

import com.nemo.webHub.Decibel.UserEntity;
import com.nemo.webHub.Decibel.UserRepository;
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

    @PostMapping("/register")
    public ResponseEntity<UserEntity> registerNewUser(@ModelAttribute LoginRequest registerRequest) {
        UserEntity user = userRepository.addNewUser(registerRequest.username(),
                "{noop}" + registerRequest.password());  // TODO: fix password later

        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/user")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserEntity user) {
        userRepository.deleteUser(user.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public UserEntity getUser(@AuthenticationPrincipal UserEntity user) {
        return user;
    }

    public record LoginRequest(String username, String password) {}
}
