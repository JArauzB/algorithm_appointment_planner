package appointmentplanner.api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Utility class to do zone time conversion.
 * Immutable and with hash code and equals so candidate for hash map key.
 *
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public class LocalDay {

    private final ZoneId zone;
    private final LocalDate date;

    /**
     * Create a local day in a time zone at a date
     *
     * @param zone to use, not null
     * @param date to use, not null
     */
    public LocalDay( ZoneId zone, LocalDate date ) {
        this.zone = zone;
        this.date = date;
    }

    public LocalDay() {
        this( ZoneId.systemDefault(), LocalDate.now() );
    }

    public LocalDate getDate() {
        return date;
    }

    public ZoneId getZone() {
        return zone;
    }

    /**
     * Get the the time as instant on the universal time line.
     *
     * @param localTime the local tome, not null
     * @return the instant for the local time
     */
    public Instant ofLocalTime( LocalTime localTime ) {
        return localTime.atDate( date ).atZone( zone ).toInstant();
    }

    /**
     * Get the LocalLime of an instant according to this day.
     *
     * @param instant not null
     * @return the LocalTime
     */
    public LocalTime timeOfInstant( Instant instant ) {
        return instant.atZone( zone ).toLocalTime();
    }

    /**
     * Get the date of an instant according to the time zone of thes local day.
     *
     * @param instant not null
     * @return the date
     */
    public LocalDate dateOfInstant( Instant instant ) {
        return instant.atZone( zone ).toLocalDate();
    }

    /**
     * Get a local day at distant days
     *
     * @param days to add
     * @return the new LocalDay shifted forward or backward in time.
     */
    public LocalDay plusDays( int days ) {
        return new LocalDay( zone, date.plusDays( days ) );
    }

    public Instant at( int hm, int m ) {
        return ofLocalTime( LocalTime.of( hm, m, 0 ) );
    }

    /**
     * Return the LocalDay for the default time zone at this date.
     *
     * @return the day
     */
    public static LocalDay now() {
        return new LocalDay();
    }

    @Override
    public String toString() {
        return "LocalDay{" + "zone=" + zone + ", date=" + date + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode( this.zone );
        hash = 73 * hash + Objects.hashCode( this.date );
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final LocalDay other = (LocalDay) obj;
        if ( !Objects.equals( this.zone, other.zone ) ) {
            return false;
        }
        return Objects.equals( this.date, other.date );
    }
}
