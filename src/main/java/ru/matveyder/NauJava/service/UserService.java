package ru.matveyder.NauJava.service;

import ru.matveyder.NauJava.entity.User;
import java.util.List;

public interface UserService {
    void registerUser(Long id, String login, String password);
    User getUserById(Long id);
    void deleteUser(Long id);
    List<User> getAllUsers();
    void changePassword(Long id, String newPassword);
}