package com.nemo.rexus.Sect;

import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Decibel.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;


/**
 * Custom {@link UserDetailsService} to hold {@link UserEntity} objects to provide a better
 * representation of what is stored in the database, but primarily to store and to be able
 * to access the user IDs.
 * */
@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user found");
        }
        return new User(user).roles(user.getRoles());
    }

    static final class User extends UserEntity implements UserDetails, CredentialsContainer {

        private final Set<GrantedAuthority> authorities;

        public User(UserEntity user) {
            super(user.getId(), user.getUsername(), user.getPassword(), user.getRoles(), user.getCreatedAt());
            this.authorities = new HashSet<>();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        public User roles(String... roles) {

            for (String role : roles) {
                Assert.isTrue(!role.startsWith("ROLE_"), role + " cannot start with ROLE_ (is automatically added)");
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }

            return this;
        }

        @Override
        public void eraseCredentials() {
            setPassword(null);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof User) {
                return ((User) obj).getId() == this.getId();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.getUsername().hashCode();
        }
    }
}
