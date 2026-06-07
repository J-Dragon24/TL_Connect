package com.tl_connect.dev.modules.realtime.notification;

public class NotificationDestination {

    private NotificationDestination() {
    }

    public static final String GLOBAL = "/topic/notification/global";

    public static String classTopic(Long classId) {
        return "/topic/notification/class/" + classId;
    }

    public static String facultyTopic(Long facultyId) {
        return "/topic/notification/faculty/" + facultyId;
    }

    public static String courseTopic(Long courseId) {
        return "/topic/notification/course/" + courseId;
    }

    public static final String PRIVATE = "/queue/notification";
}
