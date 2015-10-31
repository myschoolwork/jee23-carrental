package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession extends Session implements CarRentalSessionRemote {
    
    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        List result = em.createQuery("SELECT c.name FROM CarRentalCompany c")
            .getResultList();
        return new HashSet<String>(result);
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        List result = em.createQuery(
                  "SELECT DISTINCT c.type "
                + "FROM Car c "
                + "WHERE NOT EXISTS ("
                    + "SELECT r FROM IN(c.reservations) r WHERE r.startDate BETWEEN :start AND :end OR r.endDate BETWEEN :start AND :end"
                + ")")
                .setParameter("start", start, TemporalType.DATE)
                .setParameter("end", end, TemporalType.DATE)
                .getResultList();
        return new LinkedList<CarType>(result);
    }
    
    @Override
    public CarType getCheapestCarType(Date start, Date end) {
        List result = em.createQuery(
                  "SELECT DISTINCT c.type "
                + "FROM Car c "
                + "WHERE NOT EXISTS ("
                    + "SELECT r FROM IN(c.reservations) r WHERE r.startDate BETWEEN :start AND :end OR r.endDate BETWEEN :start AND :end"
                + ")"
                + "ORDER BY c.type.rentalPricePerDay ASC")
                .setParameter("start", start, TemporalType.DATE)
                .setParameter("end", end, TemporalType.DATE)
                .getResultList();
        
        return (CarType)result.get(0);
    }    

    @Override
    public Quote createQuote(String companyName, ReservationConstraints constraints) throws ReservationException {
        try {
            Quote out = getCompany(companyName).createQuote(constraints, renter);
            quotes.add(out);
            return out;
        } catch(Exception e) {
            throw new ReservationException(e);
        }
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
                done.add(getCompany(quote.getRentalCompany()).confirmQuote(quote));
            }
        } catch (Exception e) {
            for(Reservation r:done)
                getCompany(r.getRentalCompany()).cancelReservation(r);
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
}