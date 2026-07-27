package com.techknife.storage;

import lombok.Getter;

/**
 * Custom exception thrown during file validation, upload, deletion, or retrieval storage operations.
 */
@Getter
public class FileStorageException extends RuntimeException {

    private final String errorCode;
    private final String publicId;
    private final String fileName;

    public FileStorageException(String message) {
        super(message);
        this.errorCode = "FILE_STORAGE_ERROR";
        this.publicId = null;
        this.fileName = null;
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FILE_STORAGE_ERROR";
        this.publicId = null;
        this.fileName = null;
    }

    public FileStorageException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.publicId = null;
        this.fileName = null;
    }

    public FileStorageException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.publicId = null;
        this.fileName = null;
    }

    public FileStorageException(String errorCode, String message, String publicId, String fileName, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.publicId = publicId;
        this.fileName = fileName;
    }
}
