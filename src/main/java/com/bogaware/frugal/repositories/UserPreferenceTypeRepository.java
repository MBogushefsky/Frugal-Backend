package com.bogaware.frugal.repositories;

import com.bogaware.frugal.models.UserPreferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferenceTypeRepository extends JpaRepository<UserPreferenceType, String> {
}
