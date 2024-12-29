package com.app.consMed.Model.User.Service;

import com.app.consMed.Model.User.DTOs.LoginResponseDTO;
import com.app.consMed.Model.User.DTOs.RegisterDTO;
import com.app.consMed.Model.User.Domain.User;
import com.app.consMed.Model.User.Repository.UserRepository;
import com.app.consMed.Security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(RegisterDTO json) {
        if (userRepository.findByLogin(json.login()) != null) {
            throw new IllegalArgumentException("Usuário já existe!");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(json.password());
        User newUser = new User(json.login(), encryptedPassword, json.role());
        return userRepository.save(newUser);
    }

    public LoginResponseDTO login(String login, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password)
        );

        User user = (User) auth.getPrincipal();
        String token = tokenService.generateToken(user);

        return new LoginResponseDTO(token);
    }

    public Optional<UserDetails> findByLogin(String login) {
        return Optional.of(userRepository.findByLogin(login));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUserByLogin(String login) {
        userRepository.deleteByLogin(login);
    }
    @Transactional
    public User updateUserByLogin(String login, RegisterDTO json) {
        Optional<User> optionalUser = Optional.of((User) userRepository.findByLogin(login));
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado!");
        }

        User oldUser = optionalUser.get();

        if (userRepository.findByLogin(json.login()) != null && json.login() != oldUser.getLogin()) {
            throw new IllegalArgumentException("Login já existente!");
        }

        String encryptedPassword = passwordEncoder.encode(json.password());

        oldUser.setLogin(json.login());
        oldUser.setPassword(encryptedPassword);
        oldUser.setRole(json.role());

        return userRepository.save(oldUser);
    }
}
