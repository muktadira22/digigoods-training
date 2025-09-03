package com.example.digigoods.controller;

import com.example.digigoods.dto.UserProfileDto;
import com.example.digigoods.service.UserProfileService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for user profile operations.
 */
@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

  private final UserProfileService userProfileService;

  public UserProfileController(UserProfileService userProfileService) {
    this.userProfileService = userProfileService;
  }

  /**
   * Get all user profiles.
   *
   * @return list of all user profiles
   */
  @GetMapping
  public ResponseEntity<List<UserProfileDto>> getAllUserProfiles() {
    List<UserProfileDto> profiles = userProfileService.getAllUserProfiles();
    return ResponseEntity.ok(profiles);
  }

  /**
   * Get user profile by profile ID.
   *
   * @param profileId the profile ID
   * @return user profile
   */
  @GetMapping("/{profileId}")
  public ResponseEntity<UserProfileDto> getUserProfileById(@PathVariable Long profileId) {
    UserProfileDto profile = userProfileService.getUserProfileById(profileId);
    return ResponseEntity.ok(profile);
  }

  /**
   * Get user profile by user ID.
   *
   * @param userId the user ID
   * @return user profile
   */
  @GetMapping("/user/{userId}")
  public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
    UserProfileDto profile = userProfileService.getUserProfile(userId);
    return ResponseEntity.ok(profile);
  }

  /**
   * Create a new user profile.
   *
   * @param userProfileDto the user profile data
   * @return created user profile
   */
  @PostMapping
  public ResponseEntity<UserProfileDto> createUserProfile(
      @Valid @RequestBody UserProfileDto userProfileDto) {
    UserProfileDto createdProfile = userProfileService.createUserProfile(userProfileDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
  }

  /**
   * Update an existing user profile.
   *
   * @param profileId the profile ID to update
   * @param userProfileDto the updated profile data
   * @return updated user profile
   */
  @PutMapping("/{profileId}")
  public ResponseEntity<UserProfileDto> updateUserProfile(
      @PathVariable Long profileId,
      @Valid @RequestBody UserProfileDto userProfileDto) {
    UserProfileDto updatedProfile = userProfileService.updateUserProfile(profileId, userProfileDto);
    return ResponseEntity.ok(updatedProfile);
  }

  /**
   * Delete user profile by profile ID.
   *
   * @param profileId the profile ID to delete
   * @return no content response
   */
  @DeleteMapping("/{profileId}")
  public ResponseEntity<Void> deleteUserProfile(@PathVariable Long profileId) {
    userProfileService.deleteUserProfileById(profileId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Delete user profile by user ID.
   *
   * @param userId the user ID
   * @return no content response
   */
  @DeleteMapping("/user/{userId}")
  public ResponseEntity<Void> deleteUserProfileByUserId(@PathVariable Long userId) {
    userProfileService.deleteUserProfile(userId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Check if user profile exists for a given user ID.
   *
   * @param userId the user ID
   * @return boolean indicating if profile exists
   */
  @GetMapping("/exists/user/{userId}")
  public ResponseEntity<Boolean> userProfileExists(@PathVariable Long userId) {
    boolean exists = userProfileService.userProfileExists(userId);
    return ResponseEntity.ok(exists);
  }
}
