package com.example.digigoods.repository;

import com.example.digigoods.model.UserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for UserProfile entity operations.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

  /**
   * Find user profile by user ID.
   *
   * @param userId the user ID
   * @return optional user profile
   */
  Optional<UserProfile> findByUserId(Long userId);

  /**
   * Check if a user profile exists for a given user ID.
   *
   * @param userId the user ID
   * @return true if user profile exists, false otherwise
   */
  boolean existsByUserId(Long userId);

  /**
   * Delete user profile by user ID.
   *
   * @param userId the user ID
   */
  void deleteByUserId(Long userId);
}
