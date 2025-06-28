package appointmentplanner;

import appointmentplanner.api.AbstractAPFactory;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Simplest service finder returns null when nothing found.
 *
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public class ServiceFinder {

    public static AbstractAPFactory getFactory() {
        Optional<AbstractAPFactory> fac = ServiceLoader.load( AbstractAPFactory.class )
                .findFirst();
        if ( fac.isPresent() ) {
            return fac.get();
        }

        System.err.println( "Could not load AbstractAPPFactory" );
        return null;
    }
}
