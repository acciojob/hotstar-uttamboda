package com.driver.exceptions;

public class SubscriptionNotPaidException extends RuntimeException {

  // Constructor that accepts a message
  public SubscriptionNotPaidException(String message) {
    super(message);
  }

  // Constructor that accepts both message and cause
  public SubscriptionNotPaidException(String message, Throwable cause) {
    super(message, cause);
  }
}
