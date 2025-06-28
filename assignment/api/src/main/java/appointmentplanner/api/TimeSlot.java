package appointmentplanner.api;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A time slot represents an (un)allocated range of time defined as
 * [start,end). The starting square bracket and the parenthesis at the end
 * of [start,end) mean that start is part of the slot, end belongs to the
 * following and is the start thereof. In mathematics such notation is called a
 * half open range.
 *
 * The implementer should implement a proper to string showing start instant,
 * end instant and duration of this slot.
 * 
 * The time slots are comparable by length of the slot only!
 * If you keep the slots in a linked list there is no need to compare them
 * by start or end time, because the list will keep them in natural order.
 * The end should never be before start, however a TimeSlot with start and
 * end equal, such that it has an effective duration of
 * zero minutes, zero nanoseconds may have its use as for instance a sentinel
 * value.
 *
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public interface TimeSlot extends Comparable<TimeSlot> {

    /**
     * Get the start of the TimeSlot. The start time is included in the
     * range.
     *
     * @return the start time
     */
    Instant getStart();

    /**
     * Get the end of the TimeSlot. The end time is NOT included in the
     * range.
     *
     * @return the end time
     */
    Instant getEnd();

    /**
     * Get the duration of this slot.
     * The duration may be equal to Duration.ZERO, typically used in sentinel
     * values of TimeSlot.
     *
     * @return the duration as Duration
     */
    default Duration duration() {
        return Duration.between( getStart(), getEnd() );
    }

    /**
     * Compare two time slots by length.
     *
     * @param other TimeSlot to compare with this one.
     * @return comparison result, less than 0, 0 or greater 0.
     */
    @Override
    public default int compareTo( TimeSlot other ) {
        return this.duration().compareTo( other.duration() );
    }

    /**
     * Is this time slot sufficient to accommodate a specified duration.
     *
     * @param d to test
     * @return true if start and end are sufficiently apart to fit the given
     *         duration.
     */
    default boolean fits( Duration d ) {
        return this.duration().compareTo( d ) >= 0;
    }

    /**
     * Does the given time slot fit inside this time slot.
     *
     * @param other Timeslot to test
     * @return true if other does not start earlier nor ends earlier than this
     *         time slot.
     */
    default boolean fits( TimeSlot other ) {
        return this.getStart().compareTo( other.getStart() ) <= 0
                && this.getEnd().compareTo( other.getEnd() ) >= 0;
    }

    /**
     * Get End Time of the appointment in the given time zone.
     *
     * @param day for the time
     * @return end Time.
     */
    default LocalTime getEndTime( LocalDay day ) {
        return day.timeOfInstant( getEnd() );
    }

    /**
     * Get Start Time of the appointment in given time zone.
     *
     * @param day for the time
     * @return start Time
     */
    default LocalTime getStartTime( LocalDay day ) {
        return day.timeOfInstant( getStart() );
    }

    /**
     * Return the date of the start of the TimeSlot.
     *
     * @param day provides time zone
     * @return the date on which the TimeSlot starts.
     */
    default LocalDate getStartDate( LocalDay day ) {
        return day.dateOfInstant( getStart() );
    }

    /**
     * Return the date of the end of the TimeSlot
     *
     * @param day provides time zone
     * @return the date on which the TimeSlot ends.
     */
    default LocalDate getEndDate( LocalDay day ) {
        return day.dateOfInstant( getEnd() );
    }
}
