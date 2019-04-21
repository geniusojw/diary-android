package org.jerrioh.diary.util;

public class DiaryApiResponse<T> {
    private int statusCode;
    private int code;
    private String message;
    private T data;
}
