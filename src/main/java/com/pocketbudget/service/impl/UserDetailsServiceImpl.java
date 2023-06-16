package com.pocketbudget.service.impl;

import com.pocketbudget.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.pocketbudget.model.entity.User user = this.userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with name " + username + " was not found!"));
        return this.mapToUserDetails(user);
    }

    private UserDetails mapToUserDetails(com.pocketbudget.model.entity.User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().name()))
                .collect(Collectors.toList());

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
