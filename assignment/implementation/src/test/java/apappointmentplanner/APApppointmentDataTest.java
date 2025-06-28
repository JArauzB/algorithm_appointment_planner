/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apappointmentplanner;

import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.Priority;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author jorge
 */
public class APApppointmentDataTest {
    
    @Test
    void getDurationFromAppointmentTest(){
        //Arrange
        APAppointmentData app = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        //Act
        Duration actualDuration = app.getDuration();
        Duration expectedDuration = Duration.ofMinutes(45);
        //Assert
        assertThat(actualDuration.getSeconds()).isEqualTo(expectedDuration.getSeconds());
    }
    
    @Test
    void getDescriptionFromAppointmentTest(){
        //Arrange
        APAppointmentData app = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        //Act
        String actualDescription = app.getDescription();
        String expectedDescription = "Duration test";
        //Assert
        assertThat(actualDescription).isEqualTo(expectedDescription);
    }
    
    @Test
    void getPriorityFromAppointmentTest(){
        //Arrange
        APAppointmentData app = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        //Act
        Priority actualPriority = app.getPriority();
        Priority expectedPriority = Priority.LOW;
        //Assert
        assertThat(actualPriority).isEqualTo(expectedPriority);
    }
    
    @Test
    void equalsAndHashCodeTest() {
        //Arrange
        AppointmentData first = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        AppointmentData second = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
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
        AppointmentData actual = new APAppointmentData("Duration test", Duration.ofMinutes(45), Priority.LOW);
        //Act
        String expectedString = "duration=PT45M, priority=LOW, description=Duration test";
        //assert
        assertThat(actual.toString()).contains(expectedString);
    }
}
