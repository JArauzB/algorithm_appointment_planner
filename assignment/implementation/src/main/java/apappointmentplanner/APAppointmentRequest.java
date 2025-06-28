package apappointmentplanner;

import appointmentplanner.api.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 *
 * @author jorge
 */
public class APAppointmentRequest implements AppointmentRequest{
    
    private final AppointmentData appointmentData;
    private final LocalTime localTime;
    private final TimePreference timePreference;
    
    public APAppointmentRequest(AppointmentData appointmentData, LocalTime localTime) {
        this.appointmentData = appointmentData;
        this.localTime = localTime;
        this.timePreference = TimePreference.UNSPECIFIED;
    }

    public APAppointmentRequest(AppointmentData appointmentData, LocalTime localTime, TimePreference timePreference) {
        this.appointmentData = appointmentData;
        this.localTime = localTime;
        this.timePreference = timePreference;
    }
    

    @Override
    public Instant getStart(LocalDay onDay) {
        ZonedDateTime zdt = onDay.ofLocalTime(localTime).atZone( onDay.getZone() );
        int secondsTZDifference = zdt.getOffset().getTotalSeconds();
        Instant newInstant = onDay.ofLocalTime(localTime.plusSeconds(secondsTZDifference));
//        System.out.println("CET " + onDay.ofLocalTime(localTime.plusSeconds(secondsTZDifference)));
//        System.out.println("UTC " + onDay.ofLocalTime(localTime));
        return newInstant;
    }

    @Override
    public LocalTime getStartTime() {
        return this.localTime;
    }

    @Override
    public AppointmentData getAppointmentData() {
        return this.appointmentData;
    }

    @Override
    public TimePreference getTimePreference() {
        return this.timePreference;
    }

    @Override
    public Duration getDuration() {
        return this.appointmentData.getDuration();
    }

    @Override
    public String getDescription() {
        return this.appointmentData.getDescription();
    }

    @Override
    public Priority getPriority() {
        return this.appointmentData.getPriority();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.appointmentData);
        hash = 41 * hash + Objects.hashCode(this.localTime);
        hash = 41 * hash + Objects.hashCode(this.timePreference);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if (obj == null) return false;
        
        if (getClass() != obj.getClass()) return false;
        
        final APAppointmentRequest other = (APAppointmentRequest) obj;
        
        if (!Objects.equals(this.appointmentData, other.appointmentData)) return false;
        
        if (!Objects.equals(this.localTime, other.localTime)) return false;
        
        return this.timePreference == other.timePreference;
    }

    
    @Override
    public String toString() {
        return "APAppointmentRequest{" + "appointmentData=" + appointmentData + ", localTime=" + localTime + ", timePreference=" + timePreference + '}';
    }
    
    
    
}
