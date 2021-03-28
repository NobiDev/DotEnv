package com.nobidev.dotenv;

public class DotEnvException extends RuntimeException {
    public DotEnvException(String message) {
        super(message);
    }

    public DotEnvException(Throwable cause) {
        super(cause);
    }
}
