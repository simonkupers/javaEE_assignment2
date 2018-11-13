package rental;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "CAR")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Car.findAll", query = "SELECT c FROM Car c")
    , @NamedQuery(name = "Car.findByCompany", query = "SELECT c FROM Car c WHERE c.company = :company")
})
public class Car {

    @Column
    @Id
    private int id;
    @Column
    @ManyToOne(optional = false)
    private CarType type;
    @Column
    @OneToMany(fetch = FetchType.EAGER, targetEntity = Reservation.class)
    private Set<Reservation> reservations;
    @Column
    @ManyToOne(optional = false)
    private CarRentalCompany company;

    /**
     * *************
     * CONSTRUCTOR *
     **************
     */
    public Car(int uid, CarType type) {
        this.id = uid;
        this.type = type;
        this.reservations = new HashSet<Reservation>();
    }

    /**
     * ****
     * ID *
     *****
     */
    public int getId() {
        return id;
    }

    /**
     * **********
     * CAR TYPE *
     ***********
     */
    public CarType getType() {
        return type;
    }

    public void setType(CarType type) {
        this.type = type;
    }

    /**
     * **************
     * RESERVATIONS *
     ***************
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
