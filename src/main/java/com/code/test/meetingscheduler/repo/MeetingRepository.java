package com.code.test.meetingscheduler.repo;

import com.code.test.meetingscheduler.model.IndexNode;
import com.code.test.meetingscheduler.model.Meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;

//singleton class
public class MeetingRepository {
    private static MeetingRepository repository;
    private final TreeMap<LocalDate, TreeSet<Meeting>> meetings; // meetings stores key as date and all meeting for each day as set for as value
    private final HashMap<String, List<IndexNode>> meetingIndex; // meetingIndex stores meeting title as key and all scheduled meeting with same title as value

    private MeetingRepository() {
        meetings = new TreeMap<>(LocalDate::compareTo); //Binary search tree
        meetingIndex = new HashMap<>();
    }

    public static MeetingRepository getInstance() {
        if (repository == null) {
            repository = new MeetingRepository();
        }
        return repository;
    }
    /*
    * Accepts meeting object as argument
    * save a valid meeting to meetings and meetingIndex
    * */
    public Meeting save(Meeting meeting) throws Exception {
        LocalDate meetingDay = meeting.getStart().toLocalDate(); // get the date on which meeting is scheduled
        if (!this.meetings.containsKey(meetingDay)) {
            this.meetings.put(meetingDay, new TreeSet<>()); // if no meeting is scheduled on for the given date add the date to meetings as key and add new TreeSet
        }
        TreeSet<Meeting> meetingTree = this.meetings.get(meetingDay);
        if (meetingTree.add(meeting)) {
            if (!meetingIndex.containsKey(meeting.getTitle())) {
                meetingIndex.put(meeting.getTitle(), new ArrayList<>());
            }
            meetingIndex.get(meeting.getTitle()).add(new IndexNode(meeting, meetingDay));
            return meeting;
        } else {
            throw new Exception();
        }
    }
    /*
    * Accepts meeting object as arguments
    * Returns true if the meeting exits in scheduled business hours
    * */
    public boolean doesMeetingExistBetweenCurrentBusinessHour(Meeting meeting) {
        LocalDateTime start = meeting.getStart(); //get meeting start time
        LocalDateTime end = meeting.getEnd(); //get meeting end time
        LocalDate meetingDay = start.toLocalDate(); //get meeting day
        if (this.meetings.containsKey(meetingDay)) {
            TreeSet<Meeting> meetingTree = meetings.get(meetingDay);
            return meetingTree.first().getStart().compareTo(start) <= 0 && meetingTree.last().getEnd().compareTo(end) >= 0;
        } else {
            return false;
        }
    }
    /*
    * Accepts date as argument
    *
    */
    public long getWorkDurationOfDay(LocalDate date) {
        if (this.meetings.containsKey(date)) {
            TreeSet<Meeting> meetingTree = this.meetings.get(date);
            LocalDateTime low = meetingTree.first().getStart();
            LocalDateTime high = meetingTree.last().getEnd();
            return low.until(high, ChronoUnit.MINUTES);
        } else {
            return 0;
        }
    }
    /*
    * Accepts date as argument
    */
    public long getWorkDurationOfWeek(LocalDate date) {
        LocalDate startOfWeek = date.with(WeekFields.of(Locale.US).dayOfWeek(), 1);
        long weekDuration = 0;
        for (int i = 0; i < 6; i++) {
            weekDuration += getWorkDurationOfDay(startOfWeek.plusDays(i));
        }
        return weekDuration;
    }
    /*
     * Accepts date time as argument
     * returns first scheduled meeting from current time
     */
    public Meeting nextMeeting(LocalDateTime dateTime) {
        LocalDate meetingDay = dateTime.toLocalDate();
        if (this.meetings.containsKey(meetingDay)) {
            TreeSet<Meeting> meetingTree = this.meetings.get(meetingDay);
            return meetingTree.higher(new Meeting(dateTime, dateTime, "MeetingInstance"));
        }
        Map.Entry<LocalDate, TreeSet<Meeting>> localMeetings = this.meetings.higherEntry(meetingDay);
        if (localMeetings != null) {
            TreeSet<Meeting> meetingTree = localMeetings.getValue();
            if (meetingTree != null && !meetingTree.isEmpty()) {
                return meetingTree.first();
            }
        }
        return null;
    }
    /*
     * Accepts meeting title or meeting start time as argument
     * remove meeting from meeting
     * remove meeting from meetingIndex
     */
    public void remove(Optional<String> title, Optional<LocalDateTime> start) throws Exception {
        if (title.isPresent()) {
            String meetingTitle = title.get();
            if (this.meetingIndex.containsKey(meetingTitle)) {
                List<IndexNode> nodes = this.meetingIndex.get(meetingTitle);
                for (int i = 0; i < nodes.size(); i++) {
                    IndexNode node = nodes.get(i);
                    boolean toRemove = true;
                    if (start.isPresent()) {
                        LocalDateTime startTime = start.get();
                        if (((Meeting) node.getData()).getStart().compareTo(startTime) != 0) {
                            toRemove = false;
                        }
                    }
                    if (toRemove) {
                        LocalDate address = (LocalDate) node.getAddress();
                        Meeting data = (Meeting) node.getData();
                        this.meetings.get(address).remove(data);
                        if (this.meetings.get(address).isEmpty()) {
                            this.meetings.remove(address);
                        }
                        nodes.remove(node);
                        i--;
                    }
                }
                if (this.meetingIndex.get(meetingTitle).isEmpty()) {
                    this.meetingIndex.remove(meetingTitle);
                }
            } else {
                throw new Exception("Meeting with title " + meetingTitle + " was not found");
            }
        } else if (start.isPresent()){
            LocalDateTime startTime = start.get();
            LocalDate meetingDay = startTime.toLocalDate();
            Meeting meeting = this.meetings.get(meetingDay).higher(new Meeting(startTime, startTime, ""));
            if (meeting != null && meeting.getStart().compareTo(startTime) == 0) {
                remove(Optional.of(meeting.getTitle()), Optional.of(meeting.getStart()));
            }
        } else {
            throw new Exception("No meeting metadata provided. Provide at least meeting title or meeting start time");
        }
    }
}
