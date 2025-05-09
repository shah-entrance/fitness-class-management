package com.fitness.service.mock;

import com.fitness.model.User;
import com.fitness.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockUserRepository implements UserRepository {
    private List<User> users = new ArrayList<>();

    @Override
    public User save(User user) {
        // Remove existing user if found
        users.removeIf(u -> u.getId().equals(user.getId()));
        // Add new user
        users.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }
    
    @Override
    public void delete(String id) {
        users.removeIf(u -> u.getId().equals(id));
    }

    public void clear() {
        users.clear();
    }
}