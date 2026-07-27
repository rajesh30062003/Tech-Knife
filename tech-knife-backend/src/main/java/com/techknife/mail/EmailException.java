package com.techknife.mail;

/**
 * Exception thrown when an email sending, SMTP communication, or template processing error occurs.
 */
public class EmailException extends RuntimeException {

    private final String errorCode;

    public EmailException(String message) {
        super(message);
        this.errorCode = "EMAIL_SEND_FAILED";
    }

    public EmailException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "EMAIL_SEND_FAILED";
    }

    public EmailException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
