package com.company.aneon.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(int code, String message, T data, String errorCode) {

    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return new ApiResponse<>(code, message, data, null);
    }

    public static ApiResponse<Void> error(int code, String message, String errorCode) {
        return new ApiResponse<>(code, message, null, errorCode);
    }
}

