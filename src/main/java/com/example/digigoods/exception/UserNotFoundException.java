package com.example.digigoods.exception;

/**
 * Exception thrown when a user is not found.
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(Long userId) {
    super("User not found with ID: " + userId);
  }

  public UserNotFoundException(String message) {
    super(message);
  }
}
