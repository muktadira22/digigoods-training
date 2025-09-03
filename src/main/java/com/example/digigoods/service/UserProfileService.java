package com.example.digigoods.service;

import com.example.digigoods.dto.UserProfileDto;
import com.example.digigoods.exception.UserNotFoundException;
import com.example.digigoods.exception.UserProfileAlreadyExistsException;
import com.example.digigoods.exception.UserProfileNotFoundException;
import com.example.digigoods.model.User;
import com.example.digigoods.model.UserProfile;
import com.example.digigoods.repository.UserProfileRepository;
import com.example.digigoods.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user profile operations.
 */
@Service
@Transactional
public class UserProfileService {

  private final UserProfileRepository userProfileRepository;
  private final UserRepository userRepository;

  public UserProfileService(UserProfileRepository userProfileRepository,
                          UserRepository userRepository) {
    this.userProfileRepository = userProfileRepository;
    this.userRepository = userRepository;
  }

  /**
   * Get all user profiles.
   *
   * @return list of all user profiles as DTOs
   */
  @Transactional(readOnly = true)
  public List<UserProfileDto> getAllUserProfiles() {
    List<UserProfile> profiles = userProfileRepository.findAll();
    return profiles.stream()
        .map(this::convertToDto)
        .toList();
  }

  /**
   * Get user profile by user ID.
   *
   * @param userId the user ID
   * @return user profile DTO
   * @throws UserProfileNotFoundException if profile not found
   */
  @Transactional(readOnly = true)
  public UserProfileDto getUserProfile(Long userId) {
    UserProfile profile = userProfileRepository.findByUserId(userId)
        .orElseThrow(() -> new UserProfileNotFoundException(userId));
    return convertToDto(profile);
  }

  /**
   * Get user profile by profile ID.
   *
   * @param profileId the profile ID
   * @return user profile DTO
   * @throws UserProfileNotFoundException if profile not found
   */
  @Transactional(readOnly = true)
  public UserProfileDto getUserProfileById(Long profileId) {
    UserProfile profile = userProfileRepository.findById(profileId)
        .orElseThrow(() -> new UserProfileNotFoundException(
            "User profile not found with ID: " + profileId));
    return convertToDto(profile);
  }

  /**
   * Create a new user profile.
   *
   * @param userProfileDto the user profile data
   * @return created user profile DTO
   * @throws UserNotFoundException if user not found
   * @throws UserProfileAlreadyExistsException if profile already exists
   */
  public UserProfileDto createUserProfile(UserProfileDto userProfileDto) {
    // Validate user exists
    User user = userRepository.findById(userProfileDto.getUserId())
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: "
            + userProfileDto.getUserId()));

    // Check if profile already exists
    if (userProfileRepository.existsByUserId(userProfileDto.getUserId())) {
      throw new UserProfileAlreadyExistsException(userProfileDto.getUserId());
    }

    UserProfile profile = convertToEntity(userProfileDto, user);
    UserProfile savedProfile = userProfileRepository.save(profile);
    return convertToDto(savedProfile);
  }

  /**
   * Update an existing user profile.
   *
   * @param profileId the profile ID to update
   * @param userProfileDto the updated profile data
   * @return updated user profile DTO
   * @throws UserProfileNotFoundException if profile not found
   */
  public UserProfileDto updateUserProfile(Long profileId, UserProfileDto userProfileDto) {
    UserProfile existingProfile = userProfileRepository.findById(profileId)
        .orElseThrow(() -> new UserProfileNotFoundException(
            "User profile not found with ID: " + profileId));

    // Update profile fields
    updateProfileFields(existingProfile, userProfileDto);

    UserProfile savedProfile = userProfileRepository.save(existingProfile);
    return convertToDto(savedProfile);
  }

  /**
   * Delete user profile by user ID.
   *
   * @param userId the user ID
   * @throws UserProfileNotFoundException if profile not found
   */
  public void deleteUserProfile(Long userId) {
    if (!userProfileRepository.existsByUserId(userId)) {
      throw new UserProfileNotFoundException(userId);
    }
    userProfileRepository.deleteByUserId(userId);
  }

  /**
   * Delete user profile by profile ID.
   *
   * @param profileId the profile ID
   * @throws UserProfileNotFoundException if profile not found
   */
  public void deleteUserProfileById(Long profileId) {
    if (!userProfileRepository.existsById(profileId)) {
      throw new UserProfileNotFoundException(
          "User profile not found with ID: " + profileId);
    }
    userProfileRepository.deleteById(profileId);
  }

  /**
   * Check if user profile exists for a given user ID.
   *
   * @param userId the user ID
   * @return true if profile exists, false otherwise
   */
  @Transactional(readOnly = true)
  public boolean userProfileExists(Long userId) {
    return userProfileRepository.existsByUserId(userId);
  }

  private UserProfileDto convertToDto(UserProfile profile) {
    return UserProfileDto.builder()
        .id(profile.getId())
        .userId(profile.getUser().getId())
        .firstName(profile.getFirstName())
        .lastName(profile.getLastName())
        .email(profile.getEmail())
        .phoneNumber(profile.getPhoneNumber())
        .address(profile.getAddress())
        .city(profile.getCity())
        .postalCode(profile.getPostalCode())
        .country(profile.getCountry())
        .build();
  }

  private UserProfile convertToEntity(UserProfileDto dto, User user) {
    return UserProfile.builder()
        .user(user)
        .firstName(dto.getFirstName())
        .lastName(dto.getLastName())
        .email(dto.getEmail())
        .phoneNumber(dto.getPhoneNumber())
        .address(dto.getAddress())
        .city(dto.getCity())
        .postalCode(dto.getPostalCode())
        .country(dto.getCountry())
        .build();
  }

  private void updateProfileFields(UserProfile profile, UserProfileDto dto) {
    Optional.ofNullable(dto.getFirstName()).ifPresent(profile::setFirstName);
    Optional.ofNullable(dto.getLastName()).ifPresent(profile::setLastName);
    Optional.ofNullable(dto.getEmail()).ifPresent(profile::setEmail);
    Optional.ofNullable(dto.getPhoneNumber()).ifPresent(profile::setPhoneNumber);
    Optional.ofNullable(dto.getAddress()).ifPresent(profile::setAddress);
    Optional.ofNullable(dto.getCity()).ifPresent(profile::setCity);
    Optional.ofNullable(dto.getPostalCode()).ifPresent(profile::setPostalCode);
    Optional.ofNullable(dto.getCountry()).ifPresent(profile::setCountry);
  }
}
