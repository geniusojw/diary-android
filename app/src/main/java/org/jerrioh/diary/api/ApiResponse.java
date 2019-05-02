package org.jerrioh.diary.api;

public class ApiResponse<T> {
    private int statusCode;
    private int code;
    private String message;
    private T data;
}
