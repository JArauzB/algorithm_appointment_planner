package apappointmentplanner;

import appointmentplanner.api.*;
import java.time.Duration;
import java.util.Objects;

/**
 *
 * @author jorge
 */
public class APAppointmentData implements AppointmentData{
    
    private final Duration duration;
    private final Priority priority;
    private final String description;

    public APAppointmentData(String description, Duration duration, Priority priority) {
        this.duration = duration;
        this.priority = priority;
        this.description = description;
    }

    @Override
    public Duration getDuration() {
        return this.duration;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Priority getPriority() {
        return this.priority;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.duration);
        hash = 83 * hash + Objects.hashCode(this.priority);
        hash = 83 * hash + Objects.hashCode(this.description);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if (obj == null) return false;
        
        if (getClass() != obj.getClass())  return false;
        
        final APAppointmentData other = (APAppointmentData) obj;
        if (!Objects.equals(this.description, other.description)) return false;
        
        if (!Objects.equals(this.duration, other.duration)) return false;
        
        return this.priority == other.priority;
    }

    @Override
    public String toString() {
        return "APAppointmentData{" + "duration=" + duration + ", priority=" + priority + ", description=" + description + '}';
    }
    
    
}
