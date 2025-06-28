/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apappointmentplanner;

import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.LocalDay;
import appointmentplanner.api.Priority;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author jorge
 */
public class APAppointmentTest {

    APAppointment appointment;

    @BeforeEach
    public void initialize() {
        //Arrange
        APAppointmentData appData = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        APAppointmentRequest appRequest = new APAppointmentRequest(appData, LocalTime.of(10, 0));
        appointment = new APAppointment(LocalDay.now(), appData, appRequest);
    }

    @Test
    void getPriorityTest() {
        //Act
        Priority actualPriority = appointment.getPriority();
        Priority expectedPriority = Priority.LOW;
        //Assert
        assertThat(actualPriority).isEqualTo(expectedPriority);
    }

    @Test
    void getAppDataTest() {
        //Act
        AppointmentData actualAppData = appointment.getAppointmentData();
        AppointmentData expectedAppData = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        //Assert
        assertThat(actualAppData).isEqualTo(expectedAppData);
    }

    @Test
    void getAppRequestTest() {
        //Act
        AppointmentData actualAppRequest = appointment.getRequest();
        AppointmentData appData = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        APAppointmentRequest expectedAppRequest = new APAppointmentRequest(appData, LocalTime.of(10, 0));
        //Assert
        assertThat(actualAppRequest).isEqualTo(expectedAppRequest);
    }

    @Test
    void getGettersAppRequestTest() {
        //Act
        AppointmentData appData = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        APAppointmentRequest expected = new APAppointmentRequest(appData, LocalTime.of(10, 0));
        //Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(appointment.getStart()).isEqualTo(expected.getStart(new LocalDay()));
            softly.assertThat(appointment.getEnd()).isEqualTo(expected.getStart(new LocalDay()).plusSeconds(appData.getDuration().toSeconds()));
        });
    }

    @Test
    void getDurationTest() {
        //Act
        Duration actualDuration = appointment.getDuration();
        Duration expectedDuration = Duration.ofMinutes(45);
        //Assert
        assertThat(actualDuration).isEqualTo(expectedDuration);
    }

    @Test
    void getDescriptionTest() {
        //Act
        String actual = appointment.getDescription();
        String expected = "Duration test";
        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void equalsAndHashCodeTest() {
        //Arrange
        APAppointment first = appointment;
        APAppointment second = appointment;
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
        APAppointment actual = appointment;
        //Act
        String expectedString = "Appointment{ appRequest=APAppointmentRequest{appointmentData=APAppointmentData{duration=PT45M, priority=LOW, description=Duration test}, localTime=10:00, timePreference=UNSPECIFIED}}";
        //assert
        assertThat(actual.toString()).contains(expectedString);
    }

}

