package rental;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "RESERVATION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reservation.findAll", query = "SELECT r FROM Reservation r")
    , @NamedQuery(name = "Reservation.findById", query = "SELECT r FROM Reservation r WHERE r.id = :id")
    , @NamedQuery(name = "Reservation.findByRenter", query = "SELECT r FROM Reservation r WHERE r.carRenter = :renter")
    , @NamedQuery(name = "Reservation.countRenter", query = "SELECT r.carRenter AS carRenter, COUNT(r) AS total FROM Reservation r GROUP BY r.carRenter ORDER BY total DESC")
})
public class Reservation extends Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private int id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Car car;

    /**
     * *************
     * CONSTRUCTOR *
     **************
     */
    public Reservation() {
    }

    public Reservation(Quote quote, Car car) {
        super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(),
                quote.getRentalCompany(), quote.getCarType(), quote.getRentalPrice());
        this.car = car;
    }

    /**
     * ****
     * ID *
     *****
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCarId() {
        return car.getId();
    }

    /**
     * ***********
     * TO STRING *
     ************
     */
    @Override
    public String toString() {
        return String.format("Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f",
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getCarId(), getRentalPrice());
    }
}
