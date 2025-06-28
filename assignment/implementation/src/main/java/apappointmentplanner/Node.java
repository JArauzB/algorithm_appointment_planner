/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apappointmentplanner;

import appointmentplanner.api.Appointment;
import appointmentplanner.api.LocalDay;
import appointmentplanner.api.TimeSlot;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import static java.util.Spliterator.ORDERED;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author jorge
 */
public class Node implements TimeSlot {

    private final Appointment appointment;
    private final Instant start;
    private final Instant end;
    
    private Node next;
    private Node previous;
    

    public Node(Appointment appointment, Instant start, Instant end) {
        this.appointment = appointment;
        this.start = start;
        this.end = end;
        this.next = null;
        this.previous = null;
    }


    public void setNext(Node nextNode) {
        next = nextNode;
    }
    
    public void setPrev(Node prevNode) {
        previous = prevNode;
    }
    
    @Override
    public Duration duration() {
        return new APTimeSlot(start, end).duration();
    }

    public Node getNext() {
        return this.next;
    }

    public Node getPrevious() {
        return this.previous;
    }

    @Override
    public Instant getStart() {
        return this.start;
    }

    @Override
    public Instant getEnd() {
        return this.end;
    }

    public Appointment getAppointment(){
        return appointment;
    }

    @Override
    public String toString() {
        return "Node{" + "start= " + start + ", end=" + end + ", appointment=" + appointment + "" + '}';
    }
    
    
}
