package session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.TemporalType;
import rental.CarCompanyLoader;
import rental.CarRentalCompany;
import rental.CarType;

@Stateless
public class ManagerSession extends Session implements ManagerSessionRemote {
    
    @Override
    public void loadData(String name) {
        CarRentalCompany company = CarCompanyLoader.loadRental(name, name.toLowerCase()+".csv");
        if (company != null) {
            em.persist(company);
            // TO ASK: persist gives duplicate key error
            // probably because boths companies use the same cartypes, but they are
            // created separately. (So a cartype Compact is created for both hertz and dockx)
            // merge just updates the previous instance of this cartype in the databse
            // is this ok?
        }
    }
    
    
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        List result = em.createQuery("SELECT t FROM CarRentalCompany c, IN(c.carTypes) t WHERE c.name LIKE :company")
                .setParameter("company", company)
                .getResultList();
        return new HashSet<CarType>(result);
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        List result = em.createQuery("SELECT cr.id FROM CarRentalCompany c, IN(c.cars) cr WHERE c.name LIKE :company AND cr.type.name LIKE :type")
                .setParameter("company", company)
                .setParameter("type", type)
                .getResultList();
        return new HashSet<Integer>(result);
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        Object result = em.createQuery("SELECT COUNT(r) FROM Car c, IN(c.reservations) r WHERE c.id = :id")
                .setParameter("id", id)
                .getSingleResult();
        return ((Long)result).intValue();
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Object result = em.createQuery("SELECT COUNT(r) FROM CarRentalCompany c, IN(c.cars) cr, IN(cr.reservations) r WHERE c.name LIKE :company AND cr.type.name LIKE :type")
                .setParameter("company", company)
                .setParameter("type", type)
                .getSingleResult();
        return ((Long)result).intValue();
    }

    @Override
    public int getNumberOfReservationsBy(String renter) {
        Object result = em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.carRenter LIKE :renter")
                .setParameter("renter", renter)
                .getSingleResult();
        return ((Long)result).intValue();
    }

    @Override
    public String getMostPopularCarRentalCompany() {
        List result = em.createQuery(
                  "SELECT c.name "
                + "FROM CarRentalCompany c, IN(c.cars) cr, IN(cr.reservations) r "
                + "GROUP BY c.name "
                + "ORDER BY COUNT(r) DESC")
                .getResultList();
        return (String)result.get(0);
    }
}