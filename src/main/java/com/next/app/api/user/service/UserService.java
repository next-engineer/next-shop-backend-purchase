package com.next.app.api.user.service;

import com.next.app.api.user.entity.User;
import com.next.app.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<User> listUsersAny() {
        return userRepository.findAllRaw();
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAllByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findByIdAndDeletedFalse(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByIdAny(Long id) {
        return userRepository.findRawById(id);
    }



    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        if (userRepository.existsByEmailAndDeletedFalse(user.getEmail())) {
            throw new RuntimeException("이미 가입된 이메일입니다: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDeleted(false);
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("수정할 사용자가 존재하지 않거나 삭제된 계정입니다: " + id));

        //user.setEmail(userDetails.getEmail());
        if (userDetails.getName() != null) user.setName(userDetails.getName());
        if (userDetails.getPhone_number() != null) user.setPhone_number(userDetails.getPhone_number());
        if (userDetails.getDelivery_address() != null) user.setDelivery_address(userDetails.getDelivery_address());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("삭제할 사용자가 존재하지 않습니다: " + id));
        user.setDeleted(true);
        userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new RuntimeException("이메일이 존재하지 않습니다: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}