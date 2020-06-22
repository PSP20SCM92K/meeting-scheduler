package com.code.test.meetingscheduler.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    public static LocalDateTime epochToTime(String epoch) {
        return  LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(epoch)), ZoneId.systemDefault());
    }

    public static LocalDateTime computeStartOfDay(LocalDateTime time) {
        return time.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime computeEndOfDay(LocalDateTime time) {
        return time.toLocalDate().atTime(LocalTime.MAX);
    }

    public static long duration(LocalDateTime start, LocalDateTime end) { return start.until(end, ChronoUnit.MINUTES); }
}
