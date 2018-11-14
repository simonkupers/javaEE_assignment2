package client;

import static java.lang.System.out;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;
import java.util.logging.Logger;

public class Main extends AbstractTestManagement<CarRentalSessionRemote, ManagerSessionRemote> {

    @EJB
    private static CarRentalSessionRemote carRentalSession;

    @EJB
    private static ManagerSessionRemote managerSession; 
    
    
    
    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        Main client = new Main("trips");
        ManagerSessionRemote managerSession = client.getNewManagerSession("", "");
        System.out.println("pre herts");
        managerSession.addCarRentalCompany("hertz.csv");
        System.out.println("pre dockx");
        managerSession.addCarRentalCompany("dockx.csv");
        System.out.println("apres dockx");
        client.run();
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        return ms.getBestClients();
    }

    @Override
    protected String getCheapestCarType(CarRentalSessionRemote session, Date start, Date end, String region) throws Exception {
        return session.getCheapestCarType(start, end, region).getName();
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
    }

    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        return carRentalSession;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        return managerSession;
    }

    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        
        List<CarType> carTypes = session.getAvailableCarTypes(start, end);
        System.out.println("Available cartypes are: " + carTypes.toString());
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
	ReservationConstraints constraints = new ReservationConstraints(start,end, carType,region);
	session.createQuote(name, constraints);
    }

    @Override
    protected List<Reservation> confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        return session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getNumberOfReservations(clientName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservations(carRentalName, carType);
    }
    
}
