package session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
//import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {

    @PersistenceContext
    private EntityManager em;

    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        List<CarRentalCompany> companies = em.createQuery("Select a from CarRentalCompany a", CarRentalCompany.class)
                .getResultList();
        Set<String> companyNames = new HashSet<String>();
        for (CarRentalCompany crc : companies) {
            companyNames.add(crc.getName());
        }
        return companyNames;
    }

    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        List<CarType> availableCarTypes = new LinkedList<CarType>();
        List<CarRentalCompany> crcs = em.createQuery("Select a from CarRentalCompany a", CarRentalCompany.class)
                .getResultList();
        for (CarRentalCompany crc : crcs) {
            for (CarType ct : crc.getAvailableCarTypes(start, end)) {
                if (!availableCarTypes.contains(ct)) {
                    availableCarTypes.add(ct);
                }
            }
        }
        return availableCarTypes;
    }

    @Override
    public Quote createQuote(String name, ReservationConstraints constraints) throws ReservationException {
        
            List<CarRentalCompany> crcs = em.createNamedQuery("Company.findAll").getResultList();
            System.out.print("Companies: " + crcs.toString());
            for (CarRentalCompany crc : crcs) {
                System.out.print("Company: " + crc.getName());
               
                try {
                Quote out = crc.createQuote(constraints, name);
                quotes.add(out);
                System.out.print(out.getCarRenter() + "has created a quote");
                return out;
                } catch (Exception e) {
                    continue;
                }
                
                
            }

        
        throw new ReservationException("NO CARS AVAILABLE");
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();

        try {
            for (Quote quote : quotes) {
                CarRentalCompany crc = em.find(CarRentalCompany.class, quote.getRentalCompany());
                done.add(crc.confirmQuote(quote));
            }
        } catch (Exception e) {
            for (Reservation r : done) {
                ((CarRentalCompany) em.createNamedQuery("Company.findById").setParameter("id", r.getRentalCompany()).getSingleResult()).cancelReservation(r);
            }
            throw new ReservationException(e);
        }
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public CarType getCheapestCarType(Date start, Date end, String region) {
        List<CarRentalCompany> companies = em.createNamedQuery("Company.findAll").getResultList();     
        
        List<CarType> carTypes = new ArrayList<>();
        for (CarRentalCompany crc : companies) {
            if (crc.getRegions().contains(region)) {
                carTypes.addAll(crc.getAvailableCarTypes(start, end));
            }
        }

        CarType cheapestType = carTypes.get(0);
        for (CarType cartype : carTypes) {
            if (cartype.getRentalPricePerDay() < cheapestType.getRentalPricePerDay()) {
                System.out.println("CHEPEAEST: " + cartype.getName());
                cheapestType = cartype;
            }
        }
        return carTypes.get(0);

    }
}
