package ru.matveyder.NauJava.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.matveyder.NauJava.entity.User;
import ru.matveyder.NauJava.repository.UserRepository;
import ru.matveyder.NauJava.service.UserService;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(Long id, String login, String password) {
        userRepository.create(new User(id, login, password));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.read(id);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.read(id);
        if (user != null) {
            user.setPassword(newPassword);
            userRepository.update(user);
        }
    }
}