package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Reservation;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
    
   public void addCarRentalCompany(String datafile);
   
   public void addCar(int uid, CarType type);
   
   public void addCarType(CarType type);
   
   public int getNumberOfReservations(String clientName);
   
   public CarType getMostPopularCarTypeIn(String companyName,int year);
   
   public Set<String> getBestClients();
   
}