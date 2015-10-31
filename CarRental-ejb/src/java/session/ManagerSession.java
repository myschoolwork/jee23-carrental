package session;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarCompanyLoader;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
    
    
    @Override
    public void loadData(String name) {
        CarRentalCompany company = CarCompanyLoader.loadRental(name, name.toLowerCase()+".csv");
        if (company != null) {
            em.merge(company);
            // TO ASK: persist gives duplicate key error
            // probably because boths companies use the same cartypes, but they are
            // created separately. (So a cartype Compact is created for both hertz and dockx)
            // merge just updates the previous instance of this cartype in the databse
            // is this ok?
        }
    }
    
    
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        List result = em.createQuery("SELECT DISTINCT t FROM CarType t")
            // TODO select only from the wanted company.
            .getResultList();
        return new HashSet<CarType>(result);
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        /*try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.add(c.getId());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }*/
        // TODO
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        /*try {
            return RentalStore.getRental(company).getCar(id).getReservations().size();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }*/
        // TODO
        return 0;
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        /*Set<Reservation> out = new HashSet<Reservation>();
        try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.addAll(c.getReservations());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();*/
        // TODO
        return 314;
    }

    @Override
    public int getNumberOfReservationsBy(String renter) {
        /*Set<Reservation> out = new HashSet<Reservation>();
        for(CarRentalCompany crc : RentalStore.getRentals().values()) {
            out.addAll(crc.getReservationsBy(renter));
        }
        return out.size();*/
        // TODO
        return 12;
    }
}