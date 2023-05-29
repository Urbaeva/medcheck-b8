package com.example.medcheckb8.db.service.impl;

import com.example.medcheckb8.db.config.jwt.JwtService;
import com.example.medcheckb8.db.dto.request.ProfileRequest;
import com.example.medcheckb8.db.dto.response.ProfileResponse;
import com.example.medcheckb8.db.dto.response.SimpleResponse;
import com.example.medcheckb8.db.dto.response.UserResponse;
import com.example.medcheckb8.db.entities.Account;
import com.example.medcheckb8.db.entities.User;
import com.example.medcheckb8.db.exceptions.BadRequestException;
import com.example.medcheckb8.db.exceptions.NotFountException;
import com.example.medcheckb8.db.repository.AccountRepository;
import com.example.medcheckb8.db.repository.UserRepository;
import com.example.medcheckb8.db.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final JwtService service;
    private static final Logger logger = Logger.getLogger(User.class.getName());

    @Override
    public List<UserResponse> getAllPatients(String word) {
        logger.info("Getting all patients with search word: {}" + word);
        if (word == null) {
            return repository.getAllPatients();
        }
        return repository.getAllPatients(word);
    }

    @Override
    public SimpleResponse getProfile(Long id,ProfileRequest request) {
        User user = repository.findById(id).orElseThrow(() -> new NotFountException(
                String.format("User with email: %s not found!", id)));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.getAccount().setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        repository.save(user);
        return SimpleResponse.builder().status(HttpStatus.OK).message("Successfully update!").build();

    }

    @Override
    public ProfileResponse getResult() {
        Account account = service.getAccountInToken();
        ProfileResponse response = new ProfileResponse();
        response.setId(account.getId());
        response.setFirstName(account.getUser().getFirstName());
        response.setLastName(account.getUser().getLastName());
        response.setPhoneNumber(account.getUser().getPhoneNumber());
        response.setEmail(account.getEmail());
        return response;
    }

    @Override
    public SimpleResponse deleteById(Long id) {
        try {
            if (id == 1) {
                throw new BadRequestException("Cannot delete id \"1\" because this id belongs to the Admin");
            }
            User user = repository.findById(id).orElseThrow(() -> new NotFountException(String.format("User with Id : %s not found", id)));
            repository.delete(user);
            logger.info("User with Id " + id + " deleted successfully");
            return SimpleResponse.builder().status(HttpStatus.OK).message("The user removed").build();
        } catch (BadRequestException e) {
            logger.warning("Error while deleting user with Id " + id + ": " + e.getMessage());
            throw new RuntimeException(e);

        }
    }
}
