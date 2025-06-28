/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apappointmentplanner;


import appointmentplanner.api.LocalDay;
import appointmentplanner.api.TimeSlot;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 *
 * @author jorge
 */
public class APTimeSlot implements TimeSlot {
    private final Instant start;
    private final Instant end;

    public APTimeSlot(Instant start, Instant end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Instant getStart() {
        return this.start;
    }

    @Override
    public Instant getEnd() {
        return this.end;
    }

    @Override
    public LocalTime getEndTime(LocalDay onDay) {
        ZonedDateTime time = getEnd().atZone(onDay.getZone());
//        System.out.println(getEnd());
//        System.out.println("hjbgjvjg");
        return LocalTime.of(time.getHour(), time.getMinute(), time.getSecond());
//        return null;
    }

    @Override
    public LocalDate getEndDate(LocalDay day) {
        return LocalDate.ofInstant(getEnd(), day.getZone());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.start);
        hash = 29 * hash + Objects.hashCode(this.end);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if (obj == null) return false;
        
        if (getClass() != obj.getClass()) return false;
        
        final APTimeSlot other = (APTimeSlot) obj;
        if (!Objects.equals(this.start, other.start)) return false;
        
        return Objects.equals(this.end, other.end);
    }

    @Override
    public String toString() {
        return "APTimeSlot{" + "start=" + start + ", end=" + end + '}';
    }

    @Override
    public int compareTo(TimeSlot other) {
        if(this.duration().getSeconds() > other.duration().getSeconds()) return 1;
        
        else if(this.duration().getSeconds() == other.duration().getSeconds()) return 0;
        
        else return -1;
    }
    
    
    

}
