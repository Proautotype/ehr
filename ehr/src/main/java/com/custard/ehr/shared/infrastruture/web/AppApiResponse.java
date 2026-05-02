package com.custard.ehr.shared.infrastruture.web;

public record AppApiResponse<T>(
        boolean success,
        String message,
        T data
) {

    public static <T> AppApiResponse<T> success(T data) {
        return new AppApiResponse<>(true, "Success", data);
    }

    public static <T> AppApiResponse<T> success(String message, T data) {
        return new AppApiResponse<>(true, message, data);
    }

    public static <T> AppApiResponse<T> failure(String message) {
        return new AppApiResponse<>(false, message, null);
    }
}