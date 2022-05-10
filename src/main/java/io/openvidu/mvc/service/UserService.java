package io.openvidu.mvc.service;

import io.openvidu.dto.LoginDto;
import io.openvidu.dto.SignUpDto;
import io.openvidu.entity.User;
import io.openvidu.mvc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public User findUserByEmail(String email){
        Optional<User> findUser = userRepository.findByEmail(email);
        return findUser.orElse(null);
    }

    public User findUserByUsername(String username){
        Optional<User> findUser = userRepository.findByUsername(username);
        return findUser.orElse(null);
    }

    public User findUserById(Long id){
        Optional<User> findUser = userRepository.findById(id);
        return findUser.orElse(null);
    }

    public User login(LoginDto user){
        Optional<User> findUser = userRepository.findByEmail(user.getEmail());
        return findUser
                .filter(u -> encoder.matches(user.getPassword(), u.getPassword()))
                .orElse(null);
    }

    public User save(SignUpDto user){
        Optional<User> findUser = userRepository.findByEmail(user.getEmail());
        if (findUser.isPresent()) {
            return null;
        }
        user.setEncoder(encoder);
        user.encodePassword();
        return userRepository.save(new User(user));
    }

}
