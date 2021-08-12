package com.bogaware.frugal.controllers;

import com.bogaware.frugal.dto.ProfileDTO;
import com.bogaware.frugal.dto.UserDTO;
import com.bogaware.frugal.models.User;
import com.bogaware.frugal.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Controller
@RequestMapping("profile")
public class ProfileController {

    @Autowired
    UserRepository userRepository;

    @PutMapping("")
    @ResponseBody
    public UserDTO changeProfileSettings(@RequestHeader("Authorization") String userId, @RequestBody ProfileDTO profileDTO) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            String firstNameToSet = profileDTO.getFirstName();
            String lastNameToSet = profileDTO.getLastName();
            String emailToSet = profileDTO.getEmail();
            if (firstNameToSet.isEmpty() || lastNameToSet.isEmpty() || emailToSet.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot have an empty profile field");
            }
            currentUser.setFirstName(firstNameToSet);
            currentUser.setLastName(lastNameToSet);
            currentUser.setEmail(emailToSet);

            User savedUser = userRepository.save(currentUser);
            return savedUser.getDTO();
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @PostMapping("image")
    @ResponseBody
    public UserDTO uploadProfileImage(@RequestHeader("Authorization") String userId,
                                      @RequestParam("file") MultipartFile file) throws IOException {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            currentUser.setProfileImage(file.getBytes());
            User savedUser = userRepository.save(currentUser);
            return savedUser.getDTO();
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}