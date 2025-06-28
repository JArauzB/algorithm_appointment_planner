package apappointmentplanner;

import appointmentplanner.api.Appointment;
import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.AppointmentRequest;
import appointmentplanner.api.LocalDay;
import appointmentplanner.api.TimePreference;
import appointmentplanner.api.TimeSlot;
import appointmentplanner.api.Timeline;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
public class APTimeline implements Timeline {

    private final Node head, tail;
    private final Instant start, end;

    public APTimeline(Instant start, Instant end) {
        this.start = start;
        this.end = end;
        //set head, free slot and tail
        head = new Node(null, null, null);
        //set free block
        Node freeBlock = new Node(null, start, end);
        head.setNext(freeBlock);
        freeBlock.setPrev(head);
        //set tail
        tail = new Node(null, null, null);
        tail.setPrev(freeBlock);
        freeBlock.setNext(tail);

    }

    @Override
    public int getNrOfAppointments() {
        Predicate<Node> filter = (Node n) -> (n.getAppointment() != null);
        return (int) stream().filter(filter).count();
    }

    @Override
    public Instant start() {
        return this.start;
    }

    @Override
    public Instant end() {
        return this.end;
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentData appointment, TimePreference timepreference) {
        //Constraints
        if (appointment == null) {
            throw new NullPointerException();
        }

        LocalTime startTime;
        //if duration is bigger than timeline
        if (getGapsFitting(appointment.getDuration()).isEmpty()) {
            return Optional.empty();
        }
        switch (timepreference) {
            case EARLIEST -> {

                TimeSlot firstSpace = getGapsFitting(appointment.getDuration()).get(0);
                startTime = LocalTime.of(firstSpace.getStart().atOffset(ZoneOffset.UTC).getHour(), firstSpace.getStart().atOffset(ZoneOffset.UTC).getMinute());
            }
            case LATEST -> {
                TimeSlot lastSpace = getGapsFittingReversed(appointment.getDuration()).get(0);
                startTime = LocalTime.ofInstant(lastSpace.getEnd(), ZoneOffset.UTC).minusMinutes(appointment.getDuration().toMinutes());
            }
            default -> {
                TimeSlot firstSpace = getGapsFitting(appointment.getDuration()).get(0);
                startTime = LocalTime.of(firstSpace.getStart().atOffset(ZoneOffset.UTC).getHour(), firstSpace.getStart().atOffset(ZoneOffset.UTC).getMinute());
            }
        }

        return addAppointment(forDay, appointment, startTime, timepreference);
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentData appointment, LocalTime startTime) {
        AppointmentRequest request = new APAppointmentRequest(appointment, startTime, TimePreference.UNSPECIFIED);

        List<TimeSlot> freeTimeSlots = getGapsFitting(request.getDuration());
        //Check if there is a free time slot for this appointment
        if (freeTimeSlots.isEmpty()) {
            return Optional.empty();
        }

        for (TimeSlot timeSlot : freeTimeSlots) {

            //chec if start of new appointment is equal or higher that start time free slot
            int compareToStart = request.getStart(forDay).compareTo(timeSlot.getStart());
            int compareToEnd = request.getStart(forDay).plusSeconds(appointment.getDuration().toSeconds()).compareTo(timeSlot.getEnd());
            if ((compareToStart == 0 || compareToStart == 1) && (compareToEnd == 0 || compareToEnd == -1)) {
                return addAppointment(forDay, appointment, startTime, TimePreference.UNSPECIFIED);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentRequest appointmentRequest) {

        return addAppointment(forDay, appointmentRequest.getAppointmentData(), appointmentRequest.getStartTime(), appointmentRequest.getTimePreference());
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentData appointment, LocalTime startTime, TimePreference fallback) {
        AppointmentRequest request = new APAppointmentRequest(appointment, startTime, fallback);
        Appointment newAppointment = new APAppointment(forDay, appointment, request);

        List<TimeSlot> freeTimeSlots = getGapsFitting(request.getDuration());
        //Check if there is a free time slot for this appointment
        if (freeTimeSlots.isEmpty()) {
            return Optional.empty();
        }

//        System.out.println("------------current data---------------");
//        showNodes();
        for (TimeSlot timeSlot : freeTimeSlots) {

            //skip loop if new appointment is bigger than the current time slot
            if ((newAppointment.getStart().compareTo(timeSlot.getStart()) == 1
                    && newAppointment.getEnd().compareTo(timeSlot.getEnd()) == 1)
                    || timeSlot.getStart().compareTo(newAppointment.getStart()) == 1) {

                continue;
            }
            Node getFreeNode = getNode(timeSlot.getStart(), timeSlot.getEnd());
            //check that the start time matches with the current free time slot
            if (newAppointment.getStart().equals(timeSlot.getStart())) {
                //this means that the appoinment duration has the same duration of the free timeslot
                if (timeSlot.duration().compareTo(appointment.getDuration()) == 0) {
                    Node newNode = new Node(newAppointment, timeSlot.getStart(), timeSlot.getEnd());
                    //set previous
                    getFreeNode.getPrevious().setNext(newNode);
                    newNode.setPrev(getFreeNode.getPrevious());
                    //set next
                    getFreeNode.getNext().setPrev(newNode);
                    newNode.setNext(getFreeNode.getNext());
                } //split free slot with appointment
                else if (timeSlot.duration().compareTo(appointment.getDuration()) == 1) {
                    //TODO: check that the appoinment is in the right position
                    Node newNode = new Node(newAppointment, getFreeNode.getStart(), getFreeNode.getStart().plusSeconds(appointment.getDuration().toSeconds()));
                    Node remainedFreeTimeSlot = new Node(null, timeSlot.getStart().plusSeconds(appointment.getDuration().toSeconds()), timeSlot.getEnd());
                    //New Node
                    //set previous
                    getFreeNode.getPrevious().setNext(newNode);
                    newNode.setPrev(getFreeNode.getPrevious());
                    //Set next
                    newNode.setNext(remainedFreeTimeSlot);
                    remainedFreeTimeSlot.setPrev(newNode);
                    //Set next next
                    getFreeNode.getNext().setPrev(remainedFreeTimeSlot);
                    remainedFreeTimeSlot.setNext(getFreeNode.getNext());
                }
            } //here I split the timeslot into three or two slots
            else {
                Node newNode = new Node(newAppointment, newAppointment.getStart(), newAppointment.getEnd());
                Node firstFreeSlot = new Node(null, timeSlot.getStart(), newAppointment.getStart());

                //set prev
                getFreeNode.getPrevious().setNext(firstFreeSlot);
                firstFreeSlot.setPrev(getFreeNode.getPrevious());

                //SECOND PART OF THE SPLIT -> APPOINTMENT
                //Check if the end of the appointment is the same of the free timeslot 
                if (newNode.getEnd().equals(timeSlot.getEnd())) {
                    //set next
                    getFreeNode.getNext().setPrev(newNode);
                    newNode.setNext(getFreeNode.getNext());
                } //this means that the appoinments ends before the free time slot 
                else {
                    Node lastfreeSlot = new Node(null, newAppointment.getEnd(), timeSlot.getEnd());
                    getFreeNode.getNext().setPrev(lastfreeSlot);
                    lastfreeSlot.setNext(getFreeNode.getNext());
                    lastfreeSlot.setPrev(newNode);
                    newNode.setNext(lastfreeSlot);
                }
                //THIRD PART OF THE SPLIT -> FIRST FREE SLOT <-> NEW NODE
                firstFreeSlot.setNext(newNode);
                newNode.setPrev(firstFreeSlot);
            }
//            if(freeTimeSlots.get((freeTimeSlots.size()))

        }
        //
//        System.out.println("-----------new data----------------------");
//        showNodes();
        return Optional.of(newAppointment);

    }

    @Override
    public AppointmentRequest removeAppointment(Appointment appointment) {
        //check that the appointment exists
        if (!contains(appointment)) {
            return null;
        }
//        System.out.println("--------remove app--------------");
        Node getNodeToBeRemoved = getOccupiedNode(appointment.getStart(), appointment.getEnd());

        //check that the previous of removable appoinment is the head or that it has an appointment
        if (getNodeToBeRemoved.getPrevious() == head || getNodeToBeRemoved.getPrevious().getAppointment() != null) {
            Node previousFreeSlot = new Node(null, getNodeToBeRemoved.getStart(), getNodeToBeRemoved.getEnd());
            //set prev
            getNodeToBeRemoved.getPrevious().setNext(previousFreeSlot);
            previousFreeSlot.setPrev(getNodeToBeRemoved.getPrevious());

        } //previous free time slot found, it needs to be merge
        else {
            //check if previous of previous is also a free time slot and merge it
            Node previousFreeSlot = new Node(null, getNodeToBeRemoved.getPrevious().getStart(), getNodeToBeRemoved.getEnd());
            //Set prev
            getNodeToBeRemoved.getPrevious().getPrevious().setNext(previousFreeSlot);
            previousFreeSlot.setPrev(getNodeToBeRemoved.getPrevious().getPrevious());
            //Set next
            getNodeToBeRemoved.getNext().setPrev(previousFreeSlot);
            previousFreeSlot.setNext(getNodeToBeRemoved.getNext());

        }
        //check that the next of removable appoinment is the tail or that it has an appointment
        if (getNodeToBeRemoved.getNext() == tail || getNodeToBeRemoved.getNext().getAppointment() != null) {
            Node nextFreeSlot = getNode(getNodeToBeRemoved.getStart(), getNodeToBeRemoved.getEnd());
            //set next
            getNodeToBeRemoved.getNext().setPrev(nextFreeSlot);
            nextFreeSlot.setNext(getNodeToBeRemoved.getNext());
        } //next free time slot found, it needs to be merge
        else {

            Node previousFreeSlot = getNode(getNodeToBeRemoved.getStart(), getNodeToBeRemoved.getEnd());
            Node finalFreeSlot = new Node(null, previousFreeSlot.getStart(), getNodeToBeRemoved.getNext().getEnd());
            //set prev
            previousFreeSlot.getPrevious().setNext(finalFreeSlot);
            finalFreeSlot.setPrev(previousFreeSlot.getPrevious());
            //set next
            getNodeToBeRemoved.getNext().getNext().setPrev(finalFreeSlot);
            finalFreeSlot.setNext(getNodeToBeRemoved.getNext().getNext());
        }

//        System.out.println("---------new data-----------");
//        showNodes();
        //appointment removed
        return appointment.getRequest();

    }

    @Override
    public List<AppointmentRequest> removeAppointments(Predicate<Appointment> filter
    ) {
        return getAppointments().stream().filter(filter).map(a -> removeAppointment(a)).collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findAppointments(Predicate<Appointment> filter
    ) {
        return getAppointments().stream().filter(filter).collect(Collectors.<Appointment>toList());
    }

    @Override
    public List<Appointment> getAppointments() {
        Predicate<Node> filter = (Node n) -> (n.getAppointment() != null);
        return stream().filter(filter).map(n -> n.getAppointment()).collect(Collectors.<Appointment>toList());
    }

    public void showNodes() {
        System.out.println(head);
        stream().collect(Collectors.<Node>toList()).forEach(n -> System.out.println(n));
        System.out.println(tail);
    }

    public Node getNode(Instant start, Instant end) {
        Predicate<Node> filter = (Node n)
                -> (n.getAppointment() == null
                && (!n.duration().isZero())
                && (n.getStart().equals(start) && n.getEnd().equals(end)
                || new APTimeSlot(start, end).compareTo(new APTimeSlot(n.getStart(), n.getEnd())) == -1)
                && start.compareTo(n.getEnd()) == -1);
        return stream().filter(filter).findFirst().get();
    }

    public Node getOccupiedNode(Instant start, Instant end) {
        Predicate<Node> filter = (Node n)
                -> (n.getAppointment() != null
                && (!n.duration().isZero())
                && n.getStart().equals(start) && n.getEnd().equals(end));

        return stream().filter(filter).findFirst().get();
    }

    @Override
    public Stream<Appointment> appointmentStream() {
        return getAppointments().stream();
    }

    @Override
    public boolean contains(Appointment appointment) {
        Predicate<Node> filter = (Node n) -> (n.getAppointment() != null && n.getAppointment().equals(appointment));
        return stream().anyMatch(filter);
    }

    @Override
    public List<TimeSlot> getGapsFitting(Duration duration) {
        Predicate<Node> filter
                = (Node n)
                -> (n.getAppointment() == null
                && n.getStart() != null && n.getEnd() != null
                && (n.duration().compareTo(duration) == 1
                || n.duration().compareTo(duration) == 0));
        return stream().filter(filter).map(n -> new APTimeSlot(n.getStart(), n.getEnd())).collect(Collectors.<TimeSlot>toList());
    }

    @Override
    public boolean canAddAppointmentOfDuration(Duration duration) {
        return (!getGapsFitting(duration).isEmpty());
    }

    @Override
    public List<TimeSlot> getGapsFittingReversed(Duration duration) {
        return getGapsFitting(duration).stream().sorted((t1, t2) -> t2.getStart().compareTo(t1.getStart())).collect(Collectors.toList());
    }

    @Override
    public List<TimeSlot> getGapsFittingSmallestFirst(Duration duration) {
        return getGapsFitting(duration).stream().sorted((t1, t2) -> t1.duration().compareTo(t2.duration())).collect(Collectors.toList());
    }

    @Override
    public List<TimeSlot> getGapsFittingLargestFirst(Duration duration) {
        return getGapsFitting(duration).stream().sorted((t2, t1) -> t1.duration().compareTo(t2.duration())).collect(Collectors.toList());
    }

//    @Override
//    public List<TimeSlot> getMatchingFreeSlotsOfDuration(Duration minLength, List<Timeline> other) {
//        List<TimeSlot> allTimeslots = new ArrayList<>();
//        other.add(0, this);
//        try {
//            int sizeOfMinTimeSlot = 0;
//            for (Timeline timeline : other) {
////                APTimeline aptimeline = (APTimeline) timeline;
////                aptimeline.showNodes();
//                if (timeline.getGapsFitting(minLength).isEmpty()) {
//                    sizeOfMinTimeSlot = 0;
//                    break;
//                } else {
//                    sizeOfMinTimeSlot = (timeline.getGapsFitting(minLength).size() > sizeOfMinTimeSlot ? timeline.getGapsFitting(minLength).size() : sizeOfMinTimeSlot);
//                }
//            }
//            //return empty list if no timeslot found in 
//            if (sizeOfMinTimeSlot == 0) {
//                return new ArrayList<>();
//            }
//            int count = 0;
//            for (int i = 0; i < sizeOfMinTimeSlot; i++) {
//                int sizeOfTimeLineLists = other.size();
//                int currentTimeLine = 0;
//                Instant maxStartingEdge = null;
//                Instant minEndingEdge = null;
//                while (currentTimeLine < sizeOfTimeLineLists) {
//                    //check if there are no free timeslot left-> prevent execption
//                    if (i >= other.get(currentTimeLine).getGapsFitting(minLength).size() || i < 0) {
//                        break;
//                    }
//                    Instant currentMaxStartingEdge = other.get(currentTimeLine).getGapsFitting(minLength).get(i).getStart();
//                    Instant currentMinEndingEdge = other.get(currentTimeLine).getGapsFitting(minLength).get(i).getEnd();
//
//
//                    //For each TimeLine, skip all gaps that end before or at the same time of the current maximum start edge.                  
//                    if (currentMinEndingEdge.isBefore(currentMaxStartingEdge) || currentMinEndingEdge.equals(currentMaxStartingEdge)) {
//                        minEndingEdge = null;
//                        maxStartingEdge = null;
//                        break;
//                    }
//                    //add currentMaxStartingEdge instant if null
//                    if (maxStartingEdge == null) {
//                        maxStartingEdge = currentMaxStartingEdge;
//                    } //add new maximum start edge
//                    else if (currentMaxStartingEdge.compareTo(maxStartingEdge) == 1) {
//                        maxStartingEdge = currentMaxStartingEdge;
//                    }
//
//                    //add currentMinEndingEdge instant if null
//                    if (minEndingEdge == null) {
//                        minEndingEdge = currentMinEndingEdge;
//                    } //add new minimum ending edge
//                    else if (currentMinEndingEdge.compareTo(minEndingEdge) == -1) {
//                        minEndingEdge = currentMinEndingEdge;
//
//                    }
//                    //there is no matching slot for the first free gaps on every TimeLine.
//                    if (minEndingEdge.compareTo(maxStartingEdge) == -1 || minEndingEdge.compareTo(maxStartingEdge) == 0) {
//                        minEndingEdge = null;
//                        maxStartingEdge = null;
//                    }
//                    currentTimeLine++;
//                }
//
//
//                if (maxStartingEdge != null || minEndingEdge != null) {
//                    allTimeslots.add(new APTimeSlot(maxStartingEdge, minEndingEdge));
//                }
//            }
//        } catch (IndexOutOfBoundsException e) {
//            System.out.println(e);
//        }
//
//        return allTimeslots;
//    }
    @Override
    public List<TimeSlot> getMatchingFreeSlotsOfDuration(Duration minLength, List<Timeline> other) {
        List<TimeSlot> allTimeslots = new ArrayList<>();
        other.add(this);
        
        Predicate<Timeline> filter = (Timeline t) -> (!t.getAppointments().isEmpty());
        
        List<Timeline> allTimelines = other.stream().filter(filter).collect(Collectors.toList());
        List<TimeSlot> freeTimeSlotsFirstTimeLine = allTimelines.get(0).getGapsFitting(minLength);
        allTimelines.remove(0);
        
        long sizeOfAllTimeSlots = allTimelines.size();       
        

        Instant maxStartingEdge = null;
        Instant minEndingEdge = null;
        int countedCheckedTimeSlots = 0;
        for (TimeSlot checkTimeSlot : freeTimeSlotsFirstTimeLine) {
//            System.out.println("-----------------------");
//            System.out.println("current checktimeslot");
//            System.out.println(checkTimeSlot);
            for (Timeline timeline : allTimelines) {

                List<TimeSlot> freeSlotsCurrentTL = timeline.getGapsFitting(minLength);
//                System.out.println("current timeline");
//                System.out.println(allTimelines.indexOf(timeline));

                Instant getStart = checkTimeSlot.getStart(); //start of freetimeslot of first timeline
                Instant getEnd = checkTimeSlot.getEnd();   //end of freetimeslot of first timeline

                //loop every 
                for (TimeSlot timeSlot : freeSlotsCurrentTL) {
//                    System.out.println("current timeslot of current timeline");
//                    System.out.println(timeSlot);
                    Instant currentMaxStartingEdge = timeSlot.getStart();
                    Instant currentMinEndingEdge = timeSlot.getEnd();

                    if (maxStartingEdge == null && minEndingEdge == null) {
                        minEndingEdge = getEnd;
                        maxStartingEdge = getStart;
                    }
                    
                    //get only free timeslots where the start time is between the start and end of checkTimeSlot
                    if ((currentMaxStartingEdge.compareTo(getEnd) == -1 || currentMinEndingEdge.compareTo(getEnd) == 0) && !(maxStartingEdge.compareTo(currentMinEndingEdge) == 1)) {
                        countedCheckedTimeSlots++;
                        //set maxStartingEdge
                        if (currentMaxStartingEdge.compareTo(maxStartingEdge) == 1 && currentMaxStartingEdge.compareTo(minEndingEdge) == -1) {
                            maxStartingEdge = currentMaxStartingEdge;
                        }
                        //set minEndingEdge
                        if (currentMinEndingEdge.compareTo(minEndingEdge) == -1) {
                            minEndingEdge = currentMinEndingEdge;
                        }
//                        System.out.println("new min and max");
//                        System.out.println("min " + maxStartingEdge + " max " + minEndingEdge);
                    }

//                        
                }

            }
            if (maxStartingEdge != null || minEndingEdge != null) {
                if (sizeOfAllTimeSlots <= countedCheckedTimeSlots && !(maxStartingEdge.equals(minEndingEdge))) {
                    TimeSlot newTimeSlot = new APTimeSlot(maxStartingEdge, minEndingEdge);
                    if(newTimeSlot.duration().compareTo(minLength) == 1 || newTimeSlot.duration().compareTo(minLength) == 0){
                     allTimeslots.add(newTimeSlot);   
                    }
                    
                }
                countedCheckedTimeSlots = 0;
                maxStartingEdge = null;
                minEndingEdge = null;

            }
        }
        return allTimeslots;
    }

    Stream<Node> stream() {
        Spliterator<Node> spliterator = Spliterators.spliteratorUnknownSize(iterator(), ORDERED);
        return StreamSupport.stream(spliterator, false);
    }

    public Iterator<Node> iterator() {
        Iterator<Node> iterator = new Iterator<Node>() {
            Node current = head;

            @Override
            public boolean hasNext() {
                if (current == null || current.getNext() == tail) {
                    return false;
                }
                return (current.getNext() != null);
            }

            @Override
            public Node next() {
                Node currentNext = current.getNext();
                current = current.getNext();
                return currentNext;
            }
        };
        return iterator;
    }

}
