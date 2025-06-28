/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apappointmentplanner;

import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.LocalDay;
import appointmentplanner.api.Priority;
import appointmentplanner.api.TimePreference;
import java.time.Duration;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.SoftAssertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author jorge
 */
public class APAppointmentRequestTest {

    APAppointmentData appData;
    APAppointmentRequest appRequest;

    @BeforeEach
    public void initialize() {
        //Arrange
        appData = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        appRequest = new APAppointmentRequest(appData, LocalTime.of(10, 0));
    }

    @Test
    void setConstructorAppointmentTest() {
        //Act
        APAppointmentRequest actual = new APAppointmentRequest(appData, LocalTime.of(10, 0), TimePreference.EARLIEST);
        //Assert
        assertThat(actual).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
        "my first, 5, HIGH, 08:30, LATEST, 08:30:00Z",
        "my second, 5, LOW, 09:00, EARLIEST, 09:00:00Z",
        "my third, 120, MEDIUM, 13:45, UNSPECIFIED, 13:45:00Z",})
    void getStartFromAppointmentTest(String description, int minutes, Priority priority, LocalTime localTime, TimePreference timePreference, String expect) {
        //Arrange
        APAppointmentData data = new APAppointmentData(description, Duration.ofMinutes(minutes), priority);
        APAppointmentRequest req = new APAppointmentRequest(data, localTime, timePreference);
        String actualResult = String.valueOf(req.getStart(new LocalDay()));
        //Act
        assertThat(actualResult).contains(expect);
    }

    @Test
    void getStartTimeFromAppointmentTest() {
        //Act
        LocalTime actualStartTime = appRequest.getStartTime();
        LocalTime expectedStartTime = LocalTime.of(10, 0);
        //Assert
        assertThat(actualStartTime).isEqualTo(expectedStartTime);
    }

    @Test
    void getDurationFromAppointmentTest() {
        //Act
        Duration actualDuration = appRequest.getDuration();
        Duration expectedDuration = Duration.ofMinutes(45);
        //Assert
        assertThat(actualDuration.getSeconds()).isEqualTo(expectedDuration.getSeconds());
    }

    @Test
    void getDescriptionFromAppointmentTest() {
        //Act
        String actualDescription = appRequest.getDescription();
        String expectedDescription = "Duration test";
        //Assert
        assertThat(actualDescription).isEqualTo(expectedDescription);
    }

    @Test
    void getPriorityFromAppointmentTest() {
        //Act
        Priority actualPriority = appRequest.getPriority();
        Priority expectedPriority = Priority.LOW;
        //Assert
        assertThat(actualPriority).isEqualTo(expectedPriority);
    }

    @Test
    void getTimePreferenceFromAppointmentTest() {
        //Act
        TimePreference actualPreference = appRequest.getTimePreference();
        TimePreference expectedPreference = TimePreference.UNSPECIFIED;
        //Assert
        assertThat(actualPreference).isEqualTo(expectedPreference);
    }

    @Test
    void getAppointmentDataFromAppointmentTest() {
        //Act
        AppointmentData actualApp = appRequest.getAppointmentData();
        AppointmentData expectedApp = appData;
        //Assert
        assertThat(actualApp).isEqualTo(expectedApp);
    }

    @Test
    void equalsAndHashCodeTest() {
        //Arrange
        APAppointmentRequest first = new APAppointmentRequest(appData, LocalTime.of(20, 0));
        APAppointmentRequest second = new APAppointmentRequest(appData, LocalTime.of(20, 0));
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
        APAppointmentRequest actual = appRequest;
        //Act
        String expectedString = "duration=PT45M, priority=LOW, description=Duration test}, localTime=10:00, timePreference=UNSPECIFIED";

        //assert
        assertThat(actual.toString()).contains(expectedString);
    }

}
