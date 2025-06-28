/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apappointmentplanner;

import appointmentplanner.api.LocalDay;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author jorge
 */
public class APTimeSlotTest {
    APTimeSlot timeSlot;

    @BeforeEach
    public void initialize() {
        //Arrange                                      Tue Sep 13 2022 13:14:40 GMT+0000
        timeSlot = new APTimeSlot(Instant.ofEpochSecond(1663074880), Instant.ofEpochSecond(1663074885));
    }

    @Test
    void getStartTest() {
        //Act
        Instant actual = timeSlot.getStart();
        Instant expected = Instant.ofEpochSecond(1663074880);
        //Assert
        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    void getEndTest() {
        //Act
        Instant actual = timeSlot.getEnd();
        Instant expected = Instant.ofEpochSecond(1663074885);
        //Assert
        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    void getEndTimetTest() {
        //Act
        LocalTime actualEndTime = timeSlot.getEndTime(new LocalDay());
        LocalTime expectedEndTime = LocalTime.ofInstant(Instant.ofEpochSecond(1663074885), new LocalDay().getZone());
        //Assert
        assertThat(actualEndTime).isEqualTo(expectedEndTime);
    }
        
    @Test
    void getEndDateTest() {
        //Act
        LocalDate actualEndDate = timeSlot.getEndDate(new LocalDay());
        LocalDate expectedEndDate = LocalDate.ofInstant(Instant.ofEpochSecond(1663074885), new LocalDay().getZone());
        //Assert
        assertThat(actualEndDate).isEqualTo(expectedEndDate);
    }
    
    @Test
    void equalsAndHashCodeTest() {
        //Arrange
        APTimeSlot first = new APTimeSlot(Instant.MAX, Instant.MIN);
        APTimeSlot second = new APTimeSlot(Instant.MAX, Instant.MIN);
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
        APTimeSlot actual = new APTimeSlot(Instant.MAX, Instant.MIN);
        //Act
        String expectedString = "APTimeSlot{start=+1000000000-12-31T23:59:59.999999999Z, end=-1000000000-01-01T00:00:00Z}";
        //assert
        assertThat(actual.toString()).contains(expectedString);
    }
    
}
