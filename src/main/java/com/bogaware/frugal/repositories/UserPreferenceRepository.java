package com.bogaware.frugal.repositories;

import com.bogaware.frugal.models.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, String> {

    @Query(value = "SELECT up FROM UserPreference up WHERE UserID = :userId AND TypeID = :typeId")
    UserPreference findByUserIdAndType(@Param("userId") String userId, @Param("typeId") String typeId);

    @Query(value = "SELECT up FROM UserPreference up WHERE UserID = :userId")
    List<UserPreference> findAllByUserId(@Param("userId") String userId);
}
