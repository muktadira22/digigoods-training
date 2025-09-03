package com.example.digigoods.exception;

/**
 * Exception thrown when attempting to create a user profile that already exists.
 */
public class UserProfileAlreadyExistsException extends RuntimeException {

  public UserProfileAlreadyExistsException(Long userId) {
    super("User profile already exists for user ID: " + userId);
  }

  public UserProfileAlreadyExistsException(String message) {
    super(message);
  }
}
