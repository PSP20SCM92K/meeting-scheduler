package com.code.test.meetingscheduler.api;

import com.code.test.meetingscheduler.model.Meeting;
import com.code.test.meetingscheduler.model.MeetingPayload;
import com.code.test.meetingscheduler.repo.MeetingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import static com.code.test.meetingscheduler.util.TimeUtils.*;

@RestController
@RequestMapping("/meetings")
public class MeetingController {

    MeetingRepository meetingRepository;

    public MeetingController() {
        this.meetingRepository = MeetingRepository.getInstance();
    }
    /*
    * API to save a new meeting
    * Returns meeting object, if meeting to be scheduled is valid
    * Accepts meeting payload as argument
    * throws exception for invalid payload
    * */
    @PostMapping("/setMeeting")
    public Meeting saveMeeting(@RequestBody MeetingPayload payload) throws Exception {
        Meeting meeting = build(payload); // convert epoch to date-time format
        String meetingValidation = validate(meeting); // validate meeting duration and day
        if (meetingValidation != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, meetingValidation);
        }
        String scheduleValidation = checkPreSchedule(meeting); // validate meeting does not exceed 10 hrs a day and 40 hrs week criteria
        if (scheduleValidation != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, scheduleValidation);
        }
        try {
            return this.meetingRepository.save(meeting); // save meeting in-memory data structure
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Meeting was not inserted. Overlap Exception");
        }
    }
   /*
    * API to remove meeting
    * Accepts arguments meeting tile or meeting start time
    * throws exception for invalid input
    * */
    @DeleteMapping("/removeMeeting")
    public void removeMeeting(@RequestParam Optional<String> meetingTitle, @RequestParam Optional<String> fromTime) throws Exception {
        try {
            if (fromTime.isPresent()) {
                this.meetingRepository.remove(meetingTitle, Optional.of(epochToTime(fromTime.get()))); // remove meeting
            } else {
                this.meetingRepository.remove(meetingTitle, Optional.empty()); //remove meeting
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()); // throw error
        }
    }
    /*
    * API to get next meeting
    * Return next meeting scheduled from current time
    * Throw exception if no meeting exists
    * */
    @GetMapping("/nextMeeting")
    public Meeting getNextMeeting() {
//        return this.meetingRepository.nextMeeting(LocalDateTime.now());
        Meeting meeting = this.meetingRepository.nextMeeting(epochToTime("1592602674")); // get next meeting
        if (meeting != null) {
            return meeting;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Next Meeting found");
        }
    }
    /*
    * returns meeting object
    * Accepts meeting payload as arguments
    * converts epoch to proper time format
    * */
    private Meeting build(MeetingPayload payload) {
        return new Meeting(epochToTime(payload.getFromTime()), epochToTime(payload.getToTime()), payload.getMeetingTitle());
    }
    /*
    * Accepts meeting object as arguments
    * returns error message if not a valid start time or end time
    * method check meeting duration should be between 15 mins to 120 mins
    * meeting cannot be scheduled on saturday
    * returns null for a valid meeting
    * */
    private String validate(Meeting meeting) {
        if (meeting.getEnd().compareTo(meeting.getStart()) < 0) {
            return "Meeting end time cannot be before the meeting start time";
        }

//        if (meeting.getEnd().compareTo(LocalDateTime.now()) <= 0) {
//            return "Meeting cannot be scheduled in past";
//        }

        long duration = duration(meeting.getStart(), meeting.getEnd());
        if (duration < 15 || duration > 120) {
            return "Meeting cannot last for more than 2 hours, and for less than 15 minutes";
        }

        DayOfWeek dayOfStart = meeting.getStart().getDayOfWeek();
        DayOfWeek dayOfEnd = meeting.getEnd().getDayOfWeek();
        if (dayOfStart != dayOfEnd) {
            return "Meeting ends on a different day. Meeting should end before " + computeEndOfDay(meeting.getStart());
        }
        if (dayOfStart == DayOfWeek.SATURDAY) {
            return "Meeting cannot be scheduled on a Saturday.";
        }

        return null;
    }
    /*
    * Accepts meeting object as argument
    * returns error message if meeting exceeds 40 business hours per limit for the week
    * returns error message if meeting exceeds 10 hour limit for the day
    * returns null for a valid meeting
    * */
    private String checkPreSchedule(Meeting meeting) {
        if (!this.meetingRepository.doesMeetingExistBetweenCurrentBusinessHour(meeting)) {
            LocalDate meetingDay = meeting.getStart().toLocalDate();
            long meetingDuration = duration(meeting.getStart(), meeting.getEnd());
            long dayBusinessHour = this.meetingRepository.getWorkDurationOfDay(meetingDay);
            if (dayBusinessHour + meetingDuration <= 10 * 60) {
                long weekBusinessHour = this.meetingRepository.getWorkDurationOfWeek(meetingDay);
                if (weekBusinessHour + meetingDuration > 40 * 60) {
                    return "Meeting exceeds 40 hour business hour limit for the week " + meetingDay;
                }
            } else {
                return "Meeting exceeds 10 hour business hour limit for the day " + meetingDay;
            }
        }
        return null;
    }
}
