package com.bogaware.frugal.services;

import com.bogaware.frugal.repositories.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class SecurityService {

    private MessageDigest digest;

    @Autowired
    public SecurityService(UserPreferenceRepository userPreferenceRepository) throws NoSuchAlgorithmException {
        this.digest = MessageDigest.getInstance("SHA-256");
    }
}
