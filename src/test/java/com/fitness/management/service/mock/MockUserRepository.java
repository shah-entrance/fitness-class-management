package com.fitness.management.service.mock;

import com.fitness.management.model.User;
import com.fitness.management.repository.UserRepository;

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
    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public void clear() {
        users.clear();
    }
}