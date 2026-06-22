package com.bazario.api.service;

import com.bazario.api.model.entity.User;
import com.bazario.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user-specific data during authentication process.
     *
     * This method is called by Spring Security to retrieve user details
     * from the database using email as the username.
     *
     * Steps performed:
     * - Fetch user from database by email
     * - Throw exception if user not found
     * - Convert user roles into Spring Security authorities
     * - Return Spring Security User object with credentials and authorities
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(
                        "User not found with email: " + email));


        List<SimpleGrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());


        return  new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}
