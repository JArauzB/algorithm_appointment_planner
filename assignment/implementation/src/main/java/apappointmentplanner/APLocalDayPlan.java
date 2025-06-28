/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apappointmentplanner;

import appointmentplanner.api.Appointment;
import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.AppointmentRequest;
import appointmentplanner.api.LocalDay;
import appointmentplanner.api.LocalDayPlan;
import appointmentplanner.api.Priority;
import appointmentplanner.api.TimePreference;
import appointmentplanner.api.TimeSlot;
import appointmentplanner.api.Timeline;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author jorge
 */
public class APLocalDayPlan implements LocalDayPlan {

    private final LocalDay today;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Timeline timeline;

    public APLocalDayPlan(LocalDay today, LocalTime startTime, LocalTime endTime) {
        this.today = today;
        this.startTime = startTime;
        this.endTime = endTime;
        Instant start = LocalDateTime.of(LocalDate.now(), startTime).toInstant(ZoneOffset.UTC);
        Instant end = LocalDateTime.of(LocalDate.now(), endTime).toInstant(ZoneOffset.UTC);
        this.timeline = new APTimeline(start, end);
    }

    public APLocalDayPlan(LocalDay day, Instant start, Instant end) {
        TimeSlot timeSlot = new APTimeSlot(start, end);
        this.today = day;
        this.startTime = timeSlot.getStartTime(day);
        this.endTime = timeSlot.getEndTime(day);
        this.timeline = new APTimeline(start, end);
    }

    public APLocalDayPlan(ZoneId zone, LocalDate date, Timeline timeline) {
        TimeSlot timeSlot = new APTimeSlot(timeline.start(), timeline.end());
        LocalDay localDay = new LocalDay(zone, date);
        this.today = localDay;
        this.startTime = timeSlot.getStartTime(localDay);
        this.endTime = timeSlot.getEndTime(localDay);
        this.timeline = timeline;
    }

    @Override
    public LocalDay getDay() {
//        System.out.println("1");
        return this.today;
    }

    @Override
    public Instant earliest() {
//        System.out.println("2");
        return timeline.start();
    }

    @Override
    public Instant tooLate() {
//        System.out.println("3");
        return timeline.end();

    }

    @Override
    public Timeline getTimeline() {
//        System.out.println("4");
        return this.timeline;
    }

    @Override
    public LocalTime getStartTime() {
//        System.out.println("5");
        return getDay().timeOfInstant( earliest() );
    }

    @Override
    public LocalTime getEndTime() {
//        System.out.println("6");
        return getDay().timeOfInstant( tooLate() );
    }

    @Override
    public Instant at(int hour, int minute) {
//        System.out.println("7");
        return getDay().at( hour, minute );
    }

    @Override
    public int getNrOfAppointments() {
//        System.out.println("8");
        return this.timeline.getNrOfAppointments();
    }

    @Override
    public LocalDate getDate() {
//        System.out.println("9");
        return getDay().getDate();
    }

    @Override
    public boolean contains(Appointment appointment) {
//        System.out.println("10");
        return this.timeline.contains(appointment);

    }

    @Override
    public List<Appointment> findAppointments(Predicate<Appointment> filter) {
//        System.out.println("11");
        return this.timeline.findAppointments(filter);
    }

    @Override
    public boolean canAddAppointmentOfDuration(Duration dur) {
//        System.out.println("12");
        return this.timeline.canAddAppointmentOfDuration(dur);
    }

    @Override
    public List<TimeSlot> getGapsFittingSmallestFirst(Duration dur) {
//        System.out.println("13");
        return this.timeline.getGapsFittingSmallestFirst(dur);
    }

    @Override
    public List<TimeSlot> getGapsFittingLargestFirst(Duration dur) {
//        System.out.println("14");
        return this.timeline.getGapsFittingLargestFirst(dur);
    }

    @Override
    public List<TimeSlot> getGapsFittingReversed(Duration dur) {
//        System.out.println("15");
        return this.timeline.getGapsFittingReversed(dur);
    }

    @Override
    public List<TimeSlot> getGapsFitting(Duration dur) {
//        System.out.println("16");
        return this.timeline.getGapsFitting(dur);
    }

    @Override
    public List<TimeSlot> getMatchingFreeSlotsOfDuration(Duration dur, List<LocalDayPlan> otherPlans) {
//        System.out.println("17");
        return this.timeline.getMatchingFreeSlotsOfDuration( dur, otherPlans.stream().map( LocalDayPlan::getTimeline ).collect( toList() ) );

    }

    @Override
    public List<Appointment> getAppointments() {
//        System.out.println("18");
        return this.timeline.getAppointments();
    }

    @Override
    public List<AppointmentRequest> removeAppointments(Predicate<Appointment> filter) {
//        System.out.println("19");
        return this.timeline.removeAppointments(filter);
    }

    @Override
    public AppointmentRequest removeAppointment(Appointment appointment) {
//        System.out.println("20");
        return this.timeline.removeAppointment(appointment);
    }

    @Override
    public Optional<Appointment> addAppointment(AppointmentData appointmentData, TimePreference pref) {
//        System.out.println("21");
//        System.out.println(appointmentData); System.out.println(pref);
        return this.timeline.addAppointment(this.today, appointmentData, pref);
    }

    @Override
    public Optional<Appointment> addAppointment(AppointmentData appointmentData, LocalTime startTime) {
//        System.out.println("22");
        return this.timeline.addAppointment(this.today, appointmentData, startTime);
    }

    @Override
    public Optional<Appointment> addAppointment(AppointmentData appointmentData, LocalTime start, TimePreference fallback) {
//        System.out.println("23");

        //check if fixed appointment already exists to remove it 
        Predicate<Appointment> filter = (Appointment a) -> (a.getAppointmentData().equals(appointmentData) && a.getStartTime(today).equals(start));
        List<Appointment> foundApps = findAppointments(filter);
        if (!foundApps.isEmpty()) {
            return addAppointment(appointmentData, fallback);
        }

        return this.timeline.addAppointment(this.today, appointmentData, start, fallback);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.today);
        hash = 17 * hash + Objects.hashCode(this.startTime);
        hash = 17 * hash + Objects.hashCode(this.endTime);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        

        if (obj == null) return false;
        

        if (getClass() != obj.getClass()) return false;
        

        final APLocalDayPlan other = (APLocalDayPlan) obj;

        if (!Objects.equals(this.today, other.today)) return false;
        

        if (!Objects.equals(this.startTime, other.startTime)) return false;
        

        return Objects.equals(this.endTime, other.endTime);
    }

    @Override
    public String toString() {
        return "Plan=" + getDay() + "" + "startTime=" + startTime + ", endTime=" + endTime + "";
    }
}
