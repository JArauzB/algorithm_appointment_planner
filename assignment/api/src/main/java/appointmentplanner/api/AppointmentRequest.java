package appointmentplanner.api;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

/**
 * Value object to return the full details of a request for an appointment.
 *
 * Note that an appointment request is NOT an appointment but only an expression
 * of intent!
 * 
 * The AppointmentRequest is not input for the external API, but it is returned
 * when removing Appointment(s). The idea is that, based on the AppointmentRequest
 * of the removed Appointment, the appointment could be rescheduled.
 * 
 * The AppointmentRequest is mainly used internally in the TimeLine, when an 
 * actual appointment is created.
 * 
 * The AppointmentRequest holds LocalTime startTime, AppointmentData and 
 * TimePreference. 
 * 
 * @author Pieter van den Hombergh
 * @author Richard van den Ham
 */
public interface AppointmentRequest extends AppointmentData {

    /**
     * Get the start time of the intended appointment.
     * If the time is not specified, this method may return null.
     * @param onDay the LocalDay the time is on.
     * @return the start time as instant, potentially null.
     */
    default Instant getStart( LocalDay onDay ) {
        return onDay.ofLocalTime( getStartTime() );
    }

    /**
     * Get the requested local start time.
     *
     * @return the start time
     */
    LocalTime getStartTime();

    /**
     * Get the appointment details of this appointment.
     *
     * @return the data
     */
    AppointmentData getAppointmentData();

    /**
     * Time preference given with this appointment request. The default is
     * UNKNOWN as in unspecified.
     *
     * @return the time preference
     */
    default TimePreference getTimePreference() {
        return TimePreference.UNSPECIFIED;
    }

    @Override
    default Duration getDuration() {
        return getAppointmentData().getDuration();
    }
    
    /**
     * Defines equality.
     * Must be based on all fields of this class.
     * @param obj
     * @return 
     */
    @Override
    public boolean equals( Object obj );
    
    /**
     * To be generated.
     * @return hashCode.
     */
    @Override
    public int hashCode();
    
    
}
