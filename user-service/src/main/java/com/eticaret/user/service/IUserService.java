package com.eticaret.user.service;

import com.eticaret.user.model.User;

import java.util.List;

public interface IUserService {
    User createUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}

