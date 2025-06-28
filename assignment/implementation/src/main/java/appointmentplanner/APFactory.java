package appointmentplanner;

/*
 * Copyright (c) 2019 Informatics Fontys FHTenL University ofLength Applied Science Venlo
file:///C:/Users/jorge/Documents/GitHub/week_02_appointmentplanner-JorgeArauzStudent/assignment/api/target/site/apidocs/appointmenplanner.api/module-summary.html

 */
import appointmentplanner.api.AbstractAPFactory;
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
import java.time.LocalTime;
import java.time.ZoneId;

import apappointmentplanner.APLocalDayPlan;
import apappointmentplanner.APAppointmentData;
import apappointmentplanner.APAppointmentRequest;
import apappointmentplanner.APTimeSlot;

/**
 * Abstract factory to separate student implementations from teachers tests. The
 * instance created by this factory will be black-box tested by the teachers
 * tests.
 *
 * Richard van den Ham {@code r.vandenham@fontys.nl} Pieter van den Hombergh
 * {@code p.vandenhombergh@fontys.nl}
 */
public class APFactory implements AbstractAPFactory {

    public APFactory() {
    }
    
    @Override
    public LocalDayPlan createLocalDayPlan( ZoneId zone, LocalDate date, Timeline timeline ) {
        //TODO Return an instance of your class that implements LocalDayPlan
//        System.out.println("CreateLocalDayPlan 1");
//        System.out.println(zone + " - " +  date + " - " +  timeline);
        return new APLocalDayPlan(zone, date, timeline);
    }

    @Override
    public LocalDayPlan createLocalDayPlan( LocalDay day, Instant start, Instant end ) {
        //TODO Return an instance of your class that implements LocalDayPlan

//        System.out.println("CreateLocalDayPlan 2");
//        System.out.println(day + " - " +  start + " - " +  end);
        return new APLocalDayPlan(day, start, end);
    }

    @Override
    public AppointmentData createAppointmentData( String description, Duration duration, Priority priority ) {
        //TODO Return an instance of your class that implements AppointmentData
//        System.out.println("3");
//        System.out.println(description + " - " +  duration + " - " +  priority);
        return new APAppointmentData(description, duration, priority);
    }

    @Override
    public AppointmentRequest createAppointmentRequest( AppointmentData appData, LocalTime prefStart, TimePreference fallBack ) {
        //TODO Return an instance of your class that implements AppointmentRequest
//        System.out.println("4");
//        System.out.println(appData + " - " +  prefStart + " - " +  fallBack);
        return new APAppointmentRequest(appData, prefStart, fallBack);
    }

    @Override
    public TimeSlot between( Instant start, Instant end ) {
        //TODO Return an instance of your class that implements TimeSlot
//        System.out.println("between");
//        System.out.println(start + " - " +  end);
        return new APTimeSlot(start, end);
    }
}
