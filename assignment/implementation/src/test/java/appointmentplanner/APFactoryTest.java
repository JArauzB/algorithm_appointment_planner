/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package appointmentplanner;

import apappointmentplanner.APAppointment;
import apappointmentplanner.APAppointmentData;
import apappointmentplanner.APAppointmentRequest;
import apappointmentplanner.APLocalDayPlan;
import apappointmentplanner.APTimeSlot;
import apappointmentplanner.APTimeline;
import appointmentplanner.api.AbstractAPFactory;
import appointmentplanner.api.Appointment;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author jorge
 */
public class APFactoryTest {

    AbstractAPFactory APFactory;
    AppointmentData appointmentData;
    AppointmentRequest appointmentRequest;
    LocalDayPlan localDayPlan;
    Instant start;
    Instant end;

    @BeforeEach
    public void initialize() {
        //Arrange
        APFactory = ServiceFinder.getFactory();
        appointmentData = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        appointmentRequest = new APAppointmentRequest(appointmentData, LocalTime.of(10, 0));
        localDayPlan = new APLocalDayPlan(new LocalDay(), LocalTime.of(8, 30), LocalTime.of(17, 30));
        start = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30)).toInstant(ZoneOffset.UTC);
        end = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
    }

    @Test
    void createLocalDayPlanConstructorOneTest() {
        Instant start = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30)).toInstant(ZoneOffset.UTC);
        Instant end = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
        Timeline timeLine = new APTimeline(start, end);
        //Act
        LocalDayPlan expectedLocalDayPlan = APFactory.createLocalDayPlan(new LocalDay().getZone(), LocalDate.now(), timeLine);
        //Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(localDayPlan.getDay()).isEqualTo(expectedLocalDayPlan.getDay());
            softly.assertThat(localDayPlan.earliest()).isEqualTo(expectedLocalDayPlan.earliest());
            softly.assertThat(localDayPlan.tooLate()).isEqualTo(expectedLocalDayPlan.tooLate());
            softly.assertThat(timeLine).isEqualTo(expectedLocalDayPlan.getTimeline());
        });
    }

    @Test
    void createLocalDayPlanConstructorTwoTest() {
        //Act
        LocalDayPlan expectedLocalDayPlan = APFactory.createLocalDayPlan(localDayPlan.getDay(), LocalTime.of(8, 30), LocalTime.of(17, 30));
        //Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(localDayPlan.getDay()).isEqualTo(expectedLocalDayPlan.getDay());
//            softly.assertThat(localDayPlan.earliest()).isEqualTo(expectedLocalDayPlan.earliest());
//            softly.assertThat(localDayPlan.tooLate()).isEqualTo(expectedLocalDayPlan.tooLate());
        });
    }

    @Test
    void createAppointmentDataTest() {
        //Act
        AppointmentData expectedAPP = APFactory.createAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        //Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(appointmentData.getDescription()).isEqualTo(expectedAPP.getDescription());
            softly.assertThat(appointmentData.getDuration()).isEqualTo(expectedAPP.getDuration());
            softly.assertThat(appointmentData.getPriority()).isEqualTo(expectedAPP.getPriority());
        });
    }

    @Test
    void createAppointmentRequestTest() {
        //Act
        AppointmentRequest expectedAPP = APFactory.createAppointmentRequest(appointmentData, LocalTime.of(10, 0));
        //Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(appointmentRequest.getAppointmentData()).isEqualTo(expectedAPP.getAppointmentData());
            softly.assertThat(appointmentRequest.getDuration()).isEqualTo(expectedAPP.getDuration());
            softly.assertThat(appointmentRequest.getStartTime()).isEqualTo(expectedAPP.getStartTime());
        });
    }

    @Test
    void betweenTest() {
        //Act
        TimeSlot actualTimeSlot = APFactory.between(Instant.ofEpochSecond(1663074880), Instant.ofEpochSecond(1663074885));
        TimeSlot expectedTimeSlot = new APTimeSlot(Instant.ofEpochSecond(1663074880), Instant.ofEpochSecond(1663074885));
        //Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualTimeSlot.getStart()).isEqualTo(expectedTimeSlot.getStart());
            softly.assertThat(actualTimeSlot.getEnd()).isEqualTo(expectedTimeSlot.getEnd());
            softly.assertThat(actualTimeSlot.getEndDate(new LocalDay())).isEqualTo(expectedTimeSlot.getEndDate(new LocalDay()));
        });
    }

    @Test
    void equalsAndHashCodeTest() {
        //Arrange
        //start Thu Sep 15 2022 06:30:00 GMT+0000 end Thu Sep 15 2022 07:10:00 GMT+0000
        APLocalDayPlan first = new APLocalDayPlan(new LocalDay(), Instant.ofEpochSecond(1663223400), Instant.ofEpochSecond(1663225800));
        APLocalDayPlan second = new APLocalDayPlan(new LocalDay(), Instant.ofEpochSecond(1663223400), Instant.ofEpochSecond(1663225800));
        //Act
        //assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(first.equals(second) && second.equals(first)).isTrue();
            softly.assertThat(first.hashCode() == second.hashCode()).isTrue();
        });
    }

    @Test
    public void toStringTest() {
        //Arrange
        //start Thu Sep 15 2022 06:30:00 GMT+0000 end Thu Sep 15 2022 07:10:00 GMT+0000
        APLocalDayPlan actual = new APLocalDayPlan(new LocalDay(), Instant.ofEpochSecond(1663223400), Instant.ofEpochSecond(1663225800));

        //Act
        String expectedString = "APLocalDayPlan{startTime=08:30, endTime=09:10}";
        System.out.println(actual.toString());
        //assert
//        assertThat(actual.toString()).contains(expectedString);
    }

    @Test
    void add_earliestapp() {
        //Arrange 
        LocalDayPlan localDPOne = APFactory.createLocalDayPlan(new LocalDay(), start, end);
        LocalDayPlan localDPTwo = APFactory.createLocalDayPlan(new LocalDay(), start, end);
        AppointmentData data = new APAppointmentData("Fixed", Duration.ofMinutes(45), Priority.MEDIUM);
        //Act
        Optional<Appointment> appointment = localDPOne.addAppointment(data, TimePreference.EARLIEST);

        //Assert
        assertThat(appointment.get().getStart()).isEqualTo(localDPOne.earliest());
    }

    @Test
    void testContains() {
        //Arrange 
        LocalDayPlan localDPOne = APFactory.createLocalDayPlan(new LocalDay(ZoneId.of("Etc/UTC"), LocalDate.now()), start, end);
        LocalDayPlan localDPTwo = APFactory.createLocalDayPlan(new LocalDay(ZoneId.of("Etc/UTC"), LocalDate.now()).plusDays(7), start, end);

        AppointmentData data = new APAppointmentData("app1 30 min @9:00", Duration.ofMinutes(30), Priority.LOW);
        AppointmentData dataTwo = new APAppointmentData("app2 30 min @9:30", Duration.ofMinutes(30), Priority.LOW);
        AppointmentData dataThree = new APAppointmentData("app3 15 min @10:30", Duration.ofMinutes(15), Priority.MEDIUM);

        //Act
        localDPOne.addAppointment(data, LocalTime.of(9, 0), TimePreference.UNSPECIFIED);
        localDPOne.addAppointment(dataTwo, LocalTime.of(9, 30), TimePreference.UNSPECIFIED);
        localDPOne.addAppointment(dataThree, LocalTime.of(10, 30), TimePreference.UNSPECIFIED);

        Optional<Appointment> app1 = localDPTwo.addAppointment(data, LocalTime.of(9, 0), TimePreference.UNSPECIFIED);
        localDPTwo.addAppointment(dataTwo, LocalTime.of(9, 30), TimePreference.UNSPECIFIED);
        localDPTwo.addAppointment(dataThree, LocalTime.of(10, 30), TimePreference.UNSPECIFIED);
        boolean actual = localDPOne.contains(app1.get());
        boolean expected = false;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testAddSameTwice() {
        //Arrange 
        LocalDayPlan localDPOne = APFactory.createLocalDayPlan(new LocalDay(ZoneId.of("Etc/UTC"), LocalDate.now()), start, end);

        AppointmentData data = new APAppointmentData("ALDA Fixed at 13:10 l 45", Duration.ofMinutes(45), Priority.LOW);
        AppointmentRequest request = new APAppointmentRequest(data, LocalTime.of(12, 20), TimePreference.LATEST);

        //Act
        localDPOne.addAppointment(data, LocalTime.of(12, 20), TimePreference.LATEST).get();
        Appointment actual = localDPOne.addAppointment(data, LocalTime.of(12, 20), TimePreference.LATEST).get();
        assertThat(actual.getStart()).isNotEqualTo(request.getStart(new LocalDay()));
    }

    @Test
    void getGaps3() {
        //Arrange 
        LocalDayPlan localDPOne = APFactory.createLocalDayPlan(new LocalDay(ZoneId.of("Etc/UTC"), LocalDate.now()), start, end);

        AppointmentData data = new APAppointmentData("ALDA Fixed at 13:10 l 45", Duration.ofMinutes(45), Priority.MEDIUM);

        //Act
        localDPOne.addAppointment(data, LocalTime.of(12, 20));
        localDPOne.addAppointment(data, LocalTime.of(13, 10));
        assertThat(localDPOne.getGapsFittingReversed(Duration.ofMinutes(5)).get(0).getEnd()).isEqualTo(end);
    }

    @Test
    void removeAppointmentCoffeeTest() {
        //Arrange 
        LocalDayPlan localDPOne = APFactory.createLocalDayPlan(new LocalDay(ZoneId.of("Etc/UTC"), LocalDate.now()), start, end);

        AppointmentData data = new APAppointmentData("coffee", Duration.ofMinutes(15), Priority.LOW);
        AppointmentData data2 = new APAppointmentData("tea", Duration.ofMinutes(15), Priority.LOW);
        AppointmentRequest expected = new APAppointmentRequest(data, LocalTime.of(9, 0), TimePreference.EARLIEST);

        Appointment removableApp = new APAppointment(localDPOne.getDay(), data, expected);
        //Act
        localDPOne.addAppointment(data, TimePreference.EARLIEST);
        localDPOne.addAppointment(data, TimePreference.EARLIEST);
        localDPOne.addAppointment(data2, TimePreference.LATEST);
        localDPOne.addAppointment(data, TimePreference.EARLIEST);
        AppointmentRequest actual = localDPOne.removeAppointment(removableApp);

        assertThat(actual).isEqualTo(expected);
    }

//    @Test
//    void testCombining() {
//
//        //Arrange
//        LocalDay dayHolland = new LocalDay(ZoneId.of("Europe/Amsterdam"), LocalDate.now());
//        Instant startHolland = dayHolland.at(8, 25);// utc 6:25
//        Instant endHolland = dayHolland.at(17, 30);// utc 15:30
//        LocalDayPlan local = APFactory.createLocalDayPlan(dayHolland, startHolland, endHolland);
//        LocalDayPlan local2 = APFactory.createLocalDayPlan(dayHolland, startHolland, endHolland);
//        LocalDayPlan local3 = APFactory.createLocalDayPlan(dayHolland, startHolland, endHolland);
//        LocalDayPlan local4 = APFactory.createLocalDayPlan(dayHolland, startHolland, endHolland);
//        LocalDayPlan local5 = APFactory.createLocalDayPlan(dayHolland, startHolland, endHolland);
//        LocalDayPlan local6 = APFactory.createLocalDayPlan(dayHolland, startHolland, endHolland);
//        LocalDayPlan local7 = APFactory.createLocalDayPlan(dayHolland, startHolland, endHolland);
//
//        //Appointment Data
//        AppointmentData A = new APAppointmentData("A", Duration.ofMinutes(5), Priority.LOW);
//        AppointmentData B = new APAppointmentData("B", Duration.ofMinutes(10), Priority.LOW);
//        AppointmentData C = new APAppointmentData("C", Duration.ofMinutes(15), Priority.LOW);
//        AppointmentData D = new APAppointmentData("D", Duration.ofMinutes(20), Priority.LOW);
//        AppointmentData E = new APAppointmentData("E", Duration.ofMinutes(25), Priority.LOW);
//        AppointmentData F = new APAppointmentData("F", Duration.ofMinutes(30), Priority.LOW);
//        AppointmentData G = new APAppointmentData("G", Duration.ofMinutes(35), Priority.LOW);
//        AppointmentData H = new APAppointmentData("H", Duration.ofMinutes(40), Priority.LOW);
//        AppointmentData I = new APAppointmentData("I", Duration.ofMinutes(45), Priority.LOW);
//        AppointmentData J = new APAppointmentData("J", Duration.ofMinutes(50), Priority.LOW);
//        AppointmentData Z = new APAppointmentData("Z", Duration.ofMinutes(15), Priority.LOW);
//        //Act
//        //save appointments for first timeline  
//        local.addAppointment(A, LocalTime.ofInstant(dayHolland.at(8, 25), ZoneId.of("Etc/UTC")), TimePreference.EARLIEST);// 6:25 -- 6:30
//        local.addAppointment(C, LocalTime.ofInstant(dayHolland.at(8, 30), ZoneId.of("Etc/UTC")), TimePreference.EARLIEST);// 6:30 -- 6:45
//        local.addAppointment(D, LocalTime.ofInstant(dayHolland.at(9, 30), ZoneId.of("Etc/UTC")), TimePreference.EARLIEST);// 7:30 -- 7:50
//        local.addAppointment(E, LocalTime.ofInstant(dayHolland.at(10, 30), ZoneId.of("Etc/UTC")), TimePreference.EARLIEST);// 8:30 -- 8:55
//        local.addAppointment(F, LocalTime.ofInstant(dayHolland.at(11, 30), ZoneId.of("Etc/UTC")), TimePreference.EARLIEST);// 9:30 -- 10:00
//        local.addAppointment(G, LocalTime.ofInstant(dayHolland.at(12, 30), ZoneId.of("Etc/UTC")), TimePreference.EARLIEST);// 10:30 -- 11:05
//        local.addAppointment(H, LocalTime.ofInstant(dayHolland.at(13, 30), ZoneId.of("Etc/UTC")), TimePreference.EARLIEST);// 11:30 -- 12:10
//        local.addAppointment(I, LocalTime.ofInstant(dayHolland.at(14, 30), ZoneId.of("Etc/UTC")), TimePreference.EARLIEST);// 12:30 -- 13:15
//        local.addAppointment(J, LocalTime.ofInstant(dayHolland.at(15, 30), ZoneId.of("Etc/UTC")), TimePreference.EARLIEST);// 13:30 -- 14:20
//        local.addAppointment(H, LocalTime.ofInstant(dayHolland.at(16, 30), ZoneId.of("Etc/UTC")), TimePreference.EARLIEST);// 14:30 -- 15:10
//
//        //save appointments for second timeline  
//        local2.addAppointment(Z, LocalTime.ofInstant(dayHolland.at(8, 25), ZoneId.of("Etc/UTC")), TimePreference.EARLIEST);// 6:25 -- 6:35
//
//        List<LocalDayPlan> plans = new ArrayList<>();
//        plans.add(local2);
//        plans.add(local3);
//        List<TimeSlot> actual = local.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(5), plans);
//        List<TimeSlot> expected = new ArrayList<>();
////
////        Instant expectedStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 35)).toInstant(ZoneOffset.UTC);
////        Instant expectedEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)).toInstant(ZoneOffset.UTC);
////        expected.add(new APTimeSlot(expectedStart, expectedEnd));// 8:35 -- 9:00
////
////        expectedStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)).toInstant(ZoneOffset.UTC);
////        expectedEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 30)).toInstant(ZoneOffset.UTC);
////        expected.add(new APTimeSlot(expectedStart, expectedEnd));// 10:00 -- 10:30
////
////        expectedStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)).toInstant(ZoneOffset.UTC);
////        expectedEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 10)).toInstant(ZoneOffset.UTC);
////        expected.add(new APTimeSlot(expectedStart, expectedEnd));// 11:00 -- 11:10
////
////        expectedStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0)).toInstant(ZoneOffset.UTC);
////        expectedEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 0)).toInstant(ZoneOffset.UTC);
////        expected.add(new APTimeSlot(expectedStart, expectedEnd));// 15:00 -- 16:00
////
////        //Assert
//        assertThat(actual).containsExactlyElementsOf(expected);
//    }
}
