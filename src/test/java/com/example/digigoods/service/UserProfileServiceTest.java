package com.example.digigoods.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.digigoods.dto.UserProfileDto;
import com.example.digigoods.exception.UserNotFoundException;
import com.example.digigoods.exception.UserProfileAlreadyExistsException;
import com.example.digigoods.exception.UserProfileNotFoundException;
import com.example.digigoods.model.User;
import com.example.digigoods.model.UserProfile;
import com.example.digigoods.repository.UserProfileRepository;
import com.example.digigoods.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for UserProfileService.
 * Tests follow AAA (Arrange-Act-Assert) pattern and JUnit 5 conventions.
 */
@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

  @Mock
  private UserProfileRepository userProfileRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserProfileService userProfileService;

  private User testUser;
  private UserProfile testUserProfile;
  private UserProfileDto testUserProfileDto;

  @BeforeEach
  void setUp() {
    // Arrange - Set up test data
    testUser = createTestUser(1L, "testuser", "password123");
    testUserProfile = createTestUserProfile(1L, testUser, "John", "Doe", "john.doe@example.com");
    testUserProfileDto = createTestUserProfileDto(1L, 1L, "John", "Doe", "john.doe@example.com");
  }

  @Test
  @DisplayName("Should return all user profiles when profiles exist")
  void givenUserProfilesExist_whenGetAllUserProfiles_thenReturnAllProfiles() {
    // Arrange - Mock repository to return user profiles
    List<UserProfile> expectedProfiles = Arrays.asList(testUserProfile);
    when(userProfileRepository.findAll()).thenReturn(expectedProfiles);

    // Act - Execute the method under test
    List<UserProfileDto> actualProfiles = userProfileService.getAllUserProfiles();

    // Assert - Verify all profiles are returned
    assertEquals(1, actualProfiles.size());
    assertEquals(testUserProfileDto.getFirstName(), actualProfiles.get(0).getFirstName());
    assertEquals(testUserProfileDto.getEmail(), actualProfiles.get(0).getEmail());
    verify(userProfileRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Should return user profile when valid user ID provided")
  void givenValidUserId_whenGetUserProfile_thenReturnUserProfile() {
    // Arrange - Mock repository to find profile by user ID
    when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testUserProfile));

    // Act - Execute the method under test
    UserProfileDto actualProfile = userProfileService.getUserProfile(1L);

    // Assert - Verify correct profile is returned
    assertEquals(testUserProfileDto.getFirstName(), actualProfile.getFirstName());
    assertEquals(testUserProfileDto.getEmail(), actualProfile.getEmail());
    assertEquals(testUserProfileDto.getUserId(), actualProfile.getUserId());
    verify(userProfileRepository, times(1)).findByUserId(1L);
  }

  @Test
  @DisplayName("Should throw UserProfileNotFoundException when user ID not found")
  void givenNonExistentUserId_whenGetUserProfile_thenThrowUserProfileNotFoundException() {
    // Arrange - Mock repository to return empty optional
    when(userProfileRepository.findByUserId(999L)).thenReturn(Optional.empty());

    // Act & Assert - Verify exception is thrown
    UserProfileNotFoundException exception = assertThrows(UserProfileNotFoundException.class,
        () -> userProfileService.getUserProfile(999L));

    assertTrue(exception.getMessage().contains("User profile not found for user ID: 999"));
    verify(userProfileRepository, times(1)).findByUserId(999L);
  }

  @Test
  @DisplayName("Should return user profile when valid profile ID provided")
  void givenValidProfileId_whenGetUserProfileById_thenReturnUserProfile() {
    // Arrange - Mock repository to find profile by ID
    when(userProfileRepository.findById(1L)).thenReturn(Optional.of(testUserProfile));

    // Act - Execute the method under test
    UserProfileDto actualProfile = userProfileService.getUserProfileById(1L);

    // Assert - Verify correct profile is returned
    assertEquals(testUserProfileDto.getFirstName(), actualProfile.getFirstName());
    assertEquals(testUserProfileDto.getEmail(), actualProfile.getEmail());
    verify(userProfileRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Should throw UserProfileNotFoundException when profile ID not found")
  void givenNonExistentProfileId_whenGetUserProfileById_thenThrowUserProfileNotFoundException() {
    // Arrange - Mock repository to return empty optional
    when(userProfileRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert - Verify exception is thrown
    UserProfileNotFoundException exception = assertThrows(UserProfileNotFoundException.class,
        () -> userProfileService.getUserProfileById(999L));

    assertTrue(exception.getMessage().contains("User profile not found with ID: 999"));
    verify(userProfileRepository, times(1)).findById(999L);
  }

  @Test
  @DisplayName("Should create user profile when valid data provided")
  void givenValidUserProfileData_whenCreateUserProfile_thenCreateAndReturnProfile() {
    // Arrange - Mock user exists and profile doesn't exist
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userProfileRepository.existsByUserId(1L)).thenReturn(false);
    when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testUserProfile);

    // Act - Execute the method under test
    UserProfileDto createdProfile = userProfileService.createUserProfile(testUserProfileDto);

    // Assert - Verify profile is created successfully
    assertNotNull(createdProfile);
    assertEquals(testUserProfileDto.getFirstName(), createdProfile.getFirstName());
    assertEquals(testUserProfileDto.getEmail(), createdProfile.getEmail());
    verify(userRepository, times(1)).findById(1L);
    verify(userProfileRepository, times(1)).existsByUserId(1L);
    verify(userProfileRepository, times(1)).save(any(UserProfile.class));
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when user does not exist")
  void givenNonExistentUser_whenCreateUserProfile_thenThrowUserNotFoundException() {
    // Arrange - Mock user repository to return empty optional
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    UserProfileDto profileDto = createTestUserProfileDto(
        null, 999L, "Jane", "Smith", "jane@example.com");

    // Act & Assert - Verify exception is thrown
    UserNotFoundException exception = assertThrows(UserNotFoundException.class,
        () -> userProfileService.createUserProfile(profileDto));

    assertTrue(exception.getMessage().contains("User not found with ID: 999"));
    verify(userRepository, times(1)).findById(999L);
    verify(userProfileRepository, never()).save(any(UserProfile.class));
  }

  @Test
  @DisplayName("Should throw UserProfileAlreadyExistsException when profile already exists")
  void givenExistingUserProfile_whenCreateUserProfile_thenThrowUserProfileAlreadyExistsException() {
    // Arrange - Mock user exists and profile already exists
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userProfileRepository.existsByUserId(1L)).thenReturn(true);

    // Act & Assert - Verify exception is thrown
    UserProfileAlreadyExistsException exception = assertThrows(
        UserProfileAlreadyExistsException.class,
        () -> userProfileService.createUserProfile(testUserProfileDto));

    assertTrue(exception.getMessage().contains("User profile already exists for user ID: 1"));
    verify(userRepository, times(1)).findById(1L);
    verify(userProfileRepository, times(1)).existsByUserId(1L);
    verify(userProfileRepository, never()).save(any(UserProfile.class));
  }

  @Test
  @DisplayName("Should update user profile when valid data provided")
  void givenValidUpdateData_whenUpdateUserProfile_thenUpdateAndReturnProfile() {
    // Arrange - Mock profile exists and repository save
    when(userProfileRepository.findById(1L)).thenReturn(Optional.of(testUserProfile));
    when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testUserProfile);

    UserProfileDto updateDto = createTestUserProfileDto(
        1L, 1L, "Updated", "Name", "updated@example.com");

    // Act - Execute the method under test
    UserProfileDto updatedProfile = userProfileService.updateUserProfile(1L, updateDto);

    // Assert - Verify profile is updated successfully
    assertEquals("Updated", updatedProfile.getFirstName());
    assertEquals("Name", updatedProfile.getLastName());
    assertEquals("updated@example.com", updatedProfile.getEmail());
    verify(userProfileRepository, times(1)).findById(1L);
    verify(userProfileRepository, times(1)).save(any(UserProfile.class));
  }

  @Test
  @DisplayName("Should throw UserProfileNotFoundException when updating non-existent profile")
  void givenNonExistentProfileId_whenUpdateUserProfile_thenThrowException() {
    // Arrange - Mock repository to return empty optional
    when(userProfileRepository.findById(999L)).thenReturn(Optional.empty());

    UserProfileDto updateDto = createTestUserProfileDto(
        999L, 1L, "Updated", "Name", "updated@example.com");

    // Act & Assert - Verify exception is thrown
    UserProfileNotFoundException exception = assertThrows(UserProfileNotFoundException.class,
        () -> userProfileService.updateUserProfile(999L, updateDto));

    assertTrue(exception.getMessage().contains("User profile not found with ID: 999"));
    verify(userProfileRepository, times(1)).findById(999L);
    verify(userProfileRepository, never()).save(any(UserProfile.class));
  }

  @Test
  @DisplayName("Should delete user profile when valid user ID provided")
  void givenValidUserId_whenDeleteUserProfile_thenDeleteProfile() {
    // Arrange - Mock profile exists
    when(userProfileRepository.existsByUserId(1L)).thenReturn(true);

    // Act - Execute the method under test
    userProfileService.deleteUserProfile(1L);

    // Assert - Verify profile is deleted
    verify(userProfileRepository, times(1)).existsByUserId(1L);
    verify(userProfileRepository, times(1)).deleteByUserId(1L);
  }

  @Test
  @DisplayName("Should throw UserProfileNotFoundException when deleting non-existent profile")
  void givenNonExistentUserId_whenDeleteUserProfile_thenThrowUserProfileNotFoundException() {
    // Arrange - Mock profile doesn't exist
    when(userProfileRepository.existsByUserId(999L)).thenReturn(false);

    // Act & Assert - Verify exception is thrown
    UserProfileNotFoundException exception = assertThrows(UserProfileNotFoundException.class,
        () -> userProfileService.deleteUserProfile(999L));

    assertTrue(exception.getMessage().contains("User profile not found for user ID: 999"));
    verify(userProfileRepository, times(1)).existsByUserId(999L);
    verify(userProfileRepository, never()).deleteByUserId(999L);
  }

  @Test
  @DisplayName("Should return true when user profile exists")
  void givenExistingUserId_whenUserProfileExists_thenReturnTrue() {
    // Arrange - Mock profile exists
    when(userProfileRepository.existsByUserId(1L)).thenReturn(true);

    // Act - Execute the method under test
    boolean exists = userProfileService.userProfileExists(1L);

    // Assert - Verify true is returned
    assertTrue(exists);
    verify(userProfileRepository, times(1)).existsByUserId(1L);
  }

  @Test
  @DisplayName("Should return false when user profile does not exist")
  void givenNonExistentUserId_whenUserProfileExists_thenReturnFalse() {
    // Arrange - Mock profile doesn't exist
    when(userProfileRepository.existsByUserId(999L)).thenReturn(false);

    // Act - Execute the method under test
    boolean exists = userProfileService.userProfileExists(999L);

    // Assert - Verify false is returned
    assertFalse(exists);
    verify(userProfileRepository, times(1)).existsByUserId(999L);
  }

  @Test
  @DisplayName("Should delete user profile by profile ID when profile exists")
  void givenValidProfileId_whenDeleteUserProfileById_thenDeleteProfile() {
    // Arrange - Mock profile exists
    when(userProfileRepository.existsById(1L)).thenReturn(true);

    // Act - Execute the method under test
    userProfileService.deleteUserProfileById(1L);

    // Assert - Verify profile is deleted
    verify(userProfileRepository, times(1)).existsById(1L);
    verify(userProfileRepository, times(1)).deleteById(1L);
  }

  // Helper methods for creating test data
  private User createTestUser(Long id, String username, String password) {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setPassword(password);
    return user;
  }

  private UserProfile createTestUserProfile(Long id, User user, String firstName, 
      String lastName, String email) {
    return UserProfile.builder()
        .id(id)
        .user(user)
        .firstName(firstName)
        .lastName(lastName)
        .email(email)
        .phoneNumber("+1234567890")
        .address("123 Test Street")
        .city("Test City")
        .postalCode("12345")
        .country("Test Country")
        .build();
  }

  private UserProfileDto createTestUserProfileDto(Long id, Long userId, String firstName, 
      String lastName, String email) {
    return UserProfileDto.builder()
        .id(id)
        .userId(userId)
        .firstName(firstName)
        .lastName(lastName)
        .email(email)
        .phoneNumber("+1234567890")
        .address("123 Test Street")
        .city("Test City")
        .postalCode("12345")
        .country("Test Country")
        .build();
  }
}
