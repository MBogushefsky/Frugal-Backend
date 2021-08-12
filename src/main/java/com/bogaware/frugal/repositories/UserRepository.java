package com.bogaware.frugal.repositories;

import com.bogaware.frugal.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "SELECT u FROM User u WHERE Username = :username AND PasswordHash = :passwordHash")
    User findByUsernameAndPasswordHash(@Param("username") String username, @Param("passwordHash") String passwordHash);

    @Query(value = "SELECT u FROM User u WHERE PhoneNumber = :phoneNumber")
    User findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
