module appointmentplanner {
    requires appointmenplanner.api;
    provides appointmentplanner.api.AbstractAPFactory with appointmentplanner.APFactory;
    uses appointmentplanner.api.AbstractAPFactory; // in tests
}
