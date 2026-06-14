package com.tl_connect.dev.modules.enroll.dto;

import java.util.List;

import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.projection.DetailsForCheckEnrollRow;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

import lombok.Builder;
import lombok.Getter;

/**
 * Kết quả của giai đoạn pre-check (read-heavy phase).
 * Chứa đủ thông tin để thực hiện write transaction mà không cần query DB thêm.
 */
@Getter
@Builder
public class EnrollmentValidationResult {

    /** Bản ghi SCC hiện tại nếu student đã từng đăng ký rồi drop (re-enroll). Null nếu lần đầu. */
    private final StudentCourseClass existingScc;

    /** Status cũ trước khi thay đổi, dùng để ghi log. */
    private final com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus oldStatus;

    /** Student có đang học lại môn này không. */
    private final boolean isRetake;

    /** Credits của môn học, lấy từ details. */
    private final int credits;

    /** subjectId của course class, truyền sang write TX để tránh query thêm. */
    private final Long subjectId;

    /** semesterId của course class, truyền sang write TX để tránh query thêm. */
    private final Long semesterId;

    /** Thông tin schedule để build ScheduleInterval khi update cache sau khi enroll. */
    private final List<DetailsForCheckEnrollRow> details;

    /** Danh sách ScheduleInterval để push vào cache Redis sau khi enroll. */
    private final List<ScheduleInterval> scheduleIntervals;
}
