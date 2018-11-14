package rental;

import java.io.Serializable;
import static java.lang.reflect.Array.set;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "TYPES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CarType.findAll", query = "SELECT t FROM CarType t")
    , @NamedQuery(name = "CarType.findByCompany", query = "SELECT c FROM CarType c WHERE c.carRentalCompanys = :company")

})
public class CarType implements Serializable{
    
    @Column
    @Id
    private String name;
    
    @Column
    private int nbOfSeats;
    
    @Column
    private boolean smokingAllowed;
    
    @Column
    private double rentalPricePerDay;
    //trunk space in liters
    @Column
    private float trunkSpace;
    
    @ManyToMany(mappedBy = "carTypes")
    private List<CarRentalCompany> carRentalCompanys;
    
    /***************
     * CONSTRUCTOR *
     ***************/
    
    public CarType() {
    }

    public CarType(String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        this.name = name;
        this.nbOfSeats = nbOfSeats;
        this.trunkSpace = trunkSpace;
        this.rentalPricePerDay = rentalPricePerDay;
        this.smokingAllowed = smokingAllowed;
    }

    public String getName() {
    	return name;
    }
    
    public int getNbOfSeats() {
        return nbOfSeats;
    }
    
    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }
    
    public float getTrunkSpace() {
    	return trunkSpace;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
    	return String.format("Car type: %s \t[seats: %d, price: %.2f, smoking: %b, trunk: %.0fl]" , 
                getName(), getNbOfSeats(), getRentalPricePerDay(), isSmokingAllowed(), getTrunkSpace());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
	if (obj == null)
            return false;
	if (getClass() != obj.getClass())
            return false;
	CarType other = (CarType) obj;
	if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
	return true;
    }
}