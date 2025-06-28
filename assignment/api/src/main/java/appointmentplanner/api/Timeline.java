/*
 * Copyright (c) 2019 Informatics Fontys FHTenL University of Applied Science Venlo
 */
package appointmentplanner.api;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Timeline of planned appointments.
 *
 * The implementation of the Timeline MUST use YOUR OWN linked list
 * implementation for it's internal structure. Existing implementations or
 * arrays/Lists are not allowed.
 * 
 * However, in the methods returning lists, collections, etc produced by
 * streams, the usual java classes of such kind are allowed. No need to reinvent
 * all wheels.
 *
 * @author Richard van den Ham {@code r.vandenham@fontys.nl}
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 *
 */
public interface Timeline {

    /**
     * Returns the number of appointments on a day.
     *
     * @return Number of appointments on this timeline.
     */
    int getNrOfAppointments();

    /**
     * Get the start of this timeline as instant.
     *
     * @return the starting instant.
     */
    Instant start();

    /**
     * Get the end of this timeline.
     *
     * @return the end
     */
    Instant end();

    /**
     * Adds a new appointment to this day.
     * 
     * Requirements:
     * 
     * If the appointmentData is null, a NullPointerException is
     * thrown.
     * 
     * An appointment can only be added between start time (including) and
     * end time (excluding) of the day.
     *
     * AppointmentData having a duration greater than the length of the day in
     * minutes will result in an empty Optional to be returned.
     * 
     * If the day does not have free space to accommodate the appointment,
     * an empty optional is returned.
     * 
     * Appointments aren't allowed to overlap.
     * 
     *
     * @param forDay time partition to fit appointment
     * @param appointment to add
     * @param timepreference or null if not applicable.
     * @return Appointment instance with all fields set according to
     * AppointmentData, or empty Optional if the constraints of this day and the requested
     * appointment can't be met.
     */
    Optional<Appointment> addAppointment( LocalDay forDay, AppointmentData appointment, TimePreference timepreference );

    /**
     * Add appointment with a fixed time. If the requested slot is available,
     * that is used and the appointment is returned. Otherwise an empty Optional 
     * is returned.
     *
     * @param forDay time partition to fit appointment
     * @param appointment to add
     * @param startTime preferred start time of the appointment
     * @return the added appointment or an empty Optional on failure.
     */
    Optional<Appointment> addAppointment( LocalDay forDay, AppointmentData appointment, LocalTime startTime );

    /**
     * Create an appointment based on previous
     *
     * @param forDay time partition to fit appointment
     * @param appointmentRequest for this appointment.
     * @return the added appointment or an empty Optional on failure.
     */
    default Optional<Appointment> addAppointment( LocalDay forDay,
            AppointmentRequest appointmentRequest ) {
        return addAppointment( forDay, appointmentRequest.getAppointmentData(),
                appointmentRequest.getStartTime() );
    }

    /**
     * Add appointment with a fixed time. If the requested slot is available,
     * that is used and the appointment is returned. Otherwise the fall back
     * time preference is tried.
     *
     * @param forDay
     * @param appointment to add
     * @param startTime preferred start time of the appointment
     * @param fallback time preference as fall back if the fixed time does not
     * apply.
     * @return the added appointment or an empty Optional on failure.
     */
    Optional<Appointment> addAppointment( LocalDay forDay, AppointmentData appointment,
            LocalTime startTime, TimePreference fallback );

    /**
     * Removes the given appointment, returning the data of that appointment, if
     * found. This day is searched for a non-free time slot matching the
     * appointment. The returned data could be used to re-plan the appointment.
     *
     * @param appointment to remove
     * @return the AppointmentRequest of the removed appointment and null 
     * if the appointment is not found.
     */
    AppointmentRequest removeAppointment( Appointment appointment );

    /**
     * Removes appointments with description that matches a filter.
     *
     * @param filter to determine which items to remove.
     * @return the list of AppointmentRequests of removed appointments.
     */
    List<AppointmentRequest> removeAppointments( Predicate<Appointment> filter );

    /**
     * Finds all appointments matching given filter.
     *
     * @param filter to determine which items to select.
     * @return list of matching appointments.
     */
    List<Appointment> findAppointments( Predicate<Appointment> filter );

    /**
     * Finds all appointments for this TimeLine.
     *
     * @return list of all appointments.
     */
    default List<Appointment> getAppointments() {
        return appointmentStream().collect( Collectors.toList() );
    }

    /**
     * All appointments streamed.
     *
     * @return a stream of all appointments.
     */
    Stream<Appointment> appointmentStream();

    /**
     * Check if day contains appointment.
     *
     * @param appointment to search.
     * @return true if Appointment can be found, false otherwise.
     */
    boolean contains( Appointment appointment );

    
    /**
     * This method finds all time gaps that can accommodate an appointment of
     * the given duration in natural order. This method returns the gaps in
     * ascending start time which is the natural order.
     *
     * @param duration the requested duration for an appointment
     *
     * @return a list of gaps in which the appointment can be scheduled.
     */
    List<TimeSlot> getGapsFitting( Duration duration );

    /**
     * Check if an appointment of a duration could be accommodated.
     *
     * @param duration of the appointment
     * @return true is there is a sufficiently big gap.
     */
    boolean canAddAppointmentOfDuration( Duration duration );

    /**
     * This method finds all time gaps that can accommodate an appointment of
     * the given duration in last to first order. This method returns the gaps
     * in descending start time which is the reversed natural order.
     *
     * @param duration the requested duration for an appointment
     * @return a list of start times on which an appointment can be scheduled
     */
    List<TimeSlot> getGapsFittingReversed( Duration duration );

    /**
     * Get the gaps matching the given duration, smallest fitting first.
     *
     * @param duration required
     * @return list of all gaps fitting, ordered, smallest gap first.
     */
    List<TimeSlot> getGapsFittingSmallestFirst( Duration duration );

    /**
     * Get the gaps matching the given duration, largest fitting first.
     *
     * @param duration required
     * @return list of all gaps fitting, ordered, largest gap first.
     */
    List<TimeSlot> getGapsFittingLargestFirst( Duration duration );

    /**
     * Find matching free time slots in this and other DayaPlans.To facilitate
     * appointment proposals.
     *
     * @param minLength minimum length required.
     * @param other day plans
     * @return the list of free slots that all DayPlans share.
     */
    List<TimeSlot> getMatchingFreeSlotsOfDuration( Duration minLength, List<Timeline> other );
}
