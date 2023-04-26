package com.example.medcheckb8.db.service.impl;

import com.example.medcheckb8.db.dto.request.ProfileRequest;
import com.example.medcheckb8.db.dto.response.ProfileResponse;
import com.example.medcheckb8.db.dto.response.SimpleResponse;
import com.example.medcheckb8.db.dto.response.UserResponse;
import com.example.medcheckb8.db.entities.User;
import com.example.medcheckb8.db.exceptions.NotFountException;
import com.example.medcheckb8.db.repository.UserRepository;
import com.example.medcheckb8.db.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserResponse> getAllPatients(String word) {
        if (word == null) {
            return repository.getAllPatients();
        }
        return repository.getAllPatients(word);
    }

    @Override
    public SimpleResponse getProfile(Long id, ProfileRequest request) {
        User user = repository.findById(id).orElseThrow(() -> new NotFountException(String.format("User with email: %s not found!", id)));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.getAccount().setEmail(request.email());
        repository.save(user);
        return SimpleResponse.builder().status(HttpStatus.OK).message("Successfully update!").build();
    }

    @Override
    public ProfileResponse getResult(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new NotFountException(String.format("User with email: %s not found!", id)));
        ProfileResponse response = new ProfileResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(user.getAccount().getEmail());
        return repository.getResult(id);
    }
}