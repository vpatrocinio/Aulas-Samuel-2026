package com.tvtracker.service;

/** Exceção lançada quando algo dá errado ao comunicar com a API do TVMaze. */
public class ApiException extends Exception {
    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
