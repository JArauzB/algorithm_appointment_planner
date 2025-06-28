/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apappointmentplanner;

import appointmentplanner.api.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Objects;
/**
 *
 * @author jorge
 */
public class APAppointment implements Appointment{
    
    
    private final LocalDay day;
    private final AppointmentData appData;
    private final AppointmentRequest appRequest;

    public APAppointment(LocalDay day, AppointmentData appData, AppointmentRequest appRequest) {
        this.appData = appData;
        this.day = day;
        this.appRequest = appRequest;
    }
    

    @Override
    public Priority getPriority() {
        return this.appRequest.getPriority();
    }

    @Override
    public AppointmentData getAppointmentData() {
        return this.appData;
    }

    @Override
    public AppointmentRequest getRequest() {
        return this.appRequest;
    }

    @Override
    public Instant getStart() {
        return LocalDateTime.of(LocalDate.now(), getRequest().getStartTime()).toInstant(ZoneOffset.UTC);
        
    }

    @Override
    public Instant getEnd() {
        //TODO: change code when timeline and timeslot are done
        return getStart().plusSeconds(getDuration().toSeconds());
    }

    @Override
    public Duration getDuration() {
        return getRequest().getDuration();
    }

    @Override
    public String getDescription() {
        return getAppointmentData().getDescription();
    }

    public LocalDay getDay() {
        return day;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.day);
        hash = 89 * hash + Objects.hashCode(this.appData);
        hash = 89 * hash + Objects.hashCode(this.appRequest);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if (obj == null) return false;
        
        if (getClass() != obj.getClass()) return false;
        
        final APAppointment other = (APAppointment) obj;
        if (!Objects.equals(this.day, other.day)) return false;
        
        if (!Objects.equals(this.appData, other.appData)) return false;
        
        return Objects.equals(this.appRequest, other.appRequest);
    }

    

    @Override
    public String toString() {
        return "Appointment{" + " appRequest=" + appRequest + '}';
    }
    
    
    
}
