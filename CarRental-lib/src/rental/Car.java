package rental;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "CAR")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Car.findAll", query = "SELECT c FROM Car c")
    , @NamedQuery(name = "Car.findByCompany", query = "SELECT c FROM Car c WHERE c.company.name = :company")
})
public class Car implements Serializable{

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private int id;

    
    @ManyToOne
    private CarType type;
    
    @OneToMany(mappedBy = "car", cascade = CascadeType.PERSIST)
    private Set<Reservation> reservations;

    
    @ManyToOne
    private CarRentalCompany company;

    /**
     * *************
     * CONSTRUCTOR * *************
     */
    
    public Car() {
    }

    public Car(CarType type) {
        this.type = type;
        this.reservations = new HashSet<Reservation>();
    }

    /**
     * ****
     * ID * ****
     */
    public int getId() {
        return id;
    }

    /**
     * **********
     * CAR TYPE * **********
     */
    public CarType getType() {
        return type;
    }

    public void setType(CarType type) {
        this.type = type;
    }

    /**
     * **************
     * RESERVATIONS * **************
     */
    public CarRentalCompany getCompany() {
        return company;
    }

    public void setCompany(CarRentalCompany company) {
        this.company = company;
    }

    public boolean isAvailable(Date start, Date end) {
        if (!start.before(end)) {
            throw new IllegalArgumentException("Illegal given period");
        }

        for (Reservation reservation : reservations) {
            if (reservation.getEndDate().before(start) || reservation.getStartDate().after(end)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public void addReservation(Reservation res) {
        reservations.add(res);
    }

    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }
}
