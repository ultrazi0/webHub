package com.nemo.webHub.Sect;

import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.RobotNotFoundException;
import com.nemo.webHub.Decibel.RobotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom {@link UserDetailsService} to hold {@link RobotEntity} objects.
 * It is primarily necessary to store and be able to access the robot IDs.
 * */
@Service
@RequiredArgsConstructor
public class RobotRepositoryUserDetailsService implements UserDetailsService {

    private final RobotRepository robotRepository;

    @Override
    public UserDetails loadUserByUsername(String robotIdString) throws UsernameNotFoundException {
        int robotId;
        RobotEntity robot;
        try {
            robotId = Integer.parseInt(robotIdString);
            robot = RobotEntity.of(robotRepository.findRobotById(robotId));
        } catch (NumberFormatException | RobotNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }

        return new Robot(robot);
    }

    static final class Robot extends RobotEntity implements UserDetails, CredentialsContainer {

        private final Set<GrantedAuthority> authorities;

        public Robot(RobotEntity robot) {
            super(robot.getId(), robot.getName(), robot.getPassword(), robot.getCreatedAt(), robot.getOwner().getId());
            this.authorities = new HashSet<>();
            this.authorities.add(new SimpleGrantedAuthority("ROLE_ROBOT"));
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getUsername() {
            return this.getName();
        }

        @Override
        public void eraseCredentials() {
            setPassword(null);
        }
    }
}
