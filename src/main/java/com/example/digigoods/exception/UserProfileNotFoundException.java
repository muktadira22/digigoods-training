package com.example.digigoods.exception;

/**
 * Exception thrown when a user profile is not found.
 */
public class UserProfileNotFoundException extends RuntimeException {

  public UserProfileNotFoundException(Long userId) {
    super("User profile not found for user ID: " + userId);
  }

  public UserProfileNotFoundException(String message) {
    super(message);
  }
}
