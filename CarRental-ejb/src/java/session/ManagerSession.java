package session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.scene.input.KeyCode.T;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            return new HashSet<CarType>((Collection<CarType>) em.createNamedQuery("CarType.findByCompany")
                    .setParameter("company", company)
                    .getResultList());//RentalStore.getRental(company).getAllTypes());

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        try {
            CarRentalCompany crc = em.find(CarRentalCompany.class, company);
            for (Car c : crc.getCars(type)) {
                out.add(c.getId());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        try {
            CarRentalCompany crc = em.find(CarRentalCompany.class, company);
            return crc.getCar(id).getReservations().size();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
        try {
            CarRentalCompany crc = em.find(CarRentalCompany.class, company);
            for (Car c : crc.getCars(type)) {
                out.addAll(c.getReservations());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();
    }

    @Override
    public int getNumberOfReservations(String clientName) {
        return em.createNamedQuery("Reservation.findByRenter").setParameter("renter", clientName).getFirstResult();
    }

    @Override
    public CarType getMostPopularCarTypeIn(String companyName, int year) {
        CarRentalCompany crc = (CarRentalCompany) em.createNamedQuery("Company.findById").setParameter("id", companyName).getResultList().get(0);
        List<Car> cars = em.createNamedQuery("Car.findByCompany").setParameter("company", crc.getName()).getResultList();
        Map<CarType, Integer> maxMap = new HashMap<CarType, Integer>();
        for (Car car : cars) {
            for (Reservation reservation : car.getReservations()) {
                if (reservation.getStartDate().getYear() + 1900 == year) {
                    if (maxMap.containsKey(car.getType())) {
                        maxMap.put(car.getType(), maxMap.get(car.getType()) + 1);
                    } else {
                        maxMap.put(car.getType(), 0);
                    }
                }

            }
        }
        if (maxMap.isEmpty()) {
            return null;
        }

        Map.Entry<CarType, Integer> maxEntry = null;

        for (Map.Entry<CarType, Integer> entry : maxMap.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        return (CarType) maxEntry.getKey();

    }

    @Override
    public void addCarRentalCompany(String datafile) {
        try {
            CrcData data = loadData(datafile);
            CarRentalCompany company = new CarRentalCompany(data.name, data.regions, data.cars);

            System.out.println("COMPANY PERSISTS " + company.getName());
            em.persist(company);
//                        List<Car> cars = company.getAllCars();
//            for(Car car : cars){
//                car.setCompany(company);
//            }
            System.out.println("apres company persist");
            Logger.getLogger(ManagerSession.class.getName()).log(Level.INFO, "Loaded {0} from file {1}", new Object[]{data.name, datafile});
        } catch (NumberFormatException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, "bad file", ex);
        } catch (IOException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addCar(int uid, CarType type) {
        Car car = new Car(type);
        em.persist(car);
    }

    @Override
    public void addCarType(CarType type) {
        em.persist(type);
    }

    public static CrcData loadData(String datafile)
            throws NumberFormatException, IOException {

        CrcData out = new CrcData();
        StringTokenizer csvReader;

        //open file from jar
        BufferedReader in = new BufferedReader(new InputStreamReader(ManagerSession.class.getClassLoader().getResourceAsStream(datafile)));

        try {
            while (in.ready()) {
                String line = in.readLine();

                if (line.startsWith("#")) {
                    // comment -> skip					
                } else if (line.startsWith("-")) {
                    csvReader = new StringTokenizer(line.substring(1), ",");
                    out.name = csvReader.nextToken();
                    out.regions = Arrays.asList(csvReader.nextToken().split(":"));
                } else {
                    csvReader = new StringTokenizer(line, ",");
                    //create new car type from first 5 fields
                    CarType type = new CarType(csvReader.nextToken(),
                            Integer.parseInt(csvReader.nextToken()),
                            Float.parseFloat(csvReader.nextToken()),
                            Double.parseDouble(csvReader.nextToken()),
                            Boolean.parseBoolean(csvReader.nextToken()));
                    //create N new cars with given type, where N is the 5th field
                    for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                        out.cars.add(new Car(type));
                    }
                }
            }
        } finally {
            in.close();
        }

        return out;
    }

    static class CrcData {

        public List<Car> cars = new LinkedList<Car>();
        public String name;
        public List<String> regions = new LinkedList<String>();
    }

    public Set<String> getBestClients() {
        Set<String> clients = new HashSet<String>();
        Map<String, Integer> reservaties = new HashMap<String, Integer>();
        List<Reservation> reservations = em.createNamedQuery("Reservation.findAll").getResultList();

        for (Reservation reservation : reservations) {

            if (!reservaties.containsKey(reservation.getCarRenter())) {
                reservaties.put(reservation.getCarRenter(), 1);
            } else {
                reservaties.put(reservation.getCarRenter(), reservaties.get(reservation.getCarRenter()) + 1);
            }
        }

        int maxValueInMap = (Collections.max(reservaties.values()));  // This will return max value in the Hashmap
        for (Entry<String, Integer> entry : reservaties.entrySet()) {  // Iterate through hashmap
            if (entry.getValue() == maxValueInMap) {
                clients.add(entry.getKey());
            }
        }
        return clients;
    }

}
