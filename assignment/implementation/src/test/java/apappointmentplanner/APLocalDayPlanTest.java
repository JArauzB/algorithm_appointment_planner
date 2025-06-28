/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apappointmentplanner;

import appointmentplanner.api.Appointment;
import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.LocalDay;
import appointmentplanner.api.Priority;
import appointmentplanner.api.TimePreference;
import appointmentplanner.api.Timeline;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import appointmentplanner.api.AppointmentRequest;
import appointmentplanner.api.LocalDayPlan;
import appointmentplanner.api.TimeSlot;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author jorge
 */
public class APLocalDayPlanTest {

    APLocalDayPlan localDayPlan;
    Instant start;
    Instant end;

    @BeforeEach
    public void initialize() {
        //Arrange
        start = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30)).toInstant(ZoneOffset.UTC);
        end = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
        LocalDay localDay = new LocalDay(ZoneId.of("Etc/UTC"), LocalDate.now());
        localDayPlan = new APLocalDayPlan(localDay, LocalTime.of(8, 30), LocalTime.of(17, 30));
    }

    @Test
    void getDayTest() {
        //Act
        LocalDay actualLocalDay = localDayPlan.getDay();
        LocalDay expectedLocalDay = new LocalDay(ZoneId.of("Etc/UTC"), LocalDate.now());
        //Assert
        assertThat(actualLocalDay).isEqualTo(expectedLocalDay);
    }

    @Test
    void getEarliestTest() {
        //Act
        Instant actualEarliest = localDayPlan.earliest();
        Instant expectedEarliest = localDayPlan.getTimeline().start();
        //Assert
        assertThat(actualEarliest).isEqualTo(expectedEarliest);
    }

    @Test
    void getLocalDateTest() {
        //Act
        LocalDate actual = localDayPlan.getDate();
        LocalDate expected = LocalDate.now();
        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getTooLateTest() {
        //Act
        Instant actualTooLate = localDayPlan.tooLate();
        Instant expectedTooLate = localDayPlan.getTimeline().end();
        //Assert
        assertThat(actualTooLate).isEqualTo(expectedTooLate);
    }

    @Test
    void getTimelineTest() {
        Timeline timeline = new APTimeline(Instant.ofEpochMilli(0), Instant.ofEpochMilli(0));
        //Arrange
        localDayPlan = new APLocalDayPlan(new LocalDay().getZone(), LocalDate.of(1, Month.FEBRUARY, 2), timeline);
        //Act
        Timeline actualTimeLine = localDayPlan.getTimeline();
        Timeline expectedTimeline = timeline;
        //Assert
        assertThat(actualTimeLine).isEqualTo(expectedTimeline);
    }

    @Test
    void getStartTimeTest() {
        //Act
        LocalTime actualStartTime = localDayPlan.getStartTime();
        LocalTime expectedStartTime = LocalTime.of(8, 30);
        //Assert
        assertThat(actualStartTime).isEqualTo(expectedStartTime);
    }

    @Test
    void getEndTimeTest() {
        //Act
        LocalTime actualEndTime = localDayPlan.getEndTime();
        LocalTime expectedEndTime = LocalTime.of(17, 30);
        //Assert
        assertThat(actualEndTime).isEqualTo(expectedEndTime);
    }

    @Test
    void atTest() {
        //Act
        Instant actual = localDayPlan.at(17, 30);
        Instant expected = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getNrAppointmentsTest() {
        //Arrange
        Duration duration = Duration.ofMinutes(50);
        LocalTime localTime = LocalTime.of(8, 30);
        AppointmentData data = new APAppointmentData("Add app of 50 minutes", duration, Priority.LOW);
        localDayPlan.addAppointment(data, localTime, TimePreference.EARLIEST);
        //Act
        int actual = localDayPlan.getNrOfAppointments();
        int expected = 1;
        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void containsAppointmentTest() {
        //Arrange
        AppointmentData data = new APAppointmentData("Appointment 1", Duration.ofHours(1), Priority.LOW);
        AppointmentData data2 = new APAppointmentData("Appointment 2", Duration.ofHours(10), Priority.HIGH);
        //Act
        Appointment savedApp = localDayPlan.addAppointment(data, LocalTime.of(8, 30)).get(); //Appointment 1 must be true
        AppointmentRequest request = new APAppointmentRequest(data2, LocalTime.of(9, 30), TimePreference.EARLIEST);
        Appointment notSavedApp = new APAppointment(localDayPlan.getDay(), data2, request); //Appointment 2 must be false
        //Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(localDayPlan.contains(savedApp)).isTrue();
            softly.assertThat(localDayPlan.contains(notSavedApp)).isFalse();
        });
    }

    @Test
    void getAppointmentsTest() {
        //Arrange
        AppointmentData data = new APAppointmentData("Appointment 1", Duration.ofHours(1), Priority.LOW);
        AppointmentData data2 = new APAppointmentData("Appointment 2", Duration.ofHours(1), Priority.HIGH);
        //Act
        Appointment appointmentOne = localDayPlan.addAppointment(data, TimePreference.EARLIEST).get(); //Appointment 1
        Appointment appointmentTwo = localDayPlan.addAppointment(data2, LocalTime.of(9, 30), TimePreference.EARLIEST).get(); //Appointment 2

        List<Appointment> actual = localDayPlan.getAppointments();
        List<Appointment> expected = new ArrayList<>();
        expected.add(appointmentOne);
        expected.add(appointmentTwo);
        //Assert
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @ParameterizedTest
    @CsvSource({
        //hours, minutes, description, preference, priority
        "540 ,THIS IS THE FIRST APPOINTMENT, EARLIEST, LOW, 8, 30",
        "60 ,THIS IS THE SECOND APPOINTMENT, EARLIEST, LOW, 8, 30",
        "60 ,THIS IS THE THIRD APPOINTMENT, EARLIEST, LOW, 9, 30",
        "60 ,THIS IS THE THIRD APPOINTMENT, EARLIEST, LOW, 16, 30",})
    void removeAppointmentTest(int minutes, String description, TimePreference preference, Priority priority, int startHour, int startMinute) {
        Duration duration = Duration.ofMinutes(minutes);
        LocalTime localTime = LocalTime.of(startHour, startMinute);
        AppointmentData data = new APAppointmentData(description, duration, priority);
        AppointmentRequest request = new APAppointmentRequest(data, localTime, preference);
        Appointment appointmentToBeDeleted = new APAppointment(localDayPlan.getDay(), data, request);

        //Act
        localDayPlan.addAppointment(data, localTime, preference).get();
        AppointmentRequest actual = localDayPlan.removeAppointment(appointmentToBeDeleted);
        AppointmentRequest expected = request;

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAndRemoveAppointmentsTest() {
        //Arrange
        Duration duration = Duration.ofMinutes(60);
        LocalTime localTime = LocalTime.of(9, 30);
        AppointmentRequest requestOne = new APAppointmentRequest(new APAppointmentData("Removable appoinmtnet", duration, Priority.HIGH), localTime, TimePreference.EARLIEST);
        AppointmentRequest requestTwo = new APAppointmentRequest(new APAppointmentData("Removable appoinmtnet", duration.plusMinutes(120), Priority.HIGH), localTime.plusHours(3), TimePreference.EARLIEST);
        AppointmentRequest requestThree = new APAppointmentRequest(new APAppointmentData("Removable appoinmtnet", duration, Priority.HIGH), localTime.minusSeconds(3600), TimePreference.EARLIEST);

        //Act
        localDayPlan.addAppointment(requestOne.getAppointmentData(), localTime, requestOne.getTimePreference()); // 9:30 -- 10:30
        localDayPlan.addAppointment(requestTwo.getAppointmentData(), localTime.plusHours(3), requestTwo.getTimePreference()); // 12:30 -- 15:30
        localDayPlan.addAppointment(requestThree.getAppointmentData(), localTime.minusSeconds(3600), requestThree.getTimePreference()); // 8:30 -- 9:30
        //delete all appointmets how are lower than 3 hours
        Predicate<Appointment> filter = (Appointment a) -> (a.duration().compareTo(Duration.ofHours(3)) == -1);
        //find appointments based on the filter
        List<Appointment> foundAppointments = localDayPlan.findAppointments(filter);
        //remove appointments 
        List<AppointmentRequest> actual = localDayPlan.removeAppointments(filter);
        List<AppointmentRequest> expected = new ArrayList<>();
        //save found appointments
        for (Appointment foundApp : foundAppointments) {
            expected.add(foundApp.getRequest());
        }
        //Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
            softly.assertThat(foundAppointments).hasSize(2);
        });
    }

    @Test
    void canAddAppointmentTest() {
        //Act
        boolean actual = localDayPlan.canAddAppointmentOfDuration(Duration.ofHours(9));
        boolean expected = true;
        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getGapsFittingTest() {
        //Act
        List<TimeSlot> actual = localDayPlan.getGapsFitting(Duration.ofHours(1));
        List<TimeSlot> expected = new ArrayList<>();
        expected.add(new APTimeSlot(start, end));
        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getGapsFittingReversedTest() {
        //Arrange
        AppointmentData data = new APAppointmentData("Appointment 1", Duration.ofHours(1), Priority.LOW);
        AppointmentData data2 = new APAppointmentData("Appointment 2", Duration.ofHours(1), Priority.HIGH);
        //Act
        Appointment appOne = localDayPlan.addAppointment(data, LocalTime.of(8, 30), TimePreference.EARLIEST).get(); //Appointment 1
        Appointment appTwo = localDayPlan.addAppointment(data2, LocalTime.of(15, 30), TimePreference.EARLIEST).get(); //Appointment 2

        List<TimeSlot> actual = localDayPlan.getGapsFittingReversed(Duration.ofHours(1));
        List<TimeSlot> expected = new ArrayList<>();

        expected.add(new APTimeSlot(appTwo.getEnd(), appTwo.getEnd().plusSeconds(3600)));// 16:30 -- 17:30
        expected.add(new APTimeSlot(appOne.getEnd(), appTwo.getStart()));// 09:30 -- 15:30

        //Assert
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void getGapsFittingLargestFirstTest() {
        //Arrange
        AppointmentData data = new APAppointmentData("Appointment 1", Duration.ofHours(1), Priority.LOW);
        AppointmentData data2 = new APAppointmentData("Appointment 2", Duration.ofHours(1), Priority.HIGH);
        //Act
        localDayPlan.addAppointment(data, LocalTime.of(10, 30), TimePreference.EARLIEST).get(); //Appointment 1
        localDayPlan.addAppointment(data2, LocalTime.of(15, 30), TimePreference.EARLIEST).get(); //Appointment 2

        List<TimeSlot> actual = localDayPlan.getGapsFittingLargestFirst(Duration.ofHours(1));
        List<TimeSlot> expected = new ArrayList<>();
        end = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
        expected.add(new APTimeSlot(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 30)).toInstant(ZoneOffset.UTC), LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 30)).toInstant(ZoneOffset.UTC)));// 11:30 -- 15:30
        expected.add(new APTimeSlot(start, LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 30)).toInstant(ZoneOffset.UTC)));// 8:30 -- 10:30
        expected.add(new APTimeSlot(LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 30)).toInstant(ZoneOffset.UTC), end));// 16:30 -- 17:30
        //Assert
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void getGapsFittingSmallestFirstTest() {
        //Arrange
        AppointmentData data = new APAppointmentData("Appointment 1", Duration.ofHours(1), Priority.LOW);
        AppointmentData data2 = new APAppointmentData("Appointment 2", Duration.ofHours(1), Priority.HIGH);
        //Act
        localDayPlan.addAppointment(data, LocalTime.of(10, 30), TimePreference.EARLIEST).get(); //Appointment 1
        localDayPlan.addAppointment(data2, LocalTime.of(15, 30), TimePreference.EARLIEST).get(); //Appointment 2

        List<TimeSlot> actual = localDayPlan.getGapsFittingSmallestFirst(Duration.ofHours(1));
        List<TimeSlot> expected = new ArrayList<>();
        end = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
        expected.add(new APTimeSlot(LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 30)).toInstant(ZoneOffset.UTC), end));// 16:30 -- 17:30
        expected.add(new APTimeSlot(start, LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 30)).toInstant(ZoneOffset.UTC)));// 8:30 -- 10:30
        expected.add(new APTimeSlot(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 30)).toInstant(ZoneOffset.UTC), LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 30)).toInstant(ZoneOffset.UTC)));// 11:30 -- 15:30

        //Assert
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void getMatchingFreeSlotsOfDurationTest() {
        //Arrange
        Instant startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)).toInstant(ZoneOffset.UTC);
        Instant endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0)).toInstant(ZoneOffset.UTC);
        Timeline secondTimeline = new APTimeline(startTime, endTime);

        AppointmentData dataTLOne = new APAppointmentData("Appointment 1", Duration.ofHours(1), Priority.LOW);
        AppointmentData dataTLOne2 = new APAppointmentData("Appointment 2", Duration.ofHours(1), Priority.HIGH);

        AppointmentData dataTLTwo = new APAppointmentData("Appointment 1", Duration.ofHours(2), Priority.LOW);
        AppointmentData dataTLTwo2 = new APAppointmentData("Appointment 2", Duration.ofHours(2), Priority.HIGH);
        //Act
        //save appointments for first timeline  
        Appointment firstApppointment = localDayPlan.addAppointment(dataTLOne, LocalTime.of(10, 30), TimePreference.EARLIEST).get(); //Appointment 1
        Appointment secondApppointment = localDayPlan.addAppointment(dataTLOne2, LocalTime.of(15, 30), TimePreference.EARLIEST).get(); //Appointment 2
        //save appointments for second timeline  
        secondTimeline.addAppointment(LocalDay.now(), dataTLTwo, LocalTime.of(10, 30), TimePreference.EARLIEST).get(); //Appointment 1
        secondTimeline.addAppointment(LocalDay.now(), dataTLTwo2, LocalTime.of(15, 30), TimePreference.EARLIEST).get(); //Appointment 2
        List<LocalDayPlan> localDayPlans = new ArrayList<>();
        localDayPlans.add(localDayPlan);
        localDayPlans.add(new APLocalDayPlan(new LocalDay().getZone(), LocalDate.now(), secondTimeline));
        List<TimeSlot> actual = localDayPlan.getMatchingFreeSlotsOfDuration(Duration.ofHours(1), localDayPlans);
        List<TimeSlot> expected = new ArrayList<>();
//        end = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
//        expected.add(new APTimeSlot(startTime, firstApppointment.getStart()));// 10:00 -- 10:30
        expected.add(new APTimeSlot(firstApppointment.getStart().plusSeconds(7200), secondApppointment.getStart()));// 12:30 -- 15:30

        //Assert
//        assertThat(actual).containsExactlyElementsOf(expected);
    }
   
}
