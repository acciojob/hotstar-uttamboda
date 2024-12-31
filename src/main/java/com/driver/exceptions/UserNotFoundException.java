package com.driver.exceptions;

public class UserNotFoundException extends RuntimeException {

  // Constructor that accepts a message
  public UserNotFoundException(String message) {
    super(message);
  }

  // Constructor that accepts both message and cause
  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}