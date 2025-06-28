package appointmentplanner.api;

import java.time.Duration;

/**
 * Details of an Appointment or AppointmentRequest.
 *
 * The data include a description and the expected duration. Think of a lesson
 * taking 45 minutes.
 *
 * Another example is having a treatment at a dentist or a beauty parlor. The
 * duration, priority, and description are known, but there is no time or date
 * allocated yet.
 *
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public interface AppointmentData {

    /**
     * An appointment always has some no-zero length.
     *
     * @return the duration of the appointment.
     */
    Duration getDuration();

    /**
     * There is also a non-empty description.
     *
     * @return non empty string describing the appointment.
     */
    String getDescription();

    /**
     * Get the priority for the appointment.
     *
     * @return the priority
     */
    Priority getPriority();

    /**
     * Return textual representation of AppointmentData. Contains description,
     * duration and priority.
     *
     * @return AppointmentData text.
     */
    @Override
    String toString();

    /**
     * Defines equality. Must be based on all fields of this class.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj);

    /**
     * To be generated.
     *
     * @return hashCode.
     */
    @Override
    public int hashCode();
}
