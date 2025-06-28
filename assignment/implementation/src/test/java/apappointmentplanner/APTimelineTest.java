package apappointmentplanner;

import java.time.LocalDate;
import java.time.ZoneOffset;
import appointmentplanner.api.AppointmentRequest;
import appointmentplanner.api.Appointment;
import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.LocalDay;
import appointmentplanner.api.Priority;
import appointmentplanner.api.TimePreference;
import appointmentplanner.api.TimeSlot;
import appointmentplanner.api.Timeline;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author jorge
 */
public class APTimelineTest {

    APTimeline timeline;
    Instant start;
    Instant end;

    @BeforeEach
    public void initialize() {
        //Arrange               
        start = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30)).toInstant(ZoneOffset.UTC);
        end = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
        timeline = new APTimeline(start, end);
    }

    @ParameterizedTest
    @CsvSource({
        // minutes, description, preference, priority
        "540 ,THIS IS THE FIRST APPOINTMENT, EARLIEST, LOW, 8, 30",
        "60 ,THIS IS THE SECOND APPOINTMENT, EARLIEST, LOW, 8, 30",
        "60 ,THIS IS THE SECOND APPOINTMENT, EARLIEST, LOW, 9, 30",})
    void addAppointmentTest(int minutes, String description, TimePreference preference, Priority priority, int startHour, int startMinute) {
        Duration duration = Duration.ofMinutes(minutes);
        LocalTime localTime = LocalTime.of(startHour, startMinute);
        AppointmentData data = new APAppointmentData(description, duration, priority);
        AppointmentRequest request = new APAppointmentRequest(data, localTime, preference);
        //Act
        Appointment actual = timeline.addAppointment(new LocalDay(), data, localTime, preference).get();
        Appointment expected = new APAppointment(LocalDay.now(), data, request);
        //check appointment
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        // minutes, description, preference, priority
        "10000 ,Duration is to hight, LOW, 8, 30",
        "60 ,Duration is to hight, LOW, 12, 0",})
    void addAppointmentErrorsTest(int minutes, String description, Priority priority, int startHour, int startMinute) {
        Duration duration = Duration.ofMinutes(minutes);
        LocalTime localTime = LocalTime.of(startHour, startMinute);
        AppointmentData data = new APAppointmentData(description, duration, priority);

        //Act
        timeline.addAppointment(new LocalDay(), new APAppointmentData(description, Duration.ofHours(1), priority), LocalTime.of(12, 0), TimePreference.EARLIEST);
        timeline.addAppointment(new LocalDay(), new APAppointmentData(description, Duration.ofHours(9), priority), LocalTime.of(12, 0), TimePreference.EARLIEST);
        Optional<Appointment> actual = timeline.addAppointment(new LocalDay(), data, localTime);
//        Appointment expected = new APAppointment(data, request);
        //check appointment
        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
        "UNSPECIFIED, 8, 30",
        "EARLIEST, 8, 30",
        "LATEST, 16, 30",})
    void addAppointmentWithoutTimeTest(TimePreference pref, int hours, int minutes) {
        Duration duration = Duration.ofMinutes(60);
        AppointmentData data = new APAppointmentData("Appointment without time", duration, Priority.HIGH);
        //Act
        Appointment actual = timeline.addAppointment(new LocalDay(), data, pref).get();
        LocalTime localTime = LocalTime.of(hours, minutes);
        AppointmentRequest request = new APAppointmentRequest(data, localTime, pref);
        Appointment expected = new APAppointment(new LocalDay(), data, request);
        //check appointment
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addAppointmentWithoutPreferenceTest() {
        Duration duration = Duration.ofMinutes(60);
        AppointmentData data = new APAppointmentData("Appointment without Preference", duration, Priority.HIGH);
        //Act
        Appointment actual = timeline.addAppointment(new LocalDay(), data, LocalTime.of(8, 30)).get();
        AppointmentRequest request = new APAppointmentRequest(data, LocalTime.of(8, 30), TimePreference.UNSPECIFIED);
        Appointment expected = new APAppointment(new LocalDay(), data, request);
        //check appointment
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void throwExceptionNoDataAppTest() {
        ThrowableAssert.ThrowingCallable code = () -> {
            timeline.addAppointment(new LocalDay(), null, TimePreference.UNSPECIFIED);
        };

        assertThatThrownBy(code)
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void createSmallTimelineTest() {
        //Arrange
        Timeline smallTimeline = new APTimeline(start, start.plusSeconds(1800));

        AppointmentData ap1 = new APAppointmentData("coffee", Duration.ofMinutes(15), Priority.HIGH);
        AppointmentData ap2 = new APAppointmentData("coffee and some", Duration.ofMinutes(30), Priority.HIGH);
        //Act
        smallTimeline.addAppointment(new LocalDay(), ap1, TimePreference.EARLIEST);
        Optional<Appointment> actual = smallTimeline.addAppointment(new LocalDay(), ap2, TimePreference.EARLIEST);
        //check appointment
        //Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual).isEmpty();
            softly.assertThat(smallTimeline.getAppointments()).hasSize(1);
        });
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
        Appointment appointmentToBeDeleted = new APAppointment(new LocalDay(), data, request);

        //Act
        timeline.addAppointment(new LocalDay(), data, localTime, preference).get();
        AppointmentRequest actual = timeline.removeAppointment(appointmentToBeDeleted);
        AppointmentRequest expected = request;

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void removeNonExistingAppointmentTest() {
        Duration duration = Duration.ofMinutes(10);
        LocalTime localTime = LocalTime.of(8, 30);
        AppointmentData data = new APAppointmentData("Appointment", duration, Priority.HIGH);
        AppointmentRequest request = new APAppointmentRequest(data, localTime, TimePreference.EARLIEST);
        Appointment appointmentToBeDeleted = new APAppointment(new LocalDay(), data, request);
        //Act
        AppointmentRequest actual = timeline.removeAppointment(appointmentToBeDeleted);

        assertThat(actual).isNull();
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
        timeline.addAppointment(new LocalDay(), requestOne.getAppointmentData(), localTime, requestOne.getTimePreference()); // 9:30 -- 10:30
        timeline.addAppointment(new LocalDay(), requestTwo.getAppointmentData(), localTime.plusHours(3), requestTwo.getTimePreference()); // 12:30 -- 15:30
        timeline.addAppointment(new LocalDay(), requestThree.getAppointmentData(), localTime.minusSeconds(3600), requestThree.getTimePreference()); // 8:30 -- 9:30
        //delete all appointmets how are lower than 3 hours
        Predicate<Appointment> filter = (Appointment a) -> (a.duration().compareTo(Duration.ofHours(3)) == -1);
        //find appointments based on the filter
        List<Appointment> foundAppointments = timeline.findAppointments(filter);
        //remove appointments 
        List<AppointmentRequest> actual = timeline.removeAppointments(filter);
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
    void getNrAppointmentsTest() {
        //Arrange
        Duration duration = Duration.ofMinutes(50);
        LocalTime localTime = LocalTime.of(8, 30);
        AppointmentData data = new APAppointmentData("Add app of 50 minutes", duration, Priority.LOW);
        AppointmentRequest request = new APAppointmentRequest(data, localTime, TimePreference.EARLIEST);
        timeline.addAppointment(LocalDay.now(), request);
        //Act
        int actual = timeline.getNrOfAppointments();
        int expected = 1;
        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAppointmentsTest() {
        //Arrange
        AppointmentData data = new APAppointmentData("Appointment 1", Duration.ofHours(1), Priority.LOW);
        AppointmentData data2 = new APAppointmentData("Appointment 2", Duration.ofHours(1), Priority.HIGH);
        //Act
        Appointment appointmentOne = timeline.addAppointment(LocalDay.now(), data, LocalTime.of(8, 30), TimePreference.EARLIEST).get(); //Appointment 1
        Appointment appointmentTwo = timeline.addAppointment(LocalDay.now(), data2, LocalTime.of(9, 30), TimePreference.EARLIEST).get(); //Appointment 2

        List<Appointment> actual = timeline.getAppointments();
        List<Appointment> expected = new ArrayList<>();
        expected.add(appointmentOne);
        expected.add(appointmentTwo);
        //Assert
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void startTest() {
        //Act
        Instant actual = timeline.start();
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30));
        Instant expected = dateTime.toInstant(ZoneOffset.UTC);
        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void endTest() {
        //Act
        Instant actual = timeline.end();
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30));
        Instant expected = dateTime.toInstant(ZoneOffset.UTC);
        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getGapsFittingTest() {
        //Act
        List<TimeSlot> actual = timeline.getGapsFitting(Duration.ofHours(1));
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
        Appointment appOne = timeline.addAppointment(LocalDay.now(), data, LocalTime.of(8, 30), TimePreference.EARLIEST).get(); //Appointment 1
        Appointment appTwo = timeline.addAppointment(LocalDay.now(), data2, LocalTime.of(15, 30), TimePreference.EARLIEST).get(); //Appointment 2

        List<TimeSlot> actual = timeline.getGapsFittingReversed(Duration.ofHours(1));
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
        timeline.addAppointment(LocalDay.now(), data, LocalTime.of(10, 30), TimePreference.EARLIEST).get(); //Appointment 1
        timeline.addAppointment(LocalDay.now(), data2, LocalTime.of(15, 30), TimePreference.EARLIEST).get(); //Appointment 2

        List<TimeSlot> actual = timeline.getGapsFittingLargestFirst(Duration.ofHours(1));
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
        timeline.addAppointment(LocalDay.now(), data, LocalTime.of(10, 30), TimePreference.EARLIEST).get(); //Appointment 1
        timeline.addAppointment(LocalDay.now(), data2, LocalTime.of(15, 30), TimePreference.EARLIEST).get(); //Appointment 2

        List<TimeSlot> actual = timeline.getGapsFittingSmallestFirst(Duration.ofHours(1));
        List<TimeSlot> expected = new ArrayList<>();
        end = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
        expected.add(new APTimeSlot(LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 30)).toInstant(ZoneOffset.UTC), end));// 16:30 -- 17:30
        expected.add(new APTimeSlot(start, LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 30)).toInstant(ZoneOffset.UTC)));// 8:30 -- 10:30
        expected.add(new APTimeSlot(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 30)).toInstant(ZoneOffset.UTC), LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 30)).toInstant(ZoneOffset.UTC)));// 11:30 -- 15:30

        //Assert
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void canAddAppointmentTest() {
        //Act
        boolean actual = timeline.canAddAppointmentOfDuration(Duration.ofHours(9));
        boolean expected = true;
        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void containsAppointmentTest() {
        //Arrange
        AppointmentData data = new APAppointmentData("Appointment 1", Duration.ofHours(1), Priority.LOW);
        AppointmentData data2 = new APAppointmentData("Appointment 2", Duration.ofHours(10), Priority.HIGH);
        //Act
        Appointment savedApp = timeline.addAppointment(LocalDay.now(), data, LocalTime.of(8, 30), TimePreference.EARLIEST).get(); //Appointment 1 must be true
        AppointmentRequest request = new APAppointmentRequest(data2, LocalTime.of(9, 30), TimePreference.EARLIEST);
        Appointment notSavedApp = new APAppointment(new LocalDay(), data2, request); //Appointment 2 must be false
        //Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(timeline.contains(savedApp)).isTrue();
            softly.assertThat(timeline.contains(notSavedApp)).isFalse();
        });
    }

    @Test
    void appointmentStreamTest() {
        //Arrange
        AppointmentData data = new APAppointmentData("Appointment 1", Duration.ofHours(1), Priority.LOW);
        //Act
        Appointment savedApp = timeline.addAppointment(LocalDay.now(), data, LocalTime.of(8, 30), TimePreference.EARLIEST).get(); //Appointment 1 must be true
        //Act
        Stream<Appointment> actual = timeline.appointmentStream();
        List<Appointment> expected = new ArrayList<>();
        expected.add(savedApp);
        //Assert
        assertThat(actual.toList()).containsExactlyElementsOf(expected);
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
        Appointment firstApppointment = timeline.addAppointment(LocalDay.now(), dataTLOne, LocalTime.of(10, 30), TimePreference.EARLIEST).get(); //Appointment 1
        Appointment secondApppointment = timeline.addAppointment(LocalDay.now(), dataTLOne2, LocalTime.of(15, 30), TimePreference.EARLIEST).get(); //Appointment 2
        //save appointments for second timeline  
        secondTimeline.addAppointment(LocalDay.now(), dataTLTwo, LocalTime.of(10, 30), TimePreference.EARLIEST).get(); //Appointment 1
        secondTimeline.addAppointment(LocalDay.now(), dataTLTwo2, LocalTime.of(15, 30), TimePreference.EARLIEST).get(); //Appointment 2
        List<Timeline> timelines = new ArrayList<>();
//        timelines.add(timeline);
        timelines.add(secondTimeline);
        List<TimeSlot> actual = timeline.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(30), timelines);
        List<TimeSlot> expected = new ArrayList<>();
//        end = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
        expected.add(new APTimeSlot(startTime, firstApppointment.getStart()));// 10:00 -- 10:30
        expected.add(new APTimeSlot(firstApppointment.getStart().plusSeconds(7200), secondApppointment.getStart()));// 12:30 -- 15:30

        //Assert
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void checkIndexOutOfBoundsTest() {
        //Arrange
        Instant startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30)).toInstant(ZoneOffset.UTC);
        Instant endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
        Timeline secondTimeline = new APTimeline(startTime, endTime);

        AppointmentData dataTLOne = new APAppointmentData("Appointment 1", Duration.ofHours(1), Priority.LOW);

        AppointmentData dataTLTwo = new APAppointmentData("Appointment 1", Duration.ofHours(9), Priority.LOW);
        //Act
        //save appointments for first timeline  
        timeline.addAppointment(LocalDay.now(), dataTLOne, LocalTime.of(8, 30), TimePreference.EARLIEST).get(); //Appointment 1
//       timeline.addAppointment(LocalDay.now(), dataTLOne2, LocalTime.of(16, 30), TimePreference.EARLIEST).get(); //Appointment 2
        //save appointments for second timeline  
        secondTimeline.addAppointment(LocalDay.now(), dataTLTwo, LocalTime.of(8, 30), TimePreference.EARLIEST).get(); //Appointment 1
        List<Timeline> timelines = new ArrayList<>();
//        timelines.add(timeline);
        timelines.add(secondTimeline);
        List<TimeSlot> actual = timeline.getMatchingFreeSlotsOfDuration(Duration.ofHours(1), timelines);
        List<TimeSlot> expected = new ArrayList<>();

//        expected.add(new APTimeSlot(firstApppointment.getEnd(), secondApppointment.getStart()));// 14:30 -- 15:30
        //Assert
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void getMatchingFreeSlotsOfDurationFourTimelinesTest() {
        //Arrange
        Instant startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30)).toInstant(ZoneOffset.UTC);
        Instant endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
        APTimeline secondTimeline = new APTimeline(startTime, endTime);
        APTimeline thirdTimeline = new APTimeline(startTime, endTime);
        APTimeline fourthTimeline = new APTimeline(startTime, endTime);
        APTimeline fifthTimeline = new APTimeline(startTime, endTime);

        //Act
        //save appointments for first timeline  
        timeline.addAppointment(LocalDay.now(), new APAppointmentData("App 1.1", Duration.ofHours(2), Priority.LOW), LocalTime.of(10, 0), TimePreference.EARLIEST); // 10:00 - 12:00
        timeline.addAppointment(LocalDay.now(), new APAppointmentData("App 1.2", Duration.ofMinutes(40), Priority.LOW), LocalTime.of(12, 30), TimePreference.EARLIEST); // 12:30 - 13:10
        timeline.addAppointment(LocalDay.now(), new APAppointmentData("App 1.3", Duration.ofHours(1), Priority.LOW), LocalTime.of(14, 0), TimePreference.EARLIEST); // 14:00 - 15:00
        timeline.addAppointment(LocalDay.now(), new APAppointmentData("App 1.4", Duration.ofMinutes(30), Priority.LOW), LocalTime.of(15, 30), TimePreference.EARLIEST); // 15:30 - 16:00
        //save appointments for second timeline  
        secondTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 2.1", Duration.ofMinutes(30), Priority.LOW), LocalTime.of(8, 30), TimePreference.EARLIEST); // 08:30 - 09:30
        secondTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 2.2", Duration.ofMinutes(40), Priority.LOW), LocalTime.of(12, 40), TimePreference.EARLIEST); // 12:40 - 13:20
        secondTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 2.3", Duration.ofHours(1), Priority.LOW), LocalTime.of(14, 0), TimePreference.EARLIEST); // 14:00 - 15:00
        secondTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 2.4", Duration.ofMinutes(20), Priority.LOW), LocalTime.of(15, 30), TimePreference.EARLIEST); // 15:30 - 15:50
//       secondTimeline.showNodes();
        //save appointments for third timeline  
        thirdTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 3.1", Duration.ofHours(2), Priority.LOW), LocalTime.of(10, 0), TimePreference.EARLIEST); // 10:00 - 12:00
        thirdTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 3.2", Duration.ofMinutes(30), Priority.LOW), LocalTime.of(12, 50), TimePreference.EARLIEST); // 12:50 - 13:20
        thirdTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 3.3", Duration.ofMinutes(80), Priority.LOW), LocalTime.of(13, 50), TimePreference.EARLIEST); // 13:50 - 15:10
        thirdTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 3.4", Duration.ofMinutes(20), Priority.LOW), LocalTime.of(15, 30), TimePreference.EARLIEST); // 15:30 - 15:50
//        thirdTimeline.showNodes();
        fourthTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 4.1", Duration.ofMinutes(90), Priority.LOW), LocalTime.of(10, 30), TimePreference.EARLIEST); // 10:30 - 12:00
        fourthTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 4.2", Duration.ofMinutes(20), Priority.LOW), LocalTime.of(13, 0), TimePreference.EARLIEST); // 13:00 - 13:20
        fourthTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 4.3", Duration.ofMinutes(220), Priority.LOW), LocalTime.of(13, 50), TimePreference.EARLIEST); // 13:50 - 17:30
//        fourthTimeline.showNodes();

        List<Timeline> timelines = new ArrayList<>();
        timelines.add(secondTimeline);
        timelines.add(thirdTimeline);
        timelines.add(fourthTimeline);
        timelines.add(fifthTimeline);
        List<TimeSlot> actual = timeline.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(10), timelines);
        List<TimeSlot> expected = new ArrayList<>();

        Instant expectedStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)).toInstant(ZoneOffset.UTC);
        Instant expectedEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)).toInstant(ZoneOffset.UTC);
        expected.add(new APTimeSlot(expectedStart, expectedEnd));// 9:00 -- 10:00

//        expectedStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)).toInstant(ZoneOffset.UTC);
//        expectedEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)).toInstant(ZoneOffset.UTC);
//        expected.add(new APTimeSlot(expectedStart, expectedEnd));// 12:00 -- 12:30

        expectedStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 20)).toInstant(ZoneOffset.UTC);
        expectedEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 50)).toInstant(ZoneOffset.UTC);
        expected.add(new APTimeSlot(expectedStart, expectedEnd));// 13:20 -- 13:50

        //Assert
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void getMatchingFreeSlotsOfDurationThreeTimelinesTest() {
        //Arrange
        Instant startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30)).toInstant(ZoneOffset.UTC);
        Instant endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30)).toInstant(ZoneOffset.UTC);
        APTimeline secondTimeline = new APTimeline(startTime, endTime);
        APTimeline thirdTimeline = new APTimeline(startTime, endTime);
        APTimeline fourthTimeline = new APTimeline(startTime, endTime);

        //Act
        //save appointments for first timeline  
//        timeline.addAppointment(LocalDay.now(), new APAppointmentData("App 1.1", Duration.ofMinutes(90), Priority.LOW), LocalTime.of(8, 30), TimePreference.EARLIEST);
//        timeline.addAppointment(LocalDay.now(), new APAppointmentData("App 1.2", Duration.ofMinutes(30), Priority.LOW), LocalTime.of(12, 0), TimePreference.EARLIEST);
//        timeline.addAppointment(LocalDay.now(), new APAppointmentData("App 1.3", Duration.ofMinutes(20), Priority.LOW), LocalTime.of(12, 40), TimePreference.EARLIEST);
//        timeline.addAppointment(LocalDay.now(), new APAppointmentData("App 1.4", Duration.ofMinutes(30), Priority.LOW), LocalTime.of(13, 30), TimePreference.EARLIEST);
//        timeline.addAppointment(LocalDay.now(), new APAppointmentData("App 1.5", Duration.ofMinutes(200), Priority.LOW), LocalTime.of(14, 10), TimePreference.EARLIEST);
//        timeline.showNodes();
//        //save appointments for second timeline  
        secondTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 2.1", Duration.ofMinutes(10), Priority.LOW), LocalTime.of(8, 30), TimePreference.EARLIEST);
        secondTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 2.2", Duration.ofHours(3), Priority.LOW), LocalTime.of(9, 30), TimePreference.EARLIEST);
        secondTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 2.3", Duration.ofMinutes(15), Priority.LOW), LocalTime.of(12, 35), TimePreference.EARLIEST);
        secondTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 2.4", Duration.ofMinutes(10), Priority.LOW), LocalTime.of(13, 50), TimePreference.EARLIEST);
        secondTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 2.5", Duration.ofMinutes(200), Priority.LOW), LocalTime.of(14, 10), TimePreference.EARLIEST);
//       secondTimeline.showNodes();
        //save appointments for third timeline  
        thirdTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 3.1", Duration.ofMinutes(100), Priority.LOW), LocalTime.of(8, 30), TimePreference.EARLIEST);
        thirdTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 3.2", Duration.ofMinutes(20), Priority.LOW), LocalTime.of(12, 10), TimePreference.EARLIEST);
        thirdTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 3.3", Duration.ofMinutes(30), Priority.LOW), LocalTime.of(12, 40), TimePreference.EARLIEST);
        thirdTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 3.4", Duration.ofMinutes(10), Priority.LOW), LocalTime.of(13, 50), TimePreference.EARLIEST);
        thirdTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 3.5", Duration.ofHours(3), Priority.LOW), LocalTime.of(14, 30), TimePreference.EARLIEST);
//        thirdTimeline.showNodes();
         //save appointments for fourth timeline  
        fourthTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 4.1", Duration.ofMinutes(150), Priority.LOW), LocalTime.of(8, 30), TimePreference.EARLIEST);
        fourthTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 4.2", Duration.ofMinutes(30), Priority.LOW), LocalTime.of(12, 0), TimePreference.EARLIEST);
        fourthTimeline.addAppointment(LocalDay.now(), new APAppointmentData("App 4.3", Duration.ofMinutes(60), Priority.LOW), LocalTime.of(12, 40), TimePreference.EARLIEST);
//        fourthTimeline.showNodes();

        List<Timeline> timelines = new ArrayList<>();
        timelines.add(secondTimeline);
        timelines.add(thirdTimeline);
        timelines.add(fourthTimeline);
        List<TimeSlot> actual = timeline.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(5), timelines);
        List<TimeSlot> expected = new ArrayList<>();

        Instant expectedStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)).toInstant(ZoneOffset.UTC);
        Instant expectedEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 35)).toInstant(ZoneOffset.UTC);
        expected.add(new APTimeSlot(expectedStart, expectedEnd));

        expectedStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 40)).toInstant(ZoneOffset.UTC);
        expectedEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 50)).toInstant(ZoneOffset.UTC);
        expected.add(new APTimeSlot(expectedStart, expectedEnd));

        expectedStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)).toInstant(ZoneOffset.UTC);
        expectedEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 10)).toInstant(ZoneOffset.UTC);
        expected.add(new APTimeSlot(expectedStart, expectedEnd));// 13:20 -- 13:50

        //Assert
        assertThat(actual).containsExactlyElementsOf(expected);
    }

}
