package com.webserdi.backend.exception;

// BusinessException.java
public class BusinessException extends RuntimeException {
  public BusinessException(String message) {
    super(message);
  }
}