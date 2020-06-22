package com.code.test.meetingscheduler.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Meeting implements Comparable<Meeting> {
    UUID id; // meeting unique id
    LocalDateTime start; //meeting start time
    LocalDateTime end; //meeting end time
    String title; //meeting title

    public Meeting() {
        this.id = UUID.randomUUID(); //generates unique meeting id
    }

    public Meeting(LocalDateTime start, LocalDateTime end, String title) {
        this();
        this.start = start;
        this.end = end;
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int compareTo(Meeting newMeeting) {
        if (newMeeting.end.compareTo(this.start) <= 0) {
            return 1;
        }
        if (newMeeting.start.compareTo(this.end) >= 0) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "start=" + start +
                ", end=" + end +
                ", title='" + title + '\'' +
                '}';
    }
}
