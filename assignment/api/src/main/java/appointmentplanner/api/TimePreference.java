/*
 * Copyright (c) 2019 Informatics Fontys FHTenL University of Applied Science Venlo.
 */
package appointmentplanner.api;

/**
 * Strategy to find a Time slot to add an Appointment.
 * EARLIEST searches for the EARLIEST time slot from the start of the Day.
 * LATEST searches for the LATEST time slot from the start of the Day.
 * 
 * @author Richard van den Ham {@code r.vandenham@fontys.nl}
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public enum TimePreference {
    /**
     * Earliest on a day.
     */
    EARLIEST, 
    /**
     * Latest on a day.
     */
    LATEST,
    /**
     * 
     * In case no time preference is specified, this is the default.
     */
    UNSPECIFIED,
    /**
     * Only valid in combination with a time.
     */
    LATEST_BEFORE, 
    /**
     * Only valid with a time.
     */
    EARLIEST_AFTER;
}
