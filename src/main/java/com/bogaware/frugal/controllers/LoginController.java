package com.bogaware.frugal.controllers;

import com.bogaware.frugal.dto.UserDTO;
import com.bogaware.frugal.models.User;
import com.bogaware.frugal.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("login")
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("")
    @ResponseBody
    public UserDTO login(@RequestParam(name = "username") String username, @RequestParam(name = "passwordHash") String passwordHash) {
        User retrievedUser = userRepository.findByUsernameAndPasswordHash(username, passwordHash);
        if (retrievedUser != null) {
            return retrievedUser.getDTO();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
}