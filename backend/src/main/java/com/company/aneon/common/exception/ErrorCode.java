package com.company.aneon.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    SCHEDULE_CONFLICT(HttpStatus.CONFLICT, "Event activity schedule conflicts with another activity"),
    VOUCHER_EXHAUSTED(HttpStatus.CONFLICT, "Voucher issuance limit has been reached"),
    DUPLICATE_CHECK_IN(HttpStatus.CONFLICT, "Customer has already checked in to this event"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "QR token is invalid or expired"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ"),
    INVALID_AREA(HttpStatus.BAD_REQUEST, "Diện tích không hợp lệ"),
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "Vị trí không hợp lệ"),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "Khoảng thời gian không hợp lệ"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu"),
    DUPLICATE_CODE(HttpStatus.CONFLICT, "Mã đã tồn tại"),
    AREA_EXCEEDED(HttpStatus.CONFLICT, "Diện tích phân bổ vượt quá diện tích mặt bằng"),
    INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "Không thể chuyển sang trạng thái yêu cầu"),
    OPTIMISTIC_LOCK_CONFLICT(HttpStatus.CONFLICT, "Dữ liệu đã bị thay đổi"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Không có quyền truy cập"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

